import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.math.RoundingMode;
import java.lang.Integer;

public class manager {
	
	// declaring global variables
	static boolean verbose = false;		// true if verbose flag present
	static char c;						// most recently visited char
	static int cIndex;					// index of c within input
	static Resource [] resources;		// array of resources
	static Task [] tasks;				// array of tasks
	
	
	public static void main(String[] args) throws IOException
	{
		// declaring and initializing variables
		StringBuilder input = new StringBuilder();	// will hold input file
		
		/*
		 * check for --verbose flag
		 * then set input to text within file from command line argument
		 */
		if (args[0].equals("--verbose"))
		{	
			verbose = true;
			input = fileToSB(args , 1);
		}
		else
		{
			input = fileToSB(args , 0);
		}
				
		// initialize c and cIndex 
		c = input.charAt(0); 
		cIndex = 0;
		
		// organize the input
		organizeInput(input);
		
		// manage the tasks, once with optimistic manager and once with banker
		manageTheType("FIFO");
		manageTheType("Banker");	
				
	} // end main
	
	
	/*
	 * fileToSB - reads file and returns it's contents as StringBuiler
	 */
	public static StringBuilder fileToSB( String args[], int arg) throws IOException {
		
		// read the text file	
		BufferedReader reader = new BufferedReader(new FileReader (args[arg]) );
		StringBuilder in = new StringBuilder();
				
		String currentLine;	// current line of file
		
		// append characters to StringBuffer
		while((currentLine=reader.readLine())!=null) {			
			in.append((currentLine+"\n"));
		}		
		
		return in;
		
	} // end fileToSB
	
	/*
	 * skipTilNumber - checks each character starting from cIndex until
	 * a number is found
	 */
	public static int skipTilNumber(StringBuilder in) {
		c = in.charAt(cIndex);
		String numberAsString ="";

		// find the first character that is a digit
		while(!Character.isDigit(c) && (cIndex<in.length())) {
			cIndex++;
			c = in.charAt(cIndex);
		}
		cIndex++;
		
		numberAsString += c;
		
		// account for numbers that have more than one digit
		while(Character.isDigit(in.charAt(cIndex))) {
			c = in.charAt(cIndex);
			numberAsString += c;
			cIndex++;
		}
		cIndex++;
		
		// if one digit number is found return it
		if (numberAsString.equals(c)) {
			return Character.getNumericValue(c);
		}
		else // return multi-digit numbers
			return Integer.parseInt(numberAsString);
		
	} // end skipTilNumber
	
	/*
	 * skipTilWord - checks each character starting from cIndex until
	 * a letter is found, then adds more letter to create word
	 * and returns the word
	 */
	public static String skipTilWord(StringBuilder in) {
		c = in.charAt(cIndex);
		String word ="";

		// find the first character that is a letter
		while(!Character.isLetter(c) && (cIndex<in.length())) {
			cIndex++;
			c = in.charAt(cIndex);
		}
		cIndex++;
		
		word += c;
		
		// find and add more letters to make the word
		while(Character.isLetter(in.charAt(cIndex))) {
			c = in.charAt(cIndex);
			word += c;
			cIndex++;
		}
		cIndex++;
		
		return word;
			
	} // end skipTilWord
	
	/*
	 * organizeInput - organizes text within input
	 */
	public static void organizeInput(StringBuilder in)
	{
		// declaring and initializing local variables
		int numOfComplete = 0;		// number of tasks whose activity list is complete
		int numOfTasks = 0;			// number of tasks
		int numOfResources = 0; 	// number of resources
		int first = 0;				// first number after task number for activityType
		int second = 0;				// second number after task number for activityType
		int taskType = 0;			// task number for activityType
		String activityType = null;	// last activity type found in input
		
		// find number of tasks and resources
		numOfTasks = skipTilNumber(in);
		numOfResources = skipTilNumber(in);
		
		// initialize the task and resource array
		tasks = new Task[numOfTasks];
		resources = new Resource[numOfResources];
		
		// find each resource's information and add it to array		
		for (int i = 0; i < resources.length; i++)
		{
			// find number of units for each resource
			int numOfUnits = skipTilNumber(in);	
			
			// create the resource and add it's info to array
			resources[i] = new Resource(i+1, numOfUnits);
		}
		
		// find all task activities, and save in Task
		while (numOfComplete < numOfTasks)
		{
			// find the next activity type, task number and following two numbers
			activityType = skipTilWord(in);
			taskType = skipTilNumber(in);
			first = skipTilNumber(in);
			second = skipTilNumber(in);
			
			// initiate the task if null, add to it's activity list
			if(tasks[taskType-1] == null)
			{
				tasks[taskType-1] = new Task(taskType, first-1, second, numOfResources);
				tasks[taskType-1].addToActivities( new Activity(activityType, first, second) );
			}
			else
			{
				tasks[taskType-1].addToActivities( new Activity(activityType, first, second) );
				
				/*
				 * if activity type is terminate it is last in activity list
				 * set the task to complete and update number of complete tasks
				 */
				if (activityType.equals("terminate"))
				{
					tasks[taskType-1].setComplete();
					numOfComplete++;
				}
			}				
		} // end while
					
	} // end organizeInput
	
	/*
	 * manageTasks
	 */
	public static void manageTheType(String theType)
	{
		// declaring and initializing local variables
		int numOfDone = 0;						// number of terminated tasks
		int cycleTime = 0;						// cycle time for activities
		String type = theType;					// type of task manager
		int cycle [] = new int[tasks.length];	// cycle time for each task
		Task [] t = new Task[tasks.length];		// array of tasks
		// lists of release activities for resources to be available next cycle
		LinkedList<Activity> ReleaseActivities = new LinkedList<Activity>(); 
		// list of task indices within task array of blocked tasks, in order of cycle blocked
		LinkedList<Integer> BlockedIndices = new LinkedList<Integer>();
		
		// copy array of tasks
		for(int i = 0; i < t.length;i++)
		{
			t[i] = tasks[i];
			
			// find the constructor values
			int theTaskNumber = tasks[i].getTaskNumber();
			int theResource = 0;
			int theUnits = tasks[i].getClaimAtIndex(0);
			int theNumOfResources = resources.length;
			
			// create new duplicate of task
			t[i] = new Task (theTaskNumber, theResource, theUnits, theNumOfResources);
			
			for(int j = 0; j < resources.length; j++)
			{
				// set claims for each resource type
				t[i].setClaimAtIndex(tasks[i].getClaimAtIndex(j), j);
				
			}
			
			// add all the activities
			for(int j = 0; j < tasks[i].Activities.size(); j++)
			{
				t[i].addToActivities(tasks[i].Activities.get(j));
			}
		}
		
		if(verbose)
		{
			System.out.println("\nDetailed Information for Run : " + theType);
		}
		
		// while all tasks not terminated, manage the tasks
		while (numOfDone < t.length)
		{
			// check for any blocked requests that can be granted
			checkBlockedRequests(t, cycle, BlockedIndices, type, false); 
			
			/*
			 *  check for deadlock
			 *  if deadlock present abort the lowest numbered blocked task
			 *  and check for any tasks to unblock, repeat if necessary
			 */
			while(deadlocked(t))
			{
				abortTask(t, cycle, ReleaseActivities, BlockedIndices);
				numOfDone++;
				checkBlockedRequests(t,cycle, BlockedIndices, type, true);
			}
			
			
			for (int i = 0; i < t.length; i++)
			{
				/*
				 * if task has not terminated or is not blocked or computing
				 * complete its next activity if possible
				 */
				if (cycle[i] == cycleTime && readyForActivity(t[i]))
				{	
					// grab information from activity
					int first = t[i].getActivity().getFirst();
					int second = t[i].getActivity().getSecond();
					String activityType = t[i].getActivity().getType();
					
					// if verbose print out detailed information
					if(verbose && !activityType.equals("release"))
					{
						System.out.print("\nCycle: " + cycle[i] + "\tTask " + (i+1) + 
								" \t " + activityType + " " + first + " " + second);
					}
					
					// check initial claim and requests for banker
					if(type.equals("Banker") && 
						(activityType.equals("initiate") || activityType.equals("request")))
					{
						checkClaims(t, i, cycle, activityType, first, second);
						
						// update numOfDone for aborted tasks
						if(t[i].isAborted())
						{
							numOfDone++;
						}
					}
					
					// handle the activities
					if(activityType.equals("initiate") && !t[i].isAborted())
					{						
						handleInitiate(t, i, cycle);
					}
					
					if(activityType.equals("request") && !t[i].isAborted())
					{
						handleRequest(t, i, cycle, BlockedIndices, type);
					}
					
					if(activityType.equals("compute"))
					{
						handleCompute(t,i, cycle);					
					}
					
					if(activityType.equals("release"))
					{	
						handleRelease(t, i, ReleaseActivities);		
					}
					
					if(activityType.equals("terminate"))
					{
						handleTerminate(t, i, cycle, ReleaseActivities);
						numOfDone++;
					}
					
					// update cycle time for successful activities other than terminate
					if(!activityType.equals("terminate") && !t[i].isBlocked())
					{
						cycle[i]++;
					}
					
				}
				
				if(t[i].isBlocked())
				{
					t[i].incrWaitTime(); // increment the wait time for task t[i]
				}
				
				if(t[i].isComputing())
				{
					// find cycle computing
					int computingCycle = (t[i].computeTime - t[i].computeTimeLeft) + 1;
					
					if(verbose)
					{
						System.out.print("\nCycle: " + cycle[i] + "\tTask " + (i+1) + 
								"\t\t\tcomputing " + computingCycle + " of " + t[i].computeTime);
					}
					
					cycle[i]++; // update cycle task's cycle time
					t[i].decrComputeTime(); // decrement the compute time for task t[i]
					
				}
				
			} // end for
			
			// empty out the release activities list and release resources
			emptyReleaseActivities(t, cycle, ReleaseActivities);		
			cycleTime++; // update cycle time
			
		} // end while	
		
		printStats(t, type);
		
	} // end of manageTasks
	
	/*
	 * printStats - prints for each task the time taken, the waiting time, 
	 * and percentage of time waiting
	 * total time for all tasks, total waiting time and percentage of time waiting
	 */
	public static void printStats(Task[] t, String type)
	{
		int totalEndTime = 0; // time each non-aborted process took to complete
		int totalWaitTime = 0; // time each non-aborted process was waiting
		
		// set the rounding mode
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMaximumFractionDigits(0);
		numberFormat.setRoundingMode (RoundingMode.HALF_UP);
		
		
		System.out.println("\n\nStatistics for Run : " + type + "\n");
		
		for(int i = 0; i < t.length; i++)
		{
			if(t[i].isAborted())
			{
				System.out.println("Task " + (i+1) + "\taborted");
			}
			else
			{
				totalEndTime += t[i].endTime;
				totalWaitTime += t[i].waitTime;
				
				// percentage of time spent waiting for t[i]
				double waitPercentage = (double)(100 * t[i].waitTime)/t[i].endTime;
				
				System.out.println("Task " + (i+1) + "\t" + 
						t[i].endTime + "\t" + t[i].waitTime + 
						"\t" + numberFormat.format(waitPercentage) + "%");
			}
		}
		
		// percentage of time spent waiting for all non-aborted tasks
		double totalWaitPercentage = (double)(100 * totalWaitTime)/totalEndTime;
		
		System.out.println("Total " + "\t" + totalEndTime + "\t" + totalWaitTime + 
				"\t" + numberFormat.format(totalWaitPercentage) + "%");
		
	} // end printStats
	
	/*
	 * readyForActivity - returns true if task has not terminated and 
	 * is not blocked, computing, or aborted,
	 * false otherwise
	 */
	public static boolean readyForActivity(Task task)
	{
		if( task.getEndTime() == -1 && !task.isBlocked() 
				&& !task.isComputing() && !task.isAborted() )
		{	
			return true;
		}
		
		return false;
		
	} // end readyForActivity
	
	/*
	 * checkClaims - aborts the task if any of these three conditions are true
	 * 1. task has initial claim greater than total units of resource type
	 * 2. task has a request greater than total units of resource type
	 * 3. task has a request greater than its initial claim for resource type
	 */
	public static void checkClaims(Task [] t, int i, int[] cycle,
			String activityType, int resourceType, int units)
	{
		boolean abort = false;	// true if task should be aborted, false otherwise
		
		// check claim and request against units of resource type
		if(resources[resourceType-1].getTotalUnits() < units)
		{
			abort = true;
			
			if(verbose)
			{
				System.out.print("\tError: " + activityType + " exceeds units present");
			}
		}
		
		// check request against claim for resource type
		if(activityType.equals("request"))
		{
			// find task's claim and retained units for resource type
			int claim = t[i].getClaimAtIndex(resourceType-1);
			int has = t[i].getHasAtIndex(resourceType-1);
			
			// if request would exceed task's claim abort
			if(units > claim || (units + has) > claim)
			{
				abort = true;
				
				if(verbose)
				{
					System.out.print("\tError: request exceeds claim");
				}
			}
		}
		
		/*
		 *  if any of the conditions are true abort the task
		 *  and release its retained resources if any
		 */
		if (abort)
		{
			// abort the task
			t[i].abort();
			t[i].endTime = cycle[i];
			
			// release all task's retained resources
			releaseAllResources(t, i);
						
			// if verbose print out detailed information
			if(verbose)
			{
				System.out.print(", task has been aborted");
			}
		}
		
	} // end checkClaims
	
	/*
	 * safeTerminate - Returns true if all task's claims can be met
	 * false otherwise
	 */
	public static boolean safeTerminate(Task t, Resource[] r)
	{	
		// return true if task's next activity is terminate
		if(t.getActivity()!= null &&
				t.getActivity().getType().equals("terminate"))
		{
			return true;
		}
		
		// try to satisfy task's claim for each resource
		for (int x = 0; x < r.length; x++)
		{
			int claims = t.getClaimAtIndex(x); // units of resource x task claims
			int has = t.getHasAtIndex(x);	// units of resource x task has
			int needs = claims - has; // units of resource task needs
			
			// grant needs if possible
			if(needs <= r[x].getAvailableUnits())
			{
				t.setHasAtIndex((has + needs), x);	
				
				r[x].setAvailableUnits(r[x].getAvailableUnits() - needs);
			}
			else
			{
				return false; // claim was not satisfied
			}
			
		} // end for
		
		// release all resource units held by task
		for (int x = 0; x < r.length; x++)
		{
			// find number of units to be freed and number currently available
			int freedUnits = t.getHasAtIndex(x);
			int availableUnits = r[x].getAvailableUnits();
			
			// free the units and make them available for other tasks
			r[x].setAvailableUnits(freedUnits + availableUnits);
			t.setHasAtIndex(0, x);
		}
		
		return true; 
		
	} // end safeTerminate
	
	/*
	 * safe - returns true if in a safe state, false otherwise
	 * true if
	 * 1. there is only one task running or
	 * 2. all resources are currently available or
	 * 3. if after granting request 
	 * there is a way for all other tasks to terminate
	 */
	public static boolean safe(Task[] t, int i, int resourceType, int units)
	{
		// declaring and initializing local variables
		int rIndex = resourceType-1;// index of resource within resources[]	
		int allAvailable = 0;		// number of resources with all units available
		int allDone = 0;			// the try to find order for tasks to terminate
		Task [] tCopy = new Task[t.length]; 				 // copy of task array
		Resource [] rCopy = new Resource[resources.length];  // copy of resource array
		boolean [][] done = new boolean [t.length][t.length];// true if task can terminate
		
		// possible orders of completion for tasks
		LinkedList [] orders = new LinkedList[t.length];
		
		// if needs of task for resource exceed units available not safe
		if(resources[rIndex].getAvailableUnits() < 
				(t[i].getClaimAtIndex(rIndex) - t[i].getHasAtIndex(rIndex)))
		{
			return false;
		}
		
		// find number of tasks done
		for(int x = 0; x < t.length; x++)
		{
			if(t[x].endTime != -1)
			{
				allDone++;
			}
		}
		
		// if only one task left state is safe
		if(allDone == t.length-1 || t.length == 1)
		{
			return true;
		}
		
		// check if all resources units are available
		for(int x = 0; x< resources.length; x++)
		{
			// check is all resource units are available
			if(resources[x].getAvailableUnits() 
					== resources[x].getTotalUnits())
			{
				allAvailable++;
			}
		}
			
		// if all resources are available, state is safe
		if(allAvailable == resources.length)
		{
			return true;
		}
		
		// copy the task array
		for (int x = 0; x < t.length; x++)
		{
			// initiate list of orders
			orders[x] = new LinkedList<Integer>();
			
			for (int y = 0; y < t.length; y++)
			{	
				orders[x].add(y); // create all identical orders
					
				// check for already terminated tasks
				if((t[y].endTime != -1))
				{
					done[x][y] = true;
					
				}
			}
		}
		
			
		// sort the orders to make them different
		for(int x= 1; x < t.length; x++)
		{			
			for(int y = x; y < t.length; y++)
			{
				orders[x].add(orders[x].remove());
			}
		}
					
		// find if there is a possible order in which all tasks can terminate
		for(int x = 0; x < t.length; x++)
		{
			// create identical copy of resources
			for(int y = 0; y < resources.length; y++)
			{	
				// find the constructor values
				int rType = resources[y].getType();
				int rAvailable = resources[y].getAvailableUnits();
				int rTotal = resources[y].getTotalUnits();
				
				// create new resource
				rCopy[y] = new Resource(rType, rTotal);
				
				// update units available
				rCopy[y].setAvailableUnits(rAvailable);
			}
			
			// create identical copy of task array
			for(int y = 0; y < t.length; y++)
			{
				// find the constructor values
				int theTaskNumber = t[y].getTaskNumber();
				int theResource = 0;
				int theUnits = t[y].getClaimAtIndex(0);
				int theNumOfResources = resources.length;
				
				tCopy[y] = new Task (theTaskNumber, theResource, theUnits, theNumOfResources);
				
				for(int z = 0; z < resources.length; z++)
				{
					// set claims and units task currently has
					tCopy[y].setClaimAtIndex(t[y].getClaimAtIndex(z), z);
					tCopy[y].setHasAtIndex(t[y].getHasAtIndex(z), z);
					
					// add first two activities 
					if(t[y].Activities.size() >= 2)
					{	
						tCopy[y].addToActivities(t[y].Activities.get(0));
						tCopy[y].addToActivities(t[y].Activities.get(1));
					}
				}
			}
			
			// pretend to grant task's request
			if(units <= rCopy[rIndex].getAvailableUnits())
			{
				// find new number of units task will have
				int newHas = tCopy[i].getHasAtIndex(rIndex) + units;
				
				// grant the request to task copy
				tCopy[i].setHasAtIndex(newHas, rIndex);
				
				// update resource copy
				int newAvailable = rCopy[rIndex].getAvailableUnits() - units;
				rCopy[rIndex].setAvailableUnits(newAvailable);
				
			}
			else
			{
				return false;
			}
			
			// check for possible order for all tasks to terminate after pretended grant
			for(int y = 0; y < t.length; y++)
			{
				// find index of task in current order
				int z = ((Integer)orders[x].get(y)).intValue();
				
				// check if unterminated task can terminate
				if(done[x][y] == false &
					safeTerminate(tCopy[z], rCopy))
				{
					done[x][y] = true;
				}
			}		
		}
		
		// find if any orders successfully terminated
		for(int x = 0; x < done.length; x++)
		{
			int count = 0;
			
			for(int y = 0; y< done.length; y++)
			{
				if(done[x][y])
				{
					count++;
				}
			}
			
			// all task for order x terminated
			if(count == done.length)
			{
				allDone++;
			}
		}
		
		// return true if any successful order was found
		if(allDone > 0)
		{
			return true;
		}
		
		// false otherwise
		return false;
		
	} // end safe
	
	/*
	 * releaseAllResources - loops through task's has[] 
	 * releases the resources, and makes them available for other tasks
	 */
	public static void releaseAllResources(Task [] t, int i)
	{
		for (int x = 0; x < resources.length; x++)
		{
			// find number of units to be freed and number currently available
			int freedUnits = t[i].getHasAtIndex(x);
			int availableUnits = resources[x].getAvailableUnits();
			
			// free the units and make them available for other tasks
			t[i].setHasAtIndex(0, x);
			resources[x].setAvailableUnits(freedUnits + availableUnits);
		}
		
	} // end releaseAllResources
	
	/*
	 * releaseResourceUnits - releases the specified amount of units for 
	 * resource type and makes them available for other tasks
	 */
	public static void releaseResourceUnits(Task [] t, int i, int type, int units)
	{
		// find number of units if any task will retain
		int oldNumOfUnits = t[i].getHasAtIndex(type-1);
		int newNumOfUnits = oldNumOfUnits - units;
		
		// find number of units currently available for other tasks
		int availableUnits = resources[type-1].getAvailableUnits();
		
		// release the units and make them available for other tasks
		t[i].setHasAtIndex(newNumOfUnits, type-1);
		resources[type-1].setAvailableUnits(units + availableUnits);
		
	} // end releaseResourceUnits
	
	/*
	 * deadlocked - detects deadlock
	 * returns true when all non-terminated tasks are blocked or aborted
	 * false otherwise
	 */
	public static boolean deadlocked(Task [] t)
	{
		int unterminatedTasks = 0; // number of tasks not terminated
		int blockedTasks = 0;	   // number of tasks blocked
		
		for (int i = 0; i < t.length; i++)
		{
			if(t[i].endTime == -1)
			{
				unterminatedTasks++;
			}
			
			if(t[i].isBlocked())
			{
				blockedTasks++;
			}			
		}
		
		/*
		 *  deadlock occurs when one or more tasks are blocked
		 *  and the number of tasks blocked equals the tasks non-terminated
		 */
		if(blockedTasks > 0 && blockedTasks == unterminatedTasks)
		{
			return true;
		}
		
		return false;
		
	} // end deadlocked
	
	/*
	 * taskIndexToAbort - return the index of lowest numbered deadlocked task
	 * -1 if none are found
	 */
	public static int taskIndexToAbort(Task [] t)
	{
		int index = -1;
		
		for (int i = 0; i < t.length; i++)
		{
			if(t[i].isBlocked())
			{
				index = i;
				return index;
			}
		}
		
		return index;
		
	} // end taskIndexToAbort
	
	/*
	 * abortTask - unblocks and aborts task 
	 * and releases its resources
	 */
	public static void abortTask(Task[] t, int [] cycle, 
			LinkedList<Activity> ReleaseActivities, LinkedList<Integer> BlockedIndices)
	{		
		// find index of task to abort
		int taskIndex = taskIndexToAbort(t);
			
		// unblock and abort task
		t[taskIndex].unblock();
		t[taskIndex].abort();
		t[taskIndex].endTime = cycle[taskIndex];
		
		// find index in BlockedIndicies
		for (int x = 0; x < BlockedIndices.size(); x++)
		{
			// if index is equal to taskIndex remove it from list
			if (taskIndex == BlockedIndices.get(x))
			{
				BlockedIndices.remove(x);
			}
		}
		
		// if verbose print out detailed information
		if(verbose)
		{
			System.out.print("\nCycle: " + (cycle[taskIndex]-1) + 
					"\tTask " + (taskIndex+1) + " \t aborted after deadlock");
		}
		
		// release all task's retained resources
		releaseAllResources(t, taskIndex);
		
	} // end abortTask
	
	/*
	 * getBlockedIndex - returns the index of the task in blocked list
	 * -1 if not present
	 */
	public static int getBlockedIndex(LinkedList<Task> blocked, Task task)
	{
		int index = -1;
		
		for(int i = 0; i< blocked.size(); i++)
		{
			if(blocked.get(i).getTaskNumber() == task.getTaskNumber())
			{
				index = i;
			}
		}
		
		return index;
		
	} // end getBlockedIndex
	
	/*
	 * updateAllCycleTimes - increments all cycle times by one
	 */
	public static void updateAllCycleTimes(int [] cycle)
	{
		for(int i = 0; i < cycle.length; i++)
		{
			cycle[i]++;
		}
		
	} // end updateAllCycleTimes
	
	/*
	 * emptyReleaseActivities - removes release activities from the list
	 * and releases resources
	 */
	public static void emptyReleaseActivities(Task [] t, int [] cycle,
			LinkedList<Activity> ReleaseActivities)
	{
		// while ReleaseActivities is not empty release the resources
		while(!ReleaseActivities.isEmpty())
		{
			// remove first release activity
			Activity theActivity = ReleaseActivities.remove();
			
			// extract variables
			int index = theActivity.getTaskNumber()-1;
			int first = theActivity.getFirst();
			int second = theActivity.getSecond();
			
			// release the resource units
			releaseResourceUnits(t, index, first, second);
			
			// if verbose and task not aborted or terminated print out detailed information
			if(verbose) //&& !t[index].isAborted() && t[index].endTime == -1)
			{
				System.out.print("\nCycle: " + (cycle[index]-1) + 
						"\tTask " + (index+1) + 
						" \t release " + first + " " + second + 
						"\tsuccessful available at " + cycle[index]);
			}
		}
	} // end emptyReleaseActivities
	
	/*
	 * createReleaseAllActivities - creates release activities for task and inserts
	 * them into the ReleaseActivities list
	 */
	public static void createReleaseAllActivities(Task task, 
			LinkedList<Activity> ReleaseActivities)
	{
		for (int x = 0; x < resources.length; x++)
		{
			// find number of units task has for resource
			int units = task.getHasAtIndex(x);	
			
			/*
			 * if more than one unit create the release activity for 
			 * resource x and add to ReleaseActivities list
			 */
			if(units > 0)
			{
				Activity theActivity = new Activity("release", (x+1), units);
				theActivity.setTaskNumber(task.getTaskNumber());
				ReleaseActivities.add(theActivity);
			}
		}
		
	} // end createReleaseAllActivities
	
	/*
	 * checkBlockedRequests - cycles BlockedIndices to find the index 
	 * of blocked task within task array
	 * grants requests that are possible for current cycle
	 */
	public static void checkBlockedRequests(Task[] t, int [] cycle, 
			LinkedList<Integer> BlockedIndices, String type, boolean afterDeadlock)
	{
		// check task array for tasks with blocked requests
		for(int x = 0; x < BlockedIndices.size(); x++)
		{
			// find index of task within task[]
			int i = BlockedIndices.get(x);
			
			if(t[i].isBlocked() && t[i].hasRequest())
			{	
				if(!afterDeadlock)
				{	
					cycle[i]++; // update task's cycle time
				}
				
				boolean grant = false;	// true if request can be granted, false otherwise
				
				// grab information from activity
				int resourceType = t[i].getRequestedType();
				int numberRequested = t[i].getRequestedUnits();
				
				// check if resources units are available
				if(resources[resourceType-1].getAvailableUnits() >= numberRequested)
				{
					grant = true;
				}
				
				// if banker also check for safety
				if(type.equals("Banker") && 
						!safe(t, i, resourceType, numberRequested))
				{
					grant = false;
				}
				
				// grant request if possible
				if(grant)
				{
					// unblock the task
					t[i].unblock();	
					t[i].setRequest(-1,-1);
				
					// update units of resource for task
					int oldNumOfUnits = t[i].getHasAtIndex(resourceType-1);
					int newNumOfUnits = oldNumOfUnits + numberRequested;
					t[i].setHasAtIndex(newNumOfUnits, resourceType-1);
					
					// update units available to other tasks
					int newAvailableUnits = 
						resources[resourceType-1].getAvailableUnits() - numberRequested;
					resources[resourceType-1].setAvailableUnits(newAvailableUnits);
					
					// remove activity from task's activity list
					t[i].removeFromActivities();
											
					// if verbose print out detailed information
					if(verbose)
					{
						System.out.print("\nCycle: " + cycle[i] + "\tTask " + (i+1) + 
								" \t request " + resourceType + " " + 
								numberRequested + "\tunblocked and successful");
					}
					
					BlockedIndices.remove(x); // remove task index from blocked list
					x--; // update x for loop
					cycle[i]++; // update task's cycle time
				}
				else
				{
					if(!deadlocked(t) && verbose)
					{	
						System.out.print("\nCycle: " + cycle[i] + "\tTask " + (i+1) + 
							" \t request " + resourceType + " " + 
							numberRequested + "\tstill blocked");
				
					}
				}
			}
		}
		
	} // end checkBlockedRequests
	
	
	//====Handle Activities Methods=======================================================
	
	/*
	 * handleInitiate - performs the initiate activity for task t[i]
	 */
	public static void handleInitiate(Task[] t, int i, int [] cycle)
	{
		// grab information from activity
		int resourceType = t[i].getActivity().getFirst();
		int claim = t[i].getActivity().getSecond();
		
		// if task has not been initiated yet set the start time
		if(t[i].getStartTime() == -1)
		{
			t[i].setStartTime(cycle[i]);
		}
		
		// set the claim for resource type
		t[i].setClaimAtIndex(claim, resourceType-1);
		
		// remove activity from task's activity list
		t[i].removeFromActivities();
		
		// if verbose print out detailed information
		if(verbose)
		{
			System.out.print("\tsuccessful");
		}
		
	} // end handleInitiate
			
	/*
	 * handleRequest - performs the request activity for task t[i]
	 */
	public static void handleRequest(Task[] t, int i, int cycle[], 
			LinkedList<Integer> BlockedIndices, String type)
	{
		boolean grant = false;	// true if request can be granted, false otherwise
		
		// grab information from activity
		int resourceType = t[i].getActivity().getFirst();
		int numberRequested = t[i].getActivity().getSecond();
		
		// check if resources units are available
		if(resources[resourceType-1].getAvailableUnits() >= numberRequested)
		{
			grant = true;
		}
		
		// if banker also check for safety
		if(type.equals("Banker") && 
				!safe(t, i, resourceType, numberRequested))
		{
			grant = false;
		}
		
		// grant request if possible, otherwise block the task
		if(grant)
		{
			// update units of resource for task
			int oldNumOfUnits = t[i].getHasAtIndex(resourceType-1);
			int newNumOfUnits = oldNumOfUnits + numberRequested;
			t[i].setHasAtIndex(newNumOfUnits, resourceType-1);
				
			// update units available to other tasks
			int newAvailableUnits = 
				resources[resourceType-1].getAvailableUnits() - numberRequested;
			resources[resourceType-1].setAvailableUnits(newAvailableUnits);
				
			// remove activity from task's activity list
			t[i].removeFromActivities();
				
			// if verbose print out detailed information
			if(verbose)
			{
				System.out.print("\tsuccessful");
			}
		}
		else
		{
			t[i].block();
			t[i].setCycleBlocked(cycle[i]);
			t[i].setRequest(resourceType, numberRequested);
				
			BlockedIndices.add(i);
				
			// if verbose print out detailed information
			if(verbose)
			{
				System.out.print("\tblocked");
			}
		}
		
	} // end handleRequest
	
	/*
	 * handleCompute - performs the compute activity for task t[i]
	 */
	public static void handleCompute(Task[] t, int i, int[] cycle)
	{
		// grab information from activity
		int numOfCycles = t[i].getActivity().getFirst();
		
		// remove the activity
		t[i].removeFromActivities();
		
		// set task to computing and set the compute time
		t[i].setComputing();
		t[i].setComputeTime(numOfCycles);
		
		// if verbose print out detailed information
		if(verbose)
		{
			System.out.print("\tsuccessful");
			
			// find cycle computing
			int computingCycle = (t[i].computeTime - t[i].computeTimeLeft) + 1;
			
			System.out.print("\nCycle: " + cycle[i] + "\tTask " + (i+1) + 
					"\t\t\tcomputing " + computingCycle + " of " + t[i].computeTime);
		}
		
		// decrement the compute time for current cycle
		t[i].decrComputeTime();
		
	} // end handleCompute
	
	/*
	 * handleRelease - performs the release activity for task t[i]
	 */
	public static void handleRelease(Task [] t, int i, 
			LinkedList<Activity> ReleaseActivities)
	{
		// remove from task's activity list and set task number
		Activity theActivity = t[i].Activities.remove();
		theActivity.setTaskNumber(i+1);
		
		// insert into ReleaseActivities list
		ReleaseActivities.add(theActivity);
		
	} // end handleRelease
	
	/*
	 * handleTerminate - performs the terminate activity for task t[i]
	 */
	public static void handleTerminate(Task[] t, int i, int[]cycle, 
			LinkedList<Activity> ReleaseActivities)
	{
		// remove activity from task's activity list
		t[i].removeFromActivities();
		
		// set end time for task
		t[i].setEndTime(cycle[i]);
		
		// create activities to release all of task's retained resources
		//createReleaseAllActivities(t[i], ReleaseActivities);
		
		// release all task's retained resources
		releaseAllResources(t, i);
		
		// if verbose print out detailed information
		if(verbose)
		{
			System.out.print("\tsuccessful");
		}
		
	} // end handleTerminate
}
