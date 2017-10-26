package com.solartis.test.util.common;

public interface ExcelOperationInterface 
{
	public void openWorkbook() throws Exception;
	public void getsheets(String sheet_name);
	public void getcell(int row_number,int column_number);
	public String read_data();
	public String read_data(int rowNumber, int columnNumber);
	public int get_rownumber();
	public int get_columnnumber();
	public void set_rownumber(int row_number);
	public void set_columnnumber(int column_number);
	public void next_row();
	public void next_col();	
	public boolean has_next_row();	
	public boolean has_next_row(int columnnumberr);	
	public boolean has_next_column();
	public void write_data(String strData);	
	public void write_data(int strData);	
	public void write_data(int rownum,int columnnum,Object strData);	
	public void refresh();	
	public void save() throws Exception;
	public void saveAs(String Targetexpectedpath) throws Exception;	
	public void Copy(String Sampleexcelpath, String Targetexpectedpath);
}
