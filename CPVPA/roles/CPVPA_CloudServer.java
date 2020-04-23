/**
 * FileName: BCPA_CloudServer
 * Author:   star
 * Date:     2019/10/22 15:44
 * Description: 模拟云服务器，存储用户数据和计算生成proof
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package CPVPA.roles;


import Util.Util;
import CPVPA.keys.UserPublicKey;
import CPVPA.wrappers.Challenge;
import CPVPA.wrappers.FileData;
import CPVPA.wrappers.CPVPA_Proof;
import CPVPA.wrappers.TagSet;
import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈模拟云服务器，存储用户数据和计算生成proof〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class CPVPA_CloudServer {
    private FileData fileData;
    private TagSet tagSet;
    private CPVPA_Proof proof;
    private Challenge challenge;
    private CPVPA_PKG pkg;

    public CPVPA_CloudServer(CPVPA_PKG pkg) {
        this.pkg = pkg;
    }

    // ========================== 5. CSP对用户上传的数据进行校验 =============================
    //fileData -> M, tagSet -> Si 集合, R, △
    public boolean storeFileAndTag(FileData _fileData, TagSet _tagSet, UserPublicKey userPublicKey) {
        this.fileData = _fileData;
        this.tagSet = _tagSet;

        int n = tagSet.getTagSetLen();
        Element delta = tagSet.getDelta();
        Element R = tagSet.getR();
        Element g = pkg.getMasterPublicKey().getG();
        Element pk = pkg.getMasterPublicKey().getPk();
        String IDu = userPublicKey.getIDu();
        Element Qu0 = userPublicKey.getQu0();
        Element Qu1 = userPublicKey.getQu1();
        Element pku = userPublicKey.getPku();

        Element tao = Util.hashFromStringToZp("" + fileData.getName() + n + delta + pku + IDu);     //τ = H(name || n || △ || spku) ∈ Zp
        Element V = Util.hashFromBytesToG1(delta.toCanonicalRepresentation()).getImmutable();       //V = H3(△)
        Element W = Util.hashFromStringToG1("" + delta).getImmutable();                             //W = H4(△)

        Element mul_1_result = Util.getOneFromG1();     //Si 从1-n的连乘
        Element mul_2_result = Util.getOneFromG1();     //(Qu0^mi) * (Qu1^H_itaoR) 从1-n的连乘
        Element mul_3_result = Util.getOneFromG1();     //(V^mi) * (W^H_itaoR) 从1-n的连乘
        Element mul_4_result = Util.getOneFromG1();     //Ti 从1-n的连乘
        Element Si, Ti, mi, H_itaoR;
        Element Qu0_mi, Qu1_hi, tmp_mul_2, V_mi, W_hi, tmp_mul_3;
        for (int i = 0 ; i < n; i ++) {
            Si = tagSet.getTag(i).getS();
            Ti = tagSet.getTag(i).getT();
            mi = fileData.getBlock(i).getMj();                      //mi ∈ Zp
            H_itaoR = Util.hashFromStringToZp(""+ i + tao + R);     //H(i||τ||R)

            mul_1_result = mul_1_result.mul(Si).getImmutable();


            Qu0_mi = Qu0.powZn(mi).getImmutable();          //(Qu0^mi)
            Qu1_hi = Qu1.powZn(H_itaoR).getImmutable();     //(Qu1^H_itaoR)
            tmp_mul_2 = Qu0_mi.mul(Qu1_hi).getImmutable();  //(Qu0^mi) * (Qu1^H_itaoR)
            mul_2_result = mul_2_result.mul(tmp_mul_2).getImmutable();

            V_mi = V.powZn(mi).getImmutable();              //(V^mi)
            W_hi = W.powZn(H_itaoR).getImmutable();         //(W^H_itaoR)
            tmp_mul_3 = V_mi.mul(W_hi).getImmutable();      //(V^mi) * (W^H_itaoR)
            mul_3_result = mul_3_result.mul(tmp_mul_3).getImmutable();

            mul_4_result = mul_4_result.mul(Ti).getImmutable();
        }
        Element left = pkg.getPairing().pairing(mul_1_result, g).getImmutable();        //e(Si连乘积, g)
        Element right_1 = pkg.getPairing().pairing(mul_2_result, pk).getImmutable();    //e(第二个连乘积, g^sk)
        Element right_2 = pkg.getPairing().pairing(mul_3_result, pku).getImmutable();   //e(第三个连乘积, pku)
        Element right_3 = pkg.getPairing().pairing(mul_4_result, R).getImmutable();     //e(Ti连乘积, R)

        Element right = right_1.mul(right_2).mul(right_3).getImmutable();

        if (left.equals(right)) {
            //System.out.println("Check user's file OK!");
            return true;
        } else {
            System.out.println("BCPA_User's file error!");
            return false;
        }
    }

    public void storeChallenge(Challenge chal) {
        this.challenge = chal;
    }

    // ========================== 7. 根据challenge msg 生成Proof =============================
    public CPVPA_Proof genProof() {
        String blockHashStr = challenge.getConcat_BlockHash();
        int k1 = Util.h1_pai_key(blockHashStr);
        String k2 = Util.h2_f_key(blockHashStr);
        int c = fileData.getC();
        int n = fileData.getDataLen();

        //根据challenge msg 生成完整的(j,vj)
        List<Element> vj_list = new ArrayList<>(c);
        for (int i = 0; i < c; i ++) {
            vj_list.add(Util.pseudoFunc(k2, i));
        }
        challenge.setList_vj(vj_list);
        challenge.setList_j(Util.pseudoPerm(k1, n, c));

        //生成Proof = {S，R, μ, △}
        int j;              //j ∈ [0, n-1] 选择的块的序号
        Element vj;         //vj ∈ Zp
        Element mj;         //mj ∈ Zp
        Element Sj;         //Sj ∈ G1
        Element Sj_vj;      //Sj^vj

        Element S = Util.getOneFromG1();        //Sj^vj连乘积
        Element miu = Util.getZeroFromZp();     //vj*mj求和

        for(int i = 0; i < c; i ++){            //计算 mj*vj 求和, vj和mj均 ∈ Zp
            j = this.challenge.getJ(i);
            vj = this.challenge.getVj(i);

            mj = this.fileData.getBlock(j).getMj();
            Sj = this.tagSet.getTag(j).getS();

            Sj_vj = Sj.powZn(vj).getImmutable();
            S = S.mul(Sj_vj);                               //连乘积 Sj^vj
            miu = miu.add(mj.mulZn(vj).getImmutable());     //sum of mj*vj
        }

        //封装一下，返回Proof = {S，R, μ, △}
        proof = new CPVPA_Proof(S, tagSet.getR(), miu, tagSet.getDelta());
        return proof;
    }
//
//    public void getChallenge(EthCaller ethCaller, int taskID) {
//        ethCaller.generateChallenge();
//        this.challenge = ethCaller.getChallenge(taskID);
//    }
}
