package example;

import java.security.PrivateKey;
import java.security.PublicKey;

import util.CommonUtil;
import cipher.PublicKeyCipher;

/**
 * This class is an example of RSA encryption.
 *
 * @author Ryan M. Kane
 */
public class RSAExample {
	// Private key filename.
	public static final String PRIVATE_KEY_FILE = "C:/keys/private.key";
	// Public key filename.
	public static final String PUBLIC_KEY_FILE = "C:/keys/public.key";

	public static void main(String[] args) {
		// Create a new Public Key encryption system using RSA.
		PublicKeyCipher cipher = new PublicKeyCipher("RSA");

		try {
			// Check to see if keys already exist.
			if (!cipher.areKeysPresent(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE)) {
				// Generate new keys.
				cipher.generateKey(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);
				System.out.println("Keys generated...");
			}

			final String originalText = "Text to be encrypted.";

			// Encrypt the string using the public key
			final PublicKey publicKey = (PublicKey) cipher.getKey(PUBLIC_KEY_FILE);
			final byte[] cipherText = cipher.encryptStr(originalText, publicKey);

			// Decrypt the cipher text using the private key.
			final PrivateKey privateKey = (PrivateKey) cipher.getKey(PRIVATE_KEY_FILE);
			final String plainText = cipher.decryptStr(cipherText, privateKey);

			// Printing the Original, Encrypted and Decrypted Text
			System.out.println("Original: " + originalText);
			System.out.println("Encrypted: " + CommonUtil.byteArrayToString(cipherText));
			System.out.println("Decrypted: " + plainText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
