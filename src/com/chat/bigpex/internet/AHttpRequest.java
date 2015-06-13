package com.chat.bigpex.internet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.chat.bigpex.database.DBAdapter;
import com.chat.bigpex.helper.AppLog;
import com.chat.bigpex.helper.MyPrefrence;
import com.chat.bigpex.helper.Utils;
import com.chat.bigpex.models.GetTimelineData;
import com.chat.bigpex.models.MessageModel;
import com.chat.bigpex.models.UserDetail;
import com.chat.mainclasses.AndroidAppUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AHttpRequest {

	private AHttpResponse response;
	private RequestCallback callback;
	private List<GetTimelineData> gTmdata;
	private Activity context;
	DBAdapter db;
	private Handler mHandler = new Handler();

	public AHttpRequest() {
	}

	public AHttpRequest(Activity context, boolean showProgress) {
		this(context);
	}

	public AHttpRequest(Activity context) {
		this.context = context;
	}

	public AHttpRequest(Activity context, RequestCallback callback) {
		this(context);
		this.callback = callback;
	}

	public void userRegister(final String code, final String mobile,
			final String email, final String regid, final String time_zone) {
		// device_type
		if (!Utils.isOnline(context)) {
			Utils.showNoInternetMessage(context);
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				Calendar cal = Calendar.getInstance();

				ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("code", code));
				list.add(new BasicNameValuePair("mobile", mobile));
				list.add(new BasicNameValuePair("email", email));
				list.add(new BasicNameValuePair("regid", regid));
				list.add(new BasicNameValuePair("time_zone", time_zone));
				list.add(new BasicNameValuePair("device_type", "0"));// 0 for
																		// android

				AppLog.Log("Time ZoNE", cal.getTimeZone().toString());

				HttpRequest request = new HttpRequest();
				try {
					String responseString = request.postData(
							Urls.REGISTER_USER, list);
					response = new AHttpResponse(responseString, true);
					if (callback != null && context != null) {
						context.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								callback.onReqestComplete(response);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public void verifyUser(final String email, final String promo_code,
			final String regid) {
		if (Utils.isOnline(context)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
					list.add(new BasicNameValuePair("email", email));
					list.add(new BasicNameValuePair("promo_code", promo_code));
					list.add(new BasicNameValuePair("regid", regid));

					HttpRequest httpRequest = new HttpRequest();
					try {
						String responseString = httpRequest.postData(
								Urls.VERIFY_USER, list);
						AppLog.Log("verifyUser", "Response :: "
								+ responseString);
						if (context != null && responseString != null) {
							AHttpResponse response = new AHttpResponse(
									responseString, true);
							if (response.isSuccess) {
								MyPrefrence prefrence = new MyPrefrence(context);
								UserDetail detail = response.verifyDetail();
								prefrence.saveDetail(detail);
							} else {
							}

							if (callback != null) {
								callback.onReqestComplete(response);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}).start();
		} else {
			if (context != null) {
				Utils.showNoInternetMessage(context);
			}
		}
	}

	public void editProfile(final String id, final String name,
			final String status, final String image, final String code,
			final String mobile) {
		if (Utils.isOnline(context)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
					list.add(new BasicNameValuePair("id", id));
					if (name != null)
						list.add(new BasicNameValuePair("name", name));
					if (status != null)
						list.add(new BasicNameValuePair("status", status));
					if (image != null)
						list.add(new BasicNameValuePair("image", image));
					if (code != null)
						list.add(new BasicNameValuePair("code", code));
					if (mobile != null)
						list.add(new BasicNameValuePair("mobile", mobile));
					try {
						String responseString = new HttpRequest().postData(
								Urls.EDIT_PROFILE, list);
						final AHttpResponse response = new AHttpResponse(
								responseString, true);
						UserDetail detail = response.editProfile();
						AppLog.Log("TAG", "Image Name :: " + detail.getImage());
						MyPrefrence myPrefrence = new MyPrefrence(context);
						myPrefrence.saveDetail(detail);

						if (context != null && callback != null) {
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									callback.onReqestComplete(response);
								}
							});

						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}).start();
		} else {
			Utils.showNoInternetMessage(context);
		}
	}

	public void sendMessage(final String id, final String friend_id,
			final String message, final String type,
			final boolean isGroupMessage) {

		AppLog.Log("sendMessage", "id :: " + id + " friend_id ::" + friend_id
				+ " message :: " + message + " type :" + type
				+ "isGroupMessage :" + isGroupMessage);

		if (Utils.isOnline(context)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
					list.add(new BasicNameValuePair("id", id));
					list.add(new BasicNameValuePair("friend_id", friend_id));
					list.add(new BasicNameValuePair("message", message));
					list.add(new BasicNameValuePair("type", type));

					try {
						String responseString;

						if (!isGroupMessage) {
							responseString = new HttpRequest().postData(
									Urls.SEND_MESSAGE, list);
							AppLog.Log("SEND_MESSAGE", "Personal Message Sent");
						} else {
							responseString = new HttpRequest().postData(
									Urls.GROUP_MESSAGE, list);
							AppLog.Log("SEND_MESSAGE", "Group Message Sent");
						}
						AHttpResponse response = new AHttpResponse(
								responseString, true);
						MessageModel message = response.getSendMessage();
						if (context != null && callback != null) {
							callback.onReqestComplete(response);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			Utils.showNoInternetMessage(context);
		}
	}

	public void getLastSeen(final String id, final String myid) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				if (!Utils.isOnline(context)) {
					AppLog.Log("No Internet Connections",
							"No Internet Connection");
					return;
				}

				ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("id", id));
				list.add(new BasicNameValuePair("user_id", myid));

				try {
					String responseString = new HttpRequest().postData(
							Urls.LAST_SEEN_GET, list);
					AHttpResponse response = new AHttpResponse(responseString,
							true);
					AppLog.Log("getLastSeen", responseString);
					// response.getLastSeen();
					if (context != null && callback != null) {
						callback.onReqestComplete(response);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	public void removeAndExitGroup(final String userId, final String groupId) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpRequest httpRequest = new HttpRequest();
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("id", groupId));
				list.add(new BasicNameValuePair("membe_ids", userId));

				try {
					String responseString = httpRequest.postData(
							Urls.EXIT_GROUP, list);
					if (responseString != null) {
						final AHttpResponse response = new AHttpResponse(
								responseString, true);
						AppLog.Log("removeAndExitGroup", responseString);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (callback != null) {
									callback.onReqestComplete(response);
								}
							}
						});

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (callback != null) {
					callback.onReqestComplete(null);
				}

			}
		}).start();

	}

	public void setLastSeen(final String id, final String userStatus) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpRequest httpRequest = new HttpRequest();
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("id", id));
				list.add(new BasicNameValuePair("status", userStatus));
				try {
					String response = httpRequest.postData(Urls.LAST_SEEN_SET,
							list);
					AppLog.Log("Last Seen Set response", response);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void updateAllGroupsDetail(final String userId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("id", userId));
				HttpRequest request = new HttpRequest();
				try {
					String responseString = request.postData(Urls.GROUPLIST,
							list);
					AHttpResponse response = new AHttpResponse(responseString,
							true);
					if (response.isSuccess) {
						List<UserDetail> details = response.getGroupsDetail();
						DBAdapter adapter = new DBAdapter(context);
						adapter.openForRead();
						for (int i = 0; i < details.size(); i++) {
							adapter.insertOrUpdateGroup(details.get(i));
						}

						adapter.close();

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void getGroupInfo(final String userId, final String groupId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("user_id", userId));
				list.add(new BasicNameValuePair("id", groupId));

				HttpRequest request = new HttpRequest();
				try {
					String responseString = request.postData(Urls.GROUPINFO,
							list);

					AppLog.Log("group Info", responseString);
					// AHttpResponse response = new
					// AHttpResponse(responseString,
					// true);
					// if (response.isSuccess) {
					// List<UserDetail> details = response.getGroupsDetail();
					// DBAdapter adapter = new DBAdapter(context);
					// adapter.openForRead();
					// for (int i = 0; i < details.size(); i++) {
					// adapter.insertOrUpdateGroup(details.get(i));
					// }
					//
					// adapter.close();
					// }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void deleteChat(final String user_id, final String groupId,
			final String Who) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("user_id", user_id));
				list.add(new BasicNameValuePair("id", groupId));
				list.add(new BasicNameValuePair("who", Who));
				HttpRequest request = new HttpRequest();

				try {
					String responseString = request.postData(Urls.DELETE_CHAT,
							list);
					Log.i("response", "deleted" + responseString);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}
	
	
	public void sendTimelineImage(final String  content,final String  img_name,final String phone_number, 
			final String datetime, final String type, final String privacy) {

//		AppLog.Log("sendMessage", "id :: " + id + " friend_id ::" + friend_id
//				+ " message :: " + message + " type :" + type
//				+ "isGroupMessage :" + isGroupMessage);
 
		Log.d("METHOD CALLED", "IMAGE UPLOAD METHOD");
		
		
		if (Utils.isOnline(context)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
					list.add(new BasicNameValuePair("content", content));
					list.add(new BasicNameValuePair("phone_number", phone_number));
					list.add(new BasicNameValuePair("img_name", img_name));
					list.add(new BasicNameValuePair("datetime", datetime));
					list.add(new BasicNameValuePair("type", type));
					list.add(new BasicNameValuePair("privacy", privacy));

					
					Log.d("content CALLED", content);
					try {
						String responseString;

						
							responseString = new HttpRequest().postData(
									Urls.SEND_TIMELINE_IMAGE, list);
							AppLog.Log("SEND_MESSAGE", "Timeline Image Sent");
						
						AHttpResponse response = new AHttpResponse(
								responseString, true);
						//MessageModel message = response.getSendMessage();
						if (context != null && callback != null) {
							callback.onReqestComplete(response);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			Utils.showNoInternetMessage(context);
		}
	}
	
	
	
	
	public void sendTimelineMessage(final String phone_number, final String message,
			final String datetime, final String type, final String privacy) {

//		AppLog.Log("sendMessage", "id :: " + id + " friend_id ::" + friend_id
//				+ " message :: " + message + " type :" + type
//				+ "isGroupMessage :" + isGroupMessage);

		if (Utils.isOnline(context)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
					list.add(new BasicNameValuePair("phone_number", phone_number));
					list.add(new BasicNameValuePair("message", message));
					list.add(new BasicNameValuePair("datetime", datetime));
					list.add(new BasicNameValuePair("type", type));
					list.add(new BasicNameValuePair("privacy", privacy));

					try {
						String responseString;

						
							responseString = new HttpRequest().postData(
									Urls.SEND_TIMELINE_MESAGE, list);
							AppLog.Log("SEND_MESSAGE", "Timeline Message Sent");
							Log.e("RESPONSE STRING", responseString);
							
						
						ObjectMapper mapper = AndroidAppUtils.getJsonMapper();

						try {
							gTmdata = mapper.readValue((String) responseString,
									new TypeReference<List<GetTimelineData>>() {
									});

							Log.e("Response", "" + responseString);

							db = new DBAdapter(context);
							if (!db.isOpen()) {
								db.openForWrite();
							}
							int a = gTmdata.size();
							Log.e("A", "" + a);
							for (int i = 0; i <= a - 1; i++) {
								if (!db.isTimelineMessage(gTmdata.get(i)
										.getMessage_id())) {
									Log.d("Message id : ", ""
											+ gTmdata.get(i).getMessage_id());
									int total = db.insertTimelineMessage(gTmdata
											.get(i));

									Log.e("Number of row inserted", "" + total);
								}
							}
							db.close();
							Log.e("Method call", "new method");
							AHttpResponse response = new AHttpResponse(
									responseString, true);

						} catch (JsonParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						
						//MessageModel message = response.getSendMessage();
						if (context != null && callback != null) {
							callback.onReqestComplete(response);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			//Utils.showTimeLineMessageSend(context);	
		} else {
			Utils.showNoInternetMessage(context);
		}
	}

	
}
