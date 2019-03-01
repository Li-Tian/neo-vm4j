package neo.vm;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import neo.csharp.io.MemoryStream;

import static org.junit.Assert.*;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: ScriptTest
 * @Package neo.vm
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 16:37 2019/2/28
 */
public class ScriptTest {
    @Test
    public void getScriptHash() throws Exception {
        byte[] script_code=new byte[]{0x00};
        Script script=new Script(new TestCrypto(),script_code);
        Assert.assertEquals(true, Arrays.equals(new byte[0],script.getScriptHash()));
    }

    @Test
    public void getLength() throws Exception {
        byte[] script_code=new byte[]{0x00};
        Script script=new Script(new TestCrypto(),script_code);
        Assert.assertEquals(1, script.getLength());
    }

    @Test
    public void getOpcode() throws Exception {
        byte[] script_code=new byte[]{0x00};
        Script script=new Script(new TestCrypto(),script_code);
        Assert.assertEquals(OpCode.PUSH0, script.getOpcode(0));
    }

    @Test
    public void getBinaryReader() throws Exception {
        byte[] script_code=new byte[]{0x00,0x01};
        Script script=new Script(new TestCrypto(),script_code);
        Assert.assertEquals(0x00, script.getBinaryReader().readByte());
    }

    @Test
    public void getMemoryStream() throws Exception {
        Script scriptt=new Script(new byte[1],new byte[1]);

        byte[] script_code=new byte[]{0x00,0x01};
        Script script=new Script(new TestCrypto(),script_code);
        MemoryStream stream=script.getMemoryStream();
        byte[] testbyte=new byte[2];
        stream.read(testbyte,0,2);
        Assert.assertEquals(0x00, testbyte[0]);
        Assert.assertEquals(0x01, testbyte[1]);
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