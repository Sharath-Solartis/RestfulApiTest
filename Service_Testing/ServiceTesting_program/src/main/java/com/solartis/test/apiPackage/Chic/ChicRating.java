package com.solartis.test.apiPackage.Chic;

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

public class ChicRating extends BaseClass implements API
{
	public ChicRating(PropertiesHandle config) throws APIException 
	{
		try
		{
			this.config = config;
			XmlElements = new DatabaseOperation();
			XmlElements.GetDataObjects(config.getProperty("json_query"));
			InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
			OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
			StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
		}
		catch(DatabaseException e)
		{
			throw new APIException("ERROR IN CONSTRUCTOR-- CHIC RATING CLASS", e);
		}
	}
	
	@Override
	public void LoadSampleRequest(DatabaseOperation InputData)
	{
		this.input = InputData;
		sampleInput = new XmlHandle(config.getProperty("sample_request"));
		
	}
	
	@Override
	public void PumpDataToRequest() throws APIException 
	{
		try
		{
			InputColVerify.GetDataObjects(config.getProperty("InputColQuery"));
			request = new XmlHandle(config.getProperty("request_location")+input.ReadData("testdata")+"_request"+".xml");
			request.StringToFile(sampleInput.FileToString());
			
			do
			{
				if(InputColVerify.DbCol(input))
				{
					if(!input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))).equals(""))
					{
						request.write(jsonElements.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))), input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))));
					}
				}	
			}while(InputColVerify.MoveForward());
		}
		catch(DatabaseException | RequestFormatException e)
		{
			throw new APIException("ERROR OCCURS IN PUMPDATATOREQUEST FUNCTION -- CHIC FORM CLASS", e);
		}
	}

	@Override
	public void SendAndReceiveData() throws APIException
	{
		try
		{
			String input_data= null;
		    input_data = request.FileToString();
			http.SendData(input_data);	
			String response_string = null;
			response_string = http.ReceiveData();
			response = new XmlHandle(config.getProperty("response_location")+input.ReadData("testdata")+"_response"+".xml");
			response.StringToFile(response_string);
		}
		catch(RequestFormatException | HTTPHandleException | DatabaseException e)
		{
			throw new APIException("ERROR IN SEND AND RECIEVE DATA FUNCTION -- CHIC RATING CLASS", e);
		}
	}

	@Override
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws APIException
	{
		try
		{
			do 	
			{
			if(OutputColVerify.DbCol(input))
			{
				 try
			      {	
				
				String actual = (response.read(XmlElements.ReadData(OutputColVerify.ReadData(config.getProperty("OutputColumn")))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
				output.WriteData(OutputColVerify.ReadData(config.getProperty("OutputColumn")), actual);
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
			throw new APIException("ERROR IN SEND RESPONSE TO FILE FUNCTION -- CHIC RATING CLASS", e);
		}
		
	}
}
