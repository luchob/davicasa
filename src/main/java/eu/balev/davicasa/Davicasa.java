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

import eu.balev.davicasa.processors.CLOptionsEnum;
import eu.balev.davicasa.processors.ImageProcessor;
import eu.balev.davicasa.processors.ImageProcessorFactory;

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

		for (CLOptionsEnum entry : CLOptionsEnum.values())
		{
			Option anOption = OptionBuilder
					.withArgName(entry.getName())
					.hasArg(entry.hasArg())
					.isRequired(entry.isRequired())
					.withDescription(entry.getDescription())
					.create(entry.getName());
			
			res.addOption(anOption);
		}
		
		return res;
	}
}
