package bloomfilter;

import java.util.BitSet;
import java.util.Vector;

import hash.HashFunction;

/*
 * Implementation of a Bloom Filter, which is a space-efficiente data structure for
 * fast set membership operations. It has the vector of bits used in the algorithm,
 * and a vector of hash functions that is applied to new values. This specific filter
 * will be applied to strings. It also has the number of expected elements in the filter,
 * this will be used to calculate the false positive rate.
 */
public class BloomFilter {
	int expectedElements;
	int numHashes;
	int numBits;
	double errorRate;
	
	Vector<HashFunction> hashVector;
	BitSet bits;
	
	/*
	 * Receives the number of expected elements, the number of bits and
	 * calculates the error rate.
	 */ 
	public BloomFilter(int expectedElements, int numBits, Vector<HashFunction> hashVector) {
		this.expectedElements = expectedElements;
		this.numHashes = hashVector.size();
		this.numBits = numBits;
		this.errorRate = Math.pow((1 - Math.exp(-(double)numHashes * 
												(double) expectedElements / 
												(double) numBits)), 
								  (double) numHashes);
		bits = new BitSet(numBits);
		this.hashVector = hashVector;
	}
	
	/*
	 * Since the hash functions return a 32-bit unsigned integer,
	 * and we need signed values, we first convert the value into a
	 * 64-bit unsigned long. But since we only have numBits bits, 
	 * we perform a mod operation to find the bucket.
	 */
	
	public void add(String value) {
		int hashResult, index;
		long uindex;
		
		for (HashFunction hash : hashVector) {
			hashResult = hash.hash(value);
			uindex = Integer.toUnsignedLong(hashResult);
			index = (int)(uindex % (long)numBits);
			
			bits.set(index);
		}
	}
	
	/*
	 * If some index computed by the hash isn't set to true, then
	 * the value can't exist in the filter. If all indices are set
	 * to true, then maybe the value exists, but it can be a false
	 * positive.
	 */
	
	public boolean exists(String value) {
		int hashResult, index;
		long uindex;
		
		for (HashFunction hash : hashVector) {
			hashResult = hash.hash(value);
			uindex = Integer.toUnsignedLong(hashResult);
			index = (int)(uindex % (long)numBits);
			
			if (!bits.get(index)) {
				return false;
			}
		}
		return true;
	}
}
