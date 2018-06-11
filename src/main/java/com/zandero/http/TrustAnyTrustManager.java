package com.zandero.http;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * trust all - not recommended for production use ...
 */
public class TrustAnyTrustManager implements X509TrustManager {

	public void checkClientTrusted(X509Certificate[] chain, String authType) {}

	public void checkServerTrusted(X509Certificate[] chain, String authType) {}

	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[] {};
	}

	public static SSLSocketFactory getSSLFactory() throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
		return sc.getSocketFactory();
	}
}