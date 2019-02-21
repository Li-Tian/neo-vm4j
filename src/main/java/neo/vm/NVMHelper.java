package neo.vm;

/**
 * 这个类是从 Neo.VM.Helper 移植过来。
 * 因为 C# 版的代码中这个类与 Neo.dll 中的另一个类同名。
 * 所以移植到 Java 版时，这个类被重命名为 NVMHelper
 */
class NVMHelper {
// TODO
//    public static byte[] ReadVarBytes(BinaryReader reader, int max = 0x10000000)
//    {
//        return reader.SafeReadBytes((int)reader.ReadVarInt((ulong)max));
//    }
//
//    public static ulong ReadVarInt(this BinaryReader reader, ulong max = ulong.MaxValue)
//    {
//        byte fb = reader.ReadByte();
//        ulong value;
//        if (fb == 0xFD)
//            value = reader.ReadUInt16();
//        else if (fb == 0xFE)
//            value = reader.ReadUInt32();
//        else if (fb == 0xFF)
//            value = reader.ReadUInt64();
//        else
//            value = fb;
//        if (value > max) throw new FormatException();
//        return value;
//    }
//
//    public static byte[] SafeReadBytes(this BinaryReader reader, int count)
//    {
//        byte[] data = reader.ReadBytes(count);
//        if (data.Length < count)
//            throw new FormatException();
//        return data;
//    }
}
