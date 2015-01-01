package eu.balev.davicasa;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import eu.balev.davicasa.util.FileIdentityComparator;
import eu.balev.davicasa.util.ImageFinder;
import eu.balev.davicasa.util.MD5Calculator;
import eu.balev.davicasa.util.impl.ImageFinderImpl;
import eu.balev.davicasa.util.impl.MD5CalculatorImpl;

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

		bind(MD5Calculator.class).to(MD5CalculatorImpl.class);
	}

}
