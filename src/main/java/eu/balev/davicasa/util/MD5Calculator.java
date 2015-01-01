package eu.balev.davicasa.util;

import java.io.File;
import java.io.IOException;

/**
 * Describes a utility for calculating MD5 hash sums.
 */
public interface MD5Calculator
{
	/**
	 * Returns the MD5 sum for the provided file.
	 * 
	 * @param file
	 *            the file whose MD5 sum should be calculated
	 * 
	 * @return a sting with the MD5 hex
	 * 
	 * @throws IOException
	 *             if and I/O error occurs while reading the file.
	 * @throws NullPointerException
	 *             if the file is null.
	 */
	public String getMD5Sum(File file) throws IOException;
}
