package com.chat.bigpex;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import com.bigpex.gonechat.R;
import com.chat.bigpex.adapter.FavoriteListAdapter;
import com.chat.bigpex.adapter.PastChatAdapter;
import com.chat.bigpex.database.DBAdapter;
import com.chat.bigpex.helper.AppLog;
import com.chat.bigpex.helper.Constant.Extra;
import com.chat.bigpex.helper.Constant.MyActions;
import com.chat.bigpex.helper.MyPrefrence;
import com.chat.bigpex.helper.TimelineUtils;
import com.chat.bigpex.internet.AHttpRequest;
import com.chat.bigpex.internet.Urls;
import com.chat.bigpex.models.AdModel;
import com.chat.bigpex.models.UserDetail;
import com.chat.bigpex.services.ContactUpdateServices;
import com.chat.bigpex.services.StickerManageServices;
import com.chat.mainclasses.AndroidAppUtils;
import com.chat.mainclasses.AppConstants;
import com.chat.mainclasses.Callback;
import com.chat.mainclasses.WebServiceHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class RecentChatList extends BaseActivity implements
		OnItemClickListener, OnClickListener {

	private static final String TAG = "RecentChatList";

	private static final String TUTORIALS = "tutorials";

	public static final int USER_ONLINE = 1;
	public static final int USER_OFFLINE = 0;
	private static List<AdModel> adModel;
	private DBAdapter dbAdapter;
	private List<UserDetail> pastChatList, mFavList;
	private ListView lvFavorite, lvPastChats;
     TimelineUtils objTMUtils;
	private PastChatAdapter mPastChatAdapter;
	private FavoriteListAdapter mFavoriteListAdapter;
private ImageView imgAdd;
	private UserDetail myDetail;
	// receivers
	private MyReceiver receiver;

	private Dialog mOptionsDialog;

	private LinearLayout llLoading;
	private ProgressBar pBar;
	private TextView tvLoadingMessage;

	private EditText etSearch;

	// private Thread mLastSeenUpdate;

	// private Handler mHandler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// setLastSeen(1);
	// setUpLastSeenThread();
	// };
	// };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_recent_chant_list);
		myDetail = new MyPrefrence(this).getDetail();

		manageTutorials();

		SlidingMenu menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidth(15);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setMenu(R.layout.right_side_list_view);

		llLoading = (LinearLayout) menu.findViewById(R.id.ll_loading);
		tvLoadingMessage = (TextView) menu
				.findViewById(R.id.tv_loading_message);
		pBar = (ProgressBar) menu.findViewById(R.id.pb_loading);
		imgAdd = (ImageView)findViewById(R.id.imgAdd);
		etSearch = (EditText) menu
				.findViewById(R.id.et_search_user_recent_chat);
		setUpSearch();
		manageActionBar();
		dbAdapter = new DBAdapter(this);
		lvFavorite = (ListView) menu.findViewById(R.id.lv_my_favorite);
		lvFavorite.setOnItemClickListener(this);
		lvPastChats = (ListView) findViewById(android.R.id.list);
		
		objTMUtils = new TimelineUtils(RecentChatList.this);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
       
		loadData();

		fetchAllContactAndSendItServer();

		registerRecievers();
		// setUpLastSeenThread();

		updateGroupsDetails();
		registerForContextMenu(lvPastChats);
		loadImageinAddView();
		
		imgAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	


	private void loadData() {
		dbAdapter.openForRead();
		mFavList = dbAdapter.getAllUsers();
		pastChatList = dbAdapter.getPastChats();
		dbAdapter.close();
		dbAdapter.openForWrite();
		dbAdapter.addAllGroups(pastChatList);
		dbAdapter.close();

		if (mFavList.size() != 0) {
			llLoading.setVisibility(View.GONE);
		} else {
			llLoading.setVisibility(View.VISIBLE);
		}

		mFavoriteListAdapter = new FavoriteListAdapter(getLayoutInflater(),
				mFavList, this);
		lvFavorite.setAdapter(mFavoriteListAdapter);

		mPastChatAdapter = new PastChatAdapter(getLayoutInflater(),
				pastChatList, this);
		lvPastChats.setAdapter(mPastChatAdapter);
		lvPastChats.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(RecentChatList.this,
						NewChatActivity.class);

				UserDetail detail = pastChatList.get(position);
				AppLog.Log(TAG, "detail is :: " + detail);
				if (detail.getUserId() == myDetail.getUserId()) {
					return;
				}
				intent.putExtra(Extra.FRIEND_DETAIL, pastChatList.get(position));
				AppLog.Log(TAG, pastChatList.get(position).getName());
				startActivity(intent);
			}
		});

	}

	private void setUpSearch() {
		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mFavoriteListAdapter.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void manageActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.recent_chant_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void showMenuOptionsDialog() {

		if (mOptionsDialog != null && mOptionsDialog.isShowing()) {
			mOptionsDialog.dismiss();
			return;
		}

		mOptionsDialog = new Dialog(this);
		mOptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mOptionsDialog.setContentView(R.layout.options_menu);

		mOptionsDialog.findViewById(R.id.tv_setting_option_menu)
				.setOnClickListener(this);
		mOptionsDialog.findViewById(R.id.tv_status_option_menu)
				.setOnClickListener(this);
		mOptionsDialog.findViewById(R.id.tv_new_group_option_menu)
				.setOnClickListener(this);
		mOptionsDialog.findViewById(R.id.iv_profile_option_menu)
				.setOnClickListener(this);
		mOptionsDialog.findViewById(R.id.tv_user_name_option_menu)
				.setOnClickListener(this);

		TextView tvUserName = (TextView) mOptionsDialog
				.findViewById(R.id.tv_user_name_option_menu);
		tvUserName.setText(myDetail.getName());
		ImageView ivUser = (ImageView) mOptionsDialog
				.findViewById(R.id.iv_profile_option_menu);
		AQuery aQuery = new AQuery(this);
		AppLog.Log(TAG, "image :: " + myDetail.getImage());

		aQuery.id(ivUser).image(Urls.BASE_IMAGE + myDetail.getImage());

		mOptionsDialog.show();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_info:
			showMenuOptionsDialog();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void registerRecievers() {
		receiver = new MyReceiver();

		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);

		IntentFilter filter = new IntentFilter(MyActions.CONTACT_LIST_UPDATE);
		manager.registerReceiver(receiver, filter);
		filter = new IntentFilter(MyActions.MESSAGE_UPDATE);
		manager.registerReceiver(receiver, filter);
		filter = new IntentFilter(MyActions.SEND_MESSAGE);
		manager.registerReceiver(receiver, filter);

	}

	@Override
	protected void onResume() {
		super.onResume();

		dbAdapter.openForRead();
		pastChatList.clear();
		pastChatList.addAll(dbAdapter.getPastChats());
		if (!dbAdapter.isOpen()) {
			dbAdapter.openForRead();
		}
		dbAdapter.addAllGroups(pastChatList);
		dbAdapter.close();

		mPastChatAdapter.notifyDataSetChanged();

		if (pastChatList.size() == 0) {
			findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
		} else {
			findViewById(android.R.id.empty).setVisibility(View.GONE);
		}

		AppLog.Log(TAG, "Past Chat List Size :: " + pastChatList.size());

		setLastSeen(1);
	}

	private void setLastSeen(int online) {
		AHttpRequest aHttpRequest = new AHttpRequest(this);
		aHttpRequest.setLastSeen(myDetail.getUserId() + "", online + "");
	}

	// @Override
	// protected void onPause() {
	// mLastSeenUpdate.interrupt();
	// super.onPause();
	// }

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			showMenuOptionsDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void fetchAllContactAndSendItServer() {
		Intent intent = new Intent(this, ContactUpdateServices.class);
		startService(intent);
		intent = new Intent(this, StickerManageServices.class);
		startService(intent);
	}

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			// && intent.getAction().equals(MyActions.CONTACT_LIST_UPDATE)
			if (intent.getAction() != null) {
				// Toast.makeText(RecentChatList.this, "Contact Updated",
				// Toast.LENGTH_LONG).show();
				mFavList.clear();
				if (!dbAdapter.isOpen()) {
					dbAdapter.openForRead();
				}

				mFavList.addAll(dbAdapter.getAllUsers());
				mFavoriteListAdapter = new FavoriteListAdapter(
						getLayoutInflater(), mFavList, RecentChatList.this);
				lvFavorite.setAdapter(mFavoriteListAdapter);
				// dbAdapter.close();
				mFavoriteListAdapter.notifyDataSetChanged();
				if (mFavList.size() == 0) {
					llLoading.setVisibility(View.VISIBLE);
					tvLoadingMessage.setText("No Contacts");
					pBar.setVisibility(View.GONE);
				} else {
					llLoading.setVisibility(View.GONE);
				}

				pastChatList.clear();
				pastChatList.addAll(dbAdapter.getPastChats());
				if (!dbAdapter.isOpen()) {
					dbAdapter.openForRead();
				}
				dbAdapter.addAllGroups(pastChatList);

				dbAdapter.close();
				mPastChatAdapter.notifyDataSetChanged();
			}

			if (mPastChatAdapter != null) {
				mPastChatAdapter.notifyDataSetChanged();
			}
			if (mFavoriteListAdapter != null) {
				mFavoriteListAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> view, View arg1, int position,
			long arg3) {
		Intent intent = new Intent(this, NewChatActivity.class);
		intent.putExtra(Extra.FRIEND_DETAIL,
				(UserDetail) mFavoriteListAdapter.getItem(position));
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {

		if (mOptionsDialog != null && mOptionsDialog.isShowing()) {
			mOptionsDialog.dismiss();
		}

		switch (v.getId()) {
		case R.id.tv_setting_option_menu:
			goToSetting();
			break;

		case R.id.tv_status_option_menu:
			goToStatus();
			break;

		case R.id.tv_new_group_option_menu:
			goToNewGourp();
			break;

		case R.id.iv_profile_option_menu:
		case R.id.tv_user_name_option_menu:
			goToEditProfile();
			break;

		default:
			break;
		}
	}

	private void goToSetting() {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
	}

	private void goToStatus() {
		Intent intent = new Intent(this, StatusActivity.class);
		startActivity(intent);
	}

	private void goToNewGourp() {
		Intent intent = new Intent(this, CreateNewGroupActivity.class);
		startActivity(intent);
	}

	private void goToEditProfile() {
		Intent intent = new Intent(this, EditProfileActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		setLastSeen(0);
		super.onDestroy();
	}

	private void updateGroupsDetails() {
		AHttpRequest aHttpRequest = new AHttpRequest(this);
		aHttpRequest.updateAllGroupsDetail(myDetail.getUserId() + "");
	}

	private void manageTutorials() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (preferences.getBoolean(TUTORIALS, true)) {
			findViewById(R.id.iv_tutorials).setVisibility(View.VISIBLE);
			findViewById(R.id.iv_tutorials).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							findViewById(R.id.iv_tutorials).setVisibility(
									View.GONE);
						}
					});

			Editor editor = preferences.edit();
			editor.putBoolean(TUTORIALS, false);
			editor.commit();
		} else {
			findViewById(R.id.iv_tutorials).setVisibility(View.GONE);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add("Delete Chat");

	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		AHttpRequest request = new AHttpRequest();
		AdapterContextMenuInfo minfo = (AdapterContextMenuInfo) item
				.getMenuInfo();

		UserDetail friendDetails = (UserDetail) mPastChatAdapter
				.getItem(minfo.position);

		String response;
		dbAdapter.openForRead();
		if (friendDetails.getAdminID() != 0) {

			dbAdapter.deleteGroupAndMembers(friendDetails.getUserId());
			AppLog.Log(TAG, "group id" + friendDetails.getUserId() + "");
			request.deleteChat(myDetail.getUserId() + "",
					friendDetails.getUserId() + "", 2 + "");
		} else {
			AppLog.Log("Friend ID ", friendDetails.getUserId() + "");
			AppLog.Log("My ID ", myDetail.getUserId() + "");
			dbAdapter.deleteChat(myDetail.getUserId(),
					friendDetails.getUserId());

			request.deleteChat(myDetail.getUserId() + "",
					friendDetails.getUserId() + "", 1 + "");
		}
		dbAdapter.close();
		Intent intent = new Intent(this, RecentChatList.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		return super.onContextItemSelected(item);

	}
	public void loadImageinAddView(){
String URL = "http://janmeshjani.com/gonechat/get_ad.php";
		
		WebServiceHandler.WebServiceCall(RecentChatList.this, URL, AppConstants.GET,null, AppConstants.content_json, false, "", "", new Callback() {
			
			@Override
			public void run(Object result, int statusCode) {
				// TODO Auto-generated method stub
				
				ObjectMapper mapper = AndroidAppUtils.getJsonMapper();
				try {
					adModel = mapper.readValue((String) result,
							new TypeReference<List<AdModel>>() {
						
							});
					
					final String id = adModel.get(0).getId();
					String imgurl = Urls.BASE_URL+adModel.get(0).getAd_image();
					final String adurl = adModel.get(0).getAd_url();
					Log.e("IMAGE URL", imgurl);
					AQuery query = new AQuery(RecentChatList.this);
					int width =objTMUtils.loadSqureImag();
					
					URL url = new URL(imgurl);
					Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					bmp = Bitmap.createScaledBitmap(bmp, width, 50, true);
					imgAdd.setImageBitmap(bmp);
					imgAdd.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adurl));
							startActivity(browserIntent);
							
						}
					});
					
				/*	query.id(imgAdd)
					.image(imgurl, true, true,
							width, 0, null, AQuery.FADE_IN );*/
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
		
			}
		});

	}
	
}
