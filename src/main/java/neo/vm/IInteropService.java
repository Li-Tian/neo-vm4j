package neo.vm;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: IInteropService
 * @Package neo.vm
 * @Description: 互操作服务接口
 * @date Created in 15:22 2019/2/28
 */
public interface IInteropService {
    /**
      * @Author:doubi.liu
      * @description:调用互操作服务
      * @param method 方法 engine 虚拟机执行引擎
      * @date:2019/2/28
    */
    boolean invoke(byte[] method, ExecutionEngine engine);
}
