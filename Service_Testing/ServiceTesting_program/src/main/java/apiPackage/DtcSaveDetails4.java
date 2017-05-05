package apiPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;
import com.jayway.jsonpath.PathNotFoundException;
import util.api.*;
import util.common.*;

public class DtcSaveDetails4 extends BaseClass implements API
{
	public DtcSaveDetails4(PropertiesHandle config) throws SQLException
	{
		this.config = config;
		jsonElements = new DatabaseOperation();
		jsonElements.GetDataObjects(config.getProperty("json_query"));
		actualColumnCol = config.getProperty("actual_column").split(";");
		inputColumnCol = config.getProperty("input_column").split(";");
		actualColumnSize = actualColumnCol.length;
		inputColumnSize = inputColumnCol.length;
	}
	
	@Override
	public void LoadSampleRequest(DatabaseOperation InputData) throws SQLException
	{
		this.input = InputData;
		input = InputData;
		switch(InputData.ReadData("Plan_name"))
		{
		 case "Annual Plan":			sampleInput = new JsonHandle(config.getProperty("Sample_request_anual"));
		 									break;
		 case "Single Trip":			sampleInput = new JsonHandle(config.getProperty("sample_request_tripplans"));
											break;
		 case "Renter's Collision": 	sampleInput = new JsonHandle(config.getProperty("sample_request_RC"));
											break; 
		 
		 default:
		}
	}
	
	@Override
	public void PumpDataToRequest() throws SQLException, IOException, DocumentException, ParseException
	{
		request = new JsonHandle(config.getProperty("request_location")+input.ReadData("testdata")+"_request_"+input.ReadData("State1")+"_"+input.ReadData("Plan_name")+".json");
		request.StringToFile(sampleInput.FileToString());
		
		for(int i=0;i<inputColumnSize;i++)
		{
			if(!input.ReadData(inputColumnCol[i]).equals(""))
			{
			request.write(jsonElements.ReadData(inputColumnCol[i]), input.ReadData(inputColumnCol[i]));
			}
		}
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
		response = new JsonHandle(config.getProperty("response_location")+input.ReadData("testdata")+"_response_"+input.ReadData("State1")+"_"+input.ReadData("Plan_name")+".json");
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
		
		for(int i=0;i<actualColumnSize;i++)
		{
			
			if(StatusCode.equals("SUCCESS"))
			{
				try
				{
					String actual=null;
					actual = (response.read(jsonElements.ReadData(actualColumnCol[i])).replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
					output.WriteData(actualColumnCol[i], actual);
					output.WriteData("Flag_for_execution", StatusCode);
				}
				catch(PathNotFoundException e)
				{
					output.WriteData(actualColumnCol[i], "Path not Found");
				}
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
		output.UpdateRow();
		return output;
	}
}
