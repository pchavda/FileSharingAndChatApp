package com.chat.mainclasses;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class AndroidAppUtils {
	
	private static ObjectMapper mapper;

	
	public static ObjectMapper getJsonMapper() {

		initMapper();
		return mapper;
	}

	private static void initMapper() {

		if (mapper == null) {

			mapper = new ObjectMapper();
			// SerializationFeature for changing how JSON is written

			// to enable standard indentation ("pretty-printing"):
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			// to allow serialization of "empty" POJOs (no properties to
			// serialize)
			// (without this setting, an exception is thrown in those cases)
			mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
			// to write java.util.Date, Calendar as number (timestamp):
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

			mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
			// DeserializationFeature for changing how JSON is read as POJOs:

			mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

			mapper.setSerializationInclusion(Include.NON_NULL);

			// to prevent exception when encountering unknown property:
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			// to allow coercion of JSON empty String ("") to null Object value:
			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

			// mapper.enableDefaultTyping();
		}
		
	}

	public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";
 
        // Convert total duration into time
           int hours = (int)( milliseconds / (1000*60*60));
           int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
           int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
           // Add hours if there
           if(hours > 0){
               finalTimerString = hours + ":";
           }
 
           // Prepending 0 to seconds if it is one digit
           if(seconds < 10){
               secondsString = "0" + seconds;
           }else{
               secondsString = "" + seconds;}
 
           finalTimerString = finalTimerString + minutes + ":" + secondsString;
 
        // return timer string
        return finalTimerString;
    }
 
    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;
 
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
 
        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;
 
        // return percentage
        return percentage.intValue();
    }
 
    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);
 
        // return current duration in milliseconds
        return currentDuration * 1000;
    }
}
