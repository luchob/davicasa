package eu.balev.davicasa.util.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation of a comparator compares files bitwise. If the files are
 * of the same length the comparator makes a bitwise comparison and returns 0 or
 * -1 if the files are identical or different. Otherwise the comparator compares
 * the files based on their length.
 * 
 */
public class FileIdentityComparator implements Comparator<File>
{
	private Logger logger = LoggerFactory
			.getLogger(FileIdentityComparator.class);

	@Override
	public int compare(File file1, File file2)
	{
		long file1Len = file1.length();
		long file2Len = file2.length();

		if (file1Len != file2Len)
		{
			return file1Len > file2Len ? 1 : -1;
		}

		try
		{
			return (areDuplicates(file1, file2)) ? 0 : -1;
		}
		catch (IOException e)
		{
			logger.error(
					"Unable to compare files {} and {}. Considering that the files are different. Reason: ",
					file1.getAbsolutePath(), file2.getAbsolutePath(), e);
			return -1;
		}
	}

	private boolean areDuplicates(File file1, File file2)
			throws FileNotFoundException, IOException
	{

		int buffSize = 8192;
		byte fis1Bytes[] = new byte[buffSize];
		byte fis2Bytes[] = new byte[buffSize];

		try (FileInputStream fis1 = new FileInputStream(file1);
				FileInputStream fis2 = new FileInputStream(file2))
		{
			int read1 = getFullBuffer(fis1, fis1Bytes);
			int read2 = getFullBuffer(fis2, fis2Bytes);

			if (read1 != read2 || (!equals(fis1Bytes, fis2Bytes, read1)))
			{
				return false;
			}
		}

		return true;
	}

	public int getFullBuffer(InputStream is, byte[] buffer) throws IOException
	{
		int read = 0, total = 0;
		while ((read = is.read(buffer, total, buffer.length - total)) != -1)
		{
			total += read;

			if (total == buffer.length)
			{
				break;
			}
		}

		return total;
	}

	public boolean equals(byte[] arr1, byte[] arr2, int len)
	{
		if (arr1.length < len || arr2.length < len)
		{
			return false;
		}

		for (int idx = 0; idx < len; idx++)
		{
			if (arr1[idx] != arr2[idx])
			{
				return false;
			}
		}

		return true;
	}

}
