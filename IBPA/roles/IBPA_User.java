/**
 * FileName: BCPA_User
 * Author:   star
 * Date:     2019/10/22 11:06
 * Description: 用户完成store, auditRequest, checkLog功能
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IBPA.roles;

import Util.Util;
import IBPA.wrappers.*;
import IBPA.keys.UserPrivateKey;
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
public class IBPA_User {

    private IBPA_PKG pkg;
    private String ID;  //用户身份
    private UserPrivateKey userPrivateKey;  //用户私钥
    private String w;   //PKG返回的状态参数
    private String name = "filename";  //随机的文件名name
    private FileData fileData;
    private TagSet tagSet;
    private Element r;
    private int numChallenges;

    public Element getR() {
        return r.getImmutable();
    }

    public String getName() {
        return name;
    }

    public IBPA_User(String _ID, IBPA_PKG _pkg) {
        this.ID = _ID;
        this.pkg = _pkg;
        this.userPrivateKey = this.pkg.getSecretKey(this.ID);
        this.w = this.pkg.getW();
        //System.out.println("BCPA_User setup done!");
    }

    // ========================== 1. 用户把文件分块 =============================
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
            //n个数据块，每个块是160 bits，均是Zp的元素
            this.fileData = new FileData(n);

            int len, i = 0;
            while ((len = bis.read(block_bytes)) != -1 && i < n && i < readNumOfBlocks){
                //每次从文件中读取 160 byte 数据，映射到Zp中得到mj
                Element mj = Util.hashFromBytesToZp(block_bytes);
                FileBlock fileBlock = new FileBlock(bytes_perBlock, block_bytes, mj);
                fileData.setFileBlock(fileBlock, i);
                i ++;
            }
            System.out.println("n = " + fileData.getDataLen() + ", c = " + numChallenges);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ========================== 4. 用户对所有文件块进行签名计算tag =============================
    //生成文件的签名，包含了n个数据块的签名
    //选择随机数r ∈ Zp和随机的文件名name，和服务器返回的状态参数w
    public boolean genTagSet(){

        //生成 n 个签名，Signature 本质是 List<Element>
        int n = fileData.getDataLen();
        this.tagSet = new TagSet(n);

        // 选择随机数r ∈ Zp
        r = Util.getRandomFromZp();

        for (int j = 0; j < n; j++) {
            tagSet.addTag(genBlockTag(j));
        }
        //System.out.println("generate tags for all data blocks done! ==========> tagSet have " + tagSet.getTagSetLen() + " tags.");
        return true;
    }

    //生成一个数据块的签名  Sj , Tj ∈ G1
    // 数据块 mj 的签名 (Sj , Tj) = (r·H2(w||j) + H(name||j)·sk·PU,0 + mj·sk·PU,1, r·g)
    public Tag genBlockTag(int j) throws DataLengthException {
        long begin = System.currentTimeMillis();
        //获取数据块 mj
        Element mj = fileData.getBlock(j).getMj();

        //Sj = r·H2(w||j) + H(name||j)·sk·Pu0 + mj·sk·Pu1 ---> Sj1 * Sj2 * Sj3

        //Sj1 = H2(w||j) ^ r
        Element hash2wj = Util.hashFromStringToG1(w + j);
        Element Sj1 = hash2wj.powZn(r).getImmutable();

        //Sj2 = sPu0 ^ H(name||j)
        Element hashNamej = Util.hashFromStringToZp(name + j);
        Element Sj2 = userPrivateKey.getsPu0().powZn(hashNamej).getImmutable();

        //Sj3 = sPu1 ^ mj
        Element Sj3 = userPrivateKey.getsPu1().powZn(mj).getImmutable();

        //G1： + ---> *
        Element Sj = Util.getOneFromG1();
        Sj = Sj.mul(Sj1).mul(Sj2).mul(Sj3).getImmutable();

        //g^r
        Element Tj = pkg.getMasterPublicKey().getG().powZn(r).getImmutable();
        long end = System.currentTimeMillis();
        //System.out.println((end-begin));
        Tag tag = new Tag(Sj, Tj);
        return tag;
    }

    public void sendFileAndTagToCS(IBPA_CloudServer cloudServer) {
        cloudServer.storeFileAndTag(this.fileData, this.tagSet);
        //System.out.println("send fileData and tagSet to Cloud Server done!");
    }



    public void requestAudit() {
        this.pkg.genChall(fileData.getDataLen(), numChallenges);
    }


}
