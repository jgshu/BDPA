/**
 * FileName: TagSet
 * Author:   star
 * Date:     2019/10/22 15:18
 * Description: 文件所有数据块的签名集合
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package CPVPA.wrappers;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈文件所有数据块的签名集合〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class TagSet {
    private List<Tag> tags;
    private Element R;
    private Element delta;

    public TagSet(int n, Element R, Element delta) {
        this.tags = new ArrayList<Tag>(n);
        this.R = R;
        this.delta = delta;
    }

    public Element getR() {
        return this.R.getImmutable();
    }

    public Element getDelta() {
        return this.delta.getImmutable();
    }

    public int getTagSetLen() {
        return  tags.size();
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public Tag getTag(int index){
        return tags.get(index);
    }
}
