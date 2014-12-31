package test.eu.balev.davicasa;

import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import eu.balev.davicasa.Davicasa;
import eu.balev.davicasa.ImageProcessorFactory;
import eu.balev.davicasa.processors.ImageProcessor;

@RunWith(MockitoJUnitRunner.class)
public class DavicasaTest {
	
	private Davicasa davicasaToTest = new Davicasa();
	
	@Mock ImageProcessorFactory mockImageProcessorFactory;
	@Mock CommandLine mockCommandLine;
	
	@Test
	public void testNoProcessorNoException()
	{
		davicasaToTest.process(mockCommandLine, Guice.createInjector(new DavicasaTestModule()));
	}
	
	@Test
	public void testProcessorCalled() throws IOException
	{
		ImageProcessor mockProcessor = Mockito.mock(ImageProcessor.class);
		
		Mockito.when(mockImageProcessorFactory.tryCreateProcessor(mockCommandLine)).thenReturn(mockProcessor);
		
		davicasaToTest.process(mockCommandLine, Guice.createInjector(new DavicasaTestModule()));
		
		Mockito.verify(mockProcessor, Mockito.times(1)).process();
		
	}
	
	private class DavicasaTestModule extends AbstractModule
	{

		@Override
		protected void configure()
		{
			bind(ImageProcessorFactory.class).toInstance(mockImageProcessorFactory);
		}

	}

}
