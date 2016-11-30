package eu.balev.davicasa.processors.removeduplicates;

import java.io.File;

/**
 * A factory used to create processors for identical files.
 */
public interface IdenticalFilesProcessorFactory
{
	/**
	 * Supplies instance of processor for identical files.
	 * 
	 * @param dryRun true if this processor should perform a dry run only
	 * 
	 * @return a new processor
	 */
	public IdenticalObjectsProcessor<File> create(Boolean dryRun);
}
