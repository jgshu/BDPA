/**
 * FileName: Challenge
 * Author:   star
 * Date:     2019/10/22 16:06
 * Description: 一对随机数，j和vj，由以太坊获得
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package CPVPA.wrappers;

import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;
import java.sql.Timestamp;
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
    //(Blt-fai+1;Blt-fai+2;...;Blt-1; t)
    private String concat_BlockHash;    //fai个块的hash值连成的字符串
    private BigInteger timestamp;       //获取的区块的时间戳
    private List<Integer> list_j;       //j 是从 [1, n] 中随机选出的数
    private List<Element> list_vj;      //vj ∈ Zp

    public Challenge(String blockhashs, BigInteger timestamp){
        this.concat_BlockHash = blockhashs;
        this.timestamp = timestamp;
    }

    public BigInteger getTimestamp() {
        return timestamp;
    }

    public String getConcat_BlockHash() {
        return concat_BlockHash;
    }

    public int getChallengeLen() {
        return list_j.size();
    }


    public void setList_j(List<Integer> list_j) {
        this.list_j = list_j;
    }

    public void setList_vj(List<Element> list_vj) {
        this.list_vj = list_vj;
    }

    public Integer getJ(int index) {
        return list_j.get(index);
    }

    public Element getVj(int index) {
        return list_vj.get(index).getImmutable();
    }

}
