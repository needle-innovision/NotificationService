package com.go.notification.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for the validate https Url as per standard OWASP Validation Regex
 * 
 * @author santhosh.gudla
 *
 */
public class UrlValidator {
	/**
	 * Validates given https url is valid or not
	 * @param url
	 * @return true for valid url and false for invalid url
	 */
	public static boolean isValidUrl(String url) {
		final String OWASP_URL_REGEX = "^(((https)://)(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)([).!';/?:,][[:blank:]])?$";

		final Pattern URL_PATTERN = Pattern.compile(OWASP_URL_REGEX);
		if(url == null) {
			return false;
		}

		Matcher matcher = URL_PATTERN.matcher(url);
		
		return matcher.matches();
	}
}
