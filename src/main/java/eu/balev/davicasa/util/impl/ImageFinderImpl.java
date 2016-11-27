package eu.balev.davicasa.util.impl;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import eu.balev.davicasa.processors.inject.InjectLogger;
import eu.balev.davicasa.util.ImageFinder;

public class ImageFinderImpl implements ImageFinder
{
	@InjectLogger
	private Logger logger;

	@Inject
	@Named("ImageFileFilter")
	FileFilter imageFilter;

	@Override
	public List<File> listImages(File sourceDir)
	{
		Objects.requireNonNull(sourceDir,
				"The source directory cannot be null!");

		if ((!sourceDir.exists()) || (!sourceDir.isDirectory()))
		{
			logger.error("Unable to find images either because the source directory does not exist or because " +
					"it is not a directory. Returning an empty list...");
			return Collections.emptyList();
		}

		return getImages(sourceDir, new LinkedList<File>());
	}

	private List<File> getImages(File sourceDir, List<File> allImages)
	{
		File[] allFiles = sourceDir.listFiles();

		if (allFiles != null)
		{
			for (File aFile : allFiles)
			{
				if (imageFilter.accept(aFile))
				{
					allImages.add(aFile);
				}
				else if (aFile.isDirectory())
				{
					getImages(aFile, allImages);
				}
			}
		}
		return allImages;
	}

}
