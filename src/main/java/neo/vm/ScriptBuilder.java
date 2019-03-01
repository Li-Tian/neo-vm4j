package neo.vm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.Buffer;

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

    public int getOffset() {
        TR.fixMe("原生VM暂时未使用,暂时未支持ScriptBuilder的getOffset功能");
        throw new UnsupportedOperationException();
    }

    public ScriptBuilder() {
        this.writer = new BinaryWriter(ms);
    }

    public void dispose() throws IOException {
        writer.close();
        ms.close();
    }

    public ScriptBuilder emit(OpCode op) {
        byte[] arg = null;
        writer.writeByte(op.getCode());
        if (arg != null) {
            writer.write(arg);
        }
        return this;
    }

    public ScriptBuilder emit(OpCode op, byte[] arg) {
        writer.writeByte(op.getCode());
        if (arg != null) {
            writer.write(arg);
        }
        return this;
    }

    public ScriptBuilder emitAppCall(byte[] scriptHash) {
        boolean useTailCall = false;
        if (scriptHash.length != 20) {
            throw new IllegalArgumentException();
        }
        return emit(useTailCall ? OpCode.TAILCALL : OpCode.APPCALL, scriptHash);
    }

    public ScriptBuilder emitAppCall(byte[] scriptHash, boolean useTailCall) {
        if (scriptHash.length != 20) {
            throw new IllegalArgumentException();
        }
        return emit(useTailCall ? OpCode.TAILCALL : OpCode.APPCALL, scriptHash);
    }

    public ScriptBuilder emitJump(OpCode op, short offset) {
        if (op != OpCode.JMP && op != OpCode.JMPIF && op != OpCode.JMPIFNOT && op != OpCode.CALL) {
            throw new IllegalArgumentException();
        }
        return emit(op, BitConverter.getBytes(offset));
    }

    public ScriptBuilder emitPush(BigInteger number) {
        if (number.equals(new BigInteger("-1"))) {
            return emit(OpCode.PUSHM1);
        }
        if (number.equals(new BigInteger("0"))) {
            return emit(OpCode.PUSH0);
        }
        if (number.compareTo(new BigInteger("0")) > 0
                && number.compareTo(new BigInteger("16")) <= 0) {
            return emit(OpCode.fromByte((byte) (OpCode.PUSH1.getCode() - 1 + number.byteValue())));
        }
        return emitPush(number.toByteArray());
    }

    public ScriptBuilder emitPush(boolean data) {
        return emit(data ? OpCode.PUSHT : OpCode.PUSHF);
    }

    public ScriptBuilder emitPush(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException();
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
        return this;
    }

    public ScriptBuilder emitPush(String data) {
        try {
            return emitPush(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            //一般不会发生，不处理
            throw new RuntimeException(e);
        }
    }

    public ScriptBuilder emitSysCall(String api) {
        if (api == null)
            throw new IllegalArgumentException();
        byte[] api_bytes = new byte[0];
        try {
            api_bytes = api.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        if (api_bytes.length == 0 || api_bytes.length > 252)
            throw new IllegalArgumentException();
        byte[] arg = new byte[api_bytes.length + 1];
        arg[0] = (byte) api_bytes.length;
        //Buffer.BlockCopy(api_bytes, 0, arg, 1, api_bytes.length);
        return emit(OpCode.SYSCALL, arg);
    }

    public byte[] toArray() {
        writer.flush();
        return ms.toByteArray();
    }
    //// TODO: 2019/2/28
}
