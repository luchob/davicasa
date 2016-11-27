package eu.balev.davicasa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

/**
 * The entry point of the tool. Start with option <code>--help</code> to get
 * detailed instructions.
 * 
 */
public class Davicasa
{
	private static Logger logger = LoggerFactory.getLogger(Davicasa.class);

	private static String HELP_FILE_RESOURCE = "help.txt";
	private static String HELP_FILE_ENCODING = "UTF-8";

	@Inject
	private ImageProcessorFactory factory;

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
			logger.error(
					"Unable to parse the passed parameters. Reason is: {}",
					pe.getMessage());

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Davicasa", options);

			System.exit(-1);
		}

		printPassedArgs(line);

		if (line.getOptions().length == 0
				|| line.hasOption(CLOptionsEnum.HELP.getName()))
		{
			printHelpMessages(options);
		}
		else
		{
			Davicasa davicasa = new Davicasa();
			davicasa.process(line, Guice.createInjector(new DavicasaModule()));
		}
	}

	private static void printHelpMessages(Options options)
	{

		InputStream is = Davicasa.class.getClassLoader().getResourceAsStream(
				HELP_FILE_RESOURCE);

		if (is != null)
		{
			try
			{
				String helpMessage = readFully(is, HELP_FILE_ENCODING);
				logger.info(helpMessage);
			}
			catch (IOException e)
			{
				logger.error(e.getMessage(), e);
			}
			finally
			{
				try
				{
					is.close();
				}
				catch (IOException ioe)
				{
					logger.error(ioe.getMessage(), ioe);
				}
			}

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Davicasa", options);
		}
		else
		{
			logger.error("Unable to locate the help file! The application is not deployed properly...");
		}
		System.exit(-1);
	}

	private static String readFully(InputStream inputStream, String encoding)
			throws IOException
	{
		return new String(readFully(inputStream), encoding);
	}

	private static byte[] readFully(InputStream inputStream) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = inputStream.read(buffer)) != -1)
		{
			baos.write(buffer, 0, length);
		}
		return baos.toByteArray();
	}

	private static void printPassedArgs(CommandLine line)
	{
		Option[] lineOptions = line.getOptions();

		if (lineOptions != null && lineOptions.length > 0)
		{
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
			logger.info(
					"Trying to start the tool with the following parameters: {}",
					sb.toString());
		}
		else
		{
			logger.info("Starting the tool with no parameters... ");
		}

	}

	public void process(CommandLine line, Injector injector)
	{
		injector.injectMembers(this);

		ImageProcessor processor = factory.tryCreateProcessor(line);

		if (processor == null)
		{
			logger.error("Alas - it will not work... Cannot create a processor based on the current parameters... Call the tool with the help option.");
		}
		else
		{
			injector.injectMembers(processor);
			logger.info("Found proper image processor. Starting image processing...");
			logger.info("Running processor of class {} ", processor.getClass()
					.getSimpleName());
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
			Option anOption = OptionBuilder.withArgName(entry.getName())
					.hasArg(entry.hasArg()).isRequired(false)
					.withDescription(entry.getDescription())
					.create(entry.getName());

			res.addOption(anOption);
		}

		return res;
	}
}
