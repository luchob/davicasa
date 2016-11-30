package eu.balev.davicasa.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import eu.balev.davicasa.util.FileIdentityComparator;

public class FileIdentityComparatorTest
{
	private FileIdentityComparator identityComparatorToTest = new FileIdentityComparator();

	private Random rand = new Random();

	@Test
	public void testIdenticalFiles() throws IOException
	{
		File randFile = generateRandomFile((byte) 1, 1000);

		int comparisonResult = identityComparatorToTest.compare(randFile,
				randFile);

		randFile.delete();

		Assert.assertEquals("The files should be equal!", 0, comparisonResult);
	}

	@Test
	public void testDifferentFiles() throws IOException
	{
		File randFile1 = generateRandomFile((byte) 1, 1000);
		File randFile2 = generateRandomFile((byte) 2, 1000);

		int comparisonResult = identityComparatorToTest.compare(randFile1,
				randFile2);

		randFile1.delete();
		randFile2.delete();

		Assert.assertFalse("The files should not be equal!",
				0 == comparisonResult);
	}

	@Test
	public void testGetFullBuffer() throws IOException
	{
		// fill in an array with random bytes
		byte allBytes[] = new byte[8192];
		rand.nextBytes(allBytes);
		// an input stream that returns the previously generated byte array
		ByteArrayInputStream allBytesIS = new ByteArrayInputStream(allBytes);

		// a buffer that has to be read
		byte[] actual = new byte[1000];

		// the number of iterations in which the buffer should be full
		int fullBufferCnt = allBytes.length / actual.length;

		byte expecteds[] = new byte[actual.length];
		for (int i = 0; i < fullBufferCnt; i++)
		{
			int read = identityComparatorToTest.getFullBuffer(allBytesIS,
					actual);
			Assert.assertEquals(
					"It is expected that the full buffer had been read...",
					actual.length, read);

			System.arraycopy(allBytes, i * actual.length, expecteds, 0,
					actual.length);
			Assert.assertArrayEquals(
					"The input stream is not read correctly...", expecteds,
					actual);
		}

		// the remaining bytes that do not fit into a complete buffer
		int remainingBytes = allBytes.length % actual.length;
		int read = identityComparatorToTest.getFullBuffer(allBytesIS, actual);

		Assert.assertEquals(
				"It is expected that the the remaining bytes that will fit into part of the buffer will be read.",
				remainingBytes, read);

		expecteds = new byte[remainingBytes];
		byte lastActuals[] = new byte[remainingBytes];

		System.arraycopy(allBytes, fullBufferCnt * actual.length, expecteds, 0,
				expecteds.length);
		System.arraycopy(actual, 0, lastActuals, 0, remainingBytes);
		Assert.assertArrayEquals("The input stream is not read correctly...",
				expecteds, lastActuals);
	}

	@Test
	public void testGetFullBufferMultipleReads() throws IOException
	{
		byte actual[] = new byte[100];
		rand.nextBytes(actual);

		InputStream testIS = new TestInputStream(actual, 9);

		byte expected[] = new byte[100];
		identityComparatorToTest.getFullBuffer(testIS, expected);

		Assert.assertArrayEquals(
				"The bytes read are not the same as expected...", expected,
				actual);
	}

	private class TestInputStream extends InputStream
	{
		private final int maxReadLen;
		private final byte buffer[];

		private int pos;

		TestInputStream(byte buffer[], int maxReadLen)
		{
			this.maxReadLen = maxReadLen;
			this.buffer = buffer;
		}

		public int read(byte[] b, int off, int len)
		{
			if (pos >= buffer.length)
			{
				return -1;
			}

			int read = 0;

			for (int i = off; i < off + len; i++)
			{
				b[i] = buffer[pos++];
				read++;
				if (read >= maxReadLen)
				{
					break;
				}
			}

			return read;
		}

		@Override
		public int read()
		{
			return rand.nextInt();
		}

	}

	@Test
	public void testEqualsByteArraysLenMoreThanArrayLen()
	{
		byte buffer[] = new byte[10];
		Assert.assertFalse(
				"The comparator should not report that the arrays are equal because the comparison length is more than the array length...",
				identityComparatorToTest.equals(buffer, buffer, 15));
	}

	@Test
	public void testEqualsByteArraysSameArrays()
	{
		byte buffer[] = new byte[100];
		rand.nextBytes(buffer);
		Assert.assertTrue(
				"The comparator should report that the arrays are equal...",
				identityComparatorToTest.equals(buffer, buffer, 90));
	}

	@Test
	public void testEqualsByteArraysDifferentArrays()
	{
		byte buffer1[] = new byte[100];
		byte buffer2[] = new byte[100];
		rand.nextBytes(buffer1);
		rand.nextBytes(buffer2);
		Assert.assertFalse(
				"The comparator should not report that the arrays are equal...",
				identityComparatorToTest.equals(buffer1, buffer2, 90));
	}

	private File generateRandomFile(byte startByte, int len) throws IOException
	{
		File randFile = File.createTempFile(this.getClass().getSimpleName(),
				".dvc");

		try (FileOutputStream fos = new FileOutputStream(randFile))
		{
			byte[] bytes = new byte[len - 1];
			rand.nextBytes(bytes);
			fos.write(new byte[]
			{ startByte });
			fos.write(bytes);
			fos.flush();
		}

		return randFile;

	}
}