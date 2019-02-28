package neo.vm;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Iterator;

import neo.vm.Types.Integer;

import static org.junit.Assert.*;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: RandomAccessStackTest
 * @Package neo.vm
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 10:31 2019/2/28
 */
public class RandomAccessStackTest {
    @Test
    public void getCount() throws Exception {
        RandomAccessStack<StackItem> testStack=new RandomAccessStack();
        Assert.assertEquals(0,testStack.getCount());
    }

    @Test
    public void clear() throws Exception {
        RandomAccessStack<StackItem> testStack=new RandomAccessStack();
        Integer item=new Integer(new BigInteger("0"));
        testStack.push(item);
        Assert.assertEquals(1,testStack.getCount());
        testStack.clear();
        Assert.assertEquals(0,testStack.getCount());
    }

    @Test
    public void copyTo() throws Exception {
        RandomAccessStack<StackItem> testStack=new RandomAccessStack();
        Integer item=new Integer(new BigInteger("0"));
        testStack.push(item);

        RandomAccessStack<StackItem> testStack2=new RandomAccessStack();
        testStack.copyTo(testStack2);
        Assert.assertEquals(1,testStack2.getCount());

    }

    @Test
    public void copyTo1() throws Exception {
        RandomAccessStack<StackItem> testStack=new RandomAccessStack();
        Integer item1=new Integer(new BigInteger("1"));
        Integer item2=new Integer(new BigInteger("2"));
        testStack.push(item1);
        testStack.push(item2);

        RandomAccessStack<StackItem> testStack2=new RandomAccessStack();
        testStack.copyTo(testStack2,1);
        Assert.assertEquals(1,testStack2.getCount());
        Assert.assertEquals(2,testStack2.pop().getBigInteger().intValue());
    }

    @Test
    public void getEnumerator() throws Exception {
        RandomAccessStack<StackItem> testStack=new RandomAccessStack();
        Integer item1=new Integer(new BigInteger("1"));
        Integer item2=new Integer(new BigInteger("2"));
        testStack.push(item1);
        testStack.push(item2);
        Iterator<StackItem> iterator=testStack.getEnumerator();
        int i=1;
        while (iterator.hasNext()){
            StackItem item=iterator.next();
            Assert.assertEquals(i++,item.getBigInteger().intValue());
        }
    }

    @Test
    public void insert() throws Exception {
        RandomAccessStack<StackItem> testStack=new RandomAccessStack();
        Integer item1=new Integer(new BigInteger("1"));
        Integer item2=new Integer(new BigInteger("2"));
        Integer item3=new Integer(new BigInteger("3"));
        testStack.push(item1);
        testStack.push(item2);

        testStack.insert(0,item3);
        Assert.assertEquals(3,testStack.pop().getBigInteger().intValue());
    }

    @Test
    public void peek() throws Exception {
        RandomAccessStack<StackItem> testStack=new RandomAccessStack();
        Integer item1=new Integer(new BigInteger("1"));
        Integer item2=new Integer(new BigInteger("2"));
        testStack.push(item1);
        testStack.push(item2);
        Assert.assertEquals(1,testStack.peek(1).getBigInteger().intValue());
    }

    @Test
    public void peek1() throws Exception {
        RandomAccessStack<StackItem> testStack=new RandomAccessStack();
        Integer item1=new Integer(new BigInteger("1"));
        Integer item2=new Integer(new BigInteger("2"));
        testStack.push(item1);
        testStack.push(item2);
        Assert.assertEquals(2,testStack.peek().getBigInteger().intValue());
    }

    @Test
    public void pop() throws Exception {
        RandomAccessStack<StackItem> testStack=new RandomAccessStack();
        Integer item1=new Integer(new BigInteger("1"));
        Integer item2=new Integer(new BigInteger("2"));
        testStack.push(item1);
        testStack.push(item2);
        Assert.assertEquals(2,testStack.pop().getBigInteger().intValue());
    }

    @Test
    public void push() throws Exception {
        RandomAccessStack<StackItem> testStack=new RandomAccessStack();
        Integer item1=new Integer(new BigInteger("1"));
        Integer item2=new Integer(new BigInteger("2"));
        testStack.push(item1);
        testStack.push(item2);
        Assert.assertEquals(2,testStack.getCount());
    }

    @Test
    public void remove() throws Exception {
        RandomAccessStack<StackItem> testStack=new RandomAccessStack();
        Integer item1=new Integer(new BigInteger("1"));
        Integer item2=new Integer(new BigInteger("2"));
        testStack.push(item1);
        testStack.push(item2);
        testStack.remove(0);
        Assert.assertEquals(1,testStack.pop().getBigInteger().intValue());
    }

    @Test
    public void set() throws Exception {
        RandomAccessStack<StackItem> testStack=new RandomAccessStack();
        Integer item1=new Integer(new BigInteger("1"));
        Integer item2=new Integer(new BigInteger("2"));
        testStack.push(item1);
        testStack.set(0,item2);
        Assert.assertEquals(2,testStack.pop().getBigInteger().intValue());

    }

}