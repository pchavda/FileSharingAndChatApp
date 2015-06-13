package com.chat.bigpex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bigpex.gonechat.R;
import com.chat.bigpex.helper.AppLog;
import com.chat.bigpex.helper.Constant;
import com.chat.bigpex.helper.MyPrefrence;
import com.chat.bigpex.helper.Utils;
import com.chat.bigpex.helper.Constant.Extra;
import com.chat.bigpex.models.UserDetail;

public class CreateNewGroupActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "CreateNewGroupActivity";

	private static final int CAMERA_REQUEST_CODE = 101;
	private static final int RESULT_LOAD_IMAGE = 102;
	private static final int MOBILE_WIDTH = 520;
	private static final int REQUEST_ADD_NEW_GROUP = 103;

	private static final String IMAGE_DIRECTORY_NAME = Constant.ROOT_FOLDER_NAME;
	private ImageView ivGropGroupLogin;
	private EditText etGroupName;

	// for getting image from camera
	private File imageFile;
	private Uri imageUri;
	private String imagePath;
	private Bitmap photoBitmap;
	private String image;
	private static final int MAX_IMAGE_WIDTH = 300;
	private static final int MAX_IMAGE_HEIGHT = 300;
	// data
	private UserDetail myDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_group_activity);
		myDetail = new MyPrefrence(this).getDetail();
		ivGropGroupLogin = (ImageView) findViewById(R.id.iv_image_gruop_image);
		ivGropGroupLogin.setOnClickListener(this);
		etGroupName = (EditText) findViewById(R.id.et_group_name);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	// public static boolean isF = true;

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater()
				.inflate(R.menu.create_new_group_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_next_group:
			goToNext();
			return true;

		case android.R.id.home:
			setResult(RESULT_CANCELED);
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
		
	private void goToNext() {

		if (etGroupName.getText().toString().trim().length() == 0) {
			Utils.showToast("Please Enter Group Name", this);
			return;
		}
		
		// isF = false;
		Intent mIntent = new Intent(this, SelectGroupUserList.class);
		mIntent.putExtra(Extra.IMAGE, image);
		mIntent.putExtra(Extra.NAME, etGroupName.getText().toString().trim());
		// startActivity(mIntent);
		startActivityForResult(mIntent, REQUEST_ADD_NEW_GROUP);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_image_gruop_image:
			imageAlertDialog().show();
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	private Dialog imageAlertDialog() {

		final AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, new String[] {
								"Camera", "Gallery" }),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									Intent cameraIntent = new Intent(
											android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
									cameraIntent.putExtra(
											MediaStore.EXTRA_OUTPUT,
											getTempUri());
									startActivityForResult(cameraIntent,
											CAMERA_REQUEST_CODE);
								} else if (which == 1) {
									Intent i = new Intent(
											Intent.ACTION_PICK,
											android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									startActivityForResult(i, RESULT_LOAD_IMAGE);
								}
							}
						});

		return builder.create();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (CAMERA_REQUEST_CODE == requestCode
				&& resultCode == Activity.RESULT_OK) {

			AppLog.Log(TAG, "Path is imagePath::" + imagePath);
			// pd = ProgressDialog.show(getActivity(), "Loading",
			// "Please Wait");
			new Thread(new Runnable() {

				@Override
				public void run() {
					//resizeBitmap(imagePath);
					photoBitmap = Utils.resizeBitmap(
							MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT,
							Utils.getBitmapFromFile(imagePath));
					
					image = Utils.encodeToBase64(photoBitmap);
					
					//image = Utils.encodeToBase64(photoBitmap);
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ivGropGroupLogin.setImageBitmap(photoBitmap);
						}
					});
				}
			}).start();

		} else if (requestCode == RESULT_LOAD_IMAGE
				&& resultCode == Activity.RESULT_OK && null != data) {
			final Uri contentURI = data.getData();
			new Thread(new Runnable() {

				@Override
				public void run() {
					
					photoBitmap = Utils.resizeBitmap(
							MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT,
							Utils.getBitmapFromFile(Utils.getRealPathFromURI(contentURI,
									CreateNewGroupActivity.this)));
					
					image = Utils.encodeToBase64(photoBitmap);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ivGropGroupLogin.setImageBitmap(photoBitmap);
						}
					});
				}
			}).start();
		} else if (requestCode == REQUEST_ADD_NEW_GROUP
				&& resultCode == RESULT_OK) {
			finish();
		}
	}

	private Uri getTempUri() {

		imageFile = null;
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + Constant.ROOT_FOLDER_NAME
				+ Constant.PROFILE_PICTURE);
		if (!file.exists()) {
			file.mkdir();
		}
		File file2 = new File(Environment.getExternalStorageDirectory()
				+ File.separator + Constant.ROOT_FOLDER_NAME + "/"
				+ Constant.PROFILE_PICTURE);
		if (!file2.exists()) {
			file2.mkdir();
		}
		imagePath = Environment.getExternalStorageDirectory() + "/"
				+ File.separator + Constant.ROOT_FOLDER_NAME + "/"
				+ Constant.PROFILE_PICTURE + "/" + "Camera_"
				+ System.currentTimeMillis() + ".jpg";

		imageFile = new File(imagePath);

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

		AppLog.Log(TAG, "imagePath" + imagePath);

		imageUri = Uri.fromFile(imageFile);
		return imageUri;
	}

	private void resizeBitmap(String path) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		int outWidth, outHeight;

		if (path != null) {
			BitmapFactory.decodeFile(path, options);

			outWidth = options.outWidth;
			outHeight = options.outHeight;
		} else {
			if (photoBitmap != null) {
				outWidth = photoBitmap.getWidth();
				outHeight = photoBitmap.getHeight();
			} else {
				return;
			}
		}

		int ratio = (int) ((((float) outWidth) / MOBILE_WIDTH) + 0.5f);

		if (ratio == 0) {
			ratio = 1;
		}

		if (path != null) {
			options.inJustDecodeBounds = false;
			options.inSampleSize = ratio;
			AppLog.Log(TAG, "from gallery  " + ratio);
			photoBitmap = BitmapFactory.decodeFile(path, options);
		} else {
			outWidth = outWidth / ratio;
			outHeight = outHeight / ratio;
			photoBitmap = Bitmap.createScaledBitmap(photoBitmap, outWidth,
					outHeight, true);
		}
		// Helper.showSimpleProgressDialog(this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				// you can create a new file name "test.jpg" in sdcard folder.
				File f = new File(Environment.getExternalStorageDirectory()
						+ File.separator + IMAGE_DIRECTORY_NAME
						+ File.separator + myDetail.getUserId() + ".jpg");

				createIfNotDirectory();

				if (f.exists()) {
					f.delete();
				}
				try {
					f.createNewFile();
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(stream.toByteArray());
					// remember close de FileOutput
					fo.close();
					AppLog.Log(TAG, "***** IMAGE PATH *****");
					AppLog.Log(TAG, "Path is :: " + f.getAbsolutePath());
					AppLog.Log(TAG, "***** IMAGE PATH *****");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void createIfNotDirectory() {

		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + Constant.ROOT_FOLDER_NAME);
		if (f.exists()) {
			return;
		} else {
			f.mkdir();
		}

	}

}
