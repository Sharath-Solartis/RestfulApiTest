package com.solartis.test.apiPackage.SquareMouth;

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

public class SquareMouthPayment extends BaseClass implements API 
{
	public SquareMouthPayment(PropertiesHandle config)
	{
		this.config = config;
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
			input = InputData;
			System.out.println(config.getProperty("sample_request"));
				switch(InputData.ReadData("No_of_travelers"))
				{
				 case "1":			   sampleInput = new JsonHandle(config.getProperty("sample_request")+"Request_1.json");
				 									break;
				 case "2":			   sampleInput = new JsonHandle(config.getProperty("sample_request")+"Request_2.json");
													break;
				 case "3": 	          sampleInput = new JsonHandle(config.getProperty("sample_request")+"Request_3.json");
													break; 
				 case "4": 	          sampleInput = new JsonHandle(config.getProperty("sample_request")+"Request_4.json");
					                                break; 	
				 case "5": 	          sampleInput = new JsonHandle(config.getProperty("sample_request")+"Request_5.json");
                                                   break; 
				 case "6": 	          sampleInput = new JsonHandle(config.getProperty("sample_request")+"Request_6.json");
                                                   break; 
				 case "7": 	          sampleInput = new JsonHandle(config.getProperty("sample_request")+"Request_7.json");
                                                    break; 
				 case "8": 	          sampleInput = new JsonHandle(config.getProperty("sample_request")+"Request_8.json");
                                                    break;                                  
				 case "9": 	          sampleInput = new JsonHandle(config.getProperty("sample_request")+"Request_9.json");
                                                     break; 
				 default:
					 System.out.println("Invalid Request");
					 break;
				}
    	}
		catch(DatabaseException e)
    	{
    		throw new APIException("ERROR OCCURS IN PUMPDATATOREQUEST FUNCTION -- DTC-SMPayIssue CLASS", e);
    	}
	}
	
	@Override
	public void PumpDataToRequest() throws APIException
	{
		try
		{
			InputColVerify.GetDataObjects(config.getProperty("InputColQuery"));
			request = new JsonHandle(config.getProperty("request_location")+input.ReadData("testdata")+"_request_"+input.ReadData("State_code")+".json");
			request.StringToFile(sampleInput.FileToString());
			do
			{
				if(InputColVerify.DbCol(input) && (InputColVerify.ReadData("Flag").equalsIgnoreCase("Y")))
				{
					if(!input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))).equals(""))
					{
						request.write(InputColVerify.ReadData(config.getProperty("InputJsonPath")), input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))));
					}
				}	
			}while(InputColVerify.MoveForward());
		}
		catch(DatabaseException | RequestFormatException  e)
		{
			throw new APIException("ERROR OCCURS IN PUMPDATATOREQUEST FUNCTION -- DTC-SMPayIssue CLASS", e);
		}
	}
	
	@Override
	public void AddHeaders() throws APIException
	{
		try 
		{
			http = new HttpHandle(config.getProperty("test_url"),"POST");
			System.out.println(config.getProperty("test_url")+" "+config.getProperty("content_type")+" "+config.getProperty("token")+ " "+config.getProperty("EventName"));
			http.AddHeader("Content-Type", config.getProperty("content_type"));
			http.AddHeader("Token", config.getProperty("token"));
			http.AddHeader("EventName", config.getProperty("EventName"));
		}
		catch (HTTPHandleException e) 
		{
			throw new APIException("ERROR ADD HEADER FUNCTION -- DTC-SAVEDETAILS1 CLASS", e);
		}
	}
	
	@Override
	public void SendAndReceiveData() throws APIException
	{
		try
		{
			String input_data = request.FileToString();
			http.SendData(input_data);
			String response_string = http.ReceiveData();
			response = new JsonHandle(config.getProperty("response_location")+input.ReadData("testdata")+"_response_"+input.ReadData("State_code")+".json");
			response.StringToFile(response_string);	
		}
		catch(RequestFormatException | HTTPHandleException | DatabaseException e)
		{
			throw new APIException("ERROR IN SEND AND RECIEVE DATA FUNCTION -- DTC-SMPayIssue CLASS", e);
		}
	}
	
	@Override
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws APIException
	{
		try
		{this.output=output;
			OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));
			String StatusCode=(response.read("..RequestStatus").replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
			do 	
			{
			  if(OutputColVerify.DbCol(input)&& (OutputColVerify.ReadData("Flag").equalsIgnoreCase("Y")))
				{
				try
					{
				
				if(StatusCode.equals("SUCCESS"))
				{
	
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
					output.WriteData("User_maessage", UserMessage);
					
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
			throw new APIException("ERROR IN SEND RESPONSE TO FILE FUNCTION -- 	DTC-SMPayIssue CLASS", e);
		}
}
}




