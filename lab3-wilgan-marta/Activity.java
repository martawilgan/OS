// Activity class
//
// ************************PUBLIC OPERATIONS***************************
// getType()			->	return Activity type as String
// getFirst()			->  return first as int
// getSecond()			->	return second as int
// getTaskNumber()		->  return taskNumber as int
// set taskNumber(x)	->  set the taskNumber to x
// String toString()	->  return Activity information
// *********************************************************************

public class Activity {
	
	Activity (String theType, int theFirst, int theSecond)
	{
		type = theType;
		first = theFirst;
		second = theSecond;
	}
	
	String getType()
	{
		return type;
	}
	
	
	int getFirst()
	{
		return first;
	}
	
	int getSecond()
	{
		return second;
	}
	
	int getTaskNumber()
	{
		return taskNumber;
	}
	
	void setTaskNumber(int theTaskNumber)
	{
		taskNumber = theTaskNumber;
	}
	
	public String toString()
	{
		return "" + type + " " + first + " " + second;
	}

	String type;
	int first;
	int second;
	int taskNumber;
}
