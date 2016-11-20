package hash;

/*
 * Interface for hash functions that will be used in the
 * Bloom Filter. Since the bloom filter only accepts unsigned integers
 * for the bit positions, and most hashes return a larger than
 * 32-bit value, we will break the hash code bytes into chunks and
 * return each one as a unsigned integer. It has a utility method
 * to inform how many integers are returned
 */
public interface BloomHashFunction {
	public int returnedHashSize();
	public int[] hash(String value);
}
