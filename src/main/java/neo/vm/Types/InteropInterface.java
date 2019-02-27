package neo.vm.Types;

import neo.log.tr.TR;
import neo.vm.StackItem;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: InteropInterface
 * @Package neo.vm.Types
 * @Description: InteropInterface 是StackItem的子类
 * @date Created in 15:10 2019/2/25
 */
public class InteropInterface<T> extends StackItem {

    /**
     * 泛型内部寄存器
     */
    private T _object;

    /**
      * @Author:doubi.liu
      * @description:获取内部寄存器的字节数组形式数据
      * @param
      * @date:2019/2/26
    */
    @Override
    public byte[] getByteArray() {
        TR.enter();
        TR.exit();
        throw new UnsupportedOperationException();
    }

    /**
      * @Author:doubi.liu
      * @description:构造函数
      * @param value
      * @date:2019/2/26
    */
    public InteropInterface(T value) {
        TR.enter();
        this._object = value;
        TR.exit();
    }

    /**
      * @Author:doubi.liu
      * @description:判等方法
      * @param other
      * @date:2019/2/26
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
        if (!(other instanceof InteropInterface)) {
            return TR.exit(false);
        }
        return TR.exit(_object.equals(((InteropInterface) other).getInterface()));
    }

    /**
      * @Author:doubi.liu
      * @description:获取内部寄存器的布尔类型数据
      * @param
      * @date:2019/2/26
    */
    @Override
    public boolean getBoolean() {
        TR.enter();
        return TR.exit(_object != null);
    }

    /**
      * @Author:doubi.liu
      * @description:获取内部寄存器的指定类型格式
      * @param
      * @date:2019/2/26
    */
    //@Override
    public <I> I getInterface() {
        TR.enter();
        return TR.exit((I) _object);
    }

    /**
      * @Author:doubi.liu
      * @description:获取InteropInterface内部的数据
      * @param tinterface
      * @date:2019/2/26
    */
    public static <T> T getInteropInterface(InteropInterface<T> tinterface) {
        TR.enter();
        return TR.exit(tinterface._object);
    }
}
