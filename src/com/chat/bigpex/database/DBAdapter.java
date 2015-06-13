package com.chat.bigpex.database;

import static com.chat.bigpex.models.MessageModel.MESSAGE_TYPE_CONTACT;
import static com.chat.bigpex.models.MessageModel.MESSAGE_TYPE_IMAGE;
import static com.chat.bigpex.models.MessageModel.MESSAGE_TYPE_LOCATION;
import static com.chat.bigpex.models.MessageModel.MESSAGE_TYPE_MESSAGE;
import static com.chat.bigpex.models.MessageModel.MESSAGE_TYPE_STICKER;
import static com.chat.bigpex.models.MessageModel.MESSAGE_TYPE_VEDIO;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.chat.bigpex.helper.AppLog;
import com.chat.bigpex.helper.MyPrefrence;
import com.chat.bigpex.models.GetTimelineData;
import com.chat.bigpex.models.MessageModel;
import com.chat.bigpex.models.StickerItem;
import com.chat.bigpex.models.TimelineMessageModel;
import com.chat.bigpex.models.UserDetail;

public class DBAdapter {
	private DatabaseHelper DBhelper;
	private SQLiteDatabase db;

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "FreshImV4Database";

	public static final String TABLE_NAME_MESSAGE = "message";
	public static final String TABLE_NAME_GROUP_MEMBER = "group_member";
	public static final String TABLE_NAME_TIMELINE_MESSAGE = "timeline_message";

	// User table
	public static final String KEY_ID = "_id";

	public static final String KEY_NAME = "display_name";
	public static final String KEY_IMAGE = "image";
	public static final String KEY_STATUS = "status";
	public static final String KEY_LAST_MESSAGE = "last_message";
	public static final String KEY_PHONE_NO = "phone_no";
	public static final String KEY_PHONE_CODE = "phone_code";
	public static final String KEY_EMAIL = "key_email";

	public static final String TABLE_NAME_USER = "i_user";

	public static final String CREATE_USER_MASTER = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME_USER
			+ " ( "
			+ KEY_ID
			+ " INTEGER NOT NULL,"
			+ KEY_NAME
			+ " text not null , "
			+ KEY_IMAGE
			+ " text not null , "
			+ KEY_PHONE_NO
			+ " text not null,"
			+ KEY_PHONE_CODE
			+ " text not null,"
			+ KEY_EMAIL
			+ " text ,"
			+ KEY_STATUS
			+ " text not null , " + KEY_LAST_MESSAGE + " INTEGER " + ");";

	//
	public static final String KEY_FRIEND_ID = "friend_id";
	public static final String KEY_MESSAGE_ID = "message_id";
	public static final String KEY_WHO_ID = "who";// gtoup/personal
	public static final String KEY_MESSAGE_TYPE = "message_type";
	public static final String KEY_MESSAGE_STATUS = "key_status";// sent/delivered/uploading/downlodng
	public static final String KEY_DELIVERY_TIME = "delivery_time";
	public static final String KEY_DISPLAY_NAME = "display_name";
	public static final String KEY_MESSAGE = "message";

	// message table
	// reciever id
	public static final String KEY_TO_USER_ID = "to_user_id";
	public static final String KEY_FROM_USER_ID = "from_user_id";
	public static final String KEY_TIME = "time";
	public static final String KEY_MESSAGE_HEADER_DATE = "message_date";
	public static final String KEY_IS_FROM_GROUP = "is_from_group";

	public static final String TABLE_MESSAGE_MASTER = "message";
	public static final String KEY_USER_ID = "user_id";

	public static final String CREATE_TABLE_MESSAGE_MASTER = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_MESSAGE_MASTER
			+ " ( "
			+ KEY_ID
			+ " INTEGER PRIMARY KEY,"
			+ KEY_MESSAGE_ID
			+ " INTEGER ,"
			+ KEY_MESSAGE
			+ " text not null , "
			+ KEY_MESSAGE_TYPE
			+ " INTEGER NOT NULL ,"
			+ KEY_USER_ID
			+ " INTEGER NOT NULL ,"
			+ KEY_FRIEND_ID
			+ " INTEGER NOT NULL ,"
			+ KEY_WHO_ID
			+ " INTEGER NOT NULL ,"
			+ KEY_TIME
			+ " TEXT NOT NULL ,"
			+ KEY_MESSAGE_STATUS
			+ " INTEGER NOT NULL ,"
			+ KEY_DELIVERY_TIME
			+ " text not null ,"
			+ KEY_DISPLAY_NAME
			+ " text  ," + KEY_MESSAGE_HEADER_DATE + " text );	";

	// group
	public static final String KEY_GROUP_ID = "group_id";
	public static final String KEY_GROUP_ADMIN_ID = "admin_id";
	public static final String KEY_GROUP_IMAGE = "group_image";
	public static final String KEY_GROUP_NAME = "group_name";
	public static final String CREATED_TIME = "created_time";

	public static final String TABLE_GROUP_MASTER = "group_master";

	public static final String CREATE_TABLE_GROUP_MASTER = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_GROUP_MASTER
			+ " ( "
			// + KEY_ID
			// + " INTEGER PRIMARY KEY,"
			+ KEY_GROUP_ID // server_group_id
			+ " INTEGER PRIMARY KEY,"
			+ KEY_GROUP_NAME
			+ " text not null , "
			+ KEY_GROUP_IMAGE
			+ " text not null , "
			+ KEY_GROUP_ADMIN_ID
			+ " INTEGER NOT NULL,"
			+ CREATED_TIME
			+ " text NOT NULL,"
			+ KEY_STATUS
			+ " text not null , "
			+ KEY_LAST_MESSAGE
			+ " INTEGER);";

	
	/******************************************************       Timeline Table   ******************************************************/
	public static final String KEY = "key";
	public static final String KEY_CONTENT = "content";
	public static final String KEY_DATE_TIME = "date_time";
	public static final String KEY_TYPE = "type";
	
	
	public static final String CREATE_TIMELINE_MESSAGE_MASTER = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME_TIMELINE_MESSAGE
			+ " ( "
			+ KEY_MESSAGE_ID
			+ " INTEGER NOT NULL,"
			+ KEY
			+ " INTEGER NOT NULL,"
			+ KEY_CONTENT
			+ " text not null , "
			+ KEY_DATE_TIME
			+ " text not null , "
			+ KEY_TYPE
			+ " INTEGER NOT NULL,"
			+ KEY_PHONE_CODE
			+ " text not null,"
			+ KEY_PHONE_NO
			+ " text not null,"
			+ KEY_LAST_MESSAGE + " INTEGER " + ");";
	
	/******************************************************        *************   ******************************************************/
	
	public int insertOrUpdateGroup(UserDetail userDetail) {

		int returnVal = 0;

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_GROUP_NAME, userDetail.getName());
		contentValues.put(KEY_GROUP_IMAGE, userDetail.getImage());
		contentValues.put(KEY_GROUP_ADMIN_ID, userDetail.getAdminID());
		contentValues.put(KEY_STATUS, "tap to get group detail");

		if (getGroupDetail(userDetail.getUserId()) == null) {
			contentValues.put(KEY_GROUP_ID, userDetail.getUserId());
			contentValues.put(CREATED_TIME, System.currentTimeMillis());
			returnVal = (int) db
					.insert(TABLE_GROUP_MASTER, null, contentValues);
		} else {
			returnVal = db.update(TABLE_GROUP_MASTER, contentValues,
					KEY_GROUP_ID + "=" + userDetail.getUserId(), null);
		}

		return returnVal;
	}

	public UserDetail getGroupDetail(int groupId) {

		if (db == null || !db.isOpen()) {
			openForRead();
		}

		Cursor cursor = db.query(TABLE_GROUP_MASTER, new String[] {
				KEY_GROUP_ID, KEY_GROUP_NAME, KEY_GROUP_IMAGE,
				KEY_GROUP_ADMIN_ID, KEY_LAST_MESSAGE }, KEY_GROUP_ID + "="
				+ groupId, null, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			UserDetail userDetail = new UserDetail();
			userDetail.setUserId(cursor.getInt(0));
			userDetail.setName(cursor.getString(1));
			userDetail.setImage(cursor.getString(2));
			userDetail.setAdminId(cursor.getInt(3));
			userDetail.setLastMessage(cursor.getString(4));
			userDetail.setIsGroup(true);
			cursor.close();
			return userDetail;
		}
		return null;
	}

	public void addAllGroups(List<UserDetail> list) {

		Cursor cursor = db.query(TABLE_GROUP_MASTER, new String[] {
				KEY_GROUP_ID, KEY_GROUP_NAME, KEY_GROUP_IMAGE,
				KEY_GROUP_ADMIN_ID, KEY_LAST_MESSAGE }, null, null, null, null,
				null);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				UserDetail userDetail = new UserDetail();
				userDetail.setUserId(cursor.getInt(0));
				userDetail.setName(cursor.getString(1));
				userDetail.setImage(cursor.getString(2));
				userDetail.setAdminId(cursor.getInt(3));
				userDetail.setMessageId(cursor.getInt(4));
				userDetail.setIsGroup(true);
				if (userDetail.getMessageId() != 0) {
					setUserMessgae(userDetail);
				}
				list.add(userDetail);

			} while (cursor.moveToNext());
		}
	}

	//

	public static final String TABLE_GROUP_USER_LIST = "group_user";

	public static final String CREATE_GROUP_USER = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_GROUP_USER_LIST
			+ " ( "
			+ KEY_ID
			+ " INTEGER PRIMARY KEY,"
			+ KEY_GROUP_ID
			+ " INTEGER NOT NULL,"
			+ KEY_USER_ID
			+ " INTEGER NOT NULL ,"
			+ KEY_NAME
			+ " TEXT NOT NULL ,"
			+ KEY_PHONE_NO
			+ " text NOT NULL,"
			+ KEY_PHONE_CODE
			+ " text NOT NULL,"
			+ KEY_IMAGE
			+ " TEXT NOT NULL ,"
			+ KEY_STATUS
			+ " TEXT NOT NULL " + " );";

	public ArrayList<UserDetail> getGroupMembers(int groupId) {
		ArrayList<UserDetail> list = new ArrayList<UserDetail>();

		// if (db == null && !db.isOpen()) {
		// openForRead();
		// }

		Cursor cursor = db.query(TABLE_GROUP_USER_LIST, new String[] {
				KEY_GROUP_ID, KEY_USER_ID, KEY_NAME, KEY_PHONE_NO,
				KEY_PHONE_CODE, KEY_IMAGE, KEY_STATUS }, KEY_GROUP_ID + "="
				+ groupId, null, null, null, null);
		if (cursor == null || !cursor.moveToFirst()) {
			return list;
		}
		UserDetail detail;
		do {
			detail = new UserDetail();
			detail.setIsGroup(false);
			detail.setUserId(cursor.getInt(1));
			detail.setName(cursor.getString(2));
			detail.setPhoneNo(cursor.getString(3));
			detail.setPhoneCode(cursor.getString(4));
			detail.setImage(cursor.getString(5));
			detail.setStatus(cursor.getString(6));

			list.add(detail);

		} while (cursor.moveToNext());

		return list;
	}

	public void deleteUerFromGropup(int userId, int groupId) {

		int i = db.delete(TABLE_GROUP_USER_LIST, KEY_GROUP_ID + "=" + groupId
				+ " and " + KEY_USER_ID + "=" + userId, null);

	}

	public void deleteAllGroupUsers(int groupId) {

		if (db == null && !db.isOpen()) {
			openForRead();
		}

		int i = db.delete(TABLE_GROUP_USER_LIST, KEY_GROUP_ID + "=" + groupId,
				null);
	}

	public void deleteGroupAndMembers(int groupId) {
		db.delete(TABLE_GROUP_MASTER, KEY_GROUP_ID + "=" + groupId, null);
		deleteAllGroupUsers(groupId);
		int i = db.delete(TABLE_MESSAGE_MASTER, "((" + KEY_FRIEND_ID + "="
				+ groupId + ")" + " OR " + "(" + KEY_USER_ID + "=" + groupId
				+ ")) AND " + KEY_WHO_ID + "=" + MessageModel.WHO_GROUP, null);
	}

	
	/**********************************************************         TIMELINE MESSAGE       *********************************************************/
	
	
	public int insertTimelineMessage(GetTimelineData model) {
		int i = 0;

		ContentValues values = new ContentValues();
		values.put(KEY_MESSAGE_ID, model.getMessage_id());
		values.put(KEY, model.getKey());
		values.put(KEY_CONTENT, model.getContent());
		values.put(KEY_DATE_TIME, model.getDatetime());
		values.put(KEY_TYPE, model.getType());
		values.put(KEY_PHONE_CODE, model.getContry_code());
		values.put(KEY_PHONE_NO, model.getPhone());
		values.put(KEY_LAST_MESSAGE, model.getMessage_id());

		// AppLog.Log(TAG, "MEssage :: " + model);
		AppLog.Log("Time of Message", model.getDatetime());

		//i = (int) db.insertWithOnConflict(TABLE_NAME_TIMELINE_MESSAGE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		i = (int) db.insert(TABLE_NAME_TIMELINE_MESSAGE, null, values);
		return i;
	}
	
	
	
	public boolean isTimelineMessage(String message_id) {
		
		String Query = "Select * from " + TABLE_NAME_TIMELINE_MESSAGE + " where " + KEY_MESSAGE_ID + " = " + message_id;
	    Cursor cursor = db.rawQuery(Query, null);
	        if(cursor.getCount() <= 0){
	            return false;
	        }
	    return true;
	}
	
	
	
	
	
	
	public List<GetTimelineData> getTimelinePost(String phno) {
		List<GetTimelineData> list = new ArrayList<GetTimelineData>();

		Cursor cursor = db.query(TABLE_NAME_TIMELINE_MESSAGE, new String[] { KEY_MESSAGE_ID,
				KEY_CONTENT, KEY_DATE_TIME, KEY_TYPE
				 },KEY_PHONE_NO + "=" + phno, null, null, null, KEY_DATE_TIME+" DESC");
		if (cursor != null && cursor.moveToFirst()) {
			GetTimelineData detail;
			do {
				detail = new GetTimelineData();
				detail.setMessage_id(cursor.getString(0));
				detail.setContent(cursor.getString(1));
				detail.setDatetime(cursor.getString(2));
				detail.setType(cursor.getString(3));
				
				list.add(detail);
			} while (cursor.moveToNext());
			cursor.close();
		}

		return list;
	}
	
	
	/*****************************************************************************************************************************************************/
	public int insertUpdateGroupMember(UserDetail detail) {

		int returnValues = 0;

		ContentValues values = new ContentValues();
		values.put(KEY_GROUP_ID, detail.getGroupID());
		values.put(KEY_USER_ID, detail.getUserId());
		values.put(KEY_NAME, detail.getName());
		values.put(KEY_PHONE_NO, detail.getPhoneNo());
		values.put(KEY_PHONE_CODE, detail.getPhoneCode());
		values.put(KEY_IMAGE, detail.getImage());
		values.put(KEY_STATUS, detail.getStatus());

		if (isUserPresent(detail)) {
			returnValues = db.update(TABLE_GROUP_USER_LIST, values, KEY_USER_ID
					+ "=" + detail.getUserId() + " and " + KEY_GROUP_ID + "="
					+ detail.getGroupID(), null);
		} else {
			returnValues = (int) db.insert(TABLE_GROUP_USER_LIST, null, values);
		}

		return returnValues;
	}

	public boolean isUserPresent(UserDetail detail) {
		if (db == null && !db.isOpen()) {
			openForRead();
		}

		Cursor cursor = db.query(TABLE_GROUP_USER_LIST,
				new String[] { KEY_ID }, KEY_USER_ID + "=" + detail.getUserId()
						+ " and " + KEY_GROUP_ID + "=" + detail.getGroupID(),
				null, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			return true;
		}

		return false;
	}

	/**
	 * unused below
	 */

	public static final String TABLE_NAME = "table_sticker";
	public static final String IMAGE = "image";
	public static final String CATEGORY = "category";
	public static final String EXT = "ext";
	public static final String TIMESTAMP = "timestamp";

	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "(" + KEY_ID + " INTEGER , " + IMAGE + " TEXT, "
			+ EXT + " TEXT, " + CATEGORY + " TEXT, " + TIMESTAMP + " Text "
			+ " )";

	// status table

	private static final String KEY_STATUS_TEXT = "status_text";

	private static final String TABLE_STATUS = "status_table";

	private static final String CREATE_STATUS_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_STATUS
			+ " (_id INTEGER PRIMARY KEY ,"
			+ KEY_STATUS_TEXT
			+ " TEXT UNIQUE);";

	private Context context;

	public DBAdapter(Context ctx) {
		DBhelper = new DatabaseHelper(ctx);
		context = ctx;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_STATUS_TABLE);
			db.execSQL(CREATE_USER_MASTER);
			db.execSQL(CREATE_TABLE_MESSAGE_MASTER);
			db.execSQL(CREATE_TABLE_GROUP_MASTER);
			db.execSQL(CREATE_GROUP_USER);
			db.execSQL(CREATE_TIMELINE_MESSAGE_MASTER);
			db.execSQL(CREATE_TABLE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}

	// private DBAdapter open() {
	// try {
	// db = DBhelper.getWritableDatabase();
	// } catch (SQLiteException sql) {
	// sql.printStackTrace();
	// }
	// return this;
	// }

	public DBAdapter openForRead() {
		try {
			db = DBhelper.getWritableDatabase();
		} catch (SQLiteException sql) {
			sql.printStackTrace();
		}
		return this;
	}

	public DBAdapter openForWrite() {
		try {
			db = DBhelper.getWritableDatabase();
		} catch (SQLiteException sql) {
			sql.printStackTrace();
		}
		return this;
	}

	public boolean isCreated() {
		if (db != null) {
			return db.isOpen();
		}
		return false;
	}

	public boolean isOpen() {
		if (db != null)
			return db.isOpen();

		return false;
	}

	public void close() {
		if (DBhelper != null) {
			DBhelper.close();
		}
		if (db != null) {
			db.close();
		}
	}

	/**
	 * methods are bolow
	 */

	public List<UserDetail> getPastChats() {

		List<UserDetail> list = new ArrayList<UserDetail>();

		Cursor cursor = db.query(TABLE_NAME_USER, new String[] { KEY_ID,
				KEY_NAME, KEY_IMAGE, KEY_PHONE_NO, KEY_PHONE_CODE, KEY_STATUS,
				KEY_LAST_MESSAGE }, KEY_LAST_MESSAGE + "!=0", null, null, null,
				null);
		if (cursor != null && cursor.moveToFirst()) {
			int count = cursor.getCount();
			UserDetail detail;
			for (int i = 0; i < count; i++) {
				detail = new UserDetail();
				detail.setUserId(cursor.getInt(0));
				detail.setName(cursor.getString(1));
				detail.setImage(cursor.getString(2));
				detail.setPhoneNo(cursor.getString(3));
				detail.setPhoneCode(cursor.getString(4));
				detail.setStatus(cursor.getString(5));
				detail.setMessageId(cursor.getInt(6));
				setUserMessgae(detail);
				list.add(detail);
				if (!cursor.moveToNext()) {
					break;
				}
			}
			cursor.close();
		}

		return list;
	}

	private void setUserMessgae(UserDetail detail) {
		if (!isOpen()) {
			openForWrite();
		}

		Cursor cursor = db.query(TABLE_MESSAGE_MASTER, new String[] {
				KEY_MESSAGE, KEY_TIME, KEY_USER_ID, KEY_MESSAGE_TYPE,
				KEY_FRIEND_ID }, KEY_ID
				+ "="
				+ detail.getMessageId()
				+ " and "
				+ KEY_WHO_ID
				+ "="
				+ (detail.isGroup() ? MessageModel.WHO_GROUP
						: MessageModel.WHO_PERSONAL), null, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			int type = cursor.getInt(3);
			switch (type) {
			case MESSAGE_TYPE_MESSAGE:
				detail.setLastMessage(cursor.getString(0));
				break;

			case MESSAGE_TYPE_IMAGE:
				//
				detail.setLastMessage("image");
				break;

			case MESSAGE_TYPE_VEDIO:
				detail.setLastMessage("video");
				break;

			case MESSAGE_TYPE_LOCATION:
				detail.setLastMessage("Location");
				break;

			case MESSAGE_TYPE_STICKER:
				detail.setLastMessage("sticker");
				break;

			case MESSAGE_TYPE_CONTACT:
				detail.setLastMessage("contact");
				break;
			}
			detail.setMessageTime(cursor.getString(1));
			UserDetail myDetail = new MyPrefrence(context).getDetail();
			int id = cursor.getInt(2);
			if (myDetail.getUserId() == id) {
				detail.setUserId(cursor.getInt(4));
			} else {
				detail.setUserId(id);
			}
			cursor.close();
			close();
		}
	}

	public List<UserDetail> getAllUsers() {
		List<UserDetail> list = new ArrayList<UserDetail>();

		Cursor cursor = db.query(TABLE_NAME_USER, new String[] { KEY_ID,
				KEY_NAME, KEY_IMAGE, KEY_PHONE_NO, KEY_PHONE_CODE, KEY_STATUS,
				KEY_LAST_MESSAGE }, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			UserDetail detail;
			do {
				detail = new UserDetail();
				detail.setUserId(cursor.getInt(0));
				detail.setName(cursor.getString(1));
				detail.setImage(cursor.getString(2));
				detail.setPhoneNo(cursor.getString(3));
				detail.setPhoneCode(cursor.getString(4));
				detail.setStatus(cursor.getString(5));
				detail.setMessageId(cursor.getInt(6));

				list.add(detail);
			} while (cursor.moveToNext());
			cursor.close();
		}

		return list;
	}

	public UserDetail getUserDetail(int userId) {
		return null;
	}

	public ArrayList<MessageModel> getMessages(int userID, int friendId,
			int who, int temp) {

		ArrayList<MessageModel> list = new ArrayList<MessageModel>();
		Cursor mCursor;
		String[] colums = new String[] { KEY_ID, KEY_MESSAGE, KEY_MESSAGE_TYPE,
				KEY_USER_ID, KEY_FRIEND_ID, KEY_WHO_ID, KEY_TIME,
				KEY_MESSAGE_STATUS, KEY_DELIVERY_TIME, KEY_DISPLAY_NAME };
		if (who == MessageModel.WHO_GROUP) {
			mCursor = db.query(TABLE_MESSAGE_MASTER, colums, "(("
					+ KEY_FRIEND_ID + "=" + friendId + ")" + " OR " + "("
					+ KEY_USER_ID + "=" + friendId + ")) AND " + KEY_WHO_ID
					+ "=" + who, null, null, null, null);
		} else {
			mCursor = db.query(TABLE_MESSAGE_MASTER, colums, "((("
					+ KEY_FRIEND_ID + "=" + friendId + ")" + " OR " + "("
					+ KEY_USER_ID + "=" + friendId + ")) AND " + KEY_WHO_ID
					+ "=" + who + ")", null, null, null, null);
			// mCursor = db.query(TABLE_MESSAGE_MASTER, colums, "((("
			// + KEY_FRIEND_ID + "=" + friendId + ")" + " OR " + "("
			// + KEY_USER_ID + "=" + friendId + ")) AND " + KEY_WHO_ID
			// + "=" + who + ") AND " + KEY_ID + " > " + lastmessageID,
			// null, null, null, KEY_ID + " DESC");
		}

		String date;

		if (mCursor != null) {
			if (mCursor.moveToFirst()) {
				MessageModel model;
				// date = mCursor.getString(6).split("\\s+")[0];
				// model = new MessageModel();
				// model.listItemType = NewChatAdapter.TYPE_SEPARATOR;
				// model.setDisplayNane(date);
				// list.add(model);

				do {
					model = new MessageModel();
					model.setMessageId(mCursor.getInt(0));
					model.setMessage(mCursor.getString(1));
					model.setMessageType(mCursor.getInt(2));
					model.setUserID(mCursor.getInt(3));
					model.setFriendId(mCursor.getInt(4));
					model.setWho(mCursor.getInt(5));
					model.setTime(mCursor.getString(6));
					// if (!model.getTime().split("\\s+")[0].equals(date)) {
					// MessageModel temoModel = new MessageModel();
					// temoModel.listItemType = NewChatAdapter.TYPE_SEPARATOR;
					// temoModel
					// .setDisplayNane(model.getTime().split("\\s+")[0]);
					// list.add(temoModel);
					// }
					model.setMessageStatus(mCursor.getInt(7));
					model.setDisplayNane(mCursor.getString(9));
					// model.listItemType = NewChatAdapter.TYPE_ITEM;
					list.add(model);
				} while (mCursor.moveToNext());
			}
			mCursor.close();
		}
		return list;
	}

	public Cursor getMessages(int userID, int friendId, int who) {
		Cursor mCursor;
		if (who == MessageModel.WHO_GROUP) {
			mCursor = db.query(TABLE_MESSAGE_MASTER, new String[] {}, "(("
					+ KEY_FRIEND_ID + "=" + friendId + ")" + " OR " + "("
					+ KEY_USER_ID + "=" + friendId + ")) AND " + KEY_WHO_ID
					+ "=" + who, null, null, null, null);
		} else {
			mCursor = db.query(TABLE_MESSAGE_MASTER, new String[] {}, "(("
					+ KEY_USER_ID + "=" + userID + " AND " + KEY_FRIEND_ID
					+ "=" + friendId + ")" + " OR " + "(" + KEY_USER_ID + "="
					+ friendId + " AND " + KEY_FRIEND_ID + "=" + userID
					+ ")) AND " + KEY_WHO_ID + "=" + who, null, null, null,
					null);

		}

		return mCursor;
	}

	public int insertOrUpdateMessage(MessageModel model) {
		int i = 0;

		ContentValues values = new ContentValues();
		values.put(KEY_MESSAGE, model.getMessage());
		values.put(KEY_MESSAGE_ID, model.getMessageId());
		values.put(KEY_MESSAGE_TYPE, model.getMessageType());
		values.put(KEY_USER_ID, model.getUserID());
		values.put(KEY_FRIEND_ID, model.getFriendId());
		values.put(KEY_TIME, model.getTime());
		values.put(KEY_MESSAGE_STATUS, model.getMessageStatus());
		values.put(KEY_DELIVERY_TIME, model.getTime());
		values.put(KEY_WHO_ID, model.getWho());

		if (model.getId() != 0) {
			i = (int) db.insert(TABLE_MESSAGE_MASTER, null, values);
		} else {
			i = db.update(TABLE_MESSAGE_MASTER, values,
					KEY_ID + "=" + model.getId(), null);
		}
		return i;
	}

	public boolean deleteChat(int user_id, int toid) {

		openForRead();
		// Select All Query
		String delQuery = "DELETE FROM message where user_id=" + toid
				+ " or friend_id=" + toid + "";

		updateLastMessageId(0, toid);

		db.execSQL(delQuery);
		db.close();
		return false;
	}

	public int insertMessage(MessageModel model) {
		int i = 0;

		ContentValues values = new ContentValues();
		values.put(KEY_MESSAGE, model.getMessage());
		values.put(KEY_MESSAGE_ID, model.getMessageId());
		values.put(KEY_MESSAGE_TYPE, model.getMessageType());
		values.put(KEY_USER_ID, model.getUserID());
		values.put(KEY_FRIEND_ID, model.getFriendId());
		values.put(KEY_TIME, model.getTime());
		values.put(KEY_MESSAGE_STATUS, model.getMessageStatus());
		values.put(KEY_DELIVERY_TIME, model.getTime());
		values.put(KEY_WHO_ID, model.getWho());
		values.put(KEY_DISPLAY_NAME, model.getDisplayName());

		// AppLog.Log(TAG, "MEssage :: " + model);
		AppLog.Log("Time of Message", model.getTime());

		i = (int) db.insert(TABLE_MESSAGE_MASTER, null, values);
		return i;
	}

	public int updateLastMessageId(int messageId, int userId) {
		int i = 0;
		ContentValues values = new ContentValues();
		values.put(KEY_LAST_MESSAGE, messageId);
		i = db.update(TABLE_NAME_USER, values, KEY_ID + "=" + userId, null);
		return i;
	}

	public int insertOrUpdateUser(UserDetail detail) {

		ContentValues values = new ContentValues();
		values.put(KEY_ID, detail.getUserId());
		values.put(KEY_NAME, detail.getName());
		values.put(KEY_IMAGE, detail.getImage());
		values.put(KEY_PHONE_NO, detail.getPhoneNo());
		values.put(KEY_PHONE_CODE, detail.getPhoneCode());
		values.put(KEY_EMAIL, detail.getEmail());
		values.put(KEY_STATUS, detail.getStatus());

		int i;

		i = db.update(TABLE_NAME_USER, values,
				KEY_ID + "=" + detail.getUserId(), null);

		if (i == 0) {
			i = (int) db.insert(TABLE_NAME_USER, null, values);
		}

		return i;
	}

	public int insertStatus(String status) {
		int i = 0;
		openForRead();
		ContentValues values = new ContentValues();
		values.put(KEY_STATUS_TEXT, status);
		try {
			i = (int) db.insert(TABLE_STATUS, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();

		return i;
	}

	public int deleteStatus(String status) {
		int i = 0;
		openForRead();
		try {
			i = db.delete(TABLE_STATUS, KEY_STATUS_TEXT + "='" + status + "'",
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		close();

		return i;
	}

	public ArrayList<String> getStatusList() {
		ArrayList<String> list = new ArrayList<String>();

		openForRead();
		try {
			Cursor cursor = db.query(TABLE_STATUS,
					new String[] { KEY_STATUS_TEXT }, null, null, null, null,
					KEY_ID);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					list.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
		return list;
	}

	/**
	 * 
	 */

	public List<StickerItem> getAllDetails() {
		List<StickerItem> list = new ArrayList<StickerItem>();

		openForRead();
		Cursor cursor = db.query(TABLE_NAME, new String[] {}, null, null, null,
				null, TIMESTAMP + " DESC ");

		if (cursor != null && cursor.moveToFirst()) {
			do {
				StickerItem details = new StickerItem();
				details.setId(cursor.getString(0));
				details.setImage(cursor.getString(1));
				details.setExtension(cursor.getString(2));
				details.setCategory(cursor.getString(3));
				details.setTime(cursor.getString(4));
				// Adding to list
				list.add(details);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		close();
		return list;
	}

	public void insertInTable(StickerItem details) {
		openForRead();
		ContentValues values = new ContentValues();
		values.put(KEY_ID, details.getId());
		values.put(IMAGE, details.getImage());
		values.put(EXT, details.getExtension());
		values.put(CATEGORY, details.getCategory());
		values.put(TIMESTAMP, details.getTime());

		// Inserting Row
		try {
			db.insert(TABLE_NAME, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();
		// Closing database connection
	}

	public StickerItem getStickerDetail(String id) {
		openForRead();
		Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID, IMAGE, EXT,
				CATEGORY, TIMESTAMP }, IMAGE + "=" + id.split("\\.")[0], null,
				null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			StickerItem item = new StickerItem();
			item.setId(cursor.getInt(0) + "");
			item.setImage(cursor.getString(1));
			item.setExtension(cursor.getString(2));
			return item;
		}
		else {
			cursor.close();
			db.close();
		}
		return null;
	}

	public boolean isImagePresent(String id) {

		// Select All Query
		String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_NAME
				+ " WHERE " + KEY_ID + " = " + id + "";
		openForRead();
		Cursor cursor = null;
		if (db.rawQuery(selectQuery, null) != null) {
			cursor = db.rawQuery(selectQuery, null);
		}
		if (cursor.getCount() > 0) {
			cursor.close();
			db.close();
			return true;
		}
		db.close();
		return false;
	}

}
