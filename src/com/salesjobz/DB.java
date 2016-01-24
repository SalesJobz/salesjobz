package com.salesjobz;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Hex;

public class DB {
	
	public static String execute(String method, String payload){
		
		String result = null;
    	try{
    	String url = "https://dynamodb.us-west-2.amazonaws.com";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
		df.setTimeZone(tz);
		String nowAsISO = df.format(new Date());
		
		df = new SimpleDateFormat("yyyyMMdd");
		df.setTimeZone(tz);
		String date = df.format(new Date());
		
		con.setRequestProperty("host", "dynamodb.us-west-2.amazonaws.com");
		con.setRequestProperty("x-amz-date", nowAsISO);
		con.setRequestProperty("x-amz-target", "DynamoDB_20120810."+method);
		
		

		   String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(payload); 
		 //  System.out.println(sha256hex);
		   String canonicalRequest = "POST\n/\n\nhost:dynamodb.us-west-2.amazonaws.com\nx-amz-date:"+nowAsISO+"\nx-amz-target:DynamoDB_20120810."+method+"\n\nhost;x-amz-date;x-amz-target\n"+sha256hex;
		//   System.out.println(canonicalRequest);
		   String hashCanonicalRequest = org.apache.commons.codec.digest.DigestUtils.sha256Hex(canonicalRequest);
		//   System.out.println("h "+hashCanonicalRequest);
		   String stringToSign = "AWS4-HMAC-SHA256\n"+nowAsISO+"\n"+date+"/us-west-2/dynamodb/aws4_request\n"+hashCanonicalRequest;
		//   System.out.println(stringToSign);
			byte[] signatureKey = getSignatureKey("Qxim8f/uvE9igI3qaOTbYT3bt1XvWd8LEC8jm5eI", date, "us-west-2", "dynamodb");

		String signature = Hex.encodeHexString( HmacSHA256(stringToSign,signatureKey) );
	//	System.out.println(signature);
		String authString = "AWS4-HMAC-SHA256 Credential=AKIAI7LNLAEVF77R34BQ/"+date+"/us-west-2/dynamodb/aws4_request,SignedHeaders=host;x-amz-date;x-amz-target,Signature="+signature;
	//	System.out.println(authString);
		con.setRequestProperty("Authorization", authString);
		con.setRequestProperty("content-type", "application/x-amz-json-1.0");
		


		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(payload);
		wr.flush();
		wr.close();

	//	int responseCode = con.getResponseCode();
	//	System.out.println("\nSending 'POST' request to URL : " + url);
	//	System.out.println("Post parameters : " + urlParameters);
	//	System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
	//	System.out.println(response.toString());
		result = response.toString();
    	
    	} catch (Exception e) {
			return e.getLocalizedMessage();
		}
    //	System.out.println(result);		
		return result;

	}
	
    static byte[] HmacSHA256(String data, byte[] key) throws Exception  {
        String algorithm="HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF8"));
   }

   static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception  {
        byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
        byte[] kDate    = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion  = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(serviceName, kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);
        return kSigning;
   }

}
