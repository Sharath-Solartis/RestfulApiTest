package apiPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import jxl.read.biff.BiffException;

import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;

import util.common.*;

public interface API 
{
	public void LoadSampleRequest(DatabaseOperation InputData) throws SQLException,BiffException, IOException;
	public void PumpDataToRequest() throws SQLException, IOException, DocumentException, ParseException,ClassNotFoundException, NumberFormatException, java.text.ParseException, BiffException;
	public void AddHeaders() throws IOException;
	public void SendAndReceiveData() throws SQLException;
	public DatabaseOperation SendResponseDataToFile(DatabaseOperation output) throws UnsupportedEncodingException, IOException, ParseException, DocumentException, SQLException,ClassNotFoundException,NumberFormatException, java.text.ParseException;
	public DatabaseOperation CompareFunction(DatabaseOperation output) throws SQLException,ClassNotFoundException;
	public String RequestToString() throws IOException, ParseException, DocumentException;
	public String ResponseToString() throws IOException, ParseException, DocumentException;
	
}
