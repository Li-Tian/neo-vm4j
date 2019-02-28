package neo.vm;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: IScriptTable
 * @Package neo.vm
 * @Description: 脚本表
 * @date Created in 17:11 2019/2/27
 */
public interface IScriptTable {
    /**
      * @Author:doubi.liu
      * @description:按照脚本哈希获取脚本
      * @param script_hash  脚本哈希
      * @date:2019/2/27
    */
    byte[] getScript(byte[] script_hash);
}
