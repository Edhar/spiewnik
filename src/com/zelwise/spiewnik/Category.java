package com.zelwise.spiewnik;

import java.util.ArrayList;

import org.jsoup.nodes.Document;

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
