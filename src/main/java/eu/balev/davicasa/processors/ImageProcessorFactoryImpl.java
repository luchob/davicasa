package eu.balev.davicasa.processors;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.balev.davicasa.processors.copyrename.CopyAndRenameImageProcessor;
import eu.balev.davicasa.processors.removeduplicates.RemoveDuplicatedImagesProcessor;

import static eu.balev.davicasa.processors.CLOptionsEnum.*;

public class ImageProcessorFactoryImpl implements ImageProcessorFactory
{
	private static Logger logger = LoggerFactory
			.getLogger(ImageProcessorFactoryImpl.class);

	@Override
	public ImageProcessor tryCreateProcessor(CommandLine line)
	{
		ImageProcessorBase ret = null;

		boolean dryRun = line.hasOption(DRY_RUN.getName());

		if (line.hasOption(COPY_RENAME.getName())
				&& line.hasOption(CLEAN_SRC_DUPLICATES.getName()))
		{
			printErrorMsgForSimultaneousdParams(COPY_RENAME,
					CLEAN_SRC_DUPLICATES);
		}
		else if (line.hasOption(COPY_RENAME.getName()))
		{
			// copy and rename operation
			if (line.hasOption(SOURCE_DIR.getName())
					&& line.hasOption(TARGET_DIR.getName()))
			{
				File sourceDir = new File(line.getOptionValue(
						SOURCE_DIR.getName()).toString());
				File targetDir = new File(line.getOptionValue(
						TARGET_DIR.getName()).toString());

				ret = new CopyAndRenameImageProcessor(sourceDir, targetDir);
			}
			else
			{
				printErrorMsgForRequiredParams(COPY_RENAME, SOURCE_DIR,
						TARGET_DIR);
			}
		}
		else if (line.hasOption(CLEAN_SRC_DUPLICATES.getName()))
		{
			if (line.hasOption(SOURCE_DIR.getName()))
			{
				File sourceDir = new File(line.getOptionValue(
						SOURCE_DIR.getName()).toString());

				if (line.hasOption(TARGET_DIR.getName()))
				{
					printWarnMsgForUnusedParams(TARGET_DIR);
				}
				ret = new RemoveDuplicatedImagesProcessor(sourceDir);

			}
			else
			{
				printErrorMsgForRequiredParams(CLEAN_SRC_DUPLICATES, SOURCE_DIR);
			}
		}

		if (ret != null)
		{
			ret.setDryRun(dryRun);
		}

		return ret;
	}

	private void printWarnMsgForUnusedParams(CLOptionsEnum... unusedOptions)
	{

		StringBuilder sb = new StringBuilder();

		sb.append("The tool will not use some of the parameters which you specified, because they are not relevant to the requested operation. Unused are: ");
		sb.append(getCLOptionsAsString(unusedOptions));

		logger.error(sb.toString());
	}

	private void printErrorMsgForSimultaneousdParams(
			CLOptionsEnum... exclusiveOptions)
	{

		StringBuilder sb = new StringBuilder();

		sb.append("The following parameters are mutually exclusive and the tool cannot execute when these are provided: ");
		sb.append(getCLOptionsAsString(exclusiveOptions));

		logger.error(sb.toString());
	}

	private String getCLOptionsAsString(CLOptionsEnum... options)
	{
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < options.length; i++)
		{
			sb.append(options[i].getName());
			if (i < options.length - 1)
			{
				sb.append(", ");
			}
		}
		sb.append("]");

		return sb.toString();
	}

	private void printErrorMsgForRequiredParams(CLOptionsEnum operation,
			CLOptionsEnum... requireds)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("The operation [").append(operation.getName())
				.append("] requires the following option")
				.append(requireds.length > 1 ? "s" : "").append(": ");

		sb.append(getCLOptionsAsString(requireds));
	
		logger.error(sb.toString());
	}
}
