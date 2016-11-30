package eu.balev.davicasa.processors.removeduplicates;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import eu.balev.davicasa.inject.InjectLogger;
import eu.balev.davicasa.processors.ImageProcessorBase;
import eu.balev.davicasa.util.ImageFinder;
import eu.balev.davicasa.util.ImageHashCalculator;

/**
 * Removes duplicated images within a source directory.
 */
public class RemoveDuplicatedImagesProcessor extends ImageProcessorBase
{
	@InjectLogger
	private Logger logger;

	@Inject
	private ImageFinder imageFinder;

	@Inject
	private ImageHashCalculator hashCalc;
	
	@Inject 
	private IdenticalFilesProcessorFactory identicalFileProcessorFactory;
	
	@Inject
	@Named("FileIdentityComparator")
	private Comparator<File> fileComparator;

	public RemoveDuplicatedImagesProcessor(File sourceDir)
	{
		Objects.requireNonNull(sourceDir, "Source dir cannot be null!");

		super.setSourceDir(sourceDir);
	}

	@Override
	public void process() throws IOException
	{
		logger.info(
				"Processing images in source folder {}. The processor will remove identical images. Dry run enabled - {}.",
				getSourceDir().getAbsolutePath(), isDryRun());

		long start = System.currentTimeMillis();

		List<File> images = imageFinder.listImages(getSourceDir());

		logger.info("Found {} image(s) for processing...", images.size());

		Map<String, List<File>> filesWithSameHashSums = groupImagesByHashSum(images);

		if (filesWithSameHashSums.size() == images.size())
		{
			logger.info("There are no duplicates in the files...");
		}
		else
		{
			logger.info("Most likely there are duplicates in the processed files. Verifying...");

			filesWithSameHashSums.
					values().
					stream().
					filter(files -> files.size() > 1).
					forEach(this::processIdenticalFiles);
		}

		logger.info(
				"Finishing the processing. {} image(s) processed in {} milliseconds ",
				images.size(), System.currentTimeMillis() - start);

	}
	
	private Map<String, List<File>> groupImagesByHashSum(List<File> images) throws IOException
	{
		//no lambdas because of IO handling :-)
		Map<String, List<File>> filesWithSameHashSums = new HashMap<>();
		for (File anImage : images)
		{
			filesWithSameHashSums.
				computeIfAbsent(hashCalc.getHashSum(anImage), 
						k->new LinkedList<>()).add(anImage);
		}
		return filesWithSameHashSums;
	}

	private void processIdenticalFiles(List<File> files)
	{

		logger.info(
				"The following files are suspects for duplicates [{}].",
				files.stream().map(f -> f.getAbsolutePath())
						.collect(Collectors.joining(", ")));

		identicalFileProcessorFactory.
				create(isDryRun()).
				processIdenticalObjects(files, fileComparator);
	}
}
