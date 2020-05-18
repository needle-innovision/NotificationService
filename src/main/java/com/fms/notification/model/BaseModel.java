package com.fms.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
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