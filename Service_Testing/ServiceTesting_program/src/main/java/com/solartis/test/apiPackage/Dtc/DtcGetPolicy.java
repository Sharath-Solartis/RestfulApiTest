package com.solartis.test.apiPackage.Dtc;

import com.jayway.jsonpath.PathNotFoundException;
import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.apiPackage.API;
import com.solartis.test.apiPackage.BaseClass;
import com.solartis.test.exception.APIException;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.HTTPHandleException;
import com.solartis.test.exception.RequestFormatException;
import com.solartis.test.util.api.*;
import com.solartis.test.util.common.*;

public class DtcGetPolicy extends BaseClass implements API
{
	public DtcGetPolicy(PropertiesHandle config)
	{
		this.config=config;
		jsonElements = new DatabaseOperation();
		
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
	}

    @Override
	public void LoadSampleRequest(DatabaseOperation InputData) throws APIException
	{
    	try
    	{
			this.input = InputData;
			
			String PolicyOrBatch=InputData.ReadData("Policy_or_Batch");
			System.out.println(PolicyOrBatch);
			switch(PolicyOrBatch)
			{
			case "Policy":
					sampleInput = new JsonHandle(config.getProperty("sample_request")+"request_policy.json");
					break;
			case "Batch":
					sampleInput = new JsonHandle(config.getProperty("sample_request")+"request_batch.json");
					break;
			default:
				System.out.println("no sample request");
				break;
			}
    	}
    	catch(DatabaseException e)
    	{
    		throw new APIException("ERROR OCCURS IN PUMPDATATOREQUEST FUNCTION -- DTCGETPOLICY CLASS", e);
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
		}
    	catch (HTTPHandleException e) 
		{
			throw new APIException("ERROR ADD HEADER FUNCTION -- DTCGETPOLICY CLASS", e);
		}
	}
	
	
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws APIException
	{
		try
		{
			String StatusCode=(response.read("..RequestStatus").replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
			OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));		
				do 	
				{
				if(OutputColVerify.DbCol(input)&& (OutputColVerify.ReadData("Flag").equalsIgnoreCase("Y")))
				{
					 try
				      {	
							if(StatusCode.equals("SUCCESS"))
							{
								System.out.println(OutputColVerify.ReadData(config.getProperty("OutputColumn")));
								String actual = (response.read(OutputColVerify.ReadData(config.getProperty("OutputJsonPath"))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
								output.WriteData(OutputColVerify.ReadData(config.getProperty("OutputColumn")), actual);
								System.out.println(actual);
								output.WriteData("Flag_for_execution", StatusCode);
								
							}
							else
							{
								String MessageCode=(response.read("..messageCode").replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
								String UserMessage=(response.read("..UserMessage").replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
								output.WriteData("Flag_for_execution", "Error response");
								output.WriteData("Message_code", MessageCode);
								output.WriteData("User_message", UserMessage);
								
							}
				      }
					catch(PathNotFoundException e)
					{
						output.WriteData(OutputColVerify.ReadData(config.getProperty("OutputColumn")), "Path not Found");
					}
					}
				}while(OutputColVerify.MoveForward());
	
			return output;	
		}
		catch(DatabaseException | RequestFormatException e)
		{
			throw new APIException("ERROR IN SEND RESPONSE TO FILE FUNCTION -- 	DTCGETPOLICY CLASS", e);
		}
    }
}
