package com.chat.bigpex.helper;

public class Constant {

	// GCM
	public static final String SENDER_ID = "386342885919";

	public static final String ROOT_FOLDER_NAME = "Gonechat";
	public static final String FOLDER_IMAGE = "images";
	public static final String PROFILE_PICTURE = "profiles";

	public static final String STICKER_DIR = "sticker";

	public static boolean IS_WINDOW_OPEN = false;

	public static int UNREAD_MSG = 0;
	public static int READ_MSG = 1;

	public static final class Extra {
		public static final String CODES = "codes";
		public static final String FRIEND_DETAIL = "friend_detail";
		public static final String IMAGE = "image";
		public static final String NAME = "NAME";
		public static final String BITMAP = "bitmap";
		public static final String LOCAL_PATH = "local_image_path";
		public static final String IMAGE_URL = "image_url";
		public static final String USER_LIST = "user_list";
	}

	public static final class Pref {
		public static final String REGISTER_STEP_ONE = "register_one";
		public static final String REGISTER_STEP_TWO = "register_two";
		public static final String REGISTER_STEP_THREE = "register_three";
		public static final String GCM_ID = "gcm_id";

		// below for current register detail
		public static final String USER_ID = "user_id";
		public static final String USER_NAME = "user_name";
		public static final String USER_IMAGE = "user_image";
		public static final String USER_PHONE_NO = "phone_no";
		public static final String USER_PHONE_CODE = "phone_code";
		public static final String USER_EMAIL = "user_email";
		public static final String USER_STATUS = "user_status";

		public static final String USER_LAST_MSG_STATUS = "readornot";

	}

	public static final class Params {
		public static final String email = "email";
	}

	public static final class MyActions {
		public static final String CONTACT_LIST_UPDATE = "contact_list_updated";
		public static final String MESSAGE_UPDATE = "message_update";
		public static final String SEND_MESSAGE = "send_message";
	}

}
