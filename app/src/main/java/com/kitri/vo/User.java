package com.kitri.vo;

import java.util.Date;

public class User {
	private String user_id;
	private String user_password;
	private String user_nick_name;
	private String user_gender;
	private int user_phone1;
	private int user_phone2;
	private String user_profile_photo;
	private Date user_sign_date;
	private String user_state_flag;
	private Date user_last_connection;
	private String user_sns_status;
	private String user_sns_token;
	private String user_push_token;
	private int user_friend_count;
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_password() {
		return user_password;
	}
	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}
	public String getUser_nick_name() {
		return user_nick_name;
	}
	public void setUser_nick_name(String user_nick_name) {
		this.user_nick_name = user_nick_name;
	}
	public String getUser_gender() {
		return user_gender;
	}
	public void setUser_gender(String user_gender) {
		this.user_gender = user_gender;
	}
	public int getUser_phone1() {
		return user_phone1;
	}
	public void setUser_phone1(int user_phone1) {
		this.user_phone1 = user_phone1;
	}
	public int getUser_phone2() {
		return user_phone2;
	}
	public void setUser_phone2(int user_phone2) {
		this.user_phone2 = user_phone2;
	}
	public String getUser_profile_photo() {
		return user_profile_photo;
	}
	public void setUser_profile_photo(String user_profile_photo) {
		this.user_profile_photo = user_profile_photo;
	}
	public Date getUser_sign_date() {
		return user_sign_date;
	}
	public void setUser_sign_date(Date user_sign_date) {
		this.user_sign_date = user_sign_date;
	}
	public String getUser_state_flag() {
		return user_state_flag;
	}
	public void setUser_state_flag(String user_state_flag) {
		this.user_state_flag = user_state_flag;
	}
	public Date getUser_last_connection() {
		return user_last_connection;
	}
	public void setUser_last_connection(Date user_last_connection) {
		this.user_last_connection = user_last_connection;
	}
	public String getUser_sns_status() {
		return user_sns_status;
	}
	public void setUser_sns_status(String user_sns_status) {
		this.user_sns_status = user_sns_status;
	}
	public String getUser_sns_token() {
		return user_sns_token;
	}
	public void setUser_sns_token(String user_sns_token) {
		this.user_sns_token = user_sns_token;
	}
	public String getUser_push_token() {
		return user_push_token;
	}
	public void setUser_push_token(String user_push_token) {
		this.user_push_token = user_push_token;
	}
	public int getUser_friend_count() {
		return user_friend_count;
	}
	public void setUser_friend_count(int user_friend_count) {
		this.user_friend_count = user_friend_count;
	}
	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", user_password=" + user_password + ", user_nick_name=" + user_nick_name
				+ ", user_gender=" + user_gender + ", user_phone1=" + user_phone1 + ", user_phone2=" + user_phone2
				+ ", user_profile_photo=" + user_profile_photo + ", user_sign_date=" + user_sign_date
				+ ", user_state_flag=" + user_state_flag + ", user_last_connection=" + user_last_connection
				+ ", user_sns_status=" + user_sns_status + ", user_sns_token=" + user_sns_token + ", user_push_token="
				+ user_push_token + ", user_friend_count=" + user_friend_count + "]";
	}
	
}