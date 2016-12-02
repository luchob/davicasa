package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import eu.balev.davicasa.util.ImageHashCalculator;

/**
 * Maintains a cache in which for each directory there are lists of files with
 * identical contents.
 * 
 * The purpose of this cache is the quick discovery of identical files, possibly
 * copied earlier.
 *
 */
class TargetFileCache
{
	private Map<File, Map<String, List<File>>> targetFilesCache = new HashMap<>();

	@Inject
	private ImageHashCalculator hashCalculator;

	@Inject
	@Named("ImageFileFilter")
	private FileFilter imageFilter;
	
	@Inject
	@Named("FileIdentityComparator")
	private Comparator<File> fileComparator;

	/**
	 * Clears the cache
	 */
	void clearCache()
	{
		targetFilesCache.clear();
	}

	/**
	 * returns a duplicate of the target file within the target directory. If
	 * the duplicate does not exist then the cache is updated with the new entry
	 * for future quick reference.
	 * 
	 * @param targetDir the target directory
	 * @param targetFile the target file to be upldated
	 * 
	 * @return a possible duplicate
	 * 
	 * @throws IOException
	 */
	File getAndUpdateCache(File targetDir, File targetFile) throws IOException
	{
		Map<String, List<File>> cache = targetFilesCache.get(targetDir);
		if (cache == null)
		{
			// no cache for this directory.
			// initialize the cache.
			cache = createFileCache(targetDir);
			targetFilesCache.put(targetDir, cache);
		}

		String hash = hashCalculator.getHashSum(targetFile);

		List<File> existingFiles = cache.computeIfAbsent(hash,
				h -> new LinkedList<>());

		// search for existing file..
		Optional<File> existingOpt = existingFiles
				.stream()
				.filter(existingFile -> (fileComparator.compare(targetFile,
						existingFile) == 0)).findAny();
		if (!existingOpt.isPresent())
		{
			existingFiles.add(targetFile);

		}
		return existingOpt.orElse(null);
	}

	private Map<String, List<File>> createFileCache(File targetDir)
			throws IOException
	{
		// initialize the cache with all files available
		Map<String, List<File>> cache = new HashMap<>();
		File[] existingFiles = targetDir.listFiles(imageFilter);
		if (existingFiles != null)
		{
			for (File existingFile : existingFiles)
			{
				cache.computeIfAbsent(hashCalculator.getHashSum(existingFile),
						h -> new LinkedList<>()).add(existingFile);
			}
		}
		return cache;
	}

}
