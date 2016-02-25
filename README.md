UbiMap Demo
=====

![alt phone](http://ubirouting.com/imageUse/UbiMap.gif)

#1 准备工作

获取识途矢量地图SDK（获取SDK请联系 contact@ubirouting.com )

- ubimaplib_v1.3.jar 
- libubimap.so

若您使用eclipse ADT，项目下新建libs文件夹，将其按如下方式放置：

![alt eclipselib](http://ubirouting.com/imageUse/ubimap-eclipse-libs.png)

若为Android Studio，此略。

编辑AndroidMenifest.xml，添加权限：

	<uses-permission android:name="android.permission.INTERNET" />
	
添加Key：

	<meta-data
    	android:name="ShituKey"
        android:value="YourKey" />

#2 SDK调用 - 下载资源包

识途矢量地图SDK，在使用前，需预先下载一个资源包，该资源包包含所有地图所需渲染素材；该资源包**只需下载一次**。

调用：

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


#3 下载地图

获取您所拥有的地图，必须事先获取MapId.

根据MapId下载地图：

	final long mapId = 1000361;
	
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



#3 加载地图

	UbiMapView mMap = (UbiMapView) findViewById(R.id.mapview);
	mMap.load(mapId);

#4 Demo TODO

1. 添加搜索示例
2. 添加定位点显示示例
3. 添加地图操作控件示例
