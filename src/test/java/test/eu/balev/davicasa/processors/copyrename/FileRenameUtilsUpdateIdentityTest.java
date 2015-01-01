package test.eu.balev.davicasa.processors.copyrename;

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

import test.eu.balev.davicasa.processors.TestMD5Calculator;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import eu.balev.davicasa.ImageFileFilter;
import eu.balev.davicasa.MD5Calculator;
import eu.balev.davicasa.processors.copyrename.FileRenameUtils;
import eu.balev.davicasa.util.FileIdentityComparator;

@RunWith(MockitoJUnitRunner.class)
public class FileRenameUtilsUpdateIdentityTest
{
	private FileRenameUtils fileRenameUtilsToTest;

	@Mock
	File emptyDirMock, fullDirMock;

	@Mock
	File img1MockFile, img2MockFile;

	@Before
	public void setUp()
	{
		fileRenameUtilsToTest = new FileRenameUtils();

		fileRenameUtilsToTest.init(new File("."), true);

		Injector injector = Guice.createInjector(new DavicasaTestModule());
		injector.injectMembers(fileRenameUtilsToTest);

		// empty dir mock
		Mockito.when(emptyDirMock.listFiles(Mockito.any(FileFilter.class)))
				.thenReturn(new File[]
				{});

		Mockito.when(img1MockFile.getName()).thenReturn("img1MockFile");
		Mockito.when(img2MockFile.getName()).thenReturn("img2MockFile");

		Mockito.when(fullDirMock.listFiles(Mockito.any(FileFilter.class)))
				.thenReturn(new File[]
				{ img1MockFile, img2MockFile });
	}

	@Test
	public void testCheckAndUpdateIdentityEmptyDir() throws IOException
	{
		File res = fileRenameUtilsToTest.checkAndUpdateIdentity(emptyDirMock,
				img1MockFile);

		Assert.assertNull("The result should be null, we expect no duplicates",
				res);
	}

	@Test
	public void testCheckAndUpdateIdentityFullDirNoCollision()
			throws IOException
	{
		File aRandomImageMock = Mockito.mock(File.class);

		File res = fileRenameUtilsToTest.checkAndUpdateIdentity(fullDirMock,
				aRandomImageMock);

		Assert.assertNull("The result should be null, we expect no duplicates",
				res);
	}

	private class DavicasaTestModule extends AbstractModule
	{

		@Override
		protected void configure()
		{
			bind(new TypeLiteral<Comparator<File>>()
			{
			}).annotatedWith(Names.named("FileIdentityComparator")).to(
					FileIdentityComparator.class);

			bind(FileFilter.class)
					.annotatedWith(Names.named("ImageFileFilter")).to(
							ImageFileFilter.class);

			bind(MD5Calculator.class).to(TestMD5Calculator.class);
		}
	}
}
