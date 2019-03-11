package neo.vm;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

import neo.vm.Types.Array;

import static org.junit.Assert.*;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: ScriptBuilderTest
 * @Package neo.vm
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 11:17 2019/3/1
 */
public class ScriptBuilderTest {
    @Test(expected = UnsupportedOperationException.class)
    public void getOffset() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.getOffset();
    }

    @Test
    public void dispose() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.dispose();
    }

    @Test
    public void emit() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.emit(OpCode.PUSH0);
        byte[] result=new byte[]{0x00};
        Assert.assertEquals(true, Arrays.equals(result, scriptBuilder.toArray()));
    }

    @Test
    public void emit1() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.emit(OpCode.PUSH0,new byte[]{0x01,0x02});
        byte[] result=new byte[]{0x00,0x01,0x02};
        Assert.assertEquals(true, Arrays.equals(result, scriptBuilder.toArray()));
    }

    @Test
    public void emitAppCall() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.emitAppCall(new byte[]{0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02,
                0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02});
        byte[] result=new byte[]{0x67,0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02,
                0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02};
        Assert.assertEquals(true, Arrays.equals(result, scriptBuilder.toArray()));
    }

    @Test
    public void emitAppCall1() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.emitAppCall(new byte[]{0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02,
                0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02},true);
        byte[] result=new byte[]{0x69,0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02,
                0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02,0x01,0x02};
        Assert.assertEquals(true, Arrays.equals(result, scriptBuilder.toArray()));
    }

    @Test
    public void emitJump() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.emitJump(OpCode.JMP,(short) 1);
        byte[] result=new byte[]{0x62,0x01,0x00};
        Assert.assertEquals(true, Arrays.equals(result, scriptBuilder.toArray()));
    }

    @Test
    public void emitPush() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.emitPush(new BigInteger("17"));
        byte[] result=new byte[]{0x01,0x11};
        Assert.assertEquals(true, Arrays.equals(result, scriptBuilder.toArray()));
    }

    @Test
    public void emitPush1() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.emitPush(true);
        byte[] result=new byte[]{0x51};
        Assert.assertEquals(true, Arrays.equals(result, scriptBuilder.toArray()));
    }

    @Test
    public void emitPush2() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.emitPush(new byte[]{0x17});
        byte[] result=new byte[]{0x01,0x17};
        Assert.assertEquals(true, Arrays.equals(result, scriptBuilder.toArray()));
    }

    @Test
    public void emitPush3() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.emitPush("h");
        byte[] result=new byte[]{0x01,0x68};
        Assert.assertEquals(true, Arrays.equals(result, scriptBuilder.toArray()));
    }

    @Test
    public void emitSysCall() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.emitSysCall("h");
        byte[] result=new byte[]{0x68,0x01,0x68};
        Assert.assertEquals(true, Arrays.equals(result, scriptBuilder.toArray()));
    }

    @Test
    public void toArray() throws Exception {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.emitSysCall("h");
        byte[] result=new byte[]{0x68,0x01,0x68};
        Assert.assertEquals(true, Arrays.equals(result, scriptBuilder.toArray()));
    }

}