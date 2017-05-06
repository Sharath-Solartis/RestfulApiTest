package apiPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;
import com.jayway.jsonpath.PathNotFoundException;
import util.api.*;
import util.common.*;

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
	protected DBColoumnVerify InputColVerify = null;
	protected DBColoumnVerify OutputColVerify = null;
	
//---------------------------------------------------------------LOAD SAMPLE REQUEST--------------------------------------------------------------------	
	public void LoadSampleRequest(DatabaseOperation InputData) throws SQLException
	{
		this.input = InputData;
		sampleInput = new JsonHandle(config.getProperty("sample_request"));
	}

//-----------------------------------------------------------PUMPING TEST DATA TO REQUEST--------------------------------------------------------------- 	
	public void PumpDataToRequest() throws SQLException, IOException, DocumentException, ParseException, ClassNotFoundException
	{
		request = new JsonHandle(config.getProperty("request_location")+input.ReadData("testdata")+".json");
		request.StringToFile(sampleInput.FileToString());
		
		DBColoumnVerify.ConnectionSetup(config);
		
		do
		{
			if(InputColVerify.DbCol(input))
			{
				if(!input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))).equals(""))
				{
					request.write(jsonElements.ReadData(config.getProperty("InputColumn")), input.ReadData(InputColVerify.ReadData(config.getProperty("InputColumn"))));
				}
			}	
		}while(InputColVerify.MoveForward());
		
		DBColoumnVerify.CloseConn();
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
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws UnsupportedEncodingException, IOException, ParseException, DocumentException, SQLException, ClassNotFoundException
	{

		DBColoumnVerify.ConnectionSetup(config);
				
		do 	
		{
				  
		  if(OutputColVerify.DbCol(output))
			{
			try
				{
				String actual = (response.read(jsonElements.ReadData(output.ReadData(OutputColVerify.ReadData(config.getProperty("OutputColumn"))))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
				output.WriteData(output.ReadData(OutputColVerify.ReadData(config.getProperty("OutputColumn"))), actual);
				System.out.println(actual);
				output.WriteData("flag_for_execution", "Completed");
				}
				catch(PathNotFoundException e)
				{
					output.WriteData(output.ReadData(OutputColVerify.ReadData(config.getProperty("OutputColumn"))), "Path not Found");
				}
			}
		}while(OutputColVerify.MoveForward());
	
		DBColoumnVerify.CloseConn();
		
	return output;
	}

//---------------------------------------------------------------COMAPRISION FUNCTION-------------------------------------------------------------------	
	public DatabaseOperation CompareFunction(DatabaseOperation output) throws SQLException, ClassNotFoundException
	{
		DBColoumnVerify.ConnectionSetup(config);
		
		do 	
		{
				  
		  if(OutputColVerify.DbCol(output))
			{
				//String[] StatusIndividualColumn = statusColumnCol[i].split("-");
				String ExpectedColumn = output.ReadData(OutputColVerify.ReadData(config.getProperty("ExpectedColumn")));
				String ActualColumn = output.ReadData(OutputColVerify.ReadData(config.getProperty("OutputColumn")));
				String StatusColumn = output.ReadData(OutputColVerify.ReadData(config.getProperty("StatusColumn")));
				if(premium_comp(output.ReadData(ExpectedColumn),output.ReadData(ActualColumn)))
				{
					output.WriteData(StatusColumn, "Pass");
				}
				else
				{
					output.WriteData(StatusColumn, "Fail");
				}
				
			}
		 }while(OutputColVerify.MoveForward());
			
		  DBColoumnVerify.CloseConn(); 
	
	return output;
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
