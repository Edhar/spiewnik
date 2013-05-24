package com.zelwise.spiewnik;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.*;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DBUpgrade {
	private Context context;
	private SQLiteDatabase db;
	private int oldVersion;
	private int newVersion;

	public DBUpgrade(Context context, SQLiteDatabase db, int oldVersion,
			int newVersion) {
		this.context = context;
		this.db = db;
		this.oldVersion = oldVersion;
		this.newVersion = newVersion;
	}

	public void UpdateDb() {
		try {
			
			if (oldVersion == 1 && newVersion > 1) {
				From1to2();
			}
			
		} catch (Exception e) {

		}
	}

	private void SaveSong(String title, String content) {
		new Song(title, content, SettingsHelper.DefaultValues.SiteRatingValue,
				new Date(0)).SaveOrUpdate(db);
	}

	private void From1to2() {
		try {
			// copy update db
			String Update1To2Name = "Update1To2.db";
			InputStream myInput = context.getAssets().open(Update1To2Name);

			String outFileName = DBHelper.DB_PATH + Update1To2Name;

			OutputStream myOutput = new FileOutputStream(outFileName);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			myOutput.flush();
			myOutput.close();
			myInput.close();
			
			//
			SQLiteDatabase updateDB = null;

			try {
				updateDB = SQLiteDatabase.openDatabase(outFileName, null,SQLiteDatabase.OPEN_READONLY);

				ArrayList<Song> songs = Song.GetSongs(updateDB);
				for (Song song : songs) {
					Song newSong = new Song(song.Title(),song.Content(),SettingsHelper.DefaultValues.SiteRatingValue, song.RecentlyViewedDate());
					newSong.SaveOrUpdate(db);
				}

			} catch (SQLiteException e) {
				// database does't exist yet.
			}
			
			File tempFile = new File(outFileName);
			tempFile.delete();

			if (updateDB != null) {
				updateDB.close();
			}
		} catch (Exception e) {

		}
	}

	private List<File> getListFiles(File parentDir) {
		ArrayList<File> inFiles = new ArrayList<File>();
		File[] files = parentDir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				inFiles.addAll(getListFiles(file));
			}
		}
		return inFiles;
	}
}
