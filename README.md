UbiMap Demo
=====

![alt phone](http://ubirouting.com/imageUse/UbiMap.gif)

UbiMap矢量图引擎是由识途团队研发的矢量地图引擎，可用来显示商场、博物馆、景区等特定场所的精细地图。

特点：

- 操作流畅、体验良好；
- 轻量级的SDK；
- 轻量级的矢量地图：一般地图仅不到300KB。
- 支持导航；
- 跨平台格式，iOS\Android\HTML5
- 与识途室内定位SDK完美配合，为室内定位解决方案中不可获取一环；

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

```java
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
```

#3 下载地图

获取您所拥有的地图，必须事先获取MapId.

根据MapId下载地图：

```java
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

```

#3 加载地图

```java
	UbiMapView mMap = (UbiMapView) findViewById(R.id.mapview);
	mMap.load(mapId);
```
	
#4 定位点

UbiMap能通过传入一系列坐标，显示当前位置。*但定位本身并不属于UbiMap SDK的功能范畴*，您可下载[识途定位SDK](https://github.com/UbiroutingDevelop/NaturePosition-Android-SDK)实现室内定位的功能，也可通过其他室内定位SDK来传入坐标。。

##4.1 坐标

```java
	// 坐标为该矢量地图的相对像素坐标
	mMap.position(x, y);
```
	
##4.2 角度

```java
	// 刷新定位点角度
	mMap.angle(mDegree);
```
	
##4.3 跟随模式

跟随模式，即为所有坐标的更新，都将引起地图的移动，定位点此时固定在屏幕中央。在跟随模式下，用户能对周围地图有更好的直观体验。

跟随模式在手指触摸view后自动被破坏。
	
	//设置为跟随模式
	mMap.switchFollowMode();
	
#5 地图POI

识途矢量地图上包含2类POI。

- Mark，无区域的POI, 即该POI在地图上仅显示为一个单独的图标，常用来表示电梯、ATM、厕所、问讯处等。此类POI在实际环境中占据的区域较少，所以往往用一个单独的图标来表示；

![alt:markexample](http://www.ubirouting.com/imageUse/ubimap_mark_example.jpg)


- Area, 区域POI, 即该POI在地图上显示为一个多边形，并且带有店铺图标和文字。常用来表示店铺、停车位等比较大的区域。

![alt:area example](http://www.ubirouting.com/imageUse/ubimap_area_example.jpg)


上述2类POI均继承自MapModel。 通过地图点击事件、搜索可获取响应的POI。此外，也可在不加载地图的情况下获取数据内的相关信息。见[获取地图内POI信息](#poi)

	
#6 点击事件

设置UbiMapListener来获取点击事件。

```java
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
				//当加载完地图后调用，返回所有楼层信息。每一个楼层信息为com.ubirouting.ubimaplib.model.map.Floor的实例，其中包含该矢量地图的像素比例尺、像素长宽、与正北夹角等信息。
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
		
```
		
#7 导航

	// 调用后，将在地图中规划最短路径。用于定点导航
	mMap.navigate(startMapModel, endMapModel);
	
	// 根据坐标来导航，用于实时导航
	mMap.navigate(x, y, endModel);
	
	// 清除路径
	mMap.clearPath();
	
	
	
#8 搜索
	
```java
	// 根据关键字搜索
	List<MapModel> models = mMap.search(keyword);
```

#9 [获取地图内POI信息](id:poi)


您可在不加载地图的情况下，获取识途矢量地图中的楼层及POI信息。

```java
	// 获取楼层信息
	List<Floor> floors = UbiMapDataHelper.allFloor(mMapId);
```

```java
	// 获取某层POI
	List<MapModel> pois = UbiMapDataHelper.allPoi(mMapId, floorNum);
	
	// 获取所有POI
	List<MapModel> pois = UbiMapDataHelper.allPoi(mMapId);
```


