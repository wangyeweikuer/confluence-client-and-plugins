package com.baidu.bkm.wiki.client.xmlrpc;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import org.apache.log4j.Logger;

/**
 * 客户端连接时的配置信息
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Nov 1, 2013 4:24:42 PM
 */
public class ClientSettings {
	
	private static final Logger	log	= Logger.getLogger(ClientSettings.class);
	/**
	 * base url to the confluence in question.
	 */
	private final URL			url;
	/**
	 * location of truststore file corresponding with CA if confluence is protected by SSL. Leave null if not using.
	 */
	private String				truststore;
	/**
	 * password that was used when generating truststore file corresponding with CA if confluence is protected by SSL.
	 * Leave null if not using.
	 */
	private String				trustpass;
	/**
	 * option to allow CRJW to trust all certs. should be "true", "false". If null or empty, the default value is false.
	 * WARNING: If trustallcerts is set to true, use of the remote api could be vulnerable to
	 * "man in the middle attacks".
	 */
	public String				trustallcerts;
	
	public ClientSettings(String protocal, String host, int port) throws MalformedURLException {
		url = new URL(protocal, host, port, "/rpc/xmlrpc");
		if ("https".equals(protocal)) {
			addSSLSettings();
		}
	}
	
	public ClientSettings(String host, int port) throws MalformedURLException {
		this("http", host, port);
	}
	
	public ClientSettings(String host) throws MalformedURLException {
		this("http", host, 80);
	}
	
	/**
	 * sets up ssl properties and security provider, given confSettings. Note: location to valid certificate truststore
	 * and related password may be necessary.
	 * @param confSettings
	 * @throws IllegalArgumentException - thrown if confSettings do not contain necessary security parameters. Example:
	 *         location to existing truststore.
	 */
	@SuppressWarnings("restriction")
	protected void addSSLSettings() {
		if ("true".equals(trustallcerts)) {
			addTrustAllCertsSettings();
			return;
		}
		if (truststore == null || !new File(truststore).exists()) {
			throw new IllegalArgumentException("IllegalArgumentException: port out of range" + ": " + truststore);
		}
		// setting the cert verification callback for ssl
		System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
		// sets the "truststore" which contains the public key of the
		// CA which signed the certificate that tomcat's using.
		// we get a (self-signed one) by exporting the keystore to a certificate and
		// then importing that cert to a truststore. See:
		// keytool -export -alias tomcat -rfc -file tomcat.crt
		// keytool -import -alias servercert -file tomcat.crt -keystore truststore
		System.setProperty("javax.net.ssl.trustStore", truststore);
		// password used when we created the truststore
		System.setProperty("javax.net.ssl.trustStorePassword", trustpass);
		// set up the security provider
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
	}
	
	/**
	 * add security settings to trust all certificates. Warning: This can expose the user to man in the middle attacks.
	 * Code copied pretty much wholesale from the Apache XMLRPC library doc: http://ws.apache.org/xmlrpc/ssl.html
	 */
	private void addTrustAllCertsSettings() {
		// create an all trusting manager class
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
			
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			
			@SuppressWarnings("unused")
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
				// Trust always
			}
			
			@SuppressWarnings("unused")
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
				// Trust always
			}
			
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub
			}
		}};
		// Install the all-trusting trust manager
		SSLContext sc;
		try {
			sc = SSLContext.getInstance("SSL");
			// Create empty HostnameVerifier
			HostnameVerifier hv = new HostnameVerifier() {
				
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			};
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (NoSuchAlgorithmException e) {
			log.error("Problem setting up trust manager.");
			e.printStackTrace();
		} catch (KeyManagementException e) {
			log.error("Error initializing context");
			e.printStackTrace();
		}
	}
	
	public void setTrustpass(String trustpass) {
		this.trustpass = trustpass;
	}
	
	public void setTruststore(String truststore) {
		this.truststore = truststore;
	}
	
	public void setTrustallcerts(String trustallcerts) {
		this.trustallcerts = trustallcerts;
	}
	
	public URL getUrl() {
		return url;
	}
}
