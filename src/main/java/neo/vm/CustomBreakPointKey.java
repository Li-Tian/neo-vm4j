package neo.vm;

import java.util.Arrays;

import neo.csharp.BitConverter;
import neo.log.notr.TR;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: CustomBreakPointKey
 * @Package neo.vm
 * @Description: 自定义的断点的key类
 * @date Created in 10:22 2019/3/4
 */
public class CustomBreakPointKey {
    //断点哈希
    private byte[] breakPointHash;

    /**
     * @Author:doubi.liu
     * @description:构造函数
     * @date:2019/3/11
     */
    public CustomBreakPointKey(byte[] breakPointHash) {
        TR.enter();
        this.breakPointHash = breakPointHash;
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description:获取断点key的哈希值
     * @date:2019/3/11
     */
    public byte[] getBreakPointHash() {
        TR.enter();
        return TR.exit(breakPointHash);
    }

    /**
     * @Author:doubi.liu
     * @description:设置断点key的哈希值
     * @date:2019/3/11
     */
    public void setBreakPointHash(byte[] breakPointHash) {
        TR.enter();
        this.breakPointHash = breakPointHash;
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description:覆盖的判等方法
     * @date:2019/3/11
     */
    @Override
    public boolean equals(Object o) {
        TR.enter();
        if (this == o) {
            return TR.exit(true);
        }
        if (!(o instanceof CustomBreakPointKey)) {
            return TR.exit(false);
        }
        CustomBreakPointKey that = (CustomBreakPointKey) o;
        return TR.exit(Arrays.equals(getBreakPointHash(), that.getBreakPointHash()));

    }

    /**
      * @Author:doubi.liu
      * @description:覆盖的计算hashcode的方法
      * @param
      * @date:2019/3/11
    */
    @Override
    public int hashCode() {
        TR.enter();
        return TR.exit(BitConverter.toInt(getBreakPointHash()));
    }
}
