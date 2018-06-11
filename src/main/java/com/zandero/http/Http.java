package com.zandero.http;

import com.zandero.utils.extra.UrlUtils;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Making GET, POST, PUT and DELETE requests ...
 */
public final class Http {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(Http.class);

	private static final int DEFAULT_CONNECT_TIMEOUT = 3000; // 3s
	private static final int DEFAULT_READ_TIMEOUT = 5000; // 5s

	private static SSLSocketFactory sslFactory;

	private Http() {
		// hide constructor
	}

	public static void setSSLSocketFactory(SSLSocketFactory factory) {
		sslFactory = factory;
	}

	/**
	 * Http utils response with code and response as String (if applicable)
	 */
	public static class HttpException extends Exception {

		private final int code;

		public HttpException(int statusCode, String message) {
			super(message);
			code = statusCode;
		}
		public int getCode() {
			return code;
		}

	}
	public static class Response {

		private final int code;

		private final String response;

		private  Map<String, List<String>> headers;

		public Response(int statusCode, String requestResponse, Map<String, List<String>> headerFields) {
			code = statusCode;
			response = requestResponse;
			headers = headerFields;
		}

		public int getCode() {
			return code;
		}
		public String getResponse() {
			return response;
		}

		public boolean is(int... status) {

			if (status.length == 0) {
				return false;
			}

			for (int expected: status) {
				if (expected == code) {
					return true;
				}
			}

			return false;
		}

		public boolean not(int... status) {
			return !is(status);
		}

		public List<String> getHeaders(String name) {
			return headers.get(name);
		}

		public String getHeader(String name) {
			List<String> found = headers.get(name);
			if (found != null && found.size() > 0) {
				return found.get(0);
			}

			return null;
		}
	}

	/**
	 * Makes GET request
	 *
	 * @param url url
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response get(String url) throws HttpException {
		return get(url, null, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Makes GET request
	 *
	 * @param url   url
	 * @param query query to append to url or null to skip
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response get(String url, Map<String, String> query) throws HttpException {
		return get(url, query, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Makes GET request with default time out settings
	 *
	 * @param url     url
	 * @param query   query to append to url or null to skip
	 * @param headers to include or null to skip
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response get(String url, Map<String, String> query, Map<String, String> headers) throws HttpException {
		return get(url, query, headers, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Makes GET request
	 *
	 * @param url            url
	 * @param query          query to append to url or null to skip
	 * @param headers        to include or null to skip
	 * @param connectTimeOut connect time out in ms
	 * @param readTimeOut    read time out in ms
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response get(String url,
	                           Map<String, String> query,
	                           Map<String, String> headers,
	                           int connectTimeOut,
	                           int readTimeOut) throws HttpException {
		return execute("GET", url, null, query, headers, connectTimeOut, readTimeOut);
	}

	/**
	 * Makes POST request to given URL
	 *
	 * @param url     url
	 * @param body    request body to post or null to skip
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response post(String url, String body) throws HttpException {

		return post(url, body, null, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Makes POST request to given URL
	 *
	 * @param url     url
	 * @param body    request body to post or null to skip
	 * @param query   query to append to url or null to skip
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response post(String url, String body, Map<String, String> query) throws HttpException {

		return post(url, body, query, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Makes POST request to given URL
	 *
	 * @param url     url
	 * @param body    request body to post or null to skip
	 * @param query   query to append to url or null to skip
	 * @param headers to include or null to skip
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response post(String url, String body, Map<String, String> query, Map<String, String> headers) throws HttpException {

		return post(url, body, query, headers, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Makes POST request to given URL
	 *
	 * @param url            url
	 * @param body           request body to post or null to skip
	 * @param query          query to append to url or null to skip
	 * @param headers        to include or null to skip
	 * @param connectTimeOut connect time out in ms
	 * @param readTimeOut    read time out in ms
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response post(String url,
	                            String body, Map<String, String> query,
	                            Map<String, String> headers,
	                            int connectTimeOut,
	                            int readTimeOut) throws HttpException {

		return execute("POST", url, body, query, headers, connectTimeOut, readTimeOut);
	}

	/**
	 * Makes PUT request to given URL
	 *
	 * @param url     url
	 * @param body    request body to post or null to skip
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response put(String url, String body) throws HttpException {

		return put(url, body, null, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Makes PUT request to given URL
	 *
	 * @param url     url
	 * @param body    request body to post or null to skip
	 * @param query   query to append to url or null to skip
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response put(String url, String body, Map<String, String> query) throws HttpException {

		return put(url, body, query, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Makes PUT request to given URL
	 *
	 * @param url     url
	 * @param body    request body to post or null to skip
	 * @param query   query to append to url or null to skip
	 * @param headers to include or null to skip
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response put(String url, String body, Map<String, String> query, Map<String, String> headers) throws HttpException {

		return put(url, body, query, headers, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Makes PUT request to given URL
	 *
	 * @param url            url
	 * @param body           request body to post or null to skip
	 * @param query          query to append to url or null to skip
	 * @param headers        to include or null to skip
	 * @param connectTimeOut connect time out in ms
	 * @param readTimeOut    read time out in ms
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response put(String url,
	                           String body,
	                           Map<String, String> query,
	                           Map<String, String> headers,
	                           int connectTimeOut,
	                           int readTimeOut) throws HttpException {

		return execute("PUT", url, body, query, headers, connectTimeOut, readTimeOut);
	}

	/**
	 * Makes DELETE request to given URL
	 *
	 * @param url     url
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response delete(String url) throws HttpException {

		return delete(url, null, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Makes DELETE request to given URL
	 *
	 * @param url     url
	 * @param query   query to append to url or null to skip
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response delete(String url, Map<String, String> query) throws HttpException {

		return delete(url, query, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Makes DELETE request to given URL
	 *
	 * @param url     url
	 * @param query   query to append to url or null to skip
	 * @param headers to include or null to skip
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response delete(String url, Map<String, String> query, Map<String, String> headers) throws HttpException {

		return delete(url, query, headers, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	/**
	 * Makes DELETE request to given URL
	 *
	 * @param url            url
	 * @param query          query to append to url or null to skip
	 * @param headers        to include or null to skip
	 * @param connectTimeOut connect time out in ms
	 * @param readTimeOut    read time out in ms
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	public static Response delete(String url,
	                              Map<String, String> query,
	                              Map<String, String> headers,
	                              int connectTimeOut,
	                              int readTimeOut) throws HttpException {

		return execute("DELETE", url, null, query, headers, connectTimeOut, readTimeOut);
	}


	/**
	 * Generic request execution method
	 *
	 * @param method         to execute
	 * @param apiUrl         url to call
	 * @param body           to post/put
	 * @param query          to add to url
	 * @param headers        to add to request
	 * @param connectTimeOut connect time out in ms
	 * @param readTimeOut    read time out in ms
	 * @return Response object with HTTP response code and response as String
	 * @throws HttpException in case of invalid input parameters
	 */
	private static Response execute(String method,
	                                String apiUrl,
	                                String body,
	                                Map<String, String> query,
	                                Map<String, String> headers,
	                                int connectTimeOut,
	                                int readTimeOut) throws HttpException {

		HttpURLConnection conn = null;
		int responseCode = 500;

		try {

			if (query != null && query.size() > 0) {
				apiUrl = UrlUtils.composeUrl(apiUrl, query);
			}

			URL url = new URL(apiUrl);

			if (apiUrl.toLowerCase().startsWith("https://")) {

				conn = (HttpsURLConnection) url.openConnection();
				if (sslFactory != null) {
					((HttpsURLConnection) conn).setSSLSocketFactory(sslFactory);
				}
			}
			else {
				conn = (HttpURLConnection) url.openConnection();
			}

			// time out settings
			conn.setConnectTimeout(connectTimeOut); // 3s
			conn.setReadTimeout(readTimeOut); // 5s

			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod(method);

			byte[] postData = null;
			if (body != null) {
				postData = body.getBytes(StandardCharsets.UTF_8);
			}

			if (headers != null && headers.size() > 0) {

				// add headers
				for (String key : headers.keySet()) {
					conn.setRequestProperty(key, headers.get(key));
				}
			}

			if (postData != null) {
				// turn input on
				conn.setDoInput(true);

				int postDataLength = postData.length;
				conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

				OutputStream os = conn.getOutputStream();
				os.write(postData);
			}

			// make request ...
			responseCode = conn.getResponseCode();

			BufferedReader reader = null;
			if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
				InputStreamReader inputStream = new InputStreamReader(conn.getInputStream());
				reader = new BufferedReader(inputStream);
			} else {  /* error from server */
				if (conn.getErrorStream() != null) {
					InputStreamReader inputStream = new InputStreamReader(conn.getErrorStream());
					reader = new BufferedReader(inputStream);
				}
			}

			StringBuilder content = new StringBuilder();
			if (reader != null) {

				String line;
				while ((line = reader.readLine()) != null) {
					content.append(line);
				}
			}

			log.debug("Output from request: {} - {}", responseCode, content);

			return new Response(responseCode, content.toString(), conn.getHeaderFields());
		}
		catch (Exception e) {
			log.error("Failed execute request to: {}", apiUrl, e);
			throw new HttpException(responseCode, e.getMessage());
		}
		finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
}