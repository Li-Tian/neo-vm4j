package neo.vm;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: ExecutionContextTest
 * @Package neo.vm
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 10:10 2019/3/1
 */
public class ExecutionContextTest {
    @Test
    public void getEvaluationStack() throws Exception {
        Script script=new Script(new byte[1],new byte[1]);
        ExecutionContext context=new ExecutionContext(script,0);
        Assert.assertEquals(0,context.getEvaluationStack().getCount());

    }

    @Test
    public void getAltStack() throws Exception {
        Script script=new Script(new byte[1],new byte[1]);
        ExecutionContext context=new ExecutionContext(script,0);
        Assert.assertEquals(0,context.getAltStack().getCount());
    }

    @Test
    public void getInstructionPointer() throws Exception {
        Script script=new Script(new byte[1],new byte[1]);
        ExecutionContext context=new ExecutionContext(script,1);
        Assert.assertEquals(0,context.getInstructionPointer());
    }

    @Test
    public void setInstructionPointer() throws Exception {
        Script script=new Script(new byte[1],new byte[1]);
        ExecutionContext context=new ExecutionContext(script,1);
        context.setInstructionPointer(1);
        Assert.assertEquals(1,context.getInstructionPointer());
    }

    @Test
    public void getNextInstruction() throws Exception {
        Script script=new Script(new byte[1],new byte[1]);
        ExecutionContext context=new ExecutionContext(script,1);
        Assert.assertEquals(OpCode.PUSH0,context.getNextInstruction());
    }

    @Test
    public void getScriptHash() throws Exception {
        byte[] script_code=new byte[]{0x00};
        Script script=new Script(new TestCrypto(),script_code);
        ExecutionContext context=new ExecutionContext(script,1);
        Assert.assertEquals(true, Arrays.equals(new byte[0],context.getScriptHash()));
    }

    @Test
    public void dispose() throws Exception {
        byte[] script_code=new byte[]{0x00};
        Script script=new Script(new TestCrypto(),script_code);
        ExecutionContext context=new ExecutionContext(script,1);
        context.dispose();
    }

    class TestCrypto implements ICrypto{

        @Override
        public byte[] hash160(byte[] message) {
            return new byte[0];
        }

        @Override
        public byte[] hash256(byte[] message) {
            return new byte[0];
        }

        @Override
        public boolean verifySignature(byte[] message, byte[] signature, byte[] pubkey) {
            return false;
        }
    }

}