package com.tribytegenius.CareerCompass.UserJobService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs_cache")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String website;
}
