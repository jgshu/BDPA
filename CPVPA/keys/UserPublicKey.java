/**
 * FileName: UserPublicKey
 * Author:   star
 * Date:     2019/10/23 9:03
 * Description: 用户公钥
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package CPVPA.keys;

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
    private Element pku;  //pku = g ^ xu
    private String IDu;
    //由IDu容易计算得出
    private Element Qu0;  //Qu0 = H1(IDu, 0)
    private Element Qu1;  //Qu1 = H1(IDu, 1)

    public UserPublicKey(Element pku, String IDu, Element Qu0, Element Qu1) {
        this.pku = pku;
        this.IDu = IDu;
        this.Qu0 = Qu0;
        this.Qu1 = Qu1;
    }

    public Element getQu0() {
        return Qu0.getImmutable();
    }

    public Element getQu1() {
        return Qu1.getImmutable();
    }

    public  Element getPku() {
        return pku.getImmutable();
    }

    public  String getIDu() {
        return IDu;
    }

    public void log() {
        System.out.println("LOG: usePublicKey for BCPA_User spku = {pku, IDu} = {" + pku + ", " + IDu  + "}\n"
                + "Qu0 = H1(IDu,0) = " + Qu0 + " Qu1 = H1(IDu,1) = " + Qu1);
    }
}
