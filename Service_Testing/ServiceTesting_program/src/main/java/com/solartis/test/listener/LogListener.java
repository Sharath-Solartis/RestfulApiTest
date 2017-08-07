package com.solartis.test.listener;

import org.apache.log4j.Logger;


public class LogListener implements Listener
{
	static Logger logError = Logger.getLogger("ERRORlog");
	static Logger logInfo = Logger.getLogger("INFOlog");
	
	
	@Override
	public void beforeLoadSampleRequest()
	{
		
	}

	@Override
	public void afterLoadSampleRequest() 
	{
		
	}

	@Override
	public void beforePumpDataToRequest()
	{
		
	}

	@Override
	public void afterPumpDataToRequest() 
	{
		
	}
	@Override
	public void beforeRequestToString()
	{
		
	}

	@Override
	public void afterRequestToString() 
	{
		
	}

	@Override
	public void beforeAddHeaders()
	{
		
	}

	@Override
	public void afterAddHeaders() 
	{
		
	}

	@Override
	public void beforeSendAndReceiveData()
	{
		
	}

	@Override
	public void afterSendAndReceiveData() 
	{
		
	}
	
	@Override
	public void beforeResponseToString()
	{
		
	}

	@Override
	public void afterResponseToString() 
	{
		
	}

	@Override
	public void beforeSendResponseDataToFile()
	{
		
	}

	@Override
	public void afterSendResponseDataToFile() 
	{
		
	}

	@Override
	public void beforeCompareFunction() 
	{
		
	}

	@Override
	public void afterCompareFunction() 
	{
		
	}

	@Override
	public void onError(Throwable e)
	{
		System.out.println("Printed by Listener");
		//System.out.println(e.getMessage()+e.getCause().getMessage()+e.getCause().getCause().getMessage());
		e.printStackTrace();
	}

	

	

}
