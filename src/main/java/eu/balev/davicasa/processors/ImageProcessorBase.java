package eu.balev.davicasa.processors;

import java.io.File;
import java.util.Objects;

/**
 * A class that contains some very basic functionality common to all included
 * image processors in the tool.
 */
public abstract class ImageProcessorBase implements ImageProcessor {
	
	private File sourceDir;
	private boolean dryRun;

	/**
	 * Sets the directory where the sources for the image processor should
	 * reside.
	 * 
	 * @param sourceDir
	 *            the source dir.
	 * 
	 * @throws NullPointerException
	 *             if the source dir is null.
	 */
	public void setSourceDir(File sourceDir) {
		Objects.requireNonNull(sourceDir);

		this.sourceDir = sourceDir;
	}

	/**
	 * Returns the source directory where the sources for the image processor
	 * should reside.
	 * 
	 * @return the source directory where the sources for the image processor
	 *         should reside
	 */
	public File getSourceDir() {
		return sourceDir;
	}

	/**
	 * Sets if this processor should make a dry run. A dry run means that the
	 * processor will try to report the intended actions without modifying
	 * anything on disk.
	 * 
	 * @param dryRun
	 *            <code>true<code/> if the processor should make a dry run.
	 */
	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	/**
	 * Returns <code>true</code> if the processor should make a dry run.
	 * <code>false</code> by default.
	 * 
	 * @return <code>true</code> if the processor should make a dry run.
	 */
	public boolean isDryRun() {
		return dryRun;
	}

}
