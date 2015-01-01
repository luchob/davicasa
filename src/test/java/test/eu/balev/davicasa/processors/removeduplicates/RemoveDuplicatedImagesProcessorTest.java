package test.eu.balev.davicasa.processors.removeduplicates;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.drew.imaging.ImageProcessingException;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import eu.balev.davicasa.ImageFileFilter;
import eu.balev.davicasa.MD5Calculator;
import eu.balev.davicasa.processors.removeduplicates.RemoveDuplicatedImagesProcessor;
import eu.balev.davicasa.util.ImageFinder;

@RunWith(MockitoJUnitRunner.class)
public class RemoveDuplicatedImagesProcessorTest
{
	private RemoveDuplicatedImagesProcessor processorToTest;

	@Mock
	File mockFile1, mockFile2;
	@Mock
	File mockDir;

	@Before
	public void setUp() throws IOException, ImageProcessingException
	{
		processorToTest = new RemoveDuplicatedImagesProcessor(mockDir);

		processorToTest.setDryRun(false);

		Injector injector = Guice.createInjector(new DavicasaTestModule());
		injector.injectMembers(processorToTest);
	}

	@Test
	public void testProcessIdenticalFilesOneFile()
	{
		List<File> oneFileInAList = new LinkedList<File>();
		oneFileInAList.add(mockFile1);

		processorToTest.processIdenticalFiles(oneFileInAList,
				identityComparator);

		Mockito.verify(mockFile1, Mockito.times(0)).delete();
	}

	@Test
	public void testTwoIdenticalFiles()
	{
		List<File> filesInAList = new LinkedList<File>();
		filesInAList.add(mockFile1);
		filesInAList.add(mockFile1);

		processorToTest.processIdenticalFiles(filesInAList, identityComparator);

		Mockito.verify(mockFile1, Mockito.times(1)).delete();
	}

	@Test
	public void testThreeFilesOnlyTwoIdentical()
	{
		List<File> filesInAList = new LinkedList<File>();
		filesInAList.add(mockFile1);
		filesInAList.add(mockFile1);
		filesInAList.add(mockFile2);

		processorToTest.processIdenticalFiles(filesInAList, identityComparator);

		Mockito.verify(mockFile1, Mockito.times(1)).delete();
		Mockito.verify(mockFile2, Mockito.times(0)).delete();
	}

	private class DavicasaTestModule extends AbstractModule
	{

		@Override
		protected void configure()
		{
			bind(FileFilter.class)
					.annotatedWith(Names.named("ImageFileFilter")).to(
							ImageFileFilter.class);

			bind(ImageFinder.class).toInstance(new TestImageFinder());
			bind(MD5Calculator.class).toInstance(new TestMD5Calculator());
		}

	}

	private class TestMD5Calculator implements MD5Calculator
	{

		@Override
		public String getMD5Sum(File file) throws IOException
		{
			//just for testting purposes
			return file.getName();
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

	private Comparator<File> identityComparator = new Comparator<File>()
	{

		@Override
		public int compare(File file1, File file2)
		{
			if (file1 == null)
				return -1;
			else if (file2 == null)
				return 1;
			return file1 == file2 ? 0 : 1;
		}
	};
}
