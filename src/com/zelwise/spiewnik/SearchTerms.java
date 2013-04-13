package com.zelwise.spiewnik;

public class SearchTerms {
	private SettingsHelper settings;

	public Integer SongsPerPage() {
		return settings.SongPerPage();
	};

	public Boolean SeachByAndShowSongNumbersInResult() {
		return settings.SeachByAndShowSongNumbersInResult();
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
		return searchText;
	};

	private int currentPage = 1;

	public int CurrentPage() {
		return currentPage;
	};

	public void CurrentPage(Integer newValue) {
		currentPage = newValue;
	};

	public SearchTerms(SettingsHelper settings, SearchBy searchBy,
			String searchText, String orderByString) {
		this(settings, searchBy, searchText, 1, orderByString);
	}

	public SearchTerms(SettingsHelper settings, SearchBy searchBy,
			String searchText, Integer currentPage, String orderByString) {
		this.searchBy = searchBy;
		this.searchText = searchText.trim();
		this.orderByString = orderByString;
		this.currentPage = currentPage;
		this.settings = settings;
	}
}
