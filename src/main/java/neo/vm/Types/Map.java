package neo.vm.Types;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import neo.csharp.Out;
import neo.log.notr.TR;
import neo.vm.ICollection;
import neo.vm.StackItem;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: Map
 * @Package neo.vm.Types
 * @Description: Map是StackItem 的子类
 * @date Created in 15:56 2019/2/25
 */
public class Map extends StackItem implements ICollection<java.util.Map.Entry<StackItem, StackItem>>{

    /**
     * Map的内部寄存器
     */
    private java.util.Map<StackItem, StackItem> dictionary = new HashMap<>();

    /**
      * @Author:doubi.liu
      * @description:获取指定key相应元素
      * @param key
      * @date:2019/2/26
    */
    public StackItem getMapItem(StackItem key) {
        TR.enter();
        return TR.exit(dictionary.get(key));
    }

    /**
      * @Author:doubi.liu
      * @description:设定指定key的元素
      * @param key 指定key value 元素
      * @date:2019/2/26
    */
    public StackItem setMapItem(StackItem key, StackItem value) {
        TR.enter();
        dictionary.put(key, value);
        return TR.exit(dictionary.get(key));
    }


    /**
      * @Author:doubi.liu
      * @description:获取Map的键集合
      * @param
      * @date:2019/2/26
    */
    public Collection<StackItem> getKeys() {
        TR.enter();
        return TR.exit(dictionary.keySet());
    }

    /**
      * @Author:doubi.liu
      * @description:获取Map的值集合
      * @param
      * @date:2019/2/26
    */
    public Collection<StackItem> getValues() {
        TR.enter();
        return TR.exit(dictionary.values());
    }

    /**
      * @Author:doubi.liu
      * @description:获取Map的内部元素的个数
      * @param
      * @date:2019/2/26
    */
    @Override
    public int getCount() {
        TR.enter();
        return TR.exit(dictionary.size());
    }

    /**
     * 只读标志位
     */
    public boolean isReadOnly = false;


/*    bool ICollection.IsSynchronized => false;
    object ICollection.SyncRoot => dictionary;

    public boolean isSynchronized = false;

    public Object getSyncRoot() {
        synchronized (this) {
            return dictionary;
        }
    }*/

    /**
      * @Author:doubi.liu
      * @description:默认构造函数
      * @param
      * @date:2019/2/26
    */
    public Map() {
        this(new HashMap<StackItem, StackItem>());
    }

    /**
      * @Author:doubi.liu
      * @description:构造函数
      * @param value Map形式元素集合
      * @date:2019/2/26
    */
    public Map(java.util.Map<StackItem, StackItem> value) {
        TR.enter();
        this.dictionary = value;
        TR.exit();
    }

    /**
      * @Author:doubi.liu
      * @description:添加键和值
      * @param key 键 value 值
      * @date:2019/2/26
    */
    public void add(StackItem key, StackItem value) {
        TR.enter();
        dictionary.put(key, value);
        TR.exit();
    }

    /**
      * @Author:doubi.liu
      * @description:添加键值对
      * @param item 键值对
      * @date:2019/2/26
    */
    public void add(java.util.Map.Entry<StackItem, StackItem> item) {
        TR.enter();
        dictionary.put(item.getKey(), item.getValue());
        TR.exit();
    }

    /**
      * @Author:doubi.liu
      * @description:清空内部寄存器的元素
      * @param
      * @date:2019/2/26
    */
    public void clear() {
        TR.enter();
        dictionary.clear();
        TR.exit();
    }

    /**
      * @Author:doubi.liu
      * @description:判断是否存在指定键值对
      * @param item
      * @date:2019/2/26
    */
    public boolean contains(java.util.Map.Entry<StackItem, StackItem> item) {
        TR.enter();
        //// TODO: 2019/2/27
        //dictionary.containsKey(item.getKey()
        boolean flag=true;
        if(!dictionary.containsKey(item.getKey())){
            flag=false;
        }
        if(!dictionary.get(item.getKey()).equals(item.getValue())){
            flag=false;
        }
        TR.fixMe("与c#的实现不同，因为C#可能存在bug,未判断value是否存在");
        return TR.exit(flag);
    }

    /**
      * @Author:doubi.liu
      * @description:判断是否存在指定键
      * @param key 指定键
      * @date:2019/2/26
    */
    public boolean containsKey(StackItem key) {
        TR.enter();
        return TR.exit(dictionary.containsKey(key));
    }

    /**
      * @Author:doubi.liu
      * @description:拷贝方法
      * @param array 拷贝到的目标数组 arrayIndex 数组中拷贝的起始位置
      * @date:2019/2/26
    */
    public void copyTo(java.util.Map.Entry<StackItem, StackItem>[] array, int arrayIndex) {
        TR.enter();
        for (java.util.Map.Entry<StackItem, StackItem> item : dictionary.entrySet()) {
            array[arrayIndex++] = new AbstractMap.SimpleEntry<StackItem, StackItem>(item);
        }
        TR.exit();

    }

/*    public void CopyTo(System.Array array, int index) {
        foreach(java.util.Map.Entry < StackItem, StackItem > item:dictionary.entrySet())
        array.SetValue(item, index++);
    }*/

    /**
      * @Author:doubi.liu
      * @description:判等方法
      * @param other
      * @date:2019/2/26
    */
    @Override
    public boolean equals(StackItem other) {
        TR.enter();
        return TR.exit(this == other);
    }

    /**
      * @Author:doubi.liu
      * @description:获取内部寄存器的布尔类型数据
      * @param
      * @date:2019/2/26
    */
    @Override
    public boolean getBoolean() {
        TR.enter();
        return TR.exit(true);
    }

    /**
      * @Author:doubi.liu
      * @description:获取内部寄存器的字节数组形式数据
      * @param
      * @date:2019/2/26
    */
    @Override
    public byte[] getByteArray() {
        TR.enter();
        TR.exit();
        throw new UnsupportedOperationException();
    }

    /**
      * @Author:doubi.liu
      * @description:获取迭代器
      * @param
      * @date:2019/2/26
    */
    public Iterable<java.util.Map.Entry<StackItem, StackItem>> getEnumerator() {
        TR.enter();
        Set<java.util.Map.Entry<StackItem, StackItem>> entries = dictionary.entrySet();
        return TR.exit(entries);
    }

/*    public Iterable GetEnumerator() {
        return dictionary.GetEnumerator();
    }*/

    /**
      * @Author:doubi.liu
      * @description:移除指定key对应的元素
      * @param key 指定key
      * @date:2019/2/26
    */
    public boolean remove(StackItem key) {
        TR.enter();
        return TR.exit(dictionary.remove(key)!=null);
    }

    /**
      * @Author:doubi.liu
      * @description:移除指定键值对对应的元素
      * @param item 指定键值对
      * @date:2019/2/26
    */
    public boolean remove(java.util.Map.Entry<StackItem, StackItem> item) {
        TR.enter();
        return TR.exit(dictionary.remove(item.getKey())!=null);
    }

    /**
      * @Author:doubi.liu
      * @description:尝试获取指定键对应的值，存在则输出该对象，并返回true,否则返回false
      * @param key 指定键 value 输出
      * @date:2019/2/26
    */
    public boolean tryGetValue(StackItem key, Out<StackItem> value) {
        TR.enter();
        if (dictionary.containsKey(key)){
            value.set(dictionary.get(key));
            return TR.exit(true);
        }else{
            return TR.exit(false);
        }
    }

    @Override
    public Iterator<java.util.Map.Entry<StackItem, StackItem>> iterator() {
        return getEnumerator().iterator();
    }
}
