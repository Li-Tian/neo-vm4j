package neo.vm.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

import neo.log.notr.TR;
import neo.vm.ICollection;
import neo.vm.StackItem;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: Array
 * @Package neo.vm.Types
 * @Description: Array是StackItem的子类
 * @date Created in 14:15 2019/2/25
 */
public class Array extends StackItem implements ICollection<StackItem> {

    /**
     * 内部寄存器，存储StackItem对象集合
     */
    protected final List<StackItem> _array = new ArrayList<>();

    /**
     * @param index 索引
     * @Author:doubi.liu
     * @description:按照索引获取Array内部元素
     * @date:2019/2/26
     */
    public StackItem getArrayItem(int index) {
        TR.enter();
        return TR.exit(_array.get(index));
    }

    /**
     * @param index 索引 item 元素
     * @Author:doubi.liu
     * @description:按照索引更新Array内部元素
     * @date:2019/2/26
     */
    public StackItem setArrayItem(int index, StackItem item) {
        TR.enter();
        return TR.exit(_array.set(index, item));
    }

    /**
     * @Author:doubi.liu
     * @description:Array对象内部元素的个数
     * @date:2019/2/26
     */
    @Override
    public int getCount() {
        TR.enter();
        return TR.exit(_array.size());
    }

    /**
     * 只读标志位
     */
    public boolean IsReadOnly = false;

/*    boolean ICollection.IsSynchronized =>false;
    object ICollection.SyncRoot =>_array;*/

    /**
     * @Author:doubi.liu
     * @description:默认构造函数
     * @date:2019/2/26
     */
    public Array() {
        this(new ArrayList<StackItem>());
    }

    /**
     * @param value StackItem数组
     * @Author:doubi.liu
     * @description:构造函数
     * @date:2019/2/26
     */
    public Array(StackItem[] value) {
        this(Arrays.asList(value));
    }

    /**
     * @param value 可枚举集合
     * @Author:doubi.liu
     * @description:构造函数
     * @date:2019/2/26
     */
    public Array(Iterable<StackItem> value) {
        TR.enter();
        //LINQ START
        //this._array = value as List<StackItem> ??value.ToList();
        this._array.clear();
        Iterator<StackItem> iterator = value.iterator();
        while (iterator.hasNext()) {
            this._array.add(iterator.next());
        }
        //LINQ END
        TR.exit();
    }

    /**
     * @param item StackItem元素
     * @Author:doubi.liu
     * @description:向Array中添加新元素
     * @date:2019/2/26
     */
    public void add(StackItem item) {
        TR.enter();
        _array.add(item);
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description:清空Array内部寄存器
     * @date:2019/2/26
     */
    public void clear() {
        TR.enter();
        _array.clear();
        TR.exit();
    }

    /**
     * @param item 查找元素
     * @Author:doubi.liu
     * @description:判断Array内是否存在某元素
     * @date:2019/2/26
     */
    public boolean contains(StackItem item) {
        TR.enter();
        return TR.exit(_array.contains(item));
    }

    /**
     * @param array 拷贝到的目标对象 arrayIndex 目标对象的起始位置
     * @Author:doubi.liu
     * @description:拷贝方法
     * @date:2019/2/26
     */
    public void copyTo(StackItem[] array, int arrayIndex) {
        TR.enter();
        for (StackItem item : _array) {
            array[arrayIndex++] = item;
        }
        TR.exit();
    }

/*    public void CopyTo(System.Array array, int index) {
        foreach(StackItem item:_array){
            array.SetValue(item, index++);
        }

    }*/

    /**
     * @Author:doubi.liu
     * @description:判等方法
     * @date:2019/2/26
     */
    @Override
    public boolean equals(StackItem other) {
        TR.enter();
        TR.exit();
        throw new UnsupportedOperationException();
        //return this == other;
    }

    /**
     * @Author:doubi.liu
     * @description:判等方法（引用相同）
     * @date:2019/2/26
     */
    public boolean referenceEquals(StackItem other) {
        TR.enter();
        return TR.exit(this == other);
    }

    /**
     * @Author:doubi.liu
     * @description:获取布尔值
     * @date:2019/2/26
     */
    @Override
    public boolean getBoolean() {
        TR.enter();
        return TR.exit(true);
    }

    /**
     * @Author:doubi.liu
     * @description:获取字节数组
     * @date:2019/2/26
     */
    @Override
    public byte[] getByteArray() {
        TR.enter();
        TR.exit();
        throw new UnsupportedOperationException();
    }

/*    public Iterable GetEnumerator() {
        return GetEnumerator();
    }*/

    /**
     * @Author:doubi.liu
     * @description:获取迭代器
     * @date:2019/2/26
     */
    public Iterable<StackItem> getEnumerator() {
        TR.enter();
        return TR.exit(_array);
    }

    /**
     * @Author:doubi.liu
     * @description:获取元素索引位置
     * @date:2019/2/26
     */
    public int indexOf(StackItem item) {
        TR.enter();
        return TR.exit(_array.indexOf(item));
    }

    /**
     * @param index 指定索引位置 item 元素
     * @Author:doubi.liu
     * @description:在指定索引位置插入元素
     * @date:2019/2/26
     */
    public void insert(int index, StackItem item) {
        TR.enter();
        _array.add(index, item);
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description:移除指定元素
     * @date:2019/2/26
     */
    public boolean remove(StackItem item) {
        TR.enter();
        return TR.exit(_array.remove(item));
    }

    /**
     * @Author:doubi.liu
     * @description:删除指定索引位置的元素
     * @date:2019/2/26
     */
    public void removeAt(int index) {
        TR.enter();
        _array.remove(index);
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description:Array内元素的排列顺序做逆序
     * @date:2019/2/26
     */
    public void reverse() {
        TR.enter();
        Collections.reverse(_array);
        TR.exit();
    }

    @Override
    public Iterator iterator() {
        return _array.iterator();
    }
}
