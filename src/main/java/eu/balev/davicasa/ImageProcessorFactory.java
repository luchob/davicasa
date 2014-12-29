package eu.balev.davicasa;

import org.apache.commons.cli.CommandLine;

import eu.balev.davicasa.processors.ImageProcessor;

public interface ImageProcessorFactory {
	
	public ImageProcessor tryCreateProcessor(CommandLine line);
	
}
