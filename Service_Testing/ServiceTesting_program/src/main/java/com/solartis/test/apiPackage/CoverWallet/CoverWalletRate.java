package com.solartis.test.apiPackage.CoverWallet;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.jayway.jsonpath.PathNotFoundException;
import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.apiPackage.API;
import com.solartis.test.apiPackage.BaseClass;
import com.solartis.test.exception.APIException;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.HTTPHandleException;
import com.solartis.test.exception.MacroException;
import com.solartis.test.exception.POIException;
import com.solartis.test.exception.RequestFormatException;
import com.solartis.test.macroPackage.MacroInterface;
import com.solartis.test.macroPackage.coverWalletMacro;
import com.solartis.test.util.api.DBColoumnVerify;
import com.solartis.test.util.api.HttpHandle;

public class CoverWalletRate extends BaseClass implements API
{
	MacroInterface macro = null;
	public CoverWalletRate(PropertiesHandle config) throws MacroException
	{
	
		this.config = config;
		jsonElements = new LinkedHashMap<String, String>();
	
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
		if(config.getProperty("status").equals("Y"))
		{
		macro=new coverWalletMacro(config);	
		}
		
	}
	
	
	
	public void PumpDataToRequest() throws APIException
	{	
		try
		{
			if(config.getProperty("status").equals("Y"))
			{
			macro.PumpinData(input, config);	
			}
			//super.PumpDataToRequest();		
		}
		catch(DatabaseException | POIException | MacroException  e)
		{
			throw new APIException("ERROR OCCURS IN PUMPDATATOREQUEST FUNCTION -- Coverwallet CLASS", e);
		}
	}
	
	@Override
	 public void AddHeaders() throws APIException 
	 {
		try
		{
		  http = new HttpHandle(config.getProperty("test_url"),"POST");
		  http.AddHeader("Content-Type", config.getProperty("content_type"));
		  http.AddHeader("Token", config.getProperty("token"));
		  http.AddHeader("EventName", config.getProperty("EventName")); 
		  http.AddHeader("EventVersion", config.getProperty("EventVersion")); 
		 }
		catch(HTTPHandleException e)
		{
			throw new APIException("ERROR OCCURS IN AddHeaders FUNCTION -- Coverwallet CLASS", e);
		}
	 }
	
	public LinkedHashMap<String, String> SendResponseDataToFile(LinkedHashMap<String, String> output) throws APIException
	{
		if(config.getProperty("status").equals("Y"))
		{
			try 
			{
				macro.PumpoutData(output, input, config);
			} 
			catch (POIException | MacroException | DatabaseException e) 
			{
				throw new APIException("ERROR SendResponseDataToFile FUNCTION -- GL-RATING CLASS", e);
			}
		}
		
		try
		{
			String responseStatus = response.read("..ResponseStatus").replaceAll("\\[\"", "").replaceAll("\"\\]", "").replaceAll("\\\\","");
			System.out.println(responseStatus);
			LinkedHashMap<Integer, LinkedHashMap<String, String>> tableOutputColVerify = OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));		
			for (Entry<Integer, LinkedHashMap<String, String>> entry : tableOutputColVerify.entrySet())	
			{
				LinkedHashMap<String, String> rowOutputColVerify = entry.getValue();
				
				
				if((rowOutputColVerify.get("Flag").equalsIgnoreCase("Y"))&&OutputColVerify.ConditionReading(rowOutputColVerify.get("OutputColumnCondtn"),input))
				{
					try
					{
						if(responseStatus.equals("SUCCESS"))
						{
							//System.out.println("Writing Response to Table");
							String actual = (response.read(rowOutputColVerify.get(config.getProperty("OutputJsonPath"))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
							output.put(rowOutputColVerify.get(config.getProperty("OutputColumn")), actual);
							output.put("Flag_for_execution", "Completed");
						}
						else
						{
							output.put("Flag_for_execution", responseStatus);
							output.put("UserMessage", response.read("..Message").replaceAll("\\[\"", "").replaceAll("\"\\]", "").replaceAll("\\\\",""));
						}
					}
					catch(PathNotFoundException e)
					{
							output.put(rowOutputColVerify.get(config.getProperty("OutputColumn")), "Path not Found");
					}
				}
			}
			
			return output;
		}
		catch(DatabaseException | RequestFormatException e)
		{
			throw new APIException("ERROR IN SEND RESPONSE TO FILE FUNCTION -- BASE CLASS", e);
		}
	}
}
