UbiMap Demo
=====

![alt phone](http://ubirouting.com/imageUse/UbiMap.gif)

#1 准备工作

[获取识途矢量地图SDK](https://github.com/UbiroutingDevelop/UbiMap-SDK)

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
	
#4 显示及刷新定位点

	// 坐标为该矢量地图的相对像素坐标
	mMap.refreshPosition(x, y);
	
	// 刷新定位点角度
	mMap.refreshAngle(mDegree);
	
#5 地图POI介绍

总体来说，识途矢量地图上包含2种POI。

- Mark，无区域的POI, 即该POI在地图上仅显示为一个单独的图标，常用来表示电梯、ATM、厕所、问讯处等。此类POI在实际环境中占据的区域较少，所以往往用一个单独的图标来表示；
- Area, 区域POI, 即该POI在地图上显示为一个多边形，并且带有店铺图标和文字。常用来表示店铺、停车位等比较大的区域。

上述2类POI均继承自MapModel。

	
#5 地图点击回调
	mMap.setOnMapListener(new UbiMapListener() {

			@Override
			public void onSwitchFloor(int area) {
				//当切换楼层时调用
			}

			// When map loading completed, the floor information will be
			// returned by this method. You can access floors via 'floorList'.
			// Each element in 'floorList' is a instance of
			// com.ubirouting.ubimaplib.model.map.Floor class.
			@Override
			public void onLoadData(List floorList) {
				//当加载完地图后调用，返回所有楼层信息。每一个楼层信息为com.ubirouting.ubimaplib.model.map.Floor的实例
			}

			@Override
			public void onFailedLoadData() {
				// 地图加载失败
			}

			@Override
			public void onClickMap(float x, float y) {
				// 当点击地图时回调				
			}

			@Override
			public void onClickArea(Area area) {
				// 当点击区域时回调。区域为大块区域，常用来表示店铺、停车位等有一定面积的POI。	
			}

			@Override
			public void onClickMark(Mark mark) {
				//当点击Mark时回调。Mark为地图上的无区域的小图标，常用来表示厕所、电梯、ATM等单点POI
			}

		});
		
#6 导航路径

	// 调用后，将在地图中规划最短路径
	mMap.navigate(startMapModel, endMapModel);

#5 Demo TODO

1. 添加搜索示例
2. ~~添加定位点显示示例~~
3. ~~添加地图操作控件示例~~
