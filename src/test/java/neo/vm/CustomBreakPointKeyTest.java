package neo.vm;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: CustomBreakPointKeyTest
 * @Package neo.vm
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 14:53 2019/3/11
 */
public class CustomBreakPointKeyTest {
    @Test
    public void getBreakPointHash() throws Exception {
        CustomBreakPointKey pointKey=new CustomBreakPointKey(new byte[]{0x00});
        Assert.assertEquals(true, Arrays.equals(new byte[]{0x00},pointKey.getBreakPointHash()));
    }

    @Test
    public void setBreakPointHash() throws Exception {
        CustomBreakPointKey pointKey=new CustomBreakPointKey(new byte[]{0x00});
        pointKey.setBreakPointHash(new byte[]{0x01});

    }

    @Test
    public void equals() throws Exception {
        CustomBreakPointKey pointKey=new CustomBreakPointKey(new byte[]{0x00});
        CustomBreakPointKey pointKey2=new CustomBreakPointKey(new byte[]{0x00});
        Assert.assertEquals(true,pointKey.equals(pointKey2));
    }

    @Test
    public void hashCode1() throws Exception {
        CustomBreakPointKey pointKey=new CustomBreakPointKey(new byte[]{0x00,0x00,0x00,0x00});
        Assert.assertEquals(0,pointKey.hashCode());
    }

}