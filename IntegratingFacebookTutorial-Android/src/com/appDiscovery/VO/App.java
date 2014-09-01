package com.appDiscovery.VO;

public class App {
	
	private String appDisplayName;
	private String appBannerURL;
	private String appIconURL;
	private String appPackageName;
	private String appDescription;
	private long friendDownloads;
	private long totalDownloads;
	private long friendLikes;
	public String getAppDisplayName() {
		return appDisplayName;
	}
	public void setAppDisplayName(String appDisplayName) {
		this.appDisplayName = appDisplayName;
	}
	public String getAppBannerURL() {
		return appBannerURL;
	}
	public void setAppBannerURL(String appBannerURL) {
		this.appBannerURL = appBannerURL;
	}
	public String getAppIconURL() {
		return appIconURL;
	}
	public void setAppIconURL(String appIconURL) {
		this.appIconURL = appIconURL;
	}
	public String getAppPackageName() {
		return appPackageName;
	}
	public void setAppPackageName(String appPackageName) {
		this.appPackageName = appPackageName;
	}
	public String getAppDescription() {
		return appDescription;
	}
	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}
	public long getFriendDownloads() {
		return friendDownloads;
	}
	public void setFriendDownloads(long friendDownloads) {
		this.friendDownloads = friendDownloads;
	}
	public long getTotalDownloads() {
		return totalDownloads;
	}
	public void setTotalDownloads(long totalDownloads) {
		this.totalDownloads = totalDownloads;
	}
	public long getFriendLikes() {
		return friendLikes;
	}
	public void setFriendLikes(long friendLikes) {
		this.friendLikes = friendLikes;
	}
}
