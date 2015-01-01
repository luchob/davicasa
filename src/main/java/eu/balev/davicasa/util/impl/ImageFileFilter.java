package eu.balev.davicasa.util.impl;

import java.io.File;
import java.io.FileFilter;

/**
 * This file filter implementation filters only the 
 * currently supported image types.
 */
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
