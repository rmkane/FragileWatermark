package watermark.core.cipher;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * This interface describes a key cipher that will be injected.
 *
 * @author Ryan M. Kane
 */
public interface KeyCipher {
	<K extends Key> K getKey(String fileName);

	void generateKey(String publicKeyLocation, String privateKeyLocation);

	boolean areKeysPresent(String publicKeyLocation, String privateKeyLocation);

	byte[] encrypt(byte[] data, PublicKey key);

	byte[] decrypt(byte[] data, PrivateKey key);
}
