package com.zelwise.spiewnik;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.content.Context;

public class ImageHelper {

	private Context context;

	public ImageHelper(Context context) {
		this.context = context;
	}

	public float ConvertDipToPixel(float size) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());
	}

	public float ConvertPixelToDip(float size) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, context.getResources().getDisplayMetrics());
	}

	public Drawable getResizedDrawable(int drawableId, int widthInPx, int heightInPx) {
		Drawable image = context.getResources().getDrawable(R.drawable.ic_action_search);
		Bitmap b = ((BitmapDrawable) image).getBitmap();
		Bitmap bitmapResized = Bitmap.createScaledBitmap(b, widthInPx, heightInPx, true);
		Drawable resizedImage = new BitmapDrawable(context.getResources(), bitmapResized);
		return resizedImage;
	}

	/************************ Resize Bitmap *********************************/
	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		int width = bm.getWidth();
		int height = bm.getHeight();

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();

		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

		return resizedBitmap;
	}
}
