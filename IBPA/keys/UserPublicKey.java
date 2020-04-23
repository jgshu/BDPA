/**
 * FileName: UserPublicKey
 * Author:   star
 * Date:     2019/10/23 9:03
 * Description: 用户公钥
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IBPA.keys;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 〈一句话功能简述〉<br> 
 * 〈用户公钥〉
 *
 * @author star
 * @create 2019/10/23
 * @since 1.0.0
 */
public class UserPublicKey {
    private Element Pu0;
    private Element Pu1;

    public UserPublicKey(Element Pu0, Element Pu1) {
        this.Pu0 = Pu0;
        this.Pu1 = Pu1;
    }


    public  Element getPu0() {
        return Pu0.getImmutable();
    }

    public  Element getPu1() {
        return Pu1.getImmutable();
    }
}
