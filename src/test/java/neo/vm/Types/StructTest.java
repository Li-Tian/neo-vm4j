package neo.vm.Types;

import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import neo.vm.StackItem;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: StructTest
 * @Package neo.vm.Types
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 14:17 2019/2/26
 */
public class StructTest {
    @Test
    public void cloneTest() throws Exception {
        List temp = new ArrayList();
        temp.add(new Boolean(true));
        Struct a = new Struct(temp);
        Struct b=a.clone();
        if (!b.equals(a)){
            throw new Exception("clone方法异常");
        }
    }


    @Test
    public void equals() throws Exception {
        List temp = new ArrayList();
        temp.add(new Boolean(true));
        Struct a = new Struct(temp);
        Struct b = new Struct(temp);
        if (!a.equals(b)) {
            throw new Exception("判空方法异常");
        }
    }

}