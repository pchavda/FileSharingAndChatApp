package com.chat.bigpex.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.bigpex.gonechat.R;
import com.chat.bigpex.internet.Urls;
import com.chat.bigpex.models.UserDetail;

public class FavoriteListAdapter extends BaseAdapter implements Filterable {

	private LayoutInflater inflater;
	private List<UserDetail> list;
	private List<UserDetail> displayList;
	private AQuery aQuery;

	public FavoriteListAdapter(LayoutInflater inflater, List<UserDetail> list,
			Context context) {
		this.list = list;
		displayList = new ArrayList<UserDetail>();
		displayList.addAll(list);
		this.inflater = inflater;
		aQuery = new AQuery(context);
	}

	@Override
	public int getCount() {
		return displayList.size();
	}

	@Override
	public Object getItem(int position) {
		return displayList.get(position);
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
			convertView = inflater.inflate(R.layout.list_item_user_list, null);
			holder.ivImage = (ImageView) convertView
					.findViewById(R.id.iv_user_image);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_user_name);
			// holder
			// .tvStatus =
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		UserDetail detail = displayList.get(position);
		// aQuery.id(holder.ivImage).image(Urls.BASE_IMAGE + detail.getImage());

		aQuery.id(holder.ivImage).image(Urls.BASE_IMAGE + detail.getImage());
		Log.i("TAG", "Image URL : " + (Urls.BASE_IMAGE + detail.getImage()));
		holder.tvName.setText(detail.getName());

		return convertView;
	}

	class ViewHolder {
		ImageView ivImage;
		TextView tvName, tvStatus;
	}

	private MyFilter filter;

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new MyFilter();
		}
		return filter;
	}

	private class MyFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults filterResults = new FilterResults();
			UserDetail detail;
			displayList.clear();

			if (constraint.toString().trim().length() == 0) {
				displayList.addAll(list);
			} else {
				for (int i = 0; i < list.size(); i++) {
					detail = list.get(i);
					if (detail
							.getName()
							.toLowerCase()
							.trim()
							.contains(
									constraint.toString().toLowerCase().trim())) {
						displayList.add(detail);
					}
				}
			}

			filterResults.count = displayList.size();
			filterResults.values = filterResults;

			return filterResults;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			notifyDataSetChanged();
		}
	}

}
