package com.chat.bigpex.adapter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.bigpex.gonechat.R;
import com.chat.bigpex.helper.TimelineUtils;
import com.chat.bigpex.helper.Constant.Extra;
import com.chat.bigpex.internet.Urls;
import com.chat.bigpex.models.GetTimelineData;
import com.chat.bigpex.models.UserDetail;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class CustomAdapter extends ArrayAdapter<GetTimelineData> {

	private Activity context;
	public List<GetTimelineData> gTData;
	private ImageView tmImage1;
	// private TextView txtPostdata, txtUserName, txtUserPhoNo;
	// private LinearLayout imgLayout, ll_loadVideo;
	// Bitmap bitmap;
	ProgressDialog pDialog;
	private static final int TYPES_COUNT = 3;
	private static final int TYPE_MSG = 0;
	private static final int TYPE_IMG = 1;
	private static final int TYPE_VDO = 1;
	int coment = 0;
	TimelineUtils objTmUtils;
	private UserDetail friendDetail;
	
	public CustomAdapter(Activity context, List<GetTimelineData> gTData) {
		super(context, R.layout.custom_layout, gTData);

		this.context = context;
		this.gTData = gTData;
		objTmUtils = new TimelineUtils(context);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

	}

	class ViewHolder

	{
		LinearLayout imgLayout, msglayout, ll_loadVideo;
		ImageView tmImage1, iv_prof_msg;
		ImageView video_player_view;
		TextView txtPostData, txtFramDate, txtUserName, txtUserPhoNo,
				mtxtFramDate, mtxtUserName;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		View view = convertView;
		ViewHolder viewHolder = null;
		if (view == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.custom_layout, null, false);
			viewHolder = new ViewHolder();

			// Log.d("IMAGE ADAPTER", "IMAGE ADAPTER" );
			/*
			 * if(gTData.get(position).getType().equals("0")){ Log.d("VIEW",
			 * "MSG"); view = inflator.inflate(R.layout.tm_messageview,
			 * null,false); viewHolder.txtTMPostData =
			 * (TextView)view.findViewById(R.id.txtTMPostdata); }
			 * if(gTData.get(position).getType().equals("1")){ Log.d("VIEW",
			 * "IMG"); view = inflator.inflate(R.layout.tm_imageview,
			 * null,false); viewHolder.tmImage =
			 * (ImageView)view.findViewById(R.id.tmImage);
			 * viewHolder.txtTMFramDate =
			 * (TextView)view.findViewById(R.id.txtTMFramDate);
			 * viewHolder.txtTMUserName =
			 * (TextView)view.findViewById(R.id.txtTMUserName);
			 * 
			 * } if(gTData.get(position).getType().equals("2")){ Log.d("VIEW",
			 * "VDO"); view = inflator.inflate(R.layout.tm_videoview,
			 * null,false); viewHolder.video_player_view =
			 * (VideoView)view.findViewById(R.id.video_player_view);
			 * viewHolder.vtxtTMFramDate =
			 * (TextView)view.findViewById(R.id.vtxtTMFramDate);
			 * viewHolder.vtxtTMUserName =
			 * (TextView)view.findViewById(R.id.vtxtTMUserName);
			 * 
			 * }
			 */

			viewHolder.imgLayout = (LinearLayout) view
					.findViewById(R.id.ll_loadImgL);
			viewHolder.msglayout = (LinearLayout) view
					.findViewById(R.id.ll_loadMsg);
			viewHolder.ll_loadVideo = (LinearLayout) view
					.findViewById(R.id.ll_loadVideo);

			viewHolder.txtPostData = (TextView) view
					.findViewById(R.id.txtPostdata);
			viewHolder.txtFramDate = (TextView) view
					.findViewById(R.id.txtFramDate);
			viewHolder.txtUserName = (TextView) view
					.findViewById(R.id.txtUserName);
			viewHolder.txtUserPhoNo = (TextView) view
					.findViewById(R.id.txtUserPhoNo);

			viewHolder.mtxtFramDate = (TextView) view
					.findViewById(R.id.mtxtFramDate);
			viewHolder.mtxtUserName = (TextView) view
					.findViewById(R.id.mtxtUserName);
			/*viewHolder.mtxtUserPhoNo = (TextView) view
					.findViewById(R.id.mtxtUserPhoNo);
*/
			viewHolder.tmImage1 = (ImageView) view.findViewById(R.id.tmImage1);
			viewHolder.iv_prof_msg = (ImageView) view.findViewById(R.id.mimageView1);
			viewHolder.video_player_view = (ImageView) view
					.findViewById(R.id.video_player_view);

			view.setTag(viewHolder);
		} else {

			view = convertView;
		}
		viewHolder = (ViewHolder) view.getTag();

		if (gTData.get(position).getType().equals("0")) {

			Log.d("Message Type", "" + gTData.get(position).getType());
			if (viewHolder.imgLayout.getVisibility() == View.VISIBLE)
				viewHolder.imgLayout.setVisibility(View.GONE);

			if (viewHolder.msglayout.getVisibility() == View.GONE)
				viewHolder.msglayout.setVisibility(View.VISIBLE);

			coment = TYPE_MSG;

			viewHolder.txtPostData.setText(gTData.get(position).getContent());
			
			Intent intent = context.getIntent();
			String profileimage = context.getIntent().getStringExtra("ProfileImage");
			
			AQuery query = new AQuery(context);
			Bitmap bitmap = query.getCachedImage(Urls.BASE_IMAGE
					+ profileimage);
			if (bitmap != null) {
				viewHolder.iv_prof_msg.setImageDrawable(new BitmapDrawable(context.getResources(), bitmap));
			}
			
			
			
			viewHolder.mtxtUserName.setText(intent.getStringExtra("Name"));
			//viewHolder.mtxtUserPhoNo.setText(intent.getStringExtra("Pno"));
			String dae = objTmUtils.convertDateToTime(gTData.get(position)
					.getDatetime());
			viewHolder.mtxtFramDate.setText(dae);
			// viewHolder.mtxtFramDate.setText(gTData.get(position).getDatetime());

		} else if (gTData.get(position).getType().equals("1")) {
			// Log.d("IMG Type", ""+gTData.get(position).getType());
			if (viewHolder.msglayout.getVisibility() == View.VISIBLE) {
				viewHolder.msglayout.setVisibility(View.GONE);
			}

			if (viewHolder.imgLayout.getVisibility() == View.GONE) {
				viewHolder.imgLayout.setVisibility(View.VISIBLE);
			}
			coment = TYPE_IMG;
			Intent intent = context.getIntent();

			viewHolder.txtUserName.setText(intent.getStringExtra("Name"));
			viewHolder.txtUserPhoNo.setText(intent.getStringExtra("Pno"));

			viewHolder.tmImage1.getLayoutParams().height = objTmUtils
					.loadSqureImag();

			String localPath = objTmUtils.getFilePath(gTData.get(position)
					.getContent(), "1");

			Log.e("Imagepath NEWWWW", gTData.get(position)
					.getContent());
			int height = objTmUtils.loadSqureImag();
			if(objTmUtils.isFileExist(localPath)){					
			String img = localPath;
			File imgFile = new File(img);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2; // try to decrease decoded image
			options.inPurgeable = true; // if necessary purge pixels into disk
			options.inScaled = true;
			Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),
					options);
			bm = Bitmap.createScaledBitmap(bm, height, height, true);
			viewHolder.tmImage1.setImageBitmap(bm);
			} else {
				viewHolder.tmImage1.getLayoutParams().height = height;
				viewHolder.tmImage1.getLayoutParams().width = height;
			AQuery query = new AQuery(context);			
			query.id(viewHolder.tmImage1)
			.image(gTData.get(position)
					.getContent(), true, true,
					height, 0, null, AQuery.FADE_IN, AQuery.RATIO_PRESERVE);
			}

			String dae = objTmUtils.convertDateToTime(gTData.get(position)
					.getDatetime());
			Log.e("IMAG DATE", "" + gTData.get(position)
					.getDatetime());
			viewHolder.txtFramDate.setText(dae);

		}

		else if (gTData.get(position).getType().equals("2")) {


			if (viewHolder.msglayout.getVisibility() == View.VISIBLE) {
				viewHolder.msglayout.setVisibility(View.GONE);

			}
			if (viewHolder.imgLayout.getVisibility() == View.GONE) {
				viewHolder.imgLayout.setVisibility(View.VISIBLE);

			}

			String video = gTData.get(position).getContent();
			// MediaController mcr = new MediaController(context);

			try {
				// mcr.setAnchorView(viewHolder.video_player_view);
				// Set video link (mp4 format )
				// Uri uri=Uri.parse(video);
				// viewHolder.video_player_view.setMediaController(mcr);
				// viewHolder.video_player_view.setVideoURI(uri);
				// viewHolder.video_player_view.start();
				Log.d("Video", video);

			} catch (Exception e) {
				// TODO: handle exception
			}

			// final Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(
			// video, MediaStore.Video.Thumbnails.MINI_KIND );
			viewHolder.tmImage1.getLayoutParams().height = objTmUtils
					.loadSqureImag();
			Bitmap vbm = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.video_thumb_img);
			int height = objTmUtils.loadSqureImag();
			vbm = Bitmap.createScaledBitmap(vbm, height, height, true);

			viewHolder.tmImage1.setImageBitmap(vbm);
			Intent intent = context.getIntent();
			viewHolder.txtUserName.setText(intent.getStringExtra("Name"));
			viewHolder.txtUserPhoNo.setText(intent.getStringExtra("Pno"));
			String dae = objTmUtils.convertDateToTime(gTData.get(position)
					.getDatetime());
			Log.e("VDO DATE", "" + gTData.get(position)
					.getDatetime());
			viewHolder.txtFramDate.setText(dae);

			// Uri uri=Uri.parse(video);
			// viewHolder.video_player_view.setVideoPath(gTData.get(position).getContent());
			// viewHolder.vtxtFramDate.setText(gTData.get(position).getDatetime());

		}
		return view;
	}
	


}
