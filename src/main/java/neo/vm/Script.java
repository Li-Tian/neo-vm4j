package neo.vm;

import neo.csharp.io.BinaryReader;
import neo.csharp.io.MemoryStream;
import neo.log.notr.TR;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: Script
 * @Package neo.vm
 * @Description: 合约脚本
 * @date Created in 11:55 2019/2/28
 */
public class Script {
    //脚本哈希
    private byte[] _scriptHash = null;
    //脚本字节码
    private byte[] _value;
    //算法接口
    private ICrypto _crypto;

    /**
     * @Author:doubi.liu
     * @description:获取script hash
     * @date:2019/2/28
     */
    public byte[] getScriptHash() {
        TR.enter();
        if (_scriptHash == null) {
            _scriptHash = _crypto.hash160(_value);
        }
        return TR.exit(_scriptHash);
    }

    /**
      * @Author:doubi.liu
      * @description:获取脚本的字节数据
      * @param 
      * @date:2019/3/13
    */
    public byte[] getValue(){
        TR.enter();
        return TR.exit(_value);
    }

    /**
     * @Author:doubi.liu
     * @description:Script length
     * @date:2019/2/28
     */
    public int getLength() {
        TR.enter();
        return TR.exit(_value.length);
    }

    /**
      * @Author:doubi.liu
      * @description:获取Opcode
      * @param index 索引
      * @date:2019/2/28
    */
    public OpCode getOpcode(int index){
        TR.enter();
        return TR.exit(OpCode.fromByte(_value[index]));
    }

    /**
     * @Author:doubi.liu
     * @description:获取BinaryReader
     * @param
     * @date:2019/2/28
     */
    public BinaryReader getBinaryReader() {
        TR.enter();
        return TR.exit(new BinaryReader(new MemoryStream(_value)));
    }


    /**
     * @Author:doubi.liu
     * @description:获取MemoryStream
     * @param
     * @date:2019/2/28
     */
    public MemoryStream getMemoryStream() {
        TR.enter();
        return TR.exit(new MemoryStream(_value));
    }

    /**
     * @param crypto 算法 script 脚本
     * @Author:doubi.liu
     * @description:构造函数
     * @date:2019/2/28
     */
    public Script(ICrypto crypto, byte[] script) {
        TR.enter();
        _crypto = crypto;
        _value = script;
        TR.exit();
    }

    /**
      * @Author:doubi.liu
      * @description:构造函数
      * @param  hash 哈希  script 脚本
      * @date:2019/2/28
    */
    Script(byte[] hash, byte[] script) {
        TR.enter();
        _scriptHash = hash;
        _value = script;
        TR.exit();
    }
}
