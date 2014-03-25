package com.zelwise.spiewnik;

import java.util.Date;

import com.zelwise.spiewnik.Song.Names;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity_SearchView extends MainActivity_Ext {
	public MainActivity_SearchView(MainActivity mainActivity) {
		super(mainActivity);
	}

	public static final Integer SearchViewIndex = 1;

	Button recentlyViewedButton, oftenViewedButton, siteRatingViewedButton, searcTextClearButton, favoriteButton;

	EditText searchEditText;

	ListView songsListView;

	protected OnTouchListener onTouchListenerClear = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Button btn = (Button) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				btn.setBackgroundColor(MainAct.manager.context.getResources().getColor(R.color.theme_violet_bg_PressedClearButton));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				btn.setBackgroundColor(MainAct.manager.context.getResources().getColor(R.color.theme_violet_bg_SearcTextClearButton));
				v.performClick();
			}
			return true;
		}
	};
	protected OnTouchListener onTouchListenerTabsItem = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Button btn = (Button) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				btn.setBackgroundColor(MainAct.manager.context.getResources().getColor(R.color.theme_violet_bg_PressedButton));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				btn.setBackgroundColor(MainAct.manager.context.getResources().getColor(R.color.theme_violet_bg_TabsItem));
				v.performClick();
			}
			return true;
		}
	};

	protected void SetSelectedDefaultTab(Integer tabId) {
		ArrayAdapter<TabsItem> tabAdapter = (ArrayAdapter<TabsItem>) MainAct.mainActivity_SettingsView.byDefaultResultsForTab.getAdapter();
		if (tabAdapter != null) {
			TabsItem curTab = (TabsItem) MainAct.mainActivity_SettingsView.byDefaultResultsForTab.getItemAtPosition(MainAct.mainActivity_SettingsView.byDefaultResultsForTab.getSelectedItemPosition());
			TabsItem newTab = MainAct.manager.tabsList.Get(tabId);
			for (int i = 0; i < tabAdapter.getCount(); i++) {
				if (((TabsItem) tabAdapter.getItem(i)).Id() == newTab.Id()) {
					if (curTab.Id() != newTab.Id()) {
						MainAct.mainActivity_SettingsView.byDefaultResultsForTab.setSelection(i);
					}
					break;
				}
			}
		}
	}

	Handler startSearchHandler = new Handler();
	protected Runnable runSearchRunnable = new Runnable() {
		public void run() {
			long curTime = new Date().getTime();
			if (curTime - lastStartSearchTime > SettingsHelper.DefaultValues.StartSearchDelay) {
				StartSearchImmediately();
			} else {
				startSearchHandler.postDelayed(runSearchRunnable, SettingsHelper.DefaultValues.StartSearchDelay);
			}

		}
	};

	// to fix double event
	String searchEditTextPrevValue = "";

	protected long lastStartSearchTime;

	protected void StartSearchImmediately() {
		SearchTerms terms = new SearchTerms(MainAct.manager.settings, SearchBy.Text, searchEditText.getText().toString(), "");
		CreateAdapterAndSetToSongList(terms);
	}

	protected void StartSearchWithDelay() {
		startSearchHandler.postDelayed(runSearchRunnable, SettingsHelper.DefaultValues.StartSearchDelay);
		lastStartSearchTime = new Date().getTime();
	}

	protected void RefreshSearchTextHint() {
		String searchHint = "%s";
		switch (MainAct.manager.settings.MinSymbolsForStartSearch()) {
		case 1:
			searchHint = MainAct.manager.context.getResources().getString(R.string.search_searchHintStartText1);
			break;
		case 2:
		case 3:
		case 4:
			searchHint = MainAct.manager.context.getResources().getString(R.string.search_searchHintStartText2to4);
			break;
		default:
			searchHint = MainAct.manager.context.getResources().getString(R.string.search_searchHintStartText5);
			break;
		}
		String searchLabel = MainAct.manager.context.getResources().getString(R.string.labels_Search);
		searchEditText.setHint(searchLabel + String.format(searchHint, MainAct.manager.settings.MinSymbolsForStartSearch()));
	}

	protected void ShowSongProperties(Song song) {
		String content = MainAct.manager.context.getResources().getString(R.string.labels_Id)
				+ song.Id()
				+ Utils.NewLine
				// +
				// MainAct.manager.context.getResources().getString(R.string.labels_SiteId)
				// + song.SiteId() + Utils.NewLine
				+ MainAct.manager.context.getResources().getString(R.string.labels_Rating) + song.Rating() + Utils.NewLine
				+ MainAct.manager.context.getResources().getString(R.string.labels_SiteRating) + song.SiteRating() + Utils.NewLine
				+ MainAct.manager.context.getResources().getString(R.string.labels_Favorite)
				+ (song.Favorite() ? MainAct.manager.context.getResources().getString(R.string.buttons_Yes) : MainAct.manager.context.getResources().getString(R.string.buttons_No)) + Utils.NewLine
				+ MainAct.manager.context.getResources().getString(R.string.labels_RecentlyViewedDate) + Utils.iso8601Format.format(song.RecentlyViewedDate());

		// Toast.makeText(MainAct.manager.context, content,
		// Toast.LENGTH_LONG).show();

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainAct.manager.context);
		alertBuilder.setIcon(0);
		alertBuilder.setTitle(song.Title());

		alertBuilder.setMessage(content).setCancelable(false).setPositiveButton(MainAct.manager.context.getResources().getString(R.string.buttons_Yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alert = alertBuilder.create();
		alert.show();
	}

	protected void FocusedItemInSongsList(int itemIndex) {
		songsListView.setSelection(itemIndex);
		songsListView.requestFocus();
	}

	protected void AddDynamicallyDataToList() {
		SongArrayAdapter listAdapter = (SongArrayAdapter) songsListView.getAdapter();
		if (listAdapter != null && listAdapter.HasNextPage()) {
			listAdapter.AddAdditionalPage();
		}
	}

	protected void CreateAdapterAndSetToSongList(SearchTerms terms) {
		MarkActiveTabItem(terms);
		SongArrayAdapter songsArrayAdapter = new SongArrayAdapter(MainAct.manager, terms);
		if (songsArrayAdapter.HasSongs()) {
			songsListView.setAdapter(songsArrayAdapter);
		} else {
			songsListView.setAdapter(null);
			Toast toast = Toast.makeText(MainAct.manager.context, MainAct.manager.context.getResources().getString(R.string.search_NothingFound), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 50);
			toast.show();
		}
	}

	private void MarkActiveTabItem(SearchTerms terms) {

		int pressedColor = MainAct.manager.context.getResources().getColor(R.color.theme_violet_bg_PressedButton);
		int defaultColor = MainAct.manager.context.getResources().getColor(R.color.theme_violet_bg_TabsItem);

		recentlyViewedButton.setBackgroundColor(defaultColor);
		oftenViewedButton.setBackgroundColor(defaultColor);
		siteRatingViewedButton.setBackgroundColor(defaultColor);
		favoriteButton.setBackgroundColor(defaultColor);

		switch (terms.SearchBy()) {
		case RecentlyViewedDate:
			recentlyViewedButton.setBackgroundColor(pressedColor);
			break;
		case Rating:
			oftenViewedButton.setBackgroundColor(pressedColor);
			break;
		case SiteRating:
			siteRatingViewedButton.setBackgroundColor(pressedColor);
			break;
		case Favorite:
			favoriteButton.setBackgroundColor(pressedColor);
			break;
		case Text:
			siteRatingViewedButton.setBackgroundColor(pressedColor);
			break;

		default:
			siteRatingViewedButton.setBackgroundColor(pressedColor);
			break;
		}
	}

	protected void ClearSearchText() {
		searchEditText.setText("");
	}

	protected void LoadOftenViewed() {
		SearchTerms terms = new SearchTerms(MainAct.manager.settings, SearchBy.Rating, "", Names.Rating + DBHelper.SortDescending);
		CreateAdapterAndSetToSongList(terms);
	}

	protected void LoadSiteRatingViewed() {
		SearchTerms terms = new SearchTerms(MainAct.manager.settings, SearchBy.SiteRating, "", Names.SiteRating + DBHelper.SortDescending);
		CreateAdapterAndSetToSongList(terms);
	}

	protected void LoadRecentlyViewed() {
		SearchTerms terms = new SearchTerms(MainAct.manager.settings, SearchBy.RecentlyViewedDate, "", Names.RecentlyViewedDate + DBHelper.SortDescending);
		CreateAdapterAndSetToSongList(terms);
	}

	protected void LoadFavorite() {
		SearchTerms terms = new SearchTerms(MainAct.manager.settings, SearchBy.Favorite, "", Names.Favorite + DBHelper.SortDescending);
		CreateAdapterAndSetToSongList(terms);
	}

	protected void ShowHideClearSearchButton(Boolean show) {
		if (searcTextClearButton.getVisibility() != View.VISIBLE && show) {
			searcTextClearButton.setVisibility(View.VISIBLE);
		} else if (searcTextClearButton.getVisibility() != View.GONE && !show) {
			searcTextClearButton.setVisibility(View.GONE);
		}
	}

	protected void LoadDefaultSongsListContent() {
		int tabId = MainAct.manager.settings.DefaultTabId();

		if (tabId == MainAct.manager.tabsList.SiteRating.Id()) {
			LoadSiteRatingViewed();
		} else if (tabId == MainAct.manager.tabsList.Often.Id()) {
			LoadOftenViewed();
		} else if (tabId == MainAct.manager.tabsList.Recent.Id()) {
			LoadRecentlyViewed();
		} else if (tabId == MainAct.manager.tabsList.Favorite.Id()) {
			LoadFavorite();
		}
	}

	protected void ReloadSongsListContent() {
		SongArrayAdapter listAdapter = (SongArrayAdapter) songsListView.getAdapter();
		if (listAdapter != null) {
			listAdapter.Reload();
			FocusedItemInSongsList(songsListView.getFirstVisiblePosition());
		}
	}

	protected void DeleteSongMenuAction(Song song) {
		final Song songToDelete = song;

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainAct.manager.context);

		alertBuilder.setIcon(0);
		alertBuilder.setTitle(MainAct.manager.context.getResources().getString(R.string.labels_AreYouSureYouWantToDeleteThisSong));
		alertBuilder.setMessage("\"" + song.Title() + "\"");
		alertBuilder.setCancelable(false);
		alertBuilder.setPositiveButton(MainAct.manager.context.getResources().getString(R.string.buttons_Yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				songToDelete.Delete(MainAct.manager.db);
				if (MainAct.manager.viewPager.getCurrentItem() == MainActivity_SongView.SongViewIndex) {
					MainAct.manager.viewPager.setCurrentItem(MainActivity_SearchView.SearchViewIndex);
				}
				MainAct.mainActivity_SongView.IsSongEditMode(false);
				ReloadSongsListContent();
			}
		});
		alertBuilder.setCancelable(false);
		alertBuilder.setNegativeButton(MainAct.manager.context.getResources().getString(R.string.buttons_No), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = alertBuilder.show();
		TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
		messageText.setTypeface(Typeface.DEFAULT_BOLD);
		messageText.setTextColor(Color.RED);
		alert.show();

	}

	@Override
	public void onCreate() {
		View searchView = MainAct.manager.GetViewPage(SearchViewIndex);

		siteRatingViewedButton = (Button) searchView.findViewById(R.id.SiteRatingViewedButton);
		siteRatingViewedButton.setOnClickListener(MainAct);

		favoriteButton = (Button) searchView.findViewById(R.id.FavoriteButton);
		favoriteButton.setOnClickListener(MainAct);

		recentlyViewedButton = (Button) searchView.findViewById(R.id.RecentlyViewedButton);
		recentlyViewedButton.setOnClickListener(MainAct);
		oftenViewedButton = (Button) searchView.findViewById(R.id.OftenViewedButton);
		oftenViewedButton.setOnClickListener(MainAct);
		searcTextClearButton = (Button) searchView.findViewById(R.id.SearcTextClearButton);
		searcTextClearButton.setOnClickListener(MainAct);

		songsListView = (ListView) searchView.findViewById(R.id.SongsListView);
		MainAct.registerForContextMenu(songsListView);
		songsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View view, int position, long id) {
				MainAct.manager.viewPager.setCurrentItem(MainActivity_SongView.SongViewIndex);

				MainAct.manager.HideKeyboard();
				MainAct.mainActivity_SongView.SetSongViewViewMode();

				Song curSong = (Song) (songsListView.getItemAtPosition(position));

				// TODO update adapter
				curSong = Song.Get(MainAct.manager.db, curSong.Id());

				MainAct.mainActivity_SongView.UpdateContenSongView(curSong);

				curSong.RecentlyViewedDate(new Date());
				curSong.Rating(curSong.Rating() + 1);
				curSong.SaveOrUpdate(MainAct.manager.db);
			}
		});
		songsListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				MainAct.manager.HideKeyboard();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItemIndex, int visibleItemCountOnDisplay, int totalItemCount) {
				final int hidenSongsCount = 2;
				if (totalItemCount > 0 && (totalItemCount - firstVisibleItemIndex - visibleItemCountOnDisplay) < hidenSongsCount) {
					AddDynamicallyDataToList();
				}
			}
		});
		songsListView.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					MainAct.manager.HideKeyboard();
				}
			}
		});

		searchEditText = (EditText) searchView.findViewById(R.id.SearchEditText);
		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence newStr, int start, int before, int count) {

				if (!newStr.toString().equalsIgnoreCase(searchEditTextPrevValue) && newStr.length() >= MainAct.manager.settings.MinSymbolsForStartSearch()) {
					ShowHideClearSearchButton(true);

					if (MainAct.manager.settings.DoMoreRelevantSearch()) {
						StartSearchWithDelay();
					} else {
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
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable searchText) {

			}
		});
		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					MainAct.manager.ShowKeyboard();
				} else {
					MainAct.manager.HideKeyboard();
				}
			}
		});

		ImageHelper imgHelper = new ImageHelper(MainAct.manager.context);
		int sizeInPx = (int) imgHelper.ConvertDipToPixel(20);
		Drawable resizedDrawable = imgHelper.getResizedDrawable(R.drawable.ic_action_search, sizeInPx, sizeInPx);
		searchEditText.setCompoundDrawablesWithIntrinsicBounds(resizedDrawable, null, null, null);
		searchEditText.setCompoundDrawablePadding(15);
		searchEditText.setPadding(15, 0, 0, 0);

		recentlyViewedButton.setOnTouchListener(onTouchListenerTabsItem);
		oftenViewedButton.setOnTouchListener(onTouchListenerTabsItem);
		siteRatingViewedButton.setOnTouchListener(onTouchListenerTabsItem);
		favoriteButton.setOnTouchListener(onTouchListenerTabsItem);
		searcTextClearButton.setOnTouchListener(onTouchListenerClear);

	}
}
