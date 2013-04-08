package com.zelwise.spiewnik;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SongArrayAdapter<T> extends ArrayAdapter<T>{  
	private ArrayList<T> items;
	
	public SongArrayAdapter(Context context, int layout, ArrayList<T> list) {  
		super(context, layout, list);
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
			//view.setBackgroundColor(Color.parseColor("#EAFCFF"));
			//view.setBackgroundColor(Color.parseColor("#CBFFF9"));
			view.setBackgroundColor(Color.parseColor("#e0eaf1"));
		}
		
		Song curSong = (Song) items.get(position);
        if (curSong != null) {
                TextView tv = (TextView)view.findViewById(R.id.SongListItemTextView);
                if (tv != null) {
                	tv.setText(curSong.Title());
                	//SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                	//tv.setText(curSong.Title() + "; Date:" + iso8601Format.format(curSong.RecentlyViewedDate()) + "; Rating:" + curSong.Rating());
                }
        }
        
		return view;
   }
}
