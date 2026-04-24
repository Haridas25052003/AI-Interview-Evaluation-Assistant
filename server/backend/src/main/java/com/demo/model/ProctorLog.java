package com.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proctor_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProctorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // type of violation: TAB_SWITCH, FACE_NOT_DETECTED, MULTIPLE_FACES, NO_CAMERA etc.
    @Column(nullable = false)
    private String violationType;

    // when this violation was detected
    @Column(nullable = false)
    private LocalDateTime detectedAt;

    // optional extra info (e.g. "user switched to tab: Chrome DevTools")
    @Column(columnDefinition = "TEXT")
    private String details;

    @PrePersist
    protected void onCreate() {
        this.detectedAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonBackReference
    private InterviewSession interviewSession;
}