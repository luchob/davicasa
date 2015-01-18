# davicasa

Davicasa is a small utility that we use to sort and maintain the photos of our kids. The tool has two modes of operation:

- given a source directory it may detect and delete identical images based on binary comparison;
- given a source and a target directory the tool is able to copy and rename the source images to the target directory by using the timestamp left by the camera in the EXIF data;

The tool supports a "dry-run" - it will try to output all the operations that would be normally performed without changing anything in the FS. 

#Building from source

There is no binary distibution currently. The tool can be built from source only. There are maven scripts provided. Download the source code and run:

<pre>mvn package</pre>

If the build was successful in the target directory there should be a jar file named like <code>davicasa-1.0-SNAPSHOT-jar-with-dependencies.jar</code>. As the name implies the file contains the class files of davicasa as well as the classes of all dependencies used by the tool. The distibution contains some help resources.

#Running

The tool can be executed with java. Example:

<pre> $java -cp davicasa-1.0-SNAPSHOT-jar-with-dependencies.jar eu.balev.davicasa.Davicasa --help</pre>

This will print verbose usage information.

#Todo

- provide binary distribution;
- an even more complete help;
- a more cooperative output when the user provide wrong input;
- some javadoc;


