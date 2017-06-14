package com.kitri.vo;

import java.util.Date;

public class Admin {

    private String admin_id;

    private String admin_password;

    private String admin_nick_name;

    private String admin_status_flag;
    
    private Date admin_sign_date;

	public Admin() {
	}

	public String getAdmin_id() {
		return admin_id;
	}

	public void setAdmin_id(String admin_id) {
		this.admin_id = admin_id;
	}

	public String getAdmin_password() {
		return admin_password;
	}

	public void setAdmin_password(String admin_password) {
		this.admin_password = admin_password;
	}

	public String getAdmin_nick_name() {
		return admin_nick_name;
	}

	public void setAdmin_nick_name(String admin_nick_name) {
		this.admin_nick_name = admin_nick_name;
	}

	public String getAdmin_status_flag() {
		return admin_status_flag;
	}

	public void setAdmin_status_flag(String admin_status_flag) {
		this.admin_status_flag = admin_status_flag;
	}
	public Date getAdmin_sign_date() {
		return admin_sign_date;
	}

	public void setAdmin_sign_date(Date admin_sign_date) {
		this.admin_sign_date = admin_sign_date;
	}

	@Override
	public String toString() {
		return "Admin [admin_id=" + admin_id + ", admin_password=" + admin_password + ", admin_nick_name="
				+ admin_nick_name + ", admin_status_flag=" + admin_status_flag + ", admin_sign_date=" + admin_sign_date
				+ "]";
	}
	
}

   