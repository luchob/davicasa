package test.eu.balev.davicasa.util.impl;

import java.io.File;
import java.io.IOException;

import eu.balev.davicasa.util.ImageHashCalculator;

/**
 * A hash calculator for testing purposes.
 * It does not calculate 'real' hash sums, instead it returns
 * just the name of the passed file 
 */
public class TestHashCalculator implements ImageHashCalculator
{

	@Override
	public String getHashSum(File file) throws IOException
	{
		return file.getName();
	}

}
