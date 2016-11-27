package eu.balev.davicasa.util.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Hex;

import eu.balev.davicasa.util.ImageHashCalculator;

public class ImageHashCalculatorImpl implements ImageHashCalculator
{

	@Inject
	@Named("imagehashalg")
	private String hashAlg;

	@Override
	public String getHashSum(File file) throws IOException
	{
		MessageDigest messageDigest = null;
		try
		{
			messageDigest = MessageDigest.getInstance(hashAlg);
		}
		catch (NoSuchAlgorithmException ex)
		{
			throw new RuntimeException(
					"Unable to find "
							+ hashAlg
							+ " implementation. The processor requires it for file comparison",
					ex);
		}

		try (InputStream fis = new FileInputStream(file))
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
