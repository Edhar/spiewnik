package com.zelwise.spiewnik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

//see here http://twigstechtips.blogspot.com/2010/11/android-save-and-load-settings.html
public class SettingsHelper {
	public static class Tags {
		public static final String MaxSongInResultList = "MaxSongInResultList";
		public static final String SeachByAndShowSongNumbersInResult = "SeachByAndShowSongNumbersInResult";
	}
	public static class DefaultValues {
		public static Integer MaxSongInResultList = 50;
		public static Boolean SeachByAndShowSongNumbersInResult = false;
	}
	
	private final String sharedPreferencesName = "AppSettings";
	private final Integer sharedPreferencesMode = 0;
	
	private Context context;
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;

	public Integer MinSymbolsForStartSearch = 1;
		
	private Integer maxSongInResultList;
	public Integer MaxSongInResultList() {
		return maxSongInResultList;
	}
	public void MaxSongInResultList(Integer newValue) {
		maxSongInResultList = newValue;
		editor.putInt(Tags.MaxSongInResultList, maxSongInResultList);
		editor.commit();
	}
	public void MaxSongInResultList(String newValue) {
		maxSongInResultList = Utils.ToInt(newValue, DefaultValues.MaxSongInResultList);
		editor.putInt(Tags.MaxSongInResultList, maxSongInResultList);
		editor.commit();
	}
	
	private Boolean seachByAndShowSongNumbersInResult;
	public Boolean SeachByAndShowSongNumbersInResult() {
		return seachByAndShowSongNumbersInResult;
	}
	public void SeachByAndShowSongNumbersInResult(Boolean newValue) {
		seachByAndShowSongNumbersInResult = newValue;
		editor.putBoolean(Tags.SeachByAndShowSongNumbersInResult, seachByAndShowSongNumbersInResult);
		editor.commit();
	}

	public SettingsHelper(Context context) {
		this.context = context;
		this.settings = context.getSharedPreferences(sharedPreferencesName, sharedPreferencesMode);
		this.editor = settings.edit();
		this.loadAllSettings();
	}

	private void loadAllSettings() {		
		maxSongInResultList = settings.getInt(Tags.MaxSongInResultList,
				DefaultValues.MaxSongInResultList);
		
		seachByAndShowSongNumbersInResult = settings.getBoolean(Tags.SeachByAndShowSongNumbersInResult,
				DefaultValues.SeachByAndShowSongNumbersInResult);
	}
}
