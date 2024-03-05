package com.hospital.server.controller;

import com.hospital.server.model.DeviceMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

// TODO: add tests for API
// TODO: create API documentation, and two endpoints for two types of metadata
@RestController
@RequestMapping("/api/devices")
public class DeviceMetadataController {
    private final List<DeviceMetadata> devices = new ArrayList<>();

    //
    @PostMapping
    public ResponseEntity<DeviceMetadata> addDevice(@RequestBody DeviceMetadata deviceMetadata) {
        devices.add(deviceMetadata);
        return new ResponseEntity<>(deviceMetadata, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DeviceMetadata>> getAllDevices() {
        return ResponseEntity.ok(devices);
    }
}