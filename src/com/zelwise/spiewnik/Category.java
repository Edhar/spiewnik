package com.zelwise.spiewnik;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.nodes.Document;

import android.util.Log;

import com.zelwise.spiewnik.Song.Names;

public class Category {
	public static class Names {
		public static final String TableName = "Category";
		public static final String Id = "Id";
		public static final String Title = "Title";
		public static final String SiteCode = "SiteCode";
		public static final String MenuOrder = "MenuOrder";
		public static final String Internal = "Internal";
	}

	private Integer id = 0;

	public Integer Id() {
		return id;
	}

	private String title = "";

	public String Title() {
		return title;
	}

	public void Title(String newtitle) {
		title = newtitle;
	}

	private String siteCode = "";

	public String SiteCode() {
		return siteCode;
	}

	private Integer menuOrder = 0;

	public Integer MenuOrder() {
		return menuOrder;
	}

	public void MenuOrder(Integer newMenuOrder) {
		menuOrder = newMenuOrder;
	}

	private Boolean internal = false;

	public Boolean Internal() {
		return internal;
	}

	public void Internal(Boolean isInternal) {
		internal = isInternal;
	}

	public Category() {

	}

	private Category(Integer id, String title, String siteCode,
			Integer menuOrder, Boolean internal) {
		this.id = id;
		this.title = title;
		this.siteCode = siteCode;
		this.menuOrder = menuOrder;
		this.internal = internal;
	}
	
	public static ArrayList<Integer> GetSiteIds(Document document) {
		ArrayList<Integer> list = new ArrayList<Integer>();
			
		return list;
	}

	@Override
	public String toString() {
		return Names.Id + " - " + this.Id() + "; " + Names.Title + " - "
				+ this.Title() + "; ";
	}

}
