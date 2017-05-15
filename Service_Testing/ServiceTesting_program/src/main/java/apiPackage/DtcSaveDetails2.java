package apiPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;
import com.jayway.jsonpath.PathNotFoundException;
import util.api.*;
import util.common.*;


public class DtcSaveDetails2 extends BaseClass implements API
{
	public DtcSaveDetails2(PropertiesHandle config) throws SQLException
	{
		this.config = config;
		jsonElements = new DatabaseOperation();
		jsonElements.GetDataObjects(config.getProperty("json_query"));
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
	}

	@Override
	public void LoadSampleRequest(DatabaseOperation InputData) throws SQLException
	{
		this.input = InputData;
		input = InputData;
			switch(InputData.ReadData("Plan_Type"))
			{
			 case "Annual":			sampleInput = new JsonHandle(config.getProperty("sample_request_AnnualPlan"));
			 									break;
			 case "Single Trip":			sampleInput = new JsonHandle(config.getProperty("sample_request_SingleTrip"));
												break;
			 case "Renter's Collision": 	sampleInput = new JsonHandle(config.getProperty("sample_request_RenterCollision"));
												break; 
			 
			 default:
			}
	  }

	@Override
	public void PumpDataToRequest() throws SQLException, IOException, DocumentException, ParseException
	{
		InputColVerify.GetDataObjects(config.getProperty("InputColQuery"));
		request = new JsonHandle(config.getProperty("request_location")+input.ReadData("testdata")+"_request_"+input.ReadData("State_code")+"_"+input.ReadData("Plan_type")+".json");
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
		response = new JsonHandle(config.getProperty("response_location")+input.ReadData("testdata")+"_response_"+input.ReadData("State_code")+"_"+input.ReadData("Plan_type")+".json");
		try {
			response.StringToFile(response_string);
		} catch (IOException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws UnsupportedEncodingException, IOException, ParseException, DocumentException, SQLException
	{
		OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));		
		do 	
		{
		  if(OutputColVerify.DbCol(input))
			{
			try
				{
				System.out.println(OutputColVerify.ReadData(config.getProperty("OutputColumn")));
				String actual = (response.read(jsonElements.ReadData(OutputColVerify.ReadData(config.getProperty("OutputColumn")))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
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
}	

	
