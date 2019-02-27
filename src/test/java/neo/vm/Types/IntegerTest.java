package neo.vm.Types;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: IntegerTest
 * @Package neo.vm.Types
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 15:06 2019/2/25
 */
public class IntegerTest {
    @Test
    public void equals() throws Exception {

        Integer a=new Integer(new BigInteger("0"));
        Integer b=new Integer(new BigInteger("0"));
        if (!a.equals(b)){
            throw new Exception("判空方法异常");
        }
    }

    @Test
    public void getBigInteger() throws Exception {
        Integer a=new Integer(new BigInteger("0"));
        BigInteger temper=new BigInteger("0");
        if (!temper.equals(a.getBigInteger())){
            throw new Exception("getBigInteger方法异常");
        }
    }

    @Test
    public void getBoolean() throws Exception {
        Integer a=new Integer(new BigInteger("0"));
        assertEquals(false,a.getBoolean());
    }

    @Test
    public void getByteArray() throws Exception {
        Integer a=new Integer(new BigInteger("0"));
        boolean temp= Arrays.equals((new BigInteger("0")).toByteArray(),a.getByteArray());
        assertEquals(true,temp);
    }

}