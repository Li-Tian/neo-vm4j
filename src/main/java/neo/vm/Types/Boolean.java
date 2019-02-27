package neo.vm.Types;

import java.math.BigInteger;
import java.util.Arrays;

import neo.log.notr.TR;
import neo.vm.StackItem;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: Boolean
 * @Package neo.vm.Types
 * @Description: StackItem的子类
 * @date Created in 15:28 2019/2/25
 */
public class Boolean extends StackItem {

    /**
     * 默认值，true
     */
    private static final byte[] TRUE = {1};
    /**
     * 默认值，false
     */
    private static final byte[] FALSE = new byte[0];

    /**
     * 内部寄存器
     */
    private boolean value;

    /**
     * @Author:doubi.liu
     * @description:构造函数
     * @date:2019/2/25
     */
    public Boolean(boolean value) {
        TR.enter();
        this.value = value;
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description: 判等方法
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
        if (other instanceof Boolean) {
            return TR.exit(value == other.getBoolean());
        }
        byte[] bytes_other = other.getByteArray();
        return TR.exit(Arrays.equals(getByteArray(), bytes_other));
    }

    /**
     * @Author:doubi.liu
     * @description:获取内部寄存器大整形格式数值
     * @date:2019/2/25
     */
    @Override
    public BigInteger getBigInteger() {
        TR.enter();
        return TR.exit(value ? new BigInteger("1") : new BigInteger("0"));
    }

    /**
     * @Author:doubi.liu
     * @description:获取内部寄存器布尔类型格式数值
     * @date:2019/2/25
     */
    @Override
    public boolean getBoolean() {
        TR.enter();
        return TR.exit(value);
    }

    /**
     * @Author:doubi.liu
     * @description:获取内部寄存器字节数组格式数值
     * @date:2019/2/25
     */
    @Override
    public byte[] getByteArray() {
        TR.enter();
        return TR.exit(value ? TRUE : FALSE);
    }
}
