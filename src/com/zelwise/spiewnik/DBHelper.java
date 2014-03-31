package com.zelwise.spiewnik;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DBHelper extends SQLiteOpenHelper {
	public static String DB_PATH = "/data/data/com.zelwise.spiewnik/databases/";;
	public static String DB_SD_PATH = Environment.getExternalStorageDirectory() + File.separator;
	public static final String DB_NAME = "spiewnik.db";
	public static final int DATABASE_VERSION = 3;

	public static final String SortDescending = " DESC";
	public static final String SortAscending = " ASC";
	private Context context;

	public DBHelper(Context context, String appDataDirectory) {
		super(context, context.getExternalFilesDir(null) + File.separator + DB_NAME, null, DATABASE_VERSION);
		this.context = context;
		// DB_PATH = appDataDirectory + "/databases/";;
	}

	public static void DropTable(SQLiteDatabase db, String tableName) {

		db.execSQL("DROP TABLE IF EXISTS " + tableName);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		CreateTable(db, Song.Names.TableName);
		CreateTable(db, Category.Names.TableName);
	}

	public SQLiteDatabase open() {
		return this.getWritableDatabase();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		DBUpgrade updater = new DBUpgrade(context, db, oldVersion, newVersion);
		updater.UpdateDb();
	}

	public static void CreateTable(SQLiteDatabase db, String tableName) {
		DropTable(db, tableName);
		if (tableName == Song.Names.TableName) {

			db.execSQL("create table " + Song.Names.TableName + " (" + Song.Names.Id + " INTEGER PRIMARY KEY AUTOINCREMENT," + Song.Names.CategoryId + " INTEGER DEFAULT ( 0 )," + Song.Names.SiteId
					+ " INTEGER DEFAULT ( 0 )," + Song.Names.Title + " TEXT," + Song.Names.Content + " TEXT," + Song.Names.Rating + " INTEGER DEFAULT ( 0 )," + Song.Names.SiteRating
					+ " INTEGER DEFAULT ( 0 )," + Song.Names.RecentlyViewedDate + " long DEFAULT ( 0 )," + Song.Names.Favorite + " BOOLEAN DEFAULT ( 0 )" + ");");

			// id ASC
			db.execSQL("CREATE UNIQUE INDEX idx_Song_Id_ASC ON Song (Id ASC);");

			// title ASC
			db.execSQL("CREATE INDEX idx_Song_Title_ASC ON Song (Title ASC);");

			// Id_ASC_Title_ASC
			db.execSQL("CREATE UNIQUE INDEX idx_Song_Id_ASC_Title_ASC ON Song (Id ASC,Title ASC);");

			// Rating DESC
			db.execSQL("CREATE INDEX idx_Song_Rating_DESC ON Song (Rating DESC);");
			// Rating_DESC_Title_ASC
			db.execSQL("CREATE INDEX idx_Song_Rating_DESC_Title_ASC ON Song (Rating DESC,Title ASC);");
			// Rating_DESC_Id_ASC_Title_ASC
			db.execSQL("CREATE UNIQUE INDEX idx_Song_Rating_DESC_Id_ASC_Title_ASC ON Song (Rating DESC,Id ASC,Title ASC);");

			// RecentlyViewedDate DESC
			db.execSQL("CREATE INDEX idx_Song_RecentlyViewedDate_DESC ON Song (RecentlyViewedDate DESC);");
			// RecentlyViewedDate_DESC_Title_ASC
			db.execSQL("CREATE INDEX idx_Song_RecentlyViewedDate_DESC_Title_ASC ON Song (RecentlyViewedDate DESC,Title ASC);");
			// RecentlyViewedDate_DESC_Id_ASC_Title_ASC
			db.execSQL("CREATE UNIQUE INDEX idx_Song_RecentlyViewedDate_DESC_Id_ASC_Title_ASC ON Song (RecentlyViewedDate DESC,Id ASC,Title ASC);");

			// SiteRating_DESC
			db.execSQL("CREATE INDEX idx_Song_SiteRating_DESC ON Song (SiteRating DESC);");
			// SiteRating_DESC_Title_ASC
			db.execSQL("CREATE INDEX idx_Song_SiteRating_DESC_Title_ASC ON Song (SiteRating DESC,Title ASC);");
			// SiteRating_DESC_Title_ASC
			db.execSQL("CREATE UNIQUE INDEX idx_Song_SiteRating_DESC_Id_ASC_Title_ASC ON Song (SiteRating DESC,Id ASC,Title ASC);");

			// Favorite_DESC
			db.execSQL("CREATE INDEX idx_Song_Favorite_DESC ON Song (Favorite DESC);");
			// Favorite_DESC_Rating_DESC
			db.execSQL("CREATE INDEX idx_Song_Favorite_DESC_Rating_DESC ON Song (Favorite DESC,Rating   DESC);");
			// Favorite_DESC_Rating_DESC_Title_ASC
			db.execSQL("CREATE INDEX idx_Song_Favorite_DESC_Rating_DESC_Title_ASC ON Song (Favorite DESC,Rating   DESC,Title    ASC);");
		}

		if (tableName == Category.Names.TableName) {
			db.execSQL("create table " + Category.Names.TableName + " (" + Category.Names.Id + " INTEGER PRIMARY KEY AUTOINCREMENT," + Category.Names.Title + " TEXT NOT NULL DEFAULT ( '' ),"
					+ Category.Names.SiteCode + " CHAR NOT NULL DEFAULT ( '' )," + Category.Names.MenuOrder + " INTEGER NOT NULL DEFAULT ( 0 )," + Category.Names.Internal
					+ " text BOOLEAN DEFAULT ( 1 )" + ");");
		}
	}

	public void ClearStatistics(SQLiteDatabase db) {

		ContentValues cv = new ContentValues();
		cv.put(Song.Names.Rating, 0);
		cv.put(Song.Names.RecentlyViewedDate, new Date().getTime());
		db.update(Song.Names.TableName, cv, null, null);
	}

	public void ClearRatingStatistics(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(Song.Names.Rating, 0);
		db.update(Song.Names.TableName, cv, null, null);
	}

	public void ClearRecentlyViewedDateStatistics(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(Song.Names.RecentlyViewedDate, new Date().getTime());
		db.update(Song.Names.TableName, cv, null, null);
	}

	public void copyDataBase() throws IOException {

		InputStream myInput = context.getAssets().open(DB_NAME);

		String outFileName = context.getExternalFilesDir(null) + File.separator + DB_NAME;

		File file = new File(DB_PATH);
		file.mkdirs();

		OutputStream myOutput = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {

			String myPath = DB_PATH + DB_NAME;

			if (DATABASE_VERSION <= 3) {
				try {
					checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
					if (checkDB != null) {
						checkDB.close();
						checkDB = null;
						DBUpgrade.From2to3(context.getExternalFilesDir(null).toString());
					}
				} catch (SQLiteException e) {

				}
			}

			if (DATABASE_VERSION >= 3) {
				myPath = context.getExternalFilesDir(null) + File.separator + DB_NAME;
			}

			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// database does't exist yet.

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

}
