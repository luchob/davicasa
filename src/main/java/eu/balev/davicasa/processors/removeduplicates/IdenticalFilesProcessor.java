package eu.balev.davicasa.processors.removeduplicates;

import java.io.File;
import java.util.Objects;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.google.inject.assistedinject.Assisted;

import eu.balev.davicasa.inject.InjectLogger;

/**
 * Processes a list with identical files.
 */
public class IdenticalFilesProcessor extends IdenticalObjectsProcessor<File>
{
	@InjectLogger
	private Logger logger;
	
	private final boolean dryRun;

	@Inject
	IdenticalFilesProcessor(@Assisted Boolean dryRun)
	{
		Objects.requireNonNull(dryRun, "Please provide a value for the dry run option...");
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
