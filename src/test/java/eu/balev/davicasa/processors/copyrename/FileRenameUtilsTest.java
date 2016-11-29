package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

import eu.balev.davicasa.inject.SLF4JTypeListener;
import eu.balev.davicasa.processors.TestFileIdentityComparator;
import eu.balev.davicasa.processors.copyrename.FileRenameUtils;
import eu.balev.davicasa.util.ImageFileFilter;
import eu.balev.davicasa.util.ImageHashCalculator;
import eu.balev.davicasa.util.TestHashCalculator;

public class FileRenameUtilsTest
{
	private FileRenameUtils fileRenameUtilsToTest;

	@Before
	public void setUp()
	{
		fileRenameUtilsToTest = new FileRenameUtils();
		
		fileRenameUtilsToTest.init(new File("."), true);
		
		Injector injector = Guice.createInjector(new DavicasaTestModule());
		injector.injectMembers(fileRenameUtilsToTest);
	}
	
	@After
	public void tearDown()
	{
		fileRenameUtilsToTest = null;
	}

	@Test
	public void getImageDirTest19790307()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(1979, Calendar.MARCH, 7, 0, 0, 0);

		File theImageDir = fileRenameUtilsToTest.getImageDir(cal.getTime());

		Assert.assertEquals("07", theImageDir.getName());
		Assert.assertEquals("03", theImageDir.getParentFile().getName());
		Assert.assertEquals("1979", theImageDir.getParentFile().getParentFile()
				.getName());
	}

	@Test
	public void getImageDirTest20101231()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2010, Calendar.DECEMBER, 31, 0, 0, 0);

		File theImageDir = fileRenameUtilsToTest.getImageDir(cal.getTime());

		System.out.println("Date time: " + cal.getTime());

		Assert.assertEquals("31", theImageDir.getName());
		Assert.assertEquals("12", theImageDir.getParentFile().getName());
		Assert.assertEquals("2010", theImageDir.getParentFile().getParentFile()
				.getName());
	}
	
	@Test
	public void getImageDirTest20100101()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2010, Calendar.JANUARY, 1, 0, 0, 0);

		File theImageDir = fileRenameUtilsToTest.getImageDir(cal.getTime());

		System.out.println("Date time: " + cal.getTime());

		Assert.assertEquals("01", theImageDir.getName());
		Assert.assertEquals("01", theImageDir.getParentFile().getName());
		Assert.assertEquals("2010", theImageDir.getParentFile().getParentFile()
				.getName());
	}
	
	@Test
	public void getImageNameTest()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2010, Calendar.AUGUST, 15, 0, 0, 0);

		String theImageName = fileRenameUtilsToTest.getImageFileName(cal.getTime(), 5, "JPG");

		Assert.assertEquals("20100815_00005.JPG", theImageName);
		
	}
	
	@Test
	public void getFileExtensionTestNoExt()
	{
		File file = new File("afile");
		
		Assert.assertNull("The file should have no extension...", fileRenameUtilsToTest.getFileExtension(file));
	}
	
	@Test
	public void getFileExtensionTestJPG()
	{
		File file = new File("afile.JPG");
		
		Assert.assertEquals("JPG", fileRenameUtilsToTest.getFileExtension(file));
	}
	
	@Test
	public void getFileExtensionTestEndsDOT()
	{
		File file = new File("afile.");
		
		Assert.assertEquals("", fileRenameUtilsToTest.getFileExtension(file));
	}
	
	@Test
	public void getAndUpdateFreeIndexTest() throws IOException
	{
		File dummyFile = new File("dummy.jpg");
		Date now = new Date();
		
		fileRenameUtilsToTest.processImageFile(dummyFile, now);
		
		for (int expected = 2; expected < 10; expected++)
		{
			int idx = fileRenameUtilsToTest.getAndUpdateFreeIndex(now, "jpg");
			Assert.assertEquals(expected, idx);
		}	
	}
	
	@Test
	public void testProcessImageFile() throws IOException
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2010, Calendar.AUGUST, 15, 0, 0, 0);
		
		File dummyFile = new File("dummy.jpg");
		
		File renamed = fileRenameUtilsToTest.processImageFile(dummyFile, cal.getTime());
		
		Assert.assertEquals("20100815_00001.jpg", renamed.getName());
	}
	
	/**
	 * A test module for this file rename utils.
	 */
	private class DavicasaTestModule extends AbstractModule
	{
		@Override
		protected void configure()
		{
			bind(new TypeLiteral<Comparator<File>>()
			{
			}).annotatedWith(Names.named("FileIdentityComparator")).to(
					TestFileIdentityComparator.class);

			bind(FileFilter.class)
					.annotatedWith(Names.named("ImageFileFilter")).to(
							ImageFileFilter.class);

			bind(ImageHashCalculator.class).to(TestHashCalculator.class);
			bindListener(Matchers.any(), new SLF4JTypeListener());
		}
	}
}
