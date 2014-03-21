package com.zelwise.spiewnik;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;

public class MainActivity_Menu extends MainActivity_Ext {
    public MainActivity_Menu(MainActivity mainActivity) {
        super(mainActivity);
    }
    
	public void onCreateOptionsMenu(Menu menu) {
		MainAct.getMenuInflater().inflate(R.menu.app_menu, menu);
	}

	public void onPrepareOptionsMenu(Menu menu) {
		menu.setGroupVisible(R.id.menu_EditGroup, MainAct.manager.viewPager.getCurrentItem() == MainActivity_SongView.SongViewIndex);
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.SongsListView) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(((Song) MainAct.mainActivity_SearchView.songsListView.getItemAtPosition(info.position)).Title());
			MenuInflater inflater = MainAct.getMenuInflater();
			inflater.inflate(R.menu.song_item_context_menu, menu);
		}
	}

	public boolean onOptionsItemSelected(MenuItem item,boolean defaultResult) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			MainAct.manager.viewPager.setCurrentItem(MainActivity_SettingsView.SettingsViewIndex);
			return true;
		case R.id.menu_Exit:
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			MainAct.startActivity(intent);
			MainAct.finish();
			System.exit(0);
			return true;
		case R.id.menu_Add:
			MainAct.mainActivity_SongView.AddNewSongMenuAction();
			return true;
		case R.id.menu_Edit:
			MainAct.mainActivity_SongView.SetSongEditMode();
			return true;
		case R.id.menu_Save:
			Song newSong = MainAct.mainActivity_SongView.GetSongFromSongView();
			newSong.SaveOrUpdate(MainAct.manager.db);
			MainAct.mainActivity_SongView.UpdateContenSongView(newSong);
			MainAct.mainActivity_SongView.SetSongViewViewMode();
			MainAct.mainActivity_SearchView.ReloadSongsListContent();
			return true;
		case R.id.menu_Properties:
			MainAct.mainActivity_SearchView.ShowSongProperties(MainAct.mainActivity_SongView.GetSongFromSongView());
			return true;
		case R.id.menu_Delete:
			MainAct.mainActivity_SearchView.DeleteSongMenuAction(MainAct.mainActivity_SongView.GetSongFromSongView());
			return true;
		case R.id.menu_Share:
			MainAct.ShareSong(MainAct.mainActivity_SongView.GetSongFromSongView());
			return true;
		default:
			return defaultResult;
		}
	}

	public boolean onContextItemSelected(MenuItem item,boolean defaultResult) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Song curSong = (Song) MainAct.mainActivity_SearchView.songsListView.getItemAtPosition(info.position);
		switch (item.getItemId()) {
		case R.id.menu_Edit_Context:
			MainAct.manager.viewPager.setCurrentItem(MainActivity_SongView.SongViewIndex);
			MainAct.mainActivity_SongView.UpdateContenSongView(curSong);
			MainAct.mainActivity_SongView.SetSongEditMode();
			return true;
		case R.id.menu_Properties_Context:
			MainAct.mainActivity_SearchView.ShowSongProperties(curSong);
			return true;
		case R.id.menu_Delete_Context:
			MainAct.mainActivity_SearchView.DeleteSongMenuAction(curSong);
			return true;
		case R.id.menu_Share_Context:
			MainAct.ShareSong(curSong);
			return true;
		default:
			return defaultResult;
		}
	}

}
