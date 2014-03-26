package com.zelwise.spiewnik;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.nodes.Document;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Song implements Serializable{
	private static final long serialVersionUID = 422705449454356L;

	public static class Names {
		public static final String TableName = "Song";
		public static final String Id = "Id";
		public static final String SiteId = "SiteId";
		public static final String CategoryId = "CategoryId";
		public static final String Title = "Title";
		public static final String Favorite = "Favorite";
		public static final String Content = "Content";
		public static final String Rating = "Rating";
		public static final String SiteRating = "SiteRating";
		public static final String RecentlyViewedDate = "RecentlyViewedDate";
	}

	private Integer id = 0;

	public Integer Id() {
		return id;
	}

	private Integer siteId = 0;

	public Integer SiteId() {
		return siteId;
	}

	private Integer categoryId = 0;

	public Integer CategoryId() {
		return categoryId;
	}

	private String title = "";

	public String Title() {
		return title;
	}

	public void Title(String newtitle) {
		title = newtitle;
	}

	private String content = "";

	public String Content() {
		return content;
	}

	public void Content(String newContent) {
		content = newContent;
	}

	private Integer rating = 0;

	public Integer Rating() {

		return rating;
	}

	public void Rating(Integer newRatingValue) {
		rating = newRatingValue;
	}

	private Boolean favorite = false;

	public Boolean Favorite() {

		return favorite;
	}

	public void Favorite(Boolean isFavorite) {
		favorite = isFavorite;
	}

	private Integer siteRating = 0;

	public Integer SiteRating() {

		return siteRating;
	}

	private Date recentlyViewedDate = new Date();

	public Date RecentlyViewedDate() {
		return recentlyViewedDate;
	}

	public void RecentlyViewedDate(Date newDate) {
		recentlyViewedDate = newDate;
	}

	public Song() {

	}
	
	public Song(String title,String content,Integer siteRating,Date recentlyViewedDate) {
		this.title = title;
		this.content = content;
		this.siteRating = siteRating;
		this.recentlyViewedDate = recentlyViewedDate;
	}

	private Song(Integer id, Integer siteId, Integer categoryId, String title,
			String content, Integer rating, Integer siteRating,
			Date recentlyViewedDate, Boolean favorite) {
		this.id = id;
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.title = title;
		this.content = content;
		this.rating = rating;
		this.siteRating = siteRating;
		this.recentlyViewedDate = recentlyViewedDate;
		this.favorite = favorite;
	}

	public Song(Document doc, Integer siteID) {
		siteId = siteID;
		ParseDocument(doc);
	}

	private void ParseDocument(Document document) {
		title = document.select("#okno_glowne center h2").html().toString();
		title = StringEscapeUtils.unescapeHtml4(title);
		title = Utils.ToTitleCase(title);

		content = document.select("#okno_glowne pre").html().toString();
		content = StringEscapeUtils.unescapeHtml4(content);

		String ratingStr = "";
		ratingStr = document
				.select("#okno_glowne tbody tr small:contains(po raz)").html()
				.toString();
		Log.d("ratingStr", ratingStr);

		if (ratingStr.indexOf(":") < 0) {
			ratingStr = "0";
		} else {
			ratingStr = ratingStr.substring(ratingStr.indexOf(":") + 1).trim();
			ratingStr = ratingStr.split(" ")[0];
		}

		ratingStr = ratingStr.trim();
		siteRating = Integer.valueOf(ratingStr);
	}

	@Override
	public String toString() {
		return Names.Id + " - " + this.Id() + "; " + Names.Title + " - "
				+ this.Title() + "; " + Names.SiteId + " - " + this.SiteId()
				+ ";";
	}

	public void SaveOrUpdate(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();

		cv.put(Names.SiteId, this.SiteId());
		cv.put(Names.CategoryId, this.CategoryId());
		cv.put(Names.Title, this.Title().trim());
		cv.put(Names.Content, this.Content().trim());
		cv.put(Names.Rating, this.Rating());
		cv.put(Names.SiteRating, this.SiteRating());
		cv.put(Names.RecentlyViewedDate, this.RecentlyViewedDate().getTime());
		cv.put(Names.Favorite, this.Favorite() ? 1 : 0);

		if (this.Id() == 0) {
			db.insert(Names.TableName, null, cv);
		} else {
			db.update(Names.TableName, cv, Names.Id + "=" + this.Id(), null);
		}
	}

	public void SaveOrUpdateForDownloadFromSite(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();

		cv.put(Names.SiteId, this.SiteId());
		cv.put(Names.CategoryId, this.CategoryId());
		cv.put(Names.Title, this.Title());
		cv.put(Names.Content, this.Content());
		cv.put(Names.Rating, this.Rating());
		cv.put(Names.SiteRating, this.SiteRating());
		cv.put(Names.RecentlyViewedDate, this.RecentlyViewedDate().getTime());
		cv.put(Names.Favorite, this.Favorite() ? 1 : 0);

		if (this.Id() == 0 && !AlreadyDownloaded(this.SiteId(), db)) {
			db.insert(Names.TableName, null, cv);
		} else {
			db.update(Names.TableName, cv, Names.SiteId + "=" + this.SiteId()
					+ " AND " + Names.Id + "=" + this.Id(), null);
		}
	}

	public static Song Get(SQLiteDatabase db, Integer Id) {
		Song song = new Song();
		try {
			Cursor cursor = db.query(false, Names.TableName, new String[] {
					Names.Id, Names.SiteId, Names.CategoryId, Names.Title,
					Names.Content, Names.Rating, Names.SiteRating,
					Names.RecentlyViewedDate, Names.Favorite }, Names.Id + "="
					+ Id, null, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				Integer id = cursor.getInt(0);
				Integer siteId = cursor.getInt(1);
				Integer categoryId = cursor.getInt(2);
				String title = cursor.getString(3);
				String content = cursor.getString(4);
				Integer rating = cursor.getInt(5);
				Integer siteRating = cursor.getInt(6);
				Date recentlyViewd = new Date(cursor.getLong(7));
				Boolean favorite = cursor.getString(8).equals("1");

				song = new Song(id, siteId, categoryId, title, content, rating,
						siteRating, recentlyViewd, favorite);
			}

			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		} catch (Exception e) {
		}

		return song;
	}

	public boolean AlreadyDownloaded(int SiteId, SQLiteDatabase db) {
		try {
			Cursor mCursor = db.query(false, Names.TableName,
					new String[] { Names.SiteId }, Names.SiteId + "=" + SiteId,
					null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}

			Integer count = mCursor.getCount();

			if (mCursor != null && !mCursor.isClosed()) {
				mCursor.close();
			}

			return count != 0;
		} catch (Exception e) {
		}

		return false;
	}

	public static int AllSongsCount(SQLiteDatabase db) {
		Integer count = 0;
		String selection = Names.Title + " != ?";
		String[] selectionArgs = new String[] { "" };
		Cursor cursor = db.query(Names.TableName, new String[] { Names.Id },
				selection, selectionArgs, null, null, null, null);

		count = cursor.getCount();
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return count;
	}

	public static int AllSongsCountWithEmpty(SQLiteDatabase db) {
		Integer count = 0;
		Cursor cursor = db.query(Names.TableName, null, null, null, null, null,
				null);
		count = cursor.getCount();
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return count;
	}

	public void Delete(SQLiteDatabase db) {
		Delete(db, this.Id());
	}

	public static void Delete(SQLiteDatabase db, Integer Id) {
		if (Id != 0) {
			db.delete(Names.TableName, Names.Id + "=" + Id, null);
		}
	}

	public static ArrayList<Song> GetSongs(SQLiteDatabase db, SearchTerms terms) {

		ArrayList<Song> list = new ArrayList<Song>();
		String searchTextLIKE = "%" + terms.SearchText() + "%";
		String orderByTitle = Names.Title + DBHelper.SortAscending;

		String selection = Names.Title + " != ?";
		String[] selectionArgs = new String[] { "" };
		String orderBy = "";

		if (terms.SearchBy() == SearchBy.Text) {
			if (terms.SearchText().length() > 0) {
				selection = selection + " AND " + Names.Title + " LIKE ? ";
				selectionArgs = new String[] { "", searchTextLIKE, };
				if (terms.DoMoreRelevantSearch()) {
					orderBy = " CASE WHEN " + Names.Title + " LIKE '"
							+ terms.SearchText() + "%' THEN 0 ELSE 1 END";
				} else {
					orderBy = orderByTitle;
				}
			}

			if (terms.SeachByAndShowSongNumbersInResult()
					&& terms.SearchText().length() > 0) {
				selection = Names.Title + " != ? AND (" + Names.Id
						+ " LIKE ? OR " + Names.Title + " LIKE ?) ";
				selectionArgs = new String[] { "", searchTextLIKE,
						searchTextLIKE };

				if (terms.DoMoreRelevantSearch()) {
					orderBy = " CASE WHEN " + Names.Id + " LIKE '"
							+ terms.SearchText() + "%' THEN 0 ELSE 1 END" + ","
							+ " CASE WHEN " + Names.Title + " LIKE '"
							+ terms.SearchText() + "%' THEN 0 ELSE 1 END";
				} else {
					orderBy = Names.Id + DBHelper.SortAscending + ","
							+ orderByTitle;
				}
			}
		} else {
			orderBy = terms.OrderByString() + "," + orderByTitle;
		}

		if (terms.SearchBy() == SearchBy.Favorite) {
			selection = Names.Favorite + " == ? AND " + selection;
			String[] old = selectionArgs.clone();
			selectionArgs = new String[old.length + 1];
			selectionArgs[0] = "1";
			for (int i = 0; i < old.length; i++) {
				selectionArgs[i + 1] = old[i];
			}
		} else if (terms.SearchBy() == SearchBy.Rating) {
			selection = Names.Rating + " != ? AND " + selection;
			String[] old = selectionArgs.clone();
			selectionArgs = new String[old.length + 1];
			selectionArgs[0] = "0";
			for (int i = 0; i < old.length; i++) {
				selectionArgs[i + 1] = old[i];
			}
		} else if (terms.SearchBy() == SearchBy.RecentlyViewedDate) {
			selection = Names.RecentlyViewedDate + " != ? AND " + selection;
			String[] old = selectionArgs.clone();
			selectionArgs = new String[old.length + 1];
			selectionArgs[0] = "0";
			for (int i = 0; i < old.length; i++) {
				selectionArgs[i + 1] = old[i];
			}
		}

		String limit = ((terms.CurrentPage() - 1) * terms.SongsPerPage()) + ","
				+ terms.SongsPerPage().toString();

		Cursor cursor = db.query(false, Names.TableName, new String[] {
				Names.Id, Names.SiteId, Names.CategoryId, Names.Title,
				Names.Content, Names.Rating, Names.SiteRating,
				Names.RecentlyViewedDate, Names.Favorite }, selection,
				selectionArgs, null, null, orderBy, limit);

		if (cursor.moveToFirst()) {
			do {
				Integer id = cursor.getInt(0);
				Integer siteId = cursor.getInt(1);
				Integer categoryId = cursor.getInt(2);
				String title = cursor.getString(3);
				String content = cursor.getString(4);
				Integer rating = cursor.getInt(5);
				Integer siteRating = cursor.getInt(6);
				Date recentlyViewd = new Date(cursor.getLong(7));
				Boolean favorite = cursor.getString(8).equals("1");

				Song song = new Song(id, siteId, categoryId, title, content,
						rating, siteRating, recentlyViewd, favorite);
				list.add(song);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}
	
	public static ArrayList<Song> GetSongs(SQLiteDatabase db) {
		ArrayList<Song> list = new ArrayList<Song>();
		
		Cursor cursor = db.query(false, Names.TableName, new String[] {
				Names.Id, Names.SiteId, Names.CategoryId, Names.Title,
				Names.Content, Names.Rating, Names.SiteRating,
				Names.RecentlyViewedDate, Names.Favorite }, null,
				null, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			do {
				Integer id = cursor.getInt(0);
				Integer siteId = cursor.getInt(1);
				Integer categoryId = cursor.getInt(2);
				String title = cursor.getString(3);
				String content = cursor.getString(4);
				Integer rating = cursor.getInt(5);
				Integer siteRating = cursor.getInt(6);
				Date recentlyViewd = new Date(cursor.getLong(7));
				Boolean favorite = cursor.getString(8).equals("1");

				Song song = new Song(id, siteId, categoryId, title, content,
						rating, siteRating, recentlyViewd, favorite);
				list.add(song);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return list;
	}
}
