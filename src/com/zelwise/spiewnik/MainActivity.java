package com.zelwise.spiewnik;

import java.util.ArrayList;
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

public class MainActivity extends ActionBarActivity  implements OnClickListener {

	MainActivity_Menu mainActivity_Menu;
	MainActivity_SettingsView mainActivity_SettingsView;
	MainActivity_SearchView mainActivity_SearchView;
	MainActivity_SongView mainActivity_SongView;
	MainActivity_Click mainActivity_Click;

	AppPagerAdapter pagerAdapter;
	AppManager manager;

	public MainActivity() {
		mainActivity_SettingsView = new MainActivity_SettingsView(this);
		mainActivity_SearchView = new MainActivity_SearchView(this);
		mainActivity_SongView = new MainActivity_SongView(this);

		mainActivity_Menu = new MainActivity_Menu(this);
		mainActivity_Click = new MainActivity_Click(this);
	}

	protected void LoadSettings() {
		mainActivity_SearchView.SetSelectedDefaultTab(manager.settings.DefaultTabId());

		if (!mainActivity_SettingsView.minSymbolsForStartSearch.getText().toString().equalsIgnoreCase(manager.settings.MinSymbolsForStartSearch().toString())) {
			mainActivity_SettingsView.minSymbolsForStartSearch.setText(manager.settings.MinSymbolsForStartSearch().toString());
		}
		if (!mainActivity_SettingsView.maxSongsPerPageOnResult.getText().toString().equalsIgnoreCase(manager.settings.SongPerPage().toString())) {
			mainActivity_SettingsView.maxSongsPerPageOnResult.setText(manager.settings.SongPerPage().toString());
		}
		if (mainActivity_SettingsView.seachByAndShowSongNumbersInResult.isChecked() != manager.settings.SeachByAndShowSongNumbersInResult()) {
			mainActivity_SettingsView.seachByAndShowSongNumbersInResult.setChecked(manager.settings.SeachByAndShowSongNumbersInResult());
		}
		if (mainActivity_SettingsView.doMoreRelevantSearch.isChecked() != manager.settings.DoMoreRelevantSearch()) {
			mainActivity_SettingsView.doMoreRelevantSearch.setChecked(manager.settings.DoMoreRelevantSearch());
		}

		if (mainActivity_SettingsView.doNotTurnOffScreen.isChecked() != manager.settings.DoNotTurnOffScreen()) {
			mainActivity_SettingsView.doNotTurnOffScreen.setChecked(manager.settings.DoNotTurnOffScreen());
		}

		ToggleKeepScreenOn(manager.settings.DoNotTurnOffScreen());

		mainActivity_SongView.songContentEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, manager.settings.FontSize());

		mainActivity_SearchView.RefreshSearchTextHint();
	}

	private void InitTitle() {
		try {
			
			ActionBar actionBar = getSupportActionBar();
			Drawable actionBarDrawable = manager.context.getResources().getDrawable(R.drawable.bg_action_bar);
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		
		InitTitle();

		mainActivity_SettingsView.onCreate();
		mainActivity_SearchView.onCreate();
		mainActivity_SongView.onCreate();

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

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
		});

		LoadSettings();

		RestoreState();
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

	protected void RestoreState() {
		final AppState state = (AppState) ((Activity) manager.context).getLastNonConfigurationInstance();
		if (state != null) {
			mainActivity_SearchView.searchEditText.setText(state.Terms.SearchText());
			manager.viewPager.setCurrentItem(state.ViewIndex);
			if (state.IsSongEditMode) {
				mainActivity_SongView.SetSongEditMode();
			}
			mainActivity_SongView.UpdateContenSongView(state.Song);

			mainActivity_SearchView.CreateAdapterAndSetToSongList(state.Terms);
			mainActivity_SearchView.FocusedItemInSongsList(state.FirstVisibleSongPosition);
		} else {
			mainActivity_SearchView.LoadDefaultSongsListContent();
			mainActivity_SearchView.FocusedItemInSongsList(0);
		}
	}
}
