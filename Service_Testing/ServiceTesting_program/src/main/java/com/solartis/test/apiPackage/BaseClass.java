package com.solartis.test.apiPackage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jayway.jsonpath.PathNotFoundException;
import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.exception.APIException;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.HTTPHandleException;
import com.solartis.test.exception.RequestFormatException;
import com.solartis.test.util.api.*;
import com.solartis.test.util.common.*;

public class BaseClass 
{
	protected RequestResponse sampleInput = null;
	protected RequestResponse request = null;
	protected RequestResponse response = null;
	protected DatabaseOperation XmlElements = null;
	protected DatabaseOperation jsonElements = null;
	protected PropertiesHandle config = null;
	protected DatabaseOperation input = null;
	protected DatabaseOperation output = null;
	protected HttpHandle http = null;
	protected DBColoumnVerify InputColVerify = null;
	protected DBColoumnVerify OutputColVerify = null;
	protected DBColoumnVerify StatusColVerify = null;
	protected LinkedHashMap<String,String> hMap = new LinkedHashMap<String,String>();
	
	
//---------------------------------------------------------------LOAD SAMPLE REQUEST--------------------------------------------------------------------	
	public void LoadSampleRequest(DatabaseOperation InputData) throws APIException
	{
		this.input = InputData;
		sampleInput = new JsonHandle(config.getProperty("sample_request") + "request.json");
	}

//-----------------------------------------------------------PUMPING TEST DATA TO REQUEST--------------------------------------------------------------- 	
	public void PumpDataToRequest() throws APIException 
	{
		try
		{
			InputColVerify.GetDataObjects(config.getProperty("InputColQuery"));
			request = new JsonHandle(config.getProperty("request_location")+input.ReadData("testdata")+".json");
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
			throw new APIException("ERROR OCCURS IN PUMPDATATOREQUEST FUNCTION -- BASE CLASS", e);
		}
	}

//------------------------------------------------------------CONVERTING REQUEST TO STRING--------------------------------------------------------------	
	public String RequestToString() throws APIException
	{
	  try 
	  {
		  return request.FileToString();
	  } 
	  catch (RequestFormatException e)
	  {
		  throw new APIException("ERROR OCCURS IN REQUEST TO STRING FUNCTION -- BASE CLASS", e);
	   }
	}
	
//-------------------------------------------------------------ADDING HEADER || TOKENS------------------------------------------------------------------	
	public void AddHeaders() throws APIException
	{
		try
		{
			http = new HttpHandle(config.getProperty("test_url"),"POST");
			http.AddHeader("Content-Type", config.getProperty("content_type"));
			http.AddHeader("Token", config.getProperty("token"));
		}
		catch(HTTPHandleException e)
		{
			throw new APIException("ERROR ADD HEADER FUNCTION -- BASE CLASS", e);
		}
	}

//------------------------------------------------------------STORING RESPONSE TO FOLDER----------------------------------------------------------------	
	public void SendAndReceiveData() throws APIException 
	{
		try
		{
			String input_data= null;
			input_data = request.FileToString();
		    http.SendData(input_data);
			String response_string = http.ReceiveData();	
			response = new JsonHandle(config.getProperty("response_location")+input.ReadData("testdata")+".json");
			response.StringToFile(response_string);
		}
		catch(RequestFormatException | HTTPHandleException | DatabaseException e)
		{
			throw new APIException("ERROR IN SEND AND RECIEVE DATA FUNCTION -- BASE CLASS", e);
		}
	}
	
//-------------------------------------------------------------CONVERTING RESPONSE TO STRING------------------------------------------------------------
	public String ResponseToString() throws APIException 
	{
		try
		{
			return response.FileToString();
		}
		catch(RequestFormatException e)
		{
			throw new APIException("ERROR IN RESPONSE TO STRING FUNCTION -- BASE CLASS", e);
		}
	}
	
//-----------------------------------------------------------UPDATING RESPONSE DATA TO DATABASE---------------------------------------------------------	
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws APIException
	{
		try
		{
			this.output=output;
			OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));		
			do 	
			{
			  if(OutputColVerify.DbCol(input) && (OutputColVerify.ReadData("Flag").equalsIgnoreCase("Y")))
				{
				try
					{
					System.out.println(OutputColVerify.ReadData(config.getProperty("OutputColumn")));
					String actual = (response.read(OutputColVerify.ReadData(config.getProperty("OutputJsonPath"))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
					output.WriteData(OutputColVerify.ReadData(config.getProperty("OutputColumn")), actual);
					System.out.println(actual);
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
			throw new APIException("ERROR IN SEND RESPONSE TO FILE FUNCTION -- BASE CLASS", e);
		}
	}

//---------------------------------------------------------------COMAPRISION FUNCTION-------------------------------------------------------------------	
	public DatabaseOperation CompareFunction(DatabaseOperation output) throws APIException
	{		
	    try
	    {
			StatusColVerify.GetDataObjects(config.getProperty("OutputColQuery"));
			do 	
			{	
			  if(StatusColVerify.DbCol(input) && (StatusColVerify.ReadData("Comaparision_Flag").equalsIgnoreCase("Y")))
				{
					String ExpectedColumn = StatusColVerify.ReadData(config.getProperty("ExpectedColumn"));
					String ActualColumn = StatusColVerify.ReadData(config.getProperty("OutputColumn"));
					String StatusColumn = StatusColVerify.ReadData(config.getProperty("StatusColumn"));
					if(!(StatusColumn.equals("")) && !(ExpectedColumn.equals("")))
					{
						if(premium_comp(output.ReadData(ExpectedColumn),output.ReadData(ActualColumn)))
						{
							output.WriteData(StatusColumn, "Pass");
						}
						else
						{
							output.WriteData(StatusColumn, "Fail");
							analyse(StatusColVerify,output);
						}
					}
					
				}
			 }while(StatusColVerify.MoveForward());
			String message = "";
			for(Map.Entry m:hMap.entrySet())
			{
				System.out.println("12-----------------------------------------");
				message=message+m.getValue()+"; ";
			}
				output.WriteData("AnalyserResult", message);
			System.out.println(message);
			hMap.clear();
			return output;
	    }
	    catch(DatabaseException e)
	    {
	    	System.out.println(e);
	    	throw new APIException("ERROR IN DB COMPARISON FUNCTION -- BASE CLASS", e);
	    }
	}
	
//-----------------------------------------------------PRIVATE FUNCTION FOR SUPPORTING COMPARISON FUNCTION---------------------------------------------------	
	protected static boolean premium_comp(String expected,String actual)
	{
		
		boolean status = false;
		if(actual == null)
		{
			if((expected.equals("")||expected.equals("0")))
			{
				status = true;
			}
		}
		if(expected == null)
		{
			if(actual.equals("")||actual.equals("0"))
			{
				status = true;
			}
		}
		if(actual!=null && expected!=null)
		{
			expected = expected.replaceAll("\\[\"", "");
    		actual = actual.replaceAll("\\[\"", "");
    		expected = expected.replaceAll("\"\\]", "");
    		actual = actual.replaceAll("\"\\]", "");
    		expected = expected.replaceAll("\\.[0-9]*", "");
    		actual = actual.replaceAll("\\.[0-9]*", "");
    		if(expected.equals(actual))
    		{
    			status = true;
    		}
		}

		return status;	
		
	}
	
	protected void analyse(DatabaseOperation Conditiontable,DatabaseOperation output ) throws DatabaseException 
	{

		
		if(output.ReadData(Conditiontable.ReadData("StatusColumn")).equals("Pass"))
		{		

		}

		else if(output.ReadData(Conditiontable.ReadData("StatusColumn")).equals("Fail"))
		{	
			String[] Parentname =Conditiontable.ReadData("ParentName").split(";");
			int noOfParentname=Parentname.length;
			for(int i=0;i<noOfParentname;i++)
			{
				if(!this.ifexist(Parentname[i]))
				hMap.put(Conditiontable.ReadData("ParentName"), Conditiontable.ReadData("Message"));
			}
		}

	}

	protected boolean ifexist (String NewParentName)
	{
		boolean exist = false;
		for(Map.Entry m:hMap.entrySet())
		{
			String existParentName =(String) m.getKey();
			if(existParentName.equals(NewParentName))
			{
				exist = true;
				break;
			}
		}
		return exist;	

	}
	
}
