package apiPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;
import com.jayway.jsonpath.PathNotFoundException;
import Supporting_Classes.DatabaseOperation;
import Supporting_Classes.HttpHandle;
import Supporting_Classes.JsonHandle;
import Supporting_Classes.PropertiesHandle;
import Supporting_Classes.RequestResponse;

public class BaseClass 
{
	protected RequestResponse sampleInput = null;
	protected RequestResponse request = null;
	protected RequestResponse response = null;
	protected DatabaseOperation XmlElements = null;
	protected DatabaseOperation jsonElements = null;
	protected PropertiesHandle config = null;
	protected DatabaseOperation input = null;
	protected String[] actualColumnCol = null;
	protected String[] inputColumnCol = null;
	protected String[] statusColumnCol = null;
	protected int statusColumnSize;
	protected int actualColumnSize;
	protected int inputColumnSize;
	protected HttpHandle http = null;
	
//---------------------------------------------------------------LOAD SAMPLE REQUEST--------------------------------------------------------------------	
	public void LoadSampleRequest(DatabaseOperation InputData) throws SQLException
	{
		
		this.input = InputData;
		sampleInput = new JsonHandle(config.getProperty("sample_request"));
		
	}

//-----------------------------------------------------------PUMPING TEST DATA TO REQUEST--------------------------------------------------------------- 	
	public void PumpDataToRequest() throws SQLException, IOException, DocumentException, ParseException
	{
		request = new JsonHandle(config.getProperty("request_location")+input.ReadData("testdata")+".json");
		request.StringToFile(sampleInput.FileToString());
		
		for(int i=0;i<inputColumnSize;i++)
		{
			if(!input.ReadData(inputColumnCol[i]).equals(""))
			{
			request.write(jsonElements.ReadData(inputColumnCol[i]), input.ReadData(inputColumnCol[i]));
			}
		}
	}

//------------------------------------------------------------CONVERTING REQUEST TO STRING--------------------------------------------------------------	
	public String RequestToString() throws IOException, ParseException, DocumentException
	{
		
		return request.FileToString();
		
	}
	
//-------------------------------------------------------------ADDING HEADER || TOKENS------------------------------------------------------------------	
	public void AddHeaders() throws IOException
	{
		
		http = new HttpHandle(config.getProperty("test_url"),"POST");
		http.AddHeader("Content-Type", config.getProperty("content_type"));
		http.AddHeader("Token", config.getProperty("token"));
		
	}

//------------------------------------------------------------STORING RESPONSE TO FOLDER----------------------------------------------------------------	
	public void SendAndReceiveData() throws SQLException
	{
		String input_data= null;
		try 
		{
			input_data = request.FileToString();
		} 
		catch (IOException | ParseException | DocumentException e) 
		{
			e.printStackTrace();
		}
		try 
		{
			http.SendData(input_data);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		String response_string = null;
		
		try 
		{
			response_string = http.ReceiveData();
		} 
		catch (Exception e) 
		{
			
			e.printStackTrace();
		}
		response = new JsonHandle(config.getProperty("response_location")+input.ReadData("testdata")+".json");
		try 
		{
			response.StringToFile(response_string);
			
		} 
		catch (IOException | DocumentException e) 
		{
			e.printStackTrace();
		}
	}
	
//-------------------------------------------------------------CONVERTING RESPONSE TO STRING------------------------------------------------------------
	public String ResponseToString() throws IOException, ParseException, DocumentException
	{
		
		return response.FileToString();
	}
	
//-----------------------------------------------------------UPDATING RESPONSE DATA TO DATABASE---------------------------------------------------------	
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws UnsupportedEncodingException, IOException, ParseException, DocumentException, SQLException
	{
		for(int i=0;i<actualColumnSize;i++)
		{
			try
			{
			String actual = (response.read(jsonElements.ReadData(actualColumnCol[i])).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
			output.WriteData(actualColumnCol[i], actual);
			System.out.println(actual);
			output.WriteData("flag_for_execution", "Completed");
			}
			catch(PathNotFoundException e)
			{
				output.WriteData(actualColumnCol[i], "Path not Found");
			}
		}
	return output;
	}

//---------------------------------------------------------------COMAPRISION FUNCTION-------------------------------------------------------------------	
	public DatabaseOperation CompareFunction(DatabaseOperation output) throws SQLException
	{
		for(int i=0;i<statusColumnSize;i++)
		{
			String[] StatusIndividualColumn = statusColumnCol[i].split("-");
			String ExpectedColumn = StatusIndividualColumn[0];
			String ActualColumn = StatusIndividualColumn[1];
			String StatusColumn = StatusIndividualColumn[2];
			if(premium_comp(output.ReadData(ExpectedColumn),output.ReadData(ActualColumn)))
			{
				output.WriteData(StatusColumn, "Pass");
			}
			else
			{
				output.WriteData(StatusColumn, "Fail");
			}
			
		} return output;
		
	}
	
//-----------------------------------------------------PRIVATE FUNCTION FOR SUPPORTING COMPARISON FUNCTION---------------------------------------------------	
	protected static boolean premium_comp(String expected,String actual)
	{
		
		boolean status = false;
		if(expected == null || actual == null ||expected.equals("") || actual.equals(""))
		{
			status = false;
		}
		else
		{
			expected = expected.replaceAll("\\[\"", "");
			actual = actual.replaceAll("\\[\"", "");
			expected = expected.replaceAll("\"\\]", "");
			actual = actual.replaceAll("\"\\]", "");
			expected = expected.replaceAll("\\.[0-9]*", "");
			actual = actual.replaceAll("\\.[0-9]*", "");
			
			System.out.println(actual);
			System.out.println(expected);
			if(expected.equals(actual))
			{
				status = true;
			}
			else 
			{
				status = false;
			}
		}
		return status;	
		
	}
	
}
