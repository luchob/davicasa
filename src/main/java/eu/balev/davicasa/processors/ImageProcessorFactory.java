package eu.balev.davicasa.processors;

import org.apache.commons.cli.CommandLine;

/**
 * A factory for creating image processors based on the provided command line
 * arguments.
 */
public interface ImageProcessorFactory
{

	/**
	 * Creates an image processor based on the command line arguments.
	 * 
	 * @param line
	 *            the command line
	 * 
	 * @return an image processor or <code>null</code> if such cannot be
	 *         created.
	 */
	public ImageProcessor tryCreateProcessor(CommandLine line);

}
