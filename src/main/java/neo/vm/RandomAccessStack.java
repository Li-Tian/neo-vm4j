package neo.vm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import neo.log.notr.TR;

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
        TR.enter();
        return TR.exit(list.size());
    }

    /**
     * @Author:doubi.liu
     * @description:清空栈
     * @date:2019/2/27
     */
    public void clear() {
        TR.enter();
        list.clear();
        TR.exit();
    }

    /**
     * @param stack 目标栈
     * @Author:doubi.liu
     * @description:复制栈内元素到另一个栈
     * @date:2019/2/27
     */
    public void copyTo(RandomAccessStack<T> stack) {

        TR.enter();
        stack.list.addAll(list);
        TR.exit();
    }

    /**
     * @param stack 目标栈 count 复制的起始位置
     * @Author:doubi.liu
     * @description:复制栈内元素到另一个栈
     * @date:2019/2/28
     */
    public void copyTo(RandomAccessStack<T> stack, int count) {
        TR.enter();
        if (count == 0) {
            TR.exit();
            return;
        }
        if (count == -1) {
            stack.list.addAll(list);
        } else {
            stack.list.addAll(list.subList(list.size() - count, list.size()));
        }
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description:获取栈的迭代器
     * @date:2019/2/27
     */
    public Iterator<T> getEnumerator() {
        TR.enter();
        return TR.exit(list.iterator());
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
        TR.enter();
        if (index > list.size()) {
            throw TR.exit(new UnsupportedOperationException());
        }
        list.add(list.size() - index, item);
        TR.exit();
    }

    /**
     * @param index 索引
     * @Author:doubi.liu
     * @description: 获取栈内指定索引位置的元素，但不移除
     * @date:2019/2/28
     */
    public T peek(int index) {
        TR.enter();
        if (index >= list.size()) {
            throw TR.exit(new UnsupportedOperationException());
        }
        if (index < 0) {
            index += list.size();
        }
        if (index < 0) {
            throw TR.exit(new UnsupportedOperationException());
        }
        index = list.size() - index - 1;
        return TR.exit(list.get(index));
    }

    /**
     * @Author:doubi.liu
     * @description:获取栈顶元素，但不移除
     * @date:2019/2/28
     */
    public T peek() {
        TR.enter();
        int index = 0;
        if (index >= list.size()) {
            throw TR.exit(new UnsupportedOperationException());
        }
        if (index < 0) {
            index += list.size();
        }
        if (index < 0) {
            throw TR.exit(new UnsupportedOperationException());
        }
        index = list.size() - index - 1;
        return TR.exit(list.get(index));
    }

    /**
     * @Author:doubi.liu
     * @description:出栈，弹出栈顶元素
     * @date:2019/2/28
     */
    public T pop() {
        TR.enter();
        return TR.exit(remove(0));
    }

    /**
     * @param item 指定元素
     * @Author:doubi.liu
     * @description:压栈，压入一个元素，在栈顶
     * @date:2019/2/28
     */
    public void push(T item) {
        TR.enter();
        list.add(item);
        TR.exit();
    }

    /**
     * @param index 指定索引位置
     * @Author:doubi.liu
     * @description:移除指定索引位置的元素
     * @date:2019/2/28
     */
    public T remove(int index) {
        TR.enter();
        if (index >= list.size()) {
            throw TR.exit(new UnsupportedOperationException());
        }
        if (index < 0) index += list.size();
        if (index < 0) {
            throw TR.exit(new UnsupportedOperationException());
        }
        index = list.size() - index - 1;
        T item = list.get(index);
        list.remove(index);
        return TR.exit(item);
    }

    /**
     * @param index 指定索引位置 item 元素
     * @Author:doubi.liu
     * @description:替换指定索引位置的元素
     * @date:2019/2/28
     */
    public void set(int index, T item) {
        TR.enter();
        if (index >= list.size()) {
            throw TR.exit(new UnsupportedOperationException());
        }
        if (index < 0) index += list.size();
        if (index < 0) {
            throw TR.exit(new UnsupportedOperationException());
        }
        index = list.size() - index - 1;
        list.set(index, item);
        TR.exit();
    }
}
