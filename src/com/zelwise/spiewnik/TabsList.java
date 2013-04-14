package com.zelwise.spiewnik;

import java.util.ArrayList;

import android.content.Context;

public class TabsList {
	private Context context;

	public static final String Tag = "ListTabs";

	public TabsItem SiteRating;
	public TabsItem Recent;
	public TabsItem Often;

	public ArrayList<TabsItem> GetTabsItems() {
		ArrayList<TabsItem> list = new ArrayList<TabsItem>();
		list.add(SiteRating);
		list.add(Recent);
		list.add(Often);
		return list;
	}

	public TabsItem Get(Integer Id) {
		for (TabsItem lang : GetTabsItems()) {
			if (lang.Id() == Id) {
				return lang;
			}
		}

		return new TabsItem(-1, "");
	}

	public TabsList(Context context) {
		this.context = context;
		SiteRating = new TabsItem(0, this.context.getResources().getString(
				R.string.buttons_SiteRating));
		Recent = new TabsItem(1, this.context.getResources().getString(
				R.string.buttons_Recent));
		Often = new TabsItem(2, this.context.getResources().getString(
				R.string.buttons_Often));
	}
}

