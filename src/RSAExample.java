import java.security.PrivateKey;
import java.security.PublicKey;

import ciphers.PublicKeyCipher;
import util.CommonUtil;

public class RSAExample {
	public static final String PRIVATE_KEY_FILE = "C:/keys/private.key";
	public static final String PUBLIC_KEY_FILE = "C:/keys/public.key";

	public static void main(String[] args) {
		PublicKeyCipher cipher = new PublicKeyCipher("RSA");

		try {
			if (!cipher.areKeysPresent(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE)) {
				cipher.generateKey(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);
			}

			final String originalText = "Text to be encrypted.";

			// Encrypt the string using the public key
			final PublicKey publicKey = (PublicKey) cipher.getKey(PUBLIC_KEY_FILE);
			final byte[] cipherText = cipher.encrypt(originalText, publicKey);

			// Decrypt the cipher text using the private key.
			final PrivateKey privateKey = (PrivateKey) cipher.getKey(PRIVATE_KEY_FILE);
			final String plainText = cipher.decrypt(cipherText, privateKey);

			// Printing the Original, Encrypted and Decrypted Text
			System.out.println("Original: " + originalText);
			System.out.println("Encrypted: " + CommonUtil.byteArrayToString(cipherText));
			System.out.println("Decrypted: " + plainText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
