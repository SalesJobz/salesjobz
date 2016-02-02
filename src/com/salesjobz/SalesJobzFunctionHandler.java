package com.salesjobz;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class SalesJobzFunctionHandler implements RequestHandler<Object, Object> {

	@Override
    public Object handleRequest(Object inputObj, Context context){

		Object result = null;
     	try {
     		LinkedHashMap input = (LinkedHashMap)inputObj;
     		
     		String method  = (String) (input).get("method");
     		// comment
     		switch(method){
     			
     			case "createCandidateProfile" :
     				result = createCandidateProfile(input);
     			break;
     			
     			case "getCandidateProfile" :
     				result = getCandidateProfile(input);
     			break;
     			
     			case "updateCandidateProfile" :
     				result = updateCandidateProfile(input);
     			break;
     			
     			case "deleteCandidateProfile" :
     				result = deleteCandidateProfile(input);         			
     			break;
     			
     			case "signIn" :
         			
     			break;
     			
     			case "signOut" :
         			
     			break;
     			
     			default :
     				throw new Exception("Unknown method : "+ method);
     				
     		}
     		
		} catch (Exception e) {
			
			LinkedHashMap<String, String> error = new LinkedHashMap<String, String>();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			error.put("status", "error");
			error.put("message", errors.toString());
			result = error;
		}
    
		return result;
    }
	
	private LinkedHashMap createCandidateProfile(LinkedHashMap input) throws Exception{
		
		 LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

		//Check for duplicate entries
		LinkedHashMap existingUser = getCandidateProfile(input);
		String dbResultEx = existingUser.get("db_result").toString().replaceAll("\\\"", "\"");
		JSONObject jb = new JSONObject(dbResultEx);
		if((int)jb.get("Count") == 0)
		{
		String payload = 
				
				"{"+
						"    \"TableName\": \"Candidate\","+
						"    \"Item\": {"+
						"        \"id\": {"+
						"            \"S\": \""+input.get("id")+"\""+
						"        },"+
						"		\"password\": {"+
						"            \"S\": \""+input.get("password")+"\""+
						"        },"+
						"		 \"email\": {"+
						"            \"S\": \""+input.get("email")+"\""+
						"        },"+
						"		 \"first_name\": {"+
						"            \"S\": \""+input.get("first_name")+"\""+
						"        },"+
						"		 \"last_name\": {"+
						"            \"S\": \""+input.get("last_name")+"\""+
						"        }"+
						"    }"+
						"}";
		 String dbResult = DB.execute("PutItem", payload);

		 if( !"{}".equals(dbResult)){
			 throw new Exception("SignUp failed!!!");
		 }
		 
		 result.put("status", "ok");
		 result.put("message", "SignUp success!!!");	
		 result.put("payload", payload);
		 result.put("db_result", dbResult);	
		}else{
			 result.put("status", "error");
			 result.put("message", "User already exists!!!");	

		}
		return result;		
	}
	
	private LinkedHashMap getCandidateProfile(LinkedHashMap<String,String> input) throws Exception{
		
		String payload = 	
				
				"{"+
						"    \"TableName\": \"Candidate\",";

/*		String keyConditionExpression = "\"KeyConditionExpression\": \"email = :email\",";
		String expressionAttributeValues = "\"ExpressionAttributeValues\": {\":email\": {\"S\": \""+input.get("id")+"\"}}";		*/
		
		
		String keyConditionExpression = "\"KeyConditionExpression\": \"id = :id\",";
	//	String filterExpression = "\"FilterExpression\": \"password = :password\",";
	//	String expressionAttributeValues = "\"ExpressionAttributeValues\": {\":id\": {\"S\": \""+input.get("id")+"\"},\":password\": {\"S\": \""+input.get("access_token")+"\"}}";		
		String expressionAttributeValues = "\"ExpressionAttributeValues\": {\":id\": {\"S\": \""+input.get("id")+"\"}}";		

	
		payload = payload + keyConditionExpression + expressionAttributeValues + "}";
		
		 String dbResult = DB.execute("Query", payload);

		 if( "{}".equals(dbResult)){
			 throw new Exception("Query failed!!! payload = "+payload+" db_result = "+dbResult);
		 }
		 
		 LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		 result.put("status", "ok");
		 result.put("message", "Query success!!!");	
		 result.put("payload", payload);
		 result.put("db_result", dbResult);	

		return result;		
	}
	
	private LinkedHashMap updateCandidateProfile(LinkedHashMap<String,String> input) throws Exception{
		
		String payload = 	
				
				"{"+
						"    \"TableName\": \"Candidate\","+
						"    \"Key\": {"+
						"        \"id\": {"+
						"            \"S\": \""+input.get("id")+"\""+
						"        }"+
						"    },	";
		
		input.remove("method");
		input.remove("id");

		String updateExpression = "";
		String expressionAttributeValues = "";
		for (Map.Entry<String,String> entry : input.entrySet()) {
		    String key = entry.getKey();		    
		    updateExpression =updateExpression + entry.getKey()+ " = :"+entry.getKey()+",";	   
		    expressionAttributeValues =  expressionAttributeValues + "\":"+entry.getKey()+"\": {\"S\": \""+entry.getValue()+"\"},";

		}
		
	    updateExpression =  updateExpression.substring(0,updateExpression.length()-1);
	    expressionAttributeValues = expressionAttributeValues.substring(0,expressionAttributeValues.length()-1);
		
		updateExpression = "\"UpdateExpression\":\"set "+updateExpression+"\" ,";

		expressionAttributeValues = "\"ExpressionAttributeValues\": {"+expressionAttributeValues+"}";
		
		payload = payload + updateExpression + expressionAttributeValues + "}";
		
		 String dbResult = DB.execute("UpdateItem", payload);

		 if( !"{}".equals(dbResult)){
			 throw new Exception("Update failed!!! payload = "+payload+" db_result = "+dbResult);
		 }
		 
		 LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		 result.put("status", "ok");
		 result.put("message", "Update success!!!");	
		 result.put("payload", payload);
		 result.put("db_result", dbResult);	

		return result;		
	}
	
	private LinkedHashMap deleteCandidateProfile(LinkedHashMap<String,String> input) throws Exception{
		
		String payload = 	
				
				"{"+
						"    \"TableName\": \"Candidate\","+
						"    \"Key\": {"+
						"        \"id\": {"+
						"            \"S\": \""+input.get("id")+"\""+
						"        }"+
						"    }	"+
				 "}";

		
		 String dbResult = DB.execute("DeleteItem", payload);

		 if( !"{}".equals(dbResult)){
			 throw new Exception("Delete failed!!! payload = "+payload+" db_result = "+dbResult);
		 }
		 
		 LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		 result.put("status", "ok");
		 result.put("message", "Delete success!!!");	
		 result.put("payload", payload);
		 result.put("db_result", dbResult);	

		return result;		
	}


}
