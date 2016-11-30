package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

import eu.balev.davicasa.inject.InjectLogger;

public class ImageCreateDateExtractor
{
	@InjectLogger
	private Logger logger;
	
	public Date getImageDate(File image)
	{
		Metadata metadata = null;
		
		try
		{
			metadata = ImageMetadataReader.readMetadata(image);
		}
		catch (ImageProcessingException|IOException e)
		{
			logger.error("Unable to extract image data for image {}. An error occured... Skipping...", e);
			return null;
		}
		

		ExifIFD0Directory exifDir = metadata
				.getDirectory(ExifIFD0Directory.class);
		
		if (exifDir == null)
		{
			return null;
		}
		else
		{
			return exifDir.getDate(ExifIFD0Directory.TAG_DATETIME);
		}
	}
}
