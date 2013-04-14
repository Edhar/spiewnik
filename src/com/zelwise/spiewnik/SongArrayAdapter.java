package com.zelwise.spiewnik;

import java.io.Serializable;
import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SongArrayAdapter extends ArrayAdapter<Song> {
	private ArrayList<Song> songList;
	private SearchTerms searchTerms;
	private AppManager manager;
	private Boolean currentPageIsLast = false;

	public Boolean HasSongs() {
		return songList.size() > 0;
	}

	public Boolean HasNextPage() {
		return !currentPageIsLast;
	}

	public void AddAdditionalPage() {
		if (!currentPageIsLast) {
			this.searchTerms.CurrentPage(this.searchTerms.CurrentPage() + 1);
			ArrayList<Song> addSongs = GetSongData(this.manager.db,
					this.searchTerms);
			if (addSongs.size() > 0) {
				songList.addAll(addSongs);
				this.notifyDataSetChanged();
				currentPageIsLast = addSongs.size() < searchTerms
						.SongsPerPage();
			} else {
				currentPageIsLast = true;
			}
		}
	}

	public void Reload() {
		songList.clear();

		for (int i = 0; i < this.searchTerms.CurrentPage(); i++) {
			ArrayList<Song> addSongs = GetSongData(this.manager.db,
					this.searchTerms);
			currentPageIsLast = addSongs.size() < searchTerms.SongsPerPage();
			songList.addAll(addSongs);
		}

		this.notifyDataSetChanged();
	}

	public SongArrayAdapter(AppManager manager, SearchTerms searchTerms) {
		this(manager, GetSongData(manager.db, searchTerms), searchTerms);
	}

	public SongArrayAdapter(AppManager manager, ArrayList<Song> list,
			SearchTerms searchTerms) {
		this(manager, searchTerms, list, R.layout.song_list_item);
	}

	public SongArrayAdapter(AppManager manager, SearchTerms searchTerms,
			ArrayList<Song> list, int layout) {
		super(manager.context, layout, list);
		this.searchTerms = searchTerms;
		this.songList = list;
		this.manager = manager;
		this.currentPageIsLast = list.size() < searchTerms.SongsPerPage();
	}

	private static ArrayList<Song> GetSongData(SQLiteDatabase db,
			SearchTerms terms) {
		return Song.GetSongs(db, terms);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = super.getView(position, convertView, parent);

		if (position % 2 == 1) {
			view.setBackgroundColor(Color.WHITE);
		} else {
			view.setBackgroundColor(Color.parseColor("#e0eaf1"));
		}

		Song curSong = (Song) songList.get(position);
		if (curSong != null) {
			TextView tv = (TextView) view
					.findViewById(R.id.SongListItemTextView);
			if (tv != null) {
				String text = "";
				if (searchTerms.SeachByAndShowSongNumbersInResult()) {
					text = String.format("%5s.", curSong.Id())
							.replace(" ", "o") + " " + curSong.Title();
				} else {
					text = curSong.Title();
				}

				if (searchTerms.SearchText().length() > 0) {
					tv.setText(Html.fromHtml(text.replaceAll("(?i)("
							+ searchTerms.SearchText() + ")", "<b>$1</b>")));
				} else {
					tv.setText(text);
				}

				// SimpleDateFormat iso8601Format = new
				// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				// tv.setText(curSong.Title() + "; Date:" +
				// iso8601Format.format(curSong.RecentlyViewedDate()) +
				// "; Rating:" + curSong.Rating());
			}
		}

		return view;
	}
}
