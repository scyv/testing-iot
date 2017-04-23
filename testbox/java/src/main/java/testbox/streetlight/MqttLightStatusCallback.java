package testbox.streetlight;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MqttLightStatusCallback implements MqttCallback {

	private static final ObjectMapper JSON = new ObjectMapper();

	private AtomicBoolean messageArrived = new AtomicBoolean(false);
	private Status resultingStatus;

	public MqttLightStatusCallback(Status resultingStatus) {
		this.resultingStatus = resultingStatus;
	}
	
	public void connectionLost(Throwable arg0) {
		// not implemented
	}

	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// not implemented
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		Status status = JSON.readValue(message.toString(), Status.class);
		if (status.getCmdId().equals(resultingStatus.getCmdId())) {
			messageArrived.set(true);
			resultingStatus.setLight(status.getLight());
		}
	}
	
	public boolean hasMessageArrived() {
		return this.messageArrived.get();
	}

}
