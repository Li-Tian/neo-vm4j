package neo.vm;

import java.util.HashMap;
import java.util.Map;

import neo.log.notr.TR;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: VMState
 * @Package neo.vm
 * @Description: 虚拟机状态枚举类
 * @date Created in 17:14 2019/2/27
 */
public enum VMState {
    //正常
    NONE(0),
    //停止（正常结束）
    HALT(1 << 0),
    //故障
    FAULT(1 << 1),
    //断点
    BREAK(1 << 2);

    //字节数据和VM状态的映射关系
    private static final Map<Byte, VMState> byteToTypeMap = new HashMap<Byte, VMState>();

    static {
        TR.info("VMState枚举器初始化");
        for (VMState type : VMState.values()) {
            byteToTypeMap.put(type.getState(), type);
        }
    }
    private byte state;

    /**
      * @Author:doubi.liu
      * @description:构造器
      * @param state 字节数据
      * @date:2019/3/11
    */
    VMState(int state) {
        TR.enter();
        this.state = (byte) state;
        TR.exit();
    }

    /**
      * @Author:doubi.liu
      * @description:获取VM状态的字节数据
      * @param
      * @date:2019/3/11
    */
    public byte getState() {
        TR.enter();
        return TR.exit(state);
    }

    /**
      * @Author:doubi.liu
      * @description:通过字节数据获取VM状态
      * @param i 字节数据
      * @date:2019/3/11
    */
    public static VMState fromByte(byte i) {
        TR.enter();
        VMState type = byteToTypeMap.get(i);
        if (type == null){
            throw TR.exit(new UnsupportedOperationException());
        }
        return TR.exit(type);
    }

    /**
      * @Author:doubi.liu
      * @description:判断是否包含某一种VM状态
      * @param other 指定VM状态
      * @date:2019/3/11
    */
    public boolean hasFlag(VMState other) {
        TR.enter();
        boolean contains=((this.getState() & other.getState())!=0);
        return TR.exit(contains);
    }
}
