package com.haha.gcmp.model.params;

/**
 * Base param interface
 *
 * @author SZFHH
 * @date 2020/10/23
 */
public interface BaseParam<T> {
    /**
     * param 转为entity
     *
     * @return entity
     */
    T toEntity();
}
