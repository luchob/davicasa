package test.eu.balev.davicasa.processors;

import java.io.File;
import java.io.IOException;

import eu.balev.davicasa.MD5Calculator;

/**
 * An MD5 calculator for testing purposes.
 * It does not calculate real MD5 sums, instead it returns
 * just the name of the passed file 
 */
public class TestMD5Calculator implements MD5Calculator
{

	@Override
	public String getMD5Sum(File file) throws IOException
	{
		return file.getName();
	}

}
