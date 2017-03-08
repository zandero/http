package com.zandero.http.logging;

import com.zandero.http.RequestUtils;
import com.zandero.utils.StringUtils;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * Fill up request parameter for Log4j logging
 */
public final class LoggingUtils {

	private LoggingUtils() {
		// hide constructor
	}

	public static void prepareForLogging(HttpServletRequest request) {

		// Add the fishtag for Log4j2 MDC
		MDC.put("timestamp", System.currentTimeMillis() + "");
		MDC.put("request_id", UUID.randomUUID().toString());    // generate random request id
		MDC.put("request", request.getRequestURI());
		MDC.put("ip", RequestUtils.getClientIpAddress(request));
		MDC.put("user_agent", RequestUtils.getUserAgent(request));
		if (request.getUserPrincipal() != null) {
			MDC.put("user", request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null);
		}
		MDC.put("method", request.getMethod());
		if (!StringUtils.isNullOrEmptyTrimmed(request.getQueryString())) {
			MDC.put("query", request.getQueryString());
		}
		MDC.put("host", request.getServerName());
		MDC.put("scheme", RequestUtils.getScheme(request));
	}
	
	public static void prepareForLogging(String path, String query) {

		MDC.put("request_id", UUID.randomUUID().toString());
		// generate unique request id ...
		MDC.put("path", path);
		MDC.put("timestamp", System.currentTimeMillis() + "");

		if (!StringUtils.isNullOrEmptyTrimmed(query)) {
			MDC.put("query", query);
		}
	}
}
