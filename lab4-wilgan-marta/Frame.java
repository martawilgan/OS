// Frame class
//
// ****************************PUBLIC OPERATIONS*******************************
// void calcTimeResident()		->  calculate time page was resident
// int getLastRefTime()			->  return time frame was referenced, -1 if not
// int getTimeLoaded()			->  return time page loaded into frame. -1 if not
// int getTimeEvicted()			->  return time page evicted out of frame, -1 if not
// int getResidencyTime()		->  return total time page was resident
// int getResidentPage()		->  return number of resident page
// int getResidentProcess()		->  return number of process using resident page
// boolean isFree()				->  return true if frame is free, false otherwise
// void setLastRefTime(x)		->  set last time frame was referenced to x
// void setFree()				->  set free to true
// void setUsed()				->  set free to false
// void setResidentPage(x)		->  set the number of current resident page to x
// void setResidentProcess(x)	->  set the number of current process to x
// void setTimeLoaded(x)		->  set last time loaded to x
// void setTimeEvicted(x)		->  set last time evicted to x
// *****************************************************************************

public class Frame {
	
	Frame()
	{
		lastRefTime = -1;
		timeLoaded = -1;
		timeEvicted = -1;
		residencyTime = -1;
		residentPage = -1;
		residentProcess = -1;
		free = true;
	}
	
	public void calcTimeResident()
	{
		residencyTime = timeEvicted - timeLoaded;
	}
	
	public int getLastRefTime()
	{
		return lastRefTime;
	}
	
	public int getTimeLoaded()
	{
		return timeLoaded;
	}
	
	public int getTimeEvicted()
	{
		return timeEvicted;
	}
	
	public int getResidencyTime()
	{
		return residencyTime;
	}
	
	public int getResidentPage()
	{
		return residentPage;
	}
	
	public int getResidentProcess()
	{
		return residentProcess;
	}
	
	public boolean isFree()
	{
		return free;
	}
	
	public void setFree()
	{
		free = true;
	}
	
	public void setLastRefTime(int time)
	{
		lastRefTime = time;
	}
	
	public void setUsed()
	{
		free = false;
	}
	
	public void setTimeLoaded(int time)
	{
		timeLoaded = time;
	}
	
	public void setTimeEvicted(int time)
	{
		timeEvicted = time;
	}
	
	public void setResidentPage(int page)
	{
		residentPage = page;
	}
	
	public void setResidentProcess(int process)
	{
		residentProcess = process;
	}
	
	int lastRefTime; 	// last cycle time frame was referenced
	int timeLoaded;		// last time page was loaded into current frame
	int timeEvicted;	// last time page was evicted
	int residencyTime;	// time page is resident
	int residentPage;	// current page in frame
	int residentProcess;// current process using page in frame
	boolean free;		// true if frame has no resident, false otherwise

}
