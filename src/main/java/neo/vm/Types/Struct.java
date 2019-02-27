package neo.vm.Types;

import com.sun.jmx.remote.internal.ArrayQueue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import neo.log.notr.TR;
import neo.vm.StackItem;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: Struct
 * @Package neo.vm.Types
 * @Description: Struct是StackItem的子类
 * @date Created in 16:16 2019/2/25
 */
public class Struct extends Array {

    /**
      * @Author:doubi.liu
      * @description:默认构造函数
      * @param
      * @date:2019/2/26
    */
    public Struct() {
        this(new ArrayList<StackItem>());
    }

    /**
      * @Author:doubi.liu
      * @description:构造函数
      * @param value
      * @date:2019/2/26
    */
    public Struct(Iterable<StackItem> value) {
        super(value);
    }

    /**
      * @Author:doubi.liu
      * @description:拷贝方法
      * @param
      * @date:2019/2/26
    */
    @Override
    public Struct clone() {
        TR.enter();
        Struct tstruct = new Struct();
        Queue<Struct> queue = new ConcurrentLinkedQueue<>();
        queue.add(tstruct);
        queue.add(this);
        while (queue.size() > 0) {
            Struct a = queue.poll();
            Struct b = queue.poll();
            for (StackItem item : b.getEnumerator()) {
                if (item instanceof Struct) {
                    Struct sa = new Struct();
                    a.add(sa);
                    queue.add(sa);
                    queue.add((Struct) item);
                } else {
                    a.add(item);
                }
            }
        }
        return TR.exit(tstruct);
    }

    /**
      * @Author:doubi.liu
      * @description:判等方法
      * @param other
      * @date:2019/2/26
    */
    @Override
    public boolean equals(StackItem other) {
        TR.enter();
        if (other == null) {
            return TR.exit(false);
        }
        Stack<StackItem> stack1 = new Stack<StackItem>();
        Stack<StackItem> stack2 = new Stack<StackItem>();
        stack1.push(this);
        stack2.push(other);
        while (stack1.size() > 0) {
            StackItem a = stack1.pop();
            StackItem b = stack2.pop();
            if (a instanceof Struct) {
                if (a == b) {
                    continue;
                }
                if (!(b instanceof Struct)) {
                    return TR.exit(false);
                }
                if (((Struct) a).getCount() != ((Struct) b).getCount()) {
                    return TR.exit(false);
                }
                for (StackItem item : ((Struct) a).getEnumerator()) {
                    stack1.push(item);
                }
                for (StackItem item : ((Struct) b).getEnumerator()) {
                    stack2.push(item);
                }
            } else {
                if (!a.equals(b)) {
                    return TR.exit(false);
                }
            }
        }
        return TR.exit(true);
    }
}
