package hash;

import java.nio.charset.Charset;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/*
 * Bloom Hash implementation with the SipHash 2-4 algorithm. This
 * algorithm returns a 64-bit hash.
 */
public class SipHash24 implements BloomHashFunction {

	long k0, k1;
	
	public SipHash24(long k0, long k1) {
		this.k0 = k0;
		this.k1 = k1;
	}
	
	@Override
	public int returnedHashSize() {
		return 2;
	}

	@Override
	public int[] hash(String value) {
		HashFunction sip24 = Hashing.sipHash24(k0, k1);
		HashCode code = sip24.hashString(value, Charset.defaultCharset());
		byte[] hashBytes = code.asBytes();		
		int[] hashInts = ByteArrayToPositiveInt.convert(hashBytes);
		return hashInts;
	}

}
