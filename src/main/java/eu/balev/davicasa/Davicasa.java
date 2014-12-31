package eu.balev.davicasa;

import javax.inject.Inject;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import eu.balev.davicasa.processors.ImageProcessor;

public class Davicasa
{
	private static Logger logger = LoggerFactory.getLogger(Davicasa.class);
	
	@Inject ImageProcessorFactory factory;

	public static void main(String[] args)
	{
		CommandLine line = null;

		CommandLineParser parser = new GnuParser();
		Options options = buildOptions();

		try
		{
			line = parser.parse(options, args);
		}
		catch (ParseException pe)
		{
			logger.error(pe.getMessage(), pe);

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Davicasa", options);

			System.exit(-1);
		}
		
		printPassedArgs(line);
		
		
		Davicasa davicasa = new Davicasa();
		davicasa.process(line, Guice.createInjector(new DavicasaModule()));
	}
	
	private static void printPassedArgs(CommandLine line)
	{
		Option[] lineOptions = line.getOptions();

		StringBuilder sb = new StringBuilder();
		sb.append(System.lineSeparator());
		for (Option option : lineOptions)
		{
			sb.append(option.getArgName())
					.append(": ")
					.append(option.getValue() != null ? option.getValue()
							: "[available]");
			sb.append(System.lineSeparator());
		}

		logger.info("The following arguments had been passed to the tool: {}",
				sb.toString());
		
	}

	public void process(CommandLine line, Injector injector)
	{
		injector.injectMembers(this);
		
		ImageProcessor processor = factory.tryCreateProcessor(line);

		if (processor == null)
		{
			logger.error("Sorry. Unable to find suitable image processor. Terminating the processing...");
		}
		else
		{
			injector.injectMembers(processor);
			logger.info("Found prooper image processor. Starting image processing...");
			logger.info("Running processor of class {} ", processor.getClass());
			try
			{
				processor.process();
			}
			catch (Exception ex)
			{
				logger.error(
						"The processor failed. Terminating the processing of images. Reason: ",
						ex);
			}
		}
	}

	@SuppressWarnings("static-access")
	// CLI API quirks...
	private static Options buildOptions()
	{
		Options res = new Options();

		//all processors have a source dir, 
		//and might be dry runned
		Option sourceDir = OptionBuilder
				.withArgName("sourcedir")
				.hasArg()
				.isRequired()
				.withDescription(
						"the directory where the photos to be processed are located")
				.create("sourcedir");
		
		Option dryrun = OptionBuilder
				.withArgName("dryrun")
				.hasArg(false)
				.withDescription(
						"if the tool should make a dry run - this means that no changes will be made")
				.create("dryrun");
		
		//a task for cleaning the duplicates
		Option cleanDuplicates = OptionBuilder
				.withArgName("cleansrcduplicates")
				.hasArg(false)
				.withDescription(
						"Removes the duplicate image files in the source folder")
				.create("cleansrcduplicates");

		//a task for copy and rename of existing images
		Option copyrename = OptionBuilder
				.withArgName("copyrename")
				.hasArg(false)
				.withDescription(
						"Copies the files from the source folder into the target and renames the source files according to a time pattern")
				.create("copyrename");
		
		Option targetDir = OptionBuilder
				.withArgName("targetdir")
				.hasArg()
				.withDescription(
						"the directory where the photos to be processed should be copied")
				.create("targetdir");

		res.addOption(sourceDir);
		res.addOption(copyrename);
		res.addOption(dryrun);
		res.addOption(cleanDuplicates);
		res.addOption(targetDir);

		return res;
	}
}
