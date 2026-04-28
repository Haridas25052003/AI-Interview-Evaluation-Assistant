package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProctorEventPayload {

    // TAB_SWITCH, FACE_NOT_DETECTED, MULTIPLE_FACES, NO_CAMERA
    private String violationType;
    private String details; // optional extra info
}