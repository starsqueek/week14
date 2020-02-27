package ferrocarrilesDeAmericaDelSur.railways;

import ferrocarrilesDeAmericaDelSur.errors.ProgrammingError;
import ferrocarrilesDeAmericaDelSur.errors.RailwaySystemError;
import ferrocarrilesDeAmericaDelSur.errors.SafetyViolationError;
import ferrocarrilesDeAmericaDelSur.tools.Delay;

/**
 * Defines a basic railway system, for two railways with a shared single track pass.
 *
 */
public abstract class Railway extends Thread {
	private static Basket sharedBasket = new Basket("shared basket"); // a shared basket for notifications
	private Basket basket; // private basket
	
	private RailwaySystem railwaySystem; // the system this railway forms part of
	private Delay delay; // the delay used by this railway

	private Position position; // the position of the train on this railway
	
	public Railway(String name,Delay delay) {
		super(name);
		this.delay = delay;
		position = Position.END_PASS; // all trains start just after the
		basket = new Basket(name + "'s basket");
	}

	/**
	 * Register this railway with a railway system
	 * @param railwaySystem the railway system this railway must be registered with
	 */
	public void register(RailwaySystem railwaySystem) {
		this.railwaySystem = railwaySystem;
	}
	
	/**
	 * Get the railway system this railway is registered with
	 * @return the railway system this railway is registered with
	 * @throws ProgrammingError if the railway is not registered
	 */
	public RailwaySystem getRailwaySystem() throws ProgrammingError {
		if (railwaySystem == null) {
			throw new ProgrammingError(getName() + " is not registered with a railway system");
		}
		return railwaySystem;
	}
	
	/**
	 * Get this railway's private basket.
	 * @return this railway's private basket
	 */
	public Basket getBasket() {
	    return basket;
	}
	
	/**
	 * Get the shared basket.
	 * @return the basket shared between all railways.
	 */
	public static Basket getSharedBasket() {
		return sharedBasket;
	}
	
	/**
	 * Use delay to generate a delay for this railway
	 */
	public void delay() {
		delay.delay();
	}
	
	// Fields keeping track of trains in the pass
	private static int trainsInPass = 0; // how many trains are in the pass

	/**
	 * Defines parts of the railway system. These are specified as:
	 * <ul>
	 *     <li> START_PASS: just before entering the shared pass.</li>
	 *     <li> IN_PASS: in the shared pass.</li>
	 *     <li> END_PASS: at the end of the shared pass.</li>
	 * </ul>
	 * Trains start at the end of the pass, and must thereafter cycle through positions START_PASS, IN_PASS, END_PASS.
	 */
	public static enum Position {
		START_PASS, IN_PASS, END_PASS;
		
		public String toString() {
			switch (this) {
			case START_PASS: return "at the start of the pass";
			case IN_PASS: return "in the pass";
			case END_PASS: return "at the end of the pass";
			default: return "at an undefined position on the railway (ERROR)";
			}
		}
	}
	/**
	 * Enter the pass.
	 * This method does <i>not</i> check if it is safe to enter the pass. It is merely for
	 * administration of the information about trains in the pass.
	 * @throws ProgrammingError if this railway thinks it already has a train in the pass.
	 */
	private synchronized void enterPass() throws ProgrammingError {
		railwaySystem.trace(getName() + ": entering pass");
		if (position != Position.START_PASS) {
			throw new ProgrammingError(getName() + " cannot enter the pass, it is not " + Position.START_PASS + ", it is " + position + ".");
		}
		position = Position.IN_PASS;
		trainsInPass++;
	}
	
	/**
	 * Leave the pass.
	 * This method is merely for administration of the information about trains in the pass.
	 * @throws ProgrammingError if this railway thinks it does not have a train in the pass,
	 *                          or if there is no record of any trains in the pass.
	 */
	private synchronized void leavePass() throws ProgrammingError {
		if (position != Position.IN_PASS) {
			throw new ProgrammingError(getName() + " cannot leave the pass, it is not " + Position.IN_PASS + ", it is " + position + ".");
		}
		if (trainsInPass == 0) {
			throw new ProgrammingError("There is no train to leave the pass (even though " + getName() + " thinks it is in the pass.");
		}
		position = Position.END_PASS;
		trainsInPass--;
		railwaySystem.trace(getName() + ": leaving pass");
	}

	/**
	 * Travel round the safe part of the railway (outside the pass).
	 * @throws ProgrammingError if the train is not currently at the end of the pass (and therefore at the start of the
	 *         safe part of the railway).
	 */
	public void choochoo() throws ProgrammingError {
		if (position != Position.END_PASS) {
			throw new ProgrammingError(getName() + " cannot traverse safe section, it is not " + Position.END_PASS + ", it is " + position + ".");
		}
		railwaySystem.trace (getName() + ": choo-choo");
		delay();
		position = Position.START_PASS;
	}
	
	/**
	 * Have a siesta.
	 */
	public void siesta() {
		railwaySystem.trace(getName() + ": zzzzz");
		delay();
	}
	
	/**
	 * Cross the pass.
	 * @throws SafetyViolationError if there is/are already train(s) on the pass.
	 */
	public void crossPass() throws RailwaySystemError {
		enterPass();
		if (trainsInPass > 1) {
			throw new SafetyViolationError("There are now " + trainsInPass + " trains in the pass!");
		}
		railwaySystem.trace(getName() + ": crossing pass");
		delay();
		leavePass();
	}
	
	// Error flag must be shared so that we can stop all railways if something goes wrong	
	private static boolean errorFlag = false;
	protected static String errorMessage = "";
	
    /**
     * Run the railway.
     */
    public void run() {
    	setErrorFlag(false);
    	try {
    		runTrain();
    	} catch (RailwaySystemError error) {
    		setErrorFlag(true);
    		errorMessage = error.getMessage();
    		System.out.println("!!! Something went wrong with the railway.\n\t" + errorMessage);
    	}
    	if (errorOccurred()) {
    		System.out.println("!!! " + getName() + " shut down because of an error.\n\t" + errorMessage);
    	} else {
    		System.out.println(getName() + " shut down because time limit was reached.");
    	}
    }
    
    /**
     * Each railway should independently define how the trains are to be run, using the basket(s).
     * to maintain safety on the pass.
	 * @throws RailwaySystemError if a safety violation occurs while the railway is being run.
     */
    public abstract void runTrain() throws RailwaySystemError, RailwaySystemError;

	/**
	 * Set the shared error flag (if an error occurs).
	 * @param errorFlag is true iff an error has occured.
	 */
	public static void setErrorFlag(boolean errorFlag) {
		Railway.errorFlag = errorFlag;
	}

	/**
	 * Check the current error status.
	 * @return true iff an error is currently active.
	 */
	public static boolean errorOccurred() {
		return errorFlag;
	}

}
