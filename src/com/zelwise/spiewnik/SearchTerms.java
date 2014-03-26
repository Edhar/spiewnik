package com.zelwise.spiewnik;

import java.io.Serializable;

public class SearchTerms implements Cloneable, Serializable {
	private static final long serialVersionUID = -6533427856451164323L;

	public Integer songsPerPage = SettingsHelper.DefaultValues.SongsPerPage;

	public Integer SongsPerPage() {
		return songsPerPage;
	};

	public Boolean seachByAndShowSongNumbersInResult = SettingsHelper.DefaultValues.SeachByAndShowSongNumbersInResult;

	public Boolean SeachByAndShowSongNumbersInResult() {
		return seachByAndShowSongNumbersInResult;
	};

	public Boolean doMoreRelevantSearch = SettingsHelper.DefaultValues.DoMoreRelevantSearch;

	public Boolean DoMoreRelevantSearch() {
		return doMoreRelevantSearch;
	};

	private String orderByString = "";

	public String OrderByString() {
		return orderByString;
	};

	private SearchBy searchBy = SearchBy.Text;

	public SearchBy SearchBy() {
		return searchBy;
	};

	private String searchText = "";

	public String SearchText() {
		if (SearchBy() != SearchBy.Text) {
			return "";
		}
		return searchText;
	};

	private int currentPage = 1;

	public int CurrentPage() {
		return currentPage;
	};

	public void CurrentPage(Integer newValue) {
		currentPage = newValue;
	};

	public SearchTerms(SettingsHelper settings, SearchBy searchBy, String searchText, String orderByString) {
		this(settings, searchBy, searchText, 1, orderByString);
	}

	public SearchTerms(SettingsHelper settings, SearchBy searchBy, String searchText, Integer currentPage, String orderByString) {
		this.searchBy = searchBy;
		this.searchText = searchText.trim();
		this.orderByString = orderByString;
		this.currentPage = currentPage;

		this.songsPerPage = settings.SongsPerPage();
		this.seachByAndShowSongNumbersInResult = settings.SeachByAndShowSongNumbersInResult();
		this.doMoreRelevantSearch = settings.DoMoreRelevantSearch();
	}

	public SearchTerms(int songsPerPage, Boolean seachByAndShowSongNumbersInResult, Boolean doMoreRelevantSearch, SearchBy searchBy, String searchText, Integer currentPage, String orderByString) {
		this.searchBy = searchBy;
		this.searchText = searchText.trim();
		this.orderByString = orderByString;
		this.currentPage = currentPage;
		this.songsPerPage = songsPerPage;
		this.seachByAndShowSongNumbersInResult = seachByAndShowSongNumbersInResult;
		this.doMoreRelevantSearch = doMoreRelevantSearch;
	}

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public SearchTerms Clone() {
		try {
			return (SearchTerms) clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
