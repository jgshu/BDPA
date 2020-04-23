/**
 * FileName: UserPrivateKey
 * Author:   star
 * Date:     2019/10/3 10:20
 * Description: 用户从PKG获得的私钥
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package CPVPA.keys;

import BCPA.BCPA;
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
    private Element Du0;   //Du0 = Qu0 ^ sk    Qu0 = H1(IDu,0)
    private Element Du1;   //Du1 = Qu1 ^ sk    Qu1 = H1(IDu,1)
    private Element xu;

    public UserPrivateKey(Element xu, Element Du0, Element Du1) {
        this.xu = xu;
        this.Du0 = Du0;
        this.Du1 = Du1;
    }

    public Element getXu() {
        return xu;
    }

    public  Element getDu0() {
        return Du0.getImmutable();
    }

    public  Element getDu1() {
        return Du1.getImmutable();
    }

    public void log() {
        System.out.println("LOG: userPrivateKey for BCPA_User ssku = {Du0, Du1, xu} = {" + Du0 + ", " + Du1 + ", " + xu + "}\n"
                + "Du0 = Qu0^sk  Qu0 = H1(IDu,0)   Du1 = Qu1^sk  Qu1 = H1(IDu,1)  xu ∈ Zp* ");
    }
}
