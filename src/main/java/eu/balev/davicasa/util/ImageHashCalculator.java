package eu.balev.davicasa.util;

import java.io.File;
import java.io.IOException;

/**
 * Describes a utility for calculating hash sums for a file. 
 * The specific algorithm depends on the implementation.
 */
public interface ImageHashCalculator
{
	/**
	 * Returns the hash sum for the provided file.
	 * 
	 * @param file
	 *            the file whose hash sum should be calculated
	 * 
	 * @return a sting with the hash sum
	 * 
	 * @throws IOException
	 *             if and I/O error occurs while reading the file.
	 * @throws NullPointerException
	 *             if the file is null.
	 */
	public String getHashSum(File file) throws IOException;
}
