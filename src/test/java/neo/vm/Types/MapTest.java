package neo.vm.Types;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;

import neo.csharp.Out;
import neo.vm.StackItem;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: MapTest
 * @Package neo.vm.Types
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 14:16 2019/2/26
 */
public class MapTest {

    @Test
    public void getMapItem() throws Exception {

        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.setMapItem(key, value);
        Assert.assertEquals(value, map.getMapItem(key));
    }

    @Test
    public void setMapItem() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.setMapItem(key, value);
        Assert.assertEquals(value, map.setMapItem(key, value));

    }

    @Test
    public void getKeys() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.setMapItem(key, value);
        Assert.assertEquals(1, map.getKeys().size());
    }

    @Test
    public void getValues() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.setMapItem(key, value);
        Assert.assertEquals(1, map.getValues().size());
    }

    @Test
    public void getCount() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.setMapItem(key, value);
        Assert.assertEquals(1, map.getCount());
    }

    @Test
    public void add() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.add(key, value);
        Assert.assertEquals(value, map.setMapItem(key, value));
    }

    @Test
    public void add1() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        AbstractMap.SimpleEntry<StackItem,StackItem> entry=new AbstractMap.SimpleEntry<StackItem,
                StackItem>(key,value);
        map.add(entry);
    }


    @Test
    public void clear() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.add(key, value);
        map.clear();
        Assert.assertEquals(0, map.getCount());
    }

    @Test
    public void contains() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.add(key, value);

        HashMap<StackItem,StackItem> tempmap = new HashMap();
        tempmap.put(key, value);

        for (java.util.Map.Entry<StackItem,StackItem> entry : tempmap.entrySet()) {
            Assert.assertEquals(true, map.contains(entry));
        }

    }

    @Test
    public void containsKey() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.add(key, value);
        Assert.assertEquals(true, map.containsKey(key));
    }

    @Test
    public void copyTo() throws Exception {
        AbstractMap.SimpleEntry<StackItem,StackItem>[] array=new AbstractMap.SimpleEntry[1];
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.add(key, value);
        map.copyTo(array,0);
        java.util.Map.Entry entry=array[0];
        Assert.assertEquals(true,entry.getKey().equals(key)&&entry.getValue().equals(value));

/*        java.util.Map.Entry<StackItem, StackItem>[] array=new java.util.Map.Entry<StackItem,
                StackItem>[1];*/
    }

    @Test
    public void equals() throws Exception {
        java.util.Map temp = new HashMap();
        temp.put(new Integer(new BigInteger("0")), new Integer(new BigInteger("0")));
        Map a = new Map(temp);
        Map b = new Map(temp);
        if (a.equals(b)) {
            throw new Exception("判空方法异常");
        }
    }

    @Test
    public void getBoolean() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.add(key, value);
        Assert.assertEquals(true,map.getBoolean());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getByteArray() throws Exception {
        Map map = new Map();
        map.getByteArray();
    }

    @Test
    public void getEnumerator() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.add(key, value);
        Iterator<java.util.Map.Entry<StackItem, StackItem>> iterator=map.getEnumerator().iterator();
        while (iterator.hasNext()) {
            java.util.Map.Entry<StackItem, StackItem> temp=iterator.next();
            Assert.assertEquals(true,temp.getKey().equals(key)&&temp.getValue().equals(value));
        }
    }

    @Test
    public void remove() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.add(key, value);
        Assert.assertEquals(true,map.remove(key));
    }

    @Test
    public void remove1() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.add(key, value);
        HashMap<StackItem,StackItem> tempmap=new HashMap<>();
        tempmap.put(key,value);
        for (java.util.Map.Entry<StackItem,StackItem> entry:tempmap.entrySet()){
            Assert.assertEquals(true,map.remove(entry));
        }

    }

    @Test
    public void tryGetValue() throws Exception {
        Map map = new Map();
        StackItem key = new Integer(new BigInteger("0"));
        StackItem value = new Integer(new BigInteger("1"));
        map.add(key, value);
        Out<StackItem> out=new Out<>();
        map.tryGetValue(key,out);
        Assert.assertEquals(value,out.get());
    }

}