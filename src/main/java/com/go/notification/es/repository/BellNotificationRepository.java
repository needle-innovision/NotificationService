package com.go.notification.es.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.go.notification.models.BellNotificationVO;

public interface BellNotificationRepository extends ElasticsearchRepository<BellNotificationVO, String> {

	public BellNotificationVO findByNotificationId(String notificationId);

}
