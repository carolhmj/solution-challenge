package hash;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/*
 * A small utility class that can be used for various
 * Bloom Hashes to convert their hashes from byte arrays
 * to positive integers
 */
public class ByteArrayToPositiveInt {

	public static int[] convert(byte[] bytes) {
		
		/*
		 * Iterate over the byte array, each two bytes are read as an
		 * signed short and are converted into unsigned integers
		 */
		
		
		int signedInt;
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		IntBuffer shortBuffer = buffer.asIntBuffer();
		int[] result = new int[shortBuffer.remaining()];
		int index = 0;
		while (shortBuffer.remaining() > 0) {
			signedInt = shortBuffer.get();
			result[index] = Math.abs(signedInt);
			index++;
		}
		
		return result;
	}
}
