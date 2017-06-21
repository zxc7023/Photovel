package com.vo;

import android.graphics.Bitmap;

public class MainImage {
	private String image_file_name;
	private String image_url_link;
	private String image_subject;
	private String image_content;
	private Bitmap bitmap;
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public String getImage_file_name() {
		return image_file_name;
	}
	public void setImage_file_name(String image_file_name) {
		this.image_file_name = image_file_name;
	}
	public String getImage_url_link() {
		return image_url_link;
	}
	public void setImage_url_link(String image_url_link) {
		this.image_url_link = image_url_link;
	}
	public String getImage_subject() {
		return image_subject;
	}
	public void setImage_subject(String image_subject) {
		this.image_subject = image_subject;
	}
	public String getImage_content() {
		return image_content;
	}
	public void setImage_content(String image_content) {
		this.image_content = image_content;
	}
	@Override
	public String toString() {
		return "MainImageActivity [image_file_name=" + image_file_name + ", image_url_link=" + image_url_link
				+ ", image_subject=" + image_subject + ", image_content=" + image_content + "]";
	}
	
}