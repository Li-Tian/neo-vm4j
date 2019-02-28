package neo.vm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: RandomAccessStack
 * @Package neo.vm
 * @Description: 随机栈
 * @date Created in 17:25 2019/2/27
 */
public class RandomAccessStack<T> {
    private List<T> list = new ArrayList<T>();

    /**
     * @Author:doubi.liu
     * @description:获取栈内元素的数量
     * @date:2019/2/27
     */
    public int getCount() {
        return list.size();
    }

    /**
     * @Author:doubi.liu
     * @description:清空栈
     * @date:2019/2/27
     */
    public void clear() {
        list.clear();
    }

    /**
     * @Author:doubi.liu
     * @description:复制栈内元素到另一个栈
     * @param stack 目标栈
     * @date:2019/2/27
     */
    public void copyTo(RandomAccessStack<T> stack) {

        stack.list.addAll(list);
    }

    /**
      * @Author:doubi.liu
      * @description:复制栈内元素到另一个栈
      * @param stack 目标栈 count 复制的起始位置
      * @date:2019/2/28
    */
    public void copyTo(RandomAccessStack<T> stack, int count) {
        if (count == 0) {
            return;
        }
        if (count == -1) {
            stack.list.addAll(list);
        } else {
            stack.list.addAll(list.subList(list.size() - count, list.size()));
        }
    }

    /**
     * @Author:doubi.liu
     * @description:获取栈的迭代器
     * @date:2019/2/27
     */
    public Iterator<T> getEnumerator() {
        return list.iterator();
    }

/*    IEnumerator IEnumerable.GetEnumerator()
    {
        return GetEnumerator();
    }*/

    /**
     * @param index 索引，item 元素
     * @Author:doubi.liu
     * @description:按照指定索引位置插入元素,索引从栈顶开始算
     * @date:2019/2/27
     */
    public void insert(int index, T item) {
        if (index > list.size()) {
            throw new UnsupportedOperationException();
        }
        list.add(list.size() - index, item);
    }

    /**
     * @param index 索引
     * @Author:doubi.liu
     * @description: 获取栈内指定索引位置的元素，但不移除
     * @date:2019/2/28
     */
    public T peek(int index) {
        if (index >= list.size()) {
            throw new UnsupportedOperationException();
        }
        if (index < 0) {
            index += list.size();
        }
        if (index < 0) {
            throw new UnsupportedOperationException();
        }
        index = list.size() - index - 1;
        return list.get(index);
    }

    /**
      * @Author:doubi.liu
      * @description:获取栈顶元素，但不移除
      * @param
      * @date:2019/2/28
    */
    public T peek() {
        int index = 0;
        if (index >= list.size()) throw new UnsupportedOperationException();
        if (index < 0) index += list.size();
        if (index < 0) throw new UnsupportedOperationException();
        index = list.size() - index - 1;
        return list.get(index);
    }

    /**
     * @Author:doubi.liu
     * @description:出栈，弹出栈顶元素
     * @date:2019/2/28
     */
    public T pop() {
        return remove(0);
    }

    /**
      * @Author:doubi.liu
      * @description:压栈，压入一个元素，在栈顶
      * @param item 指定元素
      * @date:2019/2/28
    */
    public void push(T item) {
        list.add(item);
    }

    /**
      * @Author:doubi.liu
      * @description:移除指定索引位置的元素
      * @param index 指定索引位置
      * @date:2019/2/28
    */
    public T remove(int index) {
        if (index >= list.size()) throw new UnsupportedOperationException();
        if (index < 0) index += list.size();
        if (index < 0) throw new UnsupportedOperationException();
        index = list.size() - index - 1;
        T item = list.get(index);
        list.remove(index);
        return item;
    }

    /**
      * @Author:doubi.liu
      * @description:替换指定索引位置的元素
      * @param index 指定索引位置 item 元素
      * @date:2019/2/28
    */
    public void set(int index, T item) {
        if (index >= list.size()) throw new UnsupportedOperationException();
        if (index < 0) index += list.size();
        if (index < 0) throw new UnsupportedOperationException();
        index = list.size() - index - 1;
        list.set(index, item);
    }
}
