package bloomfilter;

import java.util.BitSet;
import java.util.Vector;

import hash.BloomHashFunction;

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
	
	Vector<BloomHashFunction> hashVector;
	BitSet bits;
	
	/*
	 * Receives the number of expected elements, the number of bits and
	 * calculates the error rate.
	 */ 
	public BloomFilter(int expectedElements, int numBits, Vector<BloomHashFunction> hashVector) {
		this.expectedElements = expectedElements;
		
		int count = 0;
		for (BloomHashFunction bhf : hashVector) {
			count += bhf.returnedHashSize();
		}
		this.numHashes = count;
		
		this.numBits = numBits;
		this.errorRate = Math.pow((1 - Math.exp(-(double)numHashes * 
												(double) expectedElements / 
												(double) numBits)), 
								  (double) numHashes);
		bits = new BitSet(numBits);
		this.hashVector = hashVector;
	}
	
	public void add(String value) {
		
		for (BloomHashFunction hash : hashVector) {
			int[] hashValues = hash.hash(value);
			for (int i = 0; i < hashValues.length; i++) {
				bits.set(hashValues[i]);
			}	
		}
	}
	
	/*
	 * If some index computed by the hash isn't set to true, then
	 * the value can't exist in the filter. If all indices are set
	 * to true, then maybe the value exists, but it can be a false
	 * positive.
	 */
	
	public boolean exists(String value) {
	
		for (BloomHashFunction hash : hashVector) {
			int[] hashValues = hash.hash(value);
			for (int i = 0; i < hashValues.length; i++) {
				if (!bits.get(hashValues[i])) {
					return false;
				}
			}	
		}
		return true;
	}
}
