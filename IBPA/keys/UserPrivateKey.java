/**
 * FileName: UserPrivateKey
 * Author:   star
 * Date:     2019/10/3 10:20
 * Description: 用户从PKG获得的私钥
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IBPA.keys;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 〈一句话功能简述〉<br> 
 * 〈用户从PKG获得的私钥〉
 *
 * @author star
 * @create 2019/10/3
 * @since 1.0.0
 */
public class UserPrivateKey {
    private Element sPu0;
    private Element sPu1;

    public UserPrivateKey(Element sPu0, Element sPu1) {
        this.sPu0 = sPu0;
        this.sPu1 = sPu1;
    }


    public  Element getsPu0() {
        return sPu0.getImmutable();
    }

    public  Element getsPu1() {
        return sPu1.getImmutable();
    }

}
