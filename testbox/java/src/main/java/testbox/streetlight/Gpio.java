package testbox.streetlight;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Gpio {

	public static GpioPinDigitalOutput outputLightSensor;
	public static GpioPinDigitalInput inputLightStatus;

	private static final GpioController gpio = GpioFactory.getInstance();

	static {
		inputLightStatus = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
		outputLightSensor = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.HIGH);
		inputLightStatus.setShutdownOptions(true, PinState.LOW, PinPullResistance.PULL_DOWN);
		outputLightSensor.setShutdownOptions(true, PinState.LOW, PinPullResistance.PULL_DOWN);
	}
	
	public static void addListener(GpioPinDigitalInput input, GpioPinListenerDigital listener) {
		input.addListener(listener);
		input.setDebounce(200);
		try {
			Thread.sleep(500);
		} catch (InterruptedException ie) {
			return;
		}
	}
	
	public static void shutdown() {
		gpio.shutdown();
	}

}
