package neo.vm.Types;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: BooleanTest
 * @Package neo.vm.Types
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 15:44 2019/2/25
 */
public class BooleanTest {
    @Test
    public void equals() throws Exception {
        Boolean a=new Boolean(true);
        Boolean b=new Boolean(true);
        if (!a.equals(b)){
            throw new Exception("判空方法异常");
        }
    }

    @Test
    public void getBigInteger() throws Exception {
        Boolean a=new Boolean(true);
        boolean temp=a.getBigInteger().equals(new BigInteger("1"));
        Assert.assertEquals(true,temp);
    }

    @Test
    public void getBoolean() throws Exception {
        Boolean a=new Boolean(true);
        Assert.assertEquals(true,a.getBoolean());
    }

    @Test
    public void getByteArray() throws Exception {
        Boolean a=new Boolean(true);
        boolean temp= Arrays.equals(new byte[]{1},a.getByteArray());
        Assert.assertEquals(true,temp);
    }

}