# Fragile Watermark Application

Copyright &copy; 2015, Ryan M. Kane. Licensed under MIT License.

## About

This application encodes and decodes watermarks from images using a fragile watermarking strategy. The source code is written in Java and deployed as a standalone jar file. The application's user interface is built using Java's Swing and AWT APIs.

Please read the licensing agreement in the LICENSES.txt file.

## Contributing

If you have forked this project and would like to improve any of the code, please feel free to submit a request to become a contributor.

## Downloading

There a already some executable jars already built on the [Releases][1] page. It is suggested that you place the jar in a directory that can be written to as the default key and configuration files will be saved in said directory.

## Documentation

The [project's Wiki][2] provides more information about the what the application is about and what it does.

## Source Code

The source code, along with an executable jar for each release, should be available on the [Releases][1] page.

## App Configuration File

By default, the application will read in a default configuration file. You can either open the jar file and modify the file there, but be cautious as this action is irreversible and may require you to re-download a new jar file; or you can use the built-in editor in the applications menu bar under the edit menu. This editor has safeguard built-in to validate if the properties are correct before proceeding to update the file.

If you open the jar file, using an archiving tool i.e. [*7-Zip*][3], and navigate to the resources directory, you can modify the application configuration file.

Using the `appconfig.properties` file, you can specify:
* Where your private/public keys exist in your file system.
* The image block size. The default it 32x32 pixels.

  [1]: https://github.com/ryankane/FragileWatermark/releases
  [2]: https://github.com/ryankane/FragileWatermark/wiki
  [3]: http://www.7-zip.org/
