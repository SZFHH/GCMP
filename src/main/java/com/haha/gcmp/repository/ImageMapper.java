package com.haha.gcmp.repository;

import com.haha.gcmp.model.entity.Image;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Image mapper
 *
 * @author SZFHH
 * @date 2020/10/24
 */
@Mapper
public interface ImageMapper {
    /**
     * 根据用户id获取其所有私人镜像
     *
     * @param ownerId ownerId
     * @return list of images
     */
    List<Image> listByOwner(int ownerId);

    /**
     * 获取所有镜像
     *
     * @return list of images
     */
    List<Image> listAll();

    /**
     * 添加镜像
     *
     * @param image must not be null
     * @return changed lines
     */
    int insert(Image image);

    /**
     * 更新镜像
     *
     * @param image must not be null
     * @return changed lines
     */
    int update(Image image);

    /**
     * 根据镜像id删除镜像
     *
     * @param id id
     * @return change lines
     */
    int removeById(int id);

    /**
     * 根据镜像id获取镜像
     *
     * @param id id
     * @return image
     */
    Image getById(int id);
}
