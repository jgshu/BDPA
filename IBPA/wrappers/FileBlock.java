/**
 * FileName: FileBlock
 * Author:   star
 * Date:     2019/10/22 14:12
 * Description: 文件分割成的单个数据块，包含原始数据和映射到Zp的值
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package IBPA.wrappers;
import it.unisa.dia.gas.jpbc.Element;

/**
 * 〈一句话功能简述〉<br> 
 * 〈文件分割成的单个数据块，包含原始数据和映射到Zp的值〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class FileBlock {
    private byte[] raw_bytes;
    private Element mj;

    public FileBlock(int bytes_perBlock, byte[] blockBytes, Element element){
        if (bytes_perBlock != blockBytes.length ) {
            System.out.println("设置FileBlock失败，传入数据块bytes数有误");
            return;
        }
        raw_bytes = new byte[bytes_perBlock];
        System.arraycopy( blockBytes, 0, raw_bytes, 0, raw_bytes.length);
        this.mj = element;
    }

    public Element getMj() {
        return mj.getImmutable();
    }

}
