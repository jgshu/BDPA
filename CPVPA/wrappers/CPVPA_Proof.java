/**
 * FileName: BCPA_Proof
 * Author:   star
 * Date:     2019/10/22 16:02
 * Description: 服务器端生成的证据
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package CPVPA.wrappers;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 〈一句话功能简述〉<br> 
 * 〈服务器端生成的证据〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class CPVPA_Proof {
    Element S;
    Element R;
    Element MIU;
    Element delta;

    public CPVPA_Proof(Element s, Element r, Element miu, Element delta) {
        this.S = s;
        this.R = r;
        this.MIU = miu;
        this.delta = delta;
    }

    public Element getS() {
        return S.getImmutable();
    }

    public Element getR() {
        return R.getImmutable();
    }

    public Element getMIU() {
        return MIU.getImmutable();
    }

    public Element getDelta() {
        return delta.getImmutable();
    }
}
