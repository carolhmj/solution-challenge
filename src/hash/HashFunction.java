package hash;

/*
 * Interface for hash functions that will be used in the
 * Bloom Filter. The only method needed is a method that
 * receives a string and returns a integer value
 */
public interface HashFunction {
	public int hash(String value);
}
