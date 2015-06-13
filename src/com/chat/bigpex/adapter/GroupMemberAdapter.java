package com.chat.bigpex.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.bigpex.gonechat.R;
import com.chat.bigpex.internet.Urls;
import com.chat.bigpex.models.UserDetail;

public class GroupMemberAdapter extends BaseAdapter {

	private List<UserDetail> list;
	private Context context;
	private UserDetail myDetail;
	private UserDetail groupDetail;
	private AQuery aQuery;

	public GroupMemberAdapter(Context context, List<UserDetail> list,
			UserDetail myDetail, UserDetail groupDetail) {
		this.context = context;
		this.list = list;
		this.myDetail = myDetail;
		this.groupDetail = groupDetail;
		aQuery = new AQuery(context);
		this.context = context;
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
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_group_members,
					null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_user_name_group_member);
			holder.tvAdmin = (TextView) convertView
					.findViewById(R.id.tv_admin_group_text);
			holder.ivUserImage = (ImageView) convertView
					.findViewById(R.id.iv_user_photo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		UserDetail detail = list.get(position);
		if (groupDetail.getAdminID() == detail.getUserId()) {
			holder.tvAdmin.setVisibility(View.VISIBLE);
		} else {
			holder.tvAdmin.setVisibility(View.GONE);
		}
		aQuery.id(holder.ivUserImage)
				.image(Urls.BASE_IMAGE + detail.getImage())
				.progress(R.id.pb_loading);

		holder.tvName.setText(detail.getName());

		return convertView;
	}

	class ViewHolder {
		TextView tvName, tvAdmin;
		ImageView ivUserImage;
	}

}
