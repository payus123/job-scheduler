package com.blusalt.dbxpbackgroundservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuditEntityWithUUID {

    @Id
    @GeneratedValue(generator = "increment")

    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;
    private String uuid = UUID.randomUUID().toString();
    ;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @CreatedBy
    private String createdBy;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @LastModifiedBy
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        uuid = UUID.randomUUID().toString();
    }

}
