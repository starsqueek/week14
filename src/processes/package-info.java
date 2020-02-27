/**
 * Implements two simple concurrent processes with shared variables.  Theoretically, different runs may produce
 * different values.
 * <ul>
 *     <li> Process defines the shared variables and their access methods, but does not specify the run() method.</li>
 *     <li> Process1 and Process2 are full implementations of Process, with their run() methods accessing the shared
 *     variables.</li>
 *     <li> Tutorial14 runs Process1 and Process2 concurrently, printing out the values of the shared values before
 *     and after the run.</li>
 * </ul>
 */
package processes;
