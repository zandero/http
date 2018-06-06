package com.zandero.http;

import com.zandero.http.test.HttpBinResponseJSON;
import com.zandero.utils.extra.JsonUtils;
import com.zandero.utils.extra.UrlUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import static com.zandero.utils.junit.AssertFinalClass.isWellDefined;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Using http://httpbin.org/ for endpoints
 */
class HttpTest {

	@Test
	void isFinalClass() {

		isWellDefined(Http.class);
	}

	@Test
	void testGet() throws Exception {

		Http.Response res = Http.get("http://httpbin.org/get");
		assertEquals(HttpURLConnection.HTTP_OK, res.getCode());

		HttpBinResponseJSON json = JsonUtils.fromJson(res.getResponse(), HttpBinResponseJSON.class);
		assertEquals("text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2", json.headers.get("Accept"));
	}

	@Disabled
	@Test
	void testHttpsGet() throws Exception {

		Http.Response res = Http.get("https://httpbin.org/get"); // FAILING on HTTPS ... trustore issue
		assertEquals(HttpURLConnection.HTTP_OK, res.getCode());

		HttpBinResponseJSON json = JsonUtils.fromJson(res.getResponse(), HttpBinResponseJSON.class);
		assertEquals("text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2", json.headers.get("Accept"));
	}

	@Test
	void testPost() throws Exception {

		Http.Response res = Http.post("http://httpbin.org/post", null, null, null);
		assertEquals(HttpURLConnection.HTTP_OK, res.getCode());
		assertNotNull(res.getResponse());

		// post with content
		Map<String, String> headers = new HashMap<>();
		headers.put("bla", "Bla");

		Map<String, String> formParam = new HashMap<>();
		formParam.put("Hello", "World");
		String formParams = UrlUtils.composeQuery(formParam);

		res = Http.post("http://httpbin.org/post", formParams, null, headers);

		assertEquals(HttpURLConnection.HTTP_OK, res.getCode());
		assertNotNull(res.getResponse());

		HttpBinResponseJSON json = JsonUtils.fromJson(res.getResponse(), HttpBinResponseJSON.class);
		assertEquals("Bla", json.headers.get("Bla"));
		assertEquals("World", json.form.get("Hello"));
	}

	@Test
	void testPut() throws Exception {

		Http.Response res = Http.put("http://httpbin.org/put", null, null, null);
		assertEquals(HttpURLConnection.HTTP_OK, res.getCode());
		assertNotNull(res.getResponse());

		// post with content
		Map<String, String> headers = new HashMap<>();
		headers.put("bla", "Bla");

		Map<String, String> formParam = new HashMap<>();
		formParam.put("Hello", "World");
		String formParams = UrlUtils.composeQuery(formParam);

		res = Http.put("http://httpbin.org/put", formParams, null, headers);

		assertEquals(HttpURLConnection.HTTP_OK, res.getCode());
		assertNotNull(res.getResponse());

		HttpBinResponseJSON json = JsonUtils.fromJson(res.getResponse(), HttpBinResponseJSON.class);
		assertEquals("Bla", json.headers.get("Bla"));
		assertEquals("Hello=World", json.data);
	}

	@Test
	void testDelete() throws Http.HttpException {

		Http.Response res = Http.delete("http://httpbin.org/delete");
		assertEquals(HttpURLConnection.HTTP_OK, res.getCode());

		HttpBinResponseJSON json = JsonUtils.fromJson(res.getResponse(), HttpBinResponseJSON.class);
		assertEquals("text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2", json.headers.get("Accept"));
	}
}