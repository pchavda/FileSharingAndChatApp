package com.chat.bigpex.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigpex.gonechat.R;
import com.chat.bigpex.models.RowItem;

public class ListOfMemberAdapter extends BaseAdapter {
	
	
	Context ctx;
	 List<RowItem> rowItems;
	 
	
	public ListOfMemberAdapter(Context ctx, List<RowItem> rowItems) {
	
		this.ctx = ctx;
		this.rowItems = rowItems;
	}
	
	 private class ViewHolder {
	        ImageView imageView;
	        TextView txtTitle;
	   
	    }
	 
	 @Override
		public View getView(int position, View convertView, ViewGroup parent) {
		 ViewHolder holder = null;
         
	        LayoutInflater mInflater = (LayoutInflater) 
	            ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.custom_list_item, null);
	            holder = new ViewHolder();
	         
	            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
	            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
	            convertView.setTag(holder);
	        }
	        else {
	            holder = (ViewHolder) convertView.getTag();
	        }
	         
	        RowItem rowItem = (RowItem) getItem(position);
	         
	      
	        holder.txtTitle.setText(rowItem.getTitle());
	        holder.imageView.setImageResource(rowItem.getImageId());
			return convertView;
		}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return rowItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return rowItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return rowItems.indexOf(getItem(position));
	}

	

}
