package com.haha.gcmp.service;

import com.haha.gcmp.model.entity.DataFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/25
 */
public interface DataService {

    List<DataFile> listDataDir(String hostName, String dirPath);

    void upload(String hostName, MultipartFile[] files);

    void unzip(String hostName, String filePath);

    void remove(String hostName, String path);
    
    Path getUserDataDir();
}
