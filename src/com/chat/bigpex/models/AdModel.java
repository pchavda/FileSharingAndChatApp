package com.chat.bigpex.models;

import java.io.Serializable;

public class AdModel implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
public	String id;
public	String ad_image;
public	String ad_url;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getAd_image() {
	return ad_image;
}
public void setAd_image(String ad_image) {
	this.ad_image = ad_image;
}
public String getAd_url() {
	return ad_url;
}
public void setAd_url(String ad_url) {
	this.ad_url = ad_url;
}
@Override
	public String toString() {
		// TODO Auto-generated method stub
		return id + ad_image+ad_url;
	}
}
