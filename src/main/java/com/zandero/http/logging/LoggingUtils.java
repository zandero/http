package com.zandero.http.logging;

import com.zandero.http.RequestUtils;
import com.zandero.utils.StringUtils;
import com.zandero.utils.extra.UrlUtils;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Fill up request parameter for Log4j logging
 */
public final class LoggingUtils {

	private LoggingUtils() {
		// hide constructor
	}

	/**
	 * Fills up MDC with request info
	 * @param request to be logged
	 */
	public static void prepareForLogging(HttpServletRequest request) {

		// Add the fishtag for Log4j2 MDC
		MDC.put("timestamp", System.currentTimeMillis() + "");
		MDC.put("request_id", UUID.randomUUID().toString());    // generate random request id
		MDC.put("request", request.getRequestURI());
		MDC.put("ip", RequestUtils.getClientIpAddress(request));
		MDC.put("user_agent", RequestUtils.getUserAgent(request));

		MDC.put("method", request.getMethod());

		MDC.put("host", request.getServerName());
		MDC.put("scheme", RequestUtils.getScheme(request));
		MDC.put("domain", UrlUtils.resolveDomain(request.getServerName()));
		MDC.put("port", request.getServerPort() + "");
		MDC.put("path", request.getContextPath() + request.getPathInfo());

		if (request.getUserPrincipal() != null) {
			MDC.put("user", request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null);
		}

		if (!StringUtils.isNullOrEmptyTrimmed(request.getQueryString())) {
			MDC.put("query", request.getQueryString());
		}
	}

	/**
	 * Common purpose logging
	 * @param path of request
	 * @param query optional query of request
	 */
	public static void prepareForLogging(String path, String query) {

		MDC.put("request_id", UUID.randomUUID().toString()); // generate unique request id ...

		try {
			URI uri = new URI(path);
			MDC.put("host", uri.getHost());
			MDC.put("scheme", uri.getScheme());
			MDC.put("domain", UrlUtils.resolveDomain(uri.getPath()));
			MDC.put("port", uri.getPort() + "");
			MDC.put("path", uri.getPath());
		}
		catch (URISyntaxException e) {

			// fall back
			MDC.put("path", path);
		}

		MDC.put("timestamp", System.currentTimeMillis() + "");

		if (!StringUtils.isNullOrEmptyTrimmed(query)) {
			MDC.put("query", query);
		}
	}
}
