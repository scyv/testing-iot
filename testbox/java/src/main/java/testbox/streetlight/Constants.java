package testbox.streetlight;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Constants {

	private static final ObjectMapper JSON = new ObjectMapper();

	public static final String MQTT_HOST = "tcp://192.168.42.102:1883";
	public static final String CLIENT_ID = "TestBox";

	public static final String TOPIC_STREETLIGHT_COMMAND = "scyv/smartcity/streetlights/cmd";
	public static final String TOPIC_STREETLIGHT_STATUS = "scyv/smartcity/streetlights/status";

	public static final String CMD_ON_ID = UUID.randomUUID().toString();
	public static final String CMD_OFF_ID = UUID.randomUUID().toString();

	public static String LIGHT_ON_MESSAGE;
	public static String LIGHT_OFF_MESSAGE;

	static {
		try {
			LIGHT_ON_MESSAGE = JSON.writeValueAsString(new Status(Status.STATUS_ON, Constants.CMD_ON_ID));
			LIGHT_OFF_MESSAGE = JSON.writeValueAsString(new Status(Status.STATUS_OFF, Constants.CMD_OFF_ID));
		} catch (JsonProcessingException jpe) {
			LIGHT_ON_MESSAGE = null;
			LIGHT_OFF_MESSAGE = null;
		}
	}

}
