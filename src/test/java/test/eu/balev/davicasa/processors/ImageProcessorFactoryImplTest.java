package test.eu.balev.davicasa.processors;

import static org.mockito.Mockito.when;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;

import eu.balev.davicasa.inject.SLF4JTypeListener;
import eu.balev.davicasa.processors.ImageProcessor;
import eu.balev.davicasa.processors.ImageProcessorFactoryImpl;
import eu.balev.davicasa.processors.copyrename.CopyAndRenameImageProcessor;
import eu.balev.davicasa.processors.removeduplicates.RemoveDuplicatedImagesProcessor;

@RunWith(MockitoJUnitRunner.class)
public class ImageProcessorFactoryImplTest {
	private ImageProcessorFactoryImpl factoryToTest;

	@Mock
	private CommandLine mockLine;

	@Before
	public void setUp() {
		factoryToTest = new ImageProcessorFactoryImpl();

		Injector injector = Guice.createInjector(new DavicasaTestModule());
		injector.injectMembers(factoryToTest);
	}

	@Test
	public void testNoOptions() throws ParseException {
		assertNoProcessor();
	}

	@Test
	public void testExclusiveOptions() {
		when(mockLine.hasOption("copyrename")).thenReturn(true);
		when(mockLine.hasOption("cleansrcduplicates")).thenReturn(true);

		assertNoProcessor();
	}

	@Test
	public void testRemoveDuplicatedImagesProcessorNoSourceDir() {
		when(mockLine.hasOption("cleansrcduplicates")).thenReturn(true);
		when(mockLine.hasOption("sourcedir")).thenReturn(false);

		assertNoProcessor();
	}
	
	@Test
	public void testCreateCopyAndRenameImageProcessorNoSrc() {

		when(mockLine.hasOption("copyrename")).thenReturn(true);
		when(mockLine.hasOption("sourcedir")).thenReturn(false);
		when(mockLine.hasOption("targetdir")).thenReturn(true);

		assertNoProcessor();
	}
	
	@Test
	public void testCreateCopyAndRenameImageProcessorNoTarget() {

		when(mockLine.hasOption("copyrename")).thenReturn(true);
		when(mockLine.hasOption("sourcedir")).thenReturn(true);
		when(mockLine.hasOption("targetdir")).thenReturn(false);

		assertNoProcessor();
	}


	private void assertNoProcessor() {
		ImageProcessor imageProcessor = factoryToTest
				.tryCreateProcessor(mockLine);

		Assert.assertNull(imageProcessor);
	}

	@Test
	public void testCreateCopyAndRenameImageProcessor() {

		when(mockLine.hasOption("copyrename")).thenReturn(true);
		when(mockLine.hasOption("sourcedir")).thenReturn(true);
		when(mockLine.hasOption("targetdir")).thenReturn(true);

		when(mockLine.getOptionValue("sourcedir")).thenReturn("src");
		when(mockLine.getOptionValue("targetdir")).thenReturn("target");

		ImageProcessor imageProcessor = factoryToTest
				.tryCreateProcessor(mockLine);

		Assert.assertNotNull("The image processors should not be null",
				imageProcessor);
		Assert.assertNotNull(
				"Copy and rename Image processor should be created!",
				CopyAndRenameImageProcessor.class.equals(imageProcessor
						.getClass()));
	}

	@Test
	public void testRemoveDuplicatedImagesProcessor() {
		when(mockLine.hasOption("cleansrcduplicates")).thenReturn(true);
		when(mockLine.hasOption("sourcedir")).thenReturn(true);
		when(mockLine.hasOption("targetdir")).thenReturn(true);

		when(mockLine.getOptionValue("sourcedir")).thenReturn("src");
		when(mockLine.getOptionValue("targetdir")).thenReturn("target");

		ImageProcessor imageProcessor = factoryToTest
				.tryCreateProcessor(mockLine);

		Assert.assertNotNull("The image processors should not be null",
				imageProcessor);
		Assert.assertNotNull(
				"Remove duplicate images processor should be created!",
				RemoveDuplicatedImagesProcessor.class.equals(imageProcessor
						.getClass()));
	}

	private class DavicasaTestModule extends AbstractModule {
		@Override
		protected void configure() {
			bindListener(Matchers.any(), new SLF4JTypeListener());
		}

	}

}
