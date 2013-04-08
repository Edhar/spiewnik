package com.zelwise.spiewnik;

import java.io.IOException;

import org.jsoup.nodes.Document;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class AppManager {
	public DBHelper dBHelper;
	public SQLiteDatabase db;
	public Context context;
	public ViewPager viewPager;
	private Handler responseHandler;
	
	public AppManager(Context context,ViewPager viewPager) {
		this.dBHelper = new DBHelper(context);
		this.context = context;
		this.viewPager = viewPager;
		
		if(!this.dBHelper.checkDataBase()){
			copyDb();
		}
		
		this.db = dBHelper.getWritableDatabase();
    }
	
	private void copyDb(){
		try
		{
			dBHelper.copyDataBase();
		}
		catch (IOException ioe)
		{
	 		throw new Error("Unable to create database");
	 	}
		
		/*
        try
        {
        	this.dBHelper.createDataBase();
	 	}
        catch (IOException ioe) {
	 		throw new Error("Unable to create database");
	 	}
	 
	 	try
	 	{
	 		this.dBHelper.OpenDataBase();
	 	}
	 	catch(SQLException sqle)
	 	{
	 		throw sqle;
	 	}*/
	}
	
	public void HideKeyboard(){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
	}
	
	public void ShowKeyboard(){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(viewPager.getWindowToken(), InputMethodManager.SHOW_FORCED);
	}
	
	public void CloseDB() {
		dBHelper.close();
    }
	
	public void DownloadAndSaveContent(final String url,final Integer startNumber, final Integer endNumber){
		responseHandler = new Handler() 
        {                               
            public void handleMessage(Message msg)  
            {
                super.handleMessage(msg);
                try 
                {
                	WriteStatus(Utils.Join(msg.getData().getCharArray("status"), ""));
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }                       
            }
        };
        
		Thread thread = new Thread(new Runnable(){
			    @Override
			    public void run() {
		        	for(int number = startNumber; number <= endNumber; number++){
		        		Document doc = null;
				        try 
				        {		        	
				        	doc = DownloadHelper.DownloadFromUrlDocument(url + number);
				        	new Song(doc, number).SaveOrUpdate(db);
				        }
				        catch (Exception e)
				        {
				            //e.printStackTrace();
				        }
				        
				        String status =  (number - startNumber + 1) + " of " + (endNumber - startNumber+1);
				        if(number == endNumber){
				        	status = "Completed";
				        }
				        
				        Message msg =  new Message();
				        Bundle bundle = new Bundle();
				        bundle.putCharArray("status", status.toCharArray());
				        msg.setData(bundle);
				        responseHandler.sendMessage(msg);
			        }
			    }
			});
		
		  thread.start();
	  }
	
	private void WriteStatus(String status){
		View settingsView = this.viewPager.getChildAt(this.viewPager.getCurrentItem());
		TextView songTitle = (TextView)settingsView.findViewById(R.id.DownloadStatus);
		if(songTitle != null){
			songTitle.setText(status);
		}
	}
}
