package util.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;


public interface RequestResponse 
{
	
	public String FileToString() throws IOException, ParseException, DocumentException;
	public void getFilePath(String filepath);
	public String read(String Path) throws IOException, ParseException, DocumentException,UnsupportedEncodingException;
	public void StringToFile(String String) throws IOException, DocumentException;
	public void write(String Path,String Value) throws IOException, ParseException, DocumentException;
}
