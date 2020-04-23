/**
 * FileName: MasterPrivateKey
 * Author:   star
 * Date:     2019/10/3 10:15
 * Description: PKG的主私钥
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IBPA.keys;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 〈一句话功能简述〉<br> 
 * 〈PKG的主私钥〉
 *
 * @author star
 * @create 2019/10/3
 * @since 1.0.0
 */
public class MasterPrivateKey {
    private Element sk;

    public MasterPrivateKey(Element sk) {
        this.sk = sk;
    }

    public  Element getSk() {
        return sk.getImmutable();
    }
}
