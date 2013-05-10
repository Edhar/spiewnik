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
		public static final String DefaultTabId = "DefaultTabId";
		public static final String FontSize = "FontSize";
	}
	public static class DefaultValues {
		public static Integer DefaultTabId = 0;
		public static Integer MinSymbolsForStartSearch = 3;
		public static Integer SongPerPage = 15;
		public static Boolean SeachByAndShowSongNumbersInResult = false;
		public static Boolean DoMoreRelevantSearch = true;
		public static Integer SiteRatingValue = 10000;
		public static float FontSize = 20;
		public static float FontSizeMagnifierStep = 2;
		public static float FontSizeMin(){
			return 6; 
		};
		public static float FontSizeMax(){
			return FontSize*2; 
		};
		public static long MagnifiedShowTime = 2000;
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
	
	private float fontSize;
	public float FontSize() {
		return fontSize;
	}
	public void FontSize(float newValue) {
		fontSize = newValue;
		editor.putFloat(Tags.FontSize, fontSize);
		editor.commit();
	}
	
	private Integer songPerPage;
	public Integer SongPerPage() {
		return songPerPage;
	}
	public void SongPerPage(Integer newValue) {
		songPerPage = newValue;
		editor.putInt(Tags.SongPerPage, songPerPage);
		editor.commit();
	}
	public void SongPerPage(String newValue) {
		songPerPage = Utils.ToInt(newValue, DefaultValues.SongPerPage);
		editor.putInt(Tags.SongPerPage, songPerPage);
		editor.commit();
	}
	
	private Integer defaultTabId;
	public Integer DefaultTabId() {
		return defaultTabId;
	}
	public void DefaultTabId(Integer newValue) {
		defaultTabId = newValue;
		editor.putInt(Tags.DefaultTabId, defaultTabId);
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
		defaultTabId = settings.getInt(Tags.DefaultTabId,
				DefaultValues.DefaultTabId);
		
		fontSize = settings.getFloat(Tags.FontSize,
				DefaultValues.FontSize);
		
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
