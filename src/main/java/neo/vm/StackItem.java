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
import neo.log.notr.TR;
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
        TR.enter();
        if (obj == null) {
            return TR.exit(false);
        }
        if (obj == this) {
            return TR.exit(true);
        }
        if (obj instanceof StackItem) {

            return TR.exit(equals((StackItem) obj));
        }
        return TR.exit(false);
    }

    public static <T> StackItem fromInterface(T value) {
        TR.enter();
        return TR.exit(new InteropInterface<T>(value));
    }

    public BigInteger getBigInteger() {
        TR.enter();
        return TR.exit(new BigInteger(getByteArray()));
    }

    public boolean getBoolean() {
        TR.enter();
        // LINQ START
        //return GetByteArray().Any(p = > p != 0);
        return TR.exit(Arrays.asList(getByteArray()).stream().anyMatch(p -> !p.equals(0)));
        // LINQ END
    }

    public abstract byte[] getByteArray();

    public int getHashCode() {
        TR.enter();
        int hash = 17;
        for (byte element : getByteArray()) {
            //hash = hash * 31 + element;
            hash = Math.addExact(Math.multiplyExact(hash, 31), element);
        }
        return TR.exit(hash);
    }

    public String getString() {
        TR.enter();
        try {
            return TR.exit(new String(getByteArray(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw TR.exit(new RuntimeException(e));
        }
    }

    public static BigInteger getStackItem(int value) {
        TR.enter();
        return TR.exit(new BigInteger(String.valueOf(value)));
    }

    public static BigInteger getStackItem(Uint value) {
        TR.enter();
        return TR.exit(value.ulongValue().bigIntegerValue());
    }

    public static BigInteger getStackItem(long value) {
        TR.enter();
        return TR.exit(BigInteger.valueOf(value));
    }

    public static BigInteger getStackItem(Ulong value) {
        TR.enter();
        return TR.exit(value.bigIntegerValue());
    }

    public static Integer getStackItem(BigInteger value) {
        TR.enter();
        return TR.exit(new Integer(value));
    }

    public static Boolean getStackItem(boolean value) {
        TR.enter();
        return TR.exit(new Boolean(value));
    }

    public static ByteArray getStackItem(byte[] value) {
        TR.enter();
        return TR.exit(new ByteArray(value));
    }

    public static ByteArray getStackItem(String value) {
        TR.enter();
        try {
            return TR.exit(new ByteArray(value.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw TR.exit(new RuntimeException(e));
        }
    }

    public static Array getStackItem(StackItem[] value) {
        TR.enter();
        return TR.exit(new Array(value));
    }

    public static Array getStackItem(List<StackItem> value) {
        TR.enter();
        return TR.exit(new Array(value));
    }
}
