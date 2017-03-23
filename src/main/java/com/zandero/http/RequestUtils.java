package com.zandero.http;

import com.zandero.utils.StringUtils;
import com.zandero.utils.UrlUtils;
import org.apache.commons.net.util.SubnetUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 *
 */
public final class RequestUtils {

	private RequestUtils() {
		// hide constructor
	}

	public static String getUserAgent(HttpServletRequest request) {

		return getHeader(request, "User-Agent");
	}

	private static String getCookie(Cookie[] cookies, String name) {

		if (cookies == null || cookies.length == 0 || StringUtils.isNullOrEmptyTrimmed(name)) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (StringUtils.equals(cookie.getName(), name)) { return cookie.getValue(); }
		}

		return null;
	}

	/**
	 * Returns header from given request
	 *
	 * @param request to search for header
	 * @param header  name
	 * @return header value or null if not found
	 */
	public static String getHeader(HttpServletRequest request, String header) {

		// noting to search in or nothing to be found
		if (request == null || StringUtils.isNullOrEmptyTrimmed(header)) {
			return null;
		}

		return request.getHeader(header);
	}

	/**
	 * Resolves domain name from given URL
	 *
	 * @param request to get domain name from
	 * @return domain name or null if not resolved
	 */
	public static String getDomain(HttpServletRequest request) {

		if (request == null) {
			return null;
		}

		return UrlUtils.resolveDomain(request.getServerName());
	}

	/**
	 * see: http://stackoverflow.com/questions/19598690/how-to-get-host-name-with-port-from-a-http-or-https-request/19599143#19599143
	 * takes into account that request might go through a reverse proxy and request.getScheme() might not be correct
	 *
	 * @param request request
	 * @return scheme (http, https ...)
	 */
	public static String getScheme(HttpServletRequest request) {

		String scheme = request.getHeader("X-Forwarded-Proto");
		if (StringUtils.isNullOrEmptyTrimmed(scheme)) {
			scheme = request.getScheme();
		}

		return scheme != null ? scheme.trim().toLowerCase() : null;
	}

	/**
	 * Make sure that client not interal ip address is returned
	 *
	 * @param request to look up all possible headers
	 * @return ip address
	 */
	public static String getClientIpAddress(HttpServletRequest request) {

		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		return ip;
	}

	/**
	 * Compares ipAddress with one of the ip addresses listed
	 *
	 * @param ipAddress ip address to compare
	 * @param whitelist list of ip addresses ... an be a range ip ... in format 123.123.123.123/28 where last part is a subnet range
	 * @return true if allowed, false if not
	 */
	public static boolean isIpAddressAllowed(String ipAddress, String... whitelist) {

		if (StringUtils.isNullOrEmpty(ipAddress) || whitelist == null || whitelist.length == 0) {
			return false;
		}

		for (String address : whitelist) {

			if (ipAddress.equals(address)) {
				return true;
			}

			if (address.contains("/")) { // is range definition

				SubnetUtils utils = new SubnetUtils(address);
				utils.setInclusiveHostCount(true);

				if (utils.getInfo().isInRange(ipAddress)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if request is made by cron job
	 *
	 * @param servletRequest must be GET request with cron job basic authorization set
	 * @param username       to check agains
	 * @param password       to check against
	 * @return true if cron job request, false if not
	 */
	public static boolean checkBasicAuth(HttpServletRequest servletRequest, String username, String password) {

		String basicAuth = servletRequest.getHeader("Authorization");
		if (StringUtils.isNullOrEmptyTrimmed(basicAuth) || !basicAuth.startsWith("Basic ")) {
			return false;
		}

		String base64Credentials = basicAuth.substring("Basic".length()).trim();
		String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));

		// credentials = username:password
		final String[] values = credentials.split(":", 2);
		if (values.length != 2) {
			return false;
		}

		return StringUtils.equals(values[0], username) && StringUtils.equals(values[1], password);
	}
}
