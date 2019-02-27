package neo.vm.Types;

import java.math.BigInteger;
import java.util.Arrays;

import neo.log.notr.TR;
import neo.vm.StackItem;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: Integer
 * @Package neo.vm.Types
 * @Description: 基础类型，StackItem的子类
 * @date Created in 14:19 2019/2/25
 */
public class Integer extends StackItem {

    /**
     * 内部寄存器
     */
    private BigInteger value;

    /**
     * @Author:doubi.liu
     * @description: 构造函数
     * @date:2019/2/25
     */
    public Integer(BigInteger value) {
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
        if (other instanceof Integer) {
            return TR.exit(value.compareTo(other.getBigInteger()) == 0);
        }
        byte[] bytes_other = other.getByteArray();
        return TR.exit(Arrays.equals(getByteArray(), bytes_other));
    }

    /**
     * @Author:doubi.liu
     * @description: 获取内部大整数
     * @date:2019/2/25
     */
    @Override
    public BigInteger getBigInteger() {
        TR.enter();
        return TR.exit(value);
    }

    /**
     * @Author:doubi.liu
     * @description:获取布尔值
     * @date:2019/2/25
     */
    @Override
    public boolean getBoolean() {
        TR.enter();
        return TR.exit(value.compareTo(new BigInteger("0")) != 0);
    }

    /**
     * @Author:doubi.liu
     * @description:获取内部大整数的字节数组
     * @date:2019/2/25
     */
    @Override
    public byte[] getByteArray() {
        TR.enter();
        return TR.exit(value.toByteArray());
    }
}
