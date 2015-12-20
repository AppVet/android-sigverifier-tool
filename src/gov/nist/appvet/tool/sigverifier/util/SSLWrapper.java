/* This software was developed by employees of the National Institute of
 * Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 15 United States Code Section 105, works of NIST
 * employees are not subject to copyright protection in the United States
 * and are considered to be in the public domain.  As a result, a formal
 * license is not needed to use the software.
 * 
 * This software is provided by NIST as a service and is expressly
 * provided "AS IS".  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
 * AND DATA ACCURACY.  NIST does not warrant or make any representations
 * regarding the use of the software or the results thereof including, but
 * not limited to, the correctness, accuracy, reliability or usefulness of
 * the software.
 * 
 * Permission to use this software is contingent upon your acceptance
 * of the terms of this agreement.
 */
package gov.nist.appvet.tool.sigverifier.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

public class SSLWrapper {
	@SuppressWarnings("deprecation")
	public static HttpClient wrapClient(HttpClient base) {
		SSLContext ctx = null;
		X509TrustManager tm = null;
		SSLSocketFactory ssf = null;
		SchemeRegistry sr = null;
		try {
			ctx = SSLContext.getInstance("TLSv1.2");
			tm = new X509TrustManager() {

				@Override
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

			};
			ctx.init(null, new TrustManager[] { tm }, null);
			ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			final ClientConnectionManager ccm = base.getConnectionManager();
			sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (final Exception e) {
			return null;
		} finally {
			sr = null;
			ssf = null;
			tm = null;
			ctx = null;
		}
	}

}