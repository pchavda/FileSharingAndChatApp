package com.chat.bigpex.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class TimelineMessageModel implements Parcelable {

	private int id, messageId, key, messageType, last_message;
	private String content, datetime, phoneCode, phoneNo;

	
	public TimelineMessageModel() {
	}
	
	public TimelineMessageModel(Parcel in) {
		
		messageId = in.readInt();
		key = in.readInt();
		messageType = in.readInt();
		last_message = in.readInt();
		content = in.readString();
		datetime = in.readString();
		phoneCode = in.readString();
		phoneNo = in.readString();
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(messageId);
		dest.writeInt(key);
		dest.writeInt(messageType);
		dest.writeInt(last_message);
		dest.writeString(content);
		dest.writeString(datetime);
		dest.writeString(phoneCode);
		dest.writeString(phoneNo);
	}
	
	public static final Parcelable.Creator<TimelineMessageModel> CREATOR = new Creator<TimelineMessageModel>() {

		@Override
		public TimelineMessageModel[] newArray(int size) {
			return new TimelineMessageModel[size];
		}

		@Override
		public TimelineMessageModel createFromParcel(Parcel source) {
			return new TimelineMessageModel(source);
		}
	};
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public int getLast_message() {
		return last_message;
	}

	public void setLast_message(int last_message) {
		this.last_message = last_message;
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

	public String getPhoneCode() {
		return phoneCode;
	}

	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
