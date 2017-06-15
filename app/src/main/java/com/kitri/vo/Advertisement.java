package com.kitri.vo;

import java.util.Date;

public class Advertisement {
	private int ad_id;
	private String ad_image;
	
	private String ad_content;
	private String ad_link;
	private Date ad_frdate;
	private Date ad_todate;
	private int ad_price;
	private int ad_click_count;
	
	public int getAd_id() {
		return ad_id;
	}
	public void setAd_id(int ad_id) {
		this.ad_id = ad_id;
	}
	public String getAd_image() {
		return ad_image;
	}
	public void setAd_image(String ad_image) {
		this.ad_image = ad_image;
	}
	public String getAd_content() {
		return ad_content;
	}
	public void setAd_content(String ad_content) {
		this.ad_content = ad_content;
	}
	public String getAd_link() {
		return ad_link;
	}
	public void setAd_link(String ad_link) {
		this.ad_link = ad_link;
	}
	public Date getAd_frdate() {
		return ad_frdate;
	}
	public void setAd_frdate(Date ad_frdate) {
		this.ad_frdate = ad_frdate;
	}
	public Date getAd_todate() {
		return ad_todate;
	}
	public void setAd_todate(Date ad_todate) {
		this.ad_todate = ad_todate;
	}
	public int getAd_price() {
		return ad_price;
	}
	public void setAd_price(int ad_price) {
		this.ad_price = ad_price;
	}
	public int getAd_click_count() {
		return ad_click_count;
	}
	public void setAd_click_count(int ad_click_count) {
		this.ad_click_count = ad_click_count;
	}
	@Override
	public String toString() {
		return "Advertisement [ad_id=" + ad_id + ", ad_image=" + ad_image + ", ad_content=" + ad_content + ", ad_link="
				+ ad_link + ", ad_frdate=" + ad_frdate + ", ad_todate=" + ad_todate + ", ad_price=" + ad_price
				+ ", ad_click_count=" + ad_click_count + "]";
	}
	
}