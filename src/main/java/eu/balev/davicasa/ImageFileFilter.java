package eu.balev.davicasa;

import java.io.File;
import java.io.FileFilter;

public class ImageFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {

		if (file.isDirectory())
		{
			return false;
		}
		
		String fileName = file.getName().toLowerCase();

		return (file.isFile() && file.exists())
				&& (fileName.endsWith("jpg") || fileName.endsWith("jpeg"));
	}

}
