/*******************************************************************************
 * Cloud Foundry
 * Copyright (c) [2009-2016] Pivotal Software, Inc. All Rights Reserved.
 * <p>
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 * <p>
 * This product includes a number of subcomponents with
 * separate copyright notices and license terms. Your use of these
 * subcomponents is subject to the terms and conditions of the
 * subcomponent's license, as noted in the LICENSE file.
 *******************************************************************************/
package org.springframework.security.community.samples;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class ApacheDSHelper {
	public static ApacheDsSSLContainer start() throws Exception {
		return start(33389, 33636);
	}

	public static ApacheDsSSLContainer start(int port, int sslPort) throws Exception {
		ApacheDsSSLContainer apacheDS;
		File tmpDir;

		tmpDir = new File(System.getProperty("java.io.tmpdir") + "/apacheds/" + UUID.randomUUID().toString());
		tmpDir.deleteOnExit();
		System.out.println(tmpDir);
		//configure properties for running against ApacheDS
		apacheDS = new ApacheDsSSLContainer("dc=test,dc=com",
			new Resource[]{new ClassPathResource("ldap_init_apacheds.ldif"), new ClassPathResource("ldap_init.ldif")});
		apacheDS.setWorkingDirectory(tmpDir);
		apacheDS.setPort(port);
		apacheDS.setSslPort(sslPort);
		apacheDS.afterPropertiesSet();

		return apacheDS;
	}

	public static X509Certificate getSelfSignedCertificate(X500Name x500Name,
														   Date issueDate,
														   long validForSeconds,
														   KeyPair keyPair,
														   String signatureAlgorithm)
		throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException,
			   NoSuchProviderException {

		try {
			Date expirationDate = new Date();
			expirationDate.setTime(issueDate.getTime() + validForSeconds * 1000L);

			X509CertInfo certInfo = new X509CertInfo();
			certInfo.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
			certInfo.set(
				X509CertInfo.SERIAL_NUMBER,
				new CertificateSerialNumber((new Random()).nextInt() & Integer.MAX_VALUE)
			);
			certInfo.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(AlgorithmId.get(signatureAlgorithm)));

			certInfo.set(X509CertInfo.SUBJECT, x500Name);
			certInfo.set(X509CertInfo.ISSUER, x500Name);

			certInfo.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
			certInfo.set(X509CertInfo.VALIDITY, new CertificateValidity(issueDate, expirationDate));

			X509CertImpl selfSignedCert = new X509CertImpl(certInfo);
			selfSignedCert.sign(keyPair.getPrivate(), signatureAlgorithm);
			return selfSignedCert;
		} catch (IOException ioe) {
			throw new CertificateEncodingException("Error during creation of self-signed Certificate: " +
				ioe.getMessage(), ioe);
		}
	}
}
