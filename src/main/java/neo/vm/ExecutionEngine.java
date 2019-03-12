package neo.vm;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import neo.csharp.Out;
import neo.csharp.Uint;
import neo.csharp.common.IDisposable;
import neo.log.notr.TR;
import neo.vm.Types.Array;
import neo.vm.Types.Boolean;
import neo.vm.Types.Integer;
import neo.vm.Types.Struct;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: ExecutionEngine
 * @Package neo.vm
 * @Description: VM 基础执行引擎
 * @date Created in 15:21 2019/2/28
 */
public class ExecutionEngine implements IDisposable{

    //外部可供调用脚本容器表
    private IScriptTable table;
    //private Map<byte[], HashSet<Uint>> break_points = new HashMap<byte[], HashSet<Uint>>();
    //脚本执行的断点记录表
    private Map<CustomBreakPointKey, HashSet<Uint>> break_points = new HashMap<CustomBreakPointKey, HashSet<Uint>>();

    //外部可供调用脚本容器
    public IScriptContainer scriptContainer;

    //外部可供调用的算法接口
    public ICrypto crypto;

    //外部可供调用的互操作服务
    public IInteropService service;

    //调用栈
    public RandomAccessStack<ExecutionContext> invocationStack = new RandomAccessStack<ExecutionContext>();

    //结果栈
    public RandomAccessStack<StackItem> resultStack = new RandomAccessStack<StackItem>();

    //获取当前上下文
    public ExecutionContext getCurrentContext() {
        TR.enter();
        return TR.exit(invocationStack.peek());
    }

    //获取调用者的上下文
    public ExecutionContext getCallingContext() {
        TR.enter();
        return TR.exit(invocationStack.getCount() > 1 ? invocationStack.peek(1) : null);
    }

    //获取最初的上下文
    public ExecutionContext getEntryContext() {
        TR.enter();
        return TR.exit(invocationStack.peek(invocationStack.getCount() - 1));
    }

    //VM状态
    public VMState state = VMState.BREAK;

    /**
     * @Author:doubi.liu
     * @description:构造函数
     * @date:2019/3/1
     */
    public ExecutionEngine(IScriptContainer container, ICrypto crypto) {
        TR.enter();
        if (crypto == null) {
            throw TR.exit(new RuntimeException("ICrypto 算法接口不能为空"));
        }
        this.scriptContainer = container;
        this.crypto = crypto;
        this.table = null;
        this.service = null;
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description:构造函数
     * @date:2019/3/1
     */
    public ExecutionEngine(IScriptContainer container, ICrypto crypto, IScriptTable table, IInteropService service) {
        TR.enter();
        this.scriptContainer = container;
        this.crypto = crypto;
        this.table = table;
        this.service = service;
        TR.exit();
    }

    /**
     * @param script_hash 脚本的哈希 position 断点执行位置
     * @Author:doubi.liu
     * @description:加断点
     * @date:2019/3/11
     */
    public void addBreakPoint(byte[] script_hash, Uint position) {
        TR.enter();
        HashSet<Uint> hashset = break_points.get(new CustomBreakPointKey(script_hash));
        if (hashset == null) {
            hashset = new HashSet<Uint>();
            break_points.put(new CustomBreakPointKey(script_hash), hashset);
        }
        hashset.add(position);
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description:释放资源
     * @date:2019/3/1
     */
    @Override
    public void dispose() {
        TR.enter();
        while (invocationStack.getCount() > 0) {
            invocationStack.pop().dispose();
        }
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description:VM 执行脚本
     * @date:2019/3/11
     */
    public void execute() {
        TR.enter();
       /* state &= ~VMState.BREAK;*/
        int temp = state.getState();
        temp &= ~(VMState.BREAK.getState());
        state = VMState.fromByte((byte) temp);
        while (!state.hasFlag(VMState.HALT) && !state.hasFlag(VMState.FAULT) && !state.hasFlag
                (VMState.BREAK)) {
            stepInto();
        }
        TR.exit();
    }

    /**
     * @param opcode OpCode context 上下文
     * @Author:doubi.liu
     * @description:执行OpCode对应的逻辑
     * @date:2019/3/11
     */
    private void executeOp(OpCode opcode, ExecutionContext context) {
        TR.enter();
        if ((opcode.getCode() >= OpCode.PUSHBYTES1.getCode()) && (opcode.getCode() <= OpCode
                .PUSHBYTES75.getCode())) {
            context.getEvaluationStack().push(StackItem.getStackItem(context.opReader.readFully(opcode.getCode
                    ())));
        } else {
            int tempType = 0;
            switch (opcode) {
                // Push value
                case PUSH0:
                    context.getEvaluationStack().push(StackItem.getStackItem(new byte[0]));
                    break;
                case PUSHDATA1:
                    context.getEvaluationStack().push(StackItem.getStackItem(context.opReader
                            .readFully(context.opReader.readByte())));
                    break;
                case PUSHDATA2:
                    context.getEvaluationStack().push(StackItem.getStackItem(context.opReader
                            .readFully(context.opReader.readShort())));
                    break;
                case PUSHDATA4:
                    context.getEvaluationStack().push(StackItem.getStackItem(context.opReader
                            .readFully(context.opReader.readInt())));
                    break;
                case PUSHM1:
                case PUSHT:
                case PUSH1:
                case PUSH2:
                case PUSH3:
                case PUSH4:
                case PUSH5:
                case PUSH6:
                case PUSH7:
                case PUSH8:
                case PUSH9:
                case PUSH10:
                case PUSH11:
                case PUSH12:
                case PUSH13:
                case PUSH14:
                case PUSH15:
                case PUSH16:
                    context.getEvaluationStack().push(StackItem.getStackItem(StackItem.getStackItem(opcode.getCode()
                            - OpCode.PUSH1.getCode() + 1)));
                    break;

                // Control
                case NOP:
                    break;
                case JMP:
                case JMPIF:
                case JMPIFNOT: {
                    int offset = context.opReader.readShort();
                    offset = context.getInstructionPointer() + offset - 3;
                    if (offset < 0 || offset > context.script.getLength()) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    boolean fValue = true;
                    if (opcode.getCode() > OpCode.JMP.getCode()) {
                        fValue = context.getEvaluationStack().pop().getBoolean();
                        if (opcode == OpCode.JMPIFNOT) {
                            fValue = !fValue;
                        }
                    }
                    if (fValue) {
                        context.setInstructionPointer(offset);
                    }
                }
                break;
                case CALL: {
                    ExecutionContext context_call = loadScript(context.script);
                    context.getEvaluationStack().copyTo(context_call.getEvaluationStack());
                    context_call.setInstructionPointer(context.getInstructionPointer());
                    context.getEvaluationStack().clear();
                    context.setInstructionPointer(context.getInstructionPointer() + 2);
                    executeOp(OpCode.JMP, context_call);
                }
                break;
                case RET:
                    ExecutionContext context_pop = null;
                    try {
                        context_pop = invocationStack.pop();
                        int rvcount = context_pop.RVCount;
                        if (rvcount == -1) {
                            rvcount = context_pop.getEvaluationStack().getCount();
                        }
                        if (rvcount > 0) {
                            if (context_pop.getEvaluationStack().getCount() < rvcount) {
                                tempType = state.getState();
                                tempType |= VMState.FAULT.getState();
                                state = VMState.fromByte((byte) tempType);
                                TR.exit();
                                return;
                            }
                            RandomAccessStack<StackItem> stack_eval;
                            if (invocationStack.getCount() == 0) {
                                stack_eval = resultStack;
                            } else {
                                stack_eval = getCurrentContext().getEvaluationStack();
                            }
                            context_pop.getEvaluationStack().copyTo(stack_eval, rvcount);
                        }
                        if (context_pop.RVCount == -1 && invocationStack.getCount() > 0) {
                            context_pop.getAltStack().copyTo(getCurrentContext().getAltStack());
                        }
                    } finally {
                        context_pop.dispose();
                    }
                    if (invocationStack.getCount() == 0) {
                        /*state |= VMState.HALT.getState();*/
                        tempType = state.getState();
                        tempType |= VMState.HALT.getState();
                        state = VMState.fromByte((byte) tempType);
                    }
                    break;
                case APPCALL:
                case TAILCALL: {
                    byte[] script_hash = context.opReader.readFully(20);
                    if (Arrays.asList(script_hash).stream().allMatch(p -> p.equals(0))) {
                        script_hash = context.getEvaluationStack().pop().getByteArray();
                    }

                    ExecutionContext context_new = loadScriptByHash(script_hash);
                    if (context_new == null) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }

                    context.getEvaluationStack().copyTo(context_new.getEvaluationStack());
                    if (opcode == OpCode.TAILCALL) {
                        invocationStack.remove(1).dispose();
                    } else {
                        context.getEvaluationStack().clear();
                    }
                }
                break;
                case SYSCALL:
                    if ((service != null) && (service.invoke(context.opReader.readVarBytes(252), this)
                            != true)) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                    }
                    break;

                // Stack ops
                case DUPFROMALTSTACK:
                    context.getEvaluationStack().push(context.getAltStack().peek());
                    break;
                case TOALTSTACK:
                    context.getAltStack().push(context.getEvaluationStack().pop());
                    break;
                case FROMALTSTACK:
                    context.getEvaluationStack().push(context.getAltStack().pop());
                    break;
                case XDROP: {
                    int n = context.getEvaluationStack().pop().getBigInteger().intValue();
                    if (n < 0) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    context.getEvaluationStack().remove(n);
                }
                break;
                case XSWAP: {
                    int n = context.getEvaluationStack().pop().getBigInteger().intValue();
                    if (n < 0) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    if (n == 0) {
                        break;
                    }
                    StackItem xn = context.getEvaluationStack().peek(n);
                    context.getEvaluationStack().set(n, context.getEvaluationStack().peek());
                    context.getEvaluationStack().set(0, xn);
                }
                break;
                case XTUCK: {
                    int n = context.getEvaluationStack().pop().getBigInteger().intValue();
                    if (n <= 0) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    context.getEvaluationStack().insert(n, context.getEvaluationStack().peek());
                }
                break;
                case DEPTH:
                    context.getEvaluationStack().push(new Integer(context.getEvaluationStack().getCount
                            ()));
                    break;
                case DROP:
                    context.getEvaluationStack().pop();
                    break;
                case DUP:
                    context.getEvaluationStack().push(context.getEvaluationStack().peek());
                    break;
                case NIP:
                    context.getEvaluationStack().remove(1);
                    break;
                case OVER:
                    context.getEvaluationStack().push(context.getEvaluationStack().peek(1));
                    break;
                case PICK: {
                    int n = context.getEvaluationStack().pop().getBigInteger().intValue();
                    if (n < 0) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    context.getEvaluationStack().push(context.getEvaluationStack().peek(n));
                }
                break;
                case ROLL: {
                    int n = context.getEvaluationStack().pop().getBigInteger().intValue();
                    if (n < 0) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    if (n == 0) {
                        break;
                    }
                    context.getEvaluationStack().push(context.getEvaluationStack().remove(n));
                }
                break;
                case ROT:
                    context.getEvaluationStack().push(context.getEvaluationStack().remove(2));
                    break;
                case SWAP:
                    context.getEvaluationStack().push(context.getEvaluationStack().remove(1));
                    break;
                case TUCK:
                    context.getEvaluationStack().insert(2, context.getEvaluationStack().peek());
                    break;
                case CAT: {
                    //x1.concat(x2)
                    byte[] x2 = context.getEvaluationStack().pop().getByteArray();
                    byte[] x1 = context.getEvaluationStack().pop().getByteArray();

                    byte[] bt3 = new byte[x1.length + x2.length];
                    System.arraycopy(x1, 0, bt3, 0, x1.length);
                    System.arraycopy(x2, 0, bt3, x1.length, x2.length);
                    context.getEvaluationStack().push(StackItem.getStackItem(bt3));

                }
                break;
                case SUBSTR: {
                    int count = context.getEvaluationStack().pop().getBigInteger().intValue();
                    if (count < 0) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    int index = context.getEvaluationStack().pop().getBigInteger().intValue();
                    if (index < 0) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    byte[] x = context.getEvaluationStack().pop().getByteArray();
                    byte[] temptest = new byte[count];
                    System.arraycopy(x, index, temptest, 0, count);
                    context.getEvaluationStack().push(StackItem.getStackItem(temptest));
                }
                break;
                case LEFT: {
                    int count = context.getEvaluationStack().pop().getBigInteger().intValue();
                    if (count < 0) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    byte[] x = context.getEvaluationStack().pop().getByteArray();
                    byte[] temptest = new byte[count];
                    System.arraycopy(x, 0, temptest, 0, count);
                    context.getEvaluationStack().push(StackItem.getStackItem(temptest));
                }
                break;
                case RIGHT: {
                    int count = context.getEvaluationStack().pop().getBigInteger().intValue();
                    if (count < 0) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    byte[] x = context.getEvaluationStack().pop().getByteArray();
                    if (x.length < count) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        return;
                    }
/*                    Byte[] temparray = Arrays.asList(x).stream().skip(x.length -
                            count).toArray(Byte[]::new);*/
                    byte[] temptest = new byte[count];
                    System.arraycopy(x, x.length - count, temptest, 0, temptest.length);
                    context.getEvaluationStack().push(StackItem.getStackItem(temptest));
                }
                break;
                case SIZE: {
                    byte[] x = context.getEvaluationStack().pop().getByteArray();
                    context.getEvaluationStack().push(StackItem.getStackItem(StackItem
                            .getStackItem(x.length)));
                }
                break;

                // Bitwise logic
                case INVERT: {
                    //~x;
                    BigInteger x = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x.not()));
                }
                break;
                case AND: {
                    //&
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.and(x2)));
                }
                break;
                case OR: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.or(x2)));
                }
                break;
                case XOR: {
                    //^
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.xor(x2)));
                }
                break;
                case EQUAL: {
                    StackItem x2 = context.getEvaluationStack().pop();
                    StackItem x1 = context.getEvaluationStack().pop();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.equals(x2)));
                }
                break;

                // Numeric
                case INC: {
                    BigInteger x = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x.add(new BigInteger
                            ("1"))));
                }
                break;
                case DEC: {
                    BigInteger x = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x.subtract(new BigInteger
                            ("1"))));
                }
                break;
                case SIGN: {
                    BigInteger x = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(StackItem
                            .getStackItem(x.signum())));
                }
                break;
                case NEGATE: {
                    //-x
                    BigInteger x = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x.negate()));
                }
                break;
                case ABS: {
                    BigInteger x = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x.abs()));
                }
                break;
                case NOT: {
                    boolean x = context.getEvaluationStack().pop().getBoolean();
                    context.getEvaluationStack().push(StackItem.getStackItem(!x));
                }
                break;
                case NZ: {
                    BigInteger x = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(!x.equals(BigInteger
                            .ZERO)));
                }
                break;
                case ADD: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.add(x2)));
                }
                break;
                case SUB: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.subtract(x2)));
                }
                break;
                case MUL: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.multiply(x2)));
                }
                break;
                case DIV: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.divide(x2)));
                }
                break;
                case MOD: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.mod(x2)));
                }
                break;
                case SHL: {
                    //左移
                    int n = context.getEvaluationStack().pop().getBigInteger().intValue();
                    BigInteger x = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x.shiftLeft(n)));
                }
                break;
                case SHR: {
                    //右移
                    int n = context.getEvaluationStack().pop().getBigInteger().intValue();
                    BigInteger x = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x.shiftRight(n)));
                }
                break;
                case BOOLAND: {
                    boolean x2 = context.getEvaluationStack().pop().getBoolean();
                    boolean x1 = context.getEvaluationStack().pop().getBoolean();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1 && x2));
                }
                break;
                case BOOLOR: {
                    boolean x2 = context.getEvaluationStack().pop().getBoolean();
                    boolean x1 = context.getEvaluationStack().pop().getBoolean();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1 || x2));
                }
                break;
                case NUMEQUAL: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.equals(x2)));
                }
                break;
                case NUMNOTEQUAL: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(!x1.equals(x2)));
                }
                break;
                case LT: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.compareTo(x2) < 0));
                }
                break;
                case GT: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.compareTo(x2) > 0));
                }
                break;
                case LTE: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.compareTo(x2) <= 0));
                }
                break;
                case GTE: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.compareTo(x2) >= 0));
                }
                break;
                case MIN: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.min(x2)));
                }
                break;
                case MAX: {
                    BigInteger x2 = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x1 = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(x1.max(x2)));
                }
                break;
                case WITHIN: {
                    //a <= x && x < b
                    BigInteger b = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger a = context.getEvaluationStack().pop().getBigInteger();
                    BigInteger x = context.getEvaluationStack().pop().getBigInteger();
                    context.getEvaluationStack().push(StackItem.getStackItem(a.compareTo(x) <= 0
                            && x.compareTo(b) < 0));
                }
                break;

                // Crypto
                case SHA1:
                    try {
                        byte[] x = context.getEvaluationStack().pop().getByteArray();
                        MessageDigest digest = MessageDigest.getInstance("SHA-1");
                        digest.update(x);
                        context.getEvaluationStack().push(StackItem.getStackItem(digest.digest()));
                    } catch (NoSuchAlgorithmException e) {
                        TR.error(e.getMessage());
                        throw TR.exit(new RuntimeException(e));
                    }
                    break;
                case SHA256:
                    try {
                        byte[] x = context.getEvaluationStack().pop().getByteArray();
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        digest.update(x);
                        context.getEvaluationStack().push(StackItem.getStackItem(digest.digest()));
                    } catch (NoSuchAlgorithmException e) {
                        TR.error(e.getMessage());
                        throw TR.exit(new RuntimeException(e));
                    }
                    break;
                case HASH160: {
                    byte[] x = context.getEvaluationStack().pop().getByteArray();
                    context.getEvaluationStack().push(StackItem.getStackItem(crypto.hash160(x)));
                }
                break;
                case HASH256: {
                    byte[] x = context.getEvaluationStack().pop().getByteArray();
                    context.getEvaluationStack().push(StackItem.getStackItem(crypto.hash256(x)));
                }
                break;
                case CHECKSIG: {
                    byte[] pubkey = context.getEvaluationStack().pop().getByteArray();
                    byte[] signature = context.getEvaluationStack().pop().getByteArray();
                    try {
                        context.getEvaluationStack().push(StackItem.getStackItem(crypto.verifySignature
                                (scriptContainer.getMessage(), signature, pubkey)));
                    } catch (IllegalArgumentException e) {
                        context.getEvaluationStack().push(StackItem.getStackItem(false));
                    }
                }
                break;
                case VERIFY: {
                    byte[] pubkey = context.getEvaluationStack().pop().getByteArray();
                    byte[] signature = context.getEvaluationStack().pop().getByteArray();
                    byte[] message = context.getEvaluationStack().pop().getByteArray();
                    try {
                        context.getEvaluationStack().push(StackItem.getStackItem(crypto.verifySignature
                                (message, signature, pubkey)));
                    } catch (IllegalArgumentException e) {
                        context.getEvaluationStack().push(new Boolean(false));
                    }
                }
                break;
                case CHECKMULTISIG: {
                    int n;
                    byte[][] pubkeys = new byte[0][];
                    StackItem item = context.getEvaluationStack().pop();
                    if (item instanceof Array) {
                        //LINQ START
                        // pubkeys = array1.Select(p => p.GetByteArray()).ToArray();
                        List<byte[]> list = new ArrayList<>();
                        ((Array) item).forEach(i -> list.add(i.getByteArray()));
                        pubkeys = list.toArray(pubkeys);
                        //LINQ END
                        n = pubkeys.length;
                        if (n == 0) {
                            tempType = state.getState();
                            tempType |= VMState.FAULT.getState();
                            state = VMState.fromByte((byte) tempType);
                            TR.exit();
                            return;
                        }
                    } else {
                        n = item.getBigInteger().intValue();
                        if (n < 1 || n > context.getEvaluationStack().getCount()) {
                            tempType = state.getState();
                            tempType |= VMState.FAULT.getState();
                            state = VMState.fromByte((byte) tempType);
                            TR.exit();
                            return;
                        }
                        pubkeys = new byte[n][];
                        for (int i = 0; i < n; i++) {
                            pubkeys[i] = context.getEvaluationStack().pop().getByteArray();
                        }
                    }
                    int m;
                    byte[][] signatures = new byte[0][];
                    item = context.getEvaluationStack().pop();
                    if (item instanceof Array) {
                        //LINQ START
                        //signatures = array2.Select(p => p.GetByteArray()).ToArray();
                        List<byte[]> list = new ArrayList<>();
                        ((Array) item).forEach(i -> list.add(i.getByteArray()));
                        signatures = list.toArray(signatures);
                        //LINQ END
                        m = signatures.length;
                        if (m == 0 || m > n) {
                            tempType = state.getState();
                            tempType |= VMState.FAULT.getState();
                            state = VMState.fromByte((byte) tempType);
                            TR.exit();
                            return;
                        }
                    } else {
                        m = item.getBigInteger().intValue();
                        if (m < 1 || m > n || m > context.getEvaluationStack().getCount()) {
                            tempType = state.getState();
                            tempType |= VMState.FAULT.getState();
                            state = VMState.fromByte((byte) tempType);
                            TR.exit();
                            return;
                        }
                        signatures = new byte[m][];
                        for (int i = 0; i < m; i++) {
                            signatures[i] = context.getEvaluationStack().pop().getByteArray();
                        }
                    }
                    byte[] message = scriptContainer.getMessage();
                    boolean fSuccess = true;
                    try {
                        for (int i = 0, j = 0; fSuccess && i < m && j < n; ) {
                            if (crypto.verifySignature(message, signatures[i], pubkeys[j])) {
                                i++;
                            }
                            j++;
                            if (m - i > n - j) {
                                fSuccess = false;
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        fSuccess = false;
                    }
                    context.getEvaluationStack().push(StackItem.getStackItem(fSuccess));
                }
                break;

                // Array
                case ARRAYSIZE: {
                    StackItem item = context.getEvaluationStack().pop();
                    if (item instanceof ICollection) {
                        context.getEvaluationStack().push(StackItem.getStackItem(StackItem
                                .getStackItem(((ICollection) item).getCount())));
                    } else {
                        context.getEvaluationStack().push(StackItem.getStackItem(StackItem
                                .getStackItem(item.getByteArray().length)));
                    }
                }
                break;
                case PACK: {
                    int size = context.getEvaluationStack().pop().getBigInteger().intValue();
                    if (size < 0 || size > context.getEvaluationStack().getCount()) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    List<StackItem> items = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        items.add(context.getEvaluationStack().pop());
                    }
                    context.getEvaluationStack().push(new Array(items));
                }
                break;
                case UNPACK: {
                    StackItem item = context.getEvaluationStack().pop();
                    if (item instanceof Array) {
                        for (int i = ((Array) item).getCount() - 1; i >= 0; i--) {
                            context.getEvaluationStack().push(((Array) item).getArrayItem
                                    (i));
                        }
                        context.getEvaluationStack().push(StackItem.getStackItem(StackItem
                                .getStackItem(((Array) item).getCount())));
                    } else {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                }
                break;
                case PICKITEM: {
                    StackItem key = context.getEvaluationStack().pop();
                    if (key instanceof ICollection) {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    StackItem temptest = context.getEvaluationStack().pop();
                    if (temptest instanceof Array) {
                        int index = key.getBigInteger().intValue();
                        if (index < 0 || index >= ((Array) temptest).getCount()) {
                            tempType = state.getState();
                            tempType |= VMState.FAULT.getState();
                            state = VMState.fromByte((byte) tempType);
                            TR.exit();
                            return;
                        }
                        context.getEvaluationStack().push(((Array) temptest).getArrayItem(index));
                    } else if (temptest instanceof neo.vm.Types.Map) {

                        Out<StackItem> out = new Out<>();
                        if ((((neo.vm.Types.Map) temptest).tryGetValue(key, out))) {
                            context.getEvaluationStack().push(out.get());
                        } else {
                            tempType = state.getState();
                            tempType |= VMState.FAULT.getState();
                            state = VMState.fromByte((byte) tempType);
                            TR.exit();
                            return;
                        }
                    } else {
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                }
                break;
                case SETITEM: {
                    StackItem value = context.getEvaluationStack().pop();
                    if (value instanceof Struct) {
                        value = ((Struct) value).clone();
                    }
                    StackItem key = context.getEvaluationStack().pop();
                    if (key instanceof ICollection) {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }

                    StackItem temptest = context.getEvaluationStack().pop();
                    if (temptest instanceof Array) {
                        int index = key.getBigInteger().intValue();
                        if (index < 0 || index >= ((Array) temptest).getCount()) {
                            tempType = state.getState();
                            tempType |= VMState.FAULT.getState();
                            state = VMState.fromByte((byte) tempType);
                            TR.exit();
                            return;
                        }
                        ((Array) temptest).setArrayItem(index, value);
                    } else if (temptest instanceof Map) {
                        ((neo.vm.Types.Map) temptest).setMapItem(key, value);
                    } else {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                }
                break;
                case NEWARRAY: {
                    int count = context.getEvaluationStack().pop().getBigInteger().intValue();
                    List<StackItem> items = new ArrayList<StackItem>(count);
                    for (int i = 0; i < count; i++) {
                        items.add(new Boolean(false));
                    }
                    context.getEvaluationStack().push(new Array(items));
                }
                break;
                case NEWSTRUCT: {
                    int count = context.getEvaluationStack().pop().getBigInteger().intValue();
                    List<StackItem> items = new ArrayList<StackItem>(count);
                    for (int i = 0; i < count; i++) {
                        items.add(new Boolean(false));
                    }
                    context.getEvaluationStack().push(new Struct(items));
                }
                break;
                case NEWMAP:
                    context.getEvaluationStack().push(new neo.vm.Types.Map());
                    break;
                case APPEND: {
                    StackItem newItem = context.getEvaluationStack().pop();
                    if (newItem instanceof Struct) {
                        newItem = ((Struct) newItem).clone();
                    }
                    StackItem arrItem = context.getEvaluationStack().pop();
                    if (arrItem instanceof Array) {
                        ((Array) arrItem).add(newItem);
                    } else {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                }
                break;
                case REVERSE: {
                    StackItem arrItem = context.getEvaluationStack().pop();
                    if (arrItem instanceof Array) {
                        ((Array) arrItem).reverse();
                    } else {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                }
                break;
                case REMOVE: {
                    StackItem key = context.getEvaluationStack().pop();
                    if (key instanceof ICollection) {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    StackItem temptest = context.getEvaluationStack().pop();
                    if (temptest instanceof Array) {
                        int index = key.getBigInteger().intValue();
                        if (index < 0 || index >= ((Array) temptest).getCount()) {
                            tempType = state.getState();
                            tempType |= VMState.FAULT.getState();
                            state = VMState.fromByte((byte) tempType);
                            TR.exit();
                            return;
                        }
                        ((Array) temptest).removeAt(index);
                    } else if (temptest instanceof neo.vm.Types.Map) {
                        ((neo.vm.Types.Map) temptest).remove(key);
                    } else {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                }
                break;
                case HASKEY: {
                    StackItem key = context.getEvaluationStack().pop();
                    if (key instanceof ICollection) {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    StackItem temptest = context.getEvaluationStack().pop();
                    if (temptest instanceof Array) {
                        int index = key.getBigInteger().intValue();
                        if (index < 0) {
                            tempType = state.getState();
                            tempType |= VMState.FAULT.getState();
                            state = VMState.fromByte((byte) tempType);
                            TR.exit();
                            return;
                        }
                        context.getEvaluationStack().push(StackItem.getStackItem(index < ((Array)
                                temptest).getCount()));
                    } else if (temptest instanceof Map) {
                        context.getEvaluationStack().push(StackItem.getStackItem(((neo.vm.Types.Map) temptest)
                                .containsKey(key)));
                    } else {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                }
                break;
                case KEYS:
                    StackItem temptest = context.getEvaluationStack().pop();
                    if (temptest instanceof neo.vm.Types.Map) {
                        context.getEvaluationStack().push(new Array(((neo.vm.Types.Map) temptest)
                                .getKeys()));
                    } else {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    break;
                case VALUES: {
                    Collection<StackItem> values;
                    StackItem itemtemp = context.getEvaluationStack().pop();
                    if (itemtemp instanceof Array) {
                        values = new ArrayList<>();
                        ((Array) itemtemp).getEnumerator().forEach(i -> values.add(i));
                    } else if (itemtemp instanceof neo.vm.Types.Map) {
                        values = ((neo.vm.Types.Map) itemtemp).getValues();
                    } else {
                        /* State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }


                    List<StackItem> newArray = new ArrayList<StackItem>(values.size());
                    for (StackItem item : values)
                        if (item instanceof Struct) {
                            newArray.add(((Struct) item).clone());
                        } else {
                            newArray.add(item);
                        }
                    context.getEvaluationStack().push(StackItem.getStackItem(newArray));
                }
                break;

                // Stack isolation
                case CALL_I: {
                    int rvcount = context.opReader.readByte();
                    int pcount = context.opReader.readByte();
                    if (context.getEvaluationStack().getCount() < pcount) {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    ExecutionContext context_call = loadScript(context.script, rvcount);
                    context.getEvaluationStack().copyTo(context_call.getEvaluationStack(), pcount);
                    context_call.setInstructionPointer(context.getInstructionPointer());
                    for (int i = 0; i < pcount; i++) {
                        context.getEvaluationStack().pop();
                    }
                    context.setInstructionPointer(context.getInstructionPointer() + 2);
                    executeOp(OpCode.JMP, context_call);
                }
                break;
                case CALL_E:
                case CALL_ED:
                case CALL_ET:
                case CALL_EDT: {
                    int rvcount = context.opReader.readByte();
                    int pcount = context.opReader.readByte();
                    if (context.getEvaluationStack().getCount() < pcount) {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    if (opcode == OpCode.CALL_ET || opcode == OpCode.CALL_EDT) {
                        if (context.RVCount != rvcount) {
                            /*State |= VMState.FAULT;*/
                            tempType = state.getState();
                            tempType |= VMState.FAULT.getState();
                            state = VMState.fromByte((byte) tempType);
                            TR.exit();
                            return;
                        }
                    }
                    byte[] script_hash;
                    if (opcode == OpCode.CALL_ED || opcode == OpCode.CALL_EDT) {
                        script_hash = context.getEvaluationStack().pop().getByteArray();
                    } else {
                        script_hash = context.opReader.readFully(20);
                    }
                    ExecutionContext context_new = loadScriptByHash(script_hash, rvcount);
                    if (context_new == null) {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    context.getEvaluationStack().copyTo(context_new.getEvaluationStack(), pcount);
                    if (opcode == OpCode.CALL_ET || opcode == OpCode.CALL_EDT) {
                        invocationStack.remove(1).dispose();
                    } else {
                        for (int i = 0; i < pcount; i++) {
                            context.getEvaluationStack().pop();
                        }
                    }
                }
                break;

                // Exceptions
                case THROW:
                    /*State |= VMState.FAULT;*/
                    tempType = state.getState();
                    tempType |= VMState.FAULT.getState();
                    state = VMState.fromByte((byte) tempType);
                    TR.exit();
                    return;
                case THROWIFNOT:
                    if (!context.getEvaluationStack().pop().getBoolean()) {
                        /*State |= VMState.FAULT;*/
                        tempType = state.getState();
                        tempType |= VMState.FAULT.getState();
                        state = VMState.fromByte((byte) tempType);
                        TR.exit();
                        return;
                    }
                    break;

                default:
                    /*State |= VMState.FAULT;*/
                    tempType = state.getState();
                    tempType |= VMState.FAULT.getState();
                    state = VMState.fromByte((byte) tempType);
                    TR.exit();
                    return;
            }
        }
        if (!state.hasFlag(VMState.FAULT) && invocationStack.getCount() > 0) {
            HashSet<Uint> hashset = break_points.get(new CustomBreakPointKey(getCurrentContext()
                    .getScriptHash()));
            if (hashset != null && hashset.contains(new Uint(getCurrentContext()
                    .getInstructionPointer()))) {
                /*State |= VMState.BREAK;*/
                int tempType2 = 0;
                tempType2 = state.getState();
                tempType2 |= VMState.BREAK.getState();
                state = VMState.fromByte((byte) tempType2);
            }
        }
    }

    /**
     * @param script 脚本字节码 rvcount 返回值
     * @Author:doubi.liu
     * @description:VM 加载脚本字节码
     * @date:2019/3/11
     */
    public ExecutionContext loadScript(byte[] script, int rvcount) {
        TR.enter();
        ExecutionContext context = new ExecutionContext(new Script(crypto, script), rvcount);
        invocationStack.push(context);
        return TR.exit(context);
    }

    /**
     * @param script 脚本字节码
     * @Author:doubi.liu
     * @description:
     * @date:2019/3/11
     */
    public ExecutionContext loadScript(byte[] script) {
        TR.enter();
        ExecutionContext context = new ExecutionContext(new Script(crypto, script), -1);
        invocationStack.push(context);
        return TR.exit(context);
    }

    /**
     * @param script 脚本 rvcount 返回值个数
     * @Author:doubi.liu
     * @description:VM 加载脚本字节码
     * @date:2019/3/11
     */
    private ExecutionContext loadScript(Script script, int rvcount) {
        TR.enter();
        ExecutionContext context = new ExecutionContext(script, rvcount);
        invocationStack.push(context);
        return TR.exit(context);
    }

    /**
     * @param script 脚本
     * @Author:doubi.liu
     * @description:VM 加载脚本字节码
     * @date:2019/3/11
     */
    private ExecutionContext loadScript(Script script) {
        TR.enter();
        ExecutionContext context = new ExecutionContext(script, -1);
        invocationStack.push(context);
        return TR.exit(context);
    }

    /**
     * @param hash 脚本哈希 rvcount 返回值个数
     * @Author:doubi.liu
     * @description:VM 通过脚本哈希加载脚本字节码
     * @date:2019/3/11
     */
    private ExecutionContext loadScriptByHash(byte[] hash, int rvcount) {
        TR.enter();
        if (table == null) {
            return TR.exit(null);
        }
        byte[] script = table.getScript(hash);
        if (script == null) {
            return TR.exit(null);
        }
        ExecutionContext context = new ExecutionContext(new Script(hash, script), rvcount);
        invocationStack.push(context);
        return context;
    }

    /**
     * @param hash 脚本哈希
     * @Author:doubi.liu
     * @description:VM 通过脚本哈希加载脚本字节码
     * @date:2019/3/11
     */
    private ExecutionContext loadScriptByHash(byte[] hash) {
        TR.enter();
        if (table == null) {
            return TR.exit(null);
        }
        byte[] script = table.getScript(hash);
        if (script == null) {
            return TR.exit(null);
        }
        ExecutionContext context = new ExecutionContext(new Script(hash, script), -1);
        invocationStack.push(context);
        return TR.exit(context);
    }

    /**
     * @param script_hash 脚本哈希 position 断点位置
     * @Author:doubi.liu
     * @description:移除断点
     * @date:2019/3/11
     */
    public boolean removeBreakPoint(byte[] script_hash, Uint position) {
        TR.enter();
        HashSet<Uint> hashset = break_points.get(new CustomBreakPointKey(script_hash));
        if (hashset == null) {
            return TR.exit(false);
        }
        if (!hashset.remove(position)) {
            return TR.exit(false);
        }
        if (hashset.size() == 0) {
            break_points.remove(new CustomBreakPointKey(script_hash));
        }
        return TR.exit(true);
    }

    /**
     * @Author:doubi.liu
     * @description:步进方法，执行一步操作
     * @date:2019/3/11
     */
    public void stepInto() {
        TR.enter();
        if (invocationStack.getCount() == 0) {
            /*state |= VMState.HALT;*/
            int temp = state.getState();
            temp |= VMState.HALT.getState();
            state = VMState.fromByte((byte) temp);
        }
        if (state.hasFlag(VMState.HALT) || state.hasFlag(VMState.FAULT)) {
            TR.exit();
            return;
        }
        OpCode opcode = getCurrentContext().getInstructionPointer() >= getCurrentContext().script
                .getLength() ?
                OpCode.RET : (OpCode.fromByte((byte) getCurrentContext().opReader.readByte()));
        try {
            executeOp(opcode, getCurrentContext());
        } catch (Exception e) {
           /* State |= VMState.FAULT;*/
            int tempType = 0;
            tempType = state.getState();
            tempType |= VMState.FAULT.getState();
            state = VMState.fromByte((byte) tempType);
        }
        TR.exit();
    }

    /**
     * @Author:doubi.liu
     * @description:继续执行断点后的操作
     * @date:2019/3/11
     */
    public void stepOut() {
        TR.enter();
       /* state &= ~VMState.BREAK;*/
        int temp = state.getState();
        temp &= ~(VMState.BREAK.getState());
        state = VMState.fromByte((byte) temp);
        int c = invocationStack.getCount();
        while (!state.hasFlag(VMState.HALT) && !state.hasFlag(VMState.FAULT) && !state.hasFlag
                (VMState.BREAK) && invocationStack.getCount() >= c) {
            stepInto();
        }
    }

    /**
     * @Author:doubi.liu
     * @description:继续执行断点后的一次调用操作
     * @date:2019/3/11
     */
    public void stepOver() {
        TR.enter();
        if (state.hasFlag(VMState.HALT) || state.hasFlag(VMState.FAULT)) {
            TR.exit();
            return;
        }

        /*State &= ~VMState.BREAK;*/
        int temp = state.getState();
        temp &= ~(VMState.BREAK.getState());
        state = VMState.fromByte((byte) temp);
        int c = invocationStack.getCount();
        do {
            stepInto();
        }
        while (!state.hasFlag(VMState.HALT) && !state.hasFlag(VMState.FAULT) && !state.hasFlag
                (VMState.BREAK) && invocationStack.getCount() > c);
        TR.exit();
    }
}
