package com.zelwise.spiewnik;

import android.content.pm.PackageManager.NameNotFoundException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity_SettingsView extends MainActivity_Ext {
	public MainActivity_SettingsView(MainActivity mainActivity) {
		super(mainActivity);
	}

	Button downloadButton, dropTablesButton;

	EditText downloadFromEditText, downloadToEditText, maxSongsPerPageOnResult, minSymbolsForStartSearch;

	LinearLayout advanceLinearLayout;

	Spinner byDefaultResultsForTab;

	CheckBox advanceCheckBox, seachByAndShowSongNumbersInResult, doMoreRelevantSearch, doNotTurnOffScreen;

	public static final Integer SettingsViewIndex = 0;

	protected void SetDropTableButtonText() {
		dropTablesButton.setText(MainAct.getResources().getString(R.string.settings_dropTablesButton) + Song.AllSongsCount(MainAct.manager.db) + " ("
				+ MainAct.getResources().getString(R.string.settings_totalRecods) + ":" + Song.AllSongsCountWithEmpty(MainAct.manager.db) + ")");
	}

	@Override
	public void onCreate() {
		View settingsView = MainAct.manager.GetViewPage(SettingsViewIndex);

		byDefaultResultsForTab = (Spinner) settingsView.findViewById(R.id.ByDefaultResultsForTab);
		ArrayAdapter<TabsItem> adapterDefaultTab = new ArrayAdapter<TabsItem>(MainAct, android.R.layout.simple_spinner_item, MainAct.manager.tabsList.GetTabsItems());
		adapterDefaultTab.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		byDefaultResultsForTab.setAdapter(adapterDefaultTab);
		byDefaultResultsForTab.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if (parent != null) {
					TabsItem newTab = (TabsItem) parent.getItemAtPosition(pos);
					if (newTab != null) {
						MainAct.manager.settings.DefaultTabId(newTab.Id());
					}
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		maxSongsPerPageOnResult = (EditText) settingsView.findViewById(R.id.MaxSongsPerPageOnResult);
		maxSongsPerPageOnResult.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable searchText) {
				Integer newVal = Utils.ToInt(searchText.toString(), SettingsHelper.DefaultValues.SongsPerPage);
				if (newVal > 0) {
					MainAct.manager.settings.SongsPerPage(newVal);
				} else {
					MainAct.manager.settings.SongsPerPage(SettingsHelper.DefaultValues.SongsPerPage);
					maxSongsPerPageOnResult.setText(SettingsHelper.DefaultValues.SongsPerPage.toString());
				}

			}
		});

		minSymbolsForStartSearch = (EditText) settingsView.findViewById(R.id.MinSymbolsForStartSearch);
		minSymbolsForStartSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable searchText) {
				Integer newVal = Utils.ToInt(searchText.toString(), SettingsHelper.DefaultValues.MinSymbolsForStartSearch);
				if (newVal > 0) {
					MainAct.manager.settings.MinSymbolsForStartSearch(newVal);
				} else {
					MainAct.manager.settings.MinSymbolsForStartSearch(SettingsHelper.DefaultValues.MinSymbolsForStartSearch);
					minSymbolsForStartSearch.setText(SettingsHelper.DefaultValues.MinSymbolsForStartSearch.toString());
				}
				MainAct.mainActivity_SearchView.RefreshSearchTextHint();
			}
		});

		downloadFromEditText = (EditText) settingsView.findViewById(R.id.DownloadFromEditText);
		downloadToEditText = (EditText) settingsView.findViewById(R.id.DownloadToEditText);
		downloadButton = (Button) settingsView.findViewById(R.id.DownloadButton);
		downloadButton.setOnClickListener(MainAct);
		dropTablesButton = (Button) settingsView.findViewById(R.id.DropTablesButton);
		dropTablesButton.setOnClickListener(MainAct);
		advanceLinearLayout = (LinearLayout) settingsView.findViewById(R.id.AdvanceLinearLayout);
		advanceCheckBox = (CheckBox) settingsView.findViewById(R.id.AdvanceCheckBox);
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
		seachByAndShowSongNumbersInResult = (CheckBox) settingsView.findViewById(R.id.SeachByAndShowSongNumbersInResult);
		seachByAndShowSongNumbersInResult.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainAct.manager.settings.SeachByAndShowSongNumbersInResult(((CheckBox) v).isChecked());
				MainAct.mainActivity_SearchView.ReloadSongsListContent();
			}
		});

		doMoreRelevantSearch = (CheckBox) settingsView.findViewById(R.id.DoMoreRelevantSearch);
		doMoreRelevantSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainAct.manager.settings.DoMoreRelevantSearch(((CheckBox) v).isChecked());
			}
		});

		doNotTurnOffScreen = (CheckBox) settingsView.findViewById(R.id.DoNotTurnOffScreen);
		doNotTurnOffScreen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean keepScreenOn = ((CheckBox) v).isChecked();
				MainAct.manager.settings.DoNotTurnOffScreen(keepScreenOn);
				MainAct.ToggleKeepScreenOn(keepScreenOn);
			}
		});
		

		TextView version = (TextView) settingsView.findViewById(R.id.Version);
		String versionName = "";
		try {
			versionName = MainAct.manager.context.getPackageManager().getPackageInfo(MainAct.manager.context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		version.setText(MainAct.manager.context.getResources().getString(R.string.labels_Version) + versionName);

	}
}
