package eu.balev.davicasa.processors.removeduplicates;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.balev.davicasa.ImageFinder;
import eu.balev.davicasa.MD5Calculator;
import eu.balev.davicasa.processors.ImageProcessorBase;
import eu.balev.davicasa.processors.copyrename.CopyAndRenameImageProcessor;
import eu.balev.davicasa.util.FileIdentityComparator;

public class RemoveDuplicatedImagesProcessor extends ImageProcessorBase
{

	private final Logger logger = LoggerFactory
			.getLogger(CopyAndRenameImageProcessor.class);
	
	@Inject ImageFinder imageFinder;
	
	@Inject MD5Calculator md5Calc;

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

		Map<String, List<File>> filesWithSameMD5Sums = new HashMap<>();

		List<File> images = imageFinder.listImages(getSourceDir());

		logger.info("Found {} image(s) for processing...", images.size());

		for (File anImage : images)
		{
			String md5Sum = md5Calc.getMD5Sum(anImage);

			if (filesWithSameMD5Sums.containsKey(md5Sum))
			{
				filesWithSameMD5Sums.get(md5Sum).add(anImage);
			}
			else
			{
				List<File> files = new LinkedList<>();
				files.add(anImage);
				filesWithSameMD5Sums.put(md5Sum, files);
			}

		}

		if (filesWithSameMD5Sums.size() == images.size())
		{
			logger.info("There are no duplicates in the files...");
		}
		else
		{
			logger.info("There are possible duplicates in the processed files. Checking...");

			for (Map.Entry<String, List<File>> entry : filesWithSameMD5Sums
					.entrySet())
			{
				List<File> files = entry.getValue();
				if (files.size() > 1)
				{
					List<File> filesCopy = new LinkedList<>();
					filesCopy.addAll(files);
					processIdenticalFiles(filesCopy, new FileIdentityComparator());
				}
			}
		}

		logger.info(
				"Finishing the processing. {} image(s) processed in {} milliseconds ",
				images.size(), System.currentTimeMillis() - start);

	}

	public void processIdenticalFiles(List<File> files, Comparator<File> fileComparator)
	{
		if (files.size() < 2)
		{
			files.clear();
			return;
		}

		File firstFile = files.remove(0);

		Iterator<File> filesIter = files.iterator();
		while (filesIter.hasNext())
		{
			File aFile = filesIter.next();

			if (fileComparator.compare(firstFile, aFile) == 0)
			{
				logger.info(
						"The following files are identical {} {}. The last will be removed. Dry run enabled - {}. ",
						firstFile.getAbsolutePath(), aFile.getAbsolutePath(),
						isDryRun());

				if (!isDryRun())
				{
					if (!aFile.delete())
					{
						logger.error("The file {} could not be deleted, perhaps it is locked...", aFile.getAbsolutePath());
					}
				}

				filesIter.remove();
			}
		}

		processIdenticalFiles(files, fileComparator);
	}
}
