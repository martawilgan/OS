import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Character;
import java.lang.Integer;
import java.lang.reflect.Array;
import java.util.LinkedList;

public class linker {

	/**
	 * @throws IOException 
	 *
	 */
	
	// declaring an initializing global variables
	static boolean definitionList;		// true if currently in definition list
	static boolean useList;				// true if currently in use list
	static boolean programText;			// true if currently in program text
	static char c;						// most recently visited char
	static int base = 0; 				// base of most recently processed module
	static int modNum = 1; 				// number of module
	static int nextIndex = 0;			// index for next symbol inserted into list
	static int numOfDeclared = 0;		// how many symbols declared in definition
	static int numOfInstructions = 0;  	// number of instructions in current module
	static int maxSize = 600;			// 600 words 
	static int mapIndex = 0;			// memory mapping
	static String usageWarning = "";	// symbol usage warnings
	static StringBuffer bufferOutput = new StringBuffer();				// output
	static LinkedList<Symbol> Symbols = new LinkedList<Symbol>();		// list of symbols
	static LinkedList<Module> Modules = new LinkedList<Module>(); 		// list of modules
	static LinkedList<Symbol> SymbolsToUse = new LinkedList<Symbol>(); 	// list of symbols to be used
	static LinkedList<Instruction> Instructions = new LinkedList<Instruction>(); // instruction list
	
	public static void main(String[] args) throws IOException {		
		
		// declaring and initializing local variables	
		String currentLine;			// current line of file
		
		// read the text file	
		BufferedReader reader = new BufferedReader(new FileReader (args[0]) );
		StringBuffer bufferInput = new StringBuffer();
		
		// append characters to StringBuffer
		while((currentLine=reader.readLine())!=null)
		{			
			bufferInput.append((currentLine+"\n"));
		}
		
		/*
		 *  first pass 
		 *  find symbols and their absolute addresses
		 *  find base address and length of each module
		 */
		
		// file starts with definition list 
		definitionList = true;
		int first =0;
		while( !isNumber(bufferInput.charAt(first)) )
		{
			first++;
		}
		
		// first char that is number is number of symbols being declared
		c = bufferInput.charAt(first);
		numOfDeclared = Character.getNumericValue(c); 
		bufferOutput.append("\n\nSymbol Table\n");	
		
		for(int i = first; i < bufferInput.length(); i++)
		{
			/* 
			 * c will be number of instructions for 
			 * module if followed by instruction
			 * and number of symbols declared if 
			 * followed by symbol in definition list
			 */
			
			// if there are symbols declared process them	
			while ( (numOfDeclared > 0) && definitionList)
			{
				i = processSymbols(bufferInput, i);			
			} // end of while loop	
				
			if(numOfDeclared == 0)
			{
				definitionList = false;
				i = skipTilProgramText(bufferInput, i);
				i = processModules(bufferInput, i);
				//updateSymbols();
			}	
		} // end of for loop for first pass
		
		createSymbolTable(); // create the symbol table
		
		// start at beginning of buffer for second pass
		c = bufferInput.charAt(0);
		
		modNum = 0; // list counts from zero
		
		bufferOutput.append("\nMemory Map\n");
		
		// second pass... generates output		
		for(int i = 0; i < bufferInput.length()-1; i++)
		{	
			i = skipTilUseList(bufferInput, i);
			
			if ( isNumber(c) && Character.getNumericValue(c) > 0 )
			{	
				// number of symbols used in program text of current module
				int numOfSymbols = Character.getNumericValue(c);
				
				i = skipSpaces(bufferInput, i);
				
				// temporary symbol variables
				String symbol = "";
				String pointer = "";
			
				// find the symbols to be used
				for (int a = 0; a < numOfSymbols; a++)
				{	
					while( i < (bufferInput.length()-1) && (isSymbol(c) || isNumber(c) ))
					{
						symbol += Character.toString(c);
						i++;
						c = bufferInput.charAt(i);
					}
					
					i = skipSpaces(bufferInput, i);
					
					// find pointer of symbol
					while( i < (bufferInput.length()-1) && (isNumber(c) ))
					{
						pointer += Character.toString(c);
						i++;
						c = bufferInput.charAt(i);
					}
					
					//find symbol index
					int s = getSymbolIndex(symbol);
					
					/*
					 *  if defined add symbol to SymbolsToUse list
					 *  else error message and use value zero
					 */
					if (isSymbolDefined(symbol))
					{
						
						addSymbolWithPointer(symbol, Integer.parseInt(pointer), 
							Symbols.get(s).getAddress());
					}
					else
					{	
						addSymbolWithPointer(symbol, Integer.parseInt(pointer), 0);
						
						SymbolsToUse.getLast().addToError( "\tError: " + symbol 
								+ " is not defined; zero used.");
					}
								
					symbol = ""; // clear symbol variable	
					pointer = ""; // clear pointer variable
					i = skipSpaces(bufferInput, i);
				}
			}
			
			i = skipTilProgramText(bufferInput, i);
			i = skipTilInstruction(bufferInput, i);
			
			// find all instructions and add to Instructions list
			for(int z = 0; z < numOfInstructions; z++)	
			{
				i = findInstruction(bufferInput, i, z);
			}	
			
			// process all instructions
			processInstructions();
			
			modNum++; // increase current module number
			
			/*
			 * find symbols in use list that are unused
			 * if any and error
			 */
			getUseListUnusedSymbols();
						
			i--; //adjust i for loop updating
						
		} // end of for loop for second pass 
		
		// add error generated in getUseListUnusedSymbols()
		bufferOutput.append(usageWarning);
		/*
		 * find symbols defined never used
		 * if any add error to output and print
		 */
		getDefinedUnusedSymbols();
		System.out.println(bufferOutput);
		
	} // end of method main
	
	public static int processSymbols(StringBuffer bufferInput, int i)
	{
		int absoluteOfSymbol = 0; 		// absolute address of symbol
		String addressOfSymbol = ""; 	// relative address of symbol
		String symbol = null; 			// text representation of symbol
		
		i = skipSpaces(bufferInput, i);

		if(isSymbol(c))
		{	
			// find the symbol
			symbol = Character.toString(c);
			i++;
			c = bufferInput.charAt(i);
			
			// add to symbol string if symbol > one char long
			while( i < (bufferInput.length()-1) && (isSymbol(c) || isNumber(c) ))
			{
				symbol += Character.toString(c);
				i++;
				c = bufferInput.charAt(i);
			}
			
			i = skipSpaces(bufferInput, i);
		
			// error if symbol has already been previously defined
			if ( isSymbolDefined(symbol) )
			{
				int symbolIndex = getSymbolIndex(symbol);			
				Symbols.get(symbolIndex).setError("Error: Symbol " + symbol + 
						" has been multiply defined; first value used.\n");
			}
			
			else 
			{
				// find address of symbol
				if ( isNumber(c) ) // address is number
				{
					addressOfSymbol = Character.toString(c);				
					absoluteOfSymbol = Integer.parseInt(addressOfSymbol) + base;
				}
				
				// create the symbol
				createSymbol(symbol, absoluteOfSymbol);
				
				// update base for symbol
				Symbols.getLast().setBase(base);
				// update relative address for symbol
				Symbols.getLast().setRelAddress(Integer.parseInt(addressOfSymbol));
				nextIndex++; // update index for next symbol to be inserted into list
			} 
			
			// update variables for processing of next defined symbol
			symbol = null;
			addressOfSymbol = null;
			absoluteOfSymbol = 0;
			numOfDeclared--;
		}
		
		c = bufferInput.charAt(i);
		return i;
	}
	
	public static void updateSymbols()
	{
		for (int x = 0; x < Symbols.size(); x++)
		{
			/* 
			 * obtain relative address and base 
			 * of module when symbol was created
			 */
			int relAddress = Symbols.get(x).getRelAddress();
			int symbolBase = Symbols.get(x).getBase();
			
			/* 
			 * if symbol was defined in current module
			 * & if relative address outside of module 
			 * error and change relative address to zero
			 */
			if ( (symbolBase == Modules.get(modNum-1).getBaseAddress()) 
					&& (relAddress >= Modules.get(modNum-1).getLength()) )
			{
				Symbols.get(x).setAddress(symbolBase);
				Symbols.get(x).setRelAddress(relAddress);
				Symbols.get(x).setError("Error:  The value of " 
						+ Symbols.get(x).getSymbol() + " is outside of "
						+ "module " + (modNum) + "; zero (relative) is used");
			}
		} // end for
	} // end updateSymbols
	
	public static int processModules(StringBuffer bufferInput, int i)
	{
		createModule();
		base += numOfInstructions; // update base for next potential module

		i = skipTilDefinition(bufferInput, i);
		return i;
	}
	
	public static void createSymbol(String symbol, int absoluteOfSymbol)
	{	
		Symbols.add( new Symbol(symbol, nextIndex, absoluteOfSymbol) );
	}
	
	public static void createSymbolTable()
	{
		for(int x = 0; x < Symbols.size(); x++)
		{
			bufferOutput.append(Symbols.get(x).getSymbol() + " = " 
					+ Symbols.get(x).getAddress() 
					+ "\t\t" + Symbols.get(x).getError()
					+ "\n");
			
			Symbols.get(x).setError(""); // clear the error message
		}
	}
	
	public static void addSymbolWithPointer(String symbol, int pointer, int absoluteOfSymbol)
	{
		SymbolsToUse.add( new Symbol(symbol, pointer, absoluteOfSymbol) );
	}
	
	
	public static void createModule()
	{
		/* 
		 * insert modules into linked list
		 * where numOfInstructions is the length of current module
		 */
		if (modNum == 1) //first module... base address is zero
		{
			Modules.add( new Module(modNum,numOfInstructions) );
		
		}
		else
		{
			Modules.add( new Module(modNum,numOfInstructions, base) );
		}
		
		/*
		 * check for symbols out of module bounds 
		 * create error message and correct error
		 */
		updateSymbols(); 
		
		modNum++; // update number of module for next potential module
	}
	
	public static void printSymbols()
	{
		for (int x = 0; x < nextIndex; x++)
		{
			System.out.println( (Symbols.get(x)).toString() );
		}		
		System.out.println("\n");
	}
	
	public static void printModules()
	{
		for (int x = 0; x < modNum-1; x++)
		{
			System.out.println( (Modules.get(x)).toString() );
		}
		System.out.println("\n");
	}
	
	public static boolean isSpace(char ch)
	{
		return (ch ==' ');
	}
	
	public static boolean isNewLine(char ch)
	{
		return (ch == '\n');
	}
	
	public static boolean isNumber(char ch)
	{
		return ((ch >= '0') && (ch <= '9'));
	}
	
	public static boolean isInstruction(char ch)
	{
		return ( (ch == 'I') || (ch == 'A') || (ch == 'R') || (ch == 'E') );
	}
	
	public static boolean isSymbol(char ch)
	{
		return ( !isSpace(ch) && !isNewLine(ch) && !isNumber(ch) && !isInstruction(ch) );
	}
	
	public static int skipSpaces(StringBuffer bufferInput, int i)
	{
		while( (i<bufferInput.length()-3) && (isSpace(bufferInput.charAt(i+1)) 
				|| isNewLine(bufferInput.charAt(i+1))) )
		{
			i++; // skip over spaces and new lines
		}
		
		i++;
		c = bufferInput.charAt(i);
		return i;
	}
	
	public static int skipTilSpace(StringBuffer bufferInput, int i)
	{
		while( !isSpace(bufferInput.charAt(i+1)) && (i<bufferInput.length()-2))
		{
			i++; // skip over non spaces
		}
		
		i++;
		c = bufferInput.charAt(i);
		return i;
	}
	
	public static int skipTilInstruction(StringBuffer bufferInput, int i)
	{
		while( !isInstruction(bufferInput.charAt(i+1)) && (i<bufferInput.length()-2))
		{
			i++; // skip over non instructions
		}
		
		i++;
		c = bufferInput.charAt(i);
		return i;

	}
	
	public static int skipTilNumber(StringBuffer bufferInput, int i)
	{
		while(!isNumber(c) && (i<bufferInput.length()-2))
		{
			i++;
			c = bufferInput.charAt(i);
		}
		return i;
	}
		
	public static int skipTilDefinition(StringBuffer bufferInput, int i)
	{
		// skip over all instructions
		for(int skip = numOfInstructions; skip>0; skip--)
		{	
			i = skipTilInstruction(bufferInput, i);
		}	
		
		// skip until number of symbols declared is reached
		i = skipSpaces(bufferInput, i); // now at address for last instruction
		
		// first number of definition list is 4 numbers away
		for(int skip = 4; skip>0; skip--)
		{
			i++;
			while (!isNumber(bufferInput.charAt(i)) && (i<bufferInput.length()-1))
			{
				i++;
			}
		}
		if( isSpace(bufferInput.charAt(i)) || isNewLine(bufferInput.charAt(i)) && i<bufferInput.length()-2)
		{
			i = skipSpaces(bufferInput, i);
		}
		definitionList = true;
		
		if(i < bufferInput.length()-2)
		{	
			c = bufferInput.charAt(i); // number of symbols declared
			numOfDeclared = Character.getNumericValue(c);
		}
		else
		{
			numOfDeclared = -1;
		}
		
		return i;
	}
	
	
	public static int skipTilProgramText(StringBuffer bufferInput, int i)
	{
		// find first instruction
		while( i < (bufferInput.length()-1) && !isInstruction(bufferInput.charAt(i)) )
		{
			i++;
		}
		
		// backtrack to get the length of the module
		i--;
		c = bufferInput.charAt(i);
		
		while ( (i > 0) && !isNumber(c) )
		{
			i--;
			c = bufferInput.charAt(i);
		}
		
		// must go back more if length is more than one digit
		while (i>0 && isNumber(bufferInput.charAt(i-1)) )
		{
			i--;
		}
		
		String str = "";
		c = bufferInput.charAt(i);
		
		// find the length of the module
		while(i<bufferInput.length()-1 && isNumber(c))
		{
			str += c;
			i++;
			c = bufferInput.charAt(i);
		}
		
		numOfInstructions = Integer.parseInt(str);	
		return i;
	}
	
	public static int skipTilUseList(StringBuffer bufferInput, int i)
	{
		int skip; // number of symbols to skip over
		c = bufferInput.charAt(i);
		while(!isNumber(c))
		{
			i++;
			c = bufferInput.charAt(i);
		} 
		
		if(isNumber(c) && definitionList)
		{
			String str = "";
			
			// if number is longer than two digits
			while(i<bufferInput.length()-1 && isNumber(c))
			{
				str += c;
				i++;
				c = bufferInput.charAt(i);
			}
			
			skip = Integer.parseInt(str);
			
			if (skip == 0) // no symbols declared
			{
				// move to next char
				i++;
				c = bufferInput.charAt(i);
				
				// skip spaces and new lines until start of use list reached
				if( isSpace(c) || isNewLine(c) )
				{
					i = skipSpaces(bufferInput, i);
				}
			}
			else
			{	
				while (skip > 0) // skip over all symbol declarations
				{	
					i = skipSpaces(bufferInput, i);
					
					if (isSymbol(c)) // skip over symbol
					{
						i = skipTilSpace(bufferInput, i);
						i = skipSpaces(bufferInput, i);
					}
					
					if (isNumber(c)) // skip over symbol value
					{
						i++;
						c = bufferInput.charAt(i);
					}
					skip--;
				}
				
				i = skipSpaces(bufferInput, i); // start of use list
			}
			useList = true;
		}
		
		return i;
	}
	
	public static int getSymbolIndex(String text)
	{
		int index = -1; // default if symbol is not found
		
		for (int x = 0; x < nextIndex; x++)
		{
			if ( Symbols.get(x).getSymbol().equals(text) )
			{	
				index = x;
			}
		}
		return index;
	}
	
	public static boolean isSymbolDefined(String text)
	{
		boolean isDefined = false;
		
		for (int x = 0; x < nextIndex; x++)
		{
			if ( Symbols.get(x).getSymbol().equals(text) )
			{	
				isDefined = true;
			}
		}
		
		return isDefined;	
	}
	
	public static void getDefinedUnusedSymbols()
	{
		for (int x = 0; x < nextIndex; x++)
		{
			// find unused symbol
			if ( !Symbols.get(x).isUsed() )
			{	
				int module = -1;
				
				// find module symbol where symbol was defined
				for(int y = 0; y < Modules.size(); y++)
				{
					if(Symbols.get(x).getBase() 
							== Modules.get(y).getBaseAddress())
					{
						module = y+1;
					}
					
				}
				
				// add warning
				bufferOutput.append("\nWarning: " + Symbols.get(x).getSymbol() 
						+ " was defined in module " + module
						+ " but never used.\n");
			}
		}
	}
	
	public static void getUseListUnusedSymbols()
	{
		for(int x = 0; x < SymbolsToUse.size(); x++)
		{
			if ( !(SymbolsToUse.get(x).isUsed()) )
			{
				usageWarning += "\nWarning: In module " + modNum + " " + 
				SymbolsToUse.get(x).getSymbol() + " appeared in the use "+
				"list but was not actually used.\n";
			}
		}
	}
	
	public static int findInstruction(StringBuffer bufferInput, int i, int z)
	{
		
		// find the type : I,A,R or E
		char type = bufferInput.charAt(i);
		i = skipSpaces(bufferInput, i);
		
		while(!isNumber(c))
		{
			i++;
			c = bufferInput.charAt(i);
		}
		
		// word - 4 digit instruction as string
		String word = "";
		
		// find the word
		while( i < (bufferInput.length()-1) && isNumber(c) )
		{
			word += Character.toString(c);
			i++;
			c = bufferInput.charAt(i);
		}
		
		// the index of instruction in the list
		int listIndex = z;
		
		// add instruction to list
		Instructions.add(new Instruction(type, word, listIndex, mapIndex));
		
		mapIndex++; // increment the index number of mapping
		
		// skip over empty spaces
		if (i < bufferInput.length()-1)
		{
			i = skipSpaces(bufferInput, i);
		}
		return i;
	}

	public static void processInstructions()
	{
		int theBase = Modules.get(modNum).getBaseAddress(); // base of module
		int theLength = Modules.get(modNum).getLength(); 
		
		/* instruction E
		 * loop through SymbolsToUse list to find
		 * locations at which to resolve external references
		 */
		for (int x = 0; x < SymbolsToUse.size(); x++)
		{
			// find relative location where symbol will be used
			int loc = SymbolsToUse.get(x).getIndex();
			
			// check for sentinel
			while ( ((SymbolsToUse.get(x).getIndex()) != 777)
					&& (loc < Instructions.size())) 
			{	
				// make sure instruction was not used yet
				if (!Instructions.get(loc).isUsed())
				{
					/*
					 *  if not E type 
					 *  error and treat as E
					 */
					if ( (Instructions.get(loc).getType()) != 'E' )
					{
						Instructions.get(loc).setError("Error: "
								+ Instructions.get(loc).getType() 
								+ " type address on use chain; "
								+ "treated as E type.");
					}	
						// find word
						String oldWord = Instructions.get(loc).getWord();
						
						// find address to update old word
						String subWord = String.valueOf(SymbolsToUse.get(x).getAddress());
						
						// char arrays to use for updating
						char [] main = oldWord.toCharArray();
						char [] sub = subWord.toCharArray();
						
						// remove opt code to obtain address
						String  addressAsString = String.copyValueOf(main, 1, 3);
						int addressAsInt = Integer.parseInt(addressAsString); 
											
						// replace address with zeros
						for( int m = 1; m < main.length; m++ )
						{
							main[m] = '0';
						}
						
						// replace end of address with corresponding symbol address
						for(int m = main.length-1, s = sub.length-1; 
								m >= main.length - sub.length; m--, s--)
						{
							main[m] = sub[s];
						}
										
						// replace old word for instruction with new word
						Instructions.get(loc).setWord(String.valueOf(main));	
						Instructions.get(loc).setToUsed(); // set to used
						
						// add any symbol errors
						if(SymbolsToUse.get(x).hasError())
						{
							Instructions.get(loc).addToError(SymbolsToUse.get(x).getError());
						}
						
						/* 
						 * updating
						 * replace the symbol's pointer to a new pointer
						 * pointer of 777 is sentinel ending list
						 */
						SymbolsToUse.get(x).setIndex(addressAsInt);
						
						/*
						 * If pointer in use chain is larger than module size
						 * treat as sentinel and add error
						 */
						 if ( (addressAsInt > Instructions.size())
								 && (addressAsInt != 777))
						 {
							 Instructions.get(loc).setError("Error: Pointer in use chain "
									 + "exceeds module size; chain terminated.");
						 }
						
						// if not used previously set symbol to used
						if ( !(SymbolsToUse.get(x).isUsed()) )
						{
							SymbolsToUse.get(x).setUsed();
							
							// find index for symbol in Symbols list							
							int symbolIndex = getSymbolIndex(SymbolsToUse.get(x).getSymbol());
							
							// update Symbols list
							if(symbolIndex != -1)
							{
								Symbols.get(symbolIndex).setUsed();
							}							
						}
						
						// change relative location
						loc = addressAsInt;
					
					//} //end if
				} // end if
			} //end while
		} // end for
		
		/*
		 * Instructions I,A,and R
		 * Loop through Instructions list
		 */
		
		for (int x = 0; x < Instructions.size(); x++)
		{
			char type = Instructions.get(x).getType();
			
			// find word
			String oldWord = Instructions.get(x).getWord();
			int oldWordAsInt = Instructions.get(x).getWordAsInt();
			
			// char array to use for updating
			char [] main = oldWord.toCharArray();
			
			// remove opt code to obtain address
			String  addressAsString = String.copyValueOf(main, 1, 3);
			int addressAsInt = Integer.parseInt(addressAsString); 
			
			/*
			 * I and A nothing to be done
			 * simply change instruction to used
			 */
			switch(type)
			{
			case'I':
				Instructions.get(x).setToUsed();
				break;
			case'A':
				Instructions.get(x).setToUsed();
				break;
			case'R':
				/*
				 * R - relocate relative address
				 */
				oldWordAsInt += theBase; // add base address
				// update word in Instructions list
				Instructions.get(x).setWord(String.valueOf(oldWordAsInt));			
				break;
			case'E':
				
				//if not on use chain set the error
				if( !(Instructions.get(x).isUsed()) )
				{
					Instructions.get(x).setError("Error: E type address "
							+ "not on use chain; treated as I type.");
				}
				break;
			}
		} //end of for
		
		// append changes to bufferOutput
		appendMapToBuffer();
		
		// clear lists for next module
		Instructions.clear();
		SymbolsToUse.clear();
		
	} // end of method processInstructions()
	
	public static void appendMapToBuffer()
	{
		
		for(int x = 0; x < Instructions.size(); x++)
		{
			bufferOutput.append(Instructions.get(x).getMapIndex() + ":	");
			bufferOutput.append(Instructions.get(x).getWord());
			
			if(Instructions.get(x).hasError())
			{
				bufferOutput.append("\t" + Instructions.get(x).getError());
			}
			
			bufferOutput.append("\n");
		} // end for
		
	} // end appendMapToBuffer
	
} //end of class linker
