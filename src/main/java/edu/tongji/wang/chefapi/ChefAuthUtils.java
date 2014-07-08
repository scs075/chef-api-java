package edu.tongji.wang.chefapi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Base64;

public class ChefAuthUtils {
	
	private static final int SPLIT_AT = 60;
	
	public static String sha1AndBase64(String inStr) {
		MessageDigest md = null;
		byte[] outbty = null;
		try {
			md = MessageDigest.getInstance("SHA-1"); 
			byte[] digest = md.digest(inStr.getBytes()); 
			outbty = Base64.encode(digest);
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		}
		return new String(outbty);
	}
	
	public static String signWithRSA(String inStr, String pemPath) {
		byte[] outStr = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(pemPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Security.addProvider(new BouncyCastleProvider());
		try {
			KeyPair kp = (KeyPair) new PEMReader(br).readObject();
			PrivateKey privateKey = kp.getPrivate();
			Signature instance = Signature.getInstance("RSA");
			instance.initSign(privateKey);
			instance.update(inStr.getBytes());

			byte[] signature = instance.sign();
			outStr = Base64.encode(signature);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new String(outStr);
	}
	
	public static String[] splitAs60(String inStr) {
		int count = inStr.length() / SPLIT_AT;
		String[] out = new String[count + 1];

		for (int i = 0; i < count; i++) {
			String tmp = inStr.substring(i * SPLIT_AT, i * SPLIT_AT + SPLIT_AT);
			out[i] = tmp;
		}
		if (inStr.length() > count * SPLIT_AT) {
			String tmp = inStr.substring(count * SPLIT_AT, inStr.length());
			out[count] = tmp;
		}
		return out;
	}
	
}
