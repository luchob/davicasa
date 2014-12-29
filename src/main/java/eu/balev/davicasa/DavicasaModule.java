package eu.balev.davicasa;

import java.io.FileFilter;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class DavicasaModule extends AbstractModule
{

	@Override
	protected void configure()
	{
		bind(ImageProcessorFactory.class).to(ImageProcessorFactoryImpl.class);
		bind(ImageFinder.class).to(ImageFinderImpl.class);
		bind(FileFilter.class).annotatedWith(Names.named("ImageFileFilter"))
				.to(ImageFileFilter.class);
		bind(MD5Calculator.class).to(MD5CalculatorImpl.class);
	}

}
