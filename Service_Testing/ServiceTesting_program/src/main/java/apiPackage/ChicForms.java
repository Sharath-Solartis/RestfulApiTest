package apiPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;

import com.jayway.jsonpath.PathNotFoundException;

import util.api.*;
import util.common.*;

public class ChicForms extends BaseClass implements API
{	
	public ChicForms(PropertiesHandle config) throws SQLException
	{
		this.config = config;
		XmlElements = new DatabaseOperation();
		XmlElements.GetDataObjects(config.getProperty("json_query"));
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
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
}
