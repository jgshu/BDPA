/**
 * FileName: Challenge
 * Author:   star
 * Date:     2019/10/22 16:06
 * Description: 一对随机数，j和vj，由以太坊获得
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IBPA.wrappers;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈一对随机数，j和vj，由以太坊获得〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class Challenge {
    private List<Integer> list_j;   //j 是从 [1, n] 中随机选出的数
    private List<Element> list_vj;  //vj ∈ Zp
//    private Element R;
////    private Element r;
    public Challenge(){}

    public int getChallengeLen() {
        return list_j.size();
    }

    public void addToListJ(int j) {
        this.list_j.add((Integer)j);
    }

    public void addToListVj(Element vj) {
        this.list_vj.add(vj);
    }

    public Challenge(int n){
        list_j = new ArrayList<Integer>(n);
        list_vj = new ArrayList<Element>(n);
    }

    public Integer getJ(int index) {
        return list_j.get(index);
    }

    public Element getVj(int index) {
        return list_vj.get(index).getImmutable();
    }

}
