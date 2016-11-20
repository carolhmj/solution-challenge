package hash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD2Hash implements HashFunction {

	@Override
	public int hash(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD2");
			digest.update(value.getBytes());
			byte[] hashValue = digest.digest();
			int result = new BigInteger(hashValue).intValue();
			return result;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
		return 0;
	}

}
