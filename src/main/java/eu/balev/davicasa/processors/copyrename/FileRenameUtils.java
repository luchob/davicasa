package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import eu.balev.davicasa.inject.InjectLogger;
import eu.balev.davicasa.util.ImageHashCalculator;

public class FileRenameUtils
{
	/*
	 * Currently the target directory structure is:
	 * 
	 * YYYY |----MM |----DD
	 */

	@InjectLogger
	private Logger logger;

	private File targetDir;

	private boolean dryRun;

	@Inject
	private ImageHashCalculator hashCalculator;

	@Inject
	@Named("ImageFileFilter")
	private FileFilter imageFilter;

	@Inject
	@Named("FileIdentityComparator")
	private Comparator<File> fileComparator;//todo - check istantiation of the comparator
	
	@Inject
	private FileNamingUtils fileNamingUtils;

	/**
	 * Maintains a cache for free indices. The key in the map is the directory
	 * where the image should go and the value is the next free index.
	 */
	private Map<File, Integer> indexCache = new HashMap<>();

	private Map<File, Map<String, List<File>>> targetFilesCache = new HashMap<>();
	
	public void setDryRun(boolean dryRun)
	{
		this.dryRun = dryRun;
	}

	public File processImageFile(File aFile, Date imageDate) throws IOException
	{
		File imageTargetDir = fileNamingUtils.getImageDir(targetDir, imageDate);

		if (!checkImageDir(imageTargetDir))
		{
			return null;
		}

		File duplicate = checkAndUpdateIdentity(imageTargetDir, aFile);

		if (duplicate == null)
		{
			String ext = fileNamingUtils.getFileExtension(aFile);
			if (ext == null)
			{
				logger.error("Unable to process file [{}] because it has no extension, e.g. .jpg .gif, etc...", 
						aFile.getAbsolutePath());
				return null;
			}
			Integer index = getAndUpdateFreeIndex(imageDate, ext);

			File targetFile = new File(imageTargetDir, fileNamingUtils.getImageFileName(imageDate,
					index, ext));

			if (!dryRun)
			{
				copyFile(aFile, targetFile);
			}

			logger.info("Copied {} to {}. Dry run enabled - {}.",
					aFile.getAbsolutePath(), targetFile.getAbsolutePath(),
					dryRun);

			return targetFile;
		}
		else
		{
			logger.info(
					"There is an identical file of {} in the target - {}. Will not copy.",
					aFile.getAbsolutePath(), duplicate.getAbsolutePath());
			return null;
		}
	}

	public File checkAndUpdateIdentity(File targetDir, File targetFile)
			throws IOException
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

		if (cache.containsKey(hash))
		{
			// the cache has such hash sum, meaning that the
			// target dir most likely contains a duplicate
			List<File> existingFiles = cache.get(hash);
			if (existingFiles.isEmpty())
			{
				existingFiles.add(targetFile);
			}
			else
			{

				Iterator<File> existingFilesIter = existingFiles.iterator();
				while (existingFilesIter.hasNext())
				{
					File existingFile = existingFilesIter.next();

					if (fileComparator.compare(targetFile, existingFile) == 0)
					{
						return existingFile;
					}
				}

				existingFiles.add(targetFile);
			}
		}
		else
		{
			List<File> filesForHash = new LinkedList<>();
			filesForHash.add(targetFile);
			cache.put(hash, filesForHash);
		}

		return null;
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
				String hash = hashCalculator.getHashSum(existingFile);

				List<File> files = cache.get(hash);
				if (files == null)
				{
					files = new LinkedList<>();
					cache.put(hash, files);
				}
				files.add(existingFile);
			}
		}
		return cache;
	}

	private void copyFile(File src, File target) throws IOException
	{
		byte[] buffer = new byte[8192];
		try (FileInputStream fis = new FileInputStream(src);
				FileOutputStream fos = new FileOutputStream(target))
		{
			int read = 0;
			while ((read = fis.read(buffer)) != -1)
			{
				fos.write(buffer, 0, read);
			}

			fos.flush();
		}
	}

	private boolean checkImageDir(File imageDir)
	{
		boolean ret = true;
		if (!imageDir.exists())
		{
			if (!dryRun)
			{
				ret = imageDir.mkdirs();
				if (!ret)
				{
					logger.error("Unable to create directory {}",
							imageDir.getAbsolutePath());
				}
				else
				{
					logger.info("Successfully created directory {}.",
							imageDir.getAbsolutePath());
				}
			}
		}
		else
		{
			if (!dryRun)
			{
				if (!imageDir.isDirectory())
				{
					ret = false;
					logger.error(
							"{} exists and is not directory. Unable to continue...",
							imageDir.getAbsolutePath());
				}
			}
		}

		return ret;
	}
	
	public int getAndUpdateFreeIndex(Date imageDate, String ext)
	{
		File imageDir = fileNamingUtils.getImageDir(targetDir, imageDate);

		Integer freeIdx = indexCache.get(imageDir);
		if (freeIdx == null)
		{
			freeIdx = 1;
		}

		if (!dryRun)
		{
			while (true)
			{
				File targetFile = new File(imageDir, fileNamingUtils.getImageFileName(
						imageDate, freeIdx, ext));
				if (!targetFile.exists())
				{
					break;
				}
				freeIdx++;
			}
		}

		indexCache.put(imageDir, freeIdx + 1);

		return freeIdx;
	}

	public void init(File targetDir, boolean dryRun)
	{
		Objects.requireNonNull(targetDir, "Target dir cannot be null!");

		this.targetDir = targetDir;
		this.setDryRun(dryRun);

		indexCache.clear();
		targetFilesCache.clear();
	}
}
