package util.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class XmlHandle implements RequestResponse{
	private String file_location;
	
	
	//***************constructor to load file location***************************************************************
	public XmlHandle(String file_location)
	{
		this.file_location = file_location;
		
	}
	
	//*****************method for reading the XML input file by passing the xpath value******************************
	 
	public void getFilePath(String filepath) 
	{
		// TODO Auto-generated method stub
		this.file_location = filepath;
	}
	
	public XmlHandle() 
	{
		// TODO Auto-generated constructor stub
	}
	
	

	public String read(String xpath) throws UnsupportedEncodingException, IOException,DocumentException
	{
		File inputFile = new File(file_location);
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputFile);
        //XPath xpath_w = document.createXPath(xpath);
        Node element = document.selectSingleNode(xpath);	                                                                                                                                                  
		return element.getText();	
	}
	

	//*********************method to convert a string into XML************************************************************
	
	public void StringToFile(String xml_data) throws IOException,DocumentException
	{
		Writer writer = new FileWriter(file_location);
        Document document = DocumentHelper.parseText(xml_data);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xmlwriter;
        xmlwriter = new XMLWriter( writer, format );
        xmlwriter.write( document );
        xmlwriter.close();
	}
	
	
	 //******************************method to modify values in existing XML by Passing XPath************************************************
	
	public void write(String xpath,String value) throws UnsupportedEncodingException, IOException, DocumentException
	{
		File inputFile = new File(file_location);
		SAXReader reader = new SAXReader();
        Document document = reader.read(inputFile);
        Node node = document.selectSingleNode(xpath);
        Element element = (Element)node;
        element.setText(value);
        OutputFormat format = OutputFormat.createPrettyPrint();
        Writer writer = new FileWriter(file_location);
        XMLWriter xmlwriter;
        xmlwriter = new XMLWriter( writer, format );
        xmlwriter.write( document );
        writer.close();
		
		
	}
		
   //*********************************method to Convert xml to string		
		
	public String FileToString() throws UnsupportedEncodingException, IOException, DocumentException
	{
		File inputFile = new File(file_location);
		SAXReader reader = new SAXReader();
        Document document = reader.read(inputFile);
		return document.asXML();
		
	}
	
	
	/*private String getxpath(String xpath) throws UnsupportedEncodingException, IOException,DocumentException
	{
		if(!(xpath.contains("=")))
		{
			return xpath;
		}
		else
		{
			String path[] = xpath.split("=");
			File inputFile = new File(file_location);
	        SAXReader reader = new SAXReader();
	        Document document = reader.read(inputFile);
	        String output = null;
	        output = path[0];
	        Node node = document.selectSingleNode(output);
	        
			
		}
		return null;
	}
	*/
	
	
	
	 
	 
	 




}



