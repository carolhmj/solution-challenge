package hash;

import java.nio.charset.Charset;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/*
 * HashFunction with the Murmur3 hash, 32 bits version
 */
public class Murmur32 implements BloomHashFunction{
	
	int seed;
	
	public Murmur32(int seed) {
		this.seed = seed;
	}
	
	@Override
	public int[] hash(String value) {
		HashFunction murmur32 = Hashing.murmur3_32(seed);
		HashCode code = murmur32.hashString(value, Charset.defaultCharset());
		byte[] hashBytes = code.asBytes();		
		int[] hashInts = ByteArrayToPositiveInt.convert(hashBytes);
		return hashInts;
	}

	@Override
	public int returnedHashSize() {
		return 1;
	}

}
