package eu.balev.davicasa.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;



import static org.mockito.Mockito.when;
import eu.balev.davicasa.util.ImageFileFilter;

@RunWith(MockitoJUnitRunner.class)
public class ImageFileFilterTest {

	private ImageFileFilter filterToTest = new ImageFileFilter();
	
	@Mock
	private File mockFile;
	
	@Before
	public void setUp()
	{
		when(mockFile.isFile()).thenReturn(true);
		when(mockFile.exists()).thenReturn(true);
		when(mockFile.getName()).thenReturn("mock.jpg");
	}
	
	@Test
	public void testDirectory()
	{
		when(mockFile.isDirectory()).thenReturn(true);
		
		Assert.assertFalse(filterToTest.accept(mockFile));
	}
	
	@Test
	public void testNotFile()
	{
		when(mockFile.isFile()).thenReturn(false);
		
		Assert.assertFalse(filterToTest.accept(mockFile));
	}
	
	@Test
	public void testDoesNotExist()
	{
		when(mockFile.exists()).thenReturn(false);
		
		Assert.assertFalse(filterToTest.accept(mockFile));
	}
	
	@Test
	public void testUnsupported()
	{
		List<String> names = Arrays.asList("me.png", "me.bmp", "me.tiff", "me.svg", "me.exe");
		
		for (String name : names)
		{
			when(mockFile.getName()).thenReturn(name);
			Assert.assertFalse(filterToTest.accept(mockFile));
		}
	}
	
	@Test
	public void testAccepted()
	{
		List<String> names = Arrays.asList("me.jpg", "me.JpG", "me.Jpeg", "me.jpeg", "me.JPEG");
		
		for (String name : names)
		{
			when(mockFile.getName()).thenReturn(name);
			Assert.assertTrue(filterToTest.accept(mockFile));
		}
	}
}
