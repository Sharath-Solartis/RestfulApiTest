package util.api;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.JsonReader;

public class JsonHandle implements RequestResponse
{
	private JsonPath path;
	private JSONObject obj = new JSONObject();
	private JSONParser parser = new JSONParser();
	private JsonReader  doc = new JsonReader();
	private String file_location;
	private FileReader read_file = null;
	private FileWriter write_file = null;
	public JsonHandle(String file_location)
	{
		this.file_location = file_location;
		
	}
	
	
	public JsonHandle() {
		// TODO Auto-generated constructor stub
	}

	public void getFilePath(String filepath) 
	{
		// TODO Auto-generated method stub
		this.file_location = filepath;
	}

	private String enable_read() throws IOException, ParseException
	{
			
				read_file = new FileReader(file_location);
				obj = (JSONObject) parser.parse(read_file);
			
			read_file = null;
			return obj.toJSONString();
		
	}
	
	private void enable_write(String json_string) throws IOException
	{
		
			
			write_file = new FileWriter(file_location);
			write_file.write(json_string);
			write_file.flush();
			write_file.close();
		
	}
	
	
	public void StringToFile(String json_string) throws IOException
	{
		//System.out.println("Going to create json");
		enable_write(json_string);
	}
	
	public String read(String json_path) throws IOException, ParseException
	{
		doc.parse(enable_read());
		
		path = JsonPath.compile(json_path);
		
		return doc.read(path).toString();
		
	}
	
	public String FileToString() throws IOException, ParseException
	{
		doc.parse(enable_read());
		//System.out.println(doc.jsonString());
		return doc.jsonString();
	}
	
	public void write(String json_path,String new_value) throws IOException, ParseException
	{
		doc.parse(enable_read());
		path = JsonPath.compile(json_path);
		doc.set(path, new_value);
		enable_write(doc.jsonString());
		
	}


	

	
	
}
