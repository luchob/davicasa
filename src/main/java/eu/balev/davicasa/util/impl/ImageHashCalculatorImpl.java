package eu.balev.davicasa.util.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

import eu.balev.davicasa.util.ImageHashCalculator;

public class ImageHashCalculatorImpl implements ImageHashCalculator
{

	@Override
	public String getHashSum(File file) throws IOException
	{
		MessageDigest messageDigest = null;
		try
		{
			messageDigest = MessageDigest.getInstance("SHA-256");
		}
		catch (NoSuchAlgorithmException ex)
		{
			throw new RuntimeException(
					"Unable to find SHA-256 implementation. The processor requires SHA-256 for file comparison",
					ex);
		}

		try (FileInputStream fis = new FileInputStream(file))
		{
			byte buffer[] = new byte[8192];
			int read = 0;
			while ((read = fis.read(buffer)) != -1)
			{
				messageDigest.update(buffer, 0, read);
			}
		}

		byte[] bytes = messageDigest.digest();

		return Hex.encodeHexString(bytes);
	}

}
