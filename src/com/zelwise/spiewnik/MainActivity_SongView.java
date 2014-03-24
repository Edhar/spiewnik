package com.zelwise.spiewnik;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity_SongView extends MainActivity_Ext {
	public MainActivity_SongView(MainActivity mainActivity) {
		super(mainActivity);
	}

	EditText songTitleEditText, songContentEditText;	
	LinearLayout magnifierLinearLayout;
	
	public static final Integer SongViewIndex = 2;
	
	private Boolean isSongEditMode = false;
	public Boolean IsSongEditMode() {
		return isSongEditMode;
	}	
	public void IsSongEditMode(Boolean newState) {
		isSongEditMode = newState;
	}
	
	protected long lastMagnifierShowTime;
	Handler magnifierHandler = new Handler();
	protected Runnable updateMagnifierState = new Runnable() {
		public void run() {
			long curTime = new Date().getTime();
			if (curTime - lastMagnifierShowTime > SettingsHelper.DefaultValues.MagnifiedShowTime) {
				magnifierLinearLayout.setVisibility(View.GONE);
			} else {
				magnifierHandler.postDelayed(updateMagnifierState,SettingsHelper.DefaultValues.MagnifiedShowTime);
			}

		}
	};
	protected void ShowMagnifier() {
		magnifierLinearLayout.setVisibility(View.VISIBLE);
		magnifierHandler.postDelayed(updateMagnifierState,SettingsHelper.DefaultValues.MagnifiedShowTime);

		lastMagnifierShowTime = new Date().getTime();
	}	

	protected OnTouchListener onTouchListenerMagnifier = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView btn = (ImageView) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				btn.setBackgroundColor(MainAct.manager.context.getResources().getColor(R.color.theme_violet_bg_PressedButton));
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				btn.setBackgroundColor(MainAct.manager.context.getResources().getColor(R.color.theme_TransparentColor));
				v.performClick();
			}
			return true;
		}
	};
	
	protected void ChangeFontSize(Boolean isEnlarge) {
		float currentSize = Utils.pixelsToSp(MainAct.manager.context,songContentEditText.getTextSize());
		float newSize = currentSize;
		if (isEnlarge) {
			newSize += SettingsHelper.DefaultValues.FontSizeMagnifierStep;
			if (newSize > SettingsHelper.DefaultValues.FontSizeMax()) {
				newSize = SettingsHelper.DefaultValues.FontSizeMax();
			}
		} else {
			newSize -= SettingsHelper.DefaultValues.FontSizeMagnifierStep;
			if (newSize < SettingsHelper.DefaultValues.FontSizeMin()) {
				newSize = SettingsHelper.DefaultValues.FontSizeMin();
			}
		}

		final Toast toast = Toast.makeText(MainAct.manager.context, newSize / SettingsHelper.DefaultValues.FontSize + "x", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				toast.cancel();
			}
		}, 300);

		MainAct.manager.settings.FontSize(newSize);
		songContentEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
	}
	
	protected void SetSongEditMode() {
		songTitleEditText.setVisibility(View.VISIBLE);
		songTitleEditText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		songTitleEditText.setCursorVisible(true);
		songTitleEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

		songContentEditText.setCursorVisible(true);
		songContentEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		songContentEditText.setSingleLine(false);

		songContentEditText.setEnabled(true);

		IsSongEditMode(true);

	}

	protected void SetSongViewViewMode() {
		View songView = MainAct.manager.GetViewPage(SongViewIndex);
		ScrollView songScroll = (ScrollView) songView.findViewById(R.id.SongContentScrollView);
		songScroll.scrollTo(0, 0);

		songTitleEditText.setVisibility(View.GONE);
		songTitleEditText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0));
		songTitleEditText.setCursorVisible(false);
		songTitleEditText.setInputType(InputType.TYPE_NULL);

		songContentEditText.setCursorVisible(false);
		songContentEditText.setInputType(InputType.TYPE_NULL);
		songContentEditText.setSingleLine(false);

		songContentEditText.setEnabled(true);

		IsSongEditMode(false);
	}
	
	protected void UpdateContenSongView(Song newSong) {
		try {
			View songView = MainAct.manager.GetViewPage(MainActivity_SongView.SongViewIndex);

			TextView newId = (TextView) songView.findViewById(R.id.SongIdTextView);

			newId.setText(newSong.Id().toString());
			songTitleEditText.setText(newSong.Title());
			songContentEditText.setText(newSong.Content());

			Activity activity = (Activity) MainAct.manager.context;

			if (MainAct.manager.viewPager.getCurrentItem() == MainActivity_SongView.SongViewIndex) {
				try {
					if (newSong.Title().length() != 0) {
						activity.setTitle(newSong.Title());
					} else {
						activity.setTitle(MainAct.getResources().getString(R.string.app_name));
					}
					;
				} catch (Exception e) {
					activity.setTitle(MainAct.getResources().getString(R.string.app_name));
				}
			}

		} catch (Exception e) {

		}

	}
	

	protected Song UpdateSongFromEditMode(Song originalSong) {
		try {
			if (IsSongEditMode()) {
				String content = songContentEditText.getText().toString().trim();
				String title = songTitleEditText.getText().toString().trim();
				if (title.length() == 0) {
					title = Utils.Trim(content);
				}

				originalSong.Title(title);
				originalSong.Content(content);
			}
		} catch (Exception e) {

		}

		return originalSong;
	}
	
	protected void CheckIfSongEditModeOrContentSaved() {
		if (IsSongEditMode()) {
			final Song curSong = GetSongFromSongView();

			if (curSong.Title().length() != 0 || curSong.Content().length() != 0) {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainAct.manager.context);

				alertBuilder.setIcon(0);
				alertBuilder.setTitle(MainAct.manager.context.getResources().getString(R.string.labels_WouldYouLikeToSaveThisSong));
				alertBuilder.setMessage("\"" + curSong.Title() + "\"");
				alertBuilder.setCancelable(false);
				alertBuilder.setPositiveButton(MainAct.manager.context.getResources().getString(R.string.buttons_Yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						curSong.SaveOrUpdate(MainAct.manager.db);
						IsSongEditMode(false);
						UpdateContenSongView(curSong);
						SetSongViewViewMode();
						MainAct.mainActivity_SearchView.ReloadSongsListContent();
					}
				});
				alertBuilder.setCancelable(false);
				alertBuilder.setNegativeButton(MainAct.manager.context.getResources().getString(R.string.buttons_No), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (curSong.Id() != 0) {
							Song savedSong = Song.Get(MainAct.manager.db, curSong.Id());
							UpdateContenSongView(savedSong);
						}
						IsSongEditMode(false);
						SetSongViewViewMode();
						dialog.cancel();
					}
				});
				AlertDialog alert = alertBuilder.show();
				TextView messageText = (TextView) alert.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				messageText.setTypeface(Typeface.DEFAULT_BOLD);
				alert.show();
			}
		}
	}

	protected Song GetSongFromSongView() {
		Song song = new Song();

		try {
			View songView = MainAct.manager.GetViewPage(MainActivity_SongView.SongViewIndex);
			TextView songId = (TextView) songView.findViewById(R.id.SongIdTextView);
			Integer id = Utils.ToInt(songId.getText().toString(), 0);
			if (id != 0) {
				song = Song.Get(MainAct.manager.db, id);
			}
		} catch (Exception e) {
		}

		return UpdateSongFromEditMode(song);
	}

	protected void AddNewSongMenuAction() {
		if (MainAct.manager.viewPager.getCurrentItem() != MainActivity_SongView.SongViewIndex) {
			MainAct.manager.viewPager.setCurrentItem(MainActivity_SongView.SongViewIndex);
		}

		UpdateContenSongView(new Song());
		SetSongEditMode();
	}
	
	@Override
	protected void onCreate() {
		View songView = MainAct.manager.GetViewPage(SongViewIndex);
		songTitleEditText = (EditText) songView.findViewById(R.id.SongTitleEditText);
		songContentEditText = (EditText) songView.findViewById(R.id.SongContentEditText);
		songContentEditText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!IsSongEditMode()) {
					ShowMagnifier();
				}
			}
		});
		ScrollView songContentScrollView = (ScrollView) songView.findViewById(R.id.SongContentScrollView);
		songContentScrollView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (!IsSongEditMode()) {
						ShowMagnifier();
					}
				}
				/*if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return false;
				}*/

				return false;
			}
		});
		
		ImageView magnifierMinus = (ImageView) songView.findViewById(R.id.MagnifierMinusImageView);
		ImageView magnifierPlus = (ImageView) songView.findViewById(R.id.MagnifierPlusImageView);
		magnifierMinus.setOnTouchListener(onTouchListenerMagnifier);
		magnifierPlus.setOnTouchListener(onTouchListenerMagnifier);
		magnifierMinus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChangeFontSize(false);
				ShowMagnifier();
			}
		});
		magnifierPlus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChangeFontSize(true);
				ShowMagnifier();
			}
		});
		magnifierLinearLayout = (LinearLayout) songView.findViewById(R.id.MagnifierLinearLayout);

	}
}
