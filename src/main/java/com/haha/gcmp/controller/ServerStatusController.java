package com.haha.gcmp.controller;

import com.haha.gcmp.model.dto.ServerPropertyDto;
import com.haha.gcmp.model.entity.ServerStatus;
import com.haha.gcmp.service.ServerStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Server status controller
 *
 * @author SZFHH
 * @date 2020/11/1
 */
@RestController
@RequestMapping("/api/status")
public class ServerStatusController {
    private final ServerStatusService serverStatusService;

    public ServerStatusController(ServerStatusService serverStatusService) {
        this.serverStatusService = serverStatusService;
    }

    @GetMapping("available_all")
    public List<ServerStatus> getAll() {
        return serverStatusService.getServersAllAvailable();
    }

    @GetMapping("available_gpus")
    public List<Integer> getAvailableGpus() {
        return serverStatusService.getServersGpuAvailable();
    }

    @GetMapping("server_property")
    public List<ServerPropertyDto> getServerProperty() {
        return serverStatusService.getServersServerProperty();

    }
}
