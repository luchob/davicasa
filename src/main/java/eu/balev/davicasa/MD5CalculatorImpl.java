package eu.balev.davicasa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class MD5CalculatorImpl implements MD5Calculator
{

	@Override
	public String getMD5Sum(File file) throws IOException
	{
		MessageDigest messageDigest = null;
		try
		{
			messageDigest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException ex)
		{
			throw new RuntimeException(
					"Unable to find MD5 implementation. The processor requires MD5 for file searches",
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

		byte[] md5Bytes = messageDigest.digest();

		return Hex.encodeHexString(md5Bytes);
	}

}
