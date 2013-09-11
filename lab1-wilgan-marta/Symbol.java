// Symbol class
//

// CONSTRUCTION : default constructor will set value of symbol to zero
// when value is unknown which can later be set by the setValue() method
//
// ************************PUBLIC OPERATIONS****************************
// String getSymbol()		->	return string representation 
// String getError()		-> 	return error as String
// int getBase()			-> 	return base at creation of symbol as int
// int getAddress()			-> 	return absolute address of symbol as int
// int getRelAdress()		->  return relative address of symbol as int
// int getIndex()			->  return index of symbol in symbol array
// boolean isUsed()			-> 	return true if used, false otherwise
// boolean hasError()		->  return true if error exists false otherwise
// void setError(x)			-> 	set error to x
// void addToError(x)		-> 	add x to error
// String toString()		->  return symbol information
// void setBase(x)			->  set the base to x
// void setUsed()			-> 	set usage to true
// void setAddress(x)		->	set value of symbol to x
// void setRelAddress(x) 	-> 	set relative address of symbol to x
// void setIndex(x)			->  set index within symbol array
// *********************************************************************

public class Symbol {
	
	Symbol(String text, int theIndex)
	{
		this(text, theIndex, 0);
	}
	
	Symbol(String text, int theIndex, int address)
	{
		symbolAsText = text;
		error = "";
		base = 0;
		symbolAddress = address;
		relAddress = 0;
		index = theIndex;
		used = false;
	}
	
	public String getSymbol()
	{
		return symbolAsText;
	}
	
	public String getError()
	{
		return error;
	}
	
	public int getBase()
	{
		return base;
	}
	
	public int getAddress()
	{
		return symbolAddress;
	}
	
	public int getRelAddress()
	{
		return relAddress;
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
	
	public int getIndex()
	{
		return index;
	}
	
	public void setBase(int theBase)
	{
		base = theBase;
	}
	
	public void setUsed()
	{
		used = true;
	}
	
	public void setAddress(int x)
	{
		symbolAddress = x;
	}
	
	public void setRelAddress(int x)
	{
		relAddress = x;
	}
	
	
	public void setIndex(int x)
	{
		index = x;
	}
	
	public void setError(String theError)
	{
		error = theError;
	}
	
	public void addToError(String theError)
	{
		error += theError;
	}
	
	public String toString()
	{
		return "Symbol: " + symbolAsText + "\tAddress: " + symbolAddress + "\tIndex: " + index + 
			"\t\tUsed? " + used;
	}
	
	String symbolAsText;
	String error;
	int symbolAddress;
	int index;
	int base;
	int relAddress;
	boolean used;
	
}
