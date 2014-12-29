package eu.balev.davicasa;

import java.io.File;
import java.io.IOException;

public interface MD5Calculator
{
	public String getMD5Sum(File file) throws IOException;
}
