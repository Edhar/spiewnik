package com.zelwise.spiewnik;

import android.view.View;
import android.widget.Toast;

public class MainActivity_Click extends MainActivity_Ext {
    public MainActivity_Click(MainActivity mainActivity) {
        super(mainActivity);
    }
    
    public void onClick(View v) {
		switch (v.getId()) {
		case R.id.DownloadButton:
			String url = "http://www.spiewnik.com/wyswietl.php?numer=";
			Integer startSiteId = Utils.ToInt(MainAct.mainActivity_SettingsView.downloadFromEditText.getText().toString(), 1);
			Integer endSiteId = Utils.ToInt(MainAct.mainActivity_SettingsView.downloadToEditText.getText().toString(), 15000);
			if (endSiteId < startSiteId) {
				Toast.makeText(MainAct.manager.context, "'To' must be higher then 'From'", Toast.LENGTH_SHORT).show();
			}
			MainAct.manager.HideKeyboard();
			MainAct.manager.DownloadAndSaveContent(url, startSiteId, endSiteId);
			break;
		case R.id.DropTablesButton:
			DBHelper.DropTable(MainAct.manager.db, Song.Names.TableName);
			DBHelper.CreateTable(MainAct.manager.db, Song.Names.TableName);
			MainAct.mainActivity_SettingsView.SetDropTableButtonText();
			MainAct.manager.HideKeyboard();
			break;
		case R.id.RecentlyViewedButton:
			MainAct.mainActivity_SearchView.ClearSearchText();
			MainAct.mainActivity_SearchView.LoadRecentlyViewed();
			break;
		case R.id.OftenViewedButton:
			MainAct.mainActivity_SearchView.ClearSearchText();
			MainAct.mainActivity_SearchView.LoadOftenViewed();
			break;
		case R.id.SiteRatingViewedButton:
			MainAct.mainActivity_SearchView.ClearSearchText();
			MainAct.mainActivity_SearchView.LoadSiteRatingViewed();
			break;
		case R.id.FavoriteButton:
			MainAct.mainActivity_SearchView.ClearSearchText();
			MainAct.mainActivity_SearchView.LoadFavorite();
			break;
		case R.id.SearcTextClearButton:
			MainAct.mainActivity_SearchView.ClearSearchText();
			MainAct.mainActivity_SearchView.ShowHideClearSearchButton(false);
			MainAct.mainActivity_SearchView.LoadDefaultSongsListContent();
			break;
		}
	}
}
