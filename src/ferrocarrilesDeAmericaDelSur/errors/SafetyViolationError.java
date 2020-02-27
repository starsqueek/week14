package ferrocarrilesDeAmericaDelSur.errors;

/**
 * A safety violation error has occured.
 * E.g. more than one train is in the pass at the same time.
 */
public class SafetyViolationError extends RailwaySystemError {
	public SafetyViolationError(String message) {
		super("[Safety violation error] " + message);
	}
}
