package com.zandero.http;

import com.zandero.http.test.HttpBinResponseJSON;
import com.zandero.utils.extra.JsonUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zandero.utils.junit.AssertFinalClass.isWellDefined;
import static org.junit.jupiter.api.Assertions.*;

class HttpUtilsTest {

	@Test
	void testDefinition() {

		isWellDefined(HttpUtils.class);
	}

	@Test
	void testGet() throws Exception {

		HttpRequestBase req = HttpUtils.get("http://httpbin.org/get", null, null);
		HttpResponse res = HttpUtils.execute(req);

		assertEquals(HttpStatus.SC_OK, res.getStatusLine().getStatusCode());
	}

	@Disabled
	@Test
	void testHttpsGet() throws Exception {

		HttpRequestBase req = HttpUtils.get("https://httpbin.org/get", null, null);
		HttpResponse res = HttpUtils.execute(req);

		assertEquals(HttpStatus.SC_OK, res.getStatusLine().getStatusCode());
	}

	@Test
	void testPost() throws Exception {

		HttpRequestBase req = HttpUtils.post("http://httpbin.org/post", null, null, null);
		HttpResponse res = HttpUtils.execute(req);
		assertEquals(HttpStatus.SC_OK, res.getStatusLine().getStatusCode());

		String response = HttpUtils.getContentAsString(res);
		assertNotNull(response);

		// post with content
		Map<String, String> headers = new HashMap<>();
		headers.put("bla", "Bla");

		Map<String, String> params = new HashMap<>();
		params.put("Hello", "World");

		req = HttpUtils.post("http://httpbin.org/post", headers, params, null);
		res = HttpUtils.execute(req);

		response = HttpUtils.getContentAsString(res);
		assertNotNull(response);

		HttpBinResponseJSON json = JsonUtils.fromJson(response, HttpBinResponseJSON.class);
		assertEquals("Bla", json.headers.get("Bla"));
		assertEquals("World", json.form.get("Hello"));
	}

	@Test
	void asyncClientRedirectTest() throws IOException, InterruptedException {

		HttpRequestBase req = HttpUtils.get("http://httpbin.org/redirect/1", null, null);
		HttpResponse res = HttpUtils.execute(req);

		assertEquals(HttpStatus.SC_OK, res.getStatusLine().getStatusCode());

		String response = HttpUtils.getContentAsString(res);
		assertNotNull(response);

		// async test
		final int[] result = new int[1];
		ExecutorService executor = Executors.newSingleThreadExecutor();
		HttpUtils.executeAsync(executor, req, new FutureCallback<HttpResponse>() {
			@Override public void completed(HttpResponse httpResponse) {

				result[0] = httpResponse.getStatusLine().getStatusCode();
			}

			@Override public void failed(Exception e) {

				assertFalse(true);
			}

			@Override public void cancelled() {

				assertFalse(true);
			}
		});

		Thread.sleep(2000L);
		assertEquals(HttpStatus.SC_OK, result[0]);

		executor.shutdownNow();
	}
}