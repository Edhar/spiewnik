package com.zelwise.spiewnik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

//see here http://twigstechtips.blogspot.com/2010/11/android-save-and-load-settings.html
public class SettingsHelper {
	public static class Tags {
		public static final String SongPerPage = "SongPerPage";
		public static final String SeachByAndShowSongNumbersInResult = "SeachByAndShowSongNumbersInResult";
	}
	public static class DefaultValues {
		public static Integer SongPerPage = 20;
		public static Boolean SeachByAndShowSongNumbersInResult = false;
	}
	
	private final String sharedPreferencesName = "AppSettings";
	private final Integer sharedPreferencesMode = 0;
	
	private Context context;
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;

	public Integer MinSymbolsForStartSearch = 1;
		
	private Integer songPerPage;
	public Integer SongPerPage() {
		return songPerPage;
	}
	public void MaxSongInResultList(Integer newValue) {
		songPerPage = newValue;
		editor.putInt(Tags.SongPerPage, songPerPage);
		editor.commit();
	}
	public void MaxSongInResultList(String newValue) {
		songPerPage = Utils.ToInt(newValue, DefaultValues.SongPerPage);
		editor.putInt(Tags.SongPerPage, songPerPage);
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
		songPerPage = settings.getInt(Tags.SongPerPage,
				DefaultValues.SongPerPage);
		
		seachByAndShowSongNumbersInResult = settings.getBoolean(Tags.SeachByAndShowSongNumbersInResult,
				DefaultValues.SeachByAndShowSongNumbersInResult);
	}
}
