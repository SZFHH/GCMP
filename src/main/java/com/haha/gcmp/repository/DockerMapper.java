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
    List<Image> findByOwner(int ownerId);

    List<Image> findAll();

    int insert(Image image);

    int delete(Image image);

    int update(Image image);

    int deleteByTag(String tag);

    int deleteById(int id);

    Image findById(int id);
}
