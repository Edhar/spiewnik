package com.zelwise.spiewnik;

import java.io.BufferedReader;
//import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.impl.client.DefaultHttpClient; 
//import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DownloadHelper {
 
	//private static final String PATH = "/data/data/com.zelwise.spiewnik/Temp/";
	
	public static Document DownloadFromUrlDocument(String url) throws IOException{
		Document doc = Jsoup.connect(url).get();
		return doc;
	}
	
	public static String DownloadFromUrl(String url) throws ClientProtocolException, IOException
	{
			  
			  HttpClient client = new DefaultHttpClient();
			  HttpGet get = new HttpGet(url);
			  
			  HttpResponse response = client.execute(get);
			  
			  HttpEntity entity = response.getEntity();
			  BufferedReader buf = new BufferedReader(new InputStreamReader(entity.getContent()));
			  StringBuilder sb = new StringBuilder();
			  String line = null;
			  while ((line = buf.readLine()) != null) {
			   sb.append(line+"\n");
			  }
			  
			  return sb.toString();
	}
	
	/*
    public static void CreateDirIfNotExistsOrClear(String Path) {

        File dir = new File(Path);
        if(dir.isDirectory()){
	        if (!dir.exists()) {
	            if (!dir.mkdirs()) {
	                Log.e("TravellerLog :: ", "Problem creating Image folder");
	            }
	        }else{
	        	String[] children = dir.list();
	            for (int i = 0; i < children.length; i++) {
	                new File(dir, children[i]).delete();
	            }
	        }
        }
    }*/
}
