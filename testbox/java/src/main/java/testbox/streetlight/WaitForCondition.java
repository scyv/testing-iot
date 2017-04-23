package testbox.streetlight;

import java.util.concurrent.TimeUnit;

public abstract class WaitForCondition {

	public boolean waitUntil(int maxTries, int sleep) {
		for (int t = maxTries; t > 0; t--) {
			if (condition()) {
				break;
			}
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException ie) {
				return false;
			}
		}
		return true;
	}

	protected abstract boolean condition();

}
