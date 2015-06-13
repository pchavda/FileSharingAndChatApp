package com.chat.bigpex.models;

import java.io.Serializable;

public class GetTimelineData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String message_id;
	public String content;
	public String datetime;
	public String type;
	public String key;
	
	public String phone;
	public String contry_code;
	
	public String getMessage_id() {
		return message_id;
	}
	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getContry_code() {
		return contry_code;
	}
	public void setContry_code(String contry_code) {
		this.contry_code = contry_code;
	}
	
}
