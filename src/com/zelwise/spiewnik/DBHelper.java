package com.zelwise.spiewnik;

import com.zelwise.spiewnik.Song;
import com.zelwise.spiewnik.Category;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "spiewnik.db";
    private static final int DATABASE_VERSION = 1;
    
    public static final String SortDescending = " DESC";
    public static final String SortAscending = " ASC";
    
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void DropTable(SQLiteDatabase db, String tableName ) {
    	
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        
        CreateTable(db,tableName);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            CreateTable(db,Song.Names.TableName);
            CreateTable(db,Category.Names.TableName);
    }
    
    private static void CreateTable(SQLiteDatabase db, String tableName)
    {
    	if (tableName == Song.Names.TableName) {
	  		  db.execSQL("create table " + Song.Names.TableName + " ("
                    + Song.Names.Id + " integer primary key autoincrement," 
                    + Song.Names.CategoryId + " integer,"
                    + Song.Names.SiteId + " integer,"
                    + Song.Names.Title+ " text,"
                    + Song.Names.Content+ " text,"
                    + Song.Names.Rating+ " integer,"
                    + Song.Names.SiteRating+ " integer,"
                    + Song.Names.RecentlyViewedDate+ " long"
                    + ");");
    	}
    	
    	if (tableName == Category.Names.TableName) {
    		db.execSQL("create table " + Category.Names.TableName + " ("
                    + Category.Names.Id + " integer primary key autoincrement," 
                    + Category.Names.Title + " text"
                    + ");");
    	}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
