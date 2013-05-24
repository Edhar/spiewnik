package com.zelwise.spiewnik;

public class AppState {
	public SearchTerms Terms;
	public int ViewIndex;
	public Song Song;
	public Boolean IsSongEditMode;
	public int FirstVisibleSongPosition;

	public AppState(SearchTerms terms, int activeViewIndex, Song activeSong, Boolean isSongEditMode,int firstVisibleSongPosition) {
		this.Terms = terms;
		this.ViewIndex = activeViewIndex;
		this.Song = activeSong;
		this.IsSongEditMode = isSongEditMode;
		this.FirstVisibleSongPosition = firstVisibleSongPosition;
	}
}
