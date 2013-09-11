import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Character;

public class Pager {
	
	// declaring and initializing global variables
	static int rIndex = 0;		// index in random number file
	static int numOfFrames = 0;	// number of frames
	static int numOfPages = 0;	// number of pages
	static int currentPage = 0; // current page number
	static int currentWord = 0;	// current word reference
	static int M = 0;			// machine size in words
	static int P = 0;			// page size in words
	static int S = 0;			// size of a process
	static int J = 0;			// job mix
	static int N = 0;			// number of references for each process
	static int D = 0;			// level of debugging
	static String R = null;		// replacement algorithm 
	static StringBuilder random;// random numbers from file
	static Frame[] frames;		// array of frames
	static Process[] processes;	// array of processes
	
	public static void main(String[] args) throws IOException 
	{	
		input(args); 					// organize the input 
		printInfo(); 					// print information from input
		random = new StringBuilder();	// initialize random
		random = fileToSB(args, 7); 	// create random from file
		createProcesses(); 				// initialize the process array
		
		if(D == 1 || D == 2)
		{	
			printProcesses(); 			// print the process array
		}	
		
		simulateReferences();			// simulate memory references
		
	}
	
	/*
	 * input - grabs the input from the command line
	 * and stores it in the appropriate variables
	 */
	public static void input(String args[])
	{
		M = Integer.parseInt(args[0]);
		P = Integer.parseInt(args[1]);
		S = Integer.parseInt(args[2]);
		J = Integer.parseInt(args[3]);
		N = Integer.parseInt(args[4]);
		R = args[5];
		D = Integer.parseInt(args[6]);
		
		// calculate number of frames and pages
		numOfFrames = M/P;
		numOfPages = S/P;
			
	} // end input
	
	/*
	 * printInfo - prints out description of input
	 */
	public static void printInfo()
	{
		System.out.println("\n\nThe machine size is " + M + ".");
		System.out.println("The page size is " + P + ".");
		System.out.println("The process size is " + S + ".");
		System.out.println("The job mix number is " + J + ".");
		System.out.println("The number of references per process is " + N + ".");
		System.out.println("The replacement algorithm is " + R + ".");
		System.out.println("The level of debugging output is " + D + ".");
		
	} // end printInfo
	
	/*
	 * fileToSB - reads file and returns it's contents as StringBuilder
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
	 * createProcesses - create the process array based on job mix J
	 * cases:
	 * J = 1	One process A = 1, B = C = 0
	 * J = 2	Four processes, each with A = 1 and B = C = 0
	 * J = 3	Four processes, each with A = B = B = 0
	 * J = 4 	Four processes 	1. A = .75, B = .25, C = 0
	 * 							2. A = .75, B = 0, C = .25
	 * 							3. A = .75, B = .125, C = .125
	 * 							4. A = .5, B = .125, C = .125
	 */
	public static void createProcesses()
	{
		switch(J)
		{
			case 1:
				processes = new Process[1];
				processes[0] = new Process(1, 0, 0);
				break;
			case 2:
				processes = new Process[4];
				
				for(int i = 0; i < processes.length; i++)
				{
					processes[i] = new Process(1, 0, 0);
				}

				break;
			case 3:
				processes = new Process[4];
				
				for(int i = 0; i < processes.length; i++)
				{
					processes[i] = new Process(0, 0, 0);
				}

				break;
			case 4:
				processes = new Process[4];
				processes[0] = new Process(.75, .25, 0);
				processes[1] = new Process(.75, 0, .25);
				processes[2] = new Process(.75, .125, .125);
				processes[3] = new Process(.5, .125, .125);
				break;	
		}
		
	} // end createProcesses
	
	/*
	 * createFrames - create array of frames
	 */
	public static void createFrames()
	{
		frames = new Frame[numOfFrames];
		
		for(int i = 0; i < frames.length; i++)
		{
			frames[i] = new Frame();
		}
		
	} // end createFrames
	
	/*
	 * printProcesses - prints out a table with 
	 * information about each process
	 */
	public static void printProcesses()
	{
		System.out.println("\n=====================================");
		System.out.println("Process Array");
		System.out.println("=====================================");
		System.out.println("Index\t\tA\tB\tC");
		System.out.println("-------------------------------------");
		
		for(int i = 0; i < processes.length; i++)
		{
			System.out.println(i + "\t\t" + processes[i]);
		}
		
		System.out.println("=====================================");
		
	} // end printProcesses
	
	/*
	 * firstWord - calculates the first word
	 * to be referenced by process at index
	 */
	public static int firstWord(int index)
	{
		int k = index + 1; // process number
		int w = 111*k % S;
		processes[index].setNextWord(w);
		
		return w;
		
	} // end firstWord
	
	/*
	 * nextWord - calculates the next word
	 * to be referenced by process at index
	 */
	public static void nextWord(int index)
	{
		// last referenced word by process
		int w = processes[index].getNextWord();

		// next random number from random file
		int r = getRandomNumber();
			
		// quotient 0<=y<1 to determine probabilities
		double y = r / (Integer.MAX_VALUE + 1d);
			
		// case 1: y < A
		if (y < processes[index].getA())
		{
			w = ((w + 1 + S) % S);
			processes[index].setNextWord(w);
		}
		
		// case 2: y < A + B
		else if (y < (processes[index].getA() + 
				 	  processes[index].getB()))
		{
			w = ((w - 5 + S) % S);
			processes[index].setNextWord(w);
		} 
			
		// case 3: y < A + B + C
		else if (y < (processes[index].getA() + 
				   	  processes[index].getB() + 
				   	  processes[index].getC()))
		{
			w = ((w + 4 + S) % S);
			processes[index].setNextWord(w);
		}
		
		// case 4: y >= A + B + C
		else if (y >= (processes[index].getA() + 
				   	   processes[index].getB() + 
				   	   processes[index].getC()))
		{		
			w = randomWithRange(S);
			processes[index].setNextWord(w);
				
			if(D == 2)
			{
				System.out.print(" to choose " +
						"next reference " + w);
			}
		}
			
		if (D == 2)
		{
			System.out.print("\nProcess " + (index+1) + 
					" uses random number: " + r);
		}
		
	} // end nextWord
	
	/*
	 * pageForWord - returns the page number for
	 * word w
	 */
	public static int pageForWord(int w)
	{
		int pageNumber = 0;	// page number for word
		
		int lower = 0;	// lower number to multiply P by		
		int upper = 1;	// lower number to multiply P by
		
		// lower and upper bounds of reference in page
		int lowerBound = P*lower;
		int upperBound = P*upper;
		
		/*
		 *  search for pageNumber
		 *  while the w is not in range of pageNumber
		 */
		while(!(lowerBound <= w && w < upperBound))
		{			
			pageNumber++;
			lower++;
			upper++;	
			lowerBound = P*lower;
			upperBound = P*upper;
		}
		
		return pageNumber;
		
	} // end pageForWord
	
	/*
	 * getRandomNumber - returns next random 
	 * number token in random StringBuilder
	 */
	public static int getRandomNumber()
	{
		int randomNumber = 0; 			// will hold return 
		String randomNumberString = ""; // random number as string
		char c = random.charAt(rIndex); // current char in random
		
		// increment index and char is not digit
		while(!Character.isDigit(c))
		{
			rIndex++;
			c = random.charAt(rIndex);
		}
		
		// create random number string while c is digit
		while(Character.isDigit(c))
		{
			randomNumberString += c;
			rIndex++;
			c = random.charAt(rIndex);
		}
		
		randomNumber = Integer.parseInt(randomNumberString);		
		return randomNumber;
		
	} // end getRandomNumber
	
	/*
	 * randomWithRange - returns number in
	 *  interval [0,U), using the numbers 
	 *  in the random numbers file
	 */
	public static int randomWithRange(int U)
	{
		int X = getRandomNumber();
		
		if(D == 2)
		{
			System.out.print("\nUsing random " +
					"number " + X);
		}
		
		return (X % U);
		
	} // end randomWithRange
	
	/*
	 * findFreeFrame - returns frame index
	 * of highest indexed free frame, if none -1
	 */
	public static int findFreeFrame()
	{
		for(int i = frames.length-1; i >= 0; i--)
		{
			if(frames[i].isFree())
			{
				return i;
			}
		}
		
		return -1;
		
	} // end findFreeFrame
	
	/*
	 * hit - updates reference time for hit
	 * and prints debugging output if requested
	 */
	public static void hit(int cycle, int f)
	{	
		
		// update last reference time
		frames[f].setLastRefTime(cycle);
		
		// print out debugging information
		if(D == 1 || D == 2)
		{
			System.out.print(" Hit in frame " + f);
		}
		
	} // end hit
	
	/*
	 * placePageInFrame - places page p 
	 * referencing for process x in frame f
	 * and prints debugging output if requested
	 */
	public static void placePageInFrame(int x, int p, 
			int f, int cycle, boolean afterEviction)
	{	
		// update frames
		frames[f].setLastRefTime(cycle);
		frames[f].setTimeLoaded(cycle);
		frames[f].setUsed();
		frames[f].setResidentPage(p);
		frames[f].setResidentProcess(x);
		
		// update page faults for current process
		processes[x].incrPageFaults();
			
		// if no page was evicted, fault using free frame
		if( (D == 1 || D == 2) && !afterEviction)
		{
			System.out.print(" FAULT, using free frame " + f);
		}
		
	} // end place pagePageInFrame
	
	/*
	 * frameToFree - finds and returns frame index
	 * of frame to evict its residing page from
	 * based on replacement algorithm R
	 */
	public static int frameToFree()
	{	
		int frame = 0;
		
		// lru - find page least recently referenced
		if(R.equals("lru"))
		{
			int lastRefTime = N*processes.length;
					
			for(int i = 0; i < frames.length; i++)
			{
				int tempLastRefTime = frames[i].getLastRefTime();
				
				if(tempLastRefTime < lastRefTime
					&& tempLastRefTime >= 0)
				{
					lastRefTime = tempLastRefTime;
					frame = i;
				}
			}	
		}
		
		// random - find random frame to free
		if(R.equals("random"))
		{
			frame = randomWithRange(numOfFrames);
			
			if(D == 2)
			{
				System.out.print(" to select " +
						"frame " + frame);
			}		
		}
		
		// fifo - find page first loaded
		if(R.equals("fifo"))
		{
			int timeLoaded = N*processes.length;
			
			for(int i = 0; i < frames.length; i++)
			{
				int tempTimeLoaded = frames[i].getTimeLoaded();
				
				if(tempTimeLoaded < timeLoaded
						&& tempTimeLoaded >= 0)
				{
					timeLoaded = tempTimeLoaded;
					frame = i;
				}
			}
		}
		
		return frame;
		
	} // end frameToFree
	
	/*
	 * evictPage - evicts page from its 
	 * residing frame at cycle time
	 * and prints debugging output if requested
	 */
	public static void evictPage(int f, int cycle)
	{
		// page index of page in frame
		int page = frames[f].getResidentPage();
		
		// process index for process using frame
		int p = frames[f].getResidentProcess();
		
		// update frame
		frames[f].setTimeEvicted(cycle);
		frames[f].calcTimeResident();
		frames[f].setTimeLoaded(-1);
		frames[f].setTimeEvicted(-1);
		frames[f].setFree();
		frames[f].setResidentPage(-1);	
		frames[f].setResidentProcess(-1);
		
		// update process's time resident
		processes[p].updateResidency(frames[f].getResidencyTime());
		
		// increment number of evictions
		processes[p].incrEvictions();
		
		// print debugging output
		if(D == 1 || D == 2)
		{
			System.out.print(" FAULT, evicting page " + 
					page + " of " + (p+1) + 
					" from frame " + f);
		}
		
	} // end evictPage
	
	/*
	 * switchProcess - switch to another process
	 * if more than one process
	 */
	public static int switchProcess(int p)
	{		
		// if only one process return p
		if(processes.length == 1)
		{
			return p;
		}
		
		// if last process return 0
		else if(p == processes.length-1)
		{
			return 0;
		}
		
		return ++p;	/*
		 			 *  if not only or last process 
		 			 *  increment and return
		 			 */
		
	} // end switchProcess
	
	/*
	 * frameForReference - returns frame
	 * for reference made by process in page
	 * if resident, -1 otherwise
	 */
	public static int frameForReference(int page, int process)
	{
		int frame = -1;
		
		for(int i = 0; i < frames.length; i++)
		{
			if(frames[i].getResidentPage() == page
				&& frames[i].getResidentProcess() == process)
			{
				frame = i;
			}
		}
		
		return frame;
		
	} // end frameForReference
	
	/*
	 * simulateReferences - simulate memory references 
	 * based on page size, program size, replacement 
	 * algorithm and job mix
	 * print debugging output if requested
	 */
	public static void simulateReferences()
	{
		if(D == 1 || D == 2)
		{	
			System.out.println("\nNumber of pages: " + numOfPages);
			System.out.println("Number of frames: " + numOfFrames);
		}	
		
		// create default frame array
		createFrames();
		
		int q = 1; // quantum q
		int p = 0; // process index
		
		for(int i = 1; i <= (N*processes.length); i++)
		{
			/*
			 * switch process once quantum or number 
			 * of references for process is reached
			 */
			if (q > 3 || processes[p].getReferences() == N)
			{
				p = switchProcess(p);
				q = 1;
			}
			
			/*
			 *  find word to be referenced, 
			 *  if -1 none previously calculated
			 */
			if(processes[p].getNextWord() == -1)
			{
				// find first word, if necessary
				currentWord = firstWord(p);
			}
			else
			{
				// get next word, previously calculated
				currentWord = processes[p].getNextWord();
			}
			
			// find the current page number for reference word w
			currentPage = pageForWord(currentWord);
			
			// print information based on level of debugging
			if (D == 1 || D == 2)
			{
				System.out.print("\n\nProcess " + (p+1) + 
						" references word " + currentWord + 
						" (page " + currentPage + 
						") at time " + i + ":");
			}
			
			/* 
			 * find if page is resident and referencing words 
			 * for current process, if not find a frame
			 * if no free frames choose a frame to free
			 * and evict its residing page
			 */
			
			// check if word reference is loaded in any frame
			int frameIndex = frameForReference(currentPage, p);
			
			// hit, page is resident and holding current process
			if(frameIndex != -1)
			{
				hit(i, frameIndex);
			}
			
			// no hit, word reference for process not loaded in any frame
			else
			{	
				// check for free frame, placement question
				frameIndex = findFreeFrame();
				
				// frame found
				if(frameIndex != -1)
				{
					// place current page in free frame
					placePageInFrame(p, currentPage, frameIndex, i, false);
				}
				
				// no free frames, choose frame to free and evict resident
				else
				{
					frameIndex = frameToFree(); // replacement question
					
					// evict page and place current page in same frame
					evictPage(frameIndex, i);
					placePageInFrame(p, currentPage, frameIndex, i, true);					
				}
				
			} 
			
			// increment number of references made by process
			processes[p].incrReferences();
			
			q++; //increment quantum
			nextWord(p); // find next word for process	
			
		} // end for loop
		
		printStats();
		
	} // end simulateReferences
	
	/*
	 * printStats - print out statistics for page 
	 * faults and residency
	 */
	public static void printStats()
	{
		int totalEvictions = 0;	// total evictions for all processes
		int totalFaults = 0;    // total faults for all processes
		int totalResidency = 0; // total time resident for all processes
		
		System.out.println("\n");
		
		// find faults, time resident and evictions for each process
		for(int i = 0; i < processes.length; i++)
		{
			int faults = processes[i].getPageFaults();
			int residency = processes[i].getResidency();
			int evictions = processes[i].getEvictions();
			
			// if no evictions, residency is undefined
			if(evictions == 0)
			{
				System.out.println("\nProcess " + (i+1) +
						" had " + faults + " faults." + 
						"\n\tWith no evictions, average " +
						"residence is undefined.");
			}			
			else
			{	
				System.out.println("\nProcess " + (i+1) +
				" had " + faults + " faults and " +
				(double)residency/evictions + 
				" average residency.");
			}	
			
			// update totals
			totalEvictions += evictions;
			totalFaults += faults;
			totalResidency += residency;
			
		} // end for
		
		/*
		 *  print faults and avg residency for all processes
		 *  if no evictions, residency is undefined
		 */
		if(totalEvictions == 0)
		{
			System.out.println("\nThe total number of " +
					"faults is " + totalFaults +
					".\n\tWith no evicitons, the overall " +
					"average residence is undefined.\n");
		}
		else
		{	
			System.out.println("\nThe total number of " +
				"faults is " + totalFaults + 
				" and the overall average residency is " 
				+ (double)totalResidency/totalEvictions
				+ ".\n");
		}	
		
	} // end printStats

}
