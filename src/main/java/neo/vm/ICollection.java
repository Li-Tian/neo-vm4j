package neo.vm;

import neo.log.notr.TR;

/**
 * @author doubi.liu
 * @version V1.0
 * @Title: ICollection
 * @Package neo.vm
 * @Description: (用一句话描述该文件做什么)
 * @date Created in 11:46 2019/3/4
 */
public interface ICollection<T> extends Iterable<T>{
    public int getCount();
}
