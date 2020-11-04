package com.haha.gcmp.controller;

import com.haha.gcmp.model.entity.Image;
import com.haha.gcmp.model.params.ImageParam;
import com.haha.gcmp.service.DockerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Docker controller
 *
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

    @GetMapping("common")
    public List<Image> listAllCommonImage() {
        return dockerService.listCommonImage();
    }

    @GetMapping("all")
    public Map<String, List<Image>> listAllImage() {
        return dockerService.listAllImage();
    }

    @GetMapping("user")
    public List<Image> listUserImage() {
        return dockerService.listUserImage();
    }

    @PostMapping("common")
    public void addCommonImage(@RequestBody ImageParam imageParam) {
        dockerService.addCommonImage(imageParam);
    }

    @PostMapping("user")
    public void addUserImage(@RequestBody ImageParam imageParam) {
        dockerService.addUserImage(imageParam);
    }

    @GetMapping("quota")
    public int userDockerFileQuota() {
        return dockerService.getUserDockerFileQuota();
    }

    @DeleteMapping("user/{imageId:\\d+}")
    public void removeUserImage(@PathVariable("imageId") int imageId) {
        dockerService.removeUserImage(imageId);
    }

    @DeleteMapping("common/{imageId:\\d+}")
    public void removeCommonImage(@PathVariable("imageId") int imageId) {
        dockerService.removeCommonImage(imageId);
    }

}
