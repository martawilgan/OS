// Task class
//
// CONSTRUCTION: 
// 1. initiates arrays with size large enough to hold
// 		information for each resource and linkedList 
// 2. sets the start time and claims for resource type provided
//		(more claims can be added later via the setClaimAtIndex(x, y) method)
// 3. sets other variables to defaults
//
// ******************************PUBLIC OPERATIONS*******************************
// boolean hasRequest()				-> true if request outstanding, false otherwise
// boolean isAborted()				-> true if task has been aborted, false otherwise
// boolean isBlocked()				-> true if task is blocked, false otherwise
// boolean isComplete()				-> true if activity list is complete, false otherwise
// boolean isComputing()			-> true if task is computing, false otherwise
// int getComputeTime()				-> returns number of cycles computing as int
// int getCycleBlocked()			-> returns cycle task was last blocked as int
// int getEndTime()					-> returns cycle task is terminated as int
// int getRequestedType()			-> returns type of resource requested as int
// int getRequestedUnits()			-> returns units of resource requested as int
// int getStartTime()				-> returns cycle task is initiated as int
// int getTaskNumber()				-> returns task number as int
// int getWaitTime()				-> returns number of cycles task is blocked as int
// int getHasAtIndex(x)				-> returns number of units task has for resource type x
// int getClaimAtIndex(x)			-> returns number of units task claims for resource type x
// Activity getActivity()			-> returns next Activity in linkedList
// void addToActivities(x)			-> adds Activity x to the linkedList
// void addToHasAtIndex(x, y) 		-> add x amount to resource type y
// void clearRequest()				-> sets requestedType and requestedUnits to -1
// void computeForCycles(x)			-> sets computing to true and computeTime to x
// void decrComputeTime()			-> decrements computeTime, sets computing to false when 0
// void incrWaitTime()				-> increments waitTime
// void removeFromHasAtIndex(x, y) 	-> subtracts x from the resource type y
// void removeFromActivities()		-> removes first Activity in linkedList
// void abort()						-> sets aborted to true
// void block()						-> sets blocked to true
// void unblock()					-> sets blocked to false, cycleBlocked to -1
// void setComplete()				-> sets complete to true
// void setComputing()				-> sets computing to true
// void setComputeTime(x)			-> sets computeTime
// void setCycleBlocked(x)			-> sets cycleBlocked to x
// void setEndTime(x)				-> sets endTime to x
// void setRequest(x, y)			-> sets requestedType to x and requestedUnits to y
// void setStartTime(x)				-> sets startTime to x
// void setHasAtIndex(x, y) 		-> sets resource type y to x
// void setClaimAtIndex(x, y) 		-> sets claims for resource type y to x
// public String toString()			-> returns Task information
// *******************************************************************************
import java.util.LinkedList;

public class Task {
	
	Task (int theTaskNumber, int theResource, int theUnits, int theNumOfResources)
	{
		// set the taskNumber
		taskNumber = theTaskNumber;
		
		// initiate arrays and linked list
		claims = new int[theNumOfResources];
		has = new int[theNumOfResources];
		Activities = new LinkedList<Activity>();
		
		// set the claims for resource type
		claims[theResource] = theUnits;
		
		// set other variables to defaults
		aborted = false;
		blocked = false;
		computing = false;
		complete = false;
		cycleBlocked = -1;
		endTime = -1;
		startTime = -1;
		requestedType = -1;
		requestedUnits = -1;
		waitTime = 0;
				
	}
	boolean hasRequest()
	{
		if (requestedType == -1 && requestedUnits == -1)
		{
			return false;
		}
		
		return true;
	}
	
	boolean isAborted()
	{
		return aborted;
	}
	
	boolean isBlocked()
	{
		return blocked;
	}
	
	boolean isComplete()
	{
		return complete;
	}
	
	boolean isComputing()
	{
		return computing;
	}
	
	int getComputeTime()
	{
		return computeTime;
	}
	
	int getCycleBlocked()
	{
		return cycleBlocked;
	}
	
	int getEndTime()
	{
		return endTime;
	}
	
	int getRequestedType()
	{
		return requestedType;
	}
	
	int getRequestedUnits()
	{
		return requestedUnits;
	}
	
	int getStartTime()
	{
		return startTime;
	}
	
	int getTaskNumber()
	{
		return taskNumber;
	}
	
	int getWaitTime()
	{
		return waitTime;
	}
	
	int getHasAtIndex(int theIndex)
	{
		return has[theIndex];
	}
	
	int getClaimAtIndex(int theIndex)
	{
		return claims[theIndex];
	}
	
	Activity getActivity()
	{
		return Activities.peekFirst();
	}
	
	void addToActivities(Activity theActivity)
	{
		Activities.add(theActivity);
	}
	
	void addToHasAtIndex(int theValue, int theIndex)
	{
		int newAmount = has[theIndex] + theValue;
		
		if(newAmount <= claims[theIndex])
		{
			has[theIndex] = newAmount;
		}
	}
	
	void clearRequest()
	{
		requestedType = -1;
		requestedUnits = -1;
	}
	
	void computeForCycles(int theTime)
	{
		if (!computing)
		{
			computing = true;
			computeTime = theTime;
		}
	}
	
	void decrComputeTime()
	{
		computeTimeLeft--;
		
		if(computeTimeLeft == 0)
		{
			computing = false;
			computeTime = 0;
		}
	}
	
	void incrWaitTime()
	{
		waitTime++;
	}
	
	void removeFromHasAtIndex(int theValue, int theIndex)
	{
		if (theValue<= has[theIndex])
		{	
			has[theIndex] -= theValue;
		}
	}
	
	void removeFromActivities()
	{
		if (!Activities.isEmpty())
		{	
			Activities.remove();
		}
	}
	
	void abort()
	{
		if(!aborted)
		{
			aborted = true;
		}
	}
	
	void block()
	{
		if (!blocked)
		{
			blocked = true;
		}
	}
	
	void unblock()
	{
		if(blocked)
		{
			blocked = false;
			cycleBlocked = -1;
		}
	}
	
	void setComplete()
	{
		if(!complete)
		{
			complete = true;
		}
	}
	
	void setComputing()
	{
		if(!computing)
		{
			computing = true;
		}
	}
	
	void setComputeTime(int theComputeTime)
	{
		if(computeTime != theComputeTime)
		{	
			computeTime = theComputeTime;
			computeTimeLeft = theComputeTime;
		}
	}
	
	void setCycleBlocked(int theCycle)
	{
		cycleBlocked = theCycle;
	}
	
	void setEndTime(int theEndTime)
	{
		endTime = theEndTime;
	}
	
	void setRequest(int theType, int theUnits)
	{
		requestedType = theType;
		requestedUnits = theUnits;
	}
	
	void setStartTime(int theStartTime)
	{
		startTime = theStartTime;
	}
	
	void setHasAtIndex(int theValue, int theIndex)
	{		
		has[theIndex] = theValue;
	}
	
	void setClaimAtIndex(int theValue, int theIndex)
	{
		if (claims[theIndex] == 0)
		{	
			claims[theIndex] = theValue;
		}	
	}
	
	public String toString()
	{
		String info = 	"\n================================================" +
						"\nTASK : " + taskNumber +
						"\n------------------------------------------------" +
						"\nStart Time: " + startTime +
						"\nEnd Time: " + endTime +
						"\nWait Time: " + waitTime +
						"\nCompute Time: " + computeTime;
		
		if(aborted)
		{
			info += "\nAborted: Yes";
		}
		else
		{
			info += "\nAborted: No";
		}
		if (blocked)
		{
			info += "\nBlocked: Yes";
		}
		else
		{
			info += "\nBlocked: No";
		}
		
		if(computing)
		{
			info += "\nComputing: Yes";
		}
		else
		{
			info += "\nComputing: No";
		}	
		
		info += "\n------------------------------------------------";
		info += "\n\nResources: \n";
		
		for(int i = 0; i < claims.length; i++)
		{
			info += "\nResource type: " + (i+1) + "\tClaims: " + claims[i] +
					"\tHas: " + has[i];
		}
		
		info += "\n------------------------------------------------";
		info += "\n\nActivities: \n";
		
		for(int i = 0; i < Activities.size(); i++)
		{
			info += "\n" + Activities.get(i).toString();
		}
		
		info += "\n================================================\n";
		
		return info;
	}
	
	boolean aborted;	// true if task has been aborted, false otherwise
	boolean blocked; 	// true if task is blocked, false otherwise
	boolean complete;	// true if all activities in linkedList, false otherwise
	boolean computing; 	// true if task is computing, false otherwise
	int computeTime;	// number of cycles task is to compute
	int computeTimeLeft;// number of cycles task has left to compute
	int cycleBlocked;	// cycle task has last been blocked, -1 if not blocked
	int endTime;		// cycle task is terminated, -1 if active
	int requestedType;	// resource type requested, -1 if none
	int requestedUnits;	// number of units requested, -1 if no current requests 
	int startTime;		// cycle task is initiated, -1 if not yet initiated
	int taskNumber;		// task number
	int waitTime;		// number of cycles task is blocked
	int has[]; 			// units task has for resources (index is resource type)
	int claims[]; 		// claims for resources (index is resource type)
	LinkedList<Activity> Activities; // list of task's activities
	
}
