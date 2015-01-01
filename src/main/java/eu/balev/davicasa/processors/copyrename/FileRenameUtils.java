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
import org.slf4j.LoggerFactory;

import eu.balev.davicasa.MD5Calculator;

public class FileRenameUtils
{
	/*
	 * Currently the target directory structure is:
	 * 
	 * YYYY 
	 * 	 |----MM 
	 *         |----DD
	 */

	private static Logger logger = LoggerFactory
			.getLogger(FileRenameUtils.class);

	private File targetDir;

	private boolean dryRun;

	private static final DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

	private static final DateFormat MONTH_FORMAT = new SimpleDateFormat("MM");

	private static final DateFormat DAY_FORMAT = new SimpleDateFormat("dd");

	private static final DateFormat IMAGE_NAME_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");

	@Inject
	MD5Calculator md5Calculator;

	@Inject
	@Named("ImageFileFilter")
	FileFilter imageFilter;
	
	@Inject
	@Named("FileIdentityComparator")
	Comparator<File> fileComparator;

	/**
	 * Maintains a cache for free indices. The key in the map is the directory
	 * where the image should go and the value is the next free index.
	 */
	private Map<File, Integer> indexCache = new HashMap<>();

	private Map<File, Map<String, List<File>>> targetFilesCache = new HashMap<>();

	/**
	 * Returns the image directory where the image should be saved.
	 * 
	 * @param imageDate
	 *            the date when the image was shot
	 * 
	 * @return the path where the image must be saved
	 * 
	 * @throws java.lang.NullPointerException
	 *             if image date is null
	 */
	public File getImageDir(Date imageDate)
	{
		Objects.requireNonNull(imageDate, "The image date cannot be null!");

		File ret = new File(targetDir, YEAR_FORMAT.format(imageDate));

		ret = new File(ret, MONTH_FORMAT.format(imageDate));
		ret = new File(ret, DAY_FORMAT.format(imageDate));

		return ret;
	}

	public String getImageFileName(Date imageDate, int index, String ext)
	{
		String idx = String.format("%1$05d", index);

		return IMAGE_NAME_FORMAT.format(imageDate) + "_" + idx + "." + ext;
	}

	public void setDryRun(boolean dryRun)
	{
		this.dryRun = dryRun;
	}

	public File processImageFile(File aFile, Date imageDate) throws IOException
	{
		File targetDir = getImageDir(imageDate);

		if (!checkImageDir(targetDir))
		{
			return null;
		}

		String ext = getFileExtension(aFile);
		Integer index = getAndUpdateFreeIndex(imageDate, ext);

		File targetFile = new File(targetDir, getImageFileName(imageDate,
				index, ext));

		if (!dryRun)
		{
			copyFile(aFile, targetFile);
		}

		logger.info("Copied {} to {}. Dry run enabled - {}.",
				aFile.getAbsolutePath(), targetFile.getAbsolutePath(), dryRun);

		return targetFile;
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

		String md5 = md5Calculator.getMD5Sum(targetFile);

		if (cache.containsKey(md5))
		{
			// the cache has such md5 sum, meaning that the
			// target dir most likely contains a duplicate
			List<File> existingFiles = cache.get(md5);
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
			List<File> filesForMD5 = new LinkedList<>();
			filesForMD5.add(targetFile);
			cache.put(md5, filesForMD5);
		}

		return null;
	}

	private Map<String, List<File>> createFileCache(File targetDir)
			throws IOException
	{
		// initialize the cache with all files available
		Map<String, List<File>> cache = new HashMap<>();
		File[] existingFiles = targetDir.listFiles(imageFilter);
		for (File existingFile : existingFiles)
		{
			String md5 = md5Calculator.getMD5Sum(existingFile);

			List<File> files = cache.get(md5);
			if (files == null)
			{
				files = new LinkedList<>();
				cache.put(md5, files);
			}
			files.add(existingFile);
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

	public String getFileExtension(File aFile)
	{
		String fileName = aFile.getName();
		int extIdx = fileName.lastIndexOf(".");

		if (extIdx == -1)
		{
			return null;
		}
		else
		{
			return fileName.substring(extIdx + 1, fileName.length());
		}
	}

	public int getAndUpdateFreeIndex(Date imageDate, String ext)
	{
		File imageDir = getImageDir(imageDate);

		Integer freeIdx = indexCache.get(imageDir);
		if (freeIdx == null)
		{
			freeIdx = 1;
		}

		if (!dryRun)
		{
			while (true)
			{
				File targetFile = new File(imageDir, getImageFileName(
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
