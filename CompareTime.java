/**
 * FileName: CompareTime
 * Author:   star
 * Date:     2019/11/1 15:29
 * Description: 比较不同机制的耗时
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

import BCPA.roles.BCPA_CloudServer;
import BCPA.roles.BCPA_PKG;
import BCPA.roles.BCPA_User;
import BCPA.wrappers.BCPA_Proof;
import CPVPA.roles.CPVPA_CloudServer;
import CPVPA.roles.CPVPA_PKG;
import CPVPA.roles.CPVPA_User;
import CPVPA.wrappers.CPVPA_Proof;
import IBPA.roles.IBPA_CloudServer;
import IBPA.roles.IBPA_PKG;
import IBPA.roles.IBPA_User;
import IBPA.wrappers.IBPA_Proof;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * 〈一句话功能简述〉<br> 
 * 〈比较不同机制的耗时〉
 *
 * @author star
 * @create 2019/11/1
 * @since 1.0.0
 */
public class CompareTime {

    public static void main(String[] args) {
        String userID = "AmyZou";
        int bytes_perBlock = 20;    //每一块160bit
        String filePath = "C:\\Users\\star\\Desktop\\实验代码\\实验代码\\AuditingScheme\\file\\TestFile.pdf";

        //对比user生成tag的时间、生成proof、验证proof、用户验证TPA行为、通信时间（传送challenge message）
//        BCPA_PKG bcpa_pkg = new BCPA_PKG();
//        BCPA_CloudServer bcpa_cloudServer = new BCPA_CloudServer(bcpa_pkg);
//        BCPA_User bcpa_user = new BCPA_User(userID, bcpa_pkg);



        //[][0] - BCPA [][1] - CPVPA  [][2] - IBPA
        double[][] userCompTime = new double[10][3];
        double[][] tpaCompTime = new double[10][3];
        int[] testC = new int[]{ 200, 300, 400, 420, 440, 460, 480, 500, 550, 600};
        double[][] verifyDelayTime = new double[17][3];

        int c = 40; //挑战个数40
        int index = 0;
//        for(int n = 1000; n < 10001; n = n + 1000) {    //文件总块数
//
//            CPVPA_PKG cpvpa_pkg = new CPVPA_PKG();
//            CPVPA_CloudServer cpvpa_cloudServer = new CPVPA_CloudServer(cpvpa_pkg);
//            CPVPA_User cpvpa_user = new CPVPA_User(userID, cpvpa_pkg);
//            System.out.println("CPVPA performance evaluation");
//            if (cpvpa_user.splitFile(filePath, bytes_perBlock, n, c)) {
//                long begin = System.currentTimeMillis();
//                boolean flag = cpvpa_user.genTagSet();
//                long end = System.currentTimeMillis();
//                userCompTime[index][1] = (end - begin) / 1000.0;
//                System.out.println("1.Comuputation time on User side: " + userCompTime[index][1]);
//                if (flag) {
//                    cpvpa_user.sendFileAndTagToCS(cpvpa_cloudServer);
//                    cpvpa_user.requestAudit();
//                    cpvpa_pkg.sendChallToCS(cpvpa_cloudServer);
//                    CPVPA_Proof cpvpa_proof = cpvpa_cloudServer.genProof();
//                    begin = System.currentTimeMillis();
//                    boolean result = cpvpa_pkg.verifyProof(cpvpa_proof, cpvpa_user.getFileData());
//                    end = System.currentTimeMillis();
//                    tpaCompTime[index][1] = (end - begin) / 1000.0;
//                    System.out.println("3.Verification delay: " + tpaCompTime[index][1]);
//                    System.out.println("CPVPA audit result is: " + result + "\n");
//                }
//            }
//
//            IBPA_PKG ibpa_pkg = new IBPA_PKG();
//            IBPA_CloudServer ibpa_cloudServer = new IBPA_CloudServer(ibpa_pkg);
//            IBPA_User ibpa_user = new IBPA_User(userID, ibpa_pkg);
//
//            System.out.println("IBPA performance evaluation");
//            if (ibpa_user.splitFile(filePath, bytes_perBlock, n, c)) {
//                long begin = System.currentTimeMillis();
//                boolean flag = ibpa_user.genTagSet();
//                long end = System.currentTimeMillis();
//                userCompTime[index][2] = (end - begin) / 1000.0;
//                System.out.println("1.Comuputation time on User side: " + userCompTime[index][2]);
//                if (flag) {
//                    ibpa_user.sendFileAndTagToCS(ibpa_cloudServer);
//                    ibpa_user.requestAudit();
//                    ibpa_pkg.sendChallToCS(ibpa_cloudServer);
//                    IBPA_Proof ibpa_proof = ibpa_cloudServer.genProof();
//                    begin = System.currentTimeMillis();
//                    boolean result = ibpa_pkg.verifyProof(ibpa_proof, ibpa_user.getR(), ibpa_user.getName());
//                    end = System.currentTimeMillis();
//                    tpaCompTime[index][2] = (end - begin) / 1000.0;
//                    System.out.println("3.Verification delay: " + tpaCompTime[index][2]);
//                    System.out.println("IBPA audit result is: " + result + "\n");
//                }
//            }
//
//            index = index + 1;
//        }

        int n = 600;
        for(int i = 0; i < testC.length; i ++) {
            c = testC[i];
            CPVPA_PKG cpvpa_pkg = new CPVPA_PKG();
            CPVPA_CloudServer cpvpa_cloudServer = new CPVPA_CloudServer(cpvpa_pkg);
            CPVPA_User cpvpa_user = new CPVPA_User(userID, cpvpa_pkg);
            System.out.println("CPVPA performance evaluation");
            long begin = System.currentTimeMillis();
            if (cpvpa_user.splitFile(filePath, bytes_perBlock, n, c)) {
                boolean flag = cpvpa_user.genTagSet();
                if (flag) {
                    cpvpa_user.sendFileAndTagToCS(cpvpa_cloudServer);
                    cpvpa_user.requestAudit();
                    cpvpa_pkg.sendChallToCS(cpvpa_cloudServer);
                    CPVPA_Proof cpvpa_proof = cpvpa_cloudServer.genProof();
                    boolean result = cpvpa_pkg.verifyProof(cpvpa_proof, cpvpa_user.getFileData());
                    long end = System.currentTimeMillis();
                    verifyDelayTime[i][1] = (end - begin) / 1000.0;
                    System.out.println("3.Verification delay: " + verifyDelayTime[i][1]);
                    System.out.println("CPVPA audit result is: " + result + "\n");
                }
            }

            IBPA_PKG ibpa_pkg = new IBPA_PKG();
            IBPA_CloudServer ibpa_cloudServer = new IBPA_CloudServer(ibpa_pkg);
            IBPA_User ibpa_user = new IBPA_User(userID, ibpa_pkg);
            System.out.println("IBPA performance evaluation");
            begin = System.currentTimeMillis();
            if (ibpa_user.splitFile(filePath, bytes_perBlock, n, c)) {
                boolean flag = ibpa_user.genTagSet();
                if (flag) {
                    ibpa_user.sendFileAndTagToCS(ibpa_cloudServer);
                    ibpa_user.requestAudit();
                    ibpa_pkg.sendChallToCS(ibpa_cloudServer);
                    IBPA_Proof ibpa_proof = ibpa_cloudServer.genProof();
                    boolean result = ibpa_pkg.verifyProof(ibpa_proof, ibpa_user.getR(), ibpa_user.getName());
                    long end = System.currentTimeMillis();
                    verifyDelayTime[i][2] = (end - begin) / 1000.0;
                    System.out.println("3.Verification delay: " + verifyDelayTime[i][2]);
                    System.out.println("IBPA audit result is: " + result + "\n");
                }
            }
        }

        try {
            /* 写入Txt文件 */
            File writename = new File("../file/time.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));

            out.write("\nuserCompTime\n");
            for(int i = 0; i < 10; i ++) {

                System.out.print(userCompTime[i][1] + ",");
                System.out.println();
                System.out.print(userCompTime[i][2] + ",");
                System.out.println();
                out.write(userCompTime[i][1] + ", " + userCompTime[i][2] + ", ");
            }

            out.write("\ntpaCompTime\n");
            for(int i = 0; i < 10; i ++) {
                System.out.print(tpaCompTime[i][1] + ",");
                System.out.println();
                System.out.print(tpaCompTime[i][2] + ",");
                System.out.println();
                out.write(tpaCompTime[i][1] + ", " + tpaCompTime[i][2] + ", ");
            }

            out.write("\nverifyDelayTime\n");
            for(int i = 0 ; i < 17; i ++) {
                System.out.print(verifyDelayTime[i][1] + ",");
                System.out.println();
                System.out.print(verifyDelayTime[i][2] + ",");
                System.out.println();
                out.write(verifyDelayTime[i][1] + ", " + verifyDelayTime[i][2] + ", ");
            }
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
