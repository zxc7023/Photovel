package com.vo;

public class Permission {
	private String user_id;
	private int friend_recom_flag;
	private int friend_search_flag;
	private int feed_flag;

	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public int getFriend_recom_flag() {
		return friend_recom_flag;
	}
	public void setFriend_recom_flag(int friend_recom_flag) {
		this.friend_recom_flag = friend_recom_flag;
	}
	public int getFriend_search_flag() {
		return friend_search_flag;
	}
	public void setFriend_search_flag(int friend_search_flag) {
		this.friend_search_flag = friend_search_flag;
	}
	public int getFeed_flag() {
		return feed_flag;
	}
	public void setFeed_flag(int feed_flag) {
		this.feed_flag = feed_flag;
	}
	@Override
	public String toString() {
		return "Permission [user_id=" + user_id + ", friend_recom_flag=" + friend_recom_flag + ", friend_search_flag="
				+ friend_search_flag + ", feed_flag=" + feed_flag + "]";
	}
	
}