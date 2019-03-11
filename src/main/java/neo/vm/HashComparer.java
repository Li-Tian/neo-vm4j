package neo.vm;

import java.util.Arrays;

import neo.csharp.BitConverter;
import neo.log.notr.TR;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: HashComparer
 * @Package neo.vm
 * @Description: 哈希比较器
 * @date Created in 14:27 2019/2/28
 */
public class HashComparer{

    /**
      * @Author:doubi.liu
      * @description:判等方法
      * @param x 数据1 y 数据2
      * @date:2019/2/28
    */
    public boolean equals(byte[] x, byte[] y) {
        TR.enter();
        return TR.exit(Arrays.equals(x,y));
    }

    /**
      * @Author:doubi.liu
      * @description:获取哈希code的指令
      * @param obj
      * @date:2019/2/28
    */
    public int hashCode(byte[] obj)
    {
        TR.enter();
        return TR.exit(BitConverter.toInt(obj));
    }
}
