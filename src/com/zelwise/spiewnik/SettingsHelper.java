package com.zelwise.spiewnik;

import android.content.Context;
import android.content.SharedPreferences;

//see here http://twigstechtips.blogspot.com/2010/11/android-save-and-load-settings.html
public class SettingsHelper {
	public static class Tags {
		public static final String SongsPerPage = "SongPerPage";
		public static final String SeachByAndShowSongNumbersInResult = "SeachByAndShowSongNumbersInResult";
		public static final String DoMoreRelevantSearch = "DoMoreRelevantSearch";
		public static final String MinSymbolsForStartSearch = "MinSymbolsForStartSearch";
		public static final String DefaultTabId = "DefaultTabId";
		public static final String FontSize = "FontSize";
		public static final String DoNotTurnOffScreen = "DoNotTurnOffScreen";
		public static final String ApplicationState = "ApplicationState";
	}
	public static class DefaultValues {
		public static Integer SongsPerPageMin = 1;
		public static Integer SongsPerPageHiddenToAddMore = 5;
		public static Integer SongsPerPage = 15;
		public static Integer AddDynamicallyDataToListIfNeeded_CheckInterval = 100;
		public static long AppStateDurationValid = 600000;//10*60*1000 - 10min
		public static Integer DefaultTabId = 0;
		public static Integer MinSymbolsForStartSearch = 1;
		public static long StartSearchDelay = 400;		
		public static Boolean SeachByAndShowSongNumbersInResult = false;
		public static Boolean DoMoreRelevantSearch = true;
		public static Boolean DoNotTurnOffScreen = true;
		public static Integer SiteRatingValue = 10000;
		public static float FontSize = 20;
		public static float FontSizeMagnifierStep = 2;
		public static float FontSizeMin(){
			return 2; 
		};
		public static float FontSizeMax(){
			return FontSize*5; 
		};
		public static long MagnifiedShowTime = 2000;
		
		public static String ApplicationState = "";
	}
	
	private final String sharedPreferencesName = "AppSettings";
	private final Integer sharedPreferencesMode = 0;
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	
	private String applicationState;
	public String ApplicationState() {
		return applicationState;
	}
	
	public void ApplicationState(String newValue) {
		applicationState = newValue;
		editor.putString(Tags.ApplicationState, applicationState);
		editor.commit();
	}
	
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
	
	private Integer songsPerPage;
	public Integer SongsPerPage() {
		return songsPerPage;
	}
	public void SongsPerPage(Integer newValue) {
		songsPerPage = newValue;
		editor.putInt(Tags.SongsPerPage, songsPerPage);
		editor.commit();
	}
	public void SongsPerPage(String newValue) {
		songsPerPage = Utils.ToInt(newValue, DefaultValues.SongsPerPage);
		editor.putInt(Tags.SongsPerPage, songsPerPage);
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
	
	private Boolean doNotTurnOffScreen;
	public Boolean DoNotTurnOffScreen() {
		return doNotTurnOffScreen;
	}
	public void DoNotTurnOffScreen(Boolean newValue) {
		doNotTurnOffScreen = newValue;
		editor.putBoolean(Tags.DoNotTurnOffScreen, doNotTurnOffScreen);
		editor.commit();
	}

	public SettingsHelper(Context context) {
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
		
		songsPerPage = settings.getInt(Tags.SongsPerPage,
				DefaultValues.SongsPerPage);
		
		seachByAndShowSongNumbersInResult = settings.getBoolean(Tags.SeachByAndShowSongNumbersInResult,DefaultValues.SeachByAndShowSongNumbersInResult);
		
		doMoreRelevantSearch = settings.getBoolean(Tags.DoMoreRelevantSearch,DefaultValues.DoMoreRelevantSearch);
		
		doNotTurnOffScreen = settings.getBoolean(Tags.DoNotTurnOffScreen,DefaultValues.DoNotTurnOffScreen);
		
		applicationState = settings.getString(Tags.ApplicationState,DefaultValues.ApplicationState);
	}
}
