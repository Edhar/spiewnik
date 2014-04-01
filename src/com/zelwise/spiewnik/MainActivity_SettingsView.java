package com.zelwise.spiewnik;

import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity_SettingsView extends MainActivity_Ext {
	public MainActivity_SettingsView(MainActivity mainActivity) {
		super(mainActivity);
	}

	Button downloadButton, dropTablesButton,donateButton;

	EditText downloadFromEditText, downloadToEditText, maxSongsPerPageOnResult, minSymbolsForStartSearch;

	LinearLayout advanceLinearLayout;

	Spinner byDefaultResultsForTab,donateSpinner;

	CheckBox advanceCheckBox, seachByAndShowSongNumbersInResult, doMoreRelevantSearch, doNotTurnOffScreen;

	public static final Integer SettingsViewIndex = 0;

	protected void SetDropTableButtonText() {
		dropTablesButton.setText(MainAct.getResources().getString(R.string.settings_dropTablesButton) + Song.AllSongsCount(MainAct.manager.db) + " ("
				+ MainAct.getResources().getString(R.string.settings_totalRecods) + ":" + Song.AllSongsCountWithEmpty(MainAct.manager.db) + ")");
	}
	
	private OnItemSelectedListener byDefaultResultsForTab_ItemSelectedListener = new OnItemSelectedListener() {

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
	};
	
	private TextWatcher maxSongsPerPageOnResult_TextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable searchText) {
			Integer newVal = Utils.ToInt(searchText.toString(), SettingsHelper.DefaultValues.SongsPerPage);
			if (newVal < SettingsHelper.DefaultValues.SongsPerPageMin) {
				newVal = SettingsHelper.DefaultValues.SongsPerPage;
			}
			
			MainAct.manager.settings.SongsPerPage(newVal.toString());
			
			if(!searchText.toString().equals(newVal.toString())){
				searchText.clear();
				//searchText.append(newVal.toString());
			}
		}
	};
	
	private TextWatcher minSymbolsForStartSearch_TextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable searchText) {
			Integer newVal = Utils.ToInt(searchText.toString(), SettingsHelper.DefaultValues.MinSymbolsForStartSearch);
			if (newVal < 0) {
				newVal = SettingsHelper.DefaultValues.MinSymbolsForStartSearch;
			}
			
			MainAct.manager.settings.MinSymbolsForStartSearch(newVal.toString());
			
			if(!searchText.toString().equals(newVal.toString())){
				searchText.clear();
				//searchText.append(newVal.toString());
			}
			
			MainAct.mainActivity_SearchView.RefreshSearchTextHint();
		}
	};
	
	private OnClickListener  seachByAndShowSongNumbersInResult_OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			MainAct.manager.settings.SeachByAndShowSongNumbersInResult(((CheckBox) v).isChecked());
			MainAct.mainActivity_SearchView.ReloadSongsListContent();
		}
	};
	
	private OnClickListener doMoreRelevantSearch_OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			MainAct.manager.settings.DoMoreRelevantSearch(((CheckBox) v).isChecked());
		}
	};
	
	private OnClickListener doNotTurnOffScreen_OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Boolean keepScreenOn = ((CheckBox) v).isChecked();
			MainAct.manager.settings.DoNotTurnOffScreen(keepScreenOn);
			MainAct.ToggleKeepScreenOn(keepScreenOn);
		}
	};
	

	protected void SetSelectedDefaultTab_byDefaultResultsForTab(Integer tabId) {
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
	
	public void SetTextWithoutEventRun_maxSongsPerPageOnResult(String newValue){
		maxSongsPerPageOnResult.removeTextChangedListener(maxSongsPerPageOnResult_TextWatcher);
		maxSongsPerPageOnResult.setText(newValue);
		maxSongsPerPageOnResult.addTextChangedListener(maxSongsPerPageOnResult_TextWatcher);		
	}
	
	public void SetTextWithoutEventRun_minSymbolsForStartSearch(String newValue){
		minSymbolsForStartSearch.removeTextChangedListener(minSymbolsForStartSearch_TextWatcher);
		minSymbolsForStartSearch.setText(newValue);
		minSymbolsForStartSearch.addTextChangedListener(minSymbolsForStartSearch_TextWatcher);
	}
	
	public void SetCheckBoxValue_seachByAndShowSongNumbersInResult(Boolean newStatus){
		seachByAndShowSongNumbersInResult.setOnClickListener(null);
		seachByAndShowSongNumbersInResult.setChecked(newStatus);
		seachByAndShowSongNumbersInResult.setOnClickListener(seachByAndShowSongNumbersInResult_OnClickListener);
	}
	
	public void SetCheckBoxValue_doMoreRelevantSearch(Boolean newStatus){
		doMoreRelevantSearch.setOnClickListener(null);
		doMoreRelevantSearch.setChecked(newStatus);
		doMoreRelevantSearch.setOnClickListener(doMoreRelevantSearch_OnClickListener);
	}
	
	public void SetCheckBoxValue_doNotTurnOffScreen(Boolean newStatus){
		doNotTurnOffScreen.setOnClickListener(null);
		doNotTurnOffScreen.setChecked(newStatus);
		doNotTurnOffScreen.setOnClickListener(doNotTurnOffScreen_OnClickListener);
	}

	@Override
	public void onCreate() {
		View settingsView = MainAct.manager.GetViewPage(SettingsViewIndex);

		ArrayAdapter<TabsItem> adapterDefaultTab = new ArrayAdapter<TabsItem>(MainAct, android.R.layout.simple_spinner_item, MainAct.manager.tabsList.GetTabsItems());
		adapterDefaultTab.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		byDefaultResultsForTab = (Spinner) settingsView.findViewById(R.id.ByDefaultResultsForTab);
		byDefaultResultsForTab.setAdapter(adapterDefaultTab);
		byDefaultResultsForTab.setOnItemSelectedListener(byDefaultResultsForTab_ItemSelectedListener);

		maxSongsPerPageOnResult = (EditText) settingsView.findViewById(R.id.MaxSongsPerPageOnResult);
		maxSongsPerPageOnResult.addTextChangedListener(maxSongsPerPageOnResult_TextWatcher);
		
		minSymbolsForStartSearch = (EditText) settingsView.findViewById(R.id.MinSymbolsForStartSearch);
		minSymbolsForStartSearch.addTextChangedListener(minSymbolsForStartSearch_TextWatcher);
		
		seachByAndShowSongNumbersInResult = (CheckBox) settingsView.findViewById(R.id.SeachByAndShowSongNumbersInResult);
		seachByAndShowSongNumbersInResult.setOnClickListener(seachByAndShowSongNumbersInResult_OnClickListener);

		doMoreRelevantSearch = (CheckBox) settingsView.findViewById(R.id.DoMoreRelevantSearch);
		doMoreRelevantSearch.setOnClickListener(doMoreRelevantSearch_OnClickListener);

		doNotTurnOffScreen = (CheckBox) settingsView.findViewById(R.id.DoNotTurnOffScreen);
		doNotTurnOffScreen.setOnClickListener(doNotTurnOffScreen_OnClickListener);
		
		downloadFromEditText = (EditText) settingsView.findViewById(R.id.DownloadFromEditText);
		downloadToEditText = (EditText) settingsView.findViewById(R.id.DownloadToEditText);
		downloadButton = (Button) settingsView.findViewById(R.id.DownloadButton);
		downloadButton.setOnClickListener(MainAct);
		dropTablesButton = (Button) settingsView.findViewById(R.id.DropTablesButton);
		dropTablesButton.setOnClickListener(MainAct);
		
		ArrayAdapter<DonateProduct> adapterDonate = new ArrayAdapter<DonateProduct>(MainAct, android.R.layout.simple_spinner_item, MainAct.mainActivity_InAppBilling.GetDonateProducts());
		adapterDonate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		donateSpinner = (Spinner) settingsView.findViewById(R.id.DonateSpinner);
		donateSpinner.setAdapter(adapterDonate);
		donateButton = (Button) settingsView.findViewById(R.id.DonateButton);
		donateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DonateProduct product = (DonateProduct)donateSpinner.getSelectedItem();
				if(product!=null){
					//Toast.makeText(MainAct, product.LabelDescription(), Toast.LENGTH_SHORT).show();
					MainAct.mainActivity_InAppBilling.makeDonation(product);
				}
			}
		});
		
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
		
		
		

		TextView version = (TextView) settingsView.findViewById(R.id.Version);
		String versionName = "";
		try {
			versionName = MainAct.manager.context.getPackageManager().getPackageInfo(MainAct.manager.context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		version.setText(MainAct.manager.context.getResources().getString(R.string.labels_Version) + versionName);

	}
}
