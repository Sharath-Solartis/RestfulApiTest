package com.solartis.test.apiPackage.Dtc;

import java.sql.SQLException;

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
import com.solartis.test.macroPackage.DtcMacro;
import com.solartis.test.macroPackage.MacroInterface;
import com.solartis.test.util.api.*;
import com.solartis.test.util.common.*;

public class DtcRatingService extends BaseClass implements API 
{
 protected String Planname[] = null;
 protected String Plancode[] = null;
 MacroInterface macro = null;
 public DtcRatingService(PropertiesHandle config) throws SQLException, MacroException
 {
  this.config = config;
  jsonElements = new DatabaseOperation();
  
     InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
	OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
	StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
	if(config.getProperty("status").equals("Y"))
	{
	macro=new DtcMacro(config);	
	}
 }
 
 @Override
 public void LoadSampleRequest(DatabaseOperation InputData) throws  APIException
 {
	try
	{
	 	  this.input = InputData;
		  input = InputData;
		  
		  Planname=InputData.ReadData("Plan_name").split(";");
		  Plancode=InputData.ReadData("Plan_code").split(";");
		  int numofplan = Planname.length;
		  switch(numofplan)
		   
		   {
		    case 1:   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request1.json"); break;
		    case 2:   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request2.json"); break;
		    case 3:   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request3.json"); break;
		    case 4:   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request4.json"); break;
		    case 5:   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request5.json"); break;
		    case 6:   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request6.json"); break;
		    case 7:   sampleInput = new JsonHandle(config.getProperty("sample_request")+"request7.json"); break;
		   
		    default:
		   }
		  if(config.getProperty("status").equals("Y"))
			{
				  macro.LoadSampleRatingmodel(config, InputData);  //Load sample rating model 
				  macro.GenerateExpected(InputData, config);        //Generate expected rating models  
			}
	}
	catch(DatabaseException | MacroException e)
	{
		 throw new APIException("ERROR LoadSampleRequest FUNCTION -- DTC-RatingService CLASS", e);
	}
 }

    @Override
 public void PumpDataToRequest() throws APIException
 {
	  try
	  {
    	InputColVerify.GetDataObjects(config.getProperty("InputColQuery"));
	  request = new JsonHandle(config.getProperty("request_location")+input.ReadData("testdata")+"_request_"+input.ReadData("State_code")+"_"+input.ReadData("Plan_name")+".json");
	  request.StringToFile(sampleInput.FileToString());
	  
		do
		{
		if(InputColVerify.DbCol(input)&& (InputColVerify.ReadData("Flag").equalsIgnoreCase("Y")))
		{
		  if(!input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))).equals(""))
		  {
			if(InputColVerify.ReadData(config.getProperty("InputColumn")).equals("Plan_name"))
		    {
		     for(int j=0;j<Planname.length;j++)
		     {
		      String DynamicPlannameJson = InputColVerify.ReadData(config.getProperty("InputJsonPath"));
		      String SplitPlanJson[] = DynamicPlannameJson.split("##");
		      request.write(SplitPlanJson[0]+j+SplitPlanJson[1], Planname[j]);
		     }
		    }
		    else if(InputColVerify.ReadData(config.getProperty("InputColumn")).equals("Plan_code"))
		    {
		    	System.out.println(Plancode.length);
		        for(int j=0;j<Plancode.length;j++)
		        {
		         String DynamicPlanCodeJson = InputColVerify.ReadData(config.getProperty("InputJsonPath"));
		         String SplitCodeJson[] = DynamicPlanCodeJson.split("##"); 
		         System.out.println(SplitCodeJson[0]+j+SplitCodeJson[1]);
		         request.write(SplitCodeJson[0]+j+SplitCodeJson[1], Plancode[j]);
		        }
		     }
		    else
		    {
		     request.write(InputColVerify.ReadData(config.getProperty("InputJsonPath")), input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))));
		    }
		   }
		  }
		}while(InputColVerify.MoveForward());
			if(config.getProperty("status").equals("Y"))
			{
				macro.PumpinData(input, config);    //Data feed into Sample Rating model
			}
	
	  }
	  catch(DatabaseException | RequestFormatException | POIException | MacroException e)
	  {
		  throw new APIException("ERROR PumpDataToRequest FUNCTION -- DTC-RatingService CLASS", e);
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
		  throw new APIException("ERROR ADD HEADER FUNCTION -- DTC-RatingService CLASS", e);
	  }
 }

 @Override
 public void SendAndReceiveData() throws APIException
 {
  String input_data= null;
  
  try
  {
    input_data = request.FileToString();
    http.SendData(input_data);  
    String response_string = null; 
    response_string = http.ReceiveData();  
    response = new JsonHandle(config.getProperty("response_location")+input.ReadData("testdata")+"_response_"+input.ReadData("State_code")+"_"+input.ReadData("Plan_name")+".json");  
    response.StringToFile(response_string);
  }
  catch(HTTPHandleException | RequestFormatException | DatabaseException e)
  {
	  throw new APIException("ERROR SendAndReceiveData FUNCTION -- DTC-RatingService CLASS", e);
  }
   
  
 }

 @Override
 public DatabaseOperation SendResponseDataToFile(DatabaseOperation output)   throws APIException
 {
	try
	{
		OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));		
			do 	
			{
			  if(OutputColVerify.DbCol(input)&& (OutputColVerify.ReadData("Flag").equalsIgnoreCase("Y")))
				{
				try
					{
					System.out.println(OutputColVerify.ReadData(config.getProperty("OutputColumn")));
					System.out.println(config.getProperty("OutputColumn")); 
					System.out.println(OutputColVerify.ReadData(config.getProperty("OutputColumn")));
					String actual = (response.read(OutputColVerify.ReadData(config.getProperty("OutputJsonPath"))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
	
					System.out.println(actual);
					output.WriteData(OutputColVerify.ReadData(config.getProperty("OutputColumn")), actual);
					System.out.println(actual);
					output.WriteData("flag_for_execution", "Completed");
					}
					catch(PathNotFoundException | RequestFormatException e)
					{
						output.WriteData(OutputColVerify.ReadData(config.getProperty("OutputColumn")), "Path not Found");
					}
				}
			}while(OutputColVerify.MoveForward());
		
			macro.PumpoutData(output, input, config);   //	data pumped out from expected rating model to db table
	}
	catch(DatabaseException | POIException | MacroException e)
	{
		 throw new APIException("ERROR SendResponseDataToFile FUNCTION -- DTC-RatingService CLASS", e);
	}
	return output;
}
}