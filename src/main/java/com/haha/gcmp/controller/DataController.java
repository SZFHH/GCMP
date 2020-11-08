package com.haha.gcmp.controller;

import com.haha.gcmp.model.entity.Data;
import com.haha.gcmp.model.entity.TempFile;
import com.haha.gcmp.model.params.*;
import com.haha.gcmp.model.support.CheckFileResult;
import com.haha.gcmp.service.DataService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Data controller
 *
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

    @PostMapping("/chunk")
    public void uploadChunk(UploadChunkParam uploadChunkParam, @RequestParam("file") MultipartFile file) {
        dataService.uploadChunk(uploadChunkParam, file);
    }

    @PostMapping("/chunk/merge")
    public void mergeChunk(MergeChunkParam mergeChunkParam) {
        dataService.mergeChunk(mergeChunkParam);
    }

    @GetMapping("/chunk")
    public CheckFileResult checkChunk(CheckFileQuery checkFileQuery) {
        return dataService.checkChunk(checkFileQuery);
    }

    @DeleteMapping("/chunk")
    public void cancelUpload(TempFile tempFile) {
        dataService.cancelUpload(tempFile);
    }

    @PostMapping("move")
    public void moveData(@RequestBody DataMoveParam dataMoveParam) {
        dataService.move(dataMoveParam);
    }

    @PostMapping("/dir")
    public void newDir(@RequestBody DataParam dataParam) {
        dataService.newDir(dataParam);
    }

    @PostMapping("copy")
    public void copyData(@RequestBody DataMoveParam dataMoveParam) {
        dataService.copy(dataMoveParam);
    }

    @DeleteMapping("")
    public void remove(@RequestBody DataParam dataParam) {
        dataService.remove(dataParam);
    }

    @GetMapping("")
    public List<Data> listDir(DataParam dataParam) {
        return dataService.listDataDir(dataParam);
    }

    @PostMapping("unzip")
    public void unzip(@RequestBody DataParam dataParam) {
        dataService.unzip(dataParam);
    }
}
