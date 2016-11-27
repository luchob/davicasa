package eu.balev.davicasa.processors;

import java.io.IOException;

/**
 * Describes a class that is able to process some images based on an initial
 * configuration.
 */
@FunctionalInterface
public interface ImageProcessor
{
	/**
	 * Starts the processing of the images.
	 * 
	 * @throws IOException
	 *             if an I/O exception occurs during processing.
	 */
	public void process() throws IOException;

}
