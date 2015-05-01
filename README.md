# Fragile Watermark

**Author:** Ryan M. Kane  
**Date:** 29 April 2015

## About

This application encodes and decodes watermarks from images using a fragile watermarking strategy. The source code is written in Java and deployed as a standalone jar file. The application's user interface is built using Java's Swing and AWT APIs.

## App Configuration File

If you open the jar file, using an archiving tool i.e. [*7-Zip*][1], and navigate to the resources directory, you can modify the application configuration file.

Using the `appconfig.properties` file, you can specify:
* Where your private/public keys exist in your file system.
* The image block size. The default it 32x32 pixels.

## Source Code

The source code, along with an excutable jar for each release, should be available on the [Releases][2] page.

## Documentaion

The [project's Wiki][3] provides more information about the application.

  [1]: http://www.7-zip.org/
  [2]: https://github.com/ryankane/FragileWatermark/releases
  [3]: https://github.com/ryankane/FragileWatermark/wiki