package com.haha.gcmp.controller;

import com.haha.gcmp.exception.ServiceException;
import com.haha.gcmp.model.entity.Data;
import com.haha.gcmp.model.entity.TempFile;
import com.haha.gcmp.model.params.*;
import com.haha.gcmp.model.support.CheckFileResult;
import com.haha.gcmp.service.DataService;
import com.haha.gcmp.utils.FileUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
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
    public void mergeChunk(@RequestBody MergeChunkParam mergeChunkParam) {
        dataService.mergeChunk(mergeChunkParam);
    }

    @PostMapping("/chunk/check")
    public CheckFileResult checkChunk(@RequestBody CheckFileQuery checkFileQuery) {
        return dataService.checkChunk(checkFileQuery);
    }

    @DeleteMapping("/chunk")
    public void cancelUpload(@RequestBody TempFile tempFile) {
        dataService.cancelUpload(tempFile);
    }

    @PostMapping("move")
    public void moveData(@RequestBody DataMoveParam dataMoveParam) {
        dataService.move(dataMoveParam);
    }

    @PostMapping("/dir")
    public void newDir(@RequestBody DataParam dataParam) {
        dataService.newRelativeDir(dataParam);
    }

    @PostMapping("copy")
    public void copyData(@RequestBody DataMoveParam dataMoveParam) {
        dataService.copy(dataMoveParam);
    }

    @DeleteMapping("")
    public void remove(@RequestBody DataParam dataParam) {
        dataService.removeRelativePath(dataParam);
    }

    @GetMapping("")
    public List<Data> listDir(DataParam dataParam) {
        return dataService.listDataDir(dataParam);
    }

    @PostMapping("unzip")
    public void unzip(@RequestBody DataParam dataParam) {
        dataService.unzip(dataParam);
    }

    @GetMapping("/download")
    public void downLoad(DataParam dataParam, HttpServletResponse httpServletResponse) {
        byte[] fileByte = dataService.getFile(dataParam);
        httpServletResponse.setContentType("application/force-download");
        httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + FileUtils.getFileName(dataParam.getRelativePath()));
        try (OutputStream os = httpServletResponse.getOutputStream()) {
            os.write(fileByte);
            os.flush();
        } catch (IOException e) {
            throw new ServiceException("下载文件写入HttpServletResponse输出流异常", e);
        }
    }

    @GetMapping("/common")
    public List<Data> listCommonDataset() {
        return dataService.listCommonDataset();
    }

    @PostMapping("/sync_common")
    public void syncCommonData() {
        dataService.syncCommonData();
    }
}
