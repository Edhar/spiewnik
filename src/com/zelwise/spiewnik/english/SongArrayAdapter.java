package com.zelwise.spiewnik.english;

import java.io.Serializable;
import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

	public SearchTerms GetSearchTerms() {
		return searchTerms;
	}

	public void AddAdditionalPage() {
		if (!currentPageIsLast) {
			this.searchTerms.CurrentPage(this.searchTerms.CurrentPage() + 1);
			ArrayList<Song> addSongs = GetCurrentPageSongData(this.manager.db,
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
		int pagesCount = this.searchTerms.CurrentPage();
		this.searchTerms.CurrentPage(0);
		for (int i = 0; i < pagesCount; i++) {
			this.AddAdditionalPage();
		}
	}

	public SongArrayAdapter(AppManager manager, SearchTerms searchTerms) {
		this(manager, GetSongData(manager.db, searchTerms), searchTerms);
	}

	public SongArrayAdapter(AppManager manager, ArrayList<Song> list,
			SearchTerms searchTerms) {
		this(manager, searchTerms, list, R.layout.song_list_item,
				R.id.SongListItemTextView);
	}

	public SongArrayAdapter(AppManager manager, SearchTerms searchTerms,
			ArrayList<Song> list, int layout, int textViewLayout) {
		super(manager.context, layout, textViewLayout, list);
		this.searchTerms = searchTerms;
		this.songList = list;
		this.manager = manager;
		this.currentPageIsLast = list.size() < searchTerms.SongsPerPage();
	}

	private static ArrayList<Song> GetSongData(SQLiteDatabase db,
			SearchTerms terms) {
		ArrayList<Song> list = new ArrayList<Song>();
		SearchTerms terms2 = terms.Clone();
		int pagesCount = terms2.CurrentPage();
		for (int i = 0; i < pagesCount; i++) {
			terms2.CurrentPage(i+1);
			list.addAll(GetCurrentPageSongData(db,terms2));
		}

		return list;
	}

	private static ArrayList<Song> GetCurrentPageSongData(SQLiteDatabase db,
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
			view.setBackgroundColor(Color.parseColor(manager.context.getResources().getString(R.string.theme_violet_bg_SongListItemTextView1)));
		} else {
			view.setBackgroundColor(Color.parseColor(manager.context.getResources().getString(R.string.theme_violet_bg_SongListItemTextView2)));
		}

		final Song curSong = (Song) songList.get(position);
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

			ImageView starImg = (ImageView) view
					.findViewById(R.id.SongListItemImageView);
			if (starImg != null) {
				if (curSong.Favorite()) {
					starImg.setTag(R.drawable.star1);
					starImg.setImageResource(R.drawable.star1);
				} else {
					starImg.setTag(R.drawable.star0);
					starImg.setImageResource(R.drawable.star0);
				}
				starImg.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ImageView img = (ImageView) v;
						Song song = Song.Get(manager.db, curSong.Id());
						if ((Integer) img.getTag() == R.drawable.star0) {
							img.setImageResource(R.drawable.star1);
							img.setTag(R.drawable.star1);
							curSong.Favorite(true);
							song.Favorite(true);
						} else {
							img.setImageResource(R.drawable.star0);
							img.setTag(R.drawable.star0);
							curSong.Favorite(false);
							song.Favorite(false);
						}
						curSong.SaveOrUpdate(manager.db);
					}

				});
			}
		}

		return view;
	}
}
