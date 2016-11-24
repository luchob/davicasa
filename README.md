# davicasa

Davicasa is a small utility that we use to sort and maintain the photos of our kids. The tool has two modes of operation:

- given a source directory it may detect and delete identical images based on binary comparison;
- given a source and a target directory the tool is able to copy and rename the source images to the target directory by using the timestamp left by the camera in the EXIF data;

The tool supports a "dry-run" option - it will try to output all the operations that would be normally performed without changing anything in the FS. 

#Running

There is no binary distribution. However the tool can be easily built with gradle. Gradle should be correctly installed of course. 

Download or clone the sources. From the main project directory run:

<pre>gradlew installApp</pre>

If this is successful (assuming standard directory structure) in the newly created folder <pre>./build/install/davicasa/bin</pre> there should be a shell script that is able to run the application. Take these files and put them in a directory of your choice.

From this folder run:

- for example, under Windows:

<pre>davicasa -help</pre>
