package com.vo;

import java.util.Date;

public class Comment {
	private int content_id;
	private int comment_id;
	private String comment_content;
	private Date comment_date;
	private User user;

	public Comment(){
	}

	public Comment(String comment_content){
		this.comment_content = comment_content;
	}

	public int getContent_id() {
		return content_id;
	}
	public void setContent_id(int content_id) {
		this.content_id = content_id;
	}
	public int getComment_id() {
		return comment_id;
	}
	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}
	public String getComment_content() {
		return comment_content;
	}
	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}
	public Date getComment_date() {
		return comment_date;
	}
	public void setComment_date(Date comment_date) {
		this.comment_date = comment_date;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "Comment [content_id=" + content_id + ", comment_id=" + comment_id + ", comment_content="
				+ comment_content + ", comment_date=" + comment_date + ", user=" + user + "]";
	}
	
}