/**
 * Contains the classes needed for a railway system.
 * <ul>
 *     <li> The RailwaySystem class implements a complete railway system.  It contains the main method needed to run
 *     the system.</li>
 *     <li> The Railway class provides the methods needed to construct and run a railway, and to register the railway
 *     with a railway system.  It does not, however, implement the runTrain() method, which should be specified in the
 *     separate Bolivia and Peru classes.</li>
 *     <li> The Peru and Bolivia classes should implement the runTrain() method from the Railway class, using the
 *     methods defined in the Railway class.  The runTrain() methods should, together, define a means of running the
 *     two trains that avoids safety violations.</li>
 *     <li> The Basket class defines the baskets used by the railways for communication.  Each railway has its own
 *     basket, and there is one additional basket shared throughout the whole railway system.  Railways can access each
 *     others baskets, as well as their own.</li>
 * </ul>
 */
package ferrocarrilesDeAmericaDelSur.railways;
