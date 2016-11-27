package test.eu.balev.davicasa.processors;

import java.io.FileFilter;
import java.util.Comparator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

import eu.balev.davicasa.inject.SLF4JTypeListener;
import eu.balev.davicasa.processors.ImageProcessor;
import eu.balev.davicasa.processors.ImageProcessorFactoryImpl;
import eu.balev.davicasa.processors.copyrename.CopyAndRenameImageProcessor;
import eu.balev.davicasa.processors.removeduplicates.RemoveDuplicatedImagesProcessor;
import eu.balev.davicasa.util.impl.FileIdentityComparator;
import eu.balev.davicasa.util.impl.ImageFileFilter;

public class ImageProcessorFactoryImplTest
{

	private ImageProcessorFactoryImpl factoryToTest;

	@Before
	public void setUp()
	{
		factoryToTest = new ImageProcessorFactoryImpl();

		Injector injector = Guice.createInjector(new DavicasaTestModule());
		injector.injectMembers(factoryToTest);
	}

	@Test
	public void testNoProcessorCreated() throws ParseException
	{
		CommandLine line = Mockito.mock(CommandLine.class);

		ImageProcessor imageProcessor = factoryToTest.tryCreateProcessor(line);

		Assert.assertNull("The image processors should be null", imageProcessor);

	}

	@Test
	public void testCreateCopyAndRenameImageProcessor()
	{
		CommandLine line = Mockito.mock(CommandLine.class);

		Mockito.when(line.hasOption("copyrename")).thenReturn(true);
		Mockito.when(line.hasOption("sourcedir")).thenReturn(true);
		Mockito.when(line.hasOption("targetdir")).thenReturn(true);

		Mockito.when(line.getOptionValue("sourcedir")).thenReturn("src");
		Mockito.when(line.getOptionValue("targetdir")).thenReturn("target");

		ImageProcessor imageProcessor = factoryToTest.tryCreateProcessor(line);

		Assert.assertNotNull("The image processors should not be null",
				imageProcessor);
		Assert.assertNotNull(
				"Copy and rename Image processor should be created!",
				CopyAndRenameImageProcessor.class.equals(imageProcessor
						.getClass()));
	}

	@Test
	public void testRemoveDuplicatedImagesProcessor()
	{
		CommandLine line = Mockito.mock(CommandLine.class);

		Mockito.when(line.hasOption("cleansrcduplicates")).thenReturn(true);
		Mockito.when(line.hasOption("sourcedir")).thenReturn(true);
		Mockito.when(line.hasOption("targetdir")).thenReturn(true);

		Mockito.when(line.getOptionValue("sourcedir")).thenReturn("src");
		Mockito.when(line.getOptionValue("targetdir")).thenReturn("target");

		ImageProcessor imageProcessor = factoryToTest.tryCreateProcessor(line);

		Assert.assertNotNull("The image processors should not be null",
				imageProcessor);
		Assert.assertNotNull(
				"Remove duplicate images processor should be created!",
				RemoveDuplicatedImagesProcessor.class.equals(imageProcessor
						.getClass()));
	}

	private class DavicasaTestModule extends AbstractModule
	{

		@Override
		protected void configure()
		{
			bind(FileFilter.class)
					.annotatedWith(Names.named("ImageFileFilter")).to(
							ImageFileFilter.class);
			bind(Comparator.class).annotatedWith(
					Names.named("FileIdentityComparator")).to(
					FileIdentityComparator.class);
			bindListener(Matchers.any(), new SLF4JTypeListener());
		}

	}

}
