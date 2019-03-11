package neo.vm;

import neo.csharp.io.BinaryReader;
import neo.csharp.io.MemoryStream;
import neo.log.notr.TR;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: ExecutionContext
 * @Package neo.vm
 * @Description: 虚拟机执行上下文
 * @date Created in 17:23 2019/2/27
 */
public class ExecutionContext {

    //返回的items数量
    int RVCount;

    //脚本的BinaryReader
    BinaryReader opReader;

    //脚本的BinaryReader的流
    MemoryStream opReaderStream;

    //Script
    public Script script;


    //计算栈 Evaluation stack
    public RandomAccessStack<StackItem> evaluationStack = new RandomAccessStack<StackItem>();

    /// 临时栈 Alternative stack
    public RandomAccessStack<StackItem> altStack = new RandomAccessStack<StackItem>();


    /**
     * @Author:doubi.liu
     * @description:获取计算栈
     * @date:2019/2/28
     */
    public RandomAccessStack<StackItem> getEvaluationStack() {
        TR.enter();
        return TR.exit(evaluationStack);
    }

    /**
     * @Author:doubi.liu
     * @description:获取临时栈
     * @date:2019/2/28
     */
    public RandomAccessStack<StackItem> getAltStack() {
        TR.enter();
        return TR.exit(altStack);
    }

    /**
      * @Author:doubi.liu
      * @description:获取指令指针
      * @param
      * @date:2019/2/28
    */
    public int getInstructionPointer() {
        TR.enter();
        return TR.exit(opReaderStream.getPosition());
    }

    /**
      * @Author:doubi.liu
      * @description:设置指令指针
      * @param value
      * @date:2019/2/28
    */
    public void setInstructionPointer(int value) {
        TR.enter();
        opReaderStream.seek(value);
        TR.exit();
    }

    /**
      * @Author:doubi.liu
      * @description:获取下一指令
      * @param
      * @date:2019/2/28
    */
    public OpCode getNextInstruction() {
        TR.enter();
        int position = (int) opReaderStream.getPosition();

        OpCode result= (position >= script.getLength()) ? OpCode.RET : script.getOpcode(position);
        return TR.exit(result);
    }

    /**
     * @Author:doubi.liu
     * @description:获取脚本哈希
     * @date:2019/2/28
     */
    public byte[] getScriptHash() {
        TR.enter();
        return TR.exit(script.getScriptHash());
    }

    /**
     * @param script 脚本 rvcount 返回item数量
     * @Author:doubi.liu
     * @description:构造器
     * @date:2019/2/28
     */
    ExecutionContext(Script script, int rvcount) {
        TR.enter();
        this.RVCount = rvcount;
        this.script = script;
        this.opReaderStream=script.getMemoryStream();
        this.opReader = new BinaryReader(this.opReaderStream);
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description:释放资源
     * @date:2019/2/28
     */
    public void dispose() {
        TR.enter();
        opReader.close();
        TR.exit();
    }

}
