package eu.balev.davicasa;

import java.io.File;
import java.util.List;

public interface ImageFinder
{
	public List<File> listImages(File sourceDir);
}
