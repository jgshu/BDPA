/**
 * FileName: BCPA
 * Author:   star
 * Date:     2019/10/22 15:48
 * Description: 完整流程仿真
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package CPVPA;

import CPVPA.blockchain.EthCaller;
import CPVPA.roles.CPVPA_CloudServer;
import CPVPA.roles.CPVPA_PKG;
import CPVPA.roles.CPVPA_User;
import CPVPA.wrappers.CPVPA_Proof;


/**
 * 〈一句话功能简述〉<br>
 * 〈完整流程仿真〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class CPVPA {
    public static void main(String[] args) throws Exception {
        CPVPA_PKG pkg = new CPVPA_PKG();
        CPVPA_CloudServer cloudServer = new CPVPA_CloudServer(pkg);

        String userID = "AmyZou";
        CPVPA_User user = new CPVPA_User(userID, pkg);

        int bytes_perBlock = 20;
        String filePath = "C:\\Users\\star\\Desktop\\实验代码\\实验代码\\AuditingScheme\\file\\TestFile.pdf";

        if (user.splitFile(filePath, bytes_perBlock, 1000, 40)) {
            if (user.genTagSet()) {
                user.sendFileAndTagToCS(cloudServer);
                user.requestAudit();
                pkg.sendChallToCS(cloudServer);
                CPVPA_Proof proof = cloudServer.genProof();
                pkg.verifyProof(proof, user.getFileData());
            }
        }
    }
}
