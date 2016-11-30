package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Collection of various methods used to calculate the names and the locations
 * of image files based on their capture date.
 */
public class FileNamingUtils
{
	private static final DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

	private static final DateFormat MONTH_FORMAT = new SimpleDateFormat("MM");

	private static final DateFormat DAY_FORMAT = new SimpleDateFormat("dd");
	
	private static final DateFormat IMAGE_NAME_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");
	
	/**
	 * Returns the image directory where the image should be saved.
	 * 
	 * @param targetDir
	 *            the target directory where the images should be stored
	 * @param imageDate
	 *            the date when the image was shot
	 * 
	 * @return the path where the image must be saved
	 * 
	 * @throws java.lang.NullPointerException
	 *             if image some of the arguments is null
	 */
	public File getImageDir(File targetDir, Date imageDate)
	{
		Objects.requireNonNull(imageDate, "The image date cannot be null!");
		Objects.requireNonNull(targetDir, "The image date cannot be null!");

		File ret = new File(targetDir, YEAR_FORMAT.format(imageDate));

		ret = new File(ret, MONTH_FORMAT.format(imageDate));
		ret = new File(ret, DAY_FORMAT.format(imageDate));

		return ret;
	}
	
	/**
	 * Extracts the extension of the provided file.
	 * 
	 * @param aFile the file whose extension is looked up.
	 * 
	 * @return the extension of the file or null if the extension can't be extracted.
	 */
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
			String result = fileName.substring(extIdx + 1, fileName.length());
			return result.isEmpty() ? null : result; 
		}
	}
	
	/**
	 * Constructs image file name.
	 * 
	 * @param imageDate the date of the image capture
	 * @param index the index of the image
	 * @param ext the extension of the image file
	 * 
	 * @return the name of the image
	 */
	public String getImageFileName(Date imageDate, int index, String ext)
	{
		String idx = String.format("%1$05d", index);

		return IMAGE_NAME_FORMAT.format(imageDate) + "_" + idx + "." + ext;
	}


}
