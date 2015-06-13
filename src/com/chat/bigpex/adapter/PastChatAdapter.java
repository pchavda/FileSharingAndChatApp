package com.chat.bigpex.adapter;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.bigpex.gonechat.R;
import com.chat.bigpex.helper.Constant;
import com.chat.bigpex.helper.EmoticonsHelper;
import com.chat.bigpex.helper.Constant.Pref;
import com.chat.bigpex.internet.Urls;
import com.chat.bigpex.models.UserDetail;

public class PastChatAdapter extends BaseAdapter {

	private List<UserDetail> list;
	private LayoutInflater inflater;
	private AQuery aQuery;
	private SharedPreferences preferences;
	private String key;

	public PastChatAdapter(LayoutInflater inflater, List<UserDetail> list,
			Context context) {
		this.inflater = inflater;
		aQuery = new AQuery(context);
		this.list = list;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item_past_chats, null);
			holder.ivImage = (ImageView) convertView
					.findViewById(R.id.iv_user_image);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_user_name);
			holder.tvStatus = (TextView) convertView
					.findViewById(R.id.tv_last_message);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		UserDetail detail = list.get(position);
		aQuery.id(holder.ivImage).image(Urls.BASE_IMAGE + detail.getImage());
		holder.tvName.setText(detail.getName());
		holder.tvName.setTextColor(inflater.getContext().getResources()
				.getColor(R.color.black));

		if (detail.getLastMessage() == null || detail.getLastMessage() == "") {
			holder.tvStatus.setText(EmoticonsHelper.getSmiledText(
					inflater.getContext(), "HEY!"));
		} else {
			holder.tvStatus.setText(EmoticonsHelper.getSmiledText(
					inflater.getContext(), detail.getLastMessage()));
		}

		key = detail.getUserId() + Pref.USER_LAST_MSG_STATUS;
		if (preferences.getInt(key, Constant.READ_MSG) == Constant.READ_MSG) {
			holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.double_right, 0, 0, 0);
		} else {
			holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.button_play, 0, 0, 0);
		}
		return convertView;

	}

	class ViewHolder {
		ImageView ivImage;
		TextView tvName;
		TextView tvStatus;
	}
}
