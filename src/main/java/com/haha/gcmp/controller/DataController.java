package com.haha.gcmp.controller;

import com.haha.gcmp.model.dto.CheckChunkDTO;
import com.haha.gcmp.model.params.CheckChunkParam;
import com.haha.gcmp.model.params.MergeChunkParam;
import com.haha.gcmp.model.params.UploadChunkParam;
import com.haha.gcmp.service.DataService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    }

    @PostMapping("/merge")
    public void mergeChunk(MergeChunkParam mergeChunkParam) {
        dataService.mergeChunk(mergeChunkParam);
    }

    @PostMapping("/check")
    public CheckChunkDTO checkChunk(CheckChunkParam checkChunkParam) {
        return dataService.checkChunk(checkChunkParam);
    }
}
