package com.go.notification.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * This class will have logic for convert json string to plane java object
 * Plane java object to json string.
 * @author santhosh.gudla
 *
 */
public class JsonConverterUtility {
	/**
	 *  Converts the json string to plane java object
	 * @param json
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T> T mapFromJson(String json, Class<T> clazz) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		return mapper.readValue(json, clazz);
	}
	
	/**
	 * Convert plane java object to json String
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String mapToJson(Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
