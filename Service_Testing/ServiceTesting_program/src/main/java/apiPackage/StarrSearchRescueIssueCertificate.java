package apiPackage;

import java.io.IOException;
import java.sql.SQLException;
import util.api.*;
import util.common.*;

public class StarrSearchRescueIssueCertificate extends BaseClass implements API
{
	public StarrSearchRescueIssueCertificate(PropertiesHandle config) throws SQLException
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
	public void AddHeaders() throws IOException
	{
		http = new HttpHandle(config.getProperty("test_url"),"POST");
		http.AddHeader("Content-Type", config.getProperty("content_type"));
		http.AddHeader("Token", config.getProperty("token"));
		http.AddHeader("EventName", config.getProperty("EventName"));
	}
}
