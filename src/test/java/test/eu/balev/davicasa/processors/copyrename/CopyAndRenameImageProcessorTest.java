package test.eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import test.eu.balev.davicasa.processors.TestMD5Calculator;

import com.drew.imaging.ImageProcessingException;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import eu.balev.davicasa.processors.copyrename.CopyAndRenameImageProcessor;
import eu.balev.davicasa.processors.copyrename.FileRenameUtils;
import eu.balev.davicasa.processors.copyrename.ImageCreateDateExtractor;
import eu.balev.davicasa.util.ImageFinder;
import eu.balev.davicasa.util.MD5Calculator;
import eu.balev.davicasa.util.impl.FileIdentityComparator;
import eu.balev.davicasa.util.impl.ImageFileFilter;

@RunWith(MockitoJUnitRunner.class)
public class CopyAndRenameImageProcessorTest
{
	@Mock
	File mockDir;
	@Mock
	File mockFile1, mockFile2;
	private CopyAndRenameImageProcessor processorToTest;
	private Date aDate;

	@Mock
	ImageCreateDateExtractor dateExtractorMock;
	@Mock
	FileRenameUtils fileRenameUtilsMock;

	@Before
	public void setUp() throws IOException, ImageProcessingException
	{
		processorToTest = new CopyAndRenameImageProcessor(mockDir, mockDir);

		Injector injector = Guice.createInjector(new DavicasaTestModule());
		injector.injectMembers(processorToTest);

		aDate = new Date();

		Mockito.when(dateExtractorMock.getImageDate(mockFile1)).thenReturn(
				aDate);


		processorToTest.setDryRun(true);
	}

	@Test
	public void testDateExtracted() throws IOException,
			ImageProcessingException
	{
		processorToTest.process();

		Mockito.verify(dateExtractorMock, Mockito.times(1)).getImageDate(
				mockFile1);
	}

	@Test
	public void testProcessImageFileCalled() throws IOException,
			ImageProcessingException
	{
		processorToTest.process();

		Mockito.verify(fileRenameUtilsMock, Mockito.times(1)).processImageFile(
				mockFile1, aDate);
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

			bind(FileRenameUtils.class).toInstance(fileRenameUtilsMock);

			bind(FileFilter.class)
					.annotatedWith(Names.named("ImageFileFilter")).to(
							ImageFileFilter.class);

			bind(ImageFinder.class).toInstance(new TestImageFinder());
			bind(ImageCreateDateExtractor.class).toInstance(dateExtractorMock);
			bind(MD5Calculator.class).to(TestMD5Calculator.class);
		}
	}

	private class TestImageFinder implements ImageFinder
	{
		@Override
		public List<File> listImages(File sourceDir)
		{
			List<File> allFiles = new LinkedList<>();

			if (sourceDir == mockDir)
			{
				allFiles.add(mockFile1);
				allFiles.add(mockFile2);
			}

			return allFiles;
		}
	}
}
