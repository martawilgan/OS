// Resource class
//
// ****************************PUBLIC OPERATIONS*******************************
// int getType()				->	return Resource type
// int getTotalUnits()			->	return total number of units of Resource
// int getAvailableUnits()		->  return number of units available of Resource
// void setAvailableUnits(x)	->  set value of availableUnits to x
// String toString()			->  return Resource information
// *****************************************************************************

public class Resource {

	Resource (int theType, int theUnits)
	{
		type = theType;
		totalUnits = theUnits;
		availableUnits = theUnits;
	}
	
	public int getType()
	{
		return type;
	}
	
	public int getTotalUnits()
	{
		return totalUnits;
	}
	
	public int getAvailableUnits()
	{
		return availableUnits;
	}
	
	public void setAvailableUnits(int theUnits)
	{
		if (theUnits <= totalUnits && theUnits != availableUnits)
		{	
			availableUnits = theUnits;
		}
	}
	
	public String toString()
	{
		return "Resource Type: " + type + "\tTotal Units: " 
		+ totalUnits + "\tUnits Available: " + availableUnits;
	}

	int type;
	int totalUnits;
	int availableUnits;

}

