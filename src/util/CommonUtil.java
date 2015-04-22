package util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonUtil {
	public static int setLSB(int value, int bit) {
		return (value & 0xFFFFFFFE) | bit;
	}

	public static String byteToHex(byte b) {
		return Integer.toString((b & 0xff) + 0x100, 16).substring(1);
	}

	public static String toBin(int i) {
		return toBin(32, Integer.toBinaryString(i));
	}

	public static String toBin(byte b) {
		return toBin(8, Integer.toBinaryString(b & 0xff));
	}

	private static String toBin(int size, String bitString) {
		return String.format(String.format("%%%ds", size), bitString).replace(' ', '0');
	}

	public static String hexDump(byte[] bytes, boolean addSpace) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(byteToHex(bytes[i])); // sb.append(String.format("%02x", b));

			if (addSpace && i < bytes.length - 1) {
				sb.append(' ');
			}
		}
		return sb.toString();
	}

	public static String byteArrayToString(byte[] bytes) {
		return new String(bytes);
	}

	public static String getPixelARGB(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		return String.format("argb(%3d, %3d, %3d, %3d)", alpha, red, green, blue);
	}

	public static String getPixelBinaryARGB(int pixel) {
		Color argb = new Color(pixel, true);
		byte a = (byte) argb.getAlpha();
		byte r = (byte) argb.getRed();
		byte g = (byte) argb.getGreen();
		byte b = (byte) argb.getBlue();

		return String.format("b[%s, %s, %s, %s]", CommonUtil.toBin(a),
				CommonUtil.toBin(r), CommonUtil.toBin(g), CommonUtil.toBin(b));
	}

	public static byte[] integersToBytes(int[] data) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(data);
		return byteBuffer.array();
	}

	public static byte[] integersToBytesSlow(int[] values) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			for (int i = 0; i < values.length; ++i) {
				dos.writeInt(values[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return baos.toByteArray();
	}

	public static String hashStrMD5(String input) {
		return CommonUtil.hexDump(hashMD5(input),  false);
	}

	public static byte[] hashMD5(byte[] bytes) {
		try {
			return MessageDigest.getInstance("MD5").digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] hashMD5(String input) {
		try {
			return hashMD5(input.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] xor(byte[] a, byte[] b) {
		int length = Math.max(a.length, b.length);
		byte[] result = new byte[length];

		int i = 0;
		for (byte val : a) {
			result[i] = (byte) (val ^ b[i++]);
		}

		return result;
	}
}
