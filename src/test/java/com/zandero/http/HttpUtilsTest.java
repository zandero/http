package com.zandero.http;

import com.zandero.http.test.HttpBinResponseJSON;
import com.zandero.utils.extra.JsonUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.trajano.commons.testing.UtilityClassTestUtil.assertUtilityClassWellDefined;
import static org.junit.Assert.*;

public class HttpUtilsTest {

	@Test
	public void testDefinition() throws ReflectiveOperationException {

		assertUtilityClassWellDefined(HttpUtils.class);
	}

	@Test
	public void testGet() throws Exception {

		HttpRequestBase req = HttpUtils.get("http://httpbin.org/get", null, null);
		HttpResponse res = HttpUtils.execute(req);

		assertEquals(HttpStatus.SC_OK, res.getStatusLine().getStatusCode());
	}

	@Test
	public void testPost() throws Exception {

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

		assertEquals(HttpStatus.SC_OK, res.getStatusLine().getStatusCode());

		response = HttpUtils.getContentAsString(res);
		assertNotNull(response);

		HttpBinResponseJSON json = JsonUtils.fromJson(response, HttpBinResponseJSON.class);
		assertEquals("Bla", json.headers.Bla);
		assertEquals("World", json.form.Hello);
	}

	@Test
	public void asyncClientRedirectTest() throws IOException, InterruptedException {

		HttpRequestBase req = HttpUtils.get("http://httpbin.org/redirect/1", null, null);
		HttpResponse res = HttpUtils.execute(req);

		assertEquals(HttpStatus.SC_OK, res.getStatusLine().getStatusCode());

		String response = HttpUtils.getContentAsString(res);
		assertNotNull(response);

		// async test
		final int[] result = new int[1];
		ExecutorService executor = Executors.newSingleThreadExecutor();
		HttpUtils.executeAsync(executor, req, new FutureCallback<HttpResponse>() {
			@Override
			public void completed(HttpResponse httpResponse) {

				result[0] = httpResponse.getStatusLine().getStatusCode();
			}

			@Override
			public void failed(Exception e) {

				assertFalse(true);
			}

			@Override
			public void cancelled() {

				assertFalse(true);
			}
		});

		Thread.sleep(2000L);
		assertEquals(HttpStatus.SC_OK, result[0]);

		executor.shutdownNow();
	}
}