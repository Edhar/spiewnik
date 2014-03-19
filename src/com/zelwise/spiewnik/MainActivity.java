package com.zelwise.spiewnik;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zelwise.spiewnik.AppManager;
import com.zelwise.spiewnik.Song.Names;
import com.zelwise.spiewnik.R;
import com.zelwise.spiewnik.SongArrayAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	Button downloadButton, dropTablesButton, recentlyViewedButton,
			oftenViewedButton, siteRatingViewedButton, searcTextClearButton,
			favoriteButton;

	EditText searchEditText, downloadFromEditText, downloadToEditText,
			maxSongsPerPageOnResult, minSymbolsForStartSearch,
			songTitleEditText, songContentEditText;

	LinearLayout advanceLinearLayout, magnifierLinearLayout;

	Spinner byDefaultResultsForTab;

	CheckBox advanceCheckBox, seachByAndShowSongNumbersInResult,
			doMoreRelevantSearch,doNotTurnOffScreen;

	ListView songsListView;

	private Boolean isSongEditMode = false;

	public Boolean IsSongEditMode() {
		return isSongEditMode;
	}

	private OnTouchListener onTouchListenerClear = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Button btn = (Button) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				btn.setBackgroundColor(manager.context.getResources().getColor(
						R.color.theme_violet_bg_PressedClearButton));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				btn.setBackgroundColor(manager.context.getResources().getColor(
						R.color.theme_TransparentColor));
				v.performClick();
			}
			return true;
		}
	};
	private OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Button btn = (Button) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				btn.setBackgroundColor(manager.context.getResources().getColor(
						R.color.theme_violet_bg_PressedButton));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				btn.setBackgroundColor(manager.context.getResources().getColor(
						R.color.theme_violet_bg_TabsItem));
				v.performClick();
			}
			return true;
		}
	};

	private OnTouchListener onTouchListenerMagnifier = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView btn = (ImageView) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				btn.setBackgroundColor(manager.context.getResources().getColor(
						R.color.theme_violet_bg_PressedButton));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				btn.setBackgroundColor(manager.context.getResources().getColor(
						R.color.theme_TransparentColor));
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

	private void SetSelectedDefaultTab(Integer tabId) {
		ArrayAdapter<TabsItem> tabAdapter = (ArrayAdapter<TabsItem>) byDefaultResultsForTab.getAdapter();
		if (tabAdapter != null) {
			TabsItem curTab = (TabsItem) byDefaultResultsForTab
					.getItemAtPosition(byDefaultResultsForTab
							.getSelectedItemPosition());
			TabsItem newTab = manager.tabsList.Get(tabId);
			for (int i = 0; i < tabAdapter.getCount(); i++) {
				if (((TabsItem) tabAdapter.getItem(i)).Id() == newTab.Id()) {
					if (curTab.Id() != newTab.Id()) {
						byDefaultResultsForTab.setSelection(i);
					}
					break;
				}
			}
		}
	}

	Handler magnifierHandler = new Handler();
	private Runnable updateMagnifierState = new Runnable() {
		public void run() {
			long curTime = new Date().getTime();
			if (curTime - lastMagnifierShowTime > SettingsHelper.DefaultValues.MagnifiedShowTime) {
				magnifierLinearLayout.setVisibility(View.GONE);
			} else {
				magnifierHandler.postDelayed(updateMagnifierState,
						SettingsHelper.DefaultValues.MagnifiedShowTime);
			}

		}
	};
	private long lastMagnifierShowTime;

	private void ShowMagnifier() {
		magnifierLinearLayout.setVisibility(View.VISIBLE);
		magnifierHandler.postDelayed(updateMagnifierState,
				SettingsHelper.DefaultValues.MagnifiedShowTime);

		lastMagnifierShowTime = new Date().getTime();
	}

	Handler startSearchHandler = new Handler();
	private Runnable runSearchRunnable = new Runnable() {
		public void run() {
			long curTime = new Date().getTime();
			if (curTime - lastStartSearchTime > SettingsHelper.DefaultValues.StartSearchDelay) {
				StartSearchImmediately();
			} else {
				startSearchHandler.postDelayed(runSearchRunnable,SettingsHelper.DefaultValues.StartSearchDelay);
			}

		}
	};
	private long lastStartSearchTime;
	
	private void StartSearchImmediately(){
		SearchTerms terms = new SearchTerms(manager.settings,SearchBy.Text, searchEditText.getText().toString(), "");
		CreateAdapterAndSetToSongList(terms);
	}

	private void StartSearchWithDelay() {
		startSearchHandler.postDelayed(runSearchRunnable,SettingsHelper.DefaultValues.StartSearchDelay);
		lastStartSearchTime = new Date().getTime();
	}

	private void LoadSettings() {
		SetSelectedDefaultTab(manager.settings.DefaultTabId());

		if (!minSymbolsForStartSearch
				.getText()
				.toString()
				.equalsIgnoreCase(
						manager.settings.MinSymbolsForStartSearch().toString())) {
			minSymbolsForStartSearch.setText(manager.settings
					.MinSymbolsForStartSearch().toString());
		}
		if (!maxSongsPerPageOnResult.getText().toString()
				.equalsIgnoreCase(manager.settings.SongPerPage().toString())) {
			maxSongsPerPageOnResult.setText(manager.settings.SongPerPage()
					.toString());
		}
		if (seachByAndShowSongNumbersInResult.isChecked() != manager.settings
				.SeachByAndShowSongNumbersInResult()) {
			seachByAndShowSongNumbersInResult.setChecked(manager.settings
					.SeachByAndShowSongNumbersInResult());
		}
		if (doMoreRelevantSearch.isChecked() != manager.settings
				.DoMoreRelevantSearch()) {
			doMoreRelevantSearch.setChecked(manager.settings
					.DoMoreRelevantSearch());
		}
		
		if (doNotTurnOffScreen.isChecked() != manager.settings.DoNotTurnOffScreen()) {
			doNotTurnOffScreen.setChecked(manager.settings.DoNotTurnOffScreen());
		}
		
		ToggleKeepScreenOn(manager.settings.DoNotTurnOffScreen());

		songContentEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				manager.settings.FontSize());

		RefreshSearchTextHint();
	}
	
	private void ToggleKeepScreenOn(Boolean keepScreenOn){
		if(keepScreenOn){
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}else{
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	// to fix double event
	String searchEditTextPrevValue = "";

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
							R.string.settings_settingsName));
				} else if (manager.viewPager.getCurrentItem() == searchViewIndex) {
					activity.setTitle(getResources().getString(
							R.string.app_name));
				} else if (manager.viewPager.getCurrentItem() == songViewIndex) {
					try {
						SetSongViewViewMode();
						EditText songTitle = (EditText) ((AppPagerAdapter) manager.viewPager
								.getAdapter()).pages.get(songViewIndex)
								.findViewById(R.id.SongTitleEditText);

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
		byDefaultResultsForTab = (Spinner) settingsView.findViewById(R.id.ByDefaultResultsForTab);
		ArrayAdapter<TabsItem> adapterDefaultTab = new ArrayAdapter<TabsItem>(
				this, android.R.layout.simple_spinner_item,
				manager.tabsList.GetTabsItems());
		adapterDefaultTab
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		byDefaultResultsForTab.setAdapter(adapterDefaultTab);
		byDefaultResultsForTab
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						if (parent != null) {
							TabsItem newTab = (TabsItem) parent
									.getItemAtPosition(pos);
							if (newTab != null) {
								manager.settings.DefaultTabId(newTab.Id());
							}
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

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
				Integer newVal = Utils.ToInt(searchText.toString(),
						SettingsHelper.DefaultValues.SongPerPage);
				if (newVal > 0) {
					manager.settings.SongPerPage(newVal);
				} else {
					manager.settings
							.SongPerPage(SettingsHelper.DefaultValues.SongPerPage);
					maxSongsPerPageOnResult
							.setText(SettingsHelper.DefaultValues.SongPerPage
									.toString());
				}

			}
		});

		minSymbolsForStartSearch = (EditText) settingsView
				.findViewById(R.id.MinSymbolsForStartSearch);
		minSymbolsForStartSearch.addTextChangedListener(new TextWatcher() {

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
				Integer newVal = Utils.ToInt(searchText.toString(),
						SettingsHelper.DefaultValues.MinSymbolsForStartSearch);
				if (newVal > 0) {
					manager.settings.MinSymbolsForStartSearch(newVal);
				} else {
					manager.settings
							.MinSymbolsForStartSearch(SettingsHelper.DefaultValues.MinSymbolsForStartSearch);
					minSymbolsForStartSearch
							.setText(SettingsHelper.DefaultValues.MinSymbolsForStartSearch
									.toString());
				}
				RefreshSearchTextHint();
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
		seachByAndShowSongNumbersInResult
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						manager.settings
								.SeachByAndShowSongNumbersInResult(((CheckBox) v)
										.isChecked());
						songsArrayAdapter.notifyDataSetChanged();
					}
				});

		doMoreRelevantSearch = (CheckBox) settingsView.findViewById(R.id.DoMoreRelevantSearch);
		doMoreRelevantSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.settings.DoMoreRelevantSearch(((CheckBox) v).isChecked());
			}
		});
		
		doNotTurnOffScreen = (CheckBox) settingsView.findViewById(R.id.DoNotTurnOffScreen);
		doNotTurnOffScreen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean keepScreenOn = ((CheckBox) v).isChecked();
				manager.settings.DoNotTurnOffScreen(keepScreenOn);
				ToggleKeepScreenOn(keepScreenOn);
			}
		});

		siteRatingViewedButton = (Button) searchView
				.findViewById(R.id.SiteRatingViewedButton);
		siteRatingViewedButton.setOnClickListener(this);

		favoriteButton = (Button) searchView.findViewById(R.id.FavoriteButton);
		favoriteButton.setOnClickListener(this);

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
		songContentEditText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!isSongEditMode) {
					ShowMagnifier();
				}
			}
		});
		ScrollView songContentScrollView = (ScrollView) songView
				.findViewById(R.id.SongContentScrollView);
		songContentScrollView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (!isSongEditMode) {
						ShowMagnifier();
					}
				}
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return false;
				}

				return true;
			}
		});

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
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				manager.HideKeyboard();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItemIndex,
					int visibleItemCountOnDisplay, int totalItemCount) {
				final int hidenSongsCount = 2;
				if (totalItemCount > 0
						&& (totalItemCount - firstVisibleItemIndex - visibleItemCountOnDisplay) < hidenSongsCount) {
					AddDynamicallyDataToList();
				}
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
			public void onTextChanged(CharSequence newStr, int start,
					int before, int count) {

				if (!newStr.toString()
						.equalsIgnoreCase(searchEditTextPrevValue)
						&& newStr.length() >= manager.settings
								.MinSymbolsForStartSearch()) {
					ShowHideClearSearchButton(true);
					
					if(manager.settings.DoMoreRelevantSearch()){
						StartSearchWithDelay();
					}else{
						StartSearchImmediately();
					}
					
					searchEditTextPrevValue = newStr.toString();
				}
				if (newStr.toString().length() == 0) {
					ShowHideClearSearchButton(false);
					LoadDefaultSongsListContent();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable searchText) {

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
		favoriteButton.setOnTouchListener(onTouchListener);
		searcTextClearButton.setOnTouchListener(onTouchListenerClear);

		TextView version = (TextView) settingsView.findViewById(R.id.Version);
		String versionName = "";
		try {
			versionName = manager.context.getPackageManager().getPackageInfo(
					manager.context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		version.setText(manager.context.getResources().getString(
				R.string.labels_Version)
				+ versionName);

		ImageView magnifierMinus = (ImageView) songView
				.findViewById(R.id.MagnifierMinusImageView);
		ImageView magnifierPlus = (ImageView) songView
				.findViewById(R.id.MagnifierPlusImageView);
		magnifierMinus.setOnTouchListener(onTouchListenerMagnifier);
		magnifierPlus.setOnTouchListener(onTouchListenerMagnifier);
		magnifierMinus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChangeFontSize(false);
				ShowMagnifier();
			}
		});
		magnifierPlus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChangeFontSize(true);
				ShowMagnifier();
			}
		});
		magnifierLinearLayout = (LinearLayout) songView
				.findViewById(R.id.MagnifierLinearLayout);

		LoadSettings();

		RestoreState();
	}

	private void ChangeFontSize(Boolean isEnlarge) {
		float currentSize = Utils.pixelsToSp(manager.context,
				songContentEditText.getTextSize());
		float newSize = currentSize;
		if (isEnlarge) {
			newSize += SettingsHelper.DefaultValues.FontSizeMagnifierStep;
			if (newSize > SettingsHelper.DefaultValues.FontSizeMax()) {
				newSize = SettingsHelper.DefaultValues.FontSizeMax();
			}
		} else {
			newSize -= SettingsHelper.DefaultValues.FontSizeMagnifierStep;
			if (newSize < SettingsHelper.DefaultValues.FontSizeMin()) {
				newSize = SettingsHelper.DefaultValues.FontSizeMin();
			}
		}

		final Toast toast = Toast.makeText(manager.context, newSize
				/ SettingsHelper.DefaultValues.FontSize + "x",
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				toast.cancel();
			}
		}, 300);

		manager.settings.FontSize(newSize);
		songContentEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
	}

	private void RefreshSearchTextHint() {
		String searchHint = "%s";
		switch (manager.settings.MinSymbolsForStartSearch()) {
		case 1:
			searchHint = manager.context.getResources().getString(
					R.string.search_searchHintStartText1);
			break;
		case 2:
		case 3:
		case 4:
			searchHint = manager.context.getResources().getString(
					R.string.search_searchHintStartText2to4);
			break;
		default:
			searchHint = manager.context.getResources().getString(
					R.string.search_searchHintStartText5);
			break;
		}
		searchEditText.setHint(String.format(searchHint,
				manager.settings.MinSymbolsForStartSearch()));
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
			DBHelper.DropTable(manager.db, Song.Names.TableName);
			DBHelper.CreateTable(manager.db, Song.Names.TableName);
			SetDropTableButtonText();
			manager.HideKeyboard();
			break;
		case R.id.RecentlyViewedButton:
			ClearSearchText();
			LoadRecentlyViewed();
			break;
		case R.id.OftenViewedButton:
			ClearSearchText();
			LoadOftenViewed();
			break;
		case R.id.SiteRatingViewedButton:
			ClearSearchText();
			LoadSiteRatingViewed();
			break;
		case R.id.FavoriteButton:
			ClearSearchText();
			LoadFavorite();
			break;
		case R.id.SearcTextClearButton:
			ClearSearchText();
			ShowHideClearSearchButton(false);
			LoadDefaultSongsListContent();
			break;
		}
	}

	private void ShowSongProperties(Song song) {
		String content = manager.context.getResources().getString(
				R.string.labels_Id)
				+ song.Id()
				+ Utils.NewLine
				// +
				// manager.context.getResources().getString(R.string.labels_SiteId)
				// + song.SiteId() + Utils.NewLine
				+ manager.context.getResources().getString(
						R.string.labels_Rating)
				+ song.Rating()
				+ Utils.NewLine
				+ manager.context.getResources().getString(
						R.string.labels_SiteRating)
				+ song.SiteRating()
				+ Utils.NewLine
				+ manager.context.getResources().getString(
						R.string.labels_Favorite)
				+ (song.Favorite() ? manager.context.getResources().getString(
						R.string.buttons_Yes) : manager.context.getResources()
						.getString(R.string.buttons_No))
				+ Utils.NewLine
				+ manager.context.getResources().getString(
						R.string.labels_RecentlyViewedDate)
				+ Utils.iso8601Format.format(song.RecentlyViewedDate());

		// Toast.makeText(manager.context, content, Toast.LENGTH_LONG).show();

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
				manager.context);
		alertBuilder.setIcon(0);
		alertBuilder.setTitle(song.Title());

		alertBuilder
				.setMessage(content)
				.setCancelable(false)
				.setPositiveButton(
						manager.context.getResources().getString(
								R.string.buttons_Yes),
						new DialogInterface.OnClickListener() {
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

	private void FocusedItemInSongsList(int itemIndex) {
		songsListView.setSelection(itemIndex);
		songsListView.requestFocus();
	}

	private void AddDynamicallyDataToList() {
		SongArrayAdapter listAdapter = (SongArrayAdapter) songsListView
				.getAdapter();
		if (listAdapter != null && listAdapter.HasNextPage()) {
			listAdapter.AddAdditionalPage();
		}
	}

	private void CreateAdapterAndSetToSongList(SearchTerms terms) {
		songsArrayAdapter = new SongArrayAdapter(manager, terms);
		if (songsArrayAdapter.HasSongs()) {
			songsListView.setAdapter(songsArrayAdapter);
		} else {
			songsListView.setAdapter(null);
			Toast toast = Toast.makeText(manager.context, manager.context
					.getResources().getString(R.string.search_NothingFound),
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 50);
			toast.show();
		}
	}

	private void ClearSearchText() {
		searchEditText.setText("");
	}

	private void LoadOftenViewed() {
		SearchTerms terms = new SearchTerms(manager.settings, SearchBy.Rating,
				"", Names.Rating + DBHelper.SortDescending);
		CreateAdapterAndSetToSongList(terms);
	}

	private void LoadSiteRatingViewed() {
		SearchTerms terms = new SearchTerms(manager.settings,
				SearchBy.SiteRating, "", Names.SiteRating
						+ DBHelper.SortDescending);
		CreateAdapterAndSetToSongList(terms);
	}

	private void LoadRecentlyViewed() {
		SearchTerms terms = new SearchTerms(manager.settings,
				SearchBy.RecentlyViewedDate, "", Names.RecentlyViewedDate
						+ DBHelper.SortDescending);
		CreateAdapterAndSetToSongList(terms);
	}

	private void LoadFavorite() {
		SearchTerms terms = new SearchTerms(manager.settings,
				SearchBy.Favorite, "", Names.Favorite + DBHelper.SortDescending);
		CreateAdapterAndSetToSongList(terms);
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

	private void ShowHideClearSearchButton(Boolean show) {
		if (searcTextClearButton.getVisibility() != View.VISIBLE && show) {
			searcTextClearButton.setVisibility(View.VISIBLE);
		} else if (searcTextClearButton.getVisibility() != View.GONE && !show) {
			searcTextClearButton.setVisibility(View.GONE);
		}
	}

	private void LoadDefaultSongsListContent() {
		int tabId = manager.settings.DefaultTabId();

		if (tabId == manager.tabsList.SiteRating.Id()) {
			LoadSiteRatingViewed();
		} else if (tabId == manager.tabsList.Often.Id()) {
			LoadOftenViewed();
		} else if (tabId == manager.tabsList.Recent.Id()) {
			LoadRecentlyViewed();
		} else if (tabId == manager.tabsList.Favorite.Id()) {
			LoadFavorite();
		}
	}

	private void ReloadSongsListContent() {
		SongArrayAdapter listAdapter = (SongArrayAdapter) songsListView
				.getAdapter();
		if (listAdapter != null) {
			listAdapter.Reload();
			FocusedItemInSongsList(songsListView.getFirstVisiblePosition());
		}
	}

	private void DeleteSongMenuAction(Song song) {
		final Song songToDelete = song;

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
				manager.context);

		alertBuilder.setIcon(0);
		alertBuilder.setTitle(manager.context.getResources().getString(
				R.string.labels_AreYouSureYouWantToDeleteThisSong));
		alertBuilder.setMessage("\"" + song.Title() + "\"");
		alertBuilder.setCancelable(false);
		alertBuilder.setPositiveButton(manager.context.getResources()
				.getString(R.string.buttons_Yes),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						songToDelete.Delete(manager.db);
						if (manager.viewPager.getCurrentItem() == songViewIndex) {
							manager.viewPager.setCurrentItem(searchViewIndex);
						}
						isSongEditMode = false;
						ReloadSongsListContent();
					}
				});
		alertBuilder.setCancelable(false);
		alertBuilder.setNegativeButton(manager.context.getResources()
				.getString(R.string.buttons_No),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertBuilder.show();
		TextView messageText = (TextView) alert
				.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
		messageText.setTypeface(Typeface.DEFAULT_BOLD);
		messageText.setTextColor(Color.RED);
		alert.show();

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

	private void CheckIfSongEditModeOrContentSaved() {
		if (this.IsSongEditMode()) {
			final Song curSong = GetSongFromSongView();

			if (curSong.Title().length() != 0
					|| curSong.Content().length() != 0) {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
						manager.context);

				alertBuilder.setIcon(0);
				alertBuilder.setTitle(manager.context.getResources().getString(
						R.string.labels_WouldYouLikeToSaveThisSong));
				alertBuilder.setMessage("\"" + curSong.Title() + "\"");
				alertBuilder.setCancelable(false);
				alertBuilder.setPositiveButton(manager.context.getResources()
						.getString(R.string.buttons_Yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								curSong.SaveOrUpdate(manager.db);
								isSongEditMode = false;
								UpdateContenSongView(curSong);
								SetSongViewViewMode();
								ReloadSongsListContent();
							}
						});
				alertBuilder.setCancelable(false);
				alertBuilder.setNegativeButton(manager.context.getResources()
						.getString(R.string.buttons_No),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (curSong.Id() != 0) {
									Song savedSong = Song.Get(manager.db,
											curSong.Id());
									UpdateContenSongView(savedSong);
								}
								isSongEditMode = false;
								SetSongViewViewMode();
								dialog.cancel();
							}
						});
				AlertDialog alert = alertBuilder.show();
				TextView messageText = (TextView) alert
						.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				messageText.setTypeface(Typeface.DEFAULT_BOLD);
				alert.show();
			}
		}
	}

	private Song GetSongFromSongView() {
		Song song = new Song();

		try {
			View songView = ((AppPagerAdapter) manager.viewPager.getAdapter()).pages
					.get(songViewIndex);
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

	private void AddNewSongMenuAction() {
		if (manager.viewPager.getCurrentItem() != songViewIndex) {
			manager.viewPager.setCurrentItem(songViewIndex);
		}

		UpdateContenSongView(new Song());
		SetSongEditMode();
	}

	private Song UpdateSongFromEditMode(Song originalSong) {
		try {
			if (IsSongEditMode()) {
				View songView = ((AppPagerAdapter) manager.viewPager
						.getAdapter()).pages.get(songViewIndex);

				EditText newTitle = (EditText) songView
						.findViewById(R.id.SongTitleEditText);
				EditText newContent = (EditText) songView
						.findViewById(R.id.SongContentEditText);

				String content = newContent.getText().toString().trim();
				String title = newTitle.getText().toString().trim();
				if (title.length() == 0) {
					title = Utils.Trim(content);
				}

				originalSong.Title(title);
				originalSong.Content(content);
			}
		} catch (Exception e) {

		}

		return originalSong;
	}

	private void UpdateContenSongView(Song newSong) {
		try {
			View songView = ((AppPagerAdapter) manager.viewPager.getAdapter()).pages
					.get(songViewIndex);

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
		songTitleEditText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_MULTI_LINE);

		songContentEditText.setCursorVisible(true);
		songContentEditText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		songContentEditText.setSingleLine(false);

		songContentEditText.setEnabled(true);

		isSongEditMode = true;

	}

	private void SetSongViewViewMode() {
		View songView = ((AppPagerAdapter) manager.viewPager.getAdapter()).pages
				.get(songViewIndex);
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

		songContentEditText.setEnabled(true);

		isSongEditMode = false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			viewPager.setCurrentItem(settingsViewIndex);
			return true;
		case R.id.menu_Exit:
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			System.exit(0);
			return true;
		case R.id.menu_Add:
			AddNewSongMenuAction();
			return true;
		case R.id.menu_Edit:
			SetSongEditMode();
			return true;
		case R.id.menu_Save:
			Song newSong = GetSongFromSongView();
			newSong.SaveOrUpdate(manager.db);
			UpdateContenSongView(newSong);
			SetSongViewViewMode();
			ReloadSongsListContent();
			return true;
		case R.id.menu_Properties:
			ShowSongProperties(GetSongFromSongView());
			return true;
		case R.id.menu_Delete:
			DeleteSongMenuAction(GetSongFromSongView());
			return true;
		case R.id.menu_Share:
			ShareSong(GetSongFromSongView());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
			DeleteSongMenuAction(curSong);
			return true;
		case R.id.menu_Share_Context:
			ShareSong(curSong);
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

	private void ShareSong(Song song) {
		// create the send intent
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);

		// set the type
		shareIntent.setType("text/plain");

		// add a subject
		shareIntent
				.putExtra(android.content.Intent.EXTRA_SUBJECT, song.Title());

		// build the body of the message to be shared
		String shareMessage = song.Title() + Utils.NewLine + song.Content();

		// add the message
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);

		String shareDialogTitle = String.format(manager.context.getResources()
				.getString(R.string.app_shareDialogTitle), Utils.Trim(
				song.Title(), 40));
		// start the chooser for sharing
		startActivity(Intent.createChooser(shareIntent, shareDialogTitle));
	}

	private AppState GetAppState() {
		SongArrayAdapter adapter = (SongArrayAdapter) songsListView.getAdapter();
		SearchTerms terms = adapter.GetSearchTerms();

		return new AppState(terms, viewPager.getCurrentItem(),
				GetSongFromSongView(), isSongEditMode,
				songsListView.getFirstVisiblePosition());
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		final AppState state = GetAppState();
		return state;
	}

	private void RestoreState() {
		final AppState state = (AppState) ((Activity) manager.context).getLastNonConfigurationInstance();
		if (state != null) {
			searchEditText.setText(state.Terms.SearchText());
			viewPager.setCurrentItem(state.ViewIndex);
			if (state.IsSongEditMode) {
				SetSongEditMode();
			}
			UpdateContenSongView(state.Song);

			CreateAdapterAndSetToSongList(state.Terms);
			FocusedItemInSongsList(state.FirstVisibleSongPosition);
		} else {
			LoadDefaultSongsListContent();
			FocusedItemInSongsList(0);
		}
	}
}
