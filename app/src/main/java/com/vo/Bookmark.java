package com.vo;

public class Bookmark {
	private int content_id;
	private User user;


	public int getContent_id() {
		return content_id;
	}
	public void setContent_id(int content_id) {
		this.content_id = content_id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	@Override
	public String toString() {
		return "Good [content_id=" + content_id + ", user=" + user + "]";
	}
	
}