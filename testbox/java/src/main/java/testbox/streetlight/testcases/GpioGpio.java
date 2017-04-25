package testbox.streetlight.testcases;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import testbox.streetlight.Gpio;
import testbox.streetlight.WaitForCondition;

public class GpioGpio {

	@Before
	public void before() throws Exception {
		Gpio.outputLightSensor.setState(PinState.HIGH);
	}

	@After
	public void after() throws Exception {
		Gpio.inputLightStatus.removeAllListeners();
	}

	@Test
	public void testLightsOnWhenItsDark() throws Exception {

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

		// simulate signal: "Its Dark!"
		Gpio.outputLightSensor.setState(PinState.LOW);

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
