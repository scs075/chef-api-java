package edu.tongji.wang.chefapi.method;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import edu.tongji.wang.chefapi.ChefAuthUtils;

public class ApiMethod {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private HttpClient client = null;
	protected HttpMethod method = null;
	protected String reqBody = "";
	protected String userId = "";
	protected String pemPath = "";
	private String methodName = "GET";

	private int returnCode;

	public ApiMethod(String methodName){
		client = getNewHttpClient();
		this.methodName = methodName;
	}

	public ApiMethod execute(){
		log.info("method to be executed =>"+method.getPath());
		String hashedPath = ChefAuthUtils.sha1AndBase64(method.getPath());
		String hashedBody = ChefAuthUtils.sha1AndBase64(reqBody);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String timeStamp = sdf.format(new Date());
		timeStamp = timeStamp.replace(" ", "T");
		timeStamp = timeStamp + "Z";

		StringBuilder sb = new StringBuilder();
		sb.append("Method:").append(methodName).append("\n");
		sb.append("Hashed Path:").append(hashedPath).append("\n");
		sb.append("X-Ops-Content-Hash:").append(hashedBody).append("\n");
		sb.append("X-Ops-Timestamp:").append(timeStamp).append("\n");
		sb.append("X-Ops-UserId:").append(userId);

		String auth_String = ChefAuthUtils.signWithRSA(sb.toString(), pemPath);
		String[] auth_headers = ChefAuthUtils.splitAs60(auth_String);

		method.addRequestHeader("Accept", "application/json");
		method.addRequestHeader("Content-type", "application/json");
		method.addRequestHeader("Host", method.getHostConfiguration().getHost());
		method.addRequestHeader("X-Chef-Version", "11.4.0");
		method.addRequestHeader("X-Ops-Timestamp", timeStamp);
		method.addRequestHeader("X-Ops-Userid", userId);
		method.addRequestHeader("X-Ops-Content-Hash", hashedBody);
		method.addRequestHeader("X-Ops-Sign", "version=1.0");


		for (int i = 0; i < auth_headers.length; i++) {
			method.addRequestHeader("X-Ops-Authorization-" + (i + 1), auth_headers[i]);
		}

		try {
			returnCode = client.executeMethod(method);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return this;
	}

	public void setHeaders(Header[] headers){
		for(Header header : headers){
			this.method.addRequestHeader(header);
		}
	}

	public InputStream getResponseBodyAsStream() {
		try {
			return method.getResponseBodyAsStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getResponseBodyAsString(){
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(method.getResponseBodyAsStream()));
			if(reader.ready())
				reqBody = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			method.releaseConnection();
		}
		return reqBody;
	}

	public int getReturnCode(){
		return returnCode;
	}

	public String getReqBody() {
		return reqBody;
	}

	public void setReqBody(String body) {
		this.reqBody = body;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPemPath() {
		return pemPath;
	}

	public void setPemPath(String pemPath) {
		this.pemPath = pemPath;
	}

	@SuppressWarnings("deprecation")
	private HttpClient getNewHttpClient() {
		try {

			ProtocolSocketFactory psf = new MySSLSocketFactory();
			Protocol.registerProtocol("https", 
					new Protocol("https", psf, 443));

			return new HttpClient();
		} catch (Exception e) {
			log.warning("error while registering custom protocol" + e.getMessage());
			log.warning("will proceed with simple HttpClient");
			return new HttpClient();
		}
	}
}
