package com.chat.bigpex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import com.bigpex.gonechat.R;
import com.chat.bigpex.adapter.NewChatAdapter;
import com.chat.bigpex.database.DBAdapter;
import com.chat.bigpex.dialogs.SelectDialog;
import com.chat.bigpex.helper.AppLog;
import com.chat.bigpex.helper.Constant;
import com.chat.bigpex.helper.Constant.Extra;
import com.chat.bigpex.helper.Constant.MyActions;
import com.chat.bigpex.helper.Constant.Pref;
import com.chat.bigpex.helper.LocationHelper;
import com.chat.bigpex.helper.LocationHelper.MyLocationListener;
import com.chat.bigpex.helper.MyPrefrence;
import com.chat.bigpex.helper.TimelineUtils;
import com.chat.bigpex.helper.Utils;
import com.chat.bigpex.internet.AHttpRequest;
import com.chat.bigpex.internet.AHttpResponse;
import com.chat.bigpex.internet.HttpRequest;
import com.chat.bigpex.internet.RequestCallback;
import com.chat.bigpex.internet.Urls;
import com.chat.bigpex.models.MessageModel;
import com.chat.bigpex.models.UserDetail;
import com.chat.bigpex.services.AudioUploadingService;
import com.chat.bigpex.services.DownloadService;
import com.chat.bigpex.services.GcmIntentService;
import com.chat.bigpex.services.ImageUploadingServices;
import com.chat.bigpex.services.VideoUploadService;

public class NewChatActivity extends BaseActivity implements OnClickListener,
		RequestCallback, OnItemClickListener, MyLocationListener, OnTouchListener {

	private static final String TAG = "ChatActivity";
	private static final int SELECT_PICTURE = 101;
	private static final int CONTACT_PICKER = 102;
	private static final int SELECT_VEDIO = 103;
	private static final int CAMERA_REQUEST_CODE = 104;
	private static final int REQUEST_TO_ADD_CONTACT = 105;

	private static final int LAST_SEEN_UPDATE = 1000 * 15;

	// 15 sec
	// private static final int GALLERY_INTENT_CALLED = 105;
	// private static final int GALLERY_KITKAT_INTENT_CALLED = 106;

	private EditText etMessgae;
	private ImageView btnSend, btnsendaudio;

	private UserDetail myDetail, friendDetail;
	private NewChatAdapter mChatAdapter;
	private DBAdapter mDbAdapter;
	private ListView lvChat;
	private static String filePath;
	private Dialog dialog;
	private MediaRecorder myAudioRecorder;
	   private String outputFile = null;
	private ProgressDialog pd;

	private LocationHelper mLocationHelper;

	private ArrayList<Date> dateList = new ArrayList<Date>();
	private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
	private List<MessageModel> list;
	private List<MessageModel> listOrg;
	SharedPreferences preferences;
	static String  audioPath;
	
	private TimelineUtils objTMUtils;
	public static int pos;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			getUserLastSeen();
		};
	};
	private Thread mLastSeenThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		Constant.IS_WINDOW_OPEN = true;

		myDetail = new MyPrefrence(this).getDetail();
		friendDetail = getIntent().getParcelableExtra(Extra.FRIEND_DETAIL);

		AppLog.Log(TAG, "is Group :: " + friendDetail.isGroup());

		init();

		loadAndSetChatData();

		setUpActionBar();

		IntentFilter filter = new IntentFilter(MyActions.MESSAGE_UPDATE);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mHandleMessageDelivery, filter);
		filter = new IntentFilter(MyActions.SEND_MESSAGE);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mHandleMessageDelivery, filter);

		getUserLastSeen();

		manageLocation();

		updateInfo();

		clearNotifications();

	}

	private void clearNotifications() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(GcmIntentService.NOTIFICATION_ID);
	}

	private void manageLocation() {
		mLocationHelper = new LocationHelper(this);
		mLocationHelper.startLocationUpdates(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			showProfileDialog();
			break;

		case android.R.id.title:
			AppLog.Log(TAG, "Title Click");
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showProfileDialog() {
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_user_profile);

		TextView tvUserName = (TextView) dialog
				.findViewById(R.id.tv_user_name_dialog);
		tvUserName.setText(friendDetail.getName());
		TextView tvStatus = (TextView) dialog
				.findViewById(R.id.tv_user_status_dialog);
		tvStatus.setText(friendDetail.getStatus());
		ImageView ivUser = (ImageView) dialog
				.findViewById(R.id.iv_user_profile_dialog_user_profile);
		AQuery aQuery = new AQuery(this);
		aQuery.id(ivUser).image(Urls.BASE_IMAGE + friendDetail.getImage());

		TextView tvTell = (TextView) dialog
				.findViewById(R.id.tv_tell_no_profile);
		tvTell.setText(friendDetail.getPhoneCode() + " "
				+ friendDetail.getPhoneNo());
		dialog.show();

	}

	@SuppressLint("InlinedApi")
	private void setUpActionBar() {
		ActionBar mActionBar = getSupportActionBar();

		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		mActionBar.setCustomView(R.layout.custom_action_bar);
		mActionBar.getCustomView().findViewById(R.id.ll_title_part)
				.setOnClickListener(this);
		// mActionBar.setTitle(friendDetail.getName());
		TextView tvTitle = (TextView) mActionBar.getCustomView().findViewById(
				R.id.title_actionbar);
		tvTitle.setText(friendDetail.getName());

		TextView tvSubTitle = (TextView) mActionBar.getCustomView()
				.findViewById(R.id.tv_sub_title_actionbar);
		tvSubTitle.setText(friendDetail.getStatus());

		// mActionBar.setHomeButtonEnabled(true);
		// mActionBar.setDisplayHomeAsUpEnabled(true);
		// mActionBar.setSubtitle("online");
		AQuery query = new AQuery(this);
		// mActionBar.setHomeButtonEnabled(true);
		// mActionBar.setDisplayHomeAsUpEnabled(true);
		ImageView view = (ImageView) mActionBar.getCustomView().findViewById(
				R.id.iv_image_logo);
		view.setOnClickListener(this);

		Bitmap bitmap = query.getCachedImage(Urls.BASE_IMAGE
				+ friendDetail.getImage());
		if (bitmap != null) {
			view.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
		}
	}

	private void init() {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		listOrg = new ArrayList<MessageModel>();
		objTMUtils = new TimelineUtils(NewChatActivity.this);
		mDbAdapter = new DBAdapter(this);
		etMessgae = (EditText) findViewById(R.id.et_message);
		btnSend = (ImageView) findViewById(R.id.iv_send_message);
		btnsendaudio = (ImageView)findViewById(R.id.iv_send_audio);
		btnSend.setOnClickListener(this);
		btnsendaudio.setOnTouchListener(this);
		lvChat = (ListView) findViewById(R.id.lv_chat);
		lvChat.setOnItemClickListener(this);
		findViewById(R.id.iv_attachments_chat_screen).setOnClickListener(this);
		findViewById(R.id.iv_custom_stickers).setOnClickListener(this);

	}

	// private void loadAndSetChatData() {
	// runOnUiThread(new Runnable() {
	// @SuppressLint("SimpleDateFormat")
	// @Override
	// public void run() {
	//
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	// final Calendar cal = Calendar.getInstance();
	// Calendar calendar = Calendar.getInstance();
	//
	// try {
	//
	// if (!mDbAdapter.isOpen()) {
	// mDbAdapter.openForRead();
	// }
	// listOrg.clear();
	// dateList.clear();
	//
	// list = mDbAdapter.getMessages(myDetail.getUserId(),
	// friendDetail.getUserId(),
	// friendDetail.isGroup() ? MessageModel.WHO_GROUP
	// : MessageModel.WHO_PERSONAL, lastMessageId);
	// mDbAdapter.close();
	//
	// if (list.size() == 0) {
	// return;
	// }
	//
	// // asyncResponse.getFriendDetail();
	//
	// // for (int i = 0; i < list.size(); i++) {
	// // // try {
	// // dateList.add(sdf.parse(list.get(i).getTime()));
	// // // } catch (Exception e) {
	// // // e.printStackTrace();
	// // // calendar.setTimeInMillis(Long.parseLong(list.get(i)
	// // // .getTime()));
	// // // dateList.add(calendar.getTime());
	// // // }
	// // }
	// HashSet<Date> listToSet = new HashSet<Date>(dateList);
	//
	// // Creating Arraylist without duplicate values
	// dateList = new ArrayList<Date>(listToSet);
	//
	// for (int i = 0; i < dateList.size(); i++) {
	//
	// cal.setTime(dateList.get(i));
	// System.out.println("Time is " + dateList.get(i));
	// MessageModel item = new MessageModel();
	// item.setTime(sdf.format(dateList.get(i)));
	// listOrg.add(item);
	//
	// mSeparatorsSet.add(listOrg.size() - 1);
	// for (int j = 0; j < list.size(); j++) {
	// Calendar messageTime = Calendar.getInstance();
	// // try {
	// AppLog.Log(TAG, "Date:" + list.get(j).getTime());
	// messageTime.setTime(sdf
	// .parse(list.get(j).getTime()));
	// // } catch (Exception e) {
	// // e.printStackTrace();
	// // // break;
	// // messageTime.setTimeInMillis(Long.parseLong(list
	// // .get(j).getTime()));
	// // dateList.add(calendar.getTime());
	// // }
	//
	// if (cal.getTime().compareTo(messageTime.getTime()) == 0) {
	// listOrg.add(list.get(j));
	// System.out.println("THIS IS FORMATED TIME"
	// + messageTime.getTime().toString());
	// }
	// }
	//
	// }
	//
	// if (mChatAdapter == null) {
	// mChatAdapter = new NewChatAdapter(NewChatActivity.this,
	// listOrg, mSeparatorsSet, myDetail, friendDetail);
	// lvChat.setAdapter(mChatAdapter);
	// } else {
	// mChatAdapter.refresh(listOrg);
	// }
	// scrollMyListViewToBottom();
	// } catch (Exception e) {
	// e.printStackTrace();
	//
	// // try {
	// //
	// // for (int i = 0; i < list.size(); i++) {
	// //
	// // }
	// // HashSet<Date> listToSet = new HashSet<Date>(dateList);
	// //
	// // // Creating Arraylist without duplicate values
	// // dateList = new ArrayList<Date>(listToSet);
	// //
	// // for (int i = 0; i < dateList.size(); i++) {
	// //
	// // cal.setTime(dateList.get(i));
	// // System.out.println("Time is " + dateList.get(i));
	// // MessageModel item = new MessageModel();
	// // item.setTime(sdf.format(dateList.get(i)));
	// // listOrg.add(item);
	// //
	// // mSeparatorsSet.add(listOrg.size() - 1);
	// // for (int j = 0; j < list.size(); j++) {
	// // Calendar messageTime = Calendar.getInstance();
	// // messageTime.setTime(sdf.parse(list.get(j)
	// // .getTime()));
	// //
	// // if (cal.getTime().compareTo(
	// // messageTime.getTime()) == 0) {
	// // listOrg.add(list.get(j));
	// // System.out.println("THIS IS FORMATED TIME"
	// // + messageTime.getTime().toString());
	// // }
	// // }
	// //
	// // }
	// //
	// // if (mChatAdapter == null) {
	// // mChatAdapter = new NewChatAdapter(
	// // NewChatActivity.this, listOrg,
	// // mSeparatorsSet, myDetail, friendDetail);
	// // lvChat.setAdapter(mChatAdapter);
	// // } else {
	// // mChatAdapter.refresh(listOrg);
	// // }
	// // scrollMyListViewToBottom();
	// // } catch (Exception e1) {
	// // e1.printStackTrace();
	// // }
	// }
	// }
	// });
	// }

	private void loadAndSetChatData() {
		runOnUiThread(new Runnable() {
			public void run() {

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				final Calendar cal = Calendar.getInstance();

				try {
					if (!mDbAdapter.isOpen()) {
						mDbAdapter.openForRead();
					}
					listOrg.clear();
					dateList.clear();
					list = mDbAdapter.getMessages(myDetail.getUserId(),
							friendDetail.getUserId(),
							friendDetail.isGroup() ? MessageModel.WHO_GROUP
									: MessageModel.WHO_PERSONAL, 0);

					lastMessageRead(friendDetail.getUserId());

					mDbAdapter.close();
					if (list.size() == 0) {
						return;
					}

					// asyncResponse.getFriendDetail();

					HashSet<Date> listToSet = new HashSet<Date>();
					for (int i = 0; i < list.size(); i++) {
						if (listToSet.add(sdf.parse(list.get(i).getTime()))) {
							dateList.add(sdf.parse(list.get(i).getTime()));
						}
					}

					for (int i = 0; i < dateList.size(); i++) {

						cal.setTime(dateList.get(i));
						MessageModel item = new MessageModel();
						item.setTime(sdf.format(dateList.get(i)));
						listOrg.add(item);

						mSeparatorsSet.add(listOrg.size() - 1);
						for (int j = 0; j < list.size(); j++) {
							Calendar messageTime = Calendar.getInstance();
							messageTime.setTime(sdf
									.parse(list.get(j).getTime()));
							if (cal.getTime().compareTo(messageTime.getTime()) == 0) {
								listOrg.add(list.get(j));
							}
						}
					}

					if (mChatAdapter == null) {
						mChatAdapter = new NewChatAdapter(NewChatActivity.this,
								listOrg, mSeparatorsSet, myDetail, friendDetail);
						lvChat.setAdapter(mChatAdapter);
					} else {
						mChatAdapter.refresh(listOrg);
					}
					scrollMyListViewToBottom();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// if (!mDbAdapter.isOpen()) {
	// mDbAdapter.openForWrite();
	// }
	// // Cursor mCursor = mDbAdapter.getMessages(myDetail.getUserId(),
	// // friendDetail.getUserId(),
	// // friendDetail.isGroup() ? MessageModel.WHO_GROUP
	// // : MessageModel.WHO_PERSONAL);
	// // mChatAdapter = new ChatAdapter(NewChatActivity.this, mCursor,
	// // myDetail, friendDetail, mDbAdapter);
	// // lvChat.setAdapter(mChatAdapter);
	// // mDbAdapter.close();
	// }
	// });

	private void sendMessage(final MessageModel message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AHttpRequest request = new AHttpRequest(NewChatActivity.this,
						NewChatActivity.this);
				request.sendMessage(message.getUserID() + "",
						message.getFriendId() + "", message.getMessage(),
						message.getMessageType() + "", friendDetail.isGroup());
			}
		});
	}

	private void showChatMenuDialog() {
		AppLog.Log(TAG, "Time to show Dialog");

		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_chat_menu);
		dialog.findViewById(R.id.tv_camera_chat_menu).setOnClickListener(this);
		dialog.findViewById(R.id.tv_gellary_chat_menu).setOnClickListener(this);
		dialog.findViewById(R.id.tv_vodeo_chat_menu).setOnClickListener(this);
		dialog.findViewById(R.id.tv_location_chat_menu)
				.setOnClickListener(this);
		dialog.findViewById(R.id.tv_contact_chat_menu).setOnClickListener(this);
		dialog.findViewById(R.id.tv_file_chat_menu).setOnClickListener(this);

		dialog.show();
	}
	
	
	

	@Override
	public void onClick(View v) {

		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}

		switch (v.getId()) {

		case R.id.iv_image_logo:
			finish();
			break;

		case R.id.ll_title_part:

			AppLog.Log(TAG, "MY Logic Works");
			Intent intent;
			if (!friendDetail.isGroup()) {
				intent = new Intent(this, UserDetailActivity.class);
			} else {
				// goto Group detail page
				intent = new Intent(this, GroupDetailActivity.class);
			}
			intent.putExtra(Extra.FRIEND_DETAIL, friendDetail);
			startActivity(intent);

			break;

		case R.id.iv_send_message:
			if (etMessgae.getText().toString().trim().length() != 0) {
				MessageModel message = new MessageModel();
				message.setMessageType(MessageModel.MESSAGE_TYPE_MESSAGE);
				message.setMessage(etMessgae.getText().toString().trim());
				message.setFriendId(friendDetail.getUserId());
				message.setUserID(myDetail.getUserId());
				message.setTime(Utils.getCurrentTimeInFormate());
				message.setWho(friendDetail.isGroup() ? MessageModel.WHO_GROUP
						: MessageModel.WHO_PERSONAL);

				message.isPersonalMessage = friendDetail.isGroup();

				mDbAdapter.openForWrite();

				int sendMessageId = mDbAdapter.insertMessage(message);
				int userUpdateId = mDbAdapter.updateLastMessageId(
						sendMessageId, friendDetail.getUserId());
				lastMessageRead(friendDetail.getUserId());
				if (userUpdateId == 1) {
					AppLog.Log(TAG, "User Update Success");
				} else {
					AppLog.Log(TAG, "User Update UnSucess");
				}
				mDbAdapter.close();
				loadAndSetChatData();
				AppLog.Log(TAG, "Send Messgae Local ID :: " + sendMessageId);
				message.setId(sendMessageId);
				etMessgae.setText("");
				sendMessage(message);
			}
			break;
		

		case R.id.iv_attachments_chat_screen:
			showChatMenuDialog();
			break;

		case R.id.tv_camera_chat_menu:
			Intent cameraIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
			startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
			break;

		case R.id.tv_gellary_chat_menu:
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, SELECT_PICTURE);
			break;

		case R.id.tv_file_chat_menu:
			chooseSticker();
			break;

		case R.id.tv_vodeo_chat_menu:
			takeVedioFromGalaty();
			break;

		case R.id.tv_location_chat_menu:
			getMyLocation();
			break;

		case R.id.tv_contact_chat_menu:
			readContact();
			break;

		case R.id.iv_custom_stickers:
			// goToCusTomeSticker();
			break;

		default:
			break;
		}
	}

	private void chooseSticker() {
		AppLog.Log(TAG, "Execution Sticker");
		SelectDialog dialog = new SelectDialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = dialog.getWindow();
		window.setFlags(LayoutParams.FLAG_FULLSCREEN,
				LayoutParams.FLAG_FULLSCREEN);
		dialog.setTitle("Select a Sticker");
		dialog.setOnItemClickListener(new SelectDialog.OnSelectDialogResult() {
			@Override
			public void finish(String result, int resultCode) {
				Log.e(TAG, "activity : image id : " + result);
				// showToast("image id : "+result);
				if (resultCode == SelectDialog.RESULT_OK) {
					String imageName = result;

					MessageModel model = new MessageModel();
					model.setUserID(myDetail.getUserId());
					model.setFriendId(friendDetail.getUserId());
					model.setMessage(imageName);
					model.setMessageType(MessageModel.MESSAGE_TYPE_STICKER);
					model.setWho(friendDetail.isGroup() ? MessageModel.WHO_GROUP
							: MessageModel.WHO_PERSONAL);
					model.setTime(Utils.getCurrentTimeInFormate());
					mDbAdapter.openForRead();
					mDbAdapter.insertMessage(model);
					mDbAdapter.close();
					sendMessage(model);
					loadAndSetChatData();
				}
			}
		});
		dialog.show();
	}

	private void getMyLocation() {

		if (mLocationHelper.getMyLocation() == null) {
			Utils.showToast(getString(R.string.error_no_location_found), this);
			return;
		}

		String latitude = String.valueOf(mLocationHelper.getMyLocation()
				.getLatitude());
		String longitude = String.valueOf(mLocationHelper.getMyLocation()
				.getLongitude());
		MessageModel model = new MessageModel();
		model.setMessage(latitude + "," + longitude);
		model.setMessageType(MessageModel.MESSAGE_TYPE_LOCATION);
		model.setFriendId(friendDetail.getUserId());
		model.setUserID(myDetail.getUserId());
		model.setTime(Utils.getCurrentTimeInFormate());
		if (friendDetail.isGroup()) {
			model.isPersonalMessage = false;
		}
		model.setWho(friendDetail.isGroup() ? MessageModel.WHO_GROUP
				: MessageModel.WHO_PERSONAL);
		mDbAdapter.openForWrite();
		model.setMessageId(mDbAdapter.insertMessage(model));
		int userUpdateId = mDbAdapter.updateLastMessageId(model.getMessageId(),
				friendDetail.getUserId());
		lastMessageRead(friendDetail.getUserId());
		if (userUpdateId == 1) {
			AppLog.Log(TAG, "User Update Success");
		} else {
			AppLog.Log(TAG, "User Update UnSucess");
		}
		mDbAdapter.close();
		loadAndSetChatData();
		sendMessage(model);
	}

	private void takeVedioFromGalaty() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("video/*");
		startActivityForResult(photoPickerIntent, SELECT_VEDIO);
	}

	private void readContact() {

		pd = ProgressDialog.show(NewChatActivity.this, "", "fatching Data",
				false);
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		contactPickerIntent
				.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}

		String path;

		if (resultCode == RESULT_OK) {
			switch (requestCode) {

			case SELECT_PICTURE:
				if (null == data)
					return;
				Uri selectedImage = data.getData();
				String picturePath = Utils.getRealPathFromURI(selectedImage,
						this);
				AppLog.Log(TAG, "Local Path ::" + picturePath);
				sendImage(picturePath, MessageModel.MESSAGE_TYPE_IMAGE);
				break;

			case CONTACT_PICKER:
				manageContact(data);
				break;

			case SELECT_VEDIO:
				Uri selectedFile = data.getData();
				path = Utils.getRealPathFromURI(selectedFile, this);
				manageVideo(path);
				break;

			case CAMERA_REQUEST_CODE:
				handleCameraImage();
				break;

			case REQUEST_TO_ADD_CONTACT:
				AppLog.Log(TAG, "Contact data::" + data.getExtras());
				break;

			default:
				break;
			}
		}
	}

	private void handleCameraImage() {
		if (filePath == null) {
			Utils.showToast("Problem in fetch camera image",
					getApplicationContext());
		} else {
			sendImage(filePath, MessageModel.MESSAGE_TYPE_IMAGE);
		}

	}

	private void manageVideo(String path) {

		AppLog.Log("Manage Video Who", friendDetail.isGroup() + "");

		MessageModel model = new MessageModel();
		model.setMessage(path);
		model.setMessageType(MessageModel.MESSAGE_TYPE_VEDIO);
		model.setFriendId(friendDetail.getUserId());
		model.setUserID(myDetail.getUserId());
		model.setWho(friendDetail.isGroup() ? MessageModel.WHO_GROUP
				: MessageModel.WHO_PERSONAL);
		model.setMessageStatus(MessageModel.STATUS_UPLOADING);
		model.setTime(Utils.getCurrentTimeInFormate());
		mDbAdapter.openForWrite();
		model.setId(mDbAdapter.insertMessage(model));
		model.isPersonalMessage = !friendDetail.isGroup();
		mDbAdapter.close();

		loadAndSetChatData();

		Intent intent = new Intent(this, VideoUploadService.class);
		intent.putExtra("data", model);
		startService(intent);

	}

	private void manageContact(final Intent data) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				Cursor cursor = null;
				Bitmap bitmap = null;

				Uri result = data.getData();
				cursor = getContentResolver().query(result, null, null, null,
						null);
				if (cursor == null || !cursor.moveToFirst()) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Utils.showToast("Selection Fail",
									NewChatActivity.this);
						}
					});
					return;
				}
				String contactName = cursor
						.getString(
								cursor.getColumnIndex((ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))
						.toString().trim();
				String contactNumber = cursor.getString(cursor
						.getColumnIndex((ContactsContract.CommonDataKinds.Phone.NUMBER)));
				String email = cursor.getString(cursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

				String photo = null;
				try {
					photo = cursor.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
					Log.i("TAG", "Activity Result: Old:" + photo);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (photo != null) {
					bitmap = getImageBitMap(Uri.parse(photo));
				}

				Log.i("TAG", contactName + " " + contactNumber);
				cursor.close();
				String contactImageName = bitmap == null ? "users/default.jpg"
						: myDetail.getUserId() + "_contact_" + contactName
								+ "_" + System.currentTimeMillis();
				String message = contactName + "," + contactNumber + ","
						+ email + "," + contactImageName;
				MessageModel model = new MessageModel();
				model.setMessage(message);
				model.setFriendId(friendDetail.getUserId());
				model.setUserID(myDetail.getUserId());
				model.setMessageType(MessageModel.MESSAGE_TYPE_CONTACT);
				model.setTime(Utils.getCurrentTimeInFormate());
				model.isPersonalMessage = friendDetail.isGroup();
				model.setWho(friendDetail.isGroup() ? MessageModel.WHO_GROUP
						: MessageModel.WHO_PERSONAL);
				mDbAdapter.openForRead();
				model.setId(mDbAdapter.insertMessage(model));
				// sendContactToServer(model, bitmap);
				if (bitmap == null) {
					sendMessage(model);
				} else {
					HttpRequest request = new HttpRequest();
					List<NameValuePair> list = new ArrayList<NameValuePair>();
					list.add(new BasicNameValuePair("content", Utils
							.encodeToBase64(bitmap)));
					list.add(new BasicNameValuePair("name", contactImageName));
					if (Utils.isOnline(getApplicationContext())) {
						String responseString = null;
						try {
							responseString = request.postData(
									Urls.UPLOAD_IMAGE, list);
							AppLog.Log(TAG, "response during application");
							sendMessage(model);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				}
			}

		}).start();

	}

	private Bitmap getImageBitMap(Uri uri) {
		Bitmap imageBitmap = null;
		try {
			imageBitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageBitmap;
	}

	/**
	 * 123
	 * 
	 * 
	 * @param path
	 *            sending image path,responsible for uploading image
	 */
	private void sendImage(final String path, int messageType) {

		AppLog.Log(TAG, "Image patgh :: " + path);
		if (path == null) {
			Log.i(TAG, "Can not load this image");
			return;
		}
		File file = new File(path);
		if (file != null && file.exists()) {
			MessageModel message = new MessageModel();
			message.setMessageStatus(MessageModel.STATUS_UPLOADING);
			message.setMessage(path);
			message.setFriendId(friendDetail.getUserId());
			message.setUserID(myDetail.getUserId());
			message.setWho(friendDetail.isGroup() ? MessageModel.WHO_GROUP
					: MessageModel.WHO_PERSONAL);
			message.isPersonalMessage = !friendDetail.isGroup();
			message.setMessageType(messageType);
			message.setTime(Utils.getCurrentTimeInFormate());
			if (!mDbAdapter.isOpen()) {
				mDbAdapter.openForWrite();
			}
			int insertedMessageId = mDbAdapter.insertMessage(message);
			mDbAdapter.close();
			AppLog.Log(TAG, "Last Inserted Message Id :: " + insertedMessageId);
			message.setId(insertedMessageId);
			loadAndSetChatData();

			Intent intentService = new Intent(this,
					ImageUploadingServices.class);
			intentService.putExtra("data", message);
			startService(intentService);
		} else {
			AppLog.Log(TAG, "No Such File is exitst");
		}

	}
	
	
	
	private void sendAudio(final String path, int messageType) {

		AppLog.Log(TAG, "Audio patgh :: " + path);
		if (path == null) {
			Log.i(TAG, "Can not load this Audio");
			return;
		}
		File file = new File(path);
		if (file != null && file.exists()) {
			MessageModel message = new MessageModel();
			message.setMessageStatus(MessageModel.STATUS_UPLOADING);
			message.setMessage(path);
			message.setFriendId(friendDetail.getUserId());
			message.setUserID(myDetail.getUserId());
			message.setWho(friendDetail.isGroup() ? MessageModel.WHO_GROUP
					: MessageModel.WHO_PERSONAL);
			message.isPersonalMessage = !friendDetail.isGroup();
			message.setMessageType(messageType);
			message.setTime(Utils.getCurrentTimeInFormate());
			if (!mDbAdapter.isOpen()) {
				mDbAdapter.openForWrite();
			}
			int insertedMessageId = mDbAdapter.insertMessage(message);
			mDbAdapter.close();
			AppLog.Log(TAG, "Last Inserted Message Id :: " + insertedMessageId);
			message.setId(insertedMessageId);
			loadAndSetChatData();

			Intent intentService = new Intent(this,
					AudioUploadingService.class);
			intentService.putExtra("data", message);
			startService(intentService);
		} else {
			AppLog.Log(TAG, "No Such File is exitst");
		}

	}

	private Uri getTempUri() {

		File file = new File(Environment.getExternalStorageDirectory()
				+ Constant.ROOT_FOLDER_NAME);
		if (!file.exists()) {
			file.mkdir();
		}
		File file2 = new File(Environment.getExternalStorageDirectory()
				+ Constant.ROOT_FOLDER_NAME + "/" + Constant.FOLDER_IMAGE);
		if (!file2.exists()) {
			file2.mkdir();
		}

		String imagePath = Environment.getExternalStorageDirectory() + "/"
				+ Constant.ROOT_FOLDER_NAME + "/" + Constant.FOLDER_IMAGE + "/"
				+ "Camera_" + System.currentTimeMillis() + ".jpg";

		filePath = imagePath;
		File imageFile = new File(imagePath);

		if (imageFile.exists()) {
			imageFile.delete();
		}

		if (!imageFile.exists()) {
			try {
				new File(imageFile.getParent()).mkdirs();
				imageFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Uri imageUri = Uri.fromFile(imageFile);
		return imageUri;
	}

	@Override
	public void onReqestComplete(AHttpResponse response) {
		AppLog.Log(TAG, "response :: " + response.getRootJsonObject());
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// mChatAdapter.notifyDataSetChanged();
				loadAndSetChatData();
			}
		});

	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mHandleMessageDelivery);
		try {
			if (mLastSeenThread != null && mLastSeenThread.isAlive()) {
				mLastSeenThread.interrupt();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Constant.IS_WINDOW_OPEN = false;
		super.onDestroy();
	}

	private final BroadcastReceiver mHandleMessageDelivery = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			AppLog.Log(TAG, "LocaBroadCast Received::MESSAGE_UPDATE");
			Log.i("TAG", "New Chat Activity mHandleMessageDelivery");
			loadAndSetChatData();
		}
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int location,
			long arg3) {	
		
		// Cursor cursor = mChatAdapter.getCursor();
		MessageModel model = listOrg.get(location);
		String message = model.getMessage();
		int senderID = model.getUserID();
		AppLog.Log(TAG, "Message :: " + message);
		Intent intent;
		switch (model.getMessageType()) {
		case MessageModel.MESSAGE_TYPE_IMAGE:
			intent = new Intent(this, FullScreenImage.class);
			if (senderID == myDetail.getUserId()) {
				intent.putExtra(Extra.LOCAL_PATH, message);
			} else {
				intent.putExtra(Extra.IMAGE_URL, Urls.BASE_IMAGE + message);
			}
			startActivity(intent);
			break;

		case MessageModel.MESSAGE_TYPE_VEDIO:

			if (senderID == myDetail.getUserId()) {
				AppLog.Log(TAG, "Ready for play my friend");
				File file = new File(message);
				intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "video/*");
				startActivity(intent);
				return;
			}

			String localPath = Environment.getExternalStorageDirectory()
					.getAbsoluteFile()
					+ File.separator
					+ Constant.ROOT_FOLDER_NAME
					+ File.separator
					+ Constant.FOLDER_IMAGE + File.separator + message;

			File file = new File(localPath);
			if (file.exists()) {
				// proceed for play.
				AppLog.Log(TAG, "**********Go for Play Now ***********");

				if (model.getMessageStatus() == MessageModel.STATUS_DOWNLOADING) {
					AppLog.Log(TAG, "************* Downloading *************");
					return;
				}

				intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "video/*");
				startActivity(intent);
			} else {
				model.setMessageType(MessageModel.STATUS_DOWNLOADING);
				MessageModel model1 = new MessageModel();
				model1.setMessage(message);
				model1.setMessageId(model.getMessageId());
				model1.setUserID(model.getUserID());
				model1.setFriendId(model.getFriendId());
				AppLog.Log(TAG, "**** Required to downlod *******");
				AppLog.Log(TAG, "Vid_Message : " + message);

				intent = new Intent(this, DownloadService.class);
				intent.putExtra("url", message);
				intent.putExtra("receiver", new DownloadReceiver(new Handler()));
				startService(intent);
				loadAndSetChatData();
			}

			break;

		case MessageModel.MESSAGE_TYPE_STICKER:
			//Toast.makeText(NewChatActivity.this, "Audio clicked", Toast.LENGTH_LONG).show();
			// intent = new Intent(this, FullScreenImage.class);
			// String stickerPath = Environment.getExternalStorageDirectory()
			// .getAbsoluteFile()
			// + File.separator
			// + Constant.ROOT_FOLDER_NAME
			// + File.separator
			// + Constant.STICKER_DIR + File.separator + message;
			// intent.putExtra(Extra.LOCAL_PATH, stickerPath);
			// startActivity(intent);
			break;

		case MessageModel.MESSAGE_TYPE_LOCATION:
			if (message.contains(",")) {
				String[] msg = message.split(",");
				String latitude = msg[0];
				String longitude = msg[1];
				openMap("Location of " + friendDetail.getName(), latitude,
						longitude);
			}
			break;

			
		/*case MessageModel.MESSAGE_TYPE_AUDIO:
			Toast.makeText(NewChatActivity.this, "Audio clicked", Toast.LENGTH_LONG).show();
			break;*/
			
		case MessageModel.MESSAGE_TYPE_CONTACT:
			if (senderID == myDetail.getUserId()) {
				return;
			} else {
				intent = new Intent(Intent.ACTION_INSERT);
				intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
				intent.putExtra(ContactsContract.Intents.Insert.NAME,
						message.split(",")[0]);
				intent.putExtra(ContactsContract.Intents.Insert.PHONE,
						message.split(",")[1]);
				intent.putExtra(ContactsContract.Intents.Insert.EMAIL,
						message.split(",")[2]);

				startActivityForResult(intent, REQUEST_TO_ADD_CONTACT);
			}
			break;

		case MessageModel.MESSAGE_TYPE_AUDIO:
			if (senderID == myDetail.getUserId()) {
				return;
			} else {
				/*model.setMessageType(MessageModel.STATUS_DOWNLOADING);
				MessageModel model1 = new MessageModel();
				model1.setMessage(message);
				model1.setMessageId(model.getMessageId());
				model1.setUserID(model.getUserID());
				model1.setFriendId(model.getFriendId());
				AppLog.Log(TAG, "**** Required to downlod *******");
				AppLog.Log(TAG, "Audio_Message : " + message);

				intent = new Intent(this, DownloadService.class);
				intent.putExtra("url", message);
				intent.putExtra("receiver", new DownloadReceiver(new Handler()));
				startService(intent);*/
				loadAndSetChatData();
			}
			break;
		default:
			break;
		}

	}
	private void openMap(String label, String latitude, String longitude) {
		String uriBegin = "geo:" + latitude + "," + longitude;
		String query = latitude + "," + longitude + "(" + label + ")";
		String encodedQuery = Uri.encode(query);
		String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
		Uri uri = Uri.parse(uriString);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	private void getUserLastSeen() {

		if (friendDetail.isGroup()) {
			getSupportActionBar().setSubtitle("tap to know more");
			return;
		}

		AHttpRequest request = new AHttpRequest(this, new RequestCallback() {

			@Override
			public void onReqestComplete(final AHttpResponse response) {
				if (getSupportActionBar() != null) {
					if (response.getRootJsonObject().has("last_seen")) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									ActionBar mActionBar = getSupportActionBar();
									TextView tvSubTitle = (TextView) mActionBar
											.getCustomView()
											.findViewById(
													R.id.tv_sub_title_actionbar);
									tvSubTitle.setText(Html.fromHtml(response
											.getRootJsonObject().getString(
													"last_seen")));
									mLastSeenThread = null;
									mLastSeenThread = new Thread(
											new Runnable() {

												@Override
												public void run() {
													try {
														Thread.sleep(LAST_SEEN_UPDATE);
														handler.sendEmptyMessage(0);
													} catch (InterruptedException e) {
														e.printStackTrace();
													}
												}
											});
									mLastSeenThread.start();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});

					}
				}
			}
		});
		request.getLastSeen(friendDetail.getUserId() + "", myDetail.getUserId()
				+ "");
	}

	@Override
	public void onLocationUpdate(Location location) {
		mLocationHelper.destroy();
	}

	private void updateInfo() {
		if (friendDetail.isGroup()) {
			getGtoupDetailFromServer(friendDetail);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Constant.IS_WINDOW_OPEN = false;
	}

	private void getGtoupDetailFromServer(final UserDetail detail) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("id", detail
						.getUserId() + ""));
				HttpRequest httpRequest = new HttpRequest();
				try {
					String responseString = httpRequest.postData(
							Urls.GET_GROUP_MEMBER, nameValuePairs);
					JSONObject jsonObject = new JSONObject(responseString);
					AppLog.Log(TAG, "Getting Group Detail response :: "
							+ responseString);
					if (jsonObject.getBoolean("success")) {
						JSONArray array = jsonObject.getJSONArray("details");
						ArrayList<UserDetail> list = new ArrayList<UserDetail>();

						AppLog.Log(TAG, "GRoup Members ***************** "
								+ array.length());
						mDbAdapter.openForRead();
						mDbAdapter.deleteAllGroupUsers(detail.getUserId());
						mDbAdapter.close();

						UserDetail detail;
						for (int i = 0; i < array.length(); i++) {
							detail = new UserDetail();
							jsonObject = array.getJSONObject(i);
							detail.setUserId(jsonObject.getInt("id"));
							detail.setGroupId(jsonObject.getInt("group_id"));
							detail.setAdmin(jsonObject.getInt("is_admin") == 1);

							detail.setName(jsonObject.getString("name"));
							detail.setPhoneCode(jsonObject
									.getString("phone_code"));
							detail.setPhoneNo(jsonObject.getString("phone"));
							detail.setImage(jsonObject.getString("image"));
							detail.setStatus(jsonObject.getString("status"));
							mDbAdapter.openForRead();

							// if (dbAdapter.getUserDetail(detail.getUserId())
							// == null)
							if (detail.isAdmin()) {
								friendDetail.setAdminId(detail.getUserId());
								mDbAdapter.insertOrUpdateGroup(friendDetail);
							}
							mDbAdapter.insertUpdateGroupMember(detail);
							mDbAdapter.close();
							// list.add(detail);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private class DownloadReceiver extends ResultReceiver {
		public DownloadReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			if (resultCode == DownloadService.UPDATE_PROGRESS) {
				int progress = resultData.getInt("progress");
				int mySpecialProgress = resultData.getInt("progress1");
				if (progress == 100) {
					loadAndSetChatData();
					if (mChatAdapter != null) {
						mChatAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	}

	private void scrollMyListViewToBottom() {
		lvChat.post(new Runnable() {
			@Override
			public void run() {
				lvChat.setSelection(mChatAdapter.getCount() - 1);
			}
		});
	}

	private void lastMessageRead(int userId) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = preferences.edit();
		String key = userId + Pref.USER_LAST_MSG_STATUS;
		Log.i("TAG", "NewChat GROUP key : " + key + " , Msg : "
				+ Constant.READ_MSG);
		editor.putInt(key, Constant.READ_MSG);
		editor.commit();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Constant.IS_WINDOW_OPEN = true;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Constant.IS_WINDOW_OPEN = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Constant.IS_WINDOW_OPEN = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		Constant.IS_WINDOW_OPEN = false;
	}
	
	public String sendAudioToServer(){
		String filename;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSSS");
		String currentDateandTime = sdf.format(new Date());
		String getusername = preferences.getString(Pref.USER_NAME, null);
		
		String output = getusername.replaceAll("\\s+",""); 
		
		filename = "AD_"+currentDateandTime + "_" + output;
		File filepath = Environment.getExternalStorageDirectory();
		File dir = new File(filepath.getAbsolutePath()
				+ "/Gonechat/AudioMessage/");
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		
		outputFile = filepath.getAbsolutePath()
				+ "/Gonechat/AudioMessage/" + filename +".mp3";

			      myAudioRecorder = new MediaRecorder();
			      myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			      myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			      myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
			      myAudioRecorder.setOutputFile(outputFile);
			      
			      
			      try {
			    	  
			          myAudioRecorder.prepare();
			          myAudioRecorder.start();
			       } catch (IllegalStateException e) {
			          // TODO Auto-generated catch block
			          e.printStackTrace();
			       } catch (IOException e) {
			          // TODO Auto-generated catch block
			          e.printStackTrace();
			       }
			       
			       Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
			       
				return outputFile;

		
	}
	
	public void saveAuddio(String audioPath){
		
		 myAudioRecorder.stop();
	      myAudioRecorder.release();
	      myAudioRecorder  = null;
	     
	      Toast.makeText(getApplicationContext(), "Audio recorded successfully",
	      Toast.LENGTH_LONG).show();
	      sendAudio(audioPath, MessageModel.MESSAGE_TYPE_AUDIO);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		
	    case MotionEvent.ACTION_DOWN:
	        v.setPressed(true);
	         audioPath = sendAudioToServer();
	         Log.d("audio path", audioPath);
	        break;
	    case MotionEvent.ACTION_UP:
	    case MotionEvent.ACTION_OUTSIDE:
	    case MotionEvent.ACTION_CANCEL:
	        v.setPressed(false);
	        saveAuddio(audioPath);
	        break;
	    case MotionEvent.ACTION_POINTER_DOWN:
	        break;
	    case MotionEvent.ACTION_POINTER_UP:
	        break;
	    case MotionEvent.ACTION_MOVE:
	        break;
	    }

	    return true;
		
 
	}
	public void playAudio(View v) {
		 int position = lvChat.getPositionForView((View) v.getParent()); 
		MessageModel model = listOrg.get(position);
		String message = model.getMessage();
		setSeekPos(position);
		mChatAdapter.playSong(message);
	    mChatAdapter.notifyDataSetChanged();
	}
	
	public void playFAudio(View v) {
		 int position = lvChat.getPositionForView((View) v.getParent()); 
		MessageModel model = listOrg.get(position);
		String message = model.getMessage();
		setSeekPos(position);
		mChatAdapter.playSongF("/sdcard/Gonechat/AudioMessage/" + message);
		
	   mChatAdapter.notifyDataSetChanged();
	}
	
	public void setSeekPos(int pos) {
	    this.pos = pos;
	    Log.e("Seeckbar position from Activity", ""+pos);
	}

	public int getSeekPos() {
		
	    return pos;
	}
}
