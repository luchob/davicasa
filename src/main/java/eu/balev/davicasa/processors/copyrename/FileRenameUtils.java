package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Objects;

import javax.inject.Inject;

import org.slf4j.Logger;

import eu.balev.davicasa.inject.InjectLogger;

public class FileRenameUtils
{
	/*
	 * Currently the target directory structure is:
	 * 
	 * YYYY 
	 * 	|----MM 
	 * 		  |----DD
	 */

	@InjectLogger
	private Logger logger;

	private File targetDir;

	private boolean dryRun;

	@Inject
	private FileNamingUtils fileNamingUtils;
	
	@Inject
	private FreeIndexCache indexCache;
	
	@Inject
	private TargetFileCache targetFileCache;

	public void setDryRun(boolean dryRun)
	{
		this.dryRun = dryRun;
	}

	public File processImageFile(File aFile, Date imageDate) throws IOException
	{
		File imageTargetDir = fileNamingUtils.getImageDir(targetDir, imageDate);

		if (!getOrCreateImageDir(imageTargetDir))
		{
			return null;
		}

		File duplicate = targetFileCache.getAndUpdateCache(imageTargetDir, aFile);

		if (duplicate == null)
		{
			String ext = fileNamingUtils.getFileExtension(aFile);
			if (ext == null)
			{
				logger.error(
						"Unable to process file [{}] because it has no extension, e.g. .jpg .gif, etc...",
						aFile.getAbsolutePath());
				return null;
			}
			int index = indexCache.getAndUpdateFreeIndex(imageTargetDir, imageDate, ext);

			File targetFile = new File(imageTargetDir,
					fileNamingUtils.getImageFileName(imageDate, index, ext));

			if (!dryRun)
			{
				Files.copy(aFile.toPath(), targetFile.toPath());
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

	private boolean getOrCreateImageDir(File imageDir)
	{
		boolean ret = true;
		if (!dryRun)
		{
			if (!imageDir.exists())
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
			else
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

	public void init(File targetDir, boolean dryRun)
	{
		Objects.requireNonNull(targetDir, "Target dir cannot be null!");

		this.targetDir = targetDir;
		this.setDryRun(dryRun);

		indexCache.clearCache();
		targetFileCache.clearCache();
	}
}
