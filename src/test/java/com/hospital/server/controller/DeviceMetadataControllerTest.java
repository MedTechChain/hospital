package com.hospital.server.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

//import com.hospital.server.model.DeviceMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
//@AutoConfigureMockMvc
//public class DeviceMetadataControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    public static final String DEVICE_API = "/api/devices";
//
//    private final DeviceMetadata testDevice = new DeviceMetadata("dd8cd1e2-2539-423c-a7c0-504c4d38b04e", "v1.2.3");
//
//    @Test
//    void testAddDeviceSuccessful() throws Exception {
//        mockMvc.perform(post(DEVICE_API)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(testDevice)))
//                .andExpect(status().isCreated());
//    }
//
//    // Sending a JSON object with just one value as a string rather than a DeviceMetadata object
//    @Test
//    void testAddDeviceBadRequest() throws Exception {
//        mockMvc.perform(post(DEVICE_API)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString("v.1.2.3")))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testGetAllDevices() throws Exception {
//        mockMvc.perform(get(DEVICE_API))
//                .andExpect(status().isOk());
//    }
//}
