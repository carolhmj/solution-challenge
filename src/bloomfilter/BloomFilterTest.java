package bloomfilter;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Vector;

import org.junit.Test;

import java.util.List;
import java.util.Random;

import hash.BloomHashFunction;
import hash.Murmur32;
import hash.SipHash24;

public class BloomFilterTest {

	
	@Test
	public void testBloomFilter() {
		Vector<BloomHashFunction> hv = new Vector<>();

		Murmur32 murmur32 = new Murmur32(100);
		SipHash24 sip24 = new SipHash24(29, 47);
		
		hv.add(murmur32);
		hv.add(sip24);
		
		BloomFilter testFilter = new BloomFilter(10, 128, hv);
		assertEquals(testFilter.expectedElements, 10);
		assertEquals(testFilter.numBits, 128);
		assertEquals(testFilter.bits.size(), testFilter.numBits);
		assertEquals(testFilter.numHashes, 3);
		//Error rate formula for a bloom filter is: (1-e^(kn/m))^k
		double expected = Math.pow((Math.exp(15./ 64.) - 1.), 3.) / Math.exp(45. / 64.);
		assertEquals(expected, testFilter.errorRate, 10E-4);
	}


	@Test
	public void testExists() {
		Vector<BloomHashFunction> hv = new Vector<>();

		Random random = new Random(System.currentTimeMillis());
		Murmur32 murmur32 = new Murmur32(random.nextInt());
		SipHash24 sip24 = new SipHash24(random.nextLong(), random.nextLong());

		hv.add(murmur32);
		hv.add(sip24);

		BloomFilter testFilter = new BloomFilter(10, 128, hv);
		
		//Elements to be added
		List<String> elements = Arrays.asList("google.com", "facebook.com", "reddit.com", 
											  "deezer.com", "wikipedia.org", "deezer.com",
											  "amazon.com", "gutenberg.org", "twitter.com",
											  "ufc.br");
		for (String el : elements) {
			testFilter.add(el);
			assertTrue(testFilter.exists(el));
		}
		
		/*
		 * This test should fail! I'm testing a mix of values that are
		 * very different from the inserted values and values that are similar
		 * to the inserted values
		 */
		List<String> elementsNotExist = Arrays.asList("ebay.com", "soundcloud.com",
				 									  "spotify.com", "google.com/mail/",
				 									  "googles.com", "hoogle.com",
				 									  "giogle.com", "google.comn",
				 									  "foogle.com");
		for (String elnot : elementsNotExist) {
			assertFalse(testFilter.exists(elnot));
		}
	
	}
	
	@Test
	public void testLargeScale() {
		/*
		 * We will test a large number of elements. 
		 * The number of elements is 10^6, the number of bits
		 * is 2^23, the number of hashes is 3 and the expected 
		 * error rate is approximately 2.7%
		 */
		int numElements = 1000000;
		int numBits = 8388608;
		
		Vector<BloomHashFunction> hv = new Vector<>();

		Random random = new Random(System.currentTimeMillis());
		
		//3 hashes will be generated
		Murmur32 murmur32 = new Murmur32(random.nextInt());
		SipHash24 sip24 = new SipHash24(random.nextLong(), random.nextLong());

		hv.add(murmur32);
		hv.add(sip24);
		
		BloomFilter testFilter = new BloomFilter(numElements, numBits, hv);
		assertEquals(testFilter.numBits, numBits);
				
		/*
		 * The elements added can be of two types: the first one
		 * are 6-character strings starting with "A", the second
		 * one are 5-character strings starting with "B". Having
		 * these definite types will make to know if a string 
		 * isn't supposed to be in the set.
		 */
		
		for (int i = 0; i < numElements/2; i++) {
			String insert = generateString(6, "A", random);
			testFilter.add(insert);
		}
		
		for (int i = numElements/2; i < numElements; i++) {
			String insert = generateString(5, "B", random);
			testFilter.add(insert);
		}
		
		/*
		 * Now I'll generate strings starting with a "B" and
		 * with a length of 4 or 6, so they shouldn't
		 * be in the filter. We can have false positives, 
		 * but the error rate should conform with 
		 * the calculated false positive rate.
		 */
		int numFalseElements = 100000;
		int falsePositives = 0;
		
		for (int i = 0; i < numFalseElements/2; i++) {
			String test = generateString(6, "B", random);
			if (testFilter.exists(test)) {
				falsePositives++;
			}
		}
		
		for (int i = numFalseElements/2; i < numFalseElements; i++) {
			String test = generateString(4, "B", random);
			if (testFilter.exists(test)) {
				falsePositives++;
			}
		}
		
		double falsePositiveRate = ((float)falsePositives)/((float)numFalseElements);
		
		assertTrue(Math.abs(falsePositiveRate - testFilter.errorRate) < 10E2);
	}
	
	/*
	 * Helper method to generate upper-case strings
	 */
	public String generateString(int size, String startsWith, Random random) {
		
		String str = startsWith;
		int sumInt, charInt;
		for (int i = 0; i < size; i++) {
			/*
			 * ASCII codes for upper-case strings are 65-90
			 */
			sumInt = random.nextInt(26);
			charInt = 65 + sumInt;
			str += Character.toString((char) charInt);
		}
		
		return str;
		
	}

}
