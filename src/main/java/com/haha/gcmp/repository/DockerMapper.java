package com.haha.gcmp.repository;

import com.haha.gcmp.model.entity.Image;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
@Mapper
public interface DockerMapper {
    List<Image> listByOwner(int ownerId);

    List<Image> listAll();

    int insert(Image image);

    int remove(Image image);

    int update(Image image);

    int removeByTag(String tag);

    int removeById(int id);

    Image getById(int id);
}
