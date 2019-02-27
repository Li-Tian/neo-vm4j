package neo.vm.Types;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: InteropInterfaceTest
 * @Package neo.vm.Types
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 14:17 2019/2/26
 */
public class InteropInterfaceTest {
    @Test(expected = UnsupportedOperationException.class)
    public void getByteArray() throws Exception {
        InteropInterface a=new InteropInterface(new Integer(new BigInteger("0")));
        a.getByteArray();
    }

    @Test
    public void equals() throws Exception {
        InteropInterface a=new InteropInterface(new Integer(new BigInteger("0")));
        InteropInterface b=new InteropInterface(new Integer(new BigInteger("0")));
        if (!a.equals(b)){
            throw new Exception("判空方法异常");
        }
    }

    @Test
    public void getBoolean() throws Exception {
        InteropInterface a=new InteropInterface(new Integer(new BigInteger("0")));
        Assert.assertEquals(true,a.getBoolean());
    }

    @Test
    public void getInterface() throws Exception {
        InteropInterface a=new InteropInterface(new Integer(new BigInteger("0")));
        Assert.assertEquals(true,a.getInterface().equals(new Integer(new BigInteger("0"))));
    }

    @Test
    public void getInteropInterface() throws Exception {
        InteropInterface a=new InteropInterface(new Integer(new BigInteger("0")));
        Assert.assertEquals(true,InteropInterface.getInteropInterface(a).equals(new Integer(new
                BigInteger("0"))));

    }

}