package bloomfilter;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Vector;

import org.junit.Test;

import java.util.List;
import java.util.Random;

import hash.DoubleHash;
import hash.HashFunction;
import hash.MD2Hash;
import hash.SimpleHash;

public class BloomFilterTest {

	@Test
	public void testBloomFilter() {
		Vector<HashFunction> hv = new Vector<>();

		SimpleHash simple = new SimpleHash();
		/*
		 * I'm doing the double hash wrongly, since the functions aren't
		 * independent, but that's just for creation
		 */

		hv.add(simple);
		hv.add(new DoubleHash(10, 20, simple, simple));
		hv.add(new DoubleHash(10, 20, simple, simple));
		hv.add(new DoubleHash(10, 20, simple, simple));
		hv.add(new DoubleHash(10, 20, simple, simple));
		hv.add(new DoubleHash(10, 20, simple, simple));
		hv.add(new DoubleHash(10, 20, simple, simple));
		hv.add(new DoubleHash(10, 20, simple, simple));
		hv.add(new DoubleHash(10, 20, simple, simple));

		BloomFilter testFilter = new BloomFilter(10, 128, hv);
		assertEquals(testFilter.expectedElements, 10);
		assertEquals(testFilter.numBits, 128);
		assertEquals(testFilter.bits.size(), testFilter.numBits);
		assertEquals(testFilter.numHashes, 9);
		double expected = Math.pow((Math.exp(45./ 64.) - 1.), 9.) / Math.exp(405. / 64.);
		assertEquals(expected, testFilter.errorRate, 10E-4);
	}

	@Test
	public void testAdd() {
		Vector<HashFunction> hv = new Vector<>();

		SimpleHash simple = new SimpleHash();
		MD2Hash md2 = new MD2Hash();

		//9 double hashes will be generated
		hv.add(new DoubleHash(31, 7, simple, md2));
		hv.add(new DoubleHash(11, 37, simple, md2));
		hv.add(new DoubleHash(5, 23, simple, md2));
		hv.add(new DoubleHash(79, 2, simple, md2));
		hv.add(new DoubleHash(3, 83, simple, md2));
		hv.add(new DoubleHash(29, 17, simple, md2));
		hv.add(new DoubleHash(41, 43, simple, md2));
		hv.add(new DoubleHash(3, 61, simple, md2));

		BloomFilter testFilter = new BloomFilter(10, 128, hv);
		
		//Elements to be added
		List<String> elements = Arrays.asList("google.com", "facebook.com", "reddit.com", 
											  "deezer.com", "wikipedia.org", "deezer.com",
											  "amazon.com", "gutenberg.org", "twitter.com",
											  "ufc.br");
		for (String el : elements) {
//			System.out.println("Inserting... " + el);
			testFilter.add(el);
		}
	}

	@Test
	public void testExists() {
		Vector<HashFunction> hv = new Vector<>();

		SimpleHash simple = new SimpleHash();
		MD2Hash md2 = new MD2Hash();

		//9 double hashes will be generated
		hv.add(new DoubleHash(31, 7, simple, md2));
		hv.add(new DoubleHash(11, 37, simple, md2));
		hv.add(new DoubleHash(5, 23, simple, md2));
		hv.add(new DoubleHash(79, 2, simple, md2));
		hv.add(new DoubleHash(3, 83, simple, md2));
		hv.add(new DoubleHash(29, 17, simple, md2));
		hv.add(new DoubleHash(41, 43, simple, md2));
		hv.add(new DoubleHash(3, 61, simple, md2));

		BloomFilter testFilter = new BloomFilter(10, 128, hv);
		
		//Elements to be added
		List<String> elements = Arrays.asList("google.com", "facebook.com", "reddit.com", 
											  "deezer.com", "wikipedia.org", "deezer.com",
											  "amazon.com", "gutenberg.org", "twitter.com",
											  "ufc.br");
		for (String el : elements) {
//			System.out.println("Inserting... " + el);
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
//			System.out.println("Testing... " + elnot);
			assertFalse(testFilter.exists(elnot));
		}
	
	}
	
	@Test
	public void testLargeScale() {
		/*
		 * We will test a large number of elements in this test. 
		 * The number of elements is 10^6, the number of bits
		 * is 2^23, the number of hashes is 6 and the expected 
		 * error rate is approximately 1.8%
		 */
		int numElements = 1000;
		int numBits = 8388608;
		
		Vector<HashFunction> hv = new Vector<>();

		SimpleHash simple = new SimpleHash();
		MD2Hash md2 = new MD2Hash();

		//6 double hashes will be generated
		hv.add(new DoubleHash(31, 7, simple, md2));
		hv.add(new DoubleHash(11, 37, simple, md2));
		hv.add(new DoubleHash(5, 23, simple, md2));
		hv.add(new DoubleHash(79, 2, simple, md2));
		hv.add(new DoubleHash(3, 83, simple, md2));
		hv.add(new DoubleHash(5, 19, simple, md2));
		
		BloomFilter testFilter = new BloomFilter(numElements, numBits, hv);
		assertEquals(testFilter.numBits, numBits);
		
		System.out.format("Expected error rate: %1$.10f\n", testFilter.errorRate);
		
		/*
		 * All the elements inserted will be strings starting with an "A".
		 * This will make easier to declare if a string is in the set or not.
		 */
		
		Random random = new Random(System.currentTimeMillis());
		
		for (int i = 0; i < numElements; i++) {
			String insert = generateString(6, "A", random);
			System.out.println(insert);
			testFilter.add(insert);
		}
		
		/*
		 * Now I'll generate strings starting with a "B", so they shouldn't
		 * be in the hash. We can have false positives, but their number should
		 * conform with the false positive rate.
		 */
		int numFalseElements = 100;
		int falsePositives = 0;
		
		for (int i = 0; i < numFalseElements; i++) {
			String test = generateString(6, "B", random);
			System.out.println(test);
			if (testFilter.exists(test)) {
				falsePositives++;
			}
		}
		
		System.out.println("false positives: " + falsePositives);
		double falsePositiveRate = ((float)falsePositives)/((float)numFalseElements);
		System.out.format("Found error rate: %1$.5f", falsePositiveRate);
		
		assertTrue(falsePositiveRate < testFilter.errorRate);
	}
	
	/*
	 * Helper method to generate strings
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
