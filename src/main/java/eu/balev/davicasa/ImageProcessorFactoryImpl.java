package eu.balev.davicasa;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import eu.balev.davicasa.processors.ImageProcessor;
import eu.balev.davicasa.processors.ImageProcessorBase;
import eu.balev.davicasa.processors.copyrename.CopyAndRenameImageProcessor;
import eu.balev.davicasa.processors.removeduplicates.RemoveDuplicatedImagesProcessor;

public class ImageProcessorFactoryImpl implements ImageProcessorFactory
{
	private static Logger logger = LoggerFactory
			.getLogger(ImageProcessorFactoryImpl.class);
	
	@Override
	public ImageProcessor tryCreateProcessor(CommandLine line)
	{
		ImageProcessorBase ret = null;

		boolean dryRun = line.hasOption("dryrun");

		if (line.hasOption("copyrename"))
		{
			if (line.hasOption("sourcedir") && line.hasOption("targetdir"))
			{
				File sourceDir = new File(line.getOptionValue("sourcedir")
						.toString());
				File targetDir = new File(line.getOptionValue("targetdir")
						.toString());

				ret = new CopyAndRenameImageProcessor(sourceDir, targetDir);
			}
			else
			{
				logger.error("The [copyrename] operation requires source [sourcedir] and target [targetdir] directories.");
			}
		}
		else if (line.hasOption("cleansrcduplicates"))
		{
			if (line.hasOption("sourcedir"))
			{
				File sourceDir = new File(line.getOptionValue("sourcedir")
						.toString());

				ret = new RemoveDuplicatedImagesProcessor(sourceDir);

			}
			else
			{
				logger.error("The [cleansrcduplicates] operation requires source [sourcedir] directory.");
			}
		}

		if (ret != null)
		{
			ret.setDryRun(dryRun);
			
			Injector injector = Guice.createInjector(new DavicasaModule());
			injector.injectMembers(ret);
		}
		
		return ret;
	}
}
