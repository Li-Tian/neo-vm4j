package neo.vm.Types;

import java.util.Arrays;

import neo.log.notr.TR;
import neo.vm.StackItem;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: ByteArray
 * @Package neo.vm.Types
 * @Description: ByteArray 是StackItem的子类
 * @date Created in 15:48 2019/2/25
 */
public class ByteArray extends StackItem {
    /**
     * 内部寄存器
     */
    private byte[] value;

    /**
     * @Author:doubi.liu
     * @description:构造函数
     * @date:2019/2/25
     */
    public ByteArray(byte[] value) {
        TR.enter();
        this.value = value;
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description:判等方法
     * @date:2019/2/25
     */
    @Override
    public boolean equals(StackItem other) {
        TR.enter();
        if (this == other) {
            return TR.exit(true);
        }
        if (other == null) {
            return TR.exit(false);
        }
        byte[] bytes_other = other.getByteArray();
        return TR.exit(Arrays.equals(value, bytes_other));
    }

    /**
     * @Author:doubi.liu
     * @description:获取内部寄存器的字节数组格式
     * @date:2019/2/25
     */
    @Override
    public byte[] getByteArray() {
        TR.enter();
        return TR.exit(value);
    }
}
