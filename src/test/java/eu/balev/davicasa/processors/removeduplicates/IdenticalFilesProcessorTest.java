package eu.balev.davicasa.processors.removeduplicates;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageProcessingException;

@RunWith(MockitoJUnitRunner.class)
public class IdenticalFilesProcessorTest
{
	private IdenticalFilesProcessor processorToTestNoDryRun, processorToTestDryRun;
	
	@Mock
	private File mockFile1, mockFile2;
	
	private Comparator<File> testComparator = (f1, f2) -> f1 == f2 ? 0 : 1;
	
	@Before
	public void setUp() throws IOException, ImageProcessingException
	{
		processorToTestNoDryRun = new IdenticalFilesProcessor(LoggerFactory.getLogger(IdenticalFilesProcessorTest.class), false);
		processorToTestDryRun = new IdenticalFilesProcessor(LoggerFactory.getLogger(IdenticalFilesProcessorTest.class), true);
	}
	
	@Test
	public void testProcessOneFileDryRun()
	{
		doTestProcessOneFile(processorToTestDryRun);
	}
	
	@Test
	public void testProcessOneFileNoDryRun()
	{
		doTestProcessOneFile(processorToTestNoDryRun);
	}
	
	private void doTestProcessOneFile(IdenticalFilesProcessor processor)
	{
		List<File> oneFileInAList = new LinkedList<File>();
		oneFileInAList.add(mockFile1);

		processor.processIdenticalObject(oneFileInAList,
				testComparator);

		Mockito.verify(mockFile1, Mockito.times(0)).delete();
	}
	
	@Test
	public void testTwoIdenticalFilesDryRun()
	{
		doTestTwoIdenticalFiles(processorToTestDryRun);
	}
	
	@Test
	public void testTwoIdenticalFilesNoDryRun()
	{
		doTestTwoIdenticalFiles(processorToTestNoDryRun);
	}

	private void doTestTwoIdenticalFiles(IdenticalFilesProcessor processor)
	{
		List<File> filesInAList = new LinkedList<File>();
		filesInAList.add(mockFile1);
		filesInAList.add(mockFile1);

		processor.processIdenticalObject(filesInAList, testComparator);

		Mockito.verify(mockFile1, Mockito.times(isDryRun(processor) ? 0: 1)).delete();
	}
	
	@Test
	public void testThreeFilesOnlyTwoIdenticalDryRun()
	{
		doTestThreeFilesOnlyTwoIdentical(processorToTestDryRun);
	}

	@Test
	public void testThreeFilesOnlyTwoIdenticalNoDryRun()
	{
		doTestThreeFilesOnlyTwoIdentical(processorToTestNoDryRun);
	}
	
	private void doTestThreeFilesOnlyTwoIdentical(IdenticalFilesProcessor processor)
	{
		List<File> filesInAList = new LinkedList<File>();
		filesInAList.add(mockFile1);
		filesInAList.add(mockFile1);
		filesInAList.add(mockFile2);

		processor.processIdenticalObject(filesInAList, testComparator);

		Mockito.verify(mockFile1, Mockito.times(isDryRun(processor) ? 0 : 1)).delete();
		Mockito.verify(mockFile2, Mockito.times(0)).delete();
	}
	
	private boolean isDryRun(IdenticalFilesProcessor processor)
	{
		return processor == processorToTestDryRun;
	}
}
