package testbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import testbox.streetlight.Gpio;
import testbox.streetlight.testcases.GpioGpio;
import testbox.streetlight.testcases.MqttGpio;
import testbox.streetlight.testcases.MqttMqtt;

public class TestRunner {

	private static final Class<?>[] TESTCLASSES_NO_GPIO = { MqttMqtt.class };

	private static final Class<?>[] TESTCLASSES_WITH_GPIO = { GpioGpio.class, MqttGpio.class };

	private static final Class<?>[] ALL_TESTCLASSES;

	private static boolean canUseGpios = true;

	static {
		List<Class<?>> allTests = new ArrayList<Class<?>>(Arrays.asList(TESTCLASSES_NO_GPIO));
		try {
			Gpio.inputLightStatus.getState();
			allTests.addAll(Arrays.asList(TESTCLASSES_WITH_GPIO));
		} catch (UnsatisfiedLinkError ule) {
			System.out.println("[WARNING] Skipping tests that use GPIOs!");
			// this happens when the gpio test class objects cannot be
			// instantiated
			canUseGpios = false;
		}
		ALL_TESTCLASSES = allTests.toArray(new Class<?>[0]);
	}

	public static void main(String[] args) {

		JUnitCore junitCore = new JUnitCore();
		junitCore.addListener(new RunListener() {

			@Override
			public void testStarted(Description description) throws Exception {
				System.out.println("[TEST] " + description.getDisplayName());
			}

			@Override
			public void testAssumptionFailure(Failure failure) {
				System.out.println(" [FAIL] " + failure.getMessage());
			}

			@Override
			public void testFailure(Failure failure) throws Exception {
				System.out.println(" [FAIL] " + failure.getMessage());
			}

			@Override
			public void testIgnored(Description description) throws Exception {
				System.out.println(" [IGNORED]");
			}
		});

		final Result result = junitCore.run(ALL_TESTCLASSES);

		if (canUseGpios) {
			Gpio.shutdown();
		}

		if (result.wasSuccessful()) {
			System.out.println();
			System.out.println("----------------------------------");
			System.out.println("[RESULT - SUCCESS] Tests run successfully.");
			System.out.println("----------------------------------");
		} else {
			System.err.println();
			System.err.println("----------------------------------");
			System.err.println("[RESULT - FAIL] There were test failures.");
			for (Failure failure : result.getFailures()) {
				System.err.println(" " + failure.toString());
			}
			System.err.println("----------------------------------");
		}

		if (!result.wasSuccessful()) {
			System.exit(1);
		}

	}

}
