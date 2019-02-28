package neo.vm;

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
    //中断
    HALT(1 << 0),
    //故障
    FAULT(1 << 1),
    //断点
    BREAK(1 << 2);

    private byte state;

    VMState(int state) {
        this.state = (byte) state;
    }

    public byte getState() {
        return state;
    }
}
