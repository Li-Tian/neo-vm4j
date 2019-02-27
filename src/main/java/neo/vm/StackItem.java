package neo.vm;

import org.omg.IOP.Encoding;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import neo.csharp.Uint;
import neo.csharp.Ulong;
import neo.vm.Types.Array;
import neo.vm.Types.Boolean;
import neo.vm.Types.ByteArray;
import neo.vm.Types.Integer;
import neo.vm.Types.InteropInterface;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: StackItem
 * @Package neo.vm
 * @Description: 对应c#StackItem，vm中所有基础类型的基类
 * @date Created in 13:34 2019/2/25
 */
public abstract class StackItem {

    public abstract boolean equals(StackItem other);

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof StackItem) {

            return equals((StackItem)obj);
        }
        return false;
    }

    public static <T>StackItem fromInterface(T value)
    {
        return new InteropInterface<T>(value);
    }

    public BigInteger getBigInteger() {
        return new BigInteger(getByteArray());
    }

    public boolean getBoolean() {
        // LINQ START
        //return GetByteArray().Any(p = > p != 0);
        return Arrays.asList(getByteArray()).stream().anyMatch(p->!p.equals(0));
        // LINQ END
    }

    public abstract byte[] getByteArray();

    public int getHashCode() {
            int hash = 17;
            for (byte element:getByteArray()) {
                //hash = hash * 31 + element;
                hash = Math.addExact(Math.multiplyExact(hash,31),element);
            }
            return hash;
    }

    public String getString() {
        try {
            return new String(getByteArray(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static BigInteger getStackItem(int value) {
        return new BigInteger(String.valueOf(value));
    }

    public static BigInteger getStackItem(Uint value) {
        return value.ulongValue().bigIntegerValue();
    }

    public static BigInteger getStackItem(long value) {
        return BigInteger.valueOf(value);
    }

    public static BigInteger getStackItem(Ulong value) {
        return value.bigIntegerValue();
    }

    public static Integer getStackItem(BigInteger value) {
        return new Integer(value);
    }

    public static Boolean getStackItem(boolean value) {
        return new Boolean(value);
    }

    public static ByteArray getStackItem(byte[] value) {
        return new ByteArray(value);
    }

    public static ByteArray getStackItem(String value) {
        try {
            return new ByteArray(value.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Array getStackItem(StackItem[] value) {
        return new Array(value);
    }

    public static Array getStackItem(List<StackItem> value) {
        return new Array(value);
    }
}
