package neo.vm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import neo.csharp.BitConverter;
import neo.csharp.Ushort;
import neo.csharp.io.BinaryWriter;
import neo.log.notr.TR;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: ScriptBuilder
 * @Package neo.vm
 * @Description: 脚本构建器
 * @date Created in 14:16 2019/2/28
 */
public class ScriptBuilder {
    private ByteArrayOutputStream ms = new ByteArrayOutputStream();
    private BinaryWriter writer;

    /**
      * @Author:doubi.liu
      * @description:获取脚本读取指针的偏移位
      * @param
      * @date:2019/3/1
    */
    public int getOffset() {
        TR.enter();
        TR.fixMe("原生VM暂时未使用,暂时未支持ScriptBuilder的getOffset功能");
        throw TR.exit(new UnsupportedOperationException());
    }

    /**
      * @Author:doubi.liu
      * @description:构造函数
      * @param
      * @date:2019/3/1
    */
    public ScriptBuilder() {
        this.writer = new BinaryWriter(ms);
    }

    /**
      * @Author:doubi.liu
      * @description:资源释放
      * @param 
      * @date:2019/3/1
    */
    public void dispose() throws IOException {
        TR.enter();
        try {
            writer.close();
            ms.close();
        } catch (IOException e) {
            throw TR.exit(e);
        }
        TR.exit();
    }

    /**
      * @Author:doubi.liu
      * @description:向脚本生成器中写入操作码
      * @param op Opcode
      * @date:2019/3/1
    */
    public ScriptBuilder emit(OpCode op) {
        TR.enter();
        byte[] arg = null;
        writer.writeByte(op.getCode());
        if (arg != null) {
            writer.write(arg);
        }
        return TR.exit(this);
    }

    /**
      * @Author:doubi.liu
      * @description:向脚本生成器中写入操作码和参数
      * @param op OpCode arg 字节数组
      * @date:2019/3/1
    */
    public ScriptBuilder emit(OpCode op, byte[] arg) {
        TR.enter();
        writer.writeByte(op.getCode());
        if (arg != null) {
            writer.write(arg);
        }
        return TR.exit(this);
    }

    /**
      * @Author:doubi.liu
      * @description:向脚本生成器中写入函数调用，函数由脚本哈希指定
      * @param scriptHash
      * @date:2019/3/1
    */
    public ScriptBuilder emitAppCall(byte[] scriptHash) {
        TR.enter();
        boolean useTailCall = false;
        if (scriptHash.length != 20) {
            throw TR.exit(new IllegalArgumentException());
        }
        return TR.exit(emit(useTailCall ? OpCode.TAILCALL : OpCode.APPCALL, scriptHash));
    }

    /**
      * @Author:doubi.liu
      * @description:向脚本生成器中写入函数调用，函数由脚本哈希指定
      * @param scriptHash useTailCall 是否启用尾调用
      * @date:2019/3/1
    */
    public ScriptBuilder emitAppCall(byte[] scriptHash, boolean useTailCall) {
        TR.enter();
        if (scriptHash.length != 20) {
            throw TR.exit(new IllegalArgumentException());
        }
        return TR.exit(emit(useTailCall ? OpCode.TAILCALL : OpCode.APPCALL, scriptHash));
    }

    /**
      * @Author:doubi.liu
      * @description:向脚本生成器中写入跳转指令
      * @param op  offset 跳转指令偏移量
      * @date:2019/3/1
    */
    public ScriptBuilder emitJump(OpCode op, short offset) {
        TR.enter();
        if (op != OpCode.JMP && op != OpCode.JMPIF && op != OpCode.JMPIFNOT && op != OpCode.CALL) {
            throw TR.exit(new IllegalArgumentException());
        }
        return TR.exit(emit(op, BitConverter.getBytes(offset)));
    }

    /**
      * @Author:doubi.liu
      * @description:向脚本生成器中写入一个整数，包括整数对应的压栈指令和整数本身
      * @param number 带符号整数
      * @date:2019/3/1
    */
    public ScriptBuilder emitPush(BigInteger number) {
        TR.enter();
        if (number.equals(new BigInteger("-1"))) {
            return TR.exit(emit(OpCode.PUSHM1));
        }
        if (number.equals(new BigInteger("0"))) {
            return TR.exit(emit(OpCode.PUSH0));
        }
        if (number.compareTo(new BigInteger("0")) > 0
                && number.compareTo(new BigInteger("16")) <= 0) {
            return TR.exit(emit(OpCode.fromByte((byte) (OpCode.PUSH1.getCode() - 1 + number
                    .byteValue()))));
        }
        return TR.exit(emitPush(number.toByteArray()));
    }

    /**
      * @Author:doubi.liu
      * @description:向脚本生成器中写入一个布尔类型的值
      * @param data
      * @date:2019/3/1
    */
    public ScriptBuilder emitPush(boolean data) {
        TR.enter();
        return TR.exit(emit(data ? OpCode.PUSHT : OpCode.PUSHF));
    }

    /**
      * @Author:doubi.liu
      * @description:向脚本生成器中写入一个字节数组，首先判断字节数组的长度，对不同的长度使用不同的压栈指令
      * @param data
      * @date:2019/3/1
    */
    public ScriptBuilder emitPush(byte[] data) {
        TR.enter();
        if (data == null) {
            throw TR.exit(new IllegalArgumentException());
        }
        if (data.length <= (int) OpCode.PUSHBYTES75.getCode()) {
            writer.writeByte((byte) data.length);
            writer.write(data);
        } else if (data.length < 0x100) {
            emit(OpCode.PUSHDATA1);
            writer.writeByte((byte) data.length);
            writer.write(data);
        } else if (data.length < 0x10000) {
            emit(OpCode.PUSHDATA2);
            writer.writeUshort(new Ushort(data.length));
            writer.write(data);
        } else// if (data.Length < 0x100000000L)
        {
            emit(OpCode.PUSHDATA4);
            writer.writeInt(data.length);
            writer.write(data);
        }
        return TR.exit(this);
    }

    /**
      * @Author:doubi.liu
      * @description:向脚本生成器中写入一个字符串
      * @param data
      * @date:2019/3/1
    */
    public ScriptBuilder emitPush(String data) {
        TR.enter();
        try {
            return TR.exit(emitPush(data.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            //一般不会发生，不处理
            throw TR.exit(new RuntimeException(e));
        }
    }

    /**
      * @Author:doubi.liu
      * @description:向脚本生成器中写入指定的系统互操作服务调用
      * @param api 系统互操作服务api字符串
      * @date:2019/3/1
    */
    public ScriptBuilder emitSysCall(String api) {
        TR.enter();
        if (api == null)
            throw TR.exit(new IllegalArgumentException());
        byte[] api_bytes = new byte[0];
        try {
            api_bytes = api.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            throw TR.exit(new RuntimeException(e));
        }
        if (api_bytes.length == 0 || api_bytes.length > 252)
            throw TR.exit(new IllegalArgumentException());
        byte[] arg = new byte[api_bytes.length + 1];
        arg[0] = (byte) api_bytes.length;
        //Buffer.BlockCopy(api_bytes, 0, arg, 1, api_bytes.length);
        System.arraycopy(api_bytes, 0, arg, 1, api_bytes.length);
        return TR.exit(emit(OpCode.SYSCALL, arg));
    }

    /**
      * @Author:doubi.liu
      * @description:获取脚本生成器的内容
      * @param
      * @date:2019/3/1
    */
    public byte[] toArray() {
        TR.enter();
        writer.flush();
        return TR.exit(ms.toByteArray());
    }
}
