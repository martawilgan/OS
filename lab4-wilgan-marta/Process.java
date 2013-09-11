// Process class
//
// ****************************PUBLIC OPERATIONS*******************************
// int getA()					->	return A
// int getB()					->	return B
// int getC()					->	return C
// int getCurrentFrame()		->  return current frame referencing page is resident
// int getEvictions()			->  return number of evictions
// int getNextWord()			->  return the next word to reference
// int getReferences()			->  return number of references made
// int getPageFaults()			->  return the number of page faults
// int getResidency()			->  return the total residency time
// void incrEvictions()			->  increment number of evictions by 1
// void incrReferences()		->  increment number of references by 1
// void incrPageFaults()		->  increment number of page faults by 1
// void setCurrentFrame(x)		->  set current frame to x
// void setNextWord(x)			->  set next word referenced to x
// void setPageFaults(x)		->  set number of page faults to x
// void updateResidency(x)		->  adds x to residencyTime
// String toString()			->  return Process information
// *****************************************************************************

public class Process {
	
	Process(double a, double b, double c)
	{
		A = a;
		B = b;
		C = c;
		currentFrame = -1;
		evictions = 0;
		nextWord = -1;
		references = 0;
		pageFaults = 0;
		residencyTime = 0;
		
	}
	
	public double getA()
	{
		return A;
	}
	
	public double getB()
	{
		return B;
	}
	
	public double getC()
	{
		return C;
	}
	
	public int getCurrentFrame()
	{
		return currentFrame;
	}
	
	public int getEvictions()
	{
		return evictions;
	}
	
	public int getNextWord()
	{
		return nextWord;
	}
	
	public int getReferences()
	{
		return references;
	}
	
	public int getPageFaults()
	{
		return pageFaults;
	}
	
	public int getResidency()
	{
		return residencyTime;
	}
	
	public void incrEvictions()
	{
		evictions++;
	}
	
	public void incrReferences()
	{
		references++;
	}
	
	public void incrPageFaults()
	{
		pageFaults++;
	}
	
	public void setCurrentFrame()
	{
		
	}
	
	public void setNextWord(int theWord)
	{
		nextWord = theWord;
	}
	
	public void setPageFaults(int faults)
	{
		pageFaults = faults;
	}
	
	public void updateResidency(int residency)
	{
		residencyTime += residency;
	}
	
	public String toString()
	{
		return A + "\t" + B + "\t" + C;
	}
	
	double A;
	double B;
	double C;
	int currentFrame;	// current frame number page referenced residing in
	int nextWord;		// next word to be referenced by process
	int evictions;		// number of times process's page is evicted
	int references;		// number of references made
	int pageFaults;		// number of page faults
	int residencyTime;	// total time page was resident

}
