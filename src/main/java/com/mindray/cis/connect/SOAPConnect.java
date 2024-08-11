/**
 * 
 */
package com.mindray.cis.connect;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author Administrator
 *
 */
public class SOAPConnect implements IConnect {

	public static final CloseableHttpClient mClient = HttpClients.createDefault();

	/**
	 * soap 请求.
	 *
	 * @param requestBody
	 * @param requestUrl
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String SendPostRequest(String requestBody, String requestUrl) {
		HttpPost post = new HttpPost(requestUrl);
		try {
			post.setEntity(new StringEntity(requestBody));
		} catch (UnsupportedEncodingException ignored) {
		}
		CloseableHttpResponse response = null;
		String result = null;
		try {
			response = mClient.execute(post);
			String data = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			int begin = data.indexOf("{");
			int end = data.lastIndexOf("}");
			result = data.substring(begin, end + 1);
		} catch (IOException ignored) {
			ignored.printStackTrace();
		} finally {
			HttpClientUtils.closeQuietly(response);
		}
		return result;
	}

	//requestBody通过拼装xml的方式进行
	public static String CheckSouyueUserPropertyExists(String propKey, String propValue) {

		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append(
				"<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://usercenter.zhongsou.com/soap\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns2=\"http://xml.apache.org/xml-soap\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
		sb.append("<SOAP-ENV:Header>");
		sb.append("<ns1:Auth SOAP-ENV:actor=\"http://schemas.xmlsoap.org/soap/actor/next\">");
		sb.append("<item>");
		sb.append("<key>user_name</key>");
		sb.append("<value>" + "AUTH_USER_NAME" + "</value>");
		sb.append("</item>");
		sb.append("<item>");
		sb.append("<key>pwd</key>");
		sb.append("<value>" + "AUTH_PWD" + "</value>");
		sb.append("</item>");
		sb.append("<item>");
		sb.append("<key>sign</key>");
		sb.append("<value>" + "AUTH_SIGN" + "</value>");
		sb.append("</item>");
		sb.append("</ns1:Auth>");
		sb.append("</SOAP-ENV:Header>");
		sb.append("<SOAP-ENV:Body>");
		if (propKey.equalsIgnoreCase("email")) {
			sb.append("<ns1:CheckEmailExists>");
			sb.append("<value>" + propValue + "</value>");
			sb.append("</ns1:CheckEmailExists>");
		} else if (propKey.equalsIgnoreCase("phone")) {
			sb.append("<ns1:CheckPhoneExists>");
			sb.append("<value>" + propValue + "</value>");
			sb.append("</ns1:CheckPhoneExists>");
		} else {
			sb.append("<ns1:CheckUserNameExists>");
			sb.append("<value>" + propValue + "</value>");
			sb.append("</ns1:CheckUserNameExists>");
		}
		sb.append("</SOAP-ENV:Body>");
		sb.append("</SOAP-ENV:Envelope>");

		String requestBody = sb.toString();
		System.out.println(requestBody);

		return requestBody;
	}

}
