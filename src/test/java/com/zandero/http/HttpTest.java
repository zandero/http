package com.zandero.http;

import com.zandero.http.test.*;
import com.zandero.utils.*;
import com.zandero.utils.extra.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.util.*;

import static com.zandero.utils.junit.AssertFinalClass.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Using http://httpbin.org/ for endpoints
 */
class HttpTest {

    private static final String HTTP_BIN_ROOT = "https://httpbingo.org/";

    @Test
    void isFinalClass() {

        isWellDefined(Http.class);
    }

    @Test
    void get() throws Exception {

        Http.setSSLSocketFactory(TrustAnyTrustManager.getSSLFactory());

        Http.Response res = Http.get(HTTP_BIN_ROOT + "get");
        assertEquals(HttpURLConnection.HTTP_OK, res.getCode());

        HttpBinResponseJSON json = JsonUtils.fromJson(res.getResponse(), HttpBinResponseJSON.class);
        assertEquals(SetUtils.from("text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2"),
                     json.headers.get("Accept"));
    }

    @Test
    void getSsl() throws Exception {

        Http.setSSLSocketFactory(TrustAnyTrustManager.getSSLFactory());

        Http.Response res = Http.get(HTTP_BIN_ROOT + "get"); // FAILING on HTTPS ... trustore issue
        assertEquals(HttpURLConnection.HTTP_OK, res.getCode());

        HttpBinResponseJSON json = JsonUtils.fromJson(res.getResponse(), HttpBinResponseJSON.class);
        assertEquals(SetUtils.from("text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2"), json.headers.get("Accept"));
    }

    @Test
    void post() throws Exception {

        Http.Response res = Http.post(HTTP_BIN_ROOT + "post", null, null, null);
        assertEquals(HttpURLConnection.HTTP_OK, res.getCode());
        assertNotNull(res.getResponse());

        // post with content
        Map<String, String> headers = new HashMap<>();
        headers.put("bla", "Bla");

        Map<String, String> formParam = new HashMap<>();
        formParam.put("Hello", "World");
        String formParams = UrlUtils.composeQuery(formParam);

        res = Http.post(HTTP_BIN_ROOT + "post", formParams, null, headers);

        assertEquals(HttpURLConnection.HTTP_OK, res.getCode());
        assertNotNull(res.getResponse());

        HttpBinResponseJSON json = JsonUtils.fromJson(res.getResponse(), HttpBinResponseJSON.class);
        assertEquals(SetUtils.from("Bla"), json.headers.get("Bla"));
        assertEquals(SetUtils.from("World"), json.form.get("Hello"));
    }

    @Test
    void postSsl() throws Exception {

        Http.Response res = Http.post(HTTP_BIN_ROOT + "post", null, null, null);
        assertEquals(HttpURLConnection.HTTP_OK, res.getCode());
        assertNotNull(res.getResponse());

        // post with content
        Map<String, String> headers = new HashMap<>();
        headers.put("bla", "Bla");

        Map<String, String> formParam = new HashMap<>();
        formParam.put("Hello", "World");
        String formParams = UrlUtils.composeQuery(formParam);

        res = Http.post(HTTP_BIN_ROOT + "post", formParams, null, headers);

        assertEquals(HttpURLConnection.HTTP_OK, res.getCode());
        assertNotNull(res.getResponse());

        HttpBinResponseJSON json = JsonUtils.fromJson(res.getResponse(), HttpBinResponseJSON.class);
        assertEquals(SetUtils.from("Bla"), json.headers.get("Bla"));
        assertEquals(SetUtils.from("World"), json.form.get("Hello"));
    }

    @Test
    void put() throws Exception {

        Http.Response res = Http.put(HTTP_BIN_ROOT + "put", null, null, null);
        assertEquals(HttpURLConnection.HTTP_OK, res.getCode());
        assertNotNull(res.getResponse());

        // post with content
        Map<String, String> headers = new HashMap<>();
        headers.put("bla", "Bla");

        Map<String, String> formParam = new HashMap<>();
        formParam.put("Hello", "World");
        String formParams = UrlUtils.composeQuery(formParam);

        res = Http.put(HTTP_BIN_ROOT + "put", formParams, null, headers);

        assertEquals(HttpURLConnection.HTTP_OK, res.getCode());
        assertNotNull(res.getResponse());

        HttpBinResponseJSON json = JsonUtils.fromJson(res.getResponse(), HttpBinResponseJSON.class);
        assertEquals(SetUtils.from("Bla"), json.headers.get("Bla"));
        assertEquals("Hello=World", json.data);
    }

    @Test
    void delete() throws Http.HttpException {

        Http.Response res = Http.delete(HTTP_BIN_ROOT + "delete");
        assertEquals(HttpURLConnection.HTTP_OK, res.getCode());

        HttpBinResponseJSON json = JsonUtils.fromJson(res.getResponse(), HttpBinResponseJSON.class);
        assertEquals(SetUtils.from("text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2"), json.headers.get("Accept"));
    }
}