// Instruction class
//
//
// ************************PUBLIC OPERATIONS****************************
// void setWord (x)				->	set word x as String
// void setUsed()				-> 	set used to true
// String getError()			->  return error as String
// char getType()				->	return type as char
// String getWord()				->	return word as String
// int getWordAsInt()			->  return word as int
// int getListIndex()			-> 	return index within list as int	
// int getMapIndex()			-> 	return index in memory map as int
// void setError(x)				->  set error to x
// void addToError(x)			->  add x to error
// boolean isUsed()				->  return used as boolean
// boolean hasError()			->  return true if error exists false otherwise
// String toString()			->  return instruction information
// *********************************************************************


public class Instruction {
	
	Instruction (char theType, String theWord, int theListIndex, int theMapIndex)
	{	
		type = theType;
		word = theWord;
		listIndex = theListIndex;
		mapIndex = theMapIndex;
		used = false;
		error = "";
	}
	
	public void setWord(String theWord)
	{
		word = theWord;
	}
	
	public void setToUsed()
	{
		used = true;
	}

	public String getError()
	{
		return error;
	}
	
	public char getType()
	{
		return type;
	}
	
	public String getWord()
	{
		return word;
	}
	
	public int getWordAsInt()
	{
		return Integer.parseInt(word);
	}
	
	public int getListIndex()
	{
		return listIndex;
	}
	
	public int getMapIndex()
	{
		return mapIndex;
	}
	
	public void setError(String theError)
	{
		error = theError;
	}
	
	public void addToError(String theError)
	{
		error += theError;
	}
	
	public boolean isUsed()
	{
		return used;
	}
	
	public boolean hasError()
	{
		if (!error.equals(""))		
		{	
			return true;	
		}
		
		return false;
	}
	
	
	public String toString()
	{
		return "Type: " + type + "\t\tWord: " + word + "\tList Index: " +
				listIndex + "\tMap Index: " + mapIndex + "\tUsed?  " + used;
	}
	
	char type;
	String word;
	String error;
	int listIndex;
	int mapIndex;
	boolean used;
}
