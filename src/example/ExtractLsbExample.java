package example;

import java.util.Arrays;

import util.CommonUtil;

/**
 * This class is an example of pushing the least significant bit (LSB) of each
 * pixel onto a bit-buffer to create an array of bytes.
 *
 * @author Ryan M. Kane
 */
public class ExtractLsbExample {
	public static void main(String[] args) {
		int[] pixels = new int[] {
				0xFFFF0000, 0xFFFF0001, 0xFFFF0000, 0xFFFF0001, // 0101 = 5
				0xFFFF0000, 0xFFFF0001, 0xFFFF0001, 0xFFFF0001, // 0111 = 7
				0x00000000, 0x00000001, 0x00000000 // 010 = 2
		};

		// Result should be 3 bytes [0101 (5), 0111 (7), 10 (2)]
		System.out.println(Arrays.toString(CommonUtil.extractLSB(pixels, 4)));
	}
}
