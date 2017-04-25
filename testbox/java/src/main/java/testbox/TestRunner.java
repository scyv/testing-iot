package testbox;

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

	private static final Class<?>[] TESTCLASSES = {
		GpioGpio.class, MqttGpio.class, MqttMqtt.class
	};
	
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
		Result result = junitCore.run(TESTCLASSES);

		Gpio.shutdown();
		
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
