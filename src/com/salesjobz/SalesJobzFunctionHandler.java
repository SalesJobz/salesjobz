package com.salesjobz;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;

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
     		
     			case "signUp" :
     				result = signUp(input);
     			break;
     			
     			case "signIn" :
         			
     			break;
     			
     			case "createCandidateProfile" :
         			
     			break;
     			
     			case "getCandidateProfile" :
         			
     			break;
     			
     			case "updateCandidateProfile" :
         			
     			break;
     			
     			case "deleteCandidateProfile" :
         			
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
	
	private LinkedHashMap signUp(LinkedHashMap input) throws Exception{
		
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
						"        }"+
						"    }"+
						"}";
		 String dbResult = DB.execute("PutItem", payload);

		 if( !"{}".equals(dbResult)){
			 throw new Exception("SignUp failed!!!");
		 }
		 
		 LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		 result.put("status", "ok");
		 result.put("message", "SignUp success!!!");	
		 result.put("payload", payload);
		 result.put("db_result", dbResult);	

		return result;		
	}

}
