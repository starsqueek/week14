package ferrocarrilesDeAmericaDelSur.railways;

import ferrocarrilesDeAmericaDelSur.errors.RailwaySystemError;
import ferrocarrilesDeAmericaDelSur.errors.SetUpError;
import ferrocarrilesDeAmericaDelSur.tools.Clock;
import ferrocarrilesDeAmericaDelSur.tools.Delay;

/**
 * An implementation of a railway.  The runTrain method, should, in collaboration with Bolivia's runTrain(), guarantee
 * safe joint operation of the railways.
 */
public class Peru extends Railway {
	/**
	 * Public variables to indicate the turn and id for taking the turn to show which railway has the priority to prevent deadlock.
	 */
	private Integer id = 1;
	private Integer timesCrossed = 0;

	/**
	 * Change the parameters of the Delay constructor in the call of the superconstructor to
	 * change the behaviour of this railway.
	 * @throws SetUpError if there is an error in setting up the delay.
	 */
	public Peru() throws SetUpError {
		super("Peru",new Delay(0.1,0.3));
	}

	/**
	 * Run the train on the railway.
	 * This method uses Dekker’s Algorithm
	 */
    public void runTrain() throws RailwaySystemError {
		Clock clock = getRailwaySystem().getClock();
		Railway nextRailway = getRailwaySystem().getNextRailway(this);
		while (!clock.timeOut()) {
			choochoo();
			getBasket().putStone(this);
			while (nextRailway.getBasket().hasStone(this)) {
				if (Bolivia.turn == (id+1) % 2) {
					getBasket().takeStone(this);
					siesta();
					getBasket().putStone(this);
				}
			}
			crossPass();
			timesCrossed++;
			System.out.println(getName()+" has crossed "+timesCrossed+" times.");
			Bolivia.turn = (id+1) % 2;
			getBasket().takeStone(this);
		}
	}
}