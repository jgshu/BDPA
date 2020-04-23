package Util; /**
 * FileName: Util.Util
 * Author:   star
 * Date:     2019/10/24 17:27
 * Description: 一些公共的处理参数或者返回结果的方法
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

import BCPA.roles.BCPA_PKG;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈一些公共的处理参数或者返回结果的方法〉
 *
 * @author star
 * @create 2019/10/24
 * @since 1.0.0
 */
public class Util {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static Pairing pairing;

    //从PKG设置pairing方便后续使用
    public static void setPairing(Pairing pairing) {
        Util.pairing = pairing;
    }

    //16进制的byte[]数组转换为字符串
    public static String hexBytesToString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    //16进制的字符串转换为byte[]数组
    public static byte[] hexStringToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    //G1中获取随机元素，获取1，获取0
    public static Element getRandomFromG1() {
        return pairing.getG1().newRandomElement().getImmutable();
    }

    public static Element getOneFromG1() {
        return pairing.getG1().newOneElement().getImmutable();
    }

    public static Element getZeroFromG1() {
        return pairing.getG1().newZeroElement().getImmutable();
    }

    //Zp中获取随机元素，获取1，获取0
    public static Element getRandomFromZp() {
        return pairing.getZr().newRandomElement().getImmutable();
    }

    public static Element getOneFromZp() {
        return pairing.getZr().newOneElement().getImmutable();
    }

    public static Element getZeroFromZp() {
        return pairing.getZr().newZeroElement().getImmutable();
    }

    //H1,H2 : {0, 1}∗ → G1
    public static Element hashFromStringToG1(String str) {
        return pairing.getG1().newElement().setFromHash(str.getBytes(), 0, str.length()).getImmutable();
    }

    public static Element hashFromBytesToG1(byte[] bytes) {
        return pairing.getG1().newElement().setFromHash(bytes, 0, bytes.length).getImmutable();
    }

    //H : {0, 1}∗ → Zp
    public static Element hashFromStringToZp(String str) {
        return pairing.getZr().newElement().setFromHash(str.getBytes(), 0, str.length()).getImmutable();
    }

    public static Element hashFromBytesToZp( byte[] bytes) {
        return pairing.getZr().newElement().setFromHash(bytes, 0, bytes.length).getImmutable();
    }

    //h : G1 → Zp
    public static Element hashFromG1ToZp( Element g1_element) {
        // h(y) : G1 -> Zp
        byte[] g1_bytes = g1_element.getImmutable().toCanonicalRepresentation();
        byte[] zp_bytes = g1_bytes;
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-512");
            zp_bytes = hasher.digest(g1_bytes);   //先把G1元素hash成512bits
        } catch (Exception e) {
            e.printStackTrace();
        }
        //再把hash后的bits映射到Zp
        Element hash_result = pairing.getZr().newElementFromHash(zp_bytes, 0, zp_bytes.length).getImmutable();
        return hash_result;
    }

    //{0,1}* -> key space of πkey  int空间
    public static int h1_pai_key(String data) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            byte[] result = hasher.digest(data.getBytes());
            ByteBuffer wrapped = ByteBuffer.wrap(result);
            return wrapped.getShort();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //伪随机置换 pseudorandom permutation πkey() 用于随机选择哪些块进行抽查
    public static List<Integer> pseudoPerm(int key, int n, int c) {
        List<Integer> result = new ArrayList<Integer>(c);
        if(c < n) {
            List<Integer> list = new ArrayList<>(n);
            for(int i = 0; i < n; i ++) {
                list.add(i);
            }
            for(int i = 0; i < key; i ++)
                java.util.Collections.shuffle(list);
            for(int i = 0; i < c; i ++) {
                result.add(list.get(i));
            }
        } else {
            System.out.println(" pseudorandom permutation error!");
        }
        return result;
    }

    //{0,1}* -> key space of fkey 字符串空间
    public static String h2_f_key(String data) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-512");
            byte[] result = hasher.digest(data.getBytes());
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }


    //伪随机函数 pseudorandom function fkey():{0,1}* -> Zp
    public static Element pseudoFunc(String key, int id) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-512");
            byte[] hash_bytes = hasher.digest((key + id).getBytes());   //先把G1元素hash成512bits
            return pairing.getZr().newElementFromHash(hash_bytes, 0, hash_bytes.length).getImmutable();
        } catch (Exception e) {
            e.printStackTrace();
            return pairing.getZr().newRandomElement();
        }
    }

    public static void main(String[] args) throws Exception {
        BCPA_PKG pkg = new BCPA_PKG();
        System.out.println(Util.hashFromStringToZp(123+"name"));
        System.out.println(Util.hashFromStringToZp(123+"name"));
        System.out.println(Util.hashFromStringToZp(111+"name"));
    }
}
