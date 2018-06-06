package com.zandero.http;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Base64;

import static com.zandero.utils.junit.AssertFinalClass.isWellDefined;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class RequestUtilsTest {

	private static final String SESSION_HEADER = "X-Session";

	@Test
	void testDefinition() {

		isWellDefined(RequestUtils.class);
	}

	/**
	 * @return produces mocked request
	 */
	private HttpServletRequest getRequest() {

		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getHeader("User-Agent")).thenReturn("Agent");
		Mockito.when(request.getHeader(SESSION_HEADER)).thenReturn("Session");

		return request;
	}

	private HttpServletRequest getEmptyRequest() {

		return Mockito.mock(HttpServletRequest.class);
	}

	@Test
	void testGetUserAgent() {

		assertNull(RequestUtils.getUserAgent(null));
		assertNull(RequestUtils.getUserAgent(getEmptyRequest()));
		assertEquals("Agent", RequestUtils.getUserAgent(getRequest()));
	}

	@Test
	void isIpAllowedTest() {

		assertFalse(RequestUtils.isIpAddressAllowed(null));
		assertFalse(RequestUtils.isIpAddressAllowed(null, ""));
		assertFalse(RequestUtils.isIpAddressAllowed("", ""));

		assertFalse(RequestUtils.isIpAddressAllowed("1.1.1.1", "1.2.3.4"));
		assertFalse(RequestUtils.isIpAddressAllowed("1.1.1.1", "104.192.143.208/28"));


		assertTrue(RequestUtils.isIpAddressAllowed("1.1.1.1", "1.2.3.4", "1.1.1.1"));

		// 104.192.143.192 ... 104.192.143.207
		// 104.192.143.208 ... 104.192.143.223
		assertFalse(RequestUtils.isIpAddressAllowed("104.192.143.191", "104.192.143.192/28", "104.192.143.208/28"));

		assertTrue(RequestUtils.isIpAddressAllowed("104.192.143.192", "104.192.143.192/28", "104.192.143.208/28"));
		assertTrue(RequestUtils.isIpAddressAllowed("104.192.143.207", "104.192.143.192/28", "104.192.143.208/28"));
		assertTrue(RequestUtils.isIpAddressAllowed("104.192.143.208", "104.192.143.192/28", "104.192.143.208/28"));
		assertTrue(RequestUtils.isIpAddressAllowed("104.192.143.223", "104.192.143.192/28", "104.192.143.208/28"));

		assertFalse(RequestUtils.isIpAddressAllowed("104.192.143.224", "104.192.143.192/28", "104.192.143.208/28"));
	}

	@Test
	void testBasicAuth() {

		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

		String userCredentials = "USERNAME:PASSWORD";
		String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes(Charset.forName("UTF-8"))));
		Mockito.when(request.getHeader("Authorization")).thenReturn(basicAuth);

		assertTrue(RequestUtils.checkBasicAuth(request, "USERNAME", "PASSWORD"));
		assertFalse(RequestUtils.checkBasicAuth(request, "Bla", "Bla"));
		assertFalse(RequestUtils.checkBasicAuth(request, null, null));

		Mockito.when(request.getHeader("Authorization")).thenReturn(null);
		assertFalse(RequestUtils.checkBasicAuth(request, null, null));
	}
}