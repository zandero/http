package com.zandero.http;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.*;

import javax.servlet.http.*;
import java.nio.charset.*;
import java.util.*;

import static com.zandero.utils.junit.AssertFinalClass.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RequestUtilsTest {

    private static final String SESSION_HEADER = "X-Session";

    @Mock
    HttpServletRequest request;

    @Test
    void testDefinition() {

        isWellDefined(RequestUtils.class);
    }

    /**
     * @return produces mocked request
     */
    private HttpServletRequest getRequest() {

        Mockito.when(request.getHeader("User-Agent")).thenReturn("Agent");
        Mockito.when(request.getHeader(SESSION_HEADER)).thenReturn("Session");

        return request;
    }

    @Test
    void testGetUserAgent() {

        assertNull(RequestUtils.getUserAgent(null));
        assertNull(RequestUtils.getUserAgent(request));
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

        String userCredentials = "USERNAME:PASSWORD";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes(StandardCharsets.UTF_8)));
        Mockito.when(request.getHeader("Authorization")).thenReturn(basicAuth);

        assertTrue(RequestUtils.checkBasicAuth(request, "USERNAME", "PASSWORD"));
        assertFalse(RequestUtils.checkBasicAuth(request, "Bla", "Bla"));
        assertFalse(RequestUtils.checkBasicAuth(request, null, null));

        Mockito.when(request.getHeader("Authorization")).thenReturn(null);
        assertFalse(RequestUtils.checkBasicAuth(request, null, null));
    }
}