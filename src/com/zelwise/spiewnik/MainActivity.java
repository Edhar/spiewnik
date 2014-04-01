package com.zelwise.spiewnik;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	MainActivity_Menu mainActivity_Menu;
	MainActivity_SettingsView mainActivity_SettingsView;
	MainActivity_SearchView mainActivity_SearchView;
	MainActivity_SongView mainActivity_SongView;
	MainActivity_Click mainActivity_Click;
	MainActivity_InAppBilling mainActivity_InAppBilling;

	AppPagerAdapter pagerAdapter;
	AppManager manager;

	public MainActivity() {
		mainActivity_SettingsView = new MainActivity_SettingsView(this);
		mainActivity_SearchView = new MainActivity_SearchView(this);
		mainActivity_SongView = new MainActivity_SongView(this);

		mainActivity_Menu = new MainActivity_Menu(this);
		mainActivity_Click = new MainActivity_Click(this);

		mainActivity_InAppBilling = new MainActivity_InAppBilling(this);
	}

	protected void FillWithSettings() {
		mainActivity_SettingsView.SetSelectedDefaultTab_byDefaultResultsForTab(manager.settings.DefaultTabId());

		mainActivity_SettingsView.SetTextWithoutEventRun_minSymbolsForStartSearch(manager.settings.MinSymbolsForStartSearch().toString());
		mainActivity_SettingsView.SetTextWithoutEventRun_maxSongsPerPageOnResult(manager.settings.SongsPerPage().toString());
		mainActivity_SettingsView.SetCheckBoxValue_seachByAndShowSongNumbersInResult(manager.settings.SeachByAndShowSongNumbersInResult());
		mainActivity_SettingsView.SetCheckBoxValue_doMoreRelevantSearch(manager.settings.DoMoreRelevantSearch());
		mainActivity_SettingsView.SetCheckBoxValue_doNotTurnOffScreen(manager.settings.DoNotTurnOffScreen());

		ToggleKeepScreenOn(manager.settings.DoNotTurnOffScreen());

		mainActivity_SongView.songContentEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, manager.settings.FontSize());

		mainActivity_SearchView.RefreshSearchTextHint();
	}

	private void InitTitle() {
		try {

			ActionBar actionBar = getSupportActionBar();
			Drawable actionBarDrawable = getResources().getDrawable(R.drawable.bg_action_bar);
			actionBar.setBackgroundDrawable(actionBarDrawable);

		} catch (Exception e) {
		}
	}

	protected void ToggleKeepScreenOn(Boolean keepScreenOn) {
		if (keepScreenOn) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	private OnPageChangeListener OnPageChangeListener_viewPager = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			mainActivity_SongView.CheckIfSongEditModeOrContentSaved();

			Activity activity = (Activity) manager.context;

			if (manager.viewPager.getCurrentItem() == MainActivity_SettingsView.SettingsViewIndex) {
				activity.setTitle(getResources().getString(R.string.settings_settingsName));
			} else if (manager.viewPager.getCurrentItem() == MainActivity_SearchView.SearchViewIndex) {
				activity.setTitle(getResources().getString(R.string.app_name));
			} else if (manager.viewPager.getCurrentItem() == MainActivity_SongView.SongViewIndex) {
				try {
					mainActivity_SongView.SetSongViewViewMode();
					EditText songTitle = (EditText) manager.GetViewPage(MainActivity_SongView.SongViewIndex).findViewById(R.id.SongTitleEditText);

					if (songTitle != null && songTitle.getText().length() != 0) {
						activity.setTitle(songTitle.getText());
					} else {
						activity.setTitle(getResources().getString(R.string.app_name));
					}
					;
				} catch (Exception e) {
					activity.setTitle(getResources().getString(R.string.app_name));
				}
			}
			manager.HideKeyboard();
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		InitTitle();

		LayoutInflater inflater = LayoutInflater.from(this);
		List<View> pages = new ArrayList<View>();

		View settingsView = inflater.inflate(R.layout.settings_view, null);
		pages.add(settingsView);

		View searchView = inflater.inflate(R.layout.search_view, null);
		pages.add(searchView);

		View songView = inflater.inflate(R.layout.song_view, null);
		pages.add(songView);

		pagerAdapter = new AppPagerAdapter(pages);
		ViewPager viewPager = new ViewPager(this);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(MainActivity_SearchView.SearchViewIndex);

		setContentView(viewPager);

		manager = new AppManager(this, viewPager);

		mainActivity_SettingsView.onCreate();
		mainActivity_SearchView.onCreate();
		mainActivity_SongView.onCreate();

		mainActivity_InAppBilling.onCreate();

		viewPager.setOnPageChangeListener(OnPageChangeListener_viewPager);

		FillWithSettings();

		RestoreState(savedInstanceState);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		mainActivity_Menu.onCreateContextMenu(menu, v, menuInfo);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public void onClick(View v) {
		mainActivity_Click.onClick(v);
	}

	public void ShowInFutureToast() {
		Toast.makeText(manager.context, "Only In Future", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mainActivity_Menu.onCreateOptionsMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		mainActivity_Menu.onPrepareOptionsMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}

	/*
	 * protected static final long DOUBLE_PRESS_INTERVAL = some value in ns.;
	 * protected long lastPressTime;
	 * 
	 * @Override public void onBackPressed() { long pressTime =
	 * System.nanoTime(); if(pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL)
	 * { // this is a double click event } lastPressTime = pressTime; }
	 */
	@Override
	public void onBackPressed() {
		if (manager.viewPager.getCurrentItem() == MainActivity_SettingsView.SettingsViewIndex) {
			manager.viewPager.setCurrentItem(MainActivity_SearchView.SearchViewIndex);
		} else if (manager.viewPager.getCurrentItem() == MainActivity_SearchView.SearchViewIndex) {
			moveTaskToBack(true);
		} else if (manager.viewPager.getCurrentItem() == MainActivity_SongView.SongViewIndex) {
			manager.viewPager.setCurrentItem(MainActivity_SearchView.SearchViewIndex);
		}

		manager.HideKeyboard();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return mainActivity_Menu.onOptionsItemSelected(item, super.onOptionsItemSelected(item));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return mainActivity_Menu.onContextItemSelected(item, super.onContextItemSelected(item));
	}

	@Override
	protected void onDestroy() {
		manager.CloseDB();
		mainActivity_InAppBilling.onDestroy();
		super.onDestroy();
	}

	protected void ShareSong(Song song) {
		// create the send intent
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);

		// set the type
		shareIntent.setType("text/plain");

		// add a subject
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, song.Title());

		// build the body of the message to be shared
		String shareMessage = song.Title() + Utils.NewLine + song.Content();

		// add the message
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);

		String shareDialogTitle = String.format(manager.context.getResources().getString(R.string.app_shareDialogTitle), Utils.Trim(song.Title(), 40));
		// start the chooser for sharing
		startActivity(Intent.createChooser(shareIntent, shareDialogTitle));
	}

	protected AppState GetAppState() {
		SongArrayAdapter adapter = (SongArrayAdapter) mainActivity_SearchView.songsListView.getAdapter();
		SearchTerms terms = adapter.GetSearchTerms();

		return new AppState(terms, manager.viewPager.getCurrentItem(), mainActivity_SongView.GetSongFromSongView(), mainActivity_SongView.IsSongEditMode(),
				mainActivity_SearchView.songsListView.getFirstVisiblePosition());
	}

	// DO NOT SKIP THIS METHOD
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mainActivity_InAppBilling.mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mainActivity_InAppBilling.mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			// onActivityResult handled by IABUtil.
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		AppState appState = GetAppState();
		outState.putSerializable("AppState", appState);
		manager.settings.ApplicationState(SerializeHelper.serializeObjectToString(appState));
		super.onSaveInstanceState(outState);
	}

	protected void RestoreState(Bundle savedInstanceState) {
		try {
			AppState state = null;
			if (savedInstanceState != null) {
				state = (AppState) savedInstanceState.getSerializable("AppState");
			} else {
				state = (AppState) SerializeHelper.deserializeObjectFromString(manager.settings.ApplicationState());
			}

			if (state != null && (new Date().getTime() - state.AppStateCreated.getTime()) < SettingsHelper.DefaultValues.AppStateDurationValid) {
				manager.viewPager.setCurrentItem(state.ViewIndex);
				if (state.IsSongEditMode) {
					mainActivity_SongView.SetSongEditMode();
				}

				mainActivity_SongView.UpdateContenSongView(state.SongViewSong);

				// mainActivity_SearchView.searchEditText.setText(state.Terms.SearchText());
				mainActivity_SearchView.SetTextWithoutEventRun_searchEditText(state.Terms.SearchText());
				if (state.Terms.SearchText().equals("")) {
					mainActivity_SearchView.ToggleClearSearchButton(false);
				} else {
					mainActivity_SearchView.ToggleClearSearchButton(true);
				}
				mainActivity_SearchView.CreateAdapterAndSetToSongList(state.Terms);
				mainActivity_SearchView.AddDynamicallyDataToListIfNeeded();
				mainActivity_SearchView.FocusedItemInSongsList(state.FirstVisibleSongPosition);
			} else {
				mainActivity_SearchView.LoadDefaultSongsListContent();
				mainActivity_SearchView.FocusedItemInSongsList(0);
			}
		} catch (Exception ex) {
		}
	}

}
