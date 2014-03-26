package com.zelwise.spiewnik;

import java.io.Serializable;

public class AppState implements Serializable {
	private static final long serialVersionUID = -1658975405069863873L;
	// transient you don't want to serialize field
	public SearchTerms Terms;
	public int ViewIndex;
	public Song SongViewSong;
	public Boolean IsSongEditMode;
	public int FirstVisibleSongPosition;

	public AppState(SearchTerms terms, int activeViewIndex, Song songViewSong, Boolean isSongEditMode, int firstVisibleSongPosition) {
		this.Terms = terms;
		this.ViewIndex = activeViewIndex;
		this.IsSongEditMode = isSongEditMode;
		this.FirstVisibleSongPosition = firstVisibleSongPosition;
		this.SongViewSong = songViewSong;
	}
}
