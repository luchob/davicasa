package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Comparator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import eu.balev.davicasa.processors.TestFileIdentityComparator;
import eu.balev.davicasa.processors.copyrename.FileRenameUtils;
import eu.balev.davicasa.util.ImageFileFilter;
import eu.balev.davicasa.util.ImageHashCalculator;
import eu.balev.davicasa.util.TestHashCalculator;

/**
 * Tests the behavior of the
 * {@link FileRenameUtils#checkAndUpdateIdentity(File, File)} method.
 */
@RunWith(MockitoJUnitRunner.class)
public class FileRenameUtilsUpdateIdentityTest
{
	private FileRenameUtils fileRenameUtilsToTest;

	@Mock
	File emptyDirMock, fullDir1Mock, fullDir2Mock;

	@Mock
	File dir1Img1MockFile, dir1Img2MockFile;

	@Mock
	File dir2Img1MockFile, dir2Img2MockFile;

	@Before
	public void setUp()
	{
		fileRenameUtilsToTest = new FileRenameUtils();

		Injector injector = Guice.createInjector(new DavicasaTestModule());
		injector.injectMembers(fileRenameUtilsToTest);

		fileRenameUtilsToTest.init(new File("."), true);
		
		// initialize the names of the full directories
		Mockito.when(fullDir1Mock.getName()).thenReturn("fullDir1Mock");
		Mockito.when(fullDir2Mock.getName()).thenReturn("fullDir2Mock");

		// make the full directories return some files
		prepareDirMock(fullDir1Mock, dir1Img1MockFile, dir1Img2MockFile);
		prepareDirMock(fullDir2Mock, dir2Img1MockFile, dir2Img2MockFile);

		// make the empty dir return empty array for list files
		prepareDirMock(emptyDirMock);
	}

	/**
	 * Mocks the {@link File#getName()} of the files in the file array based on
	 * the name of the mock directory. Mocks the
	 * {@link File#listFiles(FileFilter)} method so that the mock directory
	 * returns the files in the provided array.
	 * 
	 * @param dirMock
	 *            the mock directory
	 * @param files
	 *            the files that should belong to the mock dir
	 */
	private void prepareDirMock(File dirMock, File... files)
	{
		// empty dir mock
		Mockito.when(dirMock.listFiles(Mockito.any(FileFilter.class)))
				.thenReturn(files);
		String dirMockName = dirMock.getName();

		for (int i = 0; i < files.length; i++)
		{
			Mockito.when(files[i].getName()).thenReturn(
					dirMockName + "_mockFile_" + i);
		}

	}

	@Test
	public void testCheckAndUpdateIdentityEmptyDir() throws IOException
	{
		File res = fileRenameUtilsToTest.checkAndUpdateIdentity(emptyDirMock,
				dir1Img1MockFile);

		Assert.assertNull("The result should be null, we expect no duplicates.",
				res);
	}

	@Test
	public void testCheckAndUpdateIdentityFullDirNoCollision()
			throws IOException
	{
		File aRandomImageMock = Mockito.mock(File.class);

		File res = fileRenameUtilsToTest.checkAndUpdateIdentity(fullDir1Mock,
				aRandomImageMock);

		Assert.assertNull("The result should be null, we expect no duplicates.",
				res);
	}

	@Test
	public void testCheckAndUpdateIdentityFullDirCollision() throws IOException
	{
		File res = fileRenameUtilsToTest.checkAndUpdateIdentity(fullDir1Mock,
				dir1Img1MockFile);

		Assert.assertNotNull("A duplicate should have been found.", res);
		Assert.assertEquals("The mock file should be reported as duplicate.",
				dir1Img1MockFile.getName(), res.getName());
	}

	@Test
	public void testCheckAndUpdateIdentityTwoFullDirsCollision()
			throws IOException
	{
		File res1 = fileRenameUtilsToTest.checkAndUpdateIdentity(fullDir1Mock,
				dir1Img1MockFile);
		File res2 = fileRenameUtilsToTest.checkAndUpdateIdentity(fullDir2Mock,
				dir2Img1MockFile);

		Assert.assertNotNull("A duplicate should have been found.", res1);
		Assert.assertNotNull("A duplicate should have been found.", res2);

		Assert.assertEquals("The mock file should be reported as duplicate.",
				dir1Img1MockFile.getName(), res1.getName());
		Assert.assertEquals("The mock file should be reported as duplicate.",
				dir2Img1MockFile.getName(), res2.getName());
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
		}
	}
}
