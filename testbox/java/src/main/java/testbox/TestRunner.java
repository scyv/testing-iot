package testbox;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import testbox.streetlight.testcases.MqttGpio;
import testbox.streetlight.testcases.MqttMqtt;

public class TestRunner {

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
		Result result = junitCore.run(MqttGpio.class, MqttMqtt.class);

		System.out.println("----------------------------------");
		if (result.wasSuccessful()) {
			System.out.println("[RESULT - SUCCESS] Tests run successfully.");
		} else {
			System.out.println("[RESULT - FAIL] There were test failures.");
			for (Failure failure : result.getFailures()) {
				System.out.println(" " + failure.toString());
			}
		}
		System.out.println("----------------------------------");
		
		if (!result.wasSuccessful()) {
			System.exit(-1);
		}
		
	}

}
