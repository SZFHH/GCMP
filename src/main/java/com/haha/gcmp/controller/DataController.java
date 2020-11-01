package com.haha.gcmp.controller;

import com.haha.gcmp.model.entity.DataFile;
import com.haha.gcmp.model.entity.TempFileInfo;
import com.haha.gcmp.model.params.*;
import com.haha.gcmp.model.support.CheckFileResult;
import com.haha.gcmp.service.DataService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author SZFHH
 * @date 2020/10/22
 */
@RestController
@RequestMapping("/api/data")
public class DataController {
    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/upload")
    public void uploadChunk(UploadChunkParam uploadChunkParam, @RequestParam("file") MultipartFile file) {
        dataService.uploadChunk(uploadChunkParam, file);
//        System.out.println("upload" + System.currentTimeMillis() + "   " + uploadChunkParam.getChunkNumber());
    }

    @PostMapping("/merge")
    public void mergeChunk(MergeChunkParam mergeChunkParam) {
        dataService.mergeChunk(mergeChunkParam);
//        System.out.println("merge" + System.currentTimeMillis());
    }

    @PostMapping("/check")
    public CheckFileResult checkChunk(CheckFileQuery checkFileQuery) {
        return dataService.checkChunk(checkFileQuery);
    }

    @PostMapping("/cancel")
    public void cancelUpload(TempFileInfo tempFileInfo) {
        dataService.cancelUpload(tempFileInfo);
    }

    @PostMapping("/move")
    public void moveData(DataMoveParam dataMoveParam) {
        dataService.move(dataMoveParam);
    }

    @PostMapping("/remove")
    public void remove(DataParam dataParam) {
        dataService.remove(dataParam);
    }

    @PostMapping("/list")
    public List<DataFile> listDir(DataParam dataParam) {
        return dataService.listDataDir(dataParam);
    }

    @PostMapping("/unzip")
    public void unzip(DataParam dataParam) {
        dataService.unzip(dataParam);
    }
}
