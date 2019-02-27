package neo.vm;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import neo.csharp.Uint;
import neo.csharp.Ulong;
import neo.log.tr.TR;
import neo.vm.Types.Array;
import neo.vm.Types.Boolean;
import neo.vm.Types.ByteArray;
import neo.vm.Types.Integer;
import neo.vm.Types.InteropInterface;

import static org.junit.Assert.*;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: StackItemTest
 * @Package neo.vm
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 13:49 2019/2/27
 */
public class StackItemTest {
    @Test
    public void equals() throws Exception {
        StackItem a=StackItem.getStackItem(true);
        StackItem b=StackItem.getStackItem(true);
        if (!a.equals(b)){
            throw new Exception("判空方法异常");
        }
    }

    @Test
    public void fromInterface() throws Exception {
        Integer item=new Integer(new BigInteger("0"));
        InteropInterface interopInterface=new InteropInterface(item);


        StackItem item1=StackItem.fromInterface(item);


        //TR.debug(interopInterface.getInterface());
        Assert.assertEquals(interopInterface, item1);
    }


    @Test
    public void getHashCode() throws Exception {
        StackItem item=new Integer(new BigInteger("0"));
        Assert.assertEquals(527, item.getHashCode());
    }

    @Test
    public void getString() throws Exception {
        ByteArray array=new ByteArray("hello".getBytes());
        Assert.assertEquals("hello",array.getString());
    }

    @Test
    public void getStackItem() throws Exception {
        Assert.assertEquals(new BigInteger("0"),StackItem.getStackItem((int)0));
    }

    @Test
    public void getStackItem1() throws Exception {
        Assert.assertEquals(new BigInteger("0"),StackItem.getStackItem(Uint.ZERO));

    }

    @Test
    public void getStackItem2() throws Exception {
        Assert.assertEquals(new BigInteger("0"),StackItem.getStackItem((long)0));

    }

    @Test
    public void getStackItem3() throws Exception {
        Assert.assertEquals(new BigInteger("0"),StackItem.getStackItem(Ulong.ZERO));

    }

    @Test
    public void getStackItem4() throws Exception {
        Assert.assertEquals(new Integer(new BigInteger("0")),StackItem.getStackItem(new
                BigInteger("0")));

    }

    @Test
    public void getStackItem5() throws Exception {
        Assert.assertEquals(new Boolean(true),StackItem.getStackItem(true));

    }

    @Test
    public void getStackItem6() throws Exception {
        Assert.assertEquals(new ByteArray("0".getBytes()),StackItem.getStackItem("0".getBytes()));

    }

    @Test
    public void getStackItem7() throws Exception {
        Assert.assertEquals(new ByteArray("0".getBytes()),StackItem.getStackItem("0"));

    }

    @Test
    public void getStackItem8() throws Exception {
        StackItem[] array=new StackItem[2];
        Assert.assertEquals(2,StackItem.getStackItem(array).getCount());

    }

    @Test
    public void getStackItem9() throws Exception {
        List<StackItem> array=new ArrayList<>();
        Assert.assertEquals(0,StackItem.getStackItem(array).getCount());
    }

}