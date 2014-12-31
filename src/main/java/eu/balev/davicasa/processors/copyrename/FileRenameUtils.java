package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRenameUtils
{
	/*
	 * YYYY/MM/DD
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

	private Map<File, Integer> indexCache = new HashMap<>();

	/**
	 * Returns the image directory where the image should be saved.
	 * 
	 * @param imageDate
	 *            the date when the image was shot
	 * 
	 * @return the relative path where the image must be saved
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
		File imageDir = getImageDir(imageDate);

		if (!checkImageDir(imageDir))
		{
			return null;
		}

		String ext = getFileExtension(aFile);
		Integer index = getAndUpdateFreeIndex(imageDate, ext);

		File targetFile = new File(imageDir, getImageFileName(imageDate, index,
				ext));

		if (!dryRun)
		{
			copyFile(aFile, targetFile);
		}

		logger.info("Copied {} to {}. Dry run enabled - {}.",
				aFile.getAbsolutePath(), targetFile.getAbsolutePath(), dryRun);

		return targetFile;
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
			}
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
	}
}
