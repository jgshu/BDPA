/**
 * FileName: BCPA_CloudServer
 * Author:   star
 * Date:     2019/10/22 15:44
 * Description: 模拟云服务器，存储用户数据和计算生成proof
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IBPA.roles;


import Util.Util;
import IBPA.wrappers.Challenge;
import IBPA.wrappers.FileData;
import IBPA.wrappers.IBPA_Proof;
import IBPA.wrappers.TagSet;
import it.unisa.dia.gas.jpbc.Element;



/**
 * 〈一句话功能简述〉<br> 
 * 〈模拟云服务器，存储用户数据和计算生成proof〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class IBPA_CloudServer {
    private FileData fileData;
    private TagSet tagSet;
    private IBPA_Proof proof;
    private Challenge challenge;
    private IBPA_PKG pkg;

    public IBPA_CloudServer(IBPA_PKG pkg) {
        this.pkg = pkg;
    }

    public void storeFileAndTag(FileData _fileData, TagSet _tagSet) {
        this.fileData = _fileData;
        this.tagSet = _tagSet;
    }

    public void storeChallenge(Challenge chal) {
        this.challenge = chal;
    }

    // ========================== 6. 生成Proof =============================
    public IBPA_Proof genProof() {
        Element x = Util.getRandomFromZp();     //选择随机数 x ∈ Zp
        Element y = this.pkg.getUserPublicKey().getPu1().powZn(x).getImmutable();    //y = Pu1^x ∈ G1
        Element sum = Util.getZeroFromZp();     //sum of mj*vj ∈ Zp
        Element S = Util.getOneFromG1();        //连乘积 of Sj^vj ∈ G1
        Element T = Util.getOneFromG1();        //连乘积 of Tj^vj ∈ G1
        int j;              //challenge msg (j,vj)
        Element vj;         //vj ∈ Zp
        Element mj;         //mj ∈ Zp
        Element Sj, Tj;     //Sj Tj ∈ G1

        for(int i = 0; i < challenge.getChallengeLen(); i ++){  //计算 mj*vj 求和, vj和mj均 ∈ Zp
            j = this.challenge.getJ(i);
            vj = this.challenge.getVj(i);
            mj = this.fileData.getBlock(j).getMj();
            Sj = this.tagSet.getTag(j).getS();
            Tj = this.tagSet.getTag(j).getT();

            sum = sum.add(mj.mulZn(vj).getImmutable());     //sum of mj*vj
            S = S.mul(Sj.powZn(vj).getImmutable());         //连乘积 Sj^vj
            T = T.mul(Tj.powZn(vj).getImmutable());
        }

        // h(y) : G1 -> Zp
        Element hash_y = Util.hashFromG1ToZp(y);
        sum = sum.add(hash_y);

        Element x_inverse = x.invert().getImmutable();  //计算 x^-1
        Element miu = x_inverse.mulZn(sum).getImmutable();  //miu ∈ Zp

        //封装一下，返回Proof
        proof = new IBPA_Proof(S, T, miu, y);
        return proof;
    }

}
