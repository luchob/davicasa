# davicasa

Davicasa is a small utility that we use to sort and maintain the photos of our kids. The tool has two modes of operation:

- given a source directory it may detect and delete identical images based on binary comparison;
- given a source and a target directory the tool is able to copy and rename the source images to the target directory by using the timestamp left by the camera in the EXIF data;

The tool supports a "dry-run" option - it will try to output all the operations that would be normally performed without changing anything in the FS. 

#Running

There is no binary distribution. However the tool can be easily built with maven. Maven should be correctly installed of course. 

Download the sources. From the main project directory run:

<pre>mvn package</pre>

If this is successful In the newly created <pre>./target/final</pre> there should be the final jar file and a helper batch file for Windows users. Take these files and put them in a directory of your choice.

From this folder:

- under Windows:

<pre>davicasa -help</pre>

- under Linux:

<pre> $java -cp davicasa.jar eu.balev.davicasa.Davicasa -help</pre>

This will print verbose usage information.