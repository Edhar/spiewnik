package com.zelwise.spiewnik;

import java.util.ArrayList;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SongArrayAdapter<T> extends ArrayAdapter<T> {
	private ArrayList<T> items;
	private AppManager manager;

	public SongArrayAdapter(AppManager manager, int layout, ArrayList<T> list) {
		super(manager.context, layout, list);
		this.manager = manager;
		items = list;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override  
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = super.getView(position, convertView, parent);
		
		if(position %2 == 1){
			view.setBackgroundColor(Color.WHITE);
		}
		else {
			view.setBackgroundColor(Color.parseColor("#e0eaf1"));
		}
		
		Song curSong = (Song) items.get(position);
        if (curSong != null) {
                TextView tv = (TextView)view.findViewById(R.id.SongListItemTextView);
                if (tv != null) {
                	if(manager.settings.SeachByAndShowSongNumbersInResult()){
                		tv.setText(String.format("%5s. ", curSong.Id()) + ". " + curSong.Title());
                	}else{
                		tv.setText(curSong.Title());
                	}
                	
                	//SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                	//tv.setText(curSong.Title() + "; Date:" + iso8601Format.format(curSong.RecentlyViewedDate()) + "; Rating:" + curSong.Rating());
                }
        }
        
		return view;
   }
}
