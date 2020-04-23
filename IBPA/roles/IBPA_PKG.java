/**
 * FileName: BCPA_PKG
 * Author:   star
 * Date:     2019/10/3 9:58
 * Description: 模拟PKG，负责生产系统参数和用户私钥
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IBPA.roles;

import Util.Util;
import IBPA.keys.MasterPrivateKey;
import IBPA.keys.MasterPublicKey;
import IBPA.keys.UserPrivateKey;
import IBPA.keys.UserPublicKey;
import IBPA.wrappers.Challenge;
import IBPA.wrappers.IBPA_Proof;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 〈一句话功能简述〉<br> 
 * 〈模拟PKG，负责生产系统参数和用户私钥〉
 *  The BCPA_PKG generates the system papameters and a master secret key.
 * @author star
 * @create 2019/10/3
 * @since 1.0.0
 */
public class IBPA_PKG {
    private  Pairing pairing;                       //双线性映射 e: G1*G1 -> G2, G1和G2阶数均为p，P是G1的生成元
    private MasterPublicKey masterPublicKey;        //master public key pk = g^sk
    private MasterPrivateKey masterPrivateKey;      //master secret key sk
    private UserPublicKey userPublicKey;            //(Pu0^s, Pu1^s)
    private String w = "test";                      //PKG返回的状态参数
    private Challenge challenge;                    //生成的挑战信息，一组（j,vj）
    //Define Hash functions H1,H2 : {0, 1}∗ → G1, h : G1 → Zp, and H : {0, 1}∗ → Zp. 具体实现见Util类
    //The system parameters are Para = {G1,G2, e,H1,H2, h,H}.

    public Pairing getPairing() {
        return this.pairing;
    }
    public UserPublicKey getUserPublicKey() {
        return this.userPublicKey;
    }
    public MasterPublicKey getMasterPublicKey(){
        return this.masterPublicKey;
    }
    public String getW() {
        return w;
    }


    // ========================== 2. PKG初始化系统参数 =============================
    public IBPA_PKG(){
        /**
         * 给定安全参数k=80，PKG选取两个group G1 G2和双线性映射e
         * P是G1的生成元，随机选择s属于Z_p作为私钥，计算公钥Q = P^s
         */
        //指定椭圆曲线的种类、产生椭圆曲线参数、初始化Pairing
        //动态产生 TypeA 对称质数阶双线性群：rBit是Zp中阶数p的比特长度；qBit是G中阶数的比特长度
        TypeACurveGenerator pg = new TypeACurveGenerator(160,512);
        PairingParameters typeAParams = pg.generate();
        pairing = PairingFactory.getPairing(typeAParams);
        Util.setPairing(pairing);   //设置工具类的pairing，方便后续使用

        //随机产生一个Z_p群的元素 sk 当做私钥 masterPrivateKey
        Element sk = Util.getRandomFromZp();
        //用类封装一下私钥
        this.masterPrivateKey = new MasterPrivateKey(sk);

        //随机产生一个G_1群的元素
        // 设定并存储一个生成元。由于椭圆曲线是加法群，所以G群中任意一个元素都可以作为生成元
        Element g = Util.getRandomFromG1();
        //G群的Z次方运算，计算公钥：pk = g^sk
        Element pk = g.powZn(sk).getImmutable();

        //用类封装pk和g，因为很容易变化
        this.masterPublicKey = new MasterPublicKey(pk, g);
        //System.out.println("setup BCPA_PKG done! ====> secret key is sk, public key is pk = g^sk");
    }

    // ========================== 3. 用户获取自己的私钥 =============================
    public UserPrivateKey getSecretKey(String ID) {
        int id_len = ID.getBytes().length;
        byte[] byte_identity = new byte[id_len + 1];
        //把identity的数据拷贝到SecretKey中对应的字符串数组
        System.arraycopy( ID.getBytes(), 0, byte_identity, 0, id_len);

        //生成Pu0 = H1(ID, 0)
        byte_identity[id_len] = 0;
        Element Pu0 = Util.hashFromBytesToG1(byte_identity);
        Element sPu0 = Pu0.powZn(this.masterPrivateKey.getSk()).getImmutable();

        //生成Pu1 = H1(ID, 1)
        byte_identity[id_len] = 1;
        Element Pu1 = Util.hashFromBytesToG1(byte_identity);
        Element sPu1 = Pu1.powZn(this.masterPrivateKey.getSk()).getImmutable();

        //保存用户公钥
        this.userPublicKey = new UserPublicKey(Pu0, Pu1);
        //返回用户私钥
        return new UserPrivateKey(sPu0, sPu1);
    }

    // ========================== 5. 生成challenge message =============================
    //文件总块数n，抽取的块数为L
    public void genChall(int n, int L){
        long begin = System.currentTimeMillis();
        if (n <= 0) {
            System.out.println("generate challenge failed! File blocks count is " + n );
            System.exit(-1);
        }
        challenge = new Challenge(n);
        Random random = new Random();           //java自带的随机数
        List<Integer> list = new ArrayList<Integer>(n);
        for(int i = 0; i < n; i ++) {
            list.add(i);
        }

        int index;
        for (int i = 0; i < L; i++) {
            index = random.nextInt(list.size());        //从1至 n 中随机选择 L 个不重复的随机数作为 j
            challenge.addToListJ(list.get(index));
            list.remove(index);

            challenge.addToListVj(Util.getRandomFromZp());      //从 Zp 中随机选择 L 个元素作为vj
        }
        long end = System.currentTimeMillis();
        System.out.println("gen challenge" + (end - begin));
    }

    public void sendChallToCS(IBPA_CloudServer cloudServer) {
        cloudServer.storeChallenge(this.challenge);
    }

    // ========================== 7. 验证Proof =============================
    //r是用户选择的随机数 ∈ Zp，name是文件名
    public boolean verifyProof(IBPA_Proof proof, Element r, String name) {
        Element g = masterPublicKey.getG();
        Element left = pairing.pairing(proof.getS(), g);
        //System.out.println("左边的双线性对：e(S,P) \n= " + left);   // e(S, g)

        //G1 中 加法用乘法，乘法用POW，减法用除法
        Element mul_H2wj_vj = Util.getOneFromG1();       //连乘积 of H2(w||j)^vj
        Element sum_Hnamej_vj = Util.getZeroFromZp();    //sum of H(name||j)*vj
        Element H2wj;        //H2(w||j) ∈ G1
        Element Hnamej;      //H(name||j) ∈ Zp
        int j;
        Element vj;     //vj ∈ Zp

        for (int i = 0; i < challenge.getChallengeLen(); i ++) {
            j = challenge.getJ(i);
            vj = challenge.getVj(i);

            //H2(w||j) ∈ G1
            H2wj = Util.hashFromStringToG1(w + j);
            mul_H2wj_vj = mul_H2wj_vj.mul(H2wj.powZn(vj).getImmutable());            //连乘积 of H2(w||j)^vj

            //H(name||j) ∈ Zp
            Hnamej = Util.hashFromStringToZp(name + j);
            sum_Hnamej_vj = sum_Hnamej_vj.add(Hnamej.mulZn(vj).getImmutable());      //sum of H(name||j)*vj
        }

        //右边第一个双线性对：e(sum_1, g^r)
        Element g_r = g.powZn(r).getImmutable();
        Element right_1 = pairing.pairing(mul_H2wj_vj, g_r).getImmutable();
        //System.out.println("右边第1个双线性对：e(π(H2(w||j)^vj), g^r) \n= " + right_1);

        //Pu0 ^ (sum of H(name||j)*vj) ∈ G1
        Element sum_2 = userPublicKey.getPu0().powZn(sum_Hnamej_vj).getImmutable();

        //y^miu ∈ G1
        Element miu_y = proof.getY().powZn(proof.getMIU()).getImmutable();
        sum_2 = sum_2.mul(miu_y).getImmutable();

        // h(y) : G1 -> Zp
        Element hash_y = Util.hashFromG1ToZp(proof.getY());

        //Pu1 ^ h(y) ∈ G1
        Element hash_y_Pu1 = userPublicKey.getPu1().powZn(hash_y).getImmutable(); //Pu1^h(y)

        //计算完成： π(Pu0^(sum of H(name||j)*vj)) * (y^miu) / (Pu1^h(y))
        sum_2 = sum_2.div(hash_y_Pu1).getImmutable();   //G1 的减法用除法代替

        //右边第二个双线性对 e(sum_2, g^sk)
        Element right_2 = pairing.pairing(sum_2, masterPublicKey.getPk()).getImmutable();
        //System.out.println("右边第2个双线性对：e(π(Pu0^(sum of H(name||j)*vj)) * (y^miu) / (Pu1^h(y)), g^s) \n= " + right_2);

        //计算右边双线性对相乘的结果，其实和相加一样，这里保险还是用乘法
        Element right = right_1.mul(right_2).getImmutable();
        //System.out.println("右边2个双线性对相乘 \n= " + right);

        //System.out.println("verify BCPA_Proof done! ");
        return left.isEqual(right);
    }
}
