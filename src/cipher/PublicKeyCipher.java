package cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/**
 * The class is a Public Key cryptosystem.
 *
 * @author Ryan M. Kane
 */
public class PublicKeyCipher implements KeyCipher {
	private String algorithm;

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public PublicKeyCipher(String algorithm) {
		this.setAlgorithm(algorithm);
	}

	/**
	 * Generate key which contains a pair of private and public key using 1024
	 * bytes. Store the set of keys in Prvate.key and Public.key files.
	 *
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Override
	public void generateKey(String publicKeyLocation, String privateKeyLocation) {
		try {
			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(getAlgorithm());
			keyGen.initialize(1024);

			final KeyPair key = keyGen.generateKeyPair();

			File privateKeyFile = new File(privateKeyLocation);
			File publicKeyFile = new File(publicKeyLocation);

			// Create files to store public and private key
			if (privateKeyFile.getParentFile() != null) {
				privateKeyFile.getParentFile().mkdirs();
			}
			privateKeyFile.createNewFile();

			if (publicKeyFile.getParentFile() != null) {
				publicKeyFile.getParentFile().mkdirs();
			}
			publicKeyFile.createNewFile();

			// Saving the Public key in a file
			ObjectOutputStream publicKeyOS = new ObjectOutputStream(
					new FileOutputStream(publicKeyFile));
			publicKeyOS.writeObject(key.getPublic());
			publicKeyOS.close();

			// Saving the Private key in a file
			ObjectOutputStream privateKeyOS = new ObjectOutputStream(
					new FileOutputStream(privateKeyFile));
			privateKeyOS.writeObject(key.getPrivate());
			privateKeyOS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * The method checks if the pair of public and private key has been
	 * generated.
	 *
	 * @return flag indicating if the pair of keys were generated.
	 */
	@Override
	public boolean areKeysPresent(String publicKeyLocation, String privateKeyLocation) {
		File privateKey = new File(privateKeyLocation);
		File publicKey = new File(publicKeyLocation);

		if (privateKey.exists() && publicKey.exists()) {
			return true;
		}

		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <K extends Key> K getKey(String fileName) {
		ObjectInputStream objInputStream = null;
		FileInputStream fileInputStream = null;

		try {
			fileInputStream = new FileInputStream(fileName);
			objInputStream = new ObjectInputStream(fileInputStream);

			return (K) objInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				objInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}


	/**
	 * Encrypt the plain text using public key.
	 *
	 * @param data - data to be encrypted.
	 * @param key - The public key.
	 * @return Encrypted text.
	 */
	@Override
	public byte[] encrypt(byte[] data, PublicKey key) {
		byte[] cipherText = null;
		try {
			final Cipher cipher = Cipher.getInstance(getAlgorithm());

			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cipherText;
	}

	/**
	 * Decrypt byte data using private key.
	 *
	 * @param data - the data to decrypt.
	 * @param key - the private key.
	 * @return decrypted byte data.
	 */
	@Override
	public byte[] decrypt(byte[] data, PrivateKey key) {
		try {
			final Cipher cipher = Cipher.getInstance(getAlgorithm());

			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (Exception e) {
			//e.printStackTrace();
		}

		return null;
	}

	/**
	 * Decrypt text using public key.
	 *
	 * @param text - encrypted text.
	 * @param key - The public key.
	 * @return plain text.
	 */
	public byte[] encryptStr(String text, PublicKey key) {
		return encrypt(text.getBytes(), key);
	}

	/**
	 * Decrypt text using private key.
	 *
	 * @param text - encrypted text.
	 * @param key - The private key.
	 * @return plain text.
	 */
	public String decryptStr(byte[] text, PrivateKey key) {
		return new String(decrypt(text, key));
	}
}