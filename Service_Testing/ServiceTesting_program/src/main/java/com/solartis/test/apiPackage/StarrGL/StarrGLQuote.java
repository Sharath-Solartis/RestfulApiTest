package com.solartis.test.apiPackage.StarrGL;

import java.sql.SQLException;

import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.apiPackage.API;
import com.solartis.test.apiPackage.BaseClass;
import com.solartis.test.util.api.DBColoumnVerify;
import com.solartis.test.util.common.*;

public class StarrGLQuote extends BaseClass implements API 
{
	public StarrGLQuote(PropertiesHandle config) throws SQLException
	{
		this.config = config;
		jsonElements = new DatabaseOperation();
		
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
	}
}
