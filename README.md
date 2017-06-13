[![Build status](https://api.travis-ci.org/luchob/davicasa.svg?branch=master)](https://travis-ci.org/luchob/davicasa)
[![Language](http://img.shields.io/badge/language-java-brightgreen.svg)](https://www.java.com/)
[![Code Coverage](https://img.shields.io/codecov/c/github/luchob/davicasa/master.svg)](https://codecov.io/github/luchob/davicasa?branch=master)

# davicasa

Davicasa is a small utility that we use to sort and maintain the photos of our kids. The tool has two modes of operation:

- given a source directory it may detect and delete identical images based on binary comparison;
- given a source and a target directory the tool is able to copy and rename the source images to the target directory by using the timestamp left by the camera in the EXIF data;

The tool supports a "dry-run" option - it will try to output all the operations that would be normally performed without changing anything in the FS. 

# Running

There is no binary distribution. However the tool can be easily built with gradle (gradle wrapper is included in the source distribution so there is nothing to install additionally).

Download or clone the sources. From the main project directory run:

<pre>gradlew installApp</pre>

If this is successful (assuming standard directory structure) in the newly created folder <pre>./build/install/davicasa/bin</pre> there should be a shell script that is able to run the application. 
Take these files and put them in a directory of your choice.

From this folder run:

- for example, under Windows:

<pre>davicasa -help</pre>

This will print a detailed usage message.

# Exploring

The tool can be easily imported and explored in Eclipse. In the source directory run:

<pre>gradlew eclipse</pre>

You may easily import the tool in Eclipse as an existing project then.
