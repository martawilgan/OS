// Module class
//

// CONSTRUCTION : default constructor will set the base to zero which can
// be modified in the setBaseAddress method
//
// ************************PUBLIC OPERATIONS****************************
// void setBaseAddress (x,y)	->	set base to previous module's base plus length
// int getBaseAddress()			->	return base address as int
// int getLength()				->	return length as int
// String toString()			->  return module information
// *********************************************************************


public class Module {

	Module(int id, int theLength)
	{
		this(id, theLength, 0);
	}
	
	Module (int id, int theLength, int theBase)
	{
		identifier = id;
		size = theLength;
		baseAddress = theBase;
	}
	
	public void setBaseAddress(int previousBase, int previousLength)
	{
		baseAddress = previousBase + previousLength;
	}
	
	public int getBaseAddress()
	{
		return baseAddress;
	}
	
	public int getLength()
	{
		return size;
	}
	
	public String toString()
	{
		return "Module: " + identifier + "\tLength: " + size + "\tBase Address: " + baseAddress;
	}
	
	int identifier;
	int size;
	int baseAddress;
}
