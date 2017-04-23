package testbox.streetlight;

public class Status {

	public static String STATUS_ON = "on";
	public static String STATUS_OFF = "off";

	private String light;
	private String cmdId;
	private String streetlightId;

	public Status() {
		// needed for serialisation
	}

	public Status(String light, String cmdId) {
		super();
		this.light = light;
		this.cmdId = cmdId;
	}

	public String getLight() {
		return light;
	}

	public void setLight(String light) {
		this.light = light;
	}

	public String getCmdId() {
		return cmdId;
	}

	public void setCmdId(String cmdId) {
		this.cmdId = cmdId;
	}

	public String getStreetlightId() {
		return streetlightId;
	}

	public void setStreetlightId(String streetlightId) {
		this.streetlightId = streetlightId;
	}

}
