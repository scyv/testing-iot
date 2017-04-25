package testbox.streetlight.testcases;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import testbox.streetlight.Constants;
import testbox.streetlight.Gpio;
import testbox.streetlight.WaitForCondition;

public class MqttGpio {

	private static final String SUT_ID = "1";
	private static MqttClient client;

	@BeforeClass
	public static void setupMqtt() throws Exception {
		client = new MqttClient(Constants.MQTT_HOST, Constants.CLIENT_ID, new MemoryPersistence());
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		client.connect(connOpts);
		MqttMessage message = new MqttMessage(Constants.LIGHT_OFF_MESSAGE.getBytes());
		client.publish(Constants.TOPIC_STREETLIGHT_COMMAND + "/" + SUT_ID, message);
	}

	@AfterClass
	public static void disconnect() throws Exception {
		client.disconnect();
	}

	@After
	public void reset() throws Exception {
		Gpio.inputLightStatus.removeAllListeners();
		MqttMessage message = new MqttMessage(Constants.LIGHT_OFF_MESSAGE.getBytes());
		client.publish(Constants.TOPIC_STREETLIGHT_COMMAND + "/" + SUT_ID, message);
	}

	@Test
	public void testLightsOnWhenMessageSent() throws Exception {
		final AtomicBoolean stateChanged = new AtomicBoolean(false);
		final PinState[] state = new PinState[1];
		state[0] = Gpio.outputLightSensor.getState();

		Gpio.addListener(Gpio.inputLightStatus, new GpioPinListenerDigital() {

			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				synchronized (stateChanged) {
					state[0] = event.getState();
					stateChanged.set(true);					
				}
			}
		});

		// send message
		MqttMessage message = new MqttMessage(Constants.LIGHT_ON_MESSAGE.getBytes());
		client.publish(Constants.TOPIC_STREETLIGHT_COMMAND + "/" + SUT_ID, message);

		// wait until the pin goes HIGH
		new WaitForCondition() {

			@Override
			protected synchronized boolean condition() {
				return stateChanged.get();
			}
		}.waitUntil(20, 100);
		Assert.assertEquals(PinState.HIGH, state[0]);
	}

}
