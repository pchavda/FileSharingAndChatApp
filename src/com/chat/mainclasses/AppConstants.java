package com.chat.mainclasses;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class AppConstants {

	// Preference Parameters...
	public static final String providerId = "providerid";
	public static final String userId = "userid";
	public static final String isRemember = "is_remember";
	public static final String email = "email";
	public static final String firstName = "firstname";
	public static final String lastName = "lastname";
	public static final String fullName = "fullname";
	public static final String isLoggedIn = "is_logged_in";
	public static final String registrationId = "registration_id";
	public static final String GCMId = "gcm_id";
	public static final String profileId = "profileid";
	public static final String isLocationAccess = "is_location";
	public static final String metaVersion = "meta_version";
	public static final String alias = "alias";
	public static final String uniqueKey = "uniquekey";
	public static final String language = "language";
	public static final String countrycode = "countrycode";
	public static final String citycode = "citycode";
	public static final String profileImageUrl = "profileimage";

	public static final String defspinstanceid = "spinstanceid"; // DEFAULT SP
																	// INSTANCE
																	// ID.....
	public static final String defspinstancetype = "spinstancetype"; // DEFAULT
																		// SP
																		// INSTANCE
																		// TYPE.....
	public static final String defspname = "spname"; // DEFAULT SP NAME

	// METHOD TYPES...
	public static final String GET = "GET";
	public static final String POST = "POST";

	public static final String content_json = "text/json";
	public static final String content_plain = "text/plain";

	// Profile Variable.
	public static final String GENDER_MALE = "Male";
	public static final String GENDER_FEMALE = "Female";
	public static final String LANG_DE = "DE";
	public static final String LANG_EN = "EN";

	// CLOSING AND OPENING OF SHOP
	public static final String SHOP_OPEN = "Open Now";
	public static final String SHOP_CLOSE = "Closed Now";

	// Static value when location permission not accessible.
	public static String cityCode = "NNN";
	public static String countryCode = "NNN";

	// COUNTERS...
	public static int deviceIdNotExistCounter = 0;

	// ERROR CODES.....
	public static final String DEVICEID_NOT_EXIST = "DEVICEID_NOT_EXIST";
	public static final String AUTH_DATA_REQUIRED = "AUTH_DATA_REQUIRED";
	public static final String NO_PROFILE = "NO_PROFILE";
	public static final String PROFILE_EXIST = "PROFILE_EXIST";
	public static final String REG_REQUIRED = "REG_REQUIRED";

	public static final String DEAL_CODE = "deal";
	public static final String OFFER_CODE = "offer";
	public static final String EVENT_CODE = "event";
	public static final String JOB_CODE = "job";
	public static final String SHOWCASE_CODE = "showcase";
	public static final String SP_CODE = "sp";
	public static final String SHOPPRODUCT_CODE = "shopproduct";
	public static final String SHOP_CODE = "shop";
	public static final String SHOPPOINT_CODE = "shoppoint";

	public static final String InstanceID = "instanceid";
	public static final String InstanceType = "instancetype";
	public static final String ParentIntanceID = "parentinstanceid";

	public enum AppPaymentStatusType {

		CREATED(0, "created"), PENDING(1, "pending"), APPROVED(2, "approved"), FAILED(
				3, "failed"), REJECTED(4, "rejected"), CANCELED(5, "canceled"), EXPIRED(
				6, "expired"), ;

		private int code;
		private String payValue;

		private AppPaymentStatusType(int val, String payValue) {
			code = val;
			this.payValue = payValue;
		}

		public int getCode() {
			return code;
		}

		public String getPayValue() {
			return payValue;
		}

		public static int getCodeFromResponseValue(String payValue) {
			if (payValue == null) {
				return CREATED.code;
			}

			AppPaymentStatusType[] val = AppPaymentStatusType.values();
			for (int i = 0; i < val.length; i++) {
				if (val[i].payValue.equalsIgnoreCase(payValue)) {
					return val[i].code;
				}
			}
			return CREATED.code;
		}

	}

	public enum OrderStatusType {

		CREATED(0), PAYMENT_CONFIRMED(1), PAYMENT_RECEIVED(2), PAYMENT_NOT_RECEIVED(
				3), DELIEVERED(4), CANCELLED(5), COMPLETED(6), ;

		private int code;

		private OrderStatusType(int val) {
			code = val;
		}

		public int getCode() {
			return code;
		}

		public static String getStatusAsString(int code) {
			String status = "UNKNOWN";
			OrderStatusType[] values = OrderStatusType.values();
			for (int i = 0; i < values.length; i++) {
				if (values[i].code == code) {
					return values[i].name();
				}
			}
			return status;

		}

	}

	static Map<Integer, String> daysOfWeeks;

	public static String getCurrentDayValue(int daykey) {
		if (daysOfWeeks == null) {
			daysOfWeeks = new HashMap<Integer, String>();

			daysOfWeeks.put(1, "SUNDAY");
			daysOfWeeks.put(2, "MONDAY");
			daysOfWeeks.put(3, "TUESDAY");
			daysOfWeeks.put(4, "WEDNESDAY");
			daysOfWeeks.put(5, "THURSDAY");
			daysOfWeeks.put(6, "FRIDAY");
			daysOfWeeks.put(7, "SATURDAY");
		}
		String value = daysOfWeeks.get(daykey);
		Log.i("CURRENT DAY VALUE : ", value);
		return value;
	}
}
