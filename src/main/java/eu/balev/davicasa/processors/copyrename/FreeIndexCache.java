package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * A small utility which calculates the next free index for an image in a given
 * directory.
 */
class FreeIndexCache
{
	@Inject
	private FileNamingUtils fileNamingUtils;

	/**
	 * Maintains a cache for free indices. The key in the map is the directory
	 * where the image should go and the value is the next free index.
	 */
	private Map<File, Integer> indexCache = new HashMap<>();

	/**
	 * Gets the the next free index for this image. Before returning it the
	 * method updates the internal cache.
	 * 
	 * @param targetDir the target directory where the image will be stored stored.
	 * @param imageDate the date of the image capture
	 * @param ext the extension of the image.
	 * 
	 * @return the next free index
	 */
	public int getAndUpdateFreeIndex(File imageTargetDir, Date imageDate, String ext)
	{
		Integer freeIdx = indexCache.getOrDefault(imageTargetDir, 1);

		while (true)
		{
			String targetImageFileName = fileNamingUtils.getImageFileName(imageDate, freeIdx, ext);
			File targetImageFile = fileNamingUtils.getImageFile(imageTargetDir, targetImageFileName);
			if (!targetImageFile.exists())
			{
				break;
			}
			freeIdx++;
		}

		indexCache.put(imageTargetDir, freeIdx + 1);

		return freeIdx;
	}

	/**
	 * Clears the internal cache.
	 */
	public void clearCache()
	{
		indexCache.clear();
	}
}
