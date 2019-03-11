package neo.vm;

import com.sun.org.apache.bcel.internal.generic.PUSH;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import neo.csharp.Uint;
import neo.vm.Types.Array;

import static org.junit.Assert.*;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: ExecutionEngineTest
 * @Package neo.vm
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 15:05 2019/3/5
 */
public class ExecutionEngineTest {

    @Test(expected = UnsupportedOperationException.class)
    public void getCurrentContext() throws Exception {
        ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
        engine.getCurrentContext();
    }

    @Test
    public void getCallingContext() throws Exception {
        ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
        Assert.assertEquals(null, engine.getCallingContext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getEntryContext() throws Exception {
        ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
        engine.getEntryContext();
    }

    @Test
    public void addBreakPoint() throws Exception {
        ScriptBuilder builder = new ScriptBuilder();
        builder.emit(OpCode.PUSH1);
        builder.emit(OpCode.PUSH2);
        builder.emit(OpCode.PUSH3);
        builder.emit(OpCode.PUSH4);
        byte[] testScript = builder.toArray();
        ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default, new IScriptTable() {
            @Override
            public byte[] getScript(byte[] script_hash) {
                ScriptBuilder builder2 = new ScriptBuilder();
                builder2.emitPush(new BigInteger("2"));
                byte[] testScript2 = builder2.toArray();
                return testScript2;
            }
        }, new IInteropService() {
            @Override
            public boolean invoke(byte[] method, ExecutionEngine engine) {
                return true;
            }
        });
        engine.loadScript(testScript);
        engine.addBreakPoint(engine.getCurrentContext().getScriptHash() ,new Uint(2));

        engine.execute();
        Assert.assertEquals(VMState.BREAK, engine.state);
        Assert.assertEquals(2, engine.getCurrentContext().getEvaluationStack().getCount());
        engine.dispose();
    }

    @Test
    public void dispose() throws Exception {
        ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
        engine.dispose();
    }

    @Test
    public void execute() throws Exception {
        //PUSHT、PUSH0-PUSH16
        for (int i = 0; i <= 16; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.fromByte((byte) (OpCode.PUSH1.getCode() + i)));
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            engine.dispose();
        }
        //PUSHBYTES1-PUSHBYTES75
        for (int i = 0; i < 75; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            byte[] array = new byte[i + 1];
            builder.emit(OpCode.fromByte((byte) (OpCode.PUSHBYTES1.getCode() + i)), array);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            engine.dispose();
        }

        //PUSHDATA1-PUSHDATA4
        for (int i = 0; i < 3; i++) {
            OpCode[] oparray = new OpCode[]{OpCode.PUSHDATA1, OpCode.PUSHDATA2, OpCode.PUSHDATA4};
            int[] lengtharray = new int[]{1, 2, 4};
            ScriptBuilder builder = new ScriptBuilder();
            byte[] array = new byte[lengtharray[i]];
            builder.emit(oparray[i], array);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            engine.dispose();
        }

        //NOP
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.NOP);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            engine.dispose();
        }

        //JMP、JMPIF、JMPIFNOT
        for (int i = 0; i < 3; i++) {
            OpCode[] oparray = new OpCode[]{OpCode.JMP, OpCode.JMPIF, OpCode.JMPIFNOT};
            OpCode[] dataarray = new OpCode[]{OpCode.PUSH0, OpCode.PUSH0, OpCode.PUSH1};


            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(dataarray[i]);
            builder.emitJump(oparray[i], (short) 3);

            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            engine.dispose();
        }
        //DUPFROMALTSTACK、TOALTSTACK、FROMALTSTACK
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.TOALTSTACK);
            builder.emit(OpCode.DUPFROMALTSTACK);
            builder.emit(OpCode.FROMALTSTACK);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(2, engine.resultStack.getCount());
            engine.dispose();
        }

        //XDROP
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.XDROP);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1, engine.resultStack.getCount());
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //XSWAP
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH3);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.XSWAP);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(2, engine.resultStack.getCount());
            Assert.assertEquals(3, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //XTUCK
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH3);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.XTUCK);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(3, engine.resultStack.getCount());
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }

        //DEPTH
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH3);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.DEPTH);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(4, engine.resultStack.getCount());
            Assert.assertEquals(3, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //DROP
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH3);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.DROP);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(2, engine.resultStack.getCount());
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //DUP
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH3);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.DUP);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(4, engine.resultStack.getCount());
            Assert.assertEquals(1, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(1, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(3, engine.resultStack.pop().getBigInteger().intValue());

            engine.dispose();
        }

        //NIP
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH3);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.NIP);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(2, engine.resultStack.getCount());
            Assert.assertEquals(1, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(3, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }

        //OVER
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH3);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.OVER);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(4, engine.resultStack.getCount());
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(1, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(3, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //PICK
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH3);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.PICK);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(3, engine.resultStack.getCount());
            Assert.assertEquals(3, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(3, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //ROLL
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH3);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.ROLL);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(2, engine.resultStack.getCount());
            Assert.assertEquals(3, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //ROT
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH3);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.ROT);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(3, engine.resultStack.getCount());
            Assert.assertEquals(3, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(1, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }

        //SWAP
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH3);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.SWAP);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(3, engine.resultStack.getCount());
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(1, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(3, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //TUCK
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH3);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.TUCK);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(4, engine.resultStack.getCount());
            Assert.assertEquals(1, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(1, engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(3, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //CAT
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new byte[]{0x02});
            builder.emitPush(new byte[]{0x01});
            builder.emit(OpCode.CAT);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1, engine.resultStack.getCount());
            Assert.assertArrayEquals(new byte[]{0x02, 0x01}, engine.resultStack.pop().getByteArray());
            engine.dispose();
        }
        //SUBSTR
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new byte[]{0x02, 0x03, 0x04});
            builder.emitPush(new BigInteger("1"));
            builder.emitPush(new BigInteger("2"));
            builder.emit(OpCode.SUBSTR);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1, engine.resultStack.getCount());
            Assert.assertArrayEquals(new byte[]{0x03, 0x04}, engine.resultStack.pop().getByteArray());
            engine.dispose();
        }
        //LEFT
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new byte[]{0x02, 0x03, 0x04});
            builder.emitPush(new BigInteger("1"));
            builder.emit(OpCode.LEFT);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1, engine.resultStack.getCount());
            Assert.assertArrayEquals(new byte[]{0x02}, engine.resultStack.pop().getByteArray());
            engine.dispose();
        }
        //RIGHT
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new byte[]{0x02, 0x03, 0x04});
            builder.emitPush(new BigInteger("1"));
            builder.emit(OpCode.RIGHT);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1, engine.resultStack.getCount());
            Assert.assertArrayEquals(new byte[]{0x04}, engine.resultStack.pop().getByteArray());
            engine.dispose();
        }
        //SIZE
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new byte[]{0x02, 0x03, 0x04});
            builder.emit(OpCode.SIZE);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(3, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //INVERT
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new BigInteger("0"));
            builder.emit(OpCode.INVERT);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(-1, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //AND
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.AND);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(0, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //OR
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.OR);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(0, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //XOR
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.XOR);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }

        //EQUAL
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.EQUAL);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(true, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }
        //INC
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.INC);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //DEC
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.DEC);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(0, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //SIGN
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new BigInteger("-2"));
            builder.emit(OpCode.SIGN);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(-1, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //negate
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new BigInteger("-2"));
            builder.emit(OpCode.NEGATE);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //abs
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new BigInteger("-2"));
            builder.emit(OpCode.ABS);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }

        //NOT
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(true);
            builder.emit(OpCode.NOT);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(false, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }

        //NZ
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.NZ);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(false, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }
        //ADD
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.ADD);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }

        //SUB
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.SUB);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(0, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }

        //MUL
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.MUL);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(2, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //DIV
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.DIV);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }

        //MOD
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.MOD);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(0, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //SHL
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.SHL);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(4, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //SHR
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.SHR);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(0, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //BOOLAND
        //// TODO: 2019/3/7
/*        for (int i=0;i<1;i++){
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(true);
            builder.emitPush(false);
            builder.emit(OpCode.BOOLAND);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT,engine.state);
            Assert.assertEquals(false,engine.resultStack.pop().getBoolean());
            engine.dispose();
        }*/
        //BOOLOR
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(true);
            builder.emitPush(false);
            builder.emit(OpCode.BOOLOR);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(true, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }
        //NUMEQUAL
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.NUMEQUAL);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(false, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }
        //NUMENOTQUAL
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.NUMNOTEQUAL);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(true, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }

        //LT
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.LT);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(true, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }
        //GT
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.GT);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(false, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }
        //LTE
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.LTE);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(true, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }
        //GTE
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.GTE);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(false, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }
        //MIN
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.MIN);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(0, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //MAX
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.MAX);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }

        //WITHIN
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.PUSH1);
            builder.emit(OpCode.PUSH2);
            builder.emit(OpCode.PUSH0);
            builder.emit(OpCode.MAX);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(true, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }
        //SHA1
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush("love");
            builder.emit(OpCode.SHA1);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(47, engine.resultStack.pop().getByteArray()[1]);
            engine.dispose();
        }
        //SHA256
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush("love");
            builder.emit(OpCode.SHA256);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(111, engine.resultStack.pop().getByteArray()[1]);
            engine.dispose();
        }
        //HASH160
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush("love");
            builder.emit(OpCode.HASH160);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(127,
                    engine.resultStack.pop().getByteArray()[1]);
            engine.dispose();
        }
        //HASH256
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush("love");
            builder.emit(OpCode.HASH256);
            byte[] testScript = builder.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(107, engine.resultStack.pop().getByteArray()[3]);
            engine.dispose();
        }
        //CHECKSIG
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new byte[]{0x00});
            builder.emitPush(new byte[]{0x00});
            builder.emit(OpCode.CHECKSIG);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(true, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }
        //VERIFY
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new byte[]{0x00});
            builder.emitPush(new byte[]{0x00});
            builder.emitPush(new byte[]{0x00});
            builder.emit(OpCode.VERIFY);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(true, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }

        //CHECKMULTISIG
        //// TODO: 2019/3/7
/*        for (int i=0;i<1;i++){
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new BigInteger("2"));
            builder.emitPush(new byte[]{0x00,0x00});
            builder.emitPush(new byte[]{0x00,0x00});
            builder.emitPush(new BigInteger("2"));
            builder.emitPush(new byte[]{0x00,0x00});
            builder.emitPush(new byte[]{0x00,0x00});
            builder.emit(OpCode.CHECKMULTISIG);
            byte[] testScript = builder.toArray();
            IScriptContainer sc=new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT,engine.state);
            Assert.assertEquals(true,engine.resultStack.pop().getBoolean());
            engine.dispose();
        }*/

        //ARRAYSIZE
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new byte[]{0x00});
            builder.emit(OpCode.ARRAYSIZE);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1, engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }

        //PACK
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new BigInteger("1"));
            builder.emitPush(new BigInteger("1"));
            builder.emitPush(new BigInteger("2"));
            builder.emit(OpCode.PACK);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(2, ((Array) engine.resultStack.pop()).getCount());
            engine.dispose();
        }

        //UNPACK
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emitPush(new BigInteger("3"));
            builder.emit(OpCode.NEWARRAY);
            builder.emit(OpCode.UNPACK);
            //builder.emit(OpCode.UNPACK);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(4, engine.resultStack.getCount());
            engine.dispose();
        }


        //NEWARRAY、SETITEM
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();

            builder.emitPush(new BigInteger("3"));
            builder.emit(OpCode.NEWARRAY);
            builder.emitPush(new BigInteger("0"));
            builder.emitPush(new BigInteger("2"));
            builder.emit(OpCode.SETITEM);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(0, engine.resultStack.getCount());
            engine.dispose();
        }
        //PICKITEM
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();

            builder.emitPush(new BigInteger("3"));
            builder.emit(OpCode.NEWARRAY);
            builder.emitPush(new BigInteger("0"));
            builder.emit(OpCode.PICKITEM);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(false, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }
        //NEWSTRUCT
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();

            builder.emitPush(new BigInteger("3"));
            builder.emit(OpCode.NEWSTRUCT);
            builder.emitPush(new BigInteger("0"));
            builder.emit(OpCode.PICKITEM);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(false, engine.resultStack.pop().getBoolean());
            engine.dispose();
        }
        //NEWMAP
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.emit(OpCode.NEWMAP);
            builder.emitPush(new BigInteger("0"));
            builder.emit(OpCode.PICKITEM);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.FAULT, engine.state);
            engine.dispose();
        }

        //APPEND,REVERSE,REMOVE,HASKEY
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();

            builder.emitPush(new BigInteger("3"));
            builder.emit(OpCode.NEWARRAY);
            builder.emit(OpCode.DUP);
            builder.emitPush(new BigInteger("3"));
            builder.emit(OpCode.NEWARRAY);
            builder.emit(OpCode.APPEND);
            builder.emit(OpCode.DUP);
            builder.emit(OpCode.REVERSE);
            builder.emit(OpCode.DUP);
            builder.emitPush(new BigInteger("3"));
            builder.emit(OpCode.REMOVE);
            builder.emit(OpCode.DUP);
            builder.emitPush(new BigInteger("3"));
            builder.emit(OpCode.HASKEY);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(false, engine.resultStack.pop().getBoolean());
            Array a = (Array) engine.resultStack.pop();
            Assert.assertEquals(3, a.getCount());
            Assert.assertEquals(3, ((Array) a.getArrayItem(0)).getCount());
            engine.dispose();
        }

        //KEYS
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();

            builder.emit(OpCode.NEWMAP);
            builder.emit(OpCode.KEYS);

            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Array a = (Array) engine.resultStack.pop();
            Assert.assertEquals(0, a.getCount());
            engine.dispose();
        }

        //VALUES
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();

            builder.emitPush(new BigInteger("3"));
            builder.emit(OpCode.NEWARRAY);
            builder.emit(OpCode.VALUES);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Array a = (Array) engine.resultStack.pop();
            Assert.assertEquals(3, a.getCount());
            engine.dispose();
        }

        //THROW
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();

            builder.emitPush(new BigInteger("3"));
            builder.emit(OpCode.THROW);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.FAULT, engine.state);
            engine.dispose();
        }

        //THROWIFNOT
        for (int i = 0; i < 1; i++) {
            ScriptBuilder builder = new ScriptBuilder();

            builder.emitPush(new Boolean(true));
            builder.emit(OpCode.THROWIFNOT);
            byte[] testScript = builder.toArray();
            IScriptContainer sc = new IScriptContainer() {
                @Override
                public byte[] getMessage() {
                    return new byte[0];
                }
            };
            ExecutionEngine engine = new ExecutionEngine(sc, Crypto.Default);
            engine.loadScript(testScript);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            engine.dispose();
        }

        //CALL
        for (int i = 0; i < 1; i++) {

            ScriptBuilder builder1 = new ScriptBuilder();
            builder1.emit(OpCode.CALL, new byte[]{0x06, 0x00});//指针偏移位+3（JMP）
            builder1.emit(OpCode.PUSH3);
            builder1.emit(OpCode.ADD);
            builder1.emit(OpCode.RET);
            builder1.emit(OpCode.PUSH2);
            builder1.emit(OpCode.PUSH1);
            builder1.emit(OpCode.MAX);
            builder1.emit(OpCode.RET);
            byte[] testScript1 = builder1.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript1);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            engine.dispose();
        }
        //SYSCALL
        for (int i = 0; i < 1; i++) {

            ScriptBuilder builder1 = new ScriptBuilder();
            builder1.emitSysCall("hello");
            byte[] testScript1 = builder1.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default, null, new IInteropService() {
                @Override
                public boolean invoke(byte[] method, ExecutionEngine engine) {
                    return true;
                }
            });
            engine.loadScript(testScript1);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            engine.dispose();
        }

        //APPCALL
        for (int i = 0; i < 1; i++) {

            ScriptBuilder builder1 = new ScriptBuilder();
            builder1.emitAppCall(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x01});
            builder1.emitPush(new BigInteger("5"));
            byte[] testScript1 = builder1.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default, new IScriptTable() {
                @Override
                public byte[] getScript(byte[] script_hash) {
                    ScriptBuilder builder2 = new ScriptBuilder();
                    builder2.emitPush(new BigInteger("2"));
                    byte[] testScript2 = builder2.toArray();
                    return testScript2;
                }
            }, new IInteropService() {
                        @Override
                        public boolean invoke(byte[] method, ExecutionEngine engine) {
                            return true;
                        }
                    });
            engine.loadScript(testScript1);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(2,engine.resultStack.getCount());
            Assert.assertEquals(5,engine.resultStack.pop().getBigInteger().intValue());
            Assert.assertEquals(2,engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }

        //TALLCALL
        for (int i = 0; i < 1; i++) {

            ScriptBuilder builder1 = new ScriptBuilder();
            builder1.emitAppCall(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x01},true);
            builder1.emitPush(new BigInteger("5"));
            byte[] testScript1 = builder1.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default, new IScriptTable() {
                @Override
                public byte[] getScript(byte[] script_hash) {
                    ScriptBuilder builder2 = new ScriptBuilder();
                    builder2.emitPush(new BigInteger("2"));
                    byte[] testScript2 = builder2.toArray();
                    return testScript2;
                }
            }, new IInteropService() {
                @Override
                public boolean invoke(byte[] method, ExecutionEngine engine) {
                    return true;
                }
            });
            engine.loadScript(testScript1);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1,engine.resultStack.getCount());
            Assert.assertEquals(2,engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
        //CALL_I
        for (int i = 0; i < 1; i++) {

            ScriptBuilder builder1 = new ScriptBuilder();
            builder1.emit(OpCode.CALL_I);//指针偏移位+3（JMP）

            builder1.emit(OpCode.PUSHBYTES1);//指针偏移位+3（JMP）
            builder1.emit(OpCode.PUSH0);//指针偏移位+3（JMP）
            builder1.emit(OpCode.PUSHBYTES6);//指针偏移位+3（JMP）
            builder1.emit(OpCode.PUSH0);//指针偏移位+3（JMP）

            builder1.emit(OpCode.PUSH3);
            builder1.emit(OpCode.ADD);
            builder1.emit(OpCode.RET);
            builder1.emit(OpCode.PUSH2);
            builder1.emit(OpCode.PUSH1);
            builder1.emit(OpCode.MAX);
            builder1.emit(OpCode.RET);
            byte[] testScript1 = builder1.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default);
            engine.loadScript(testScript1);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            engine.dispose();
        }

        //CALL_E
        for (int i = 0; i < 1; i++) {

            ScriptBuilder builder1 = new ScriptBuilder();
            builder1.emit(OpCode.CALL_E);//指针偏移位+3（JMP）

            builder1.emit(OpCode.PUSHBYTES1);//rvcount
            builder1.emit(OpCode.PUSH0);//pvcount

            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSHBYTES1);//20hash


            builder1.emit(OpCode.PUSH3);
            builder1.emit(OpCode.ADD);
            builder1.emit(OpCode.RET);
            byte[] testScript1 = builder1.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default, new IScriptTable() {
                @Override
                public byte[] getScript(byte[] script_hash) {
                    ScriptBuilder builder2 = new ScriptBuilder();
                    builder2.emitPush(new BigInteger("2"));
                    byte[] testScript2 = builder2.toArray();
                    return testScript2;
                }
            }, new IInteropService() {
                @Override
                public boolean invoke(byte[] method, ExecutionEngine engine) {
                    return true;
                }
            });
            engine.loadScript(testScript1);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1,engine.resultStack.getCount());
            Assert.assertEquals(5,engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
            engine.dispose();
        }
        //CALL_ED
        for (int i = 0; i < 1; i++) {

            ScriptBuilder builder1 = new ScriptBuilder();
            builder1.emitPush(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x01});//指针偏移位+3（JMP）
            builder1.emit(OpCode.CALL_ED);//指针偏移位+3（JMP）

            builder1.emit(OpCode.PUSHBYTES1);//rvcount
            builder1.emit(OpCode.PUSH0);//pvcount
            builder1.emit(OpCode.PUSH3);
            builder1.emit(OpCode.ADD);
            builder1.emit(OpCode.RET);
            byte[] testScript1 = builder1.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default, new IScriptTable() {
                @Override
                public byte[] getScript(byte[] script_hash) {
                    ScriptBuilder builder2 = new ScriptBuilder();
                    builder2.emitPush(new BigInteger("2"));
                    byte[] testScript2 = builder2.toArray();
                    return testScript2;
                }
            }, new IInteropService() {
                @Override
                public boolean invoke(byte[] method, ExecutionEngine engine) {
                    return true;
                }
            });
            engine.loadScript(testScript1);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1,engine.resultStack.getCount());
            Assert.assertEquals(5,engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
            engine.dispose();
        }
        //CALL_ET
        for (int i = 0; i < 1; i++) {

            ScriptBuilder builder1 = new ScriptBuilder();
            builder1.emit(OpCode.CALL_ET);//指针偏移位+3（JMP）

            builder1.emit(OpCode.PUSHBYTES1);//rvcount
            builder1.emit(OpCode.PUSH0);//pvcount

            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSHBYTES1);//20hash


            builder1.emit(OpCode.PUSH3);
            builder1.emit(OpCode.ADD);
            builder1.emit(OpCode.RET);
            byte[] testScript1 = builder1.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default, new IScriptTable() {
                @Override
                public byte[] getScript(byte[] script_hash) {
                    ScriptBuilder builder2 = new ScriptBuilder();
                    builder2.emitPush(new BigInteger("2"));
                    byte[] testScript2 = builder2.toArray();
                    return testScript2;
                }
            }, new IInteropService() {
                @Override
                public boolean invoke(byte[] method, ExecutionEngine engine) {
                    return true;
                }
            });
            engine.loadScript(testScript1,1);

            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1,engine.resultStack.getCount());
            Assert.assertEquals(2,engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }

        //CALL_EDT
        for (int i = 0; i < 1; i++) {

            ScriptBuilder builder1 = new ScriptBuilder();
            builder1.emitPush(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x01});//指针偏移位+3（JMP）
            builder1.emit(OpCode.CALL_EDT);//指针偏移位+3（JMP）

            builder1.emit(OpCode.PUSHBYTES1);//rvcount
            builder1.emit(OpCode.PUSH0);//pvcount
            builder1.emit(OpCode.PUSH3);
            builder1.emit(OpCode.ADD);
            builder1.emit(OpCode.RET);
            byte[] testScript1 = builder1.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default, new IScriptTable() {
                @Override
                public byte[] getScript(byte[] script_hash) {
                    ScriptBuilder builder2 = new ScriptBuilder();
                    builder2.emitPush(new BigInteger("2"));
                    byte[] testScript2 = builder2.toArray();
                    return testScript2;
                }
            }, new IInteropService() {
                @Override
                public boolean invoke(byte[] method, ExecutionEngine engine) {
                    return true;
                }
            });
            engine.loadScript(testScript1,1);
            engine.execute();
            Assert.assertEquals(VMState.HALT, engine.state);
            Assert.assertEquals(1,engine.resultStack.getCount());
            Assert.assertEquals(2,engine.resultStack.pop().getBigInteger().intValue());
            engine.dispose();
        }
    }

    @Test
    public void removeBreakPoint() throws Exception {
        ScriptBuilder builder = new ScriptBuilder();
        builder.emit(OpCode.PUSH1);
        builder.emit(OpCode.PUSH2);
        builder.emit(OpCode.PUSH3);
        builder.emit(OpCode.PUSH4);
        byte[] testScript = builder.toArray();
        ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default, new IScriptTable() {
            @Override
            public byte[] getScript(byte[] script_hash) {
                ScriptBuilder builder2 = new ScriptBuilder();
                builder2.emitPush(new BigInteger("2"));
                byte[] testScript2 = builder2.toArray();
                return testScript2;
            }
        }, new IInteropService() {
            @Override
            public boolean invoke(byte[] method, ExecutionEngine engine) {
                return true;
            }
        });
        engine.loadScript(testScript);
        engine.addBreakPoint(engine.getCurrentContext().getScriptHash() ,new Uint(2));
        engine.removeBreakPoint(engine.getCurrentContext().getScriptHash(),new Uint(2));
        engine.execute();
        Assert.assertEquals(VMState.HALT, engine.state);
        Assert.assertEquals(4, engine.resultStack.getCount());
        engine.dispose();
    }

    @Test
    public void stepOut() throws Exception {
        ScriptBuilder builder = new ScriptBuilder();
        builder.emit(OpCode.PUSH1);
        builder.emit(OpCode.PUSH2);
        builder.emit(OpCode.PUSH3);
        builder.emit(OpCode.PUSH4);
        byte[] testScript = builder.toArray();
        ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default, new IScriptTable() {
            @Override
            public byte[] getScript(byte[] script_hash) {
                ScriptBuilder builder2 = new ScriptBuilder();
                builder2.emitPush(new BigInteger("2"));
                byte[] testScript2 = builder2.toArray();
                return testScript2;
            }
        }, new IInteropService() {
            @Override
            public boolean invoke(byte[] method, ExecutionEngine engine) {
                return true;
            }
        });
        engine.loadScript(testScript);
        engine.addBreakPoint(engine.getCurrentContext().getScriptHash() ,new Uint(2));
        engine.execute();
        engine.stepOut();
        Assert.assertEquals(VMState.HALT, engine.state);
        Assert.assertEquals(4, engine.resultStack.getCount());
        engine.dispose();
    }

    @Test
    public void stepOver() throws Exception {
//CALL_E
        for (int i = 0; i < 1; i++) {

            ScriptBuilder builder1 = new ScriptBuilder();
            builder1.emit(OpCode.PUSH0);//指针偏移位+3（JMP）
            builder1.emit(OpCode.CALL_E);//指针偏移位+3（JMP）

            builder1.emit(OpCode.PUSHBYTES1);//rvcount
            builder1.emit(OpCode.PUSH0);//pvcount

            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSH0);//20hash
            builder1.emit(OpCode.PUSHBYTES1);//20hash


            builder1.emit(OpCode.PUSH3);
            builder1.emit(OpCode.ADD);
            builder1.emit(OpCode.RET);
            byte[] testScript1 = builder1.toArray();
            ExecutionEngine engine = new ExecutionEngine(null, Crypto.Default, new IScriptTable() {
                @Override
                public byte[] getScript(byte[] script_hash) {
                    ScriptBuilder builder2 = new ScriptBuilder();
                    builder2.emitPush(new BigInteger("2"));
                    byte[] testScript2 = builder2.toArray();
                    return testScript2;
                }
            }, new IInteropService() {
                @Override
                public boolean invoke(byte[] method, ExecutionEngine engine) {
                    return true;
                }
            });
            engine.loadScript(testScript1);
            engine.addBreakPoint(engine.getCurrentContext().getScriptHash(),new Uint(1));
            engine.execute();
            engine.stepOver();
            Assert.assertEquals(VMState.NONE, engine.state);
            Assert.assertEquals(1,engine.invocationStack.getCount());
            Assert.assertEquals(2,engine.getCurrentContext().getEvaluationStack().pop()
                    .getBigInteger().intValue());
            Assert.assertEquals(0,engine.getCurrentContext().getEvaluationStack().pop()
                    .getBigInteger().intValue());
            engine.dispose();
        }
    }

}