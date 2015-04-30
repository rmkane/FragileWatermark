package watermark.core.datatypes.buffer;

/**
 * The class represents a bit-buffer.
 *
 * @author Ryan M. Kane
 */
public class BitBuffer {
	public static final int BITS_PER_BYTE = 8;

	private byte[] buffer;
	private int bitsPerByte;
	private int currentIndex;

	public BitBuffer(int size, int bitsPerByte) {
		this.buffer = new byte[size];
		this.bitsPerByte = bitsPerByte;
		this.currentIndex = 0;
	}

	public BitBuffer(int size) {
		this(size, BITS_PER_BYTE);
	}

	public void push(byte bit) {
		final int bytePos = currentIndex / this.bitsPerByte;
		buffer[bytePos] <<= 1; // Make space for the new bit to arrive.
		buffer[bytePos] |= bit & 1; // Copy the bit into the byte's position.
		currentIndex++;
	}

	public void resize(int newSize) {
		byte[] newArr = new byte[newSize];
		System.arraycopy(this.buffer, 0, newArr, 0, newSize);
		this.buffer = newArr;
	}

	public void flush() {
		this.currentIndex = 0;

		for (int i = 0; i < this.size(); i++) {
			buffer[i] = 0;
		}
	}

	public int size() {
		return buffer.length;
	}

	public byte[] getBytes() {
		return buffer;
	}
}