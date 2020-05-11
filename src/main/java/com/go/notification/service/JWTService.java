package com.go.notification.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JWTService {

	private static final Logger logger = LogManager.getLogger(JWTService.class);

	@Value("${jwt_secret_key}")
	String jwtSecret;

	public JWTService() {
	}

	public int getUserId(String jwtToken) {
		int userId = 0;
		try {
			if (jwtToken != null) {
				Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).getBody();
				String userIdString = claims.get("user_id").toString();
				if (userIdString != null) {
					userId = Integer.valueOf(userIdString);
				} else {
					logger.error("userId in JWT is null");
				}
			} else {
				logger.error("JWTSecret is null.");
			}
		} catch (Exception e) {
			logger.error("Exception in getting jwt token:", e);
		}
		return userId;
	}

	public int getOrgId(String jwtToken) {
		int orgId = 0;
		try {
			if (jwtToken != null) {
				Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).getBody();
				String orgIdString = claims.get("org_id").toString();
				if (orgIdString != null) {
					orgId = Integer.valueOf(orgIdString);
				} else {
					logger.error("userId in JWT is null");
				}
			} else {
				logger.error("JWTSecret is null.");
			}
		} catch (Exception e) {
			logger.error("Exception in getting jwt token:", e);
		}

		return orgId;
	}

	public long getOrgUniqueId(String jwtToken) {
		long orgUniqueId = 0;
		try {
			if (jwtToken != null) {
				Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).getBody();
				String orgUniqueIdString = claims.get("org_unique_id").toString();
				if (orgUniqueIdString != null) {
					orgUniqueId = Long.parseLong(orgUniqueIdString);
				} else {
					logger.error("orgUniqueId in JWT is null");
				}
			} else {
				logger.error("JWTSecret is null.");
			}
		} catch (Exception e) {
			logger.error("Exception in getting jwt token:", e);
		}

		return orgUniqueId;
	}

	public String getUserName(String jwtToken) {
		String userName = null;
		try {
			if (jwtToken != null) {
				Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).getBody();

				// String userNameString = claims.get("username").toString();

				String firstNameString = claims.get("Firstname").toString();

				String lastNameString = claims.get("Lastname").toString();

				if (firstNameString != null && lastNameString != null) {
					userName = firstNameString + lastNameString;
				} else {
					logger.error("userName in JWT is null");
				}
			} else {
				logger.error("JWTSecret is null");
			}

		} catch (Exception e) {
			logger.error("Exception in getting jwt token:" + e);
		}
		return userName;
	}

	public int getOrgTypeId(String jwtToken) {
		int orgTypeId = 0;
		try {
			if (jwtToken != null) {
				Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).getBody();
				String orgTypeIdString = claims.get("org_type").toString();
				if (orgTypeIdString != null) {
					orgTypeId = Integer.parseInt(orgTypeIdString);
				} else {
					logger.error("orgTypeId in JWT is null");
				}
			} else {
				logger.error("JWTToken is null.");
			}
		} catch (Exception e) {
			logger.error("Exception in getting jwt token:", e);
		}
		return orgTypeId;

	}

}
