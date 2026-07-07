package com.supplychain.ai.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "upload_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private LocalDateTime uploadTime;
    private Long fileSize;
    private Integer rowCount;
    private String status; // SUCCESS, FAILED
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
