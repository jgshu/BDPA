/**
 * FileName: BCPA_Proof
 * Author:   star
 * Date:     2019/10/22 16:02
 * Description: 服务器端生成的证据
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IBPA.wrappers;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 〈一句话功能简述〉<br> 
 * 〈服务器端生成的证据〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class IBPA_Proof {
    Element S;
    Element T;
    Element MIU;
    Element Y;

    public IBPA_Proof(Element s, Element t, Element miu, Element y) {
        this.S = s;
        this.T = t;
        this.MIU = miu;
        this.Y = y;
    }

    public Element getS() {
        return S.getImmutable();
    }

    public Element getT() {
        return T.getImmutable();
    }

    public Element getMIU() {
        return MIU.getImmutable();
    }

    public Element getY() {
        return Y.getImmutable();
    }
}
