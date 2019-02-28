package neo.vm;

import java.io.IOException;
import java.io.InputStream;

import neo.csharp.BitConverter;
import neo.log.notr.TR;

/**
 * 这个类是从 Neo.VM.Helper 移植过来。因为 C# 版的代码中这个类与 Neo.dll 中的另一个类同名，所以移植到 Java 版时，这个类被重命名为 NVMHelper
 */
class NVMHelper {

    /**
     * 从指定输入流中读取可变长的字节数组
     *
     * @param reader 输入流
     * @return 字节数组
     * @throws IOException 读取失败
     */
    public static byte[] readVarBytes(InputStream reader) throws IOException {
        TR.enter();
        return TR.exit(readVarBytes(reader, 0x10000000));
    }

    /**
     * 从指定输入流中读取可变长的字节数组
     *
     * @param reader 输入流
     * @param max    指定最大字节数
     * @return 字节数组
     * @throws IOException 读取失败
     */
    public static byte[] readVarBytes(InputStream reader, int max) throws IOException {
        TR.enter();
        int length = (int) readVarInt(reader, max);
        byte[] result = safeReadBytes(reader, length);
        return TR.exit(result);
    }

    /**
     * 从字节流中读取变长整数
     *
     * @param reader 字节流
     * @return 读取的整数。由于java没有ULONG，能读取的最大整数与C#版相比，要小大约一半。
     * @throws IOException 读取失败。数据格式不正确。
     */
    public static long readVarInt(InputStream reader) throws IOException {
        TR.enter();
        return TR.exit(readVarInt(reader, Long.MAX_VALUE));
    }

    /**
     * 从字节流中读取变长整数
     *
     * @param reader 字节流
     * @param max    指定的最大值，如果读取的值超过此值则认定为数据格式错误，抛出IOException
     * @return 读取的整数。由于java没有ULONG，能读取的最大整数与C#版相比，要小大约一半。
     * @throws IOException 读取失败。数据格式不正确。
     */
    public static long readVarInt(InputStream reader, long max) throws IOException {
        TR.enter();
        long value = BitConverter.decodeVarInt(reader);
        if (value > max) {
            throw new IOException("VarInt out of specified range : " + value);
        }
        return TR.exit(value);
    }

    /**
     * 安全读取字节数组
     *
     * @param reader 输入数据流
     * @param count  指定读取字节数组的长度
     * @return 成功读取的字节数组
     * @throws IOException 任何未能成功读取到指定字节长度的数据的原因。
     */
    public static byte[] safeReadBytes(InputStream reader, int count) throws IOException {
        TR.enter();
        byte[] data = new byte[count];
        int countRead = reader.read(data);
        if (countRead < count) {
            TR.exit();
            throw new IOException(String.format(
                    "try to read %d bytes, actual returned %d bytes", count, countRead));
        }
        TR.exit();
        return data;
    }
}
