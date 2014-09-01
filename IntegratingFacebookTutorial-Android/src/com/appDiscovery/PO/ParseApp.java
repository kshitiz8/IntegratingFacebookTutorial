package com.appDiscovery.PO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ParseApp extends BasePO{
	public static final String OBJECT_NAME = "App";
	public static final String APPID = "appId";
	public static final String BANNER_URL_LARGE = "bannerUrlLarge";
	
	/*
	 * Constructor to Assign Parse object class
	 */
	public ParseApp() {
		super(OBJECT_NAME);
	}
	
	public ParseApp(ParseObject parseObject) {
		super(parseObject);
	}

	/*
	 * appId example: com.aviary.android.feather
	 */
	public String getAppId() {
		return this.parseObject.getString(APPID);
	}
	public void setAppId(String appId) {
		 this.parseObject.put(APPID, appId);
	}
	public String getBannerUrlLarge() {
		return this.parseObject.getString(BANNER_URL_LARGE);
	}
	public void setBannerUrlLarge(String bannerUrlLarge) {
		this.parseObject.put(BANNER_URL_LARGE, bannerUrlLarge);
	}
	
	/*
	 * Load parseApps from DB 
	 * lookup done by appId
	 */
	public static void loadParseAppInBackground(List<ParseApp> parseApps, FindCallback<ParseApp> callback){
		Collection<String> parseAppIds = new ArrayList<String>();
		for(ParseApp parseApp: parseApps){
			parseAppIds.add(parseApp.getAppId());
		}
		ParseQuery<ParseObject> query = ParseQuery.getQuery(OBJECT_NAME);
		query.whereContainedIn(APPID, parseAppIds);
		query.findInBackground(getParseFindCallback(OBJECT_NAME, callback, ParseApp.class));

	}
	/* Load parseApp from DB 
	 * lookup done by appId 
	 */
	public static void loadParseAppInBackground(ParseApp parseApps, FindCallback<ParseApp> callback){
		List<ParseApp> p = new ArrayList<ParseApp>();
		p.add(parseApps);
		loadParseAppInBackground(parseApps, callback);

	}
	public static List<ParseApp> loadParseApp(List<ParseApp> parseApps) throws ParseException{
		Collection<String> parseAppIds = new ArrayList<String>();
		for(ParseApp parseApp: parseApps){
			parseAppIds.add(parseApp.getAppId());
		}
		ParseQuery<ParseObject> query = ParseQuery.getQuery(OBJECT_NAME);
		query.whereContainedIn(APPID, parseAppIds);
		return parseObjectToParseApp(query.find());

	}
	public static List<ParseApp> parseObjectToParseApp(List<ParseObject> parseObjectList){
		List<ParseApp> parseAppList = new ArrayList<ParseApp>(); 
		for(ParseObject po: parseObjectList){
			ParseApp pa = new ParseApp(po);
			parseAppList.add(pa);
		}
		return parseAppList;
	}

}