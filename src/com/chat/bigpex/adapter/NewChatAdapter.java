package com.chat.bigpex.adapter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.bigpex.gonechat.R;
import com.chat.bigpex.NewChatActivity;
import com.chat.bigpex.helper.AppLog;
import com.chat.bigpex.helper.Constant;
import com.chat.bigpex.internet.Urls;
import com.chat.bigpex.models.MessageModel;
import com.chat.bigpex.models.UserDetail;
import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;

public class NewChatAdapter extends BaseAdapter implements
		PinnedSectionListAdapter, SeekBar.OnSeekBarChangeListener {
	private static final String TAG = "NewChatAdapter";

	public static final int TYPE_ITEM = 0;
	public static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
	
	private Handler durationHandler = new Handler();
	
	private DecimalFormat df;
	
	boolean userTouch;

	private static final String STICKERPATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator
			+ Constant.ROOT_FOLDER_NAME
			+ File.separator
			+ Constant.STICKER_DIR
			+ File.separator;

	private static final String VEDIO_PATH = Environment
			.getExternalStorageDirectory().getAbsoluteFile()
			+ File.separator
			+ Constant.ROOT_FOLDER_NAME
			+ File.separator
			+ Constant.FOLDER_IMAGE;

	private Context context;
	private UserDetail myDetail;
	private UserDetail friendDetail;

	private List<MessageModel> list;
	private TreeSet<Integer> mSeparatorsSet;

	private LayoutInflater inflater;

	SimpleDateFormat simpleDateFormat;
	private  MediaPlayer mp;
	private AQuery aQuery;
	NewChatActivity objNewChatAct;
	public NewChatAdapter(Context context, List<MessageModel> list,
			TreeSet<Integer> mSeparatorsSet, UserDetail myDetail,
			UserDetail friendDetail) {
		df = new DecimalFormat("00");
		mp = new MediaPlayer();
		this.context = context;
		this.list = list;
		this.mSeparatorsSet = mSeparatorsSet;
		this.myDetail = myDetail;
		this.friendDetail = friendDetail;
		objNewChatAct = new NewChatActivity();
		//objTMUtils = new TimelineUtils((Activity)context);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		aQuery = new AQuery(context);
		aQuery.clear();

		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	}
	
	



	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	public void refresh(List<MessageModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	View hView;
	private ViewHolder holder;
	private SectionViewHolder sectionHolder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		hView = convertView;

		final MessageModel model = list.get(position);

		int type = getItemViewType(position);

		if (convertView == null) {

			holder = new ViewHolder();
			sectionHolder = new SectionViewHolder();

			switch (type) {
			case TYPE_ITEM:
				hView = inflater.inflate(R.layout.list_item_chat_new, null);

				holder.tvMessage = (TextView) hView
						.findViewById(R.id.tv_message_chat_item);
				holder.tvTime = (TextView) hView
						.findViewById(R.id.tv_send_time_chat_item);
				holder.tvUserName = (TextView) hView
						.findViewById(R.id.tv_user_sender_name_chat_item);
				holder.ivMessageDevider = (ImageView) hView
						.findViewById(R.id.iv_message_devider);
				holder.ivMessageStaus = (ImageView) hView
						.findViewById(R.id.iv_mesage_send_status);
				// holder.llMain = (RelativeLayout) view
				// .findViewById(R.id.ll_chat_item_main);
				holder.llChat = (LinearLayout) hView
						.findViewById(R.id.rl_main_chat_item);
				holder.rl_audio = (RelativeLayout) hView
						.findViewById(R.id.rl_audio_part);
				holder.iv_message_image = (ImageView) hView
						.findViewById(R.id.iv_message_image);
				holder.play = (ImageView)hView.findViewById(R.id.play);
				holder.progressBar1 = (ProgressBar)hView.findViewById(R.id.progressBar1);
				//holder.play.setOnClickListener((OnClickListener) context);
				holder.seekBar1 = (SeekBar)hView.findViewById(R.id.seekBar1);
					//holder.seekBar1.setOnSeekBarChangeListener(this);	
				holder.pBar = (ProgressBar) hView.findViewById(R.id.pb_loading);
				holder.rLayout = (RelativeLayout) hView
						.findViewById(R.id.rl_image_part);
				holder.ivMessageStaus.setVisibility(View.GONE);

				// friends
				holder.tvMessageF = (TextView) hView
						.findViewById(R.id.tv_message_chat_item_f);
				holder.tvTime = (TextView) hView
						.findViewById(R.id.tv_send_time_chat_item);
				holder.tvUserNameF = (TextView) hView
						.findViewById(R.id.tv_user_sender_name_chat_item_f);
				holder.ivMessageDeviderF = (ImageView) hView
						.findViewById(R.id.iv_message_devider_f);
				holder.ivMessageStausF = (ImageView) hView
						.findViewById(R.id.iv_mesage_send_status_f);
				// holder.llMainF = (RelativeLayout) view
				// .findViewById(R.id.ll_chat_item_main);
				holder.rl_audioF = (RelativeLayout) hView
						.findViewById(R.id.rl_audio_part_f);
				holder.playF = (ImageView)hView.findViewById(R.id.play_f);
				//holder.play.setOnClickListener((OnClickListener) context);
				holder.seekBar1F = (SeekBar)hView.findViewById(R.id.seekBar1_f);

				holder.llChatF = (LinearLayout) hView
						.findViewById(R.id.rl_main_chat_item_f);
				holder.iv_message_imageF = (ImageView) hView
						.findViewById(R.id.iv_message_image_f);
				holder.pBarF = (ProgressBar) hView
						.findViewById(R.id.pb_loading_f);
				holder.rLayoutF = (RelativeLayout) hView
						.findViewById(R.id.rl_image_part_f);
				holder.ivMessageStausF.setVisibility(View.GONE);
				// ends friens
				holder.tvDisplayName = (TextView) hView
						.findViewById(R.id.tv_display_name);

				holder.tvTime = (TextView) hView
						.findViewById(R.id.tv_send_time_chat_item);
				holder.tvTimeF = (TextView) hView
						.findViewById(R.id.tv_send_time_chat_item_f);

				hView.setTag(holder);
				break;
				
			
			

			case TYPE_SEPARATOR:
				hView = inflater.inflate(R.layout.chat_header_layout, null);
				sectionHolder.tv = (TextView) hView
						.findViewById(R.id.tvChatHedaerDate);
				hView.setTag(sectionHolder);
				break;
			}
		} else {
			switch (type) {
			case TYPE_ITEM:
				holder = (ViewHolder) hView.getTag();
				break;
			case TYPE_SEPARATOR:
				sectionHolder = (SectionViewHolder) hView.getTag();
				break;

			}
		}

		switch (type) {
		case TYPE_ITEM:

			int senderID = model.getUserID();
			boolean isFrindsMessage = false;
			Calendar calendar = Calendar.getInstance();
			if (senderID == myDetail.getUserId()) {// sender(me)

				holder.llChat.setVisibility(View.VISIBLE);
				holder.llChatF.setVisibility(View.GONE);
				try {

					Date date = simpleDateFormat.parse(model.getTime());
					calendar.setTime(date);
					holder.tvTime.setText(df.format(calendar
							.get(Calendar.HOUR_OF_DAY))
							+ ":"
							+ df.format(calendar.get(Calendar.MINUTE)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (friendDetail.isGroup()) {
					holder.tvDisplayName.setVisibility(View.VISIBLE);
					holder.tvDisplayName.setText(model.getDisplayName());
				} else {
					holder.tvDisplayName.setVisibility(View.GONE);
				}
				isFrindsMessage = true;
				holder.llChat.setVisibility(View.GONE);
				holder.llChatF.setVisibility(View.VISIBLE);
				try {
					Date date = simpleDateFormat.parse(model.getTime());
					calendar.setTime(date);
					holder.tvTimeF.setText(df.format(calendar
							.get(Calendar.HOUR_OF_DAY))
							+ ":"
							+ df.format(calendar.get(Calendar.MINUTE)));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			int messageType = model.getMessageType();

			String message = model.getMessage();
			String time = model.getTime();

			holder.llChatF.setBackgroundResource(R.drawable.chat_bg_white);
			holder.llChat.setBackgroundResource(R.drawable.chat_bg_green);

			switch (messageType) {
			case MessageModel.MESSAGE_TYPE_MESSAGE:
				// holder.tvUserName.setTextColor(color);
				if (!isFrindsMessage) {
					holder.tvMessage.setText(message);
					holder.iv_message_image.setVisibility(View.GONE);
					
					holder.rl_audio.setVisibility(View.GONE);
					
					
					holder.tvMessage.setVisibility(View.VISIBLE);
					holder.rLayout.setVisibility(View.GONE);
				} else {
					holder.tvMessageF.setText(message);
					holder.iv_message_imageF.setVisibility(View.GONE);
					holder.rl_audioF.setVisibility(View.GONE);
					holder.tvMessageF.setVisibility(View.VISIBLE);
					holder.rLayoutF.setVisibility(View.GONE);
				}
				break;

			case MessageModel.MESSAGE_TYPE_IMAGE:

				if (isFrindsMessage) {
					AppLog.Log("Chatu",
							"-----------------Friend----------------");
					holder.rl_audioF.setVisibility(View.GONE);
					holder.tvMessageF.setVisibility(View.GONE);
					holder.iv_message_imageF.setVisibility(View.VISIBLE);
					holder.rLayoutF.setVisibility(View.VISIBLE);
					aQuery.id(holder.iv_message_imageF)
							.image(Urls.BASE_IMAGE + message)
							
							.progress(R.id.pb_loading);
				} else {
					AppLog.Log("Chatu", "---------Me---- ---");

					holder.tvMessage.setVisibility(View.GONE);
					holder.iv_message_image.setVisibility(View.VISIBLE);
					holder.rLayout.setVisibility(View.VISIBLE);
					
					holder.rl_audio.setVisibility(View.GONE);
					
					aQuery.id(holder.iv_message_image).image(message)
							.progress(R.id.pb_loading);
				}

				break;
				case MessageModel.MESSAGE_TYPE_AUDIO:
					if (!isFrindsMessage) {
					AppLog.Log("Chatu", "---------Me---- ---");
										holder.tvMessage.setVisibility(View.GONE);
					holder.iv_message_image.setVisibility(View.GONE);
					
					holder.rl_audio.setVisibility(View.VISIBLE);
					
					holder.rLayout.setVisibility(View.GONE);
					}  else {
						if(!isFileAudioExist(message)){
							new  DownloadAudioFile().execute(message);
						}
						
                      Log.d("Audio path", message);
						holder.tvMessageF.setVisibility(View.GONE);
						holder.iv_message_imageF.setVisibility(View.GONE);
						
						holder.rl_audioF.setVisibility(View.VISIBLE);
						
						holder.rLayoutF.setVisibility(View.GONE);
						
					}
					
					break;
			case MessageModel.MESSAGE_TYPE_VEDIO:

				File file;
				String localPath;
				ImageView imageView = (isFrindsMessage ? holder.iv_message_imageF
						: holder.iv_message_image);
				ProgressBar bar = (isFrindsMessage ? holder.pBarF : holder.pBar);

				if (!isFrindsMessage) {
					holder.tvMessage.setVisibility(View.GONE);
					holder.iv_message_image.setVisibility(View.VISIBLE);
					holder.rl_audio.setVisibility(View.GONE);
					holder.rLayout.setVisibility(View.VISIBLE);
					localPath = message;
				} else {
					holder.iv_message_imageF.setVisibility(View.VISIBLE);
					holder.rLayoutF.setVisibility(View.VISIBLE);
					holder.rl_audioF.setVisibility(View.GONE);
					localPath = VEDIO_PATH + File.separator + message;

					file = new File(localPath);
					if (!file.exists()) {
						holder.tvMessageF.setVisibility(View.VISIBLE);
						holder.tvMessageF.setText("Click To Download");
					} else {
						holder.tvMessageF.setVisibility(View.GONE);
					}
				}

				// if (senderID == myDetail.getUserId()) {
				//
				// } else {
				//
				// }

				AppLog.Log(TAG, "Local Path ::" + localPath);
				try {
					file = new File(localPath);
					if (file.exists()) {
						Bitmap bMap = ThumbnailUtils.createVideoThumbnail(
								localPath,
								MediaStore.Video.Thumbnails.MICRO_KIND);
						if (bMap != null) {
							aQuery.id(imageView).image(bMap).progress(bar);
							// imageView.setImageBitmap(bMap);
							// bar.setVisibility(View.GONE);
						} else {
							imageView.setImageResource(R.drawable.film_icon);
						}
					} else {
						AppLog.Log(TAG, "File Does not Exits");
						imageView.setImageResource(R.drawable.film_icon);
					}
				} catch (Exception e) {
					e.printStackTrace();
					imageView.setImageResource(R.drawable.film_icon);
				}

				break;
			case MessageModel.MESSAGE_TYPE_LOCATION:

				String url = getImageUrlFromLatLon(message.split(",")[0],
						message.split(",")[1]);

				// if (isFrindsMessage) {
				// holder.iv_message_imageF.setVisibility(View.VISIBLE);
				// holder.tvMessageF.setVisibility(View.GONE);
				// aQuery.id(holder.iv_message_imageF).image(url)
				// .progress(R.id.pb_loading);
				// } else {
				// holder.iv_message_image.setVisibility(View.VISIBLE);
				// holder.tvMessage.setVisibility(View.GONE);
				// aQuery.id(holder.iv_message_image).image(url)
				// .progress(R.id.pb_loading);
				// }

				if (isFrindsMessage) {
					holder.tvMessageF.setVisibility(View.GONE);
					holder.iv_message_imageF.setVisibility(View.VISIBLE);
					holder.rl_audioF.setVisibility(View.GONE);
					
					holder.rLayoutF.setVisibility(View.VISIBLE);
					aQuery.id(holder.iv_message_imageF).image(url)
							.progress(R.id.pb_loading);

				} else {
					holder.tvMessage.setVisibility(View.GONE);
					holder.iv_message_image.setVisibility(View.VISIBLE);
					holder.rLayout.setVisibility(View.VISIBLE);
					holder.rl_audio.setVisibility(View.GONE);
					aQuery.id(holder.iv_message_image).image(url)
							.progress(R.id.pb_loading);
				}
				break;
			case MessageModel.MESSAGE_TYPE_CONTACT:

				String temp[] = message.split(",");
				String name = message.split(",")[0] + "\nclick to save contact";
				String image = temp[3];

				if (isFrindsMessage) {
					holder.rl_audioF.setVisibility(View.GONE);
					
					holder.iv_message_imageF.setVisibility(View.VISIBLE);
					holder.tvMessageF.setVisibility(View.VISIBLE);
					holder.rLayoutF.setVisibility(View.VISIBLE);
					if (image != null)
						holder.tvMessageF.setVisibility(View.VISIBLE);
					holder.iv_message_imageF.setVisibility(View.VISIBLE);
					holder.tvMessageF.setText(name);
					if (image != null && image.trim().length() != 0
							&& !image.equals("null")) {
						aQuery.id(holder.iv_message_imageF).image(
								Urls.BASE_IMAGE + image);
					}

				} else {
					holder.rl_audio.setVisibility(View.GONE);
					holder.iv_message_image.setVisibility(View.VISIBLE);
					holder.tvMessage.setVisibility(View.VISIBLE);
					holder.rLayout.setVisibility(View.VISIBLE);
					if (image != null)
						holder.tvMessage.setVisibility(View.VISIBLE);
					holder.iv_message_image.setVisibility(View.VISIBLE);
					holder.tvMessage.setText(name);
					if (image != null && image.trim().length() != 0
							&& !image.equals("null")) {
						aQuery.id(holder.iv_message_image).image(
								Urls.BASE_IMAGE + image);
					}
				}
				break;

			case MessageModel.MESSAGE_TYPE_STICKER:

				if (isFrindsMessage) {
					holder.rl_audioF.setVisibility(View.GONE);
					holder.tvMessageF.setVisibility(View.GONE);
					
					holder.iv_message_imageF.setVisibility(View.VISIBLE);
					holder.rLayoutF.setVisibility(View.VISIBLE);
					
					AppLog.Log(TAG, "Sticker Image Path :: " + STICKERPATH
							+ message);
					aQuery.id(holder.iv_message_imageF)
							.image(STICKERPATH + message)
							.progress(R.id.pb_loading_f);

					// Uri uri = Uri.parse(STICKERPATH + message);
					// if (uri != null) {
					// holder.iv_message_imageF.setImageURI(uri);
					// }

				} else {
					holder.rl_audio.setVisibility(View.GONE);
					holder.tvMessage.setVisibility(View.GONE);
					holder.iv_message_image.setVisibility(View.VISIBLE);
					holder.rLayout.setVisibility(View.VISIBLE);
					AppLog.Log(TAG, "Sticker Image Path :: " + STICKERPATH
							+ message);
					aQuery.id(holder.iv_message_image)
							.image(STICKERPATH + message)
							.progress(R.id.pb_loading);

				}

				break;

			default:
				break;
			}

			break;

		case TYPE_SEPARATOR:
			// Convert in (dd-mm-yy)

			String dtArr[] = list.get(position).getTime().split("-");
			sectionHolder.tv
					.setText(dtArr[2] + "-" + dtArr[1] + "-" + dtArr[0]);

			break;
		}

		return hView;
	}
	


	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == TYPE_SEPARATOR;
	}

	private String getImageUrlFromLatLon(String latitude, String longitude) {
		String getMapURL = "http://maps.googleapis.com/maps/api/staticmap?zoom=20&size=560x560&markers=size:large|color:red|"
				+ latitude + "," + longitude + "&sensor=false";
		return getMapURL;
	}
	

	public void  playSong(String audioString){
        // Play song
        try {
        	if(mp != null) {
        		mp.reset();
        	}
            mp.setDataSource(audioString);
            mp.prepare();
            mp.start();
         
          
            holder.seekBar1.setId(objNewChatAct.getSeekPos());
            //holder.seekBar1.setTag(objNewChatAct.getSeekPos());
    		//holder.seekBar1.setProgress((int) timeElapsed);
    		durationHandler.postDelayed(updateSeekBarTime, 100);
    		
    		mp.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.reset();
					holder.seekBar1.setProgress(0);
					durationHandler.removeCallbacks(updateSeekBarTime);
					//mp.stop();
				}
			});

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	
	public void  playSongF(String audioString){
        // Play song
        try {
        	if(mp != null) {
        		mp.reset();
        	}
        	mp.setDataSource(audioString);
        	mp.prepare();
        	 mp.start();
         Log.e("Seeckbar position from method", ""+objNewChatAct.getSeekPos());
         
        
         holder.seekBar1.setId(objNewChatAct.getSeekPos());
//        	holder.seekBar1F.setTag(objNewChatAct.getSeekPos());
     		durationHandler.postDelayed(updateSeekBarTimeF, 100);

			mp.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					 mp.reset();
					 holder.seekBar1F.setProgress(0);
					 durationHandler.removeCallbacks(updateSeekBarTimeF);
				}
			});			

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	
	class ViewHolder {
		TextView tvMessage, tvUserName, tvTime;
		ImageView ivMessageDevider, ivMessageStaus, iv_message_image, play;
		SeekBar seekBar1 ;
		// RelativeLayout llMain;
		LinearLayout llChat;
		ProgressBar pBar;
		RelativeLayout rLayout , rl_audio;
		ProgressBar progressBar1;

		// friends
		TextView tvMessageF, tvUserNameF, tvTimeF;
		ImageView ivMessageDeviderF, ivMessageStausF, iv_message_imageF, playF;
		SeekBar seekBar1F ;
		// RelativeLayout llMainF;
		LinearLayout llChatF;
		ProgressBar pBarF;
		RelativeLayout rLayoutF , rl_audioF;

		TextView tvDisplayName;
	}

	class SectionViewHolder {
		TextView tv;
	}
	
	private Runnable updateSeekBarTimeF = new Runnable() {
		public void run() {
			//get current position
			
			holder.seekBar1F.setProgress(( 100* mp.getCurrentPosition() / mp.getDuration()));
			
			durationHandler.postDelayed(this, 100);
		}
	};

	private Runnable updateSeekBarTime = new Runnable() {
		public void run() {
			//get current position
			
			holder.seekBar1.setProgress(( 100* mp.getCurrentPosition() / mp.getDuration()));
			
			durationHandler.postDelayed(this, 100);
		}
	};

	 public class DownloadAudioFile extends AsyncTask<String, Integer, String>{
		 
		    @Override
		    protected String doInBackground(String... url) {
		    int count;
		   String filename= url[0];
		    try {
		    URL url2 = new URL( Urls.BASE_IMAGE + filename);
		    URLConnection conexion = url2.openConnection();
		    conexion.connect();
		    // this will be useful so that you can show a tipical 0-100% progress bar
		    int lenghtOfFile = conexion.getContentLength();

		    // downlod the file
		    InputStream input = new BufferedInputStream(url2.openStream());
		    OutputStream output = new FileOutputStream("/sdcard/Gonechat/AudioMessage/"+filename);

		    byte data[] = new byte[1024];

		    long total = 0;

		    while ((count = input.read(data)) != -1) {
		        total += count;
		        // publishing the progress....
		        publishProgress((int)(total*100/lenghtOfFile));
		        output.write(data, 0, count);
		    }

		    output.flush();
		    output.close();
		    input.close();
		} catch (Exception e) {}
		return null;
		}
		    
		    @Override
		    protected void onPostExecute(String result) {

		    holder.progressBar1.setVisibility(View.GONE);
		    holder.seekBar1F.setVisibility(View.VISIBLE);
		    //super.onPostExecute(result);
		    }
		    @Override
		    protected void onPreExecute() {
		    	 holder.progressBar1.setVisibility(View.VISIBLE);
				    holder.seekBar1F.setVisibility(View.GONE);
		   // super.onPreExecute();
		    }
	 }
	
	 public boolean isFileAudioExist(String audioFileName) {
			File filepath = Environment.getExternalStorageDirectory();
			String ado = filepath.getAbsolutePath()
					+ "/Gonechat/AudioMessage/" + audioFileName;
			
			File f = new File(ado);
			if (f.exists())
				return true;
			else
				return false;
		}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
}