package apiPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;
import util.api.*;
import util.common.*;

public class DtcGetPolicy extends BaseClass implements API
{
	public DtcGetPolicy(PropertiesHandle config) throws SQLException
	{
		this.config=config;
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
		
		String PolicyOrBatch=InputData.ReadData("Policy_or_Batch");
		System.out.println(PolicyOrBatch);
		switch(PolicyOrBatch)
		{
		case "Policy":
				sampleInput = new JsonHandle(config.getProperty("sample_request_policy"));
				break;
		case "Batch":
				sampleInput = new JsonHandle(config.getProperty("sample_request_batch"));
				break;
		default:
			System.out.println("no sample request");
			break;
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
	
	
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output)
			throws UnsupportedEncodingException, IOException, ParseException, DocumentException, SQLException
	{
        String StatusCode=(response.read("..RequestStatus").replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
		for(int i=0;i<actualColumnSize;i++)
		{
			if(StatusCode.equals("SUCCESS"))
			{
				String actual=null;
				actual = (response.read(jsonElements.ReadData(actualColumnCol[i])).replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
				output.WriteData(actualColumnCol[i], actual);
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
		return output;
	 }

}
