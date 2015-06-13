package com.chat.bigpex;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import com.bigpex.gonechat.R;
import com.chat.bigpex.adapter.GroupMemberAdapter;
import com.chat.bigpex.database.DBAdapter;
import com.chat.bigpex.helper.AppLog;
import com.chat.bigpex.helper.MyPrefrence;
import com.chat.bigpex.helper.Utils;
import com.chat.bigpex.helper.Constant.Extra;
import com.chat.bigpex.internet.AHttpRequest;
import com.chat.bigpex.internet.AHttpResponse;
import com.chat.bigpex.internet.HttpRequest;
import com.chat.bigpex.internet.RequestCallback;
import com.chat.bigpex.internet.Urls;
import com.chat.bigpex.models.UserDetail;

public class GroupDetailActivity extends BaseActivity implements
		OnClickListener, RequestCallback, OnItemClickListener {

	private static final String TAG = "GroupDetailActivity";

	private ListView lvMembers;

	private UserDetail groupDetail, myDetail;
	private ArrayList<UserDetail> memberList;
	private GroupMemberAdapter mGroupMemberAdapter;
	private DBAdapter mDbAdapter;

	private TextView tvCreatedDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_detail);

		myDetail = new MyPrefrence(this).getDetail();
		groupDetail = getIntent().getParcelableExtra(Extra.FRIEND_DETAIL);
		if (groupDetail == null) {
			AppLog.Log(TAG, "group detail null");
			return;
		}

		AppLog.Log(TAG, "groupDetail.getGroupID():: " + groupDetail.getUserId()
				+ " name ::" + groupDetail.getName() +" Group ID "+groupDetail.getGroupID());

		getGroupInfo();
		
		TextView tvGroupName = (TextView) findViewById(R.id.tv_group_name_group_detail);
		tvGroupName.setText(groupDetail.getName());

		lvMembers = (ListView) findViewById(R.id.lv_group_members);
		mDbAdapter = new DBAdapter(this);

		// mDbAdapter.openForRead();
		// memberList = mDbAdapter.getGroupMembers(groupDetail.getUserId());
		// mDbAdapter.close();

		// AppLog.Log(TAG, "memberList size ::" + memberList.size());

		View footerView = getLayoutInflater().inflate(
				R.layout.footer_group_member_list, null);
		lvMembers.addFooterView(footerView);
		 tvCreatedDate = (TextView) footerView
				.findViewById(R.id.tv_group_created_date);
		TextView tvAddParticipate = (TextView) footerView
				.findViewById(R.id.tv_add_new_members);
		tvAddParticipate.setOnClickListener(this);
		// this field only visible for the admin
		// only addmin can add new members
		if (myDetail.getUserId() == groupDetail.getAdminID()) {
			tvAddParticipate.setVisibility(View.VISIBLE);
		} else {
			tvAddParticipate.setVisibility(View.GONE);
		}

		//tvCreatedDate.setText(new Date().toString());
		
		footerView.findViewById(R.id.btn_delete_and_exit_group)
				.setOnClickListener(this);

		// mGroupMemberAdapter = new GroupMemberAdapter(this, memberList,
		// myDetail, groupDetail);
		// lvMembers.setAdapter(mGroupMemberAdapter);
		// lvMembers.setOnItemClickListener(this);

		ImageView ivIcon = (ImageView) findViewById(R.id.iv_group_pic_group_detail);
		ivIcon.setOnClickListener(this);
		AQuery aQuery = new AQuery(this);
		aQuery.id(ivIcon).image(Urls.BASE_IMAGE + groupDetail.getImage());

		setUpActionBar();
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillUserList();
	}

	private void fillUserList() {
		mDbAdapter.openForRead();
		memberList = mDbAdapter.getGroupMembers(groupDetail.getUserId());
		mDbAdapter.close();

		mGroupMemberAdapter = new GroupMemberAdapter(this, memberList,
				myDetail, groupDetail);
		lvMembers.setAdapter(mGroupMemberAdapter);
		lvMembers.setOnItemClickListener(this);
	}

	private void setUpActionBar() {
		ActionBar mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_delete_and_exit_group:
			deleteAndExitGroup();
			break;

		case R.id.tv_add_new_members:
			goToAddingNewMembers();
			break;

		case R.id.iv_group_pic_group_detail:
			if (groupDetail.getImage() == null
					|| groupDetail.getImage().trim().length() == 0
					|| groupDetail.getImage().equals("users/default.jpg")) {
				Utils.showToast(getString(R.string.text_no_photo), this);
				return;
			}

			Intent intent = new Intent(this, FullScreenImage.class);
			intent.putExtra(Extra.IMAGE_URL,
					Urls.BASE_IMAGE + groupDetail.getImage());
			startActivity(intent);

			break;

		default:
			break;
		}
	}

	private void goToAddingNewMembers() {
		Intent intent = new Intent(this, SelectGroupUserList.class);
		intent.putParcelableArrayListExtra(Extra.USER_LIST, memberList);
		intent.putExtra(Extra.FRIEND_DETAIL, groupDetail);
		startActivity(intent);
	}

	private void deleteAndExitGroup() {
		if (Utils.isOnline(this)) {
			pd = ProgressDialog.show(this, "Loading", "Please Wait");
			AHttpRequest aHttpRequest = new AHttpRequest(this, this);
			aHttpRequest.removeAndExitGroup(myDetail.getUserId() + "",
					groupDetail.getUserId() + "");
		} else {

		}
	}

	private ProgressDialog pd;

	@Override
	public void onReqestComplete(AHttpResponse response) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
			}
		});

		if (response == null) {
			AppLog.Log(TAG, "Group Detail Activity Response is null");
			return;
		}
		// if (response.isSuccess) {
		DBAdapter adapter = new DBAdapter(this);
		adapter.openForRead();
		adapter.deleteGroupAndMembers(groupDetail.getUserId());
		adapter.close();
		Intent intent = new Intent(this, RecentChatList.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		// } else {
		//
		// }

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long l) {

		if (myDetail.getUserId() == groupDetail.getAdminID()) {
			if (memberList.get(position).getUserId() == groupDetail
					.getAdminID()) {
			} else {
				showAdminDialog(memberList.get(position));
			}
		} else {
			showDialog(memberList.get(position));
		}

		// if (myDetail.getUserId() == groupDetail.getAdminID()) {
		// Utils.showToast("Admin", this);
		// }
		// if (myDetail.getUserId() == memberList.get(position).getUserId()) {
		// return;
		// }

	}

	private void showDialog(final UserDetail detail) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String[] items = new String[2];
		items[0] = "Send Message " + detail.getName();
		items[1] = "Call " + detail.getName();
		builder.setItems(items, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				// case 0:
				// removeUsers(detail);
				// break;

				case 0:
					sendMessage(detail);
					break;

				case 1:
					Intent callIntent = new Intent(Intent.ACTION_DIAL);
					callIntent.setData(Uri.parse("tel:" + detail.getPhoneCode()
							+ detail.getPhoneNo()));
					startActivity(callIntent);
					break;

				default:
					break;
				}
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void showAdminDialog(final UserDetail detail) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String[] items = new String[3];
		items[0] = "Remove " + detail.getName();
		items[1] = "Send Message " + detail.getName();
		items[2] = "Call " + detail.getName();
		builder.setItems(items, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0:
					removeUsers(detail);
					break;

				case 1:
					sendMessage(detail);
					break;

				case 2:
					Intent callIntent = new Intent(Intent.ACTION_DIAL);
					callIntent.setData(Uri.parse("tel:" + detail.getPhoneCode()
							+ detail.getPhoneNo()));
					startActivity(callIntent);
					break;

				default:
					break;
				}
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void sendMessage(UserDetail detail) {
		Uri uri = Uri.parse("smsto:" + detail.getPhoneCode()
				+ detail.getPhoneNo());
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", getString(R.string.text_mesage_body));
		startActivity(it);
	}

	private void removeUsers(final UserDetail userDetail) {

		if (!Utils.isOnline(this)) {
			Utils.showNoInternetMessage(this);
			return;
		}

		pd = ProgressDialog.show(this, "Loading", "Please wait");

		new Thread(new Runnable() {
			@Override
			public void run() {

				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("id", groupDetail.getUserId()
						+ ""));
				pairs.add(new BasicNameValuePair("type", "0"));
				pairs.add(new BasicNameValuePair("membe_ids", userDetail
						.getUserId() + ""));

				AppLog.Log(TAG, "Params :: " + pairs.toString());

				HttpRequest httpRequest = new HttpRequest();
				try {
					String responseString = httpRequest.postData(
							Urls.JOIN_UNJOIN_GROUP, pairs);
					parseTheRemoveMemberResponse(responseString, userDetail);
				} catch (final Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Utils.showToast(e.getMessage(),
									GroupDetailActivity.this);
							if (pd != null && pd.isShowing()) {
								pd.dismiss();
							}
						}
					});
				}

			}
		}).start();
	}
	private void getGroupInfo() {

		if (!Utils.isOnline(this)) {
			Utils.showNoInternetMessage(this);
			return;
		}

		pd = ProgressDialog.show(this, "Loading", "Please wait");

		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("user_id", ""+myDetail.getUserId()));
				list.add(new BasicNameValuePair("id", ""+groupDetail.getUserId()));
				
				HttpRequest request = new HttpRequest();
				try {
					String responseString = request.postData(Urls.GROUPINFO,
							list);
					pd.dismiss();
					AppLog.Log("group Info", responseString);
					AHttpResponse response = new AHttpResponse(responseString,
							true);
					if (response.isSuccess) {
						tvCreatedDate.setText(response.getRootJsonObject().getJSONObject("details").getString("time"));						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// when user successfully added
	// {
	// "details": {
	// "id": "72",
	// "time": "2014-05-24 15:05:03",
	// "type": "",
	// "user_id": "71",
	// "friend_id": "",
	// "who": "4",
	// "message": "",
	// "delivery_time": "",
	// "image": "group/default.jpg",
	// "user_status": "",
	// "phone": "",
	// "phone_code": "",
	// "name": "Pappus group",
	// "status": ""
	// },
	// "success": true,
	// "message": "User successfully added."
	// }
	//

	private void parseTheRemoveMemberResponse(String response,
			final UserDetail detail) throws JSONException {
		final JSONObject jsonObject = new JSONObject(response);
		if (jsonObject.getBoolean("success")) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						Utils.showToast(jsonObject.getString("message"),
								GroupDetailActivity.this);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					memberList.remove(detail);
					mDbAdapter.openForRead();
					mDbAdapter.deleteUerFromGropup(detail.getUserId(),
							groupDetail.getUserId());
					mDbAdapter.close();
					Intent loginscreen = new Intent(GroupDetailActivity.this,
							RecentChatList.class);
					finish();
					loginscreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(loginscreen);
				}
			});
			// JSONArray array = jsonObject.getJSONArray("details");
			// for (int i = 0; i < array.length(); i++) {
			// UserDetail detail = new UserDetail();
			// jsonObject = array.getJSONObject(i);
			// detail.setUserId(jsonObject.getInt("id"));
			// }
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}
	}
}
