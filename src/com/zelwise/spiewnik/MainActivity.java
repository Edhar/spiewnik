package com.zelwise.spiewnik;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zelwise.spiewnik.AppManager;
import com.zelwise.spiewnik.Song.Names;
import com.zelwise.spiewnik.SongArrayAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	Button downloadButton, dropTablesButton, recentlyViewedButton,
			oftenViewedButton, siteRatingViewedButton, searcTextClearButton;

	EditText searchEditText, downloadFromEditText, downloadToEditText,
			maxSongsPerPageOnResult, songTitleEditText, songContentEditText;

	LinearLayout advanceLinearLayout;

	CheckBox advanceCheckBox,seachByAndShowSongNumbersInResult;

	ListView songsListView;

	private Boolean isSongEditMode = false;

	public Boolean IsSongEditMode() {
		return isSongEditMode;
	}
	
	private OnTouchListener onTouchListenerClear = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Button btn = (Button)v;
			if (event.getAction() == MotionEvent.ACTION_DOWN){
			    btn.setBackgroundColor(Color.parseColor("#C1E3FC"));
			}
			if (event.getAction() == MotionEvent.ACTION_UP){
				btn.setBackgroundColor(Color.parseColor("#00000000"));
				v.performClick();
			}
			return true;
		}
	};
	private OnTouchListener onTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Button btn = (Button)v;
			if (event.getAction() == MotionEvent.ACTION_DOWN){
			    btn.setBackgroundColor(Color.parseColor("#660099"));
			}
			if (event.getAction() == MotionEvent.ACTION_UP){
				btn.setBackgroundColor(Color.parseColor("#C1E3FC"));
				v.performClick();
			}
			return true;
		}
	};

	ViewPager viewPager;
	AppPagerAdapter pagerAdapter;

	SongArrayAdapter songsArrayAdapter;

	Integer settingsViewIndex = 0;
	Integer searchViewIndex = 1;
	Integer songViewIndex = 2;

	

	AppManager manager;

	private void LoadSettings() {
		maxSongsPerPageOnResult.setText(manager.settings.MaxSongInResultList()
				.toString());
		seachByAndShowSongNumbersInResult.setChecked(manager.settings.SeachByAndShowSongNumbersInResult());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = LayoutInflater.from(this);
		List<View> pages = new ArrayList<View>();

		View settingsView = inflater.inflate(R.layout.settings_view, null);
		settingsView.setTag(settingsViewIndex);
		pages.add(settingsView);

		View searchView = inflater.inflate(R.layout.search_view, null);
		searchView.setTag(searchViewIndex);
		pages.add(searchView);

		View songView = inflater.inflate(R.layout.song_view, null);
		songView.setTag(songViewIndex);
		pages.add(songView);

		pagerAdapter = new AppPagerAdapter(pages);
		viewPager = new ViewPager(this);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(searchViewIndex);
		setContentView(viewPager);

		manager = new AppManager(this, viewPager);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				CheckIfSongEditModeOrContentSaved();
				
				Activity activity = (Activity) manager.context;

				if (manager.viewPager.getCurrentItem() == settingsViewIndex) {
					activity.setTitle(getResources().getString(
							R.string.app_settings_name));
				} else if (manager.viewPager.getCurrentItem() == searchViewIndex) {
					activity.setTitle(getResources().getString(
							R.string.app_name));
					songsArrayAdapter.notifyDataSetChanged();
				} else if (manager.viewPager.getCurrentItem() == songViewIndex) {
					try {
						SetSongViewViewMode();
						EditText songTitle = (EditText) pagerAdapter.pages.get(
								songViewIndex).findViewById(
								R.id.SongTitleEditText);

						if (songTitle != null
								&& songTitle.getText().length() != 0) {
							activity.setTitle(songTitle.getText());
						} else {
							activity.setTitle(getResources().getString(
									R.string.app_name));
						}
						;
					} catch (Exception e) {
						activity.setTitle(getResources().getString(
								R.string.app_name));
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

		maxSongsPerPageOnResult = (EditText) settingsView
				.findViewById(R.id.MaxSongsPerPageOnResult);
		maxSongsPerPageOnResult.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable searchText) {
				manager.settings
				.MaxSongInResultList(maxSongsPerPageOnResult
						.getText().toString());
			}
		});

		downloadFromEditText = (EditText) settingsView
				.findViewById(R.id.DownloadFromEditText);
		downloadToEditText = (EditText) settingsView
				.findViewById(R.id.DownloadToEditText);
		downloadButton = (Button) settingsView
				.findViewById(R.id.DownloadButton);
		downloadButton.setOnClickListener(this);
		dropTablesButton = (Button) settingsView
				.findViewById(R.id.DropTablesButton);
		dropTablesButton.setOnClickListener(this);
		advanceLinearLayout = (LinearLayout) settingsView
				.findViewById(R.id.AdvanceLinearLayout);
		advanceCheckBox = (CheckBox) settingsView
				.findViewById(R.id.AdvanceCheckBox);
		advanceCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					advanceLinearLayout.setVisibility(View.VISIBLE);
					SetDropTableButtonText();
				} else {
					advanceLinearLayout.setVisibility(View.GONE);
				}
			}
		});
		seachByAndShowSongNumbersInResult = (CheckBox) settingsView
				.findViewById(R.id.SeachByAndShowSongNumbersInResult);
		seachByAndShowSongNumbersInResult.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.settings.SeachByAndShowSongNumbersInResult(((CheckBox) v).isChecked());
			}
		});

		siteRatingViewedButton = (Button) searchView
				.findViewById(R.id.SiteRatingViewedButton);
		siteRatingViewedButton.setOnClickListener(this);
		recentlyViewedButton = (Button) searchView
				.findViewById(R.id.RecentlyViewedButton);
		recentlyViewedButton.setOnClickListener(this);
		oftenViewedButton = (Button) searchView
				.findViewById(R.id.OftenViewedButton);
		oftenViewedButton.setOnClickListener(this);
		searcTextClearButton = (Button) searchView
				.findViewById(R.id.SearcTextClearButton);
		searcTextClearButton.setOnClickListener(this);

		songTitleEditText = (EditText) songView
				.findViewById(R.id.SongTitleEditText);
		songContentEditText = (EditText) songView
				.findViewById(R.id.SongContentEditText);

		songsListView = (ListView) searchView.findViewById(R.id.SongsListView);
		songsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View view, int position,
					long id) {
				viewPager.setCurrentItem(songViewIndex);

				manager.HideKeyboard();
				SetSongViewViewMode();

				Song curSong = (Song) (songsListView
						.getItemAtPosition(position));

				// TODO update adapter
				curSong = Song.Get(manager.db, curSong.Id());

				UpdateContenSongView(curSong);

				curSong.RecentlyViewedDate(new Date());
				curSong.Rating(curSong.Rating() + 1);
				curSong.SaveOrUpdate(manager.db);
			}
		});
		songsListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				manager.HideKeyboard();
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// manager.HideKeyboard();
			}
		});
		songsListView.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					manager.HideKeyboard();
				}
			}
		});

		registerForContextMenu(songsListView);

		searchEditText = (EditText) searchView
				.findViewById(R.id.SearchEditText);
		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable searchText) {
				ToggleClearSearchButton(true);
				if (searchText.toString().trim().length() >= manager.settings.MinSymbolsForStartSearch) {
					ArrayList<Song> songs = Song.GetSongs(manager.db,
							searchText.toString().trim(),
							manager.settings.MaxSongInResultList(), "",manager.settings.SeachByAndShowSongNumbersInResult());
					CreateAdapterAndSetToSongList(songs);
				}
			}
		});
		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					manager.ShowKeyboard();
				} else {
					manager.HideKeyboard();
				}
			}
		});
		
		recentlyViewedButton.setOnTouchListener(onTouchListener);
		oftenViewedButton.setOnTouchListener(onTouchListener);
		siteRatingViewedButton.setOnTouchListener(onTouchListener);
		searcTextClearButton.setOnTouchListener(onTouchListenerClear);
		
		LoadSettings();
		LoadRecentlyViewed();
		FocusedFirstSongsListViewItem();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.SongsListView) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(((Song) songsListView
					.getItemAtPosition(info.position)).Title());
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.song_item_context_menu, menu);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.DownloadButton:
			String url = "http://www.spiewnik.com/wyswietl.php?numer=";
			Integer startSiteId = Utils.ToInt(downloadFromEditText.getText()
					.toString(), 1);
			Integer endSiteId = Utils.ToInt(downloadToEditText.getText()
					.toString(), 15000);
			if (endSiteId < startSiteId) {
				Toast.makeText(manager.context,
						"'To' must be higher then 'From'", Toast.LENGTH_SHORT)
						.show();
			}
			manager.HideKeyboard();
			manager.DownloadAndSaveContent(url, startSiteId, endSiteId);
			break;
		case R.id.DropTablesButton:
			manager.dBHelper.DropTable(manager.db, Song.Names.TableName);
			SetDropTableButtonText();
			manager.HideKeyboard();
			// Toast.makeText(manager.context, "Deleted table - " +
			// Song.Names.TableName, Toast.LENGTH_SHORT).show();
			// manager.dBHelper.DropTables(manager.db,
			// Category.Names.TableName);
			// Toast.makeText(this, "Deleted table - " +
			// Category.Names.TableName, Toast.LENGTH_SHORT).show();
			break;
		case R.id.RecentlyViewedButton:
			LoadRecentlyViewed();
			break;
		case R.id.OftenViewedButton:
			LoadOftenViewed();
			break;
		case R.id.SiteRatingViewedButton:
			LoadSiteRatingViewed();
			break;
		case R.id.SearcTextClearButton:
			searchEditText.setText("");
			ToggleClearSearchButton(false);
			LoadRecentlyViewed();
			break;
		}
	}

	private void ShowSongProperties(Song song) {
		String content = Song.Names.Id + ": " + song.Id() + Utils.NewLine
				+ Song.Names.SiteId + ": " + song.SiteId() + Utils.NewLine
				+ Song.Names.Rating + ": " + song.Rating() + Utils.NewLine
				+ Song.Names.SiteRating + ": " + song.SiteRating()
				+ Utils.NewLine + Song.Names.RecentlyViewedDate + ": "
				+ Utils.iso8601Format.format(song.RecentlyViewedDate());

		// Toast.makeText(manager.context, content, Toast.LENGTH_LONG).show();

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
				manager.context);
		alertBuilder.setIcon(0);
		alertBuilder.setTitle(song.Title());

		alertBuilder.setMessage(content).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert = alertBuilder.create();
		alert.show();
	}

	public void ShowInFutureToast() {
		Toast.makeText(manager.context, "Only In Future", Toast.LENGTH_SHORT)
				.show();
	}

	private void SetDropTableButtonText() {
		dropTablesButton.setText(getResources().getString(
				R.string.settings_dropTablesButton)
				+ Song.AllSongsCount(manager.db)
				+ " ("
				+ getResources().getString(R.string.settings_totalRecods)
				+ ":"
				+ Song.AllSongsCountWithEmpty(manager.db) + ")");
	}

	private void FocusedFirstSongsListViewItem() {
		songsListView.setSelection(0);
		songsListView.requestFocus();
	}

	private void CreateAdapterAndSetToSongList(ArrayList<Song> songs) {
		if (songs.size() > 0) {
			songsArrayAdapter = new SongArrayAdapter<Song>(
					manager, R.layout.song_list_item, songs);
			songsListView.setAdapter(songsArrayAdapter);
		} else {
			songsListView.setAdapter(null);
			Toast.makeText(manager.context, "Not found songs",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void LoadOftenViewed() {
		ArrayList<Song> songs = Song.GetSongs(manager.db, "",
				manager.settings.MaxSongInResultList(), Names.Rating
						+ DBHelper.SortDescending,manager.settings.SeachByAndShowSongNumbersInResult());
		CreateAdapterAndSetToSongList(songs);
	}

	private void LoadSiteRatingViewed() {
		ArrayList<Song> songs = Song.GetSongs(manager.db, "",
				manager.settings.MaxSongInResultList(), Names.SiteRating
						+ DBHelper.SortDescending,manager.settings.SeachByAndShowSongNumbersInResult());
		CreateAdapterAndSetToSongList(songs);
	}

	private void LoadRecentlyViewed() {
		ArrayList<Song> songs = Song.GetSongs(manager.db, "",
				manager.settings.MaxSongInResultList(),
				Names.RecentlyViewedDate + DBHelper.SortDescending,manager.settings.SeachByAndShowSongNumbersInResult());
		CreateAdapterAndSetToSongList(songs);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.app_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.setGroupVisible(R.id.menu_EditGroup,
				manager.viewPager.getCurrentItem() == songViewIndex);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menu_settings:
			viewPager.setCurrentItem(settingsViewIndex);
			return true;
		case R.id.menu_Exit:
			finish();
			System.exit(0);
			return true;
		case R.id.menu_Add:
			AddNewSongSongView();
			return true;
		case R.id.menu_Edit:
			SetSongEditMode();
			return true;
		case R.id.menu_Save:
			Song newSong = GetSongFromSongView();
			newSong.SaveOrUpdate(manager.db);
			UpdateContenSongView(newSong);
			SetSongViewViewMode();
			return true;
		case R.id.menu_Properties:
			ShowSongProperties(GetSongFromSongView());
			return true;
		case R.id.menu_Delete:
			DeleteMenuAction(GetSongFromSongView());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void ToggleClearSearchButton(Boolean show) {
		if (show) {
			searcTextClearButton.setVisibility(View.VISIBLE);
		} else {
			searcTextClearButton.setVisibility(View.GONE);
		}
	}

	private void DeleteMenuAction(Song song) {
		(song).Delete(manager.db);
		manager.viewPager.setCurrentItem(searchViewIndex);
		LoadRecentlyViewed();
	}

	/*
	 * private static final long DOUBLE_PRESS_INTERVAL = some value in ns.;
	 * private long lastPressTime;
	 * 
	 * @Override public void onBackPressed() { long pressTime =
	 * System.nanoTime(); if(pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL)
	 * { // this is a double click event } lastPressTime = pressTime; }
	 */

	@Override
	public void onBackPressed() {
		if (manager.viewPager.getCurrentItem() == settingsViewIndex) {
			manager.viewPager.setCurrentItem(searchViewIndex);
		} else if (manager.viewPager.getCurrentItem() == searchViewIndex) {
			moveTaskToBack(true);
		} else if (manager.viewPager.getCurrentItem() == songViewIndex) {
			manager.viewPager.setCurrentItem(searchViewIndex);
		}

		manager.HideKeyboard();

	}

	private void CheckIfSongEditModeOrContentSaved()
	{
		if(this.IsSongEditMode()){
			Song curSong = GetSongFromSongView();
			
			if(curSong.Title().trim().length() != 0 || curSong.Content().trim().length() != 0){
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(manager.context);
				
				alertBuilder.setIcon(0);
				alertBuilder.setTitle("Would you like to save this song:");
				alertBuilder.setMessage(curSong.Title());
				alertBuilder.setCancelable(false);
				alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								GetSongFromSongView().SaveOrUpdate(manager.db);
								isSongEditMode = false;
							}
						});
				alertBuilder.setCancelable(false);
				alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						isSongEditMode = false;
						dialog.cancel();
					}
				});
				AlertDialog alert = alertBuilder.show();
				TextView messageText = (TextView)alert.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				messageText.setTypeface(Typeface.DEFAULT_BOLD);
				alert.show();
			}
		}
	}

	private Song GetSongFromSongView() {
		Song song = new Song();
		
		try {
			View songView = manager.viewPager.findViewWithTag(songViewIndex);
			TextView songId = (TextView) songView
					.findViewById(R.id.SongIdTextView);
			Integer id = Utils.ToInt(songId.getText().toString(), 0);
			if (id != 0) {
				song = Song.Get(manager.db, id);
			}
		} catch (Exception e) {
		}

		return UpdateSongFromEditMode(song);
	}

	private void AddNewSongSongView() {
		View songView = manager.viewPager.findViewWithTag(songViewIndex);
		if (manager.viewPager.getCurrentItem() != songViewIndex) {
			manager.viewPager.setCurrentItem(songViewIndex);
		}

		UpdateContenSongView(new Song());
		SetSongEditMode();
	}

	private Song UpdateSongFromEditMode(Song originalSong) {
		try {
			View songView = manager.viewPager.findViewWithTag(songViewIndex);

			EditText newTitle = (EditText) songView
					.findViewById(R.id.SongTitleEditText);
			EditText newContent = (EditText) songView
					.findViewById(R.id.SongContentEditText);

			originalSong.Title(newTitle.getText().toString());
			originalSong.Content(newContent.getText().toString());
		} catch (Exception e) {

		}

		return originalSong;
	}

	private void UpdateContenSongView(Song newSong) {
		try {
			View songView = manager.viewPager.findViewWithTag(songViewIndex);

			TextView newId = (TextView) songView
					.findViewById(R.id.SongIdTextView);
			EditText newTitle = (EditText) songView
					.findViewById(R.id.SongTitleEditText);
			EditText newContent = (EditText) songView
					.findViewById(R.id.SongContentEditText);

			newId.setText(newSong.Id().toString());
			newTitle.setText(newSong.Title());
			newContent.setText(newSong.Content());

			Activity activity = (Activity) manager.context;

			if (manager.viewPager.getCurrentItem() == songViewIndex) {
				try {
					if (newSong.Title().length() != 0) {
						activity.setTitle(newSong.Title());
					} else {
						activity.setTitle(getResources().getString(
								R.string.app_name));
					}
					;
				} catch (Exception e) {
					activity.setTitle(getResources().getString(
							R.string.app_name));
				}
			}

		} catch (Exception e) {

		}

	}

	private void SetSongEditMode() {
		songTitleEditText.setVisibility(View.VISIBLE);
		songTitleEditText.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		songTitleEditText.setCursorVisible(true);
		songTitleEditText.setInputType(InputType.TYPE_CLASS_TEXT);

		songContentEditText.setCursorVisible(true);
		songContentEditText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_MULTI_LINE);

		isSongEditMode = true;

	}

	private void SetSongViewViewMode() {
		View songView = manager.viewPager.findViewWithTag(songViewIndex);
		ScrollView songScroll = (ScrollView) songView
				.findViewById(R.id.SongContentScrollView);
		songScroll.scrollTo(0, 0);

		songTitleEditText.setVisibility(View.GONE);
		songTitleEditText.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, 0));
		songTitleEditText.setCursorVisible(false);
		songTitleEditText.setInputType(InputType.TYPE_NULL);

		songContentEditText.setCursorVisible(false);
		songContentEditText.setInputType(InputType.TYPE_NULL);
		songContentEditText.setSingleLine(false);

		isSongEditMode = false;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		Song curSong = (Song) songsListView.getItemAtPosition(info.position);
		switch (item.getItemId()) {
		case R.id.menu_Edit_Context:
			viewPager.setCurrentItem(songViewIndex);
			UpdateContenSongView(curSong);
			SetSongEditMode();
			return true;
		case R.id.menu_Properties_Context:
			ShowSongProperties(curSong);
			return true;
		case R.id.menu_Delete_Context:
			DeleteMenuAction(curSong);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		manager.CloseDB();
		super.onDestroy();
	}
}
