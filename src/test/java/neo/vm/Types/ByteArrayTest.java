package neo.vm.Types;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: ByteArrayTest
 * @Package neo.vm.Types
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 15:54 2019/2/25
 */
public class ByteArrayTest {
    @Test
    public void equals() throws Exception {
        ByteArray a=new ByteArray("haha".getBytes());
        ByteArray b=new ByteArray("haha".getBytes());
        if (!a.equals(b)){
            throw new Exception("判空方法异常");
        }
    }

    @Test
    public void getByteArray() throws Exception {
        ByteArray a=new ByteArray("haha".getBytes());
        boolean temp= Arrays.equals("haha".getBytes(),a.getByteArray());
        Assert.assertEquals(true,temp);
    }

}