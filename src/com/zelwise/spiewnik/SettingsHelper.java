package com.zelwise.spiewnik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

//see here http://twigstechtips.blogspot.com/2010/11/android-save-and-load-settings.html
public class SettingsHelper {
	private Context context;
	private final String sharedPreferencesName = "AppSettings";
	private final Integer sharedPreferencesMode = 0;
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;

	public Integer MaxSongInResultListDefault = 10;
	private Integer maxSongInResultList;

	public Integer MaxSongInResultList() {
		return maxSongInResultList;
	}

	public void MaxSongInResultList(Integer newValue) {
		maxSongInResultList = newValue;
		editor.putInt("MaxSongInResultList", maxSongInResultList);
		editor.commit();
	}
	public void MaxSongInResultList(String newValue) {
		maxSongInResultList = Utils.ToInt(newValue, MaxSongInResultListDefault);
		editor.putInt("MaxSongInResultList", maxSongInResultList);
		editor.commit();
	}

	public SettingsHelper(Context context) {
		this.context = context;
		this.settings = context.getSharedPreferences(sharedPreferencesName, sharedPreferencesMode);
		this.editor = settings.edit();
		this.loadAllSettings();
	}

	private void loadAllSettings() {
		maxSongInResultList = settings.getInt("MaxSongInResultList",
				MaxSongInResultListDefault);
	}
}
