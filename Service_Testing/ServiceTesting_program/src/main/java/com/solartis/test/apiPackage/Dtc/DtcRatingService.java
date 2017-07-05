package com.solartis.test.apiPackage.DTC;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;
import com.jayway.jsonpath.PathNotFoundException;
import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.apiPackage.API;
import com.solartis.test.apiPackage.BaseClass;
import com.solartis.test.util.api.*;
import com.solartis.test.util.common.*;

public class DtcRatingService extends BaseClass implements API 
{
	public DtcRatingService(PropertiesHandle config) throws SQLException
	{
		this.config = config;
		jsonElements = new DatabaseOperation();
		
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
	}

    @Override
	public void PumpDataToRequest() throws SQLException, IOException, DocumentException, ParseException
	{
    	InputColVerify.GetDataObjects(config.getProperty("InputColQuery"));
		request = new JsonHandle(config.getProperty("request_location")+input.ReadData("testdata")+"_request_"+input.ReadData("State_code")+"_"+input.ReadData("Plan_name")+".json");
		request.StringToFile(sampleInput.FileToString());
		
		do
		{
			if(InputColVerify.DbCol(input))
			{
				if(!input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))).equals(""))
				{
					request.write(InputColVerify.ReadData(config.getProperty("InputJsonPath")), input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))));
				}
			}	
		}while(InputColVerify.MoveForward());
		
	}

	@Override
	public void AddHeaders() throws IOException 
	{
		http = new HttpHandle(config.getProperty("test_url"),"POST");
		http.AddHeader("Content-Type", config.getProperty("content_type"));
		http.AddHeader("Token", config.getProperty("token"));
		http.AddHeader("EventName", config.getProperty("EventName"));	
	}

	@Override
	public void SendAndReceiveData() throws SQLException
	{
		String input_data= null;
		try {
			input_data = request.FileToString();
		} catch (IOException | ParseException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			http.SendData(input_data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String response_string = null;
		
		try {
			response_string = http.ReceiveData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response = new JsonHandle(config.getProperty("response_location")+input.ReadData("testdata")+"_response_"+input.ReadData("State_code")+"_"+input.ReadData("Plan_name")+".json");
		try {
			response.StringToFile(response_string);
		} catch (IOException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output)
			throws UnsupportedEncodingException, IOException, ParseException, DocumentException, SQLException 
	{
     String StatusCode=(response.read("..RequestStatus").replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
     OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));
 	do 	
	{
	  if(OutputColVerify.DbCol(input))
		{
			if(StatusCode.equals("SUCCESS"))
			{
				try
				{
					
					String actual = (response.read(OutputColVerify.ReadData(config.getProperty("OutputJsonPath"))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
					output.WriteData(OutputColVerify.ReadData(config.getProperty("OutputColumn")), actual);
					System.out.println(actual);
					output.WriteData("Flag_for_execution", StatusCode);
				}
				catch(PathNotFoundException e)
				{
					output.WriteData(OutputColVerify.ReadData(config.getProperty("OutputColumn")), "Path not Found");
					
				}
				
			}
			else
			{
				//String MessageCode=(response.read("..messageCode").replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
				//String UserMessage=(response.read("..UserMessage").replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
				//output.WriteData("Flag_for_execution", "Error response");
				//output.WriteData("Message_code", MessageCode);
				//output.WriteData("User_message", UserMessage);
				
			}
		}
	}while(OutputColVerify.MoveForward());
		return output;
		
	}
}
