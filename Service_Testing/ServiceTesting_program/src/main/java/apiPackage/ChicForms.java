package apiPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;
import util.api.*;
import util.common.*;

public class ChicForms extends BaseClass implements API
{	
	public ChicForms(PropertiesHandle config) throws SQLException
	{
		this.config = config;
		XmlElements = new DatabaseOperation();
		XmlElements.GetDataObjects(config.getProperty("json_query"));
		actualColumnCol = config.getProperty("actual_column").split(";");
		inputColumnCol = config.getProperty("input_column").split(";");
		actualColumnSize = actualColumnCol.length;
		inputColumnSize = inputColumnCol.length;
	}
	
	@Override
	public void LoadSampleRequest(DatabaseOperation InputData) throws SQLException
	{
		this.input = InputData;
		sampleInput = new XmlHandle(config.getProperty("sample_request"));
		
	}
	
	@Override
	public void PumpDataToRequest() throws SQLException, IOException, DocumentException, ParseException 
	{
		request = new XmlHandle(config.getProperty("request_location")+input.ReadData("testdata")+"_request"+".xml");
		request.StringToFile(sampleInput.FileToString());
		
		for(int i=0;i<inputColumnSize;i++)
		{
			if(!input.ReadData(inputColumnCol[i]).equals(""))
			{
			request.write(XmlElements.ReadData(inputColumnCol[i]), input.ReadData(inputColumnCol[i]));
			}
		}
		
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
		response = new XmlHandle(config.getProperty("response_location")+input.ReadData("testdata")+"_response"+".xml");
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
  
		
		for(int i=0;i<actualColumnSize;i++)
		{
			
			String actual = (response.read(XmlElements.ReadData(actualColumnCol[i])).replaceAll("\\[\"", "")).replaceAll("\"\\]", "");
			output.WriteData(actualColumnCol[i], actual);
		
			
		}
		return output;
		
	}
}
