package testbox.streetlight.testcases;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
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

import testbox.streetlight.WaitForCondition;

public class GpioGpio {

	private static final GpioController gpio = GpioFactory.getInstance();

	private static GpioPinDigitalOutput outputLightSensor;
	private static GpioPinDigitalInput inputLightStatus;

	@BeforeClass
	public static void setup() throws Exception {
		inputLightStatus = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
		outputLightSensor = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.HIGH);
		inputLightStatus.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
		outputLightSensor.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
	}

	@AfterClass
	public static void disconnect() throws Exception {
		gpio.shutdown();
	}

	@After
	public void reset() throws Exception {
		inputLightStatus.removeAllListeners();
		outputLightSensor.setState(PinState.HIGH);		
	}

	@Test
	public void testLightsOnWhenItsDark() throws Exception {
		// simulate signal: "Its Dark!"
		outputLightSensor.setState(PinState.LOW);
		
		final AtomicBoolean stateChanged = new AtomicBoolean(false);
		final PinState[] state = new PinState[1];
		state[0] = PinState.LOW;
		// wait for GPIO HIGH on input
		inputLightStatus.addListener(new GpioPinListenerDigital() {

			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				state[0] = event.getState();
				stateChanged.set(true);
			}
		});

				
		new WaitForCondition() {

			@Override
			protected boolean condition() {
				return stateChanged.get();
			}
		}.waitUntil(50, 100);
		Assert.assertEquals(PinState.HIGH, state[0]);
	}

}
