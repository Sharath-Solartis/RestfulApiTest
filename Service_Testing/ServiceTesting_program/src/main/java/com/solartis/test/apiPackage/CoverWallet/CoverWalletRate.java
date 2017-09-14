package com.solartis.test.apiPackage.CoverWallet;

import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.apiPackage.API;
import com.solartis.test.apiPackage.BaseClass;
import com.solartis.test.exception.APIException;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.HTTPHandleException;
import com.solartis.test.exception.MacroException;
import com.solartis.test.exception.POIException;
import com.solartis.test.macroPackage.MacroInterface;
import com.solartis.test.macroPackage.coverWalletMacro;
import com.solartis.test.util.api.DBColoumnVerify;
import com.solartis.test.util.api.HttpHandle;
import com.solartis.test.util.api.JsonHandle;
import com.solartis.test.util.common.DatabaseOperation;

public class CoverWalletRate extends BaseClass implements API
{
	MacroInterface macro = null;
	public CoverWalletRate(PropertiesHandle config) throws MacroException
	{
	
		this.config = config;
		jsonElements = new DatabaseOperation();
	
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
		if(config.getProperty("status").equals("Y"))
		{
		macro=new coverWalletMacro(config);	
		}
		
	}
	
	public void LoadSampleRequest(DatabaseOperation InputData) throws APIException
	{
		try
		{
			this.input = InputData;
			String numofprovider=InputData.ReadData("NumOfProviders");
			switch(numofprovider)
			   
			   {
			    case "1":   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request1.json"); break;
			    case "2":   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request2.json"); break;
			    case "3":   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request3.json"); break;
			    case "4":   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request4.json"); break;
			    case "5":   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request5.json"); break;
			    case "6":   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request6.json"); break;
			    case "7":   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request7.json"); break;
			    case "8":   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request8.json"); break;
			    case "9":   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request9.json"); break;
			    case "10":  sampleInput = new JsonHandle(config.getProperty("sample_request")+"request10.json"); break;
			   
			    default:
			   }
			if(config.getProperty("status").equals("Y"))
			{
		      macro.LoadSampleRatingmodel(config, InputData);
			  macro.GenerateExpected(InputData, config);
			}
		}
		catch(DatabaseException | MacroException e)
		{
			throw new APIException("ERROR OCCURS IN LoadSampleRequest FUNCTION -- CoverWallet CLASS", e);
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
			super.PumpDataToRequest();		
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

	
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws APIException
	{
		try
		{
			if(config.getProperty("status").equals("Y"))
			{
			macro.PumpoutData(output, input, config);
			}
			super.SendResponseDataToFile(output);
		}
		catch( POIException | MacroException| DatabaseException e)
		{
			throw new APIException("ERROR OCCURS IN SendResponseDataToFile FUNCTION -- Coverwallet CLASS", e);
		}
		return output;		
	}
}
