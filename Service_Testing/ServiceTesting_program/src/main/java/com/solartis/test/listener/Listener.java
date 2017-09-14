package com.solartis.test.listener;

public interface Listener 
{
	public void beforeLoadSampleRequest();
	public void afterLoadSampleRequest();	
	public void beforePumpDataToRequest();	
	public void afterPumpDataToRequest();
	public void beforeRequestToString();	
	public void afterRequestToString();
	public void beforeAddHeaders();	
	public void afterAddHeaders();	
	public void beforeSendAndReceiveData();	
	public void afterSendAndReceiveData();
	public void beforeSendResponseDataToFile();
	public void afterSendResponseDataToFile();	
	public void beforeCompareFunction();	
	public void afterCompareFunction();		
	public void beforeResponseToString();	
	public void afterResponseToString();
	public void onError(Throwable e);
}
