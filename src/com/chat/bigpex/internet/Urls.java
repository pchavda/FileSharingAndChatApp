package com.chat.bigpex.internet;

import java.io.File;

import android.os.Environment;

import com.chat.bigpex.helper.Constant;

public class Urls {
	public static final String BASE_URL = "http://janmeshjani.com/gonechat/";
	public static final String BASE_IMAGE = BASE_URL + "images/";
	public static final String BASE_STICKER = BASE_IMAGE + "sticker/";

	public static final String BASE_LOCAL_IMAGE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator
			+ Constant.ROOT_FOLDER_NAME
			+ File.separator
			+ Constant.FOLDER_IMAGE;

	
	public static final String SEND_TIMELINE_MESAGE = BASE_URL + "send_timeline_message.php";
	
	public static final String SEND_TIMELINE_IMAGE = BASE_URL + "send_timeline_image.php";
	
	
	public static final String REGISTER_USER = BASE_URL + "userregister_old.php";

	public static final String VERIFY_USER = BASE_URL + "veryfiedcode.php";

	public static final String EDIT_PROFILE = BASE_URL + "edit_profile.php";

	public static final String UPDATE_CONTACTS = BASE_URL + "friendalllist.php";

	public static final String SEND_MESSAGE = BASE_URL + "message.php";

	public static final String UPLOAD_IMAGE = BASE_URL + "upload_image.php";

	public static final String UPLOAD_VIDEO = BASE_URL + "upload_fil.php";
	
	public static final String UPLOAD_AUDEO = BASE_URL + "upload_audio.php";

	public static final String GET_STICKER = BASE_URL + "getstickerlist.php";

	public static final String LAST_SEEN_GET = BASE_URL + "last_seen_get.php";

	public static final String LAST_SEEN_SET = BASE_URL + "last_seen_set.php";

	public static final String CREATE_GROUP = BASE_URL + "create_group.php";

	public static final String GET_GROUP_MEMBER = BASE_URL + "group_member.php";

	public static final String GROUP_MESSAGE = BASE_URL + "message_group.php";

	public static final String GROUPLIST = "grouplist.php";

	public static final String GROUPINFO = BASE_URL + "group_time_info.php";

	public static final String EXIT_GROUP = BASE_URL
			+ "join_unjoin_group_2.php";

	public static final String JOIN_UNJOIN_GROUP = BASE_URL
			+ "join_unjoin_group.php";
	public static final String DELETE_CHAT = BASE_URL
			+ "group_member_delete.php";

}
