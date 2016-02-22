package com.ubirouting.ubimapdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.ubirouting.ubimaplib.Loader;
import com.ubirouting.ubimaplib.UbiMapDownloadListener;
import com.ubirouting.ubimaplib.data.UbiMapDownloader;

public class MainActivity extends Activity implements OnClickListener {
	private Button mMapDownloadBtn, mEnterMapBtn;
	private UbiMapDownloader mMapDownloader;
	private ProgressDialog mDownloadDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mMapDownloadBtn = (Button) findViewById(R.id.start_map_btn);
		mEnterMapBtn = (Button) findViewById(R.id.enter_map_btn);
		mMapDownloadBtn.setOnClickListener(this);
		mEnterMapBtn.setOnClickListener(this);

		// Should be invoked ahead of all other UbiMap API.
		Loader.loadWindowParas(this);

		// Try to download resource file which contains the necessary texture
		// for map rendering. If resource file has been already download, then
		// UbiMapDownloader won't download unless there is an new version of
		// resource file.
		// **IMPORTANT** : SHOULD BE INVOKED IN UI THREAD.
		mMapDownloader = new UbiMapDownloader();
		mMapDownloader.tryDownloadRes(new UbiMapDownloadListener() {

			// If no resource file should be downloaded.
			@Override
			public void onNoNeedDownload() {
				// Usually do nothing here
			}

			// Downloading failed, may caused by network problem.
			@Override
			public void onFailed(final String arg0) {
				Toast.makeText(MainActivity.this, "download failed because of " + arg0, Toast.LENGTH_LONG).show();
				if (mDownloadDialog != null)
					mDownloadDialog.dismiss();

			}

			// On downloading, and 'percent' indicates the process of
			// downloading.
			@Override
			public void onDownloading(final float percent) {
				mDownloadDialog.setMessage("downloading resource.." + (int) (percent * 100) + "%");
			}

			// On download succeeds.
			@Override
			public void onDownloadSuccess() {
				mDownloadDialog.dismiss();

			}

			// On download starts.
			@Override
			public void onDownloadStart() {
				if (mDownloadDialog != null)
					mDownloadDialog.dismiss();

				mDownloadDialog = new ProgressDialog(MainActivity.this);
				mDownloadDialog.setMessage("downloading resource..");
				mDownloadDialog.setCancelable(false);
				mDownloadDialog.show();

			}
		});

	}

	final long mapId = 1000361;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_map_btn:

			mMapDownloader.tryDownloadMap(mapId, new UbiMapDownloadListener() {

				@Override
				public void onNoNeedDownload() {
					// Usually do nothing
					Intent i = new Intent();
					i.putExtra("mapId", mapId);
					i.setClass(MainActivity.this, MapActivity.class);
					MainActivity.this.startActivity(i);
				}

				@Override
				public void onFailed(String arg0) {
					// Must check the NULL situation of mDownloadDialog, because
					// the
					// onDownloadStart() is
					// only invoked when there is a new version of map to be
					// downloading.
					if (mDownloadDialog != null)
						mDownloadDialog.dismiss();
					Toast.makeText(MainActivity.this, "download failed because of " + arg0, Toast.LENGTH_LONG).show();
				}

				@Override
				public void onDownloading(float percent) {
					mDownloadDialog.setMessage("downloading map.." + (int) (percent * 100) + "%");
				}

				@Override
				public void onDownloadSuccess() {
					mDownloadDialog.dismiss();

					Intent i = new Intent();
					i.putExtra("mapId", mapId);
					i.setClass(MainActivity.this, MapActivity.class);
					MainActivity.this.startActivity(i);
				}

				@Override
				public void onDownloadStart() {
					if (mDownloadDialog != null)
						mDownloadDialog.dismiss();

					mDownloadDialog = new ProgressDialog(MainActivity.this);
					mDownloadDialog.setMessage("downloading map..");
					mDownloadDialog.setCancelable(false);
					mDownloadDialog.show();

				}
			});

			break;
		case R.id.enter_map_btn:
			if (mMapDownloader.isMapFileReady(mapId)) {
				Intent i = new Intent();
				i.putExtra("mapId", mapId);
				i.setClass(MainActivity.this, MapActivity.class);
				MainActivity.this.startActivity(i);
			}
		}

	}

	private static void debug(String msg) {
		Log.d("UbiMapDemo", msg);
	}

}
