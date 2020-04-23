/**
 * FileName: Tag
 * Author:   star
 * Date:     2019/10/22 15:15
 * Description: 数据块mj的签名信息，包括Sj和Tj
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IBPA.wrappers;
import it.unisa.dia.gas.jpbc.Element;
/**
 * 〈一句话功能简述〉<br> 
 * 〈数据块mj的签名信息，包括Sj和Tj〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class Tag {
    private Element S;
    private Element T;


    public Tag(Element _S, Element _T ){
        this.S = _S;
        this.T = _T;
    }

    public Element getS() {
        return S.getImmutable();
    }

    public Element getT() {
        return T.getImmutable();
    }

}
