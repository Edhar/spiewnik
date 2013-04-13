package com.zelwise.spiewnik;

public class SearchTerms {
	
	private SearchBy searchBy = SearchBy.Text;
	public SearchBy SearchBy(){
		return searchBy;
	};
	
	private String searchText = "";
	public String SearchText(){
		return searchText;
	};
	
	private Integer songsPerPage = SettingsHelper.DefaultValues.SongPerPage;
	public Integer SongsPerPage(){
		return songsPerPage;
	};
	
	private String orderByString = "";
	public String OrderByString(){
		return orderByString;
	};
	
	private Boolean seachByAndShowSongNumbersInResult = SettingsHelper.DefaultValues.SeachByAndShowSongNumbersInResult;
	public Boolean SeachByAndShowSongNumbersInResult(){
		return seachByAndShowSongNumbersInResult;
	};
	
	private int currentPage = 1;
	public int CurrentPage(){
		return currentPage;
	};
	public void CurrentPage(Integer newValue){
		currentPage=newValue;
	};
	
	public SearchTerms(SettingsHelper settings,SearchBy searchBy,String searchText,String orderByString){
		this(searchBy,searchText, settings.SongPerPage(), 1,orderByString,settings.SeachByAndShowSongNumbersInResult());
	}
	
	public SearchTerms(SettingsHelper settings,SearchBy searchBy,String searchText,int currentPage,String orderByString){
		this(searchBy,searchText, settings.SongPerPage(), currentPage,orderByString,settings.SeachByAndShowSongNumbersInResult());
	}

	public SearchTerms(SearchBy searchBy,String searchText, Integer songsPerPage,Integer currentPage,String orderByString,Boolean seachByAndShowSongNumbersInResult){
		this.searchBy = searchBy;
		this.searchText = searchText.trim();
		this.songsPerPage = songsPerPage;
		this.orderByString = orderByString;
		this.seachByAndShowSongNumbersInResult = seachByAndShowSongNumbersInResult;
		this.currentPage = currentPage;
	}
}
