package eu.balev.davicasa.processors;

import org.apache.commons.cli.CommandLine;

public interface ImageProcessorFactory {
	
	public ImageProcessor tryCreateProcessor(CommandLine line);
	
}
