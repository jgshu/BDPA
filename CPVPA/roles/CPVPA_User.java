/**
 * FileName: BCPA_User
 * Author:   star
 * Date:     2019/10/22 11:06
 * Description: 用户完成store, auditRequest, checkLog功能
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package CPVPA.roles;

import CPVPA.keys.UserPublicKey;
import CPVPA.wrappers.*;
import CPVPA.keys.UserPrivateKey;
import Util.Util;
import it.unisa.dia.gas.jpbc.Element;
import org.bouncycastle.crypto.DataLengthException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈用户完成store, auditRequest, checkLog功能〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class CPVPA_User {

    private CPVPA_PKG pkg;
    private String IDu;                     //用户身份
    private UserPrivateKey userPrivateKey;  //用户私钥  xu由用户自己填 ssku = {xu, Du0, Du1}
    private UserPublicKey userPublicKey;    //用户公钥  spku = {pku, IDu}, Qu0和Qu1是由IDu可以计算出来
    private Element name;                   //name ∈ Zp
    private FileData fileData;              //文件划分结果
    private TagSet tagSet;                  //生成的tag集合
    private Element r, R, tao, V, W;        //r是用户选择的随机数∈Zp，R=g^r, tao=H(name || n || △ || spku) V=H3(△) W=H4(△)
    private int numChallenges;

    public Element getr() {
        return r.getImmutable();
    }

    public FileData getFileData() {
        return fileData;
    }

    public CPVPA_User(String _ID, CPVPA_PKG _pkg) {
        this.IDu = _ID;
        this.pkg = _pkg;
        Element xu = Util.getRandomFromZp();                            //xu ∈ Zp*    pku = g^xu
        this.userPrivateKey = this.pkg.getSecretKey(xu, this.IDu);      //私钥ssku = {xu, Du0, Du1}
        this.userPublicKey = this.pkg.getUserPublicKey();               //公钥spku = {pku, IDu} 附加Qu0和Qu1，可以有IDu计算得出
        //System.out.println("BCPA_User setup done!");
    }



    // ========================== 3. Store - 用户把文件分块 =============================
    // F = m1||m2|| · · ·||mn      mj ∈ Zp, j ∈ [1, n]
    //把文件切分成n块，每一块是160 bit = 20 byte，bytes_perBlock = 20, 方便映射到Zp中
    public boolean splitFile(String path, int bytes_perBlock, int readNumOfBlocks, int numChallenges){
        this.numChallenges = numChallenges;
        BufferedInputStream bis = null;
        try{
            File file = new File(path);
            //文件大小以字节为单位，除以 20byte 得到文件能分成几块，向上取整
            int n = (int)Math.ceil(file.length()/ bytes_perBlock);
            if (readNumOfBlocks < n)
                n = readNumOfBlocks;

            FileInputStream fileInputStream = new FileInputStream(file);
            bis = new BufferedInputStream(fileInputStream);
            byte[] block_bytes = new byte[bytes_perBlock];  //数据块bytes数组

            name = Util.getRandomFromZp();                  //随机的文件名 name ∈ Zp
            fileData = new FileData(name, n, n/2);      //n个数据块，每个块是160 bits，均是Zp的元素

            int len, i = 0;
            while ((len = bis.read(block_bytes)) != -1 && i < n && i < readNumOfBlocks){
                //每次从文件中读取 160 byte 数据，映射到Zp中得到mj
                Element mj = Util.hashFromBytesToZp(block_bytes);
                FileBlock fileBlock = new FileBlock(bytes_perBlock, block_bytes, mj);
                fileData.setFileBlock(fileBlock, i);
                i ++;
            }
            System.out.println("n = " + fileData.getDataLen() + ", c = " + numChallenges);
            bis.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ========================== 4. 用户对所有文件块进行签名计算tag =============================
    //生成文件的签名，包含了n个数据块的签名
    public boolean genTagSet(){
        int n = fileData.getDataLen();              //生成 n 个签名，Signature 本质是 List<Element>

        Element delta = Util.getRandomFromZp();     //one-time number △ ∈ Zp
        //τ = H(name || n || △ || spku)
        tao = Util.hashFromStringToZp("" + name + n + delta + this.userPublicKey.getPku() + this.userPublicKey.getIDu());
        V = Util.hashFromBytesToG1(delta.toCanonicalRepresentation()).getImmutable();   //V = H3(△)
        W = Util.hashFromStringToG1("" + delta).getImmutable();                         //W = H4(△)

        r = Util.getRandomFromZp();                 // 选择随机数r ∈ Zp*
        R = this.pkg.getMasterPublicKey().getG().powZn(r).getImmutable();               //R = g^r

        this.tagSet = new TagSet(n, R, delta);

        for (int j = 0; j < n; j++) {
            tagSet.addTag(genBlockTag(j));
        }
        System.out.println("generate tags for all data blocks done! ==========> tagSet have " + tagSet.getTagSetLen() + " tags.");
        return true;
    }

    //生成一个数据块的签名  Sj , Tj ∈ G
    public Tag genBlockTag(int j) throws DataLengthException {
        Element mj = fileData.getBlock(j).getMj();                  //获取数据块 mj ∈ Zp
        Element Tj = Util.hashFromStringToG1("" + j + tao + R);     //Tj = H2(j || τ || R) ∈ G

        //Sj = (Duo*(V^xu))^mj * (Du1*(W^xu))^H(j||τ||R) * (Tj^r)
        Element Du0 = this.userPrivateKey.getDu0();     // ∈ G
        Element Du1 = this.userPrivateKey.getDu1();     // ∈ G
        Element xu = this.userPrivateKey.getXu();       // ∈ Zp

        //Sj1 = (Duo*(V^xu))^mj
        Element Vxu = V.powZn(xu).getImmutable();
        Element Du0_Vxu = Du0.mul(Vxu).getImmutable();
        Element Sj1 = Du0_Vxu.powZn(mj).getImmutable();

        //Sj2 = (Du1*(W^xu))^H(j||τ||R)
        Element Wxu = W.powZn(xu).getImmutable();
        Element Du1_Wxu = Du1.mul(Wxu).getImmutable();
        Element H_jtaoR = Util.hashFromStringToZp(""+ j + tao + R);
        Element Sj2 = Du1_Wxu.powZn(H_jtaoR).getImmutable();

        //Sj3 = Tj^r
        Element Sj3 = Tj.powZn(r).getImmutable();

        Element Sj = Util.getOneFromG1();
        Sj = Sj.mul(Sj1).mul(Sj2).mul(Sj3).getImmutable();

        Tag tag = new Tag(Sj, Tj);
        return tag;
    }

    public void sendFileAndTagToCS(CPVPA_CloudServer cloudServer) {
        cloudServer.storeFileAndTag(this.fileData, this.tagSet, this.userPublicKey);
    }

    public void requestAudit() {
        this.pkg.genChall(fileData.getDataLen(), numChallenges);
    }


}
