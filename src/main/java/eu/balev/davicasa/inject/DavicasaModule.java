package eu.balev.davicasa.inject;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

import eu.balev.davicasa.processors.ImageProcessorFactory;
import eu.balev.davicasa.processors.ImageProcessorFactoryImpl;
import eu.balev.davicasa.util.ImageFinder;
import eu.balev.davicasa.util.ImageHashCalculator;
import eu.balev.davicasa.util.impl.FileIdentityComparator;
import eu.balev.davicasa.util.impl.ImageFileFilter;
import eu.balev.davicasa.util.impl.ImageFinderImpl;
import eu.balev.davicasa.util.impl.ImageHashCalculatorImpl;

/**
 * A Guice module for the DaViCasa tool.
 */
public class DavicasaModule extends AbstractModule
{

	@Override
	protected void configure()
	{
		bind(ImageProcessorFactory.class).to(ImageProcessorFactoryImpl.class);
		bind(ImageFinder.class).to(ImageFinderImpl.class);
		bind(FileFilter.class).annotatedWith(Names.named("ImageFileFilter"))
				.to(ImageFileFilter.class);

		bind(new TypeLiteral<Comparator<File>>()
		{
		}).annotatedWith(Names.named("FileIdentityComparator")).to(
				FileIdentityComparator.class);

		bind(ImageHashCalculator.class).to(ImageHashCalculatorImpl.class);
		
		bindListener(Matchers.any(), new SLF4JTypeListener());
	}

}
