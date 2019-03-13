package neo.vm.Types;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import neo.vm.StackItem;

import static org.junit.Assert.*;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: ArrayTest
 * @Package neo.vm.Types
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 14:17 2019/2/26
 */
public class ArrayTest {
    @Test
    public void getArrayItem() throws Exception {
        StackItem[] stackitemArray=new StackItem[10];
        Array ta=new Array(stackitemArray);


        StackItem a=new Boolean(true);
        List<StackItem> list=new ArrayList<>();
        list.add(a);
        Array array=new Array(list);
        Assert.assertEquals(a,array.getArrayItem(0));
        Assert.assertEquals(false,array.IsReadOnly);


    }

    @Test
    public void setArrayItem() throws Exception {
        StackItem a=new Boolean(true);
        StackItem b=new Boolean(false);
        StackItem c=new Boolean(false);
        List<StackItem> list=new ArrayList<>();
        list.add(a);
        list.add(b);
        Array array=new Array(list);
        Assert.assertEquals(c,array.setArrayItem(0,c));
    }

    @Test
    public void getCount() throws Exception {
        StackItem a=new Boolean(true);
        List<StackItem> list=new ArrayList<>();
        list.add(a);
        Array array=new Array(list);
        Assert.assertEquals(1,array.getCount());
    }

    @Test
    public void add() throws Exception {
        Array array=new Array();
        StackItem a=new Boolean(true);
        array.add(a);
        Assert.assertEquals(1,array.getCount());
    }

    @Test
    public void clear() throws Exception {
        Array array=new Array();
        StackItem a=new Boolean(true);
        array.add(a);
        array.clear();
        Assert.assertEquals(0,array.getCount());
    }

    @Test
    public void contains() throws Exception {
        Array array=new Array();
        StackItem a=new Boolean(true);
        array.add(a);
        Assert.assertEquals(true,array.contains(a));
    }

    @Test
    public void copyTo() throws Exception {
        Array array=new Array();
        StackItem a=new Boolean(true);
        array.add(a);
        StackItem[] temp=new StackItem[1];
        array.copyTo(temp,0);
        Assert.assertEquals(a,temp[0]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void equals() throws Exception {
        Array array1=new Array();
        Array array2=new Array();
        array1.equals(array2);
    }

    @Test
    public void referenceEquals() throws Exception{
        Array a=new Array(new StackItem[]{StackItem.getStackItem(true)});
        Array b=new Array(new StackItem[]{StackItem.getStackItem(true)});
        if (!a.referenceEquals(a)){
            throw new Exception("判空方法异常");
        }
    }

    @Test
    public void getBoolean() throws Exception {

        Array array1=new Array();
        Assert.assertEquals(true,array1.getBoolean());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getByteArray() throws Exception {
        Array array1=new Array();
        array1.getByteArray();
    }

    @Test
    public void getEnumerator() throws Exception {
        Array array=new Array();
        StackItem a=new Boolean(true);
        array.add(a);
        for (StackItem stackItem :array.getEnumerator()) {
            Assert.assertEquals(a,stackItem);
        }
    }

    @Test
    public void indexOf() throws Exception {
        Array array=new Array();
        StackItem a=new Boolean(true);
        array.add(a);
        Assert.assertEquals(0,array.indexOf(a));
    }

    @Test
    public void insert() throws Exception {
        Array array=new Array();
        StackItem a=new Boolean(true);
        StackItem b=new Boolean(false);
        array.add(a);
        array.insert(0,b);
        Assert.assertEquals(b,array.getArrayItem(0));
    }

    @Test
    public void remove() throws Exception {
        Array array=new Array();
        StackItem a=new Boolean(true);
        array.add(a);
        array.remove(a);
        Assert.assertEquals(0,array.getCount());
    }

    @Test
    public void removeAt() throws Exception {
        Array array=new Array();
        StackItem a=new Boolean(true);
        StackItem b=new Boolean(true);
        array.add(a);
        array.add(b);
        array.removeAt(0);
        Assert.assertEquals(b,array.getArrayItem(0));

    }

    @Test
    public void reverse() throws Exception {
        Array array=new Array();
        StackItem a=new Boolean(true);
        StackItem b=new Boolean(true);
        array.add(a);
        array.add(b);
        array.reverse();
        Assert.assertEquals(b,array.getArrayItem(0));
    }

}