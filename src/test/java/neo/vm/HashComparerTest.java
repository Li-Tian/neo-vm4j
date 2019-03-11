package neo.vm;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: HashComparerTest
 * @Package neo.vm
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 15:08 2019/2/28
 */
public class HashComparerTest {
    @Test
    public void equals() throws Exception {
        HashComparer comparer=new HashComparer();
        Assert.assertEquals(true,comparer.equals(new byte[]{0x01},new byte[]{0x01}));
        Assert.assertEquals(false,comparer.equals(new byte[]{0x01},new byte[]{0x02}));

    }

    @Test
    public void getHashCode() throws Exception {
        HashComparer comparer=new HashComparer();
        Assert.assertEquals(50462976,comparer.hashCode(new byte[]{0x00,0x01,0x02,0x03}));
    }

}