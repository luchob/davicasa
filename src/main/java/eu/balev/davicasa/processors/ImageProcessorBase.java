package eu.balev.davicasa.processors;

import java.io.File;

public abstract class ImageProcessorBase implements ImageProcessor
{
	private File sourceDir;
	private boolean dryRun;
	
	public void setSourceDir(File sourceDir)
	{
		this.sourceDir = sourceDir;
	}
	
	public File getSourceDir()
	{
		return sourceDir;
	}
	
	public void setDryRun(boolean dryRun)
	{
		this.dryRun = dryRun;
	}
	
	public boolean isDryRun()
	{
		return dryRun;
	}

}
