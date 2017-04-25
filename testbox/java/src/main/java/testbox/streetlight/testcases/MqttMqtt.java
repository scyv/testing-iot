package testbox.streetlight.testcases;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import testbox.streetlight.Constants;
import testbox.streetlight.MqttLightStatusCallback;
import testbox.streetlight.Status;
import testbox.streetlight.WaitForCondition;

public class MqttMqtt {

	private static final String SUT_ID = "1";
	private static MqttClient client;

	@BeforeClass
	public static void setUp() throws Exception {
		client = new MqttClient(Constants.MQTT_HOST, Constants.CLIENT_ID, new MemoryPersistence());
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		client.connect(connOpts);
	}

	@AfterClass
	public static void disconnect() throws Exception {
		client.disconnect();
	}

	@After
	public void reset() throws Exception {
		client.setCallback(null);
		MqttMessage message = new MqttMessage(Constants.LIGHT_OFF_MESSAGE.getBytes());
		client.publish(Constants.TOPIC_STREETLIGHT_COMMAND + "/" + SUT_ID, message);
	}

	@Test
	public void testLightsOnResponseWhenMessageSent() throws Exception {

		final Status resultingStatus = new Status(Status.STATUS_OFF, Constants.CMD_ON_ID);
		final MqttLightStatusCallback callback = new MqttLightStatusCallback(resultingStatus);

		client.setCallback(callback);
		client.subscribe(Constants.TOPIC_STREETLIGHT_STATUS + "/" + SUT_ID);

		MqttMessage message = new MqttMessage(Constants.LIGHT_ON_MESSAGE.getBytes());
		client.publish(Constants.TOPIC_STREETLIGHT_COMMAND + "/" + SUT_ID, message);

		(new WaitForCondition() {
			@Override
			protected boolean condition() {
				return callback.hasMessageArrived();
			}
		}).waitUntil(50, 100);

		Assert.assertEquals(Status.STATUS_ON, resultingStatus.getLight());
	}

	@Test
	public void testLightsOffResponseWhenMessageSent() throws Exception {

		// first switch the lights on
		testLightsOnResponseWhenMessageSent();

		final Status resultingStatus = new Status(Status.STATUS_ON, Constants.CMD_OFF_ID);
		final MqttLightStatusCallback callback = new MqttLightStatusCallback(resultingStatus);

		client.setCallback(callback);
		client.subscribe(Constants.TOPIC_STREETLIGHT_STATUS + "/" + SUT_ID);

		MqttMessage message = new MqttMessage(Constants.LIGHT_OFF_MESSAGE.getBytes());
		client.publish(Constants.TOPIC_STREETLIGHT_COMMAND + "/" + SUT_ID, message);

		(new WaitForCondition() {
			@Override
			protected boolean condition() {
				return callback.hasMessageArrived();
			}
		}).waitUntil(50, 100);

		Assert.assertEquals(Status.STATUS_OFF, resultingStatus.getLight());
	}

}
