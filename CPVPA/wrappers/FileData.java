/**
 * FileName: FileData
 * Author:   star
 * Date:     2019/10/22 14:11
 * Description: 分割后的文件，以块的列表形式存在
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package CPVPA.wrappers;


import it.unisa.dia.gas.jpbc.Element;

/**
 * 〈一句话功能简述〉<br> 
 * 〈分割后的文件，以块的列表形式存在〉
 *
 * @author star
 * @create 2019/10/22
 * @since 1.0.0
 */
public class FileData {
    // FileBlock 数组, 每个块大小为160 bits
    private Element name;
    private FileBlock [] data;
    private int c;

    //初始化为n个元素的数组
    public FileData(Element name, int n, int c){
        this.name = name;
        this.data = new FileBlock[n];
        this.c = c;
    }

    public int getC() {
        return c;
    }

    public Element getName() {
        return name.getImmutable();
    }

    public int getDataLen() {
        return data.length;
    }

    public FileBlock getBlock(int index) {
        if((index < 0) || (index >= data.length)) {
            System.out.println("设置FileBlock失败，传入数据块index数有误");
            System.exit(-1);
        }
        return data[index];
    }

    public void setFileBlock(FileBlock fileBlock, int index) {
        if((index < 0) || (index >= data.length)) {
            System.out.println("设置FileBlock失败，传入数据块index数有误");
            System.exit(-1);
        }
        this.data[index] = fileBlock;
    }
}
