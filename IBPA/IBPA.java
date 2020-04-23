/**
 * FileName: BCPA
 * Author:   star
 * Date:     2019/10/22 15:48
 * Description: 完整流程仿真
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IBPA;

import IBPA.roles.IBPA_CloudServer;
import IBPA.roles.IBPA_PKG;
import IBPA.roles.IBPA_User;


/**
 * 〈一句话功能简述〉<br>
 * 〈完整流程仿真〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class IBPA {
    public static void main(String[] args) throws Exception {
        IBPA_PKG pkg = new IBPA_PKG();
        IBPA_CloudServer cloudServer = new IBPA_CloudServer(pkg);

        String userID = "AmyZou";
        IBPA_User user = new IBPA_User(userID, pkg);

        int bytes_perBlock = 128;
        String filePath = "C:\\Users\\star\\Desktop\\实验代码\\实验代码\\AuditingScheme\\file\\TestFile.pdf";


        if (user.splitFile(filePath, bytes_perBlock,1000, 600)) {
            if (user.genTagSet()) {
                user.sendFileAndTagToCS(cloudServer);
                user.requestAudit();
                pkg.sendChallToCS(cloudServer);
                boolean result = pkg.verifyProof(cloudServer.genProof(), user.getR(), user.getName());
                System.out.println("Audit result is: " + result);
            }
        }
    }
}
