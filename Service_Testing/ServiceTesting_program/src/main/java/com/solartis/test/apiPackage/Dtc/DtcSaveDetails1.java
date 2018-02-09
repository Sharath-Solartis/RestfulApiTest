package com.solartis.test.apiPackage.Dtc;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import com.jayway.jsonpath.PathNotFoundException;
import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.apiPackage.API;
import com.solartis.test.apiPackage.BaseClass;
import com.solartis.test.exception.APIException;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.HTTPHandleException;
import com.solartis.test.exception.RequestFormatException;
import com.solartis.test.util.api.*;

public class DtcSaveDetails1 extends BaseClass implements API 
{
	public DtcSaveDetails1(PropertiesHandle config)
	{
		this.config = config;
		jsonElements = new LinkedHashMap<String, String>();
		
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
	}
	
	@Override
	public void LoadSampleRequest(LinkedHashMap<String, String> InputData) throws APIException
	{
<<<<<<< HEAD
		try
    	{
			this.input = InputData;
			input = InputData;
				switch(InputData.ReadData("Plan_Type"))
				{
				 case "Annual":			        sampleInput = new JsonHandle(config.getProperty("sample_request")+"First_Save_AnnualPlan.json");
				 							    break;
				 case "Air Ticket":									
				 case "Single Trip":			sampleInput = new JsonHandle(config.getProperty("sample_request")+"First save_trip.json");
				                                break;
				 
				 case "Car Rental":
				 case "Renter's Collision": 	sampleInput = new JsonHandle(config.getProperty("sample_request")+"FirstSave_RC.json");
												break; 
				 
				 default:
				}
    	}
		catch(DatabaseException e)
    	{
    		throw new APIException("ERROR OCCURS IN PUMPDATATOREQUEST FUNCTION -- DTC-SAVEDETAILS1 CLASS", e);
    	}
=======
		this.input = InputData;
		input = InputData;
			switch(InputData.get("Plan_Type"))
			{
			 case "Annual":			      sampleInput = new JsonHandle(config.getProperty("sample_request")+"First_Save_AnnualPlan.json");
			 									break;
			 case "Single Trip":			sampleInput = new JsonHandle(config.getProperty("sample_request")+"First save_trip.json");
												break;
			 case "Renter's Collision": 	sampleInput = new JsonHandle(config.getProperty("sample_request")+"FirstSave_RC.json");
												break; 
			 
			 default:
			}
>>>>>>> refs/remotes/origin/Testng
	}
	
	@Override
	public void PumpDataToRequest() throws APIException
	{
		try
		{
			LinkedHashMap<Integer, LinkedHashMap<String, String>> tableInputColVerify =  InputColVerify.GetDataObjects(config.getProperty("InputColQuery"));
			request = new JsonHandle(config.getProperty("request_location")+input.get("testdata")+"_request_"+input.get("State_code")+"_"+input.get("Plan_type")+".json");
			request.StringToFile(sampleInput.FileToString());
			for (Entry<Integer, LinkedHashMap<String, String>> entry : tableInputColVerify.entrySet())	
			{
<<<<<<< HEAD
				if(InputColVerify.DbCol(input)&& (InputColVerify.ReadData("Flag").equalsIgnoreCase("Y")))
=======
				LinkedHashMap<String, String> rowInputColVerify = entry.getValue();
				if(InputColVerify.DbCol(rowInputColVerify))
>>>>>>> refs/remotes/origin/Testng
				{
					if(!input.get(rowInputColVerify.get(config.getProperty("InputColumn"))).equals(""))
					{
						request.write(rowInputColVerify.get(config.getProperty("InputJsonPath")), input.get(rowInputColVerify.get(config.getProperty("InputColumn"))));
					}
				}	
			}
		}
		catch(DatabaseException | RequestFormatException  e)
		{
			throw new APIException("ERROR OCCURS IN PUMPDATATOREQUEST FUNCTION -- DTC-SAVEDETAILS1 CLASS", e);
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
			response = new JsonHandle(config.getProperty("response_location")+input.get("testdata")+"_response_"+input.get("State_code")+"_"+input.get("Plan_type")+".json");
			response.StringToFile(response_string);	
		}
		catch(RequestFormatException | HTTPHandleException e)
		{
			throw new APIException("ERROR IN SEND AND RECIEVE DATA FUNCTION -- DTC-SAVEDETAILS1 CLASS", e);
		}
	}
	
	@Override
	public LinkedHashMap<String, String> SendResponseDataToFile(LinkedHashMap<String, String> output) throws APIException
	{
		try
		{
			LinkedHashMap<Integer, LinkedHashMap<String, String>> tableOutputColVerify = OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));
			String StatusCode=(response.read("..RequestStatus").replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
			for (Entry<Integer, LinkedHashMap<String, String>> entry : tableOutputColVerify.entrySet())	
			{
<<<<<<< HEAD
			  if(OutputColVerify.DbCol(input)&&(OutputColVerify.ReadData("Flag").equalsIgnoreCase("Y")))
=======
				LinkedHashMap<String, String> rowOutputColVerify = entry.getValue();
			  if(OutputColVerify.DbCol(rowOutputColVerify))
>>>>>>> refs/remotes/origin/Testng
				{
				try
					{
				
				if(StatusCode.equals("SUCCESS"))
				{
	
					String actual = (response.read(rowOutputColVerify.get(config.getProperty("OutputJsonPath"))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
					output.put(rowOutputColVerify.get(config.getProperty("OutputColumn")), actual);
					System.out.println(actual);
					output.put("Flag_for_execution", StatusCode);
					
				}
				else
				{
					String MessageCode=(response.read("..messageCode").replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
					String UserMessage=(response.read("..UserMessage").replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
					output.put("Flag_for_execution", "Error response");
					output.put("Message_code", MessageCode);
					output.put("User_maessage", UserMessage);
					
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
			throw new APIException("ERROR IN SEND RESPONSE TO FILE FUNCTION -- 	DTC-SAVEDETAILS1 CLASS", e);
		}
}
}




