package apiPackage;

import java.sql.SQLException;

import util.api.DBColoumnVerify;
import util.common.*;
import Configuration.PropertiesHandle;

public class IsoBopRateCancel extends BaseClass implements API 
{
	public IsoBopRateCancel(PropertiesHandle config) throws SQLException
	{
		this.config = config;
		jsonElements = new DatabaseOperation();
		jsonElements.GetDataObjects(config.getProperty("json_query"));
		
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
	}
}