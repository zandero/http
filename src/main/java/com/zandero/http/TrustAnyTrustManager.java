package com.zandero.http;

import javax.net.ssl.*;
import java.security.*;
import java.security.cert.*;

/**
 * Dummy trust manager allowing everybody
 * not intended for production use ...
 */
public class TrustAnyTrustManager implements X509TrustManager {

    /**
     * Dummy trust manager allowing everybody
     */
    public TrustAnyTrustManager(){
        super();
    }

    /**
	 * Dummy implementation
     * checks client is trusted (no implementation)
     *
     * @param chain    certificate chain
     * @param authType authorization type
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    /**
	 * Dummy implementation
     * checks server is trusted (no implementation)
     *
     * @param chain    certificate chain
     * @param authType authorization type
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    /**
     * List of issuers
     *
     * @return list of accepted issuers
     */
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
    }

    /**
     * Simple SSL socket factory trusting everybody
     *
     * @return SSL socket factory
     * @throws KeyManagementException key management exception
     * @throws NoSuchAlgorithmException missing algorithm exception
     */
    public static SSLSocketFactory getSSLFactory() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
        return sc.getSocketFactory();
    }
}