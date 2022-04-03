package com.zandero.http;

/**
 *
 */

import com.zandero.utils.Assert;
import com.zandero.utils.ResourceUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Common purpose utility to fetch data from external URLs
 */
public final class HttpUtils {

	/**
	 * UTF-8
	 */
	public static final String UTF_8 = "UTF-8";

	private HttpUtils() {
		// hiding constructor
	}

	private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

	private static final int TIME_OUT_IN_SECONDS = 30;


	/**
	 * Step 1. prepare GET request
	 *
	 * @param path target url
	 * @return get request
	 */
	public static HttpRequestBase get(String path) {

		return get(path, null, null);
	}

	/**
	 * Step 1. prepare GET request
	 *
	 * @param path             target url
	 * @param headers          request headers
	 * @param timeOutInSeconds time out in seconds or null for default time out
	 * @return get request
	 */
	public static HttpRequestBase get(String path,
	                                  Map<String, String> headers,
	                                  Integer timeOutInSeconds) {


		HttpGet get = new HttpGet(path);
		addHeaders(get, headers);
		get.setConfig(getConfig(timeOutInSeconds));
		return get;
	}

	/**
	 * Step 1. prepare POST request
	 *
	 * @param path target url
	 * @return post request
	 */
	public static HttpRequestBase post(String path) {

		return post(path, null, null, null);
	}

	/**
	 * Step 1. prepare POST request
	 *
	 * @param path             target url
	 * @param headers          request headers
	 * @param entity           to POST
	 * @param parameters       to POST
	 * @param timeOutInSeconds time out in seconds or null for default time out
	 * @return post request
	 */
	public static HttpRequestBase post(String path,
	                                   Map<String, String> headers,
	                                   Map<String, String> parameters,
	                                   HttpEntity entity,
	                                   Integer timeOutInSeconds) {


		HttpPost post = new HttpPost(path);
		addHeaders(post, headers);
		addParameters(post, parameters);

		post.setConfig(getConfig(timeOutInSeconds));

		if (entity != null) {
			post.setEntity(entity);
		}
		return post;
	}

	/**
	 * Step 1. prepare POST request
	 *
	 * @param path             target url
	 * @param headers          request headers
	 * @param parameters       to POST
	 * @param timeOutInSeconds time out in seconds or null for default time out
	 * @return post request
	 */
	public static HttpRequestBase post(String path,
	                                   Map<String, String> headers,
	                                   Map<String, String> parameters,
	                                   Integer timeOutInSeconds) {


		return post(path, headers, parameters, null, timeOutInSeconds);
	}

	/**
	 * Step 1. prepare PUT request
	 *
	 * @param path   target url
	 * @param entity to PUT
	 * @return put request
	 */
	public static HttpRequestBase put(String path,
	                                  HttpEntity entity) {

		return put(path, null, null, entity, null);
	}

	/**
	 * Step 1. prepare PUT request
	 *
	 * @param path             target url
	 * @param headers          request headers
	 * @param entity           to PUT
	 * @param parameters       to PUT
	 * @param timeOutInSeconds time out in seconds or null for default time out
	 * @return put request
	 */
	public static HttpRequestBase put(String path,
	                                  Map<String, String> headers,
	                                  Map<String, String> parameters,
	                                  HttpEntity entity,
	                                  Integer timeOutInSeconds) {


		HttpPut put = new HttpPut(path);
		addHeaders(put, headers);
		addParameters(put, parameters);
		put.setConfig(getConfig(timeOutInSeconds));

		if (entity != null) {
			put.setEntity(entity);
		}
		return put;
	}

	/**
	 * Step 1. prepare PUT request
	 *
	 * @param path             target url
	 * @param headers          request headers
	 * @param parameters       to PUT
	 * @param timeOutInSeconds time out in seconds or null for default time out
	 * @return put response
	 */
	public static HttpRequestBase put(String path,
	                                  Map<String, String> headers,
	                                  Map<String, String> parameters,
	                                  Integer timeOutInSeconds) {

		return put(path, headers, parameters, null, timeOutInSeconds);
	}


	/**
	 * Step 1. prepare PATCH request
	 *
	 * @param path             target url
	 * @param headers          request headers
	 * @param parameters       to PATCH
	 * @param entity           send to patch
	 * @param timeOutInSeconds time out in seconds or null for default time out
	 * @return patch request
	 */
	public static HttpRequestBase patch(String path,
	                                    Map<String, String> headers,
	                                    Map<String, String> parameters,
	                                    HttpEntity entity,
	                                    Integer timeOutInSeconds) {


		HttpPatch patch = new HttpPatch(path);
		addHeaders(patch, headers);
		addParameters(patch, parameters);
		patch.setConfig(getConfig(timeOutInSeconds));

		if (entity != null) {
			patch.setEntity(entity);
		}
		return patch;
	}

	/**
	 * Step 1. prepare PATCH request
	 *
	 * @param path target url
	 * @return patch request
	 */
	public static HttpRequestBase patch(String path) {

		return patch(path, null, null, null);
	}

	/**
	 * Step 1. prepare PATCH request
	 *
	 * @param path             target url
	 * @param headers          request headers
	 * @param parameters       to patch
	 * @param timeOutInSeconds time out in seconds or null for default time out
	 * @return patch request
	 */
	public static HttpRequestBase patch(String path,
	                                    Map<String, String> headers,
	                                    Map<String, String> parameters,
	                                    Integer timeOutInSeconds) {


		return patch(path, headers, parameters, null, timeOutInSeconds);
	}

	/**
	 * Step 1. prepare DELETE request
	 *
	 * @param path target url
	 * @return delete request
	 */
	public static HttpRequestBase delete(String path) {

		return delete(path, null, null);
	}

	/**
	 * Step 1. prepare DELETE request
	 *
	 * @param path             target url
	 * @param headers          request headers
	 * @param timeOutInSeconds time out in seconds or null for default time out
	 * @return delete request
	 */
	public static HttpRequestBase delete(String path,
	                                     Map<String, String> headers,
	                                     Integer timeOutInSeconds) {

		HttpDelete delete = new HttpDelete(path);
		addHeaders(delete, headers);
		delete.setConfig(getConfig(timeOutInSeconds));

		return delete;
	}

	/**
	 * Step 2. execute request
	 *
	 * @param request to be executed
	 * @return response
	 * @throws IOException in case of network failure
	 */
	public static HttpResponse execute(HttpRequestBase request) throws IOException {

		Assert.notNull(request, "Missing request!");
		HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new DefaultRedirectStrategy()).build();
		return client.execute(request);
	}

	/**
	 * Step 2. execute request asynchronously
	 *
	 * @param executor thread executor to be used
	 * @param request  to be executed
	 * @param callback to be invoked when request is completed or failes
	 */
	public static void executeAsync(Executor executor, HttpRequestBase request, FutureCallback<HttpResponse> callback) {

		try {
			executor.execute(new AsyncHttpCall(request, callback));
		}
		catch (Exception e) {
			log.error("Failed to execute asynchronously: " + request.getMethod() + " " + request.getURI().toString());
		}
	}

	private static class AsyncHttpCall implements Runnable {

		private final HttpRequestBase request;

		private final FutureCallback<HttpResponse> callback;

		AsyncHttpCall(HttpRequestBase req, FutureCallback<HttpResponse> back) {

			request = req;
			callback = back;
		}

		@Override
		public void run() {

			try (CloseableHttpAsyncClient client = HttpAsyncClients.createDefault()) {
				client.start();

				Future<HttpResponse> future = client.execute(request, callback);

				HttpResponse response = future.get();
				log.info("Request: " + request.getURI().toString() + " -> " + response.getStatusLine().getStatusCode());
			}
			catch (IOException | InterruptedException | ExecutionException e) {
				log.error("Failed to execute request: " + request);
			}
		}
	}


	/**
	 * Step 3. get content
	 * <p>
	 * extracts response content in case response was 200 (OK)
	 *
	 * @param response to get content from
	 * @return response string or null if unable to get content
	 */
	public static String getContentAsString(HttpResponse response) {

		try {
			if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				HttpEntity entity = response.getEntity();
				Header contentEncoding = entity.getContentEncoding();
				String encoding = contentEncoding != null ? contentEncoding.getValue() : UTF_8;
				return ResourceUtils.getString(entity.getContent(), encoding);
			}
		}
		catch (IOException e) {
			log.error("Failed to read response: ", e.getMessage());
		}

		return null;
	}

	/**
	 * Step 3. get content
	 * <p>
	 * extracts response content in case response was 200 (OK)
	 *
	 * @param response to get content from
	 * @return response as byte array
	 */
	public static byte[] getContent(HttpResponse response) {

		try {
			if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return ResourceUtils.getBytes(response.getEntity().getContent());
			}
		}
		catch (IOException e) {
			log.error("Failed to read response: ", e.getMessage());
		}

		return null;
	}

	private static void addHeaders(HttpRequest request, Map<String, String> headers) {

		if (headers != null && headers.size() > 0) {
			for (String name : headers.keySet()) {

				request.setHeader(new BasicHeader(name, headers.get(name)));
			}
		}
	}

	private static void addParameters(HttpEntityEnclosingRequestBase requestBase, Map<String, String> parameters) {

		if (parameters != null && parameters.size() > 0) {
			try {

				List<BasicNameValuePair> params = parameters.keySet()
				                                            .stream()
				                                            .map(name -> new BasicNameValuePair(name, parameters.get(name)))
				                                            .collect(Collectors.toList());

				requestBase.setEntity(new UrlEncodedFormEntity(params, UTF_8));
			}
			catch (UnsupportedEncodingException e) {
				throw new IllegalArgumentException("Cant encode parameters!", e);
			}
		}
	}

	private static RequestConfig getConfig(Integer timeOutInSeconds) {

		if (timeOutInSeconds == null) {
			timeOutInSeconds = TIME_OUT_IN_SECONDS;
		}

		int timeOut = timeOutInSeconds * 1000;
		return RequestConfig.custom()
		                    .setSocketTimeout(timeOut)
		                    .setConnectTimeout(timeOut)
		                    .setConnectionRequestTimeout(timeOut)
		                    .build();
	}
}

