package com.go.notification.service.dao;

import java.sql.SQLException;
import java.util.*;

import com.go.notification.models.UserVO;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.ActionFilter;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.go.notification.constants.NotificationTypes;
import com.go.notification.es.repository.BellNotificationRepository;
import com.go.notification.models.BellNotificationVO;
import com.go.notification.models.BellResponseVO;
import com.go.notification.utils.JedisEnum;
import com.ne.commons.redis.config.RedisConfig;

import redis.clients.jedis.Jedis;

@Component
public class NotificationESDAOImpl implements NotificationESDAO {

	@Autowired
	Client esClient;

	@Autowired
	BellNotificationRepository bellRepo;

	@Value("${elasticsearch.indexName}")
	String indexName;


	@Autowired
	RedisConfig redisConfig;
	
	private static final Logger log = LoggerFactory.getLogger(NotificationESDAOImpl.class);

	@Override
	public HashMap<String, Object> getBellNotifications(int userId, String filter, long offset, int limit) {
		HashMap<String, Object> notificationResults = new HashMap<String, Object>();
		List<BellResponseVO> notificationList = new ArrayList<BellResponseVO>();
		long resultsFound = 0L;

		// ES Query


		BoolQueryBuilder nestedBool=QueryBuilders.boolQuery();
		nestedBool.must(QueryBuilders.termQuery("users.userId", userId));
		nestedBool.must(QueryBuilders.termQuery("users.notificationReadStatus", 0));
		BoolQueryBuilder searchBool = QueryBuilders.boolQuery();

		searchBool.must(QueryBuilders.matchQuery("notificationType", NotificationTypes.BELL.toString()));
		searchBool.filter(QueryBuilders.existsQuery("notificationId"));
		NestedQueryBuilder nestedQueryBuilder=QueryBuilders.nestedQuery("users", nestedBool, ScoreMode.None);
		searchBool.must(nestedQueryBuilder);

		try {

			resultsFound = esClient.prepareSearch(indexName).setTypes("notification").setQuery(searchBool)
					.addSort(new ScoreSortBuilder()).setSize(0).get().getHits().getTotalHits();

			if (offset > 0) {

				QueryBuilder rangeQuery = QueryBuilders.rangeQuery("eventTime").lt(offset);
				searchBool.filter(rangeQuery);
			}
			log.info(searchBool+"****************************" );
			SearchResponse response = esClient.prepareSearch(indexName).setTypes("notification").setQuery(searchBool)
					.addSort(new ScoreSortBuilder()).addSort("eventTime", SortOrder.DESC).setSize(limit).get();

			SearchHit[] results = response.getHits().getHits();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			for (SearchHit hit : results) {
				String source = hit.getSourceAsString();
				try {
					BellNotificationVO bellObj = mapper.readValue(source, BellNotificationVO.class);
					BellResponseVO obj = new BellResponseVO();
					try {
					bellObj.getCommonEventContent().getUser().setOnline_status(
							getUserStatusFromRedis(bellObj.getCommonEventContent().getUser().getId()));
					obj.setAction(bellObj.getAction());
					}catch(Exception e) {
						log.error("Unable to set user status for bell notification:",e);
					}
					obj.setBaseEvent(bellObj.getCommonEventContent());
					obj.setNotificationId(bellObj.getNotificationId());
					obj.setNotificationType(bellObj.getNotificationType());
					obj.setEventTime(bellObj.getEventTime());
					obj.setNotificationLevel(bellObj.getNotificationLevel());

					notificationList.add(obj);
				} catch (Exception e) {
					log.error("Exception in mapping string to bell Notification VO :", e);
				}
			}
		} catch (Exception e) {
			log.error("Exception in getting bell notification list for userId:" + userId, e);
		}

		notificationResults.put("resultsFound", resultsFound);
		notificationResults.put("response", notificationList);

		return notificationResults;
	}

	@Override
	public boolean updateBellNotificationStatus(int userId, String[] notificationIds, int option) {
		boolean status = false;
		long resultsFound = 0L;
//		userId=4651;
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("userName","navee");

		log.info(indexName+"******************** INDEXNAME      ****");
//		try{
//			if(notificationIds.length!=0){
//
//				log.info(params+"((((((((((((((((((((((( parms");
//
//				UpdateByQueryRequestBuilder updateByQuery =
//						new UpdateByQueryRequestBuilder(esClient, UpdateByQueryAction.INSTANCE);
//				updateByQuery.source(indexName).abortOnVersionConflict(false).refresh(true)
//						.filter(QueryBuilders.idsQuery().types("notification").addIds(notificationIds))
//
//						.script(new Script(ScriptType.INLINE,
//								"painless",
//								"for (int i = 0; i < ctx._source.users.length; ++i) {"
//								+"if(ctx._source.users[i].userId == 4651) {"
//								+"ctx._source.users[i].notificationReadStatus = 0;"
//								+"}}",
//								params))
//						.execute();
//				updateByQuery.request();
//				BulkByScrollResponse updateresponse = updateByQuery.get();
//
//
//				Script one=new Script(ScriptType.INLINE,
//						"painless",
//						"for (int i = 0; i < ctx._source.users.length; ++i) {"
//								+"if(ctx._source.users[i].userId == 4651) {"
//
//								+"ctx._source.users[i].notificationReadStatus = 1;"
//								+"}}",
//						params);
//				log.info(updateByQuery.request()+"this si the response )))))))********************");
//				log.info("upddd^^^^^^^^^^^^^^^^"+"--"+updateresponse.getUpdated()+"---");
//
//			}
//			else{
//				UpdateByQueryRequestBuilder updateByQuery =
//						new UpdateByQueryRequestBuilder(esClient, UpdateByQueryAction.INSTANCE);
//				updateByQuery.source(indexName).abortOnVersionConflict(false)
//						.filter(QueryBuilders.termQuery("users.userId", "4651"))
//						.script(new Script(ScriptType.INLINE,
//								"painless",
//								"for (int i = 0; i < ctx._source.users.length; ++i) {"
//										+"if(ctx._source.users[i].userId == 4651) {"
//										+"ctx._source.users[i].notificationReadStatus = 1;"
//										+"}}",
//								params));
//				BulkByScrollResponse updateresponse = updateByQuery.get();
//				log.info(updateresponse+"this si the response ********************");
//
//			}
//
//
//
//			log.info(QueryBuilders.idsQuery().types("notification").addIds(notificationIds)+" &&&&& ids");
//
//		}catch(Exception e){
//			log.info("exception while updating fields to search"+e);
//		}


		int getStatus=option==0?1:0;

		BoolQueryBuilder nestedBool=QueryBuilders.boolQuery();
		nestedBool.must(QueryBuilders.termQuery("users.userId", userId));
//		nestedBool.must(QueryBuilders.termQuery("users.notificationReadStatus", getStatus));
		NestedQueryBuilder nestedQueryBuilder=QueryBuilders.nestedQuery("users", nestedBool, ScoreMode.None);



		BoolQueryBuilder searchBool = QueryBuilders.boolQuery();
		searchBool.must(QueryBuilders.matchQuery("notificationType", NotificationTypes.BELL.toString()));
//		searchBool.filter(QueryBuilders.existsQuery("notificationId"));
		searchBool.must(nestedQueryBuilder);


		if (notificationIds.length != 0) {
			searchBool.must(QueryBuilders.idsQuery().types("notification").addIds(notificationIds));
		}
		log.info(searchBool+ "************Query ***444**************");
		List<BellNotificationVO> notificationList = new ArrayList<BellNotificationVO>();
		try {
			resultsFound = esClient.prepareSearch(indexName).setTypes("notification").setQuery(searchBool).setSize(0)
					.get().getHits().getTotalHits();
			SearchResponse response = esClient.prepareSearch(indexName).setTypes("notification").setQuery(searchBool)
					.setFrom(0).setSize((int) resultsFound).get();
			SearchHit[] results = response.getHits().getHits();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			for (SearchHit hit : results) {
				String source = hit.getSourceAsString();

				try {
					BellNotificationVO bellObj = mapper.readValue(source, BellNotificationVO.class);
					Iterator<UserVO> userIterator=bellObj.getUsers().iterator();
					List<UserVO> bellUsers=new ArrayList<>();
					while (userIterator.hasNext()){

						UserVO userObj=userIterator.next();
						log.info(userObj.toString()+"9879080980980980980980908909");
						if(userObj.getUserId()==userId){
							userObj.setNotificationReadStatus(option);
						}
						bellUsers.add(userObj);
					}
					bellObj.setUsers(bellUsers);
					bellObj.setStatus(option);
					notificationList.add(bellObj);
					log.info(option+"******))))))))))))))))))))))))))))))))))))))))))))");
				} catch (Exception e) {
					log.error("Exception in mapping string to bell Notification VO :", e);
				}
			}
			if (notificationList.size() > 0) {
				bellRepo.saveAll(notificationList);

				log.info(notificationList.size()+"size matters-------------------"+resultsFound);
				status = true;
			}
		} catch (Exception e) {
			log.error("Exception in updating bell notification status:", e);
		}
		return status;
	}

	@Override
	public boolean updateBellNotificationStatus(String entityId, long userId) {
		boolean status = false;
		long resultsFound = 0L;
		BoolQueryBuilder searchBool = QueryBuilders.boolQuery();
		searchBool.must(QueryBuilders.termQuery("users.userId", userId));
		searchBool.must(QueryBuilders.matchQuery("entityId.keyword", entityId));
		searchBool.must(QueryBuilders.termQuery("status", 0));
		searchBool.must(QueryBuilders.matchQuery("notificationType", NotificationTypes.BELL.toString()));

		List<BellNotificationVO> notificationList = new ArrayList<BellNotificationVO>();
		try {
			resultsFound = esClient.prepareSearch(indexName).setTypes("notification").setQuery(searchBool).setSize(0)
					.get().getHits().getTotalHits();
			SearchResponse response = esClient.prepareSearch(indexName).setTypes("notification").setQuery(searchBool)
					.setFrom(0).setSize((int) resultsFound).get();
			SearchHit[] results = response.getHits().getHits();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			for (SearchHit hit : results) {
				String source = hit.getSourceAsString();

				try {
					BellNotificationVO bellObj = mapper.readValue(source, BellNotificationVO.class);
					bellObj.setStatus(1);
					List<UserVO> updatedUser= new ArrayList<>();

					notificationList.add(bellObj);
				} catch (Exception e) {
					log.error("Exception in mapping string to bell Notification VO :", e);
				}
			}
			if (notificationList.size() > 0) {
				bellRepo.saveAll(notificationList);
				status = true;
			}
		} catch (Exception e) {
			log.error("Exception in updating bell notification status:", e);
		}
		return status;
	}

	public String getUserStatusFromRedis(long userId) throws SQLException {
		String userStatus = "offline";
		Jedis jedis = redisConfig.getConnection();

		long userTimeStamp = 0L;
		try {
			String cacheTimeStamp = jedis.get(JedisEnum.USERPRESENCE + "_" + String.valueOf(userId));
			if (cacheTimeStamp != null) {
				userTimeStamp = Long.parseLong(cacheTimeStamp);
				DateTime dt = new DateTime(DateTimeZone.UTC);
				long currentTimeStamp = dt.getMillis() / 1000;
				if (userTimeStamp == 0) {
					userStatus = "offline";
				} else if (currentTimeStamp - userTimeStamp < 300) {
					userStatus = "online";
				} else if ((currentTimeStamp - userTimeStamp) < 28800) {
					userStatus = "away";
				} else if ((currentTimeStamp - userTimeStamp) >= 28800) {
					userStatus = "offline";
				}
			}
		} catch (Exception e) {
			log.error("Exception in getting user presence from redis:", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		return userStatus;
	}

}
