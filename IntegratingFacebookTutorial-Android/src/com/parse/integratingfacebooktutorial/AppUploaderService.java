package com.parse.integratingfacebooktutorial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.appDiscovery.PO.BasePO;
import com.appDiscovery.PO.FindCallback;
import com.appDiscovery.PO.ParseApp;
import com.appDiscovery.PO.ParseUserAppMap;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class AppUploaderService {
	private static final String TAG = "AppUploader";
	
	private ParseUser currentUser = null;
	private String deviceId = null;
	private PackageManager packageManager = null; 
	
	
	
	public AppUploaderService(String deviceId,PackageManager packageManager ) {
		currentUser = ParseUser.getCurrentUser();
		this.packageManager = packageManager;
		this.deviceId = deviceId;
		uploadUserApps();
	}
	
	public void uploadUserApps() {
		if (currentUser.get("profile") != null) {
			ParseUserAppMap.findParseUserAppsInBackground(currentUser, 
					new FindCallback<ParseUserAppMap>() {
				@Override
				public void done(List<ParseUserAppMap> userAppsFromServer, ParseException e) {
					if(e == null){
						List<ApplicationInfo> newInstalledAppList = getNewInstalledApps(userAppsFromServer);
						persistNewInstalledApps(newInstalledAppList);
					}else{ 
						Log.e(TAG, e.getMessage());
						Log.e(TAG, "---------uploadUserApps-------");
					}
				}
			});

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
		ParseApp.loadParseAppInBackground(appList, new FindCallback<ParseApp>() {

			@Override
			public void done(List<ParseApp> objects, ParseException e) {
				ArrayList<ParseUserAppMap> userAppMapList = new ArrayList<ParseUserAppMap>();
				for(ParseApp parseApp: objects){
					ParseUserAppMap userAppMap = new ParseUserAppMap();
					userAppMap.setParseApp(parseApp);
					userAppMap.setParseUser(currentUser);
					userAppMap.setDeviceId(deviceId);
					userAppMapList.add(userAppMap);
				}
				ParseObject.saveAllInBackground(BasePO.getPOList(userAppMapList));
			}
		});


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
