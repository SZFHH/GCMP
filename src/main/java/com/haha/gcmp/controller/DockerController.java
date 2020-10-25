package com.haha.gcmp.controller;

import com.haha.gcmp.model.entity.Image;
import com.haha.gcmp.model.params.ImageToAddParam;
import com.haha.gcmp.model.params.ImageToRemoveParam;
import com.haha.gcmp.service.DockerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author SZFHH
 * @date 2020/10/22
 */
@RestController
@RequestMapping("/api/docker")
public class DockerController {
    private final DockerService dockerService;

    public DockerController(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @PostMapping("/list/common")
    public List<Image> listAllCommonImage() {
        return dockerService.listCommonImage();
    }

    @PostMapping("/list/all")
    public Map<String, List<Image>> listAllImage() {
        return dockerService.listAllImage();
    }

    @PostMapping("/list/user")
    public List<Image> listUserImage() {
        return dockerService.listUserImage();
    }

    @PostMapping("add/common")
    public void addCommonImage(@RequestBody ImageToAddParam imageToAddParam) {
        dockerService.addCommonImage(imageToAddParam);
    }

    @PostMapping("add/user")
    public void addUserImage(@RequestBody ImageToAddParam imageToAddParam) {
        dockerService.addUserImage(imageToAddParam);
    }

    @PostMapping("quota")
    public int userDockerFileQuota() {
        return dockerService.getUserDockerFileQuota();
    }

    @PostMapping("/remove/user")
    public void removeUserImage(@RequestBody ImageToRemoveParam imageParam) {
        dockerService.removeUserImage(imageParam);
    }

    @PostMapping("/remove/common")
    public void removeCommonImage(@RequestBody ImageToRemoveParam imageParam) {
        dockerService.removeCommonImage(imageParam);
    }

}
