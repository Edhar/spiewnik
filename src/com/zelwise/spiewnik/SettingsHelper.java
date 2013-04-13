package com.zelwise.spiewnik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

//see here http://twigstechtips.blogspot.com/2010/11/android-save-and-load-settings.html
public class SettingsHelper {
	public static class Tags {
		public static final String SongPerPage = "SongPerPage";
		public static final String SeachByAndShowSongNumbersInResult = "SeachByAndShowSongNumbersInResult";
		public static final String DoMoreRelevantSearch = "DoMoreRelevantSearch";
		public static final String MinSymbolsForStartSearch = "MinSymbolsForStartSearch";
	}
	public static class DefaultValues {
		public static Integer MinSymbolsForStartSearch = 1;
		public static Integer SongPerPage = 20;
		public static Boolean SeachByAndShowSongNumbersInResult = false;
		public static Boolean DoMoreRelevantSearch = false;
	}
	
	private final String sharedPreferencesName = "AppSettings";
	private final Integer sharedPreferencesMode = 0;
	
	private Context context;
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;

	private Integer minSymbolsForStartSearch;
	public Integer MinSymbolsForStartSearch() {
		return minSymbolsForStartSearch;
	}
	public void MinSymbolsForStartSearch(Integer newValue) {
		minSymbolsForStartSearch = newValue;
		editor.putInt(Tags.MinSymbolsForStartSearch, minSymbolsForStartSearch);
		editor.commit();
	}
	public void MinSymbolsForStartSearch(String newValue) {
		minSymbolsForStartSearch = Utils.ToInt(newValue, DefaultValues.MinSymbolsForStartSearch);
		editor.putInt(Tags.MinSymbolsForStartSearch, minSymbolsForStartSearch);
		editor.commit();
	}
	
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
	
	private Boolean doMoreRelevantSearch;
	public Boolean DoMoreRelevantSearch() {
		return doMoreRelevantSearch;
	}
	public void DoMoreRelevantSearch(Boolean newValue) {
		doMoreRelevantSearch = newValue;
		editor.putBoolean(Tags.DoMoreRelevantSearch, doMoreRelevantSearch);
		editor.commit();
	}

	public SettingsHelper(Context context) {
		this.context = context;
		this.settings = context.getSharedPreferences(sharedPreferencesName, sharedPreferencesMode);
		this.editor = settings.edit();
		this.loadAllSettings();
	}

	private void loadAllSettings() {		
		minSymbolsForStartSearch = settings.getInt(Tags.MinSymbolsForStartSearch,
				DefaultValues.MinSymbolsForStartSearch);
		
		songPerPage = settings.getInt(Tags.SongPerPage,
				DefaultValues.SongPerPage);
		
		seachByAndShowSongNumbersInResult = settings.getBoolean(Tags.SeachByAndShowSongNumbersInResult,
				DefaultValues.SeachByAndShowSongNumbersInResult);
		doMoreRelevantSearch = settings.getBoolean(Tags.DoMoreRelevantSearch,
				DefaultValues.DoMoreRelevantSearch);
	}
}
