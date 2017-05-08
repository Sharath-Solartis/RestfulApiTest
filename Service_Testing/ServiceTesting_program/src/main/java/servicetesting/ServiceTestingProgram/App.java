package servicetesting.ServiceTestingProgram;

import util.common.DatabaseOperation;
import apiPackage.API;
import apiPackage.ChicForms;
import apiPackage.ChicRating;
import apiPackage.DtcFindPolicy;
import apiPackage.DtcGetCustomerDetails;
import apiPackage.DtcGetPolicy;
import apiPackage.DtcPayIssue;
import apiPackage.DtcPreviewPDF;
import apiPackage.DtcRatingService;
import apiPackage.DtcSaveDetails1;
import apiPackage.DtcSaveDetails2;
import apiPackage.DtcSaveDetails3;
import apiPackage.DtcSaveDetails4;
import apiPackage.IsoBopEndrosement;
import apiPackage.IsoBopInstalllmentPayissue;
import apiPackage.IsoBopPayissue;
import apiPackage.IsoBopPayissueCancel;
import apiPackage.IsoBopQuote;
import apiPackage.IsoBopRateCancel;
import apiPackage.IsoBopissue;
import apiPackage.IsoBoprating;
import apiPackage.SolartisIsoBopRating;
import apiPackage.StarrSearchRescueIssueCertificate;
import java.io.IOException;
import java.sql.SQLException;
import org.dom4j.DocumentException;
import org.json.simple.parser.ParseException;
import util.common.PropertiesHandle;
import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
	static Logger logError = Logger.getLogger("ERRORlog");
	static Logger logInfo = Logger.getLogger("INFOlog");
	static API api=null;
	
	public static void main( String[] args ) throws ClassNotFoundException 
    {   
		System.setProperty("jsse.enableSNIExtension", "false");
		PropertiesHandle config = new PropertiesHandle("A:/1 Projects/09 ISO/Release_14 -help/Rating/configuration_file/config_json.properties");
		//PropertiesHandle config = new PropertiesHandle(args[0]);
		
		try                                      
		{
			logInfo.info("Connecting DataBase");
			DatabaseOperation.ConnectionSetup(config);
		} 
		catch (ClassNotFoundException e) 
		{
			logError.error("Failed DataBase Connection -- ClassNotFoundError");
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			logError.error("Failed DataBase Connection -- SQLError");
			e.printStackTrace();
		}
		String actualchoice = config.getProperty("actual");
		String statuschoice = config.getProperty("status");
		String outputtable = config.getProperty("output_in_same_table");
		
		DatabaseOperation input = new DatabaseOperation();
		try 
		{
			logInfo.info("Creating Instance for Input");
			input.GetDataObjects(config.getProperty("input_query"));
		}
		catch (SQLException e1) 
		{
			logError.error("Failed Creating Instance for Input -- SQLError");
			e1.printStackTrace();
		}
		
		DatabaseOperation output = new DatabaseOperation();
		try 
		{
			logInfo.info("Creating Instance for Output");
			output.GetDataObjects(config.getProperty("output_query"));	
		} 
		catch (SQLException e1) 
		{
			logError.error("Failed Creating Instance for Output -- SQLError");
			e1.printStackTrace();
		}
		
		String ApiType = config.getProperty("ApiType");
	
		logInfo.info("Selecting API");
		try 
		{
		switch(ApiType.toLowerCase())//SELECTING API 
        {
      
          case "isoquote":
        	    logInfo.info("IsoBopQuote API Selected");
				api = new IsoBopQuote(config);
    	        break;
      
          case "isorate":
        	    logInfo.info("IsoBopRating API Selected");
    	        api = new IsoBoprating(config);
    	        break;
    	  
          case "isobopendrosement":
        	    logInfo.info("IsoBopQuote API Selected");
    	        api = new IsoBopEndrosement(config);
    	        break;
    	  
          case "isobopinstalllmentpayissue":
        	    logInfo.info("IsoBopQuote API Selected");
    	        api = new IsoBopInstalllmentPayissue(config);
    	        break;
    	  
          case "isobopissue":
        	    logInfo.info("IsoBopQuote API Selected");
    	        api = new IsoBopissue(config);
    	        break;
    	  
          case "isoboppayissue":
        	    logInfo.info("IsoBopQuote API Selected");
        	    api = new IsoBopPayissue(config);
    	        break;
    	  
          case "isoboppayissuecancel":
        	    logInfo.info("IsoBopQuote API Selected");
        	    api = new IsoBopPayissueCancel(config);
    	        break;
    	  
          case "solartisisoboprating":
        	    logInfo.info("IsoBopQuote API Selected");
    	        api = new SolartisIsoBopRating(config);	
    	        break;
    	  
          case "isobopratecancel":
        	    logInfo.info("IsoBopQuote API Selected");
    	        api = new IsoBopRateCancel(config);	  
    	        break;
    	        
          case "chicform":
        	    logInfo.info("IsoBopQuote API Selected");
  	            api = new ChicForms(config);
  	            break;
  	        
          case "chicrating":
        	    logInfo.info("IsoBopQuote API Selected");
  	            api = new ChicRating(config);
  	            break;
  	        
          case "dtcfindpolicy":
        	    logInfo.info("IsoBopQuote API Selected");
  	            api = new DtcFindPolicy(config);
  	            break;
  	            
          case "dtcgetpolicy":
        	    logInfo.info("IsoBopQuote API Selected");
        	    api = new DtcGetPolicy(config);
	            break;
  	        
          case "dtcgetcustomerdetails":
        	    logInfo.info("IsoBopQuote API Selected");
  	            api = new DtcGetCustomerDetails(config);
  	            break;
  	        
          case "dtcpayissue":
        	    logInfo.info("IsoBopQuote API Selected");
  	            api = new DtcPayIssue(config);
  	            break;
  	        
          case "dtcpreviewpdf":
        	    logInfo.info("IsoBopQuote API Selected");
  	            api = new DtcPreviewPDF(config);
  	            break;
  	        
          case "dtcratingservice":
        	   logInfo.info("IsoBopQuote API Selected");
  	           api = new DtcRatingService(config);
  	           break;
  	        
          case "dtcsavedetails1":
        	    logInfo.info("IsoBopQuote API Selected");
  	            api = new DtcSaveDetails1(config);
  	            break;
  	        
          case "dtcsavedetails2":
        	    logInfo.info("IsoBopQuote API Selected");
        	    api = new DtcSaveDetails2(config);
  	            break;
  	        
          case "dtcsavedetails3":
        	    logInfo.info("IsoBopQuote API Selected");
    	        api = new DtcSaveDetails3(config);
    	        break;
    	        
          case "dtcsavedetails4":
        	    logInfo.info("IsoBopQuote API Selected");
    	        api = new DtcSaveDetails4(config);
    	        break;
    	        
          case "starrsearchrescueissuecertificate":
        	    logInfo.info("IsoBopQuote API Selected");
  	            api = new StarrSearchRescueIssueCertificate(config);
  	            break;
    	        
          default :
        	     logError.error("API Selected is Wrong");
        	     System.out.println("API is not coded"); 
        	     System.exit(0);
        	     break;
    	  
       }
	   } 
		catch (SQLException e) 
		{
			logError.error("Failed Failed Selecting API -- SQLError");
			e.printStackTrace();
		}

		int i = 1;

			try 
			{

				do
				{
					logInfo.info("TestData" + i + "Running" );
					System.out.println("TestData : " + i);  	
							if(input.ReadData("flag_for_execution").equals("Y"))
							{
							    logInfo.info("TestData" + i + "flag_for_execution = Y" );	
								
									logInfo.info("Loading Sample Request for Testdata--" + i);
									  api.LoadSampleRequest(input);//LOADING SAMPLE REQUEST
					
								try 
								{
									logInfo.info("Pumping Testdata--" + i + "To Sample Request");
								      api.PumpDataToRequest();//PUMPING TESTDATA TO SAMPLEREQUEST 
								} 
								catch (IOException | DocumentException | ParseException e)
								{
									logError.error("Failed Pumping Testdata--" + i + "To Sample Request---IOException | DocumentException | ParseException");
									e.printStackTrace();
								} 
								
								try 
								{
									logInfo.info("REQUEST For Testdata--" + i);
									  String req = api.RequestToString();//SHOWING REQUEST IN LOG 
									logInfo.info(req);
								} 
								catch (IOException | ParseException | DocumentException e1) 
								{
									logError.error("Failed Rquest For Testdata--" + i + "---IOException | ParseException | DocumentException");
									e1.printStackTrace();
								} 
								
								try 
								{
									logInfo.info("Adding Header For Testdata--" + i);
									  api.AddHeaders();//ADDING HEADER || TOKENS || EVENTS FOR HITTING REQUEST
								} 
								catch (IOException e) 
								{
									logError.error("Failed Adding Header For Testdata--" + i + "---IOException");
									e.printStackTrace();
								}
								
									logInfo.info("Respone For Testdata--" + i + "is Received");
									api.SendAndReceiveData();//RECIEVING AND STORING RESPONSE TO THE FILE
								
									logInfo.info("REQUEST For Testdata--" + i);
									try 
									{
										logInfo.info("REQUEST For Testdata--" + i);
										  String res = api.ResponseToString();//SHOWING RESPONSE IN LOG 
										logInfo.info(res);
									} 
									catch (IOException | ParseException | DocumentException e1) 
									{
										logError.error("Failed Rquest For Testdata--" + i + "---IOException | ParseException | DocumentException e1");
										e1.printStackTrace();
									}						
								
								if(actualchoice.equals("Y"))
								{
									 logInfo.info("Fetching Data from Response to Store in DB is selected");
									  
								if(outputtable.equals("Y"))//INPUT AND OUT DB TABLE ARE SAME
								{	
							    try 
							    {
										logInfo.info("Storing Response--" + i + "Data into DB");
										input = api.SendResponseDataToFile(input);//FETCHING DATA FROM RESPONSE AND STORE THEM INTO THE DATABASE TABLE
								} 
								catch (IOException | ParseException | DocumentException e) 
								{
										 logError.error("Failed Storing Response--" + i + "Data into DB---IOException | ParseException | DocumentException e");
										 e.printStackTrace();
								}
									 
									     logInfo.info("Updating DB For Testdata--" + i);
									     input.UpdateRow();//UPDATE DB TABLE ROWS AFTER INSERTING RESPONSE DATA
								}
								else//INPUT AND OUT DB TABLE ARE DIFFERENT
								{	
								try 
							    {
										logInfo.info("Storing Response--" + i + "Data into DB");
										output = api.SendResponseDataToFile(output);//FETCHING DATA FROM RESPONSE AND STORE THEM INTO THE DATABASE TABLE
								} 
								catch (IOException | ParseException | DocumentException e) 
							    {
										 logError.error("Failed Storing Response--" + i + "Data into DB---IOException | ParseException | DocumentException e");
										 e.printStackTrace();
								}
									 
									     logInfo.info("Updating DB For Testdata--" + i);
									     output.UpdateRow();//UPDATE DB TABLE ROWS AFTER INSERTING RESPONSE DATA	
								}
								} 
								
								if(statuschoice.equals("Y"))
								{
								 logInfo.info("Comparison is selected");
								 logInfo.info("Starts Copmaring Expected and atual");
							       output = api.CompareFunction(output);//CALLING COMPARING FUNCTION
							     logInfo.info("Updating DB For Comparison");
								 output.UpdateRow();
								} 
								
								input.WriteData("Flag_for_execution", "Completed");
								  input.UpdateRow();//UPDATE DB TABLE ROWS AFTER COMPARSION
								}
							else
							{
								logError.error("TestData" + i + "---flag_for_execution N");
								logInfo.info("TestData" + i + "---flag_for_execution N" );
							}
					 i++;
					logInfo.info("Moving To Next TestData");
					if(actualchoice.equals("Y") || statuschoice.equals("Y"))
					{
						output.MoveForward();
					}
				}while(input.MoveForward());
			} 
			catch (SQLException e1)
			{
				e1.printStackTrace();
				System.exit(0);
				

			}

		try 
		{
			logInfo.info("Closing DB Connection");
			DatabaseOperation.CloseConn();
		}
		catch (SQLException e) 
		{
			logError.error("Failed DataBase Closing -- SQLError");
			e.printStackTrace();
			System.exit(0);
		}
   }
}	
