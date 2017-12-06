package com.solartis.test.apiPackage.LDWC;

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

import java.util.UUID;



public class LDWCRating extends BaseClass implements API
{
	protected String numofclasscode;
	public LDWCRating(PropertiesHandle config)
	{
		this.config = config;
		XmlElements = new DatabaseOperation();
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
		
	}
	
	@Override
	public void LoadSampleRequest(DatabaseOperation InputData) throws APIException
	{	 
		this.input = InputData;	
		sampleInput = new XmlHandle(config.getProperty("sample_request")+ "request.xml");
		
		//classcode=InputData.ReadData("Class_code");
		try
		{
		 numofclasscode = InputData.ReadData("classcode");
		}
		catch(Exception e)
		{
			
		}
		//int numofclasscode=numclasscode.length;
		switch(numofclasscode)
		   
		 {
		    case "1":   sampleInput = new XmlHandle(config.getProperty("sample_request")+"request1.xml"); break;
		    case "2":   sampleInput = new XmlHandle(config.getProperty("sample_request")+"request2.xml"); break;
		    case "3":   sampleInput = new XmlHandle(config.getProperty("sample_request")+"request3.xml"); break;
		    case "4":   sampleInput = new XmlHandle(config.getProperty("sample_request")+"request4.xml"); break;
		    case "5":   sampleInput = new XmlHandle(config.getProperty("sample_request")+"request5.xml"); break;
		    
		    default:
		 }
	}
		
	@Override
	public void PumpDataToRequest() throws APIException
	{
		try
		{
			InputColVerify.GetDataObjects(config.getProperty("InputColQuery"));
			request = new XmlHandle(config.getProperty("request_location")+input.ReadData("testdata")+"_request"+".xml");
			request.StringToFile(sampleInput.FileToString());
			String Reqid = UUID.randomUUID().toString();
		    System.out.println(Reqid);
			request.write("//InsuranceSvcRq/RqUID",Reqid);
			input.WriteData("RequestUID", Reqid);
			System.out.println(InputColVerify.ReadData(config.getProperty("InputJsonPath")));//+".............."+ input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))));
			
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
		catch(DatabaseException | RequestFormatException e)
		{
			throw new APIException("ERROR OCCURS IN PUMPDATATOREQUEST FUNCTION -- LDWC RATING CLASS", e);
		}
		
	}
	
	@Override
	public void AddHeaders() throws APIException
	{
		try 
		{
			http = new HttpHandle(config.getProperty("test_url"),"POST");
			http.AddHeader("Content-Type", config.getProperty("content_type"));
		}
		catch (HTTPHandleException e) 
		{
			throw new APIException("ERROR ADD HEADER FUNCTION -- LDWC RATING CLASS", e);
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
			response = new XmlHandle(config.getProperty("response_location")+input.ReadData("testdata")+"_response"+".xml");
			response.StringToFile(response_string);
		}
		catch(RequestFormatException | HTTPHandleException | DatabaseException e)
		{
			throw new APIException("ERROR IN SEND AND RECIEVE DATA FUNCTION -- BASE CLASS", e);
		}
	}

	@Override
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws APIException
	{
		try
		{ 
			OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));		
			do 	
			{
			if(OutputColVerify.DbCol(input) && (OutputColVerify.ReadData("Flag").equalsIgnoreCase("Y")))
			{
				 try
			      {	
				   System.out.println(OutputColVerify.ReadData(config.getProperty("OutputColumn")));
				   String actual = (response.read((OutputColVerify.ReadData(config.getProperty("OutputJsonPath")))).replaceAll("\\[\"","")).replaceAll("\"\\]","").replaceAll("\\\\","");
				   output.WriteData(OutputColVerify.ReadData(config.getProperty("OutputColumn")), actual);
				   output.WriteData("flag_for_execution", "Completed");
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
			throw new APIException("ERROR IN SEND RESPONSE TO FILE FUNCTION -- LDWC RATING CLASS", e);
		}
		
	}
	
}

