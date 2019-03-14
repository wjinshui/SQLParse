package cn.edu.fjut.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Log {

	public static void main(String[] args) {
		Log log = new Log();
		
	}
	
	
	File file ;
	public Log(String sfile, boolean isDeleted)
	{
		file = new File(sfile);
		if(isDeleted)
			file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public Log(boolean isDeleted)
	{
		this("./log.txt", isDeleted);
	}
	
	public Log()
	{
		this(true);
	}
	
	public List<String> getContent()
	{
		List<String> content = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempString = null;            
            while ((tempString = reader.readLine()) != null) {
            	if(tempString.trim().length() > 0)
            		content.add(tempString);                
            }
            reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public void log(String content)
	{
		try {
			
			BufferedWriter bw = new BufferedWriter(new FileWriter( file, true));
			bw.write(content + "\r\n");
			bw.close();			
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
