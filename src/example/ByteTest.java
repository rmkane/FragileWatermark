package example;

import java.util.Arrays;

import util.CommonUtil;

/**
 * This class is just a test environment for bit-level manipulation.
 *
 * @author Ryan M. Kane
 */
public class ByteTest {
	public static void main(String[] args) {
		int[] data = { 100, 200, 300, 400 };

		byte[] array = CommonUtil.integersToBytes(data);

		for (int i = 0; i < array.length; i++) {
			System.out.println(i + ": " + array[i]);
		}

		System.out.println();

		byte[] hashed = CommonUtil.hashMD5(array);

		for (int i = 0; i < hashed.length; i++) {
			System.out.println(i + ": " + hashed[i]);
		}

		System.out.println();

		byte[] arr = { 4, 3, 2, 5 };

		for (byte b : arr) {
			System.out.println(CommonUtil.toBin(b));
		}

		System.out.println();

		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			buff.append(CommonUtil.toBin(arr[i]).substring(0, 7));
		}

		System.out.println(Arrays.toString(buff.toString().split("(?<=\\G.......)")));

		System.out.println(Arrays.toString(buff.toString().split("(?<=\\G........)")));
	}
}
