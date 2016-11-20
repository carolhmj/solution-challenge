package hash;

/*
 * A hash function that can be constructed from two other
 * independent hash functions and two arbitrary values
 */
public class DoubleHash implements HashFunction {

	HashFunction hash1, hash2;
	int i, m;
	
	public DoubleHash(int i, int m, HashFunction hash1, HashFunction hash2) {
		this.i = i;
		this.m = m;
		this.hash1 = hash1;
		this.hash2 = hash2;
	}
	
	@Override
	public int hash(String value) {
		return (hash1.hash(value) + i * hash2.hash(value)) % m;
	}

}
