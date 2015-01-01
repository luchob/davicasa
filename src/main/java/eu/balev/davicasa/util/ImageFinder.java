package eu.balev.davicasa.util;

import java.io.File;
import java.util.List;

/**
 * Describes a utility for finding image files which are supported.
 * Implementations may do recursive browsing.
 */
public interface ImageFinder
{
	/**
	 * Lists images in the source directory. Implementations may do recursive
	 * browsing.
	 * 
	 * @param sourceDir
	 *            the source directory which should be browsed.
	 * 
	 * @return a list with images. Empty list should be returned if no images
	 *         are found.
	 * 
	 * @throws java.lang.NullPointerException
	 *             if the source dir is null.
	 */
	public List<File> listImages(File sourceDir);
}
