package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

public class ImageCreateDateExtractor
{

	public Date getImageDate(File image) throws ImageProcessingException,
			IOException
	{
		Metadata metadata = ImageMetadataReader.readMetadata(image);

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
