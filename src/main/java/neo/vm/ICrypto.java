package neo.vm;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: ICrypto
 * @Package neo.vm
 * @Description: 算法接口包
 * @date Created in 17:00 2019/2/27
 */
public interface ICrypto {
    /**
      * @Author:doubi.liu
      * @description:哈希160算法接口
      * @param message 消息
      * @date:2019/2/27
    */
    byte[] hash160(byte[] message);

    /**
      * @Author:doubi.liu
      * @description:哈希256算法接口
      * @param message 消息
      * @date:2019/2/27
    */
    byte[] hash256(byte[] message);

    /**
      * @Author:doubi.liu
      * @description:签名
      * @param message 消息 signature 签名 pubkey 公钥
      * @date:2019/2/27
    */
    boolean verifySignature(byte[] message, byte[] signature, byte[] pubkey);
}
