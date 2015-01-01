package test.eu.balev.davicasa.processors;

import java.io.File;
import java.util.Comparator;

public class TestFileIdentityComparator implements Comparator<File>
{
	@Override
	public int compare(File file1, File file2)
	{
		if (file1.getName() == null || file2.getName() == null)
		{
			return -1;
		}
		else
		{
			return file1.getName().compareTo(file2.getName());
		}
	}

}
