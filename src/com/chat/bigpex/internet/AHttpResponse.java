package com.chat.bigpex.internet;

import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chat.bigpex.helper.AppLog;
import com.chat.bigpex.helper.Utils;
import com.chat.bigpex.models.GroupDetailModel;
import com.chat.bigpex.models.MessageModel;
import com.chat.bigpex.models.UserDetail;

public class AHttpResponse {

	private static final String STATUS = "success";
	private static final String MESSAGE = "message";

	public boolean isInternetConnected;
	public boolean isSuccess;
	public String messgae;

	// private String response;
	private JSONObject rootObject;

	public AHttpResponse(String response, boolean isInternetConnected) {
		// this.response = response;
		try {
			rootObject = new JSONObject(response);
			isSuccess = rootObject.getBoolean(STATUS);
			messgae = rootObject.getString(MESSAGE);
		} catch (JSONException e) {
			e.printStackTrace();
			isSuccess = false;
			messgae = "Server Connection failed please check code";
		}
	}

	public JSONObject getRootJsonObject() {
		return rootObject;
	}

	private UserDetail detail;
	private int month;

	public UserDetail verifyDetail() {

		if (detail == null && isSuccess) {
			detail = new UserDetail();
			if (rootObject != null) {
				try {
					detail.setUserId(Integer.parseInt(rootObject
							.getString("user_id")));
					detail.setEmail(rootObject.getString("email"));
					detail.setImage(rootObject.getString("pic"));
					detail.setName(rootObject.getString("fname"));
					detail.setPhoneCode(rootObject.getString("phonecode"));
					detail.setPhoneNo(rootObject.getString("phone"));
					detail.setStatus(rootObject.getString("status"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return detail;
	}

	// {
	// "details": {
	// "id": "77",
	// "name": "Deep",
	// "online": "1",
	// "status": "Hi i am deep",
	// "image": "users/1.40066621935E+12deep.jpg",
	// "phone": "9429502121",
	// "phone_code": "+93"
	// },
	// "success": true,
	// "message": "Success."
	// }

	public UserDetail editProfile() {
		if (detail == null && isSuccess) {
			detail = new UserDetail();
			try {
				JSONObject object = rootObject.getJSONObject("details");
				detail.setUserId(Integer.parseInt(object.getString("id")));
				detail.setName(object.getString("name"));
				detail.setStatus(object.getString("status"));
				detail.setImage(object.getString("image"));
				detail.setPhoneNo(object.getString("phone"));
				detail.setPhoneCode(object.getString("phone_code"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return detail;
	}

	// {
	// "details": {
	// "id": "704",
	// "time": "2014-05-24 14:55:17",
	// "type": "1",
	// "user_id": "71",
	// "friend_id": "70",
	// "who": "1",
	// "message": "hello",
	// "delivery_time": "0000-00-00 00:00:00",
	// "image": "default.jpg",
	// "user_status": "hi...!",
	// "phone": "9409383861",
	// "phone_code": "+91",
	// "name": "pappu",
	// "status": "1"
	// },
	// "message": "Stored message for sending.",
	// "success": true
	// }

	public MessageModel getSendMessage() {
		MessageModel model = new MessageModel();
		
		try {
			if (rootObject.has("temp_msg_id"))
				model.setId(rootObject.getInt("temp_msg_id"));

			JSONObject object = rootObject.getJSONObject("details");
			model.setMessageId(object.getInt("id"));
			
			// Convert to Miliseconds 
			
			String milli_sec = Utils.convertDateIntoLocalMilli(object.getString("time"));
			
			Calendar cal = Calendar.getInstance();
			//cal.setTimeInMillis(Long.parseLong(milli_sec));
			month = cal.get(Calendar.MONTH)+1;
			model.setTime(cal.get(Calendar.YEAR)+"-"+month+"-"+cal.get(Calendar.DAY_OF_MONTH)+" "
						  +cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND));
			
			model.setMessageType(object.getInt("type"));
			model.setUserID(object.getInt("user_id"));
			model.setFriendId(object.getInt("friend_id"));
			model.setWho(object.getInt("who"));
			if (object.has("name"))
				model.setDisplayNane(object.getString("name"));
			model.setMessage(object.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return model;
	}

	public String getLastSeen() {
		try {
			AppLog.Log("getLastSeen", "response String" + rootObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// {"details":{"id":"119","time":"2014-06-12 17:02:11","type":"","user_id":"131",
	// "friend_id":"","who":"4","message":"","delivery_time":"",
	// "image":"group\/default.jpg","user_status":"","phone":"","phone_code":"","name":"test
	// 33","status":""},"success":true,"message":"Successfully group created."}

	public UserDetail getJoinGroupDetail() {

		try {
			UserDetail detail = new UserDetail();
			JSONObject object = rootObject.getJSONObject("details");
			detail.setAdminId(object.getInt("user_id"));
			detail.setName(object.getString("name"));
			detail.setImage(object.getString("image"));
			detail.setUserId(object.getInt("id"));
			return detail;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<UserDetail> getGroupsDetail() {

		GroupDetailModel model = new GroupDetailModel();
		UserDetail groupDetail;
		if (isSuccess) {
			try {
				JSONArray array = rootObject.getJSONArray("details");

				JSONObject jsonObject;
				for (int i = 0; i < array.length(); i++) {
					jsonObject = array.getJSONObject(i);
					detail.setGroupId(jsonObject.getInt("id"));
					detail.setName(jsonObject.getString("name"));
					detail.setImage(jsonObject.getString("image"));
					detail.setAdminId(jsonObject.getInt("admin_id"));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// {
	// "id": "76",
	// "name": "Pappus",
	// "admin_id": "71",
	// "image": "group/default.jpg",
	// "time": "2014-05-24 16:53:20",
	// "group_members": [
	// {
	// "id": "71",
	// "name": "pappu",
	// "phone_code": "+91",
	// "phone": "9409383861",
	// "image": "default.jpg",
	// "status": "hi...!"
	// },
	// {
	// "id": "1",
	// "name": "",
	// "phone_code": "+91",
	// "phone": "9998106007",
	// "image": "",
	// "status": "Yes i am using Freshim."
	// }
	// ]
	// }

	// private void getGroupDetail(JSONObject jsonObject) {
	// GroupDetailModel detailModel = new GroupDetailModel();
	// UserDetail detail = new UserDetail();
	// try {
	// detail.setGroupId(jsonObject.getInt("id"));
	// detail.setName(jsonObject.getString("name"));
	// detail.setImage(jsonObject.getString("image"));
	// detail.setAdminId(jsonObject.getInt("admin_id"));
	// List<UserDetail> list = new ArrayList<UserDetail>();
	// detailModel.setGroupDetail(detail);
	//
	// JSONArray array = jsonObject.getJSONArray("group_members");
	// for (int i = 0; i < array.length(); i++) {
	//
	// }
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
}
