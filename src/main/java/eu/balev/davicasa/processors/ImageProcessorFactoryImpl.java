package eu.balev.davicasa.processors;

import static eu.balev.davicasa.processors.CLOptionsEnum.CLEAN_SRC_DUPLICATES;
import static eu.balev.davicasa.processors.CLOptionsEnum.COPY_RENAME;
import static eu.balev.davicasa.processors.CLOptionsEnum.DRY_RUN;
import static eu.balev.davicasa.processors.CLOptionsEnum.SOURCE_DIR;
import static eu.balev.davicasa.processors.CLOptionsEnum.TARGET_DIR;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;

import eu.balev.davicasa.inject.InjectLogger;
import eu.balev.davicasa.processors.copyrename.CopyAndRenameImageProcessor;
import eu.balev.davicasa.processors.removeduplicates.RemoveDuplicatedImagesProcessor;

public class ImageProcessorFactoryImpl implements ImageProcessorFactory {

	@InjectLogger
	private Logger logger;

	@Override
	public ImageProcessor tryCreateProcessor(CommandLine line) {

		if (line.hasOption(COPY_RENAME.getName())
				&& line.hasOption(CLEAN_SRC_DUPLICATES.getName())) {
			
			//we do not support both copy and rename and 
			//clean up in the same run
			printErrorMsgForSimultaneousdParams(COPY_RENAME,
					CLEAN_SRC_DUPLICATES);
			return null;
		} 
		
		ImageProcessorBase ret = null;
		
		if (line.hasOption(COPY_RENAME.getName())) {
			ret = tryCreateCopyAndRename(line);
		} else if (line.hasOption(CLEAN_SRC_DUPLICATES.getName())) {
			ret = tryCreateCleanSrcDuplicates(line);
		}

		if (ret != null) {
			ret.setDryRun(line.hasOption(DRY_RUN.getName()));
		}

		return ret;
	}

	private ImageProcessorBase tryCreateCleanSrcDuplicates(CommandLine line)
	{
		if (line.hasOption(SOURCE_DIR.getName())) {
			
			File sourceDir = new File(line.getOptionValue(
					SOURCE_DIR.getName()).toString());

			if (line.hasOption(TARGET_DIR.getName())) {
				printWarnMsgForUnusedParams(TARGET_DIR);
			}
			return new RemoveDuplicatedImagesProcessor(sourceDir);

		} else {
			printErrorMsgForRequiredParams(CLEAN_SRC_DUPLICATES, SOURCE_DIR);
			return null;
		}
	}
	
	private ImageProcessorBase tryCreateCopyAndRename(CommandLine line) {
		
		// copy and rename operation
		if (line.hasOption(SOURCE_DIR.getName())
				&& line.hasOption(TARGET_DIR.getName())) {
			File sourceDir = new File(line.getOptionValue(SOURCE_DIR.getName())
					.toString());
			File targetDir = new File(line.getOptionValue(TARGET_DIR.getName())
					.toString());

			return new CopyAndRenameImageProcessor(sourceDir, targetDir);
		} else {
			printErrorMsgForRequiredParams(COPY_RENAME, SOURCE_DIR, TARGET_DIR);
			return null;
		}
	}

	private void printWarnMsgForUnusedParams(CLOptionsEnum... unusedOptions) {

		StringBuilder sb = new StringBuilder();

		sb.append("The tool will not use some of the parameters which you specified, because they are not relevant to the requested operation. Unused are: ");
		sb.append(getCLOptionsAsString(unusedOptions));

		logger.error(sb.toString());
	}

	private void printErrorMsgForSimultaneousdParams(
			CLOptionsEnum... exclusiveOptions) {

		StringBuilder sb = new StringBuilder();

		sb.append("The following parameters are mutually exclusive and the tool cannot execute when these are provided: ");
		sb.append(getCLOptionsAsString(exclusiveOptions));

		logger.error(sb.toString());
	}

	private String getCLOptionsAsString(CLOptionsEnum... options) {

		String joinedOpts = Arrays.stream(options).map(o -> o.getName())
				.collect(Collectors.joining(", "));

		return new StringBuilder("[").append(joinedOpts).append("]").toString();
	}

	private void printErrorMsgForRequiredParams(CLOptionsEnum operation,
			CLOptionsEnum... requireds) {

		StringBuilder sb = new StringBuilder();

		sb.append("The operation [").append(operation.getName())
				.append("] requires the following option")
				.append(requireds.length > 1 ? "s" : "").append(": ");

		sb.append(getCLOptionsAsString(requireds));

		logger.error(sb.toString());
	}
}
