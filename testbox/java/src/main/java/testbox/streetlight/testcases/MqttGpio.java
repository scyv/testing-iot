package testbox.streetlight.testcases;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import testbox.streetlight.Constants;
import testbox.streetlight.WaitForCondition;

public class MqttGpio {

	private static final String SUT_ID = "1";
	private final GpioController gpio = GpioFactory.getInstance();

	private static MqttClient client;

	private GpioPinDigitalOutput outputLightSensor;
	private GpioPinDigitalInput inputLightStatus;

	@BeforeClass
	public static void setupMqtt() throws Exception {
		client = new MqttClient(Constants.MQTT_HOST, Constants.CLIENT_ID, new MemoryPersistence());
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		client.connect(connOpts);		
	}
	
	@Before
	public void setupGPIOs() throws Exception {
		inputLightStatus = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
		outputLightSensor = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.HIGH);
		inputLightStatus.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
		outputLightSensor.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
	}

	@AfterClass
	public static void disconnect() throws Exception {
		client.disconnect();
	}

	@After
	public void reset() throws Exception {
		inputLightStatus.removeAllListeners();
		MqttMessage message = new MqttMessage(Constants.LIGHT_OFF_MESSAGE.getBytes());
		client.publish(Constants.TOPIC_STREETLIGHT_COMMAND + "/" + SUT_ID, message);
		gpio.shutdown();
	}

	@Test
	public void testLightsOnWhenMessageSent() throws Exception {
		final AtomicBoolean stateChanged = new AtomicBoolean(false);
		final PinState[] state = new PinState[1];
		state[0] = PinState.LOW;

		// wait for GPIO
		inputLightStatus.addListener(new GpioPinListenerDigital() {

			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				state[0] = event.getState();
				stateChanged.set(true);
			}
		});

		// send message
		MqttMessage message = new MqttMessage(Constants.LIGHT_ON_MESSAGE.getBytes());
		client.publish(Constants.TOPIC_STREETLIGHT_COMMAND + "/" + SUT_ID, message);
				
		new WaitForCondition() {

			@Override
			protected boolean condition() {
				return stateChanged.get();
			}
		}.waitUntil(50, 100);
		Assert.assertEquals(PinState.HIGH, state[0]);
	}

}
