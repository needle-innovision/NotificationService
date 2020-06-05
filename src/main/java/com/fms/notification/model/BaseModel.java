package com.fms.notification.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@MappedSuperclass
public class BaseModel {

    @CreationTimestamp
    @Column(name = "createdAt")
    protected Timestamp createdAt;
	
	@UpdateTimestamp
    @Column(name = "updatedAt")
    protected Timestamp updatedAt;
}