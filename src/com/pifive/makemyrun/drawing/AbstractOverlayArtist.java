package com.pifive.makemyrun.drawing;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Abstract class to setup default paint for OverlayArtists
 */
public abstract class AbstractOverlayArtist implements OverlayArtist{
	
	protected Paint paint = new Paint();
	
	/**
	 * Sets up initial paint to use.
	 */
	public AbstractOverlayArtist() {
		setupMapPaint();
	}
	
	/**
	 * Sets up a simple paint to minimize code repetition
	 */
	protected void setupMapPaint() {
		paint.setDither(true);
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(5);
	}

}
