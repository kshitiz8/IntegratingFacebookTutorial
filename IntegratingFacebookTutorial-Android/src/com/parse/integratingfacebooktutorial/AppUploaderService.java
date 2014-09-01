package com.parse.integratingfacebooktutorial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.util.Log;

import com.appDiscovery.PO.BasePO;
import com.appDiscovery.PO.FindCallback;
import com.appDiscovery.PO.ParseApp;
import com.appDiscovery.PO.ParseUserAppMap;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class AppUploaderService extends Service {
	private static final String TAG = "AppUploaderService";
	private int state = 0;

	private ParseUser currentUser = null;
	private String deviceId = null;
	private PackageManager packageManager = null; 

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//handleCommand(intent);
		Log.d(TAG,"AppUploaderService onStartCommand");
		if(state == 0){
			state =1;
			Log.d(TAG,"AppUploaderService state incative, calling ");
			currentUser = ParseUser.getCurrentUser();
			this.packageManager = getPackageManager();
			this.deviceId = Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID);
			new Thread(new Runnable() {
				public void run() {
					uploadUserApps();
					try {
						Thread.sleep(300000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.d(TAG,"AppUploaderService shutting down...");
					stopSelf();
				}
			}).start();
		}else{
			Log.d(TAG,"AppUploaderService state active, no need to do anything");

		}
		return 1;
	}


	public void uploadUserApps() {
		if (currentUser.get("profile") != null) {
			List<ParseUserAppMap> userAppsFromServer;
			try {
				userAppsFromServer = ParseUserAppMap.findParseUserApps(currentUser);
				for(ParseUserAppMap app : userAppsFromServer){
					Log.e(TAG,app.getParseAppId());
				}
				List<ApplicationInfo> newInstalledAppList = this.getNewInstalledApps(userAppsFromServer);
				this.persistNewInstalledApps(newInstalledAppList);
			} catch (ParseException e) {
				Log.e(TAG, "unable to bring user apps from server");
				stopSelf();
			}
		}
	}

	public  void persistNewInstalledApps(List<ApplicationInfo> newInstalledAppList){

		ArrayList<ParseApp> appList = new ArrayList<ParseApp>();
		for (ApplicationInfo  appInfo : newInstalledAppList){

			ParseApp parseApp = new ParseApp();
			parseApp.setAppId(appInfo.packageName);
			appList.add(parseApp);

			//			Log.d(TAG, "Name: " + appInfo.loadLabel(packageManager));

		}
		//save new apps if any to App dimension

		try {
			List<ParseApp> parseAppList = ParseApp.loadParseApp(appList);
			ArrayList<ParseUserAppMap> userAppMapList = new ArrayList<ParseUserAppMap>();
			for(ParseApp parseApp: parseAppList){
				ParseUserAppMap userAppMap = new ParseUserAppMap();
				userAppMap.setParseApp(parseApp);
				userAppMap.setParseUser(currentUser);
				userAppMap.setDeviceId(deviceId);
				userAppMapList.add(userAppMap);
			}
			ParseObject.saveAll(BasePO.getPOList(userAppMapList));
		} catch (ParseException e) {
			Log.e(TAG, "unable to load app detail from server");
			stopSelf();
		}
	}

	private List<ApplicationInfo> getNewInstalledApps(List<ParseUserAppMap> userAppsFromServer){

		// generate a Collection of App ids from the UserApps returned from Server 
		// these apps will be filtered out from currently installed apps
		Collection<String> currentUserApps = new ArrayList<String>();
		for(ParseUserAppMap userApp: userAppsFromServer){
			Log.v(TAG,userApp.getParseAppId());
			currentUserApps.add(userApp.getParseAppId());
		}


		List<ApplicationInfo> installedApplications =  packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
		List<ApplicationInfo> filteredApps = new ArrayList<ApplicationInfo>();
		for (ApplicationInfo  appInfo : installedApplications)
		{
			if(appInfo.packageName.startsWith("com.android.")
					|| appInfo.packageName.startsWith("com.example.android.")
					|| "android".equals(appInfo.packageName)
					|| currentUserApps.contains(appInfo.packageName)){
				Log.d(TAG, "Skipping package: " + appInfo.packageName);
				continue;
			}
			filteredApps.add(appInfo);
		}
		return filteredApps;
	}

}
