/* --------------------------------------------------------------------------
 * @author Francis Labrie
 * Copyright by Francis Labrie
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.util;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This class provide various static methods that relax X509 certificate and
 * hostname verification while using the SSL over the HTTP protocol.
 * 
 */
public final class SSLUtilities {

	/**
	 * Hostname verifier.
	 */
	private static HostnameVerifier hostnameVerifier;

	/**
	 * Thrust managers.
	 */
	private static TrustManager[] trustManagers;

	/**
	 * No instantiation.
	 */
	private SSLUtilities() {
	}

	/**
	 * Return true if the protocol handler property java. protocol.handler.pkgs
	 * is set to the Sun's com.sun.net.ssl. internal.www.protocol deprecated
	 * one, false otherwise.
	 * 
	 * @return true if the protocol handler property is set to the Sun's
	 *         deprecated one, false otherwise.
	 */
	public static boolean isDeprecatedSSLProtocol() {
		return ("com.sun.net.ssl.internal.www.protocol".equals(System.getProperty("java.protocol.handler.pkgs")));
	}

	/**
	 * Set the default Hostname Verifier to an instance of a fake class that
	 * trust all hostnames.
	 */
	public static void trustAllHostnames() {
		// Create a trust manager that does not validate certificate chains
		if (hostnameVerifier == null) {
			hostnameVerifier = new FakeHostnameVerifier();
		}
		// Install the all-trusting host name verifier:
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
	}

	/**
	 * Set the default X509 Trust Manager to an instance of a fake class that
	 * trust all certificates, even the self-signed ones.
	 */
	public static void trustAllHttpsCertificates() {
		SSLContext context;

		// Create a trust manager that does not validate certificate chains
		if (trustManagers == null) {
			trustManagers = new TrustManager[]{new FakeX509TrustManager()};
		}
		// Install the all-trusting trust manager:
		try {
			context = SSLContext.getInstance("SSL");
			context.init(null, trustManagers, new SecureRandom());
		} catch (final GeneralSecurityException gse) {
			throw new IllegalStateException(gse.getMessage());
		} // catch
		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	}

	/**
	 * This class implements a fake hostname verificator, trusting any host
	 * name.
	 * 
	 * @author Francis Labrie
	 */
	public static class FakeHostnameVerifier implements HostnameVerifier {

		/**
		 * Always return true, indicating that the host name is an acceptable
		 * match with the server's authentication scheme.
		 * 
		 * @param aHostname
		 *            the host name.
		 * @param aSession
		 *            the SSL session used on the connection to host.
		 * @return the true boolean value indicating the host name is trusted.
		 */
		
		public boolean verify(String aHostname, javax.net.ssl.SSLSession aSession) {
			return (true);
		}
	}

	/**
	 * This class allow any X509 certificates to be used to authenticate the
	 * remote side of a secure socket, including self-signed certificates.
	 * 
	 * @author Francis Labrie
	 */
	public static class FakeX509TrustManager implements X509TrustManager {

		/**
		 * Empty array of certificate authority certificates.
		 */
		private static final X509Certificate[] ACCEPTED_ISSUERS = new X509Certificate[]{};

		/**
		 * Always trust for client SSL chain peer certificate chain with any
		 * authType authentication types.
		 * 
		 * @param aChain
		 *            the peer certificate chain.
		 * @param aAuthType
		 *            the authentication type based on the client certificate.
		 */
		
		public void checkClientTrusted(X509Certificate[] aChain, String aAuthType) {
		}

		/**
		 * Always trust for server SSL chain peer certificate chain with any
		 * authType exchange algorithm types.
		 * 
		 * @param aChain
		 *            the peer certificate chain.
		 * @param aAuthType
		 *            the key exchange algorithm used.
		 */
		
		public void checkServerTrusted(X509Certificate[] aChain, String aAuthType) {
		}

		/**
		 * Return an empty array of certificate authority certificates which are
		 * trusted for authenticating peers.
		 * 
		 * @return a empty array of issuer certificates.
		 */
		
		public X509Certificate[] getAcceptedIssuers() {
			return (ACCEPTED_ISSUERS);
		}
	}

}
