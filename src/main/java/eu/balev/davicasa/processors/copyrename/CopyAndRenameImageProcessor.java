package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.drew.imaging.ImageProcessingException;

import eu.balev.davicasa.inject.InjectLogger;
import eu.balev.davicasa.processors.ImageProcessorBase;
import eu.balev.davicasa.util.ImageFinder;

public class CopyAndRenameImageProcessor extends ImageProcessorBase
{
	@InjectLogger
	private Logger logger;
	
	private final File targetDir;
	
	@Inject 
	private ImageFinder imageFinder;
	
	@Inject 
	private ImageCreateDateExtractor dateExtractor;
	
	@Inject FileRenameUtils fileRenameUtils;
	
	public CopyAndRenameImageProcessor(File sourceDir, File targetDir)
	{
		Objects.requireNonNull(sourceDir, "The source dir cannot be null!");
		Objects.requireNonNull(targetDir, "The target dir cannot be null!");

		setSourceDir(sourceDir);

		this.targetDir = targetDir;
	}

	@Override
	public void process() throws IOException
	{
		logger.info(
				"Processing images from source folder {}. " +
				"The processor will copy or move the images to {} and then rename them following a " +
				"certain pattern. Dry run enabled - {}.",
				getSourceDir().getAbsolutePath(), targetDir.getAbsoluteFile(),
				isDryRun());

		fileRenameUtils.init(targetDir, isDryRun());
		
		long start = System.currentTimeMillis();

		List<File> images = imageFinder.listImages(getSourceDir());

		logger.info("Found {} images for processing...", images.size());

		for (File anImage : images)
		{
			Date imageDate = null;
			try
			{
				imageDate = dateExtractor.getImageDate(anImage);
			}
			catch (ImageProcessingException e)
			{

				logger.error(
						"Unable to read the metadata from image {}. Skipping... Reason: ",
						anImage, e);
				continue;
			}

			if (imageDate != null)
			{
				fileRenameUtils.processImageFile(anImage, imageDate);
			}
			else
			{
				logger.error(
						"There is no date in the exif information of image {}. Skipping...",
						anImage.getAbsolutePath());
			}
		}

		logger.info(
				"Finishing the processing. {} image(s) were processed in {} millis.",
				images.size(), System.currentTimeMillis() - start);
	}
}
