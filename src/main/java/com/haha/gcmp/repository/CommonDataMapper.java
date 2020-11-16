package com.haha.gcmp.repository;

import com.haha.gcmp.model.entity.Data;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/11/10
 */
public interface CommonDataMapper {

    /**
     * 获得所有的公共数据集记录
     *
     * @return list of data
     */
    List<Data> listAll();

    /**
     * 根据id获取公共数据集记录
     *
     * @param id common data id
     * @return Data
     */
    Data getById(int id);

    /**
     * 根据id删除公共数据集记录
     *
     * @param id common data id
     * @return changed lines
     */
    int removeById(int id);

    /**
     * 插入公共数据集记录
     *
     * @param data must not be null
     * @return changed lines
     */
    int insert(Data data);

    /**
     * 删除所有的公共数据集记录
     *
     * @return changed lines
     */
    int removeAll();
}
