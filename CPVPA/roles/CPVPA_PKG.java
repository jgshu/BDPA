package CPVPA.roles; /**
 * FileName: BCPA_PKG
 * Author:   star
 * Date:     2019/10/3 9:58
 * Description: 模拟PKG，负责生产系统参数和用户私钥
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

import CPVPA.keys.MasterPrivateKey;
import CPVPA.keys.MasterPublicKey;
import CPVPA.keys.UserPrivateKey;
import CPVPA.keys.UserPublicKey;
import CPVPA.wrappers.Challenge;
import CPVPA.wrappers.FileData;
import CPVPA.wrappers.CPVPA_Proof;
import Util.Util;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * 〈一句话功能简述〉<br> 
 * 〈模拟PKG，负责生产系统参数和用户私钥〉
 *  The BCPA_PKG generates the system papameters and a master secret key.
 * @author star
 * @create 2019/10/3
 * @since 1.0.0
 */
public class CPVPA_PKG {
    private  Pairing pairing;                       //双线性映射 e: G*G -> GT, G和GT阶数均为p的乘法群，g是G1的生成元
    private MasterPublicKey masterPublicKey;        //master public key pk = g^sk
    private MasterPrivateKey masterPrivateKey;      //master secret key sk
    private UserPublicKey userPublicKey;            // spku = {pku, IDu }  pku = g^xu
                                                    // ssku = {xu, Pu0^s, Pu1^s}  Pu0 = H1(IDu,0) Pu1 = H1(IDu,1)

    private Challenge challenge;                    //生成的挑战信息，一组（j,vj）
    //H -> Zp, H1, H2, H3, H4 -> G
    //伪随机置换 πkey() 伪随机函数fkey()
    //The system parameters are Para = {e, G, GT, g, p, H, H1, H2, H3, H4, H5, πkey, fkey}.

    public Pairing getPairing() {
        return this.pairing;
    }
    public UserPublicKey getUserPublicKey() {
        return this.userPublicKey;
    }
    public MasterPublicKey getMasterPublicKey(){
        return this.masterPublicKey;
    }


    // ========================== 1. Setup - PKG初始化系统参数和用户公私钥 =============================
    public CPVPA_PKG(){
        /**
         * 给定安全参数k=80，PKG选取两个乘法群 G GT和双线性映射e
         * P是G的生成元，随机选择s属于Z_p作为私钥，计算公钥Q = P^s
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

    // ========================== 2. 用户获取自己的私钥 =============================
    public UserPrivateKey getSecretKey(Element xu, String ID) {
        int id_len = ID.getBytes().length;
        byte[] byte_identity = new byte[id_len + 1];
        //把identity的数据拷贝到SecretKey中对应的字符串数组
        System.arraycopy( ID.getBytes(), 0, byte_identity, 0, id_len);

        //生成Pu0 = H1(ID, 0)
        byte_identity[id_len] = 0;
        Element Qu0 = Util.hashFromBytesToG1(byte_identity);                    //Qu0 = H1(IDu,0)
        Element Du0 = Qu0.powZn(this.masterPrivateKey.getSk()).getImmutable();  //Du0 = Qu0^sk

        //生成Pu1 = H1(ID, 1)
        byte_identity[id_len] = 1;
        Element Qu1 = Util.hashFromBytesToG1(byte_identity);                    //Qu1 = H1(IDu,1)
        Element Du1 = Qu1.powZn(this.masterPrivateKey.getSk()).getImmutable();  //Du1 = Qu1^sk

        Element pku = masterPublicKey.getG().powZn(xu).getImmutable();          //pku = g^xu
        //保存用户公钥 spku = {pku, IDu}, Qu0和Qu1是由IDu可以计算出来
        this.userPublicKey = new UserPublicKey(pku, ID, Qu0, Qu1);
        //返回用户私钥  xu由用户自己填 ssku = {xu, Du0, Du1}
        return new UserPrivateKey(xu, Du0, Du1);
    }

    // ========================== 6. 生成challenge message =============================
    //文件总块数n，抽取的块数为c，c由安全参数k决定
    public void genChall(int n, int c){
        if (n <= 0) {
            System.out.println("generate challenge failed! File blocks count is " + n );
            System.exit(-1);
        }

        //challenge msg: {多个区块hash, timestamp}
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        BigInteger time_seconds = BigInteger.valueOf(timestamp.getSeconds());
        //TODO 访问区块链网络，获取hash值
        String concatBlockHash = "Blocks";
        challenge = new Challenge(concatBlockHash, time_seconds);
        //System.out.println("generate challenge done!");
    }

    public void sendChallToCS(CPVPA_CloudServer cloudServer) {
        cloudServer.storeChallenge(this.challenge);
    }

    // ========================== 8. 验证Proof =============================
    //r是用户选择的随机数 ∈ Zp，name是文件名
    public boolean verifyProof(CPVPA_Proof proof, FileData fileData) {
        Element g = masterPublicKey.getG();
        Element left = pairing.pairing(proof.getS(), g);

        Element name = fileData.getName();
        int n = fileData.getDataLen();
        int c = fileData.getC();
        Element delta = proof.getDelta();
        Element miu = proof.getMIU();
        Element R = proof.getR();
        Element Qu0 = this.userPublicKey.getQu0();
        Element Qu1 = this.userPublicKey.getQu1();
        Element pk = this.masterPublicKey.getPk();
        Element pku = this.userPublicKey.getPku();

        Element tao = Util.hashFromStringToZp("" + name + n + delta + pku + this.userPublicKey.getIDu());  //τ = H(name || n || △ || spku)

        Element H_itaoR;        //H(j||τ||R)
        Element vj_H_itaoR;     //vj * H_itaoR
        Element mul_1_result = Util.getOneFromG1();    //Qu1^vj_H_itaoR 连乘积

        Element Tj;             //Tj = H2(j||τ||R) ∈ G1
        Element Tj_vj;          //Tj^vj
        Element mul_2_result = Util.getOneFromG1();   //Tj^vj 连乘积

        Element V = Util.hashFromBytesToG1(delta.toCanonicalRepresentation()).getImmutable();   //V = H3(△)
        Element W = Util.hashFromStringToG1("" + delta).getImmutable();                         //W = H4(△)

        Element W_vj_H_itaoR;                           //W^vj_H_itaoR
        Element mul_3_result = Util.getOneFromG1();     //W^vj_H_itaoR 连乘积

        int j;
        Element vj;     //vj ∈ Zp

        for (int i = 0; i < c; i ++) {
            j = challenge.getJ(i);
            vj = challenge.getVj(i);

            H_itaoR = Util.hashFromStringToZp(""+ j + tao + R);         //H(j||τ||R)
            vj_H_itaoR = vj.mul(H_itaoR).getImmutable();                //vj * H_itaoR
            mul_1_result = mul_1_result.mul(Qu1.powZn(vj_H_itaoR).getImmutable());  //Qu1^vj_H_itaoR 连乘积

            Tj = Util.hashFromStringToG1("" + j + tao + R);             //H2(j||τ||R)
            Tj_vj = Tj.powZn(vj).getImmutable();
            mul_2_result = mul_2_result.mul(Tj_vj).getImmutable();      //Tj^vj 连乘积

            W_vj_H_itaoR = W.powZn(vj_H_itaoR).getImmutable();
            mul_3_result = mul_3_result.mul(W_vj_H_itaoR).getImmutable();           //W^vj_H_itaoR 连乘积
        }

        //右边第1个双线性对
        Element Qu0_miu = Qu0.powZn(miu).getImmutable();                //Qu0^μ
        Element right_1 = pairing.pairing(Qu0_miu.mul(mul_1_result).getImmutable(), pk).getImmutable();

        //右边第2个双线性对
        Element right_2 = pairing.pairing(mul_2_result, R).getImmutable();

        //右边第3个双线性对
        Element V_miu = V.powZn(miu).getImmutable();                    //V^μ
        Element right_3 = pairing.pairing(V_miu.mul(mul_3_result).getImmutable(), pku).getImmutable();

        Element right = right_1.mul(right_2).mul(right_3).getImmutable();
        boolean result = left.isEqual(right);

        //System.out.println("verify BCPA_Proof done! " + result);
        return result;
    }
}
