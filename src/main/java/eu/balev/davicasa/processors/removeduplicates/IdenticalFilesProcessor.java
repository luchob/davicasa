package eu.balev.davicasa.processors.removeduplicates;

import java.io.File;

import org.slf4j.Logger;

/**
 * Processes a list with identical files.
 */
class IdenticalFilesProcessor extends IdenticalObjectsProcessor<File>
{
	private final Logger logger;
	private final boolean dryRun;

	IdenticalFilesProcessor(Logger logger, boolean dryRun)
	{
		this.logger = logger;
		this.dryRun = dryRun;
	}

	@Override
	protected File reduce(File firstFile, File secondFile)
	{
		logger.info(
				"The following files are identical: [{}] and [{}]. The last will be removed. Dry run enabled: {}. ",
				firstFile.getAbsolutePath(), secondFile.getAbsolutePath(),
				dryRun ? "yes" : "no");

		if (!dryRun)
		{
			if (!secondFile.delete())
			{
				logger.error(
						"The file [{}] could not be deleted, perhaps it is locked or you have no permission to delete it. Skipping it...",
						secondFile.getAbsolutePath());
			}
		}

		return firstFile;
	}

	@Override
	protected void unique(File file)
	{
		logger.info(
				"The file [{}] is not unique compared with the rest of the files that were suspected to be identical. "
						+ "We consider it unique althoug this is so incredible that it is most likely a bug.",
				file.getAbsolutePath());

	}
}
