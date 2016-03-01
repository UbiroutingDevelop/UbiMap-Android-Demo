package com.ubirouting.ubimapdemo;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ubirouting.ubimaplib.UbiMapView;

public class PositionActivity extends Activity implements Runnable {

	// this will move the position around the out frame
	private static final float[] positionTest = { 174f, 1088f, 78f, 1182f, 879f, 2008f, 976f, 1909f, 782f, 1708f, 836f, 1656f };

	private int mPoistionIndex;

	private UbiMapView mMap;

	private Thread mTestThread;

	private volatile boolean mIsRun = false;

	private float mDegree;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_positionmap);

		Intent i = getIntent();
		long mapId = i.getLongExtra("mapId", -1);

		mMap = (UbiMapView) findViewById(R.id.mapview);
		mMap.load(mapId);

		mTestThread = new Thread(this);
		mIsRun = true;
		mTestThread.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mIsRun = false;
		mTestThread.interrupt();
	}

	@Override
	public void run() {
		while (mIsRun) {

			int p = mPoistionIndex % (positionTest.length / 2);
			mMap.refreshPosition(positionTest[p * 2], positionTest[p * 2 + 1]);
			mMap.refreshAngle(mDegree);

			mDegree += 30;

			mPoistionIndex++;
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
