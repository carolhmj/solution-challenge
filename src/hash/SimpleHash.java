package hash;

/*
 * Simple hash that uses the hashCode method of the 
 * String class
 */
public class SimpleHash implements HashFunction {

	@Override
	public int hash(String value) {
		return value.hashCode();
	}

}
