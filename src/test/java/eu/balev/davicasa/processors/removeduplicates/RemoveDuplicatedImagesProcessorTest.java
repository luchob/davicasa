package eu.balev.davicasa.processors.removeduplicates;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

import eu.balev.davicasa.inject.SLF4JTypeListener;
import eu.balev.davicasa.util.ImageFinder;
import eu.balev.davicasa.util.ImageHashCalculator;

@RunWith(MockitoJUnitRunner.class)
public class RemoveDuplicatedImagesProcessorTest
{
	private RemoveDuplicatedImagesProcessor processorToTest;

	@Mock
	private File mockSourceDir;

	@Mock
	private ImageFinder mockImageFinder;

	@Mock
	private ImageHashCalculator mockHashCalc;

	@Mock
	private IdenticalFilesProcessorFactory mockIdenticalFileProcessorFactory;

	@Mock
	private IdenticalObjectsProcessor<File> identicalObjectsProcessor;

	@Mock
	private File mockFile1, mockFile2;

	private Comparator<File> testComparator = (f1, f2) -> f1 == f2 ? 0 : -1;

	@Before
	public void setUp() throws IOException
	{
		processorToTest = new RemoveDuplicatedImagesProcessor(mockSourceDir);

		when(mockHashCalc.getHashSum(mockFile1)).thenReturn("hash1");
		when(mockHashCalc.getHashSum(mockFile1)).thenReturn("hash2");
		when(mockIdenticalFileProcessorFactory.create(Boolean.FALSE))
				.thenReturn(identicalObjectsProcessor);

		Injector injector = Guice.createInjector(new DavicasaTestModule());

		injector.injectMembers(processorToTest);
	}

	@Test
	public void testNoIdenticals() throws IOException
	{
		when(mockImageFinder.listImages(mockSourceDir)).thenReturn(
				Arrays.asList(mockFile1, mockFile2));

		processorToTest.process();

		Mockito.verifyZeroInteractions(mockIdenticalFileProcessorFactory);
	}

	@Test
	public void testIdenticals() throws IOException
	{
		List<File> files = Arrays.asList(mockFile1, mockFile1, mockFile1,
				mockFile2);
		when(mockImageFinder.listImages(mockSourceDir)).thenReturn(files);

		processorToTest.process();

		Mockito.verify(identicalObjectsProcessor, Mockito.times(1))
				.processIdenticalObjects(
						Arrays.asList(mockFile1, mockFile1, mockFile1),
						testComparator);
	}

	private class DavicasaTestModule extends AbstractModule
	{
		@Override
		protected void configure()
		{
			bind(ImageFinder.class).toInstance(mockImageFinder);
			bind(ImageHashCalculator.class).toInstance(mockHashCalc);
			bind(IdenticalFilesProcessorFactory.class).toInstance(
					mockIdenticalFileProcessorFactory);
			bindListener(Matchers.any(), new SLF4JTypeListener());
			bind(new TypeLiteral<Comparator<File>>()
			{
			}).annotatedWith(Names.named("FileIdentityComparator")).toInstance(
					testComparator);

		}
	}
}
