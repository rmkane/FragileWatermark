package example;

import java.awt.image.BufferedImage;

import util.ImageUtil;

/**
 * Partitions an image into blocks, recombines them, compares the output image
 * to the original for equality, and saves the output image to a file.
 *
 * @author Ryan Kane
 *
 */
public class ImageToBlocksToImage {
	public static void main(String[] args) {
		BufferedImage img = ImageUtil.loadImage("duke_stickers.png");
		BufferedImage[][] blocks = ImageUtil.partitionImage(img, 8);
		BufferedImage out = ImageUtil.recombine(blocks);

		System.out.println("Images equal?: " + ImageUtil.compareImages(img, out));
		ImageUtil.writeImage(out, "export", "irregularImage.png");
	}
}
