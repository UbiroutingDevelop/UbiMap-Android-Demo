package com.ubirouting.ubimapdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ubirouting.ubimaplib.Loader;
import com.ubirouting.ubimaplib.UbiMapDownloadListener;
import com.ubirouting.ubimaplib.data.UbiMapDownloader;

public class MainActivity extends Activity implements OnClickListener {
	private Button mMapBtn;
	private UbiMapDownloader mMapDownloader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mMapBtn = (Button) findViewById(R.id.start_map_btn);
		mMapBtn.setOnClickListener(this);
		Loader.loadWindowParas(this);

		mMapDownloader = new UbiMapDownloader();

		mMapDownloader.tryDownloadRes(new UbiMapDownloadListener() {

			@Override
			public void onNoNeedDownload() {
				Log.d("TAG", "onNoNeedDownload");
			}

			@Override
			public void onFailed(Exception arg0) {
				Log.d("TAG", arg0.getMessage());

			}

			@Override
			public void onDownloading(float arg0) {
				Log.d("TAG", "process is " + arg0);

			}

			@Override
			public void onDownloadSuccess() {
				Log.d("TAG", "onDownloadSuccess");

			}

			@Override
			public void onDownloadStart() {
				Log.d("TAG", "onDownloadStart");

			}
		});

	}

	@Override
	public void onClick(View v) {

	}

}
