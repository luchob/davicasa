package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class FileNamingUtilsTest
{
	private FileNamingUtils utilsToTest = new FileNamingUtils();
	
	private File mockTarget = new File(".");
	
	@Test(expected=NullPointerException.class)
	public void testGetImageDirNoImageDate()
	{
		utilsToTest.getImageDir(mockTarget, null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetImageDirNoTargetDir()
	{
		utilsToTest.getImageDir(null, new Date());
	}
	
	@Test
	public void testGetImageDirTest19790307()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(1979, Calendar.MARCH, 7, 0, 0, 0);

		File theImageDir = utilsToTest.getImageDir(mockTarget, cal.getTime());

		Assert.assertEquals("07", theImageDir.getName());
		Assert.assertEquals("03", theImageDir.getParentFile().getName());
		Assert.assertEquals("1979", theImageDir.getParentFile().getParentFile()
				.getName());
	}

	@Test
	public void testGetImageDirTest20101231()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2010, Calendar.DECEMBER, 31, 0, 0, 0);

		File theImageDir = utilsToTest.getImageDir(mockTarget, cal.getTime());

		Assert.assertEquals("31", theImageDir.getName());
		Assert.assertEquals("12", theImageDir.getParentFile().getName());
		Assert.assertEquals("2010", theImageDir.getParentFile().getParentFile()
				.getName());
	}
	
	@Test
	public void testGetImageDirTest20100101()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2010, Calendar.JANUARY, 1, 0, 0, 0);

		File theImageDir = utilsToTest.getImageDir(mockTarget, cal.getTime());

		Assert.assertEquals("01", theImageDir.getName());
		Assert.assertEquals("01", theImageDir.getParentFile().getName());
		Assert.assertEquals("2010", theImageDir.getParentFile().getParentFile()
				.getName());
	}
	
	@Test
	public void testGetFileExtensionNoExt()
	{
		File file = new File("afile");
		
		Assert.assertNull("The file should have no extension...", utilsToTest.getFileExtension(file));
	}
	
	@Test
	public void testGetFileExtensionJPG()
	{
		File file = new File("afile.JPG");
		
		Assert.assertEquals("JPG", utilsToTest.getFileExtension(file));
	}
	
	@Test
	public void testGetFileExtensionEndsDOT()
	{
		File file = new File("afile.");
		
		Assert.assertNull("Empty extension is not supported...", utilsToTest.getFileExtension(file));
	}
	
	@Test
	public void testGetImageNameTest()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2010, Calendar.AUGUST, 15, 0, 0, 0);

		String theImageName = utilsToTest.getImageFileName(cal.getTime(), 5, "JPG");

		Assert.assertEquals("20100815_00005.JPG", theImageName);
		
	}
}
