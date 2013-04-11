package com.zelwise.spiewnik;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import com.zelwise.spiewnik.Song;
import com.zelwise.spiewnik.Category;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static String DB_PATH = "/data/data/com.zelwise.spiewnik/databases/";
	private static final String DB_NAME = "spiewnik.db";
    private static final int DATABASE_VERSION = 1;
    
    private SQLiteDatabase myDataBase;
    
    public static final String SortDescending = " DESC";
    public static final String SortAscending = " ASC";
    private Context context;
    
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;
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
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
	  		
	  		//id ASC
	  		db.execSQL("CREATE UNIQUE INDEX idx_Song_Id_ASC ON Song (Id ASC);");
	  		
	  		//title ASC
	  		db.execSQL("CREATE INDEX idx_Song_Title_ASC ON Song (Title ASC);");
	  		
	  	    //Id_ASC_Title_ASC
	  		db.execSQL("CREATE UNIQUE INDEX idx_Song_Id_ASC_Title_ASC ON Song (Id ASC,Title ASC);");
	  		
	  		
	  		//Rating DESC
	  		db.execSQL("CREATE INDEX idx_Song_Rating_DESC ON Song (Rating DESC);");
	  		//Rating_DESC_Title_ASC
	  		db.execSQL("CREATE INDEX idx_Song_Rating_DESC_Title_ASC ON Song (Rating DESC,Title ASC);");
	  		//Rating_DESC_Id_ASC_Title_ASC
	  		db.execSQL("CREATE UNIQUE INDEX idx_Song_Rating_DESC_Id_ASC_Title_ASC ON Song (Rating DESC,Id ASC,Title ASC);");
	  		
	  		
	  		//RecentlyViewedDate DESC
	  		db.execSQL("CREATE INDEX idx_Song_RecentlyViewedDate_DESC ON Song (RecentlyViewedDate DESC);");
	  		//RecentlyViewedDate_DESC_Title_ASC
	  		db.execSQL("CREATE INDEX idx_Song_RecentlyViewedDate_DESC_Title_ASC ON Song (RecentlyViewedDate DESC,Title ASC);");
	  		//RecentlyViewedDate_DESC_Id_ASC_Title_ASC
	  		db.execSQL("CREATE UNIQUE INDEX idx_Song_RecentlyViewedDate_DESC_Id_ASC_Title_ASC ON Song (RecentlyViewedDate DESC,Id ASC,Title ASC);");
	  		
	  		//SiteRating_DESC
	  		db.execSQL("CREATE INDEX idx_Song_SiteRating_DESC ON Song (SiteRating DESC);");
	  		//SiteRating_DESC_Title_ASC
	  		db.execSQL("CREATE INDEX idx_Song_SiteRating_DESC_Title_ASC ON Song (SiteRating DESC,Title ASC);");
	  		//SiteRating_DESC_Title_ASC
	  		db.execSQL("CREATE UNIQUE INDEX idx_Song_SiteRating_DESC_Id_ASC_Title_ASC ON Song (SiteRating DESC,Id ASC,Title ASC);");
    	}
    	
    	if (tableName == Category.Names.TableName) {
    		db.execSQL("create table " + Category.Names.TableName + " ("
                    + Category.Names.Id + " integer primary key autoincrement," 
                    + Category.Names.Title + " text"
                    + ");");
    	}
    }
    
    public void ClearStatistics(SQLiteDatabase db){
    	
    	ContentValues cv = new ContentValues();
    	cv.put(Song.Names.Rating, 0);
    	cv.put(Song.Names.RecentlyViewedDate, new Date().getTime());
    	db.update(Song.Names.TableName, cv, null, null);
    }
    
	public void ClearRatingStatistics(SQLiteDatabase db){
		ContentValues cv = new ContentValues();
    	cv.put(Song.Names.Rating, 0);    	
    	db.update(Song.Names.TableName, cv, null, null);
	}
	public void ClearRecentlyViewedDateStatistics(SQLiteDatabase db){
		ContentValues cv = new ContentValues();
    	cv.put(Song.Names.RecentlyViewedDate, new Date().getTime());   	
    	db.update(Song.Names.TableName, cv, null, null);
	}
    
    public void copyDataBase() throws IOException{
    	
    	InputStream myInput = context.getAssets().open(DB_NAME);
    	 
    	String outFileName = DB_PATH + DB_NAME;
    	
    	File file = new File(DB_PATH);
    	file.mkdirs();
    	
    	OutputStream myOutput = new FileOutputStream(outFileName);
    	
    	
    	
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
    	
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    }
    
    
    public boolean checkDataBase(){
    	 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
    /*
    //below need for extra tancy s bubnom
    public void createDataBase() throws IOException{
    	 
	    	boolean dbExist = checkDataBase();
	 
	    	if(dbExist){
	    		//do nothing - database already exist
	    	}else{
	    		//By calling this method and empty database will be created into the default system path
	            //of your application so we are gonna be able to overwrite that database with our database.
	        	
	    		this.getReadableDatabase();
	 
	        	try {
	 
	        		copyDataBase();
	 
	    		} catch (IOException e) {
	 
	        		throw new Error("Error copying database");
	 
	        	}
	    	}
	 
	    }
 
    

    
    
    public void OpenDataBase() throws SQLException{
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
    */
    
}
