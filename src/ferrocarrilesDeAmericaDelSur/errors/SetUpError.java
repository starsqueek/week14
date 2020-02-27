package ferrocarrilesDeAmericaDelSur.errors;

/**
 * An error has occured while trying to set up the system.
 * E.g. the system has attempted to specify a negative time delay.
 */
public class SetUpError extends RailwaySystemError {
	public SetUpError(String message) {
		super("[Set up error] " + message);
	}
}
