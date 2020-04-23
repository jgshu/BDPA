/**
 * FileName: MasterPublicKey
 * Author:   star
 * Date:     2019/10/3 10:17
 * Description: PKG的主公钥
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IBPA.keys;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 〈一句话功能简述〉<br> 
 * 〈PKG的主公钥〉
 *
 * @author star
 * @create 2019/10/3
 * @since 1.0.0
 */
public class MasterPublicKey {
    private  Element pk;
    private  Element g;

    public MasterPublicKey(Element pk, Element g) {
        this.pk = pk;
        this.g = g;
    }

    public  Element getG(){
        return g.getImmutable();
    }

    public  Element getPk() {
        return pk.getImmutable();
    }
}
