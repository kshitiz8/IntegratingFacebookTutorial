package com.appDiscovery.PO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@com.parse.ParseClassName(value="UserApp")
public class ParseUserAppMap extends BasePO{
	public ParseUserAppMap(){
		super(OBJECT_NAME);
	}
	
	public static final String OBJECT_NAME = "UserApp";
	public static final String APP = "app";
	public static final String APP_ID = "appId";
	public static final String USER = "user";
	public static final String DEVICE_ID = "deviceId";
	
	
	public String getParseAppId() {
		return (String) this.parseObject.getString(APP_ID);
	}
	public void setParseAppId(ParseApp parseAppId) {
		 this.parseObject.put(APP_ID, parseAppId);
	}
	public ParseApp getParseApp() {
		return new ParseApp((ParseObject)this.parseObject.getParseObject(APP));
	}
	public void setParseApp(ParseApp parseApp) {
		 this.parseObject.put(APP, parseApp);
		 this.parseObject.put(APP_ID, parseApp.getAppId());
	}
	public ParseUser getParseUser() {
		return (ParseUser) this.parseObject.getParseUser(USER);
	}
	public void setParseUser(ParseUser parseUser) {
		this.parseObject.put(USER, parseUser);
	}
	public String getDeviceId() {
		return this.parseObject.getString(DEVICE_ID);
	}
	public void setDeviceId(String deviceId) {
		this.parseObject.put(DEVICE_ID,deviceId );
	}
	 
	/*
	 * returns parse UserApps for single parse user
	 */
	public static void findParseUserAppsInBackground(ParseUser parseUser, FindCallback<ParseUserAppMap> callback){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(OBJECT_NAME);
		query.whereEqualTo(USER, parseUser.get("objectId"));
		query.findInBackground(BasePO.getParseFindCallback(OBJECT_NAME, callback));

	}
	/*
	 * returns parse UserApps for a list of parseusers
	 */
	public static void findParseUserAppsInBackground(List<ParseUser> parseUsers, 
			FindCallback<ParseUserAppMap> callback){
		Collection<String> parseUserObjectIds = new ArrayList<String>();
		for(ParseUser parseUser: parseUsers){
			parseUserObjectIds.add((String) parseUser.get("objectId"));
			
		}
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(OBJECT_NAME);
		query.whereContainedIn(USER, parseUserObjectIds);
		query.findInBackground(BasePO.getParseFindCallback(OBJECT_NAME, callback));

	}
	
	/*
	 * returns ParseUsers for list of facebook id
	 * this function doesnt belong here 
	 */
	public static void findParseUser(List<String> facebookIds, FindCallback<ParseApp> callback){
		
		//TODO
	}
	
	/*
	 * returns n ParseUsers
	 * this function doesnt belong here 
	 */
	public static void findParseUser(int n, com.parse.FindCallback<ParseUser> callback){
		
		ParseQuery<ParseUser> q = ParseUser.getQuery();
		q.setLimit(n);
		q.findInBackground( callback);
	}
	
}
