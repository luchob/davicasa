package eu.balev.davicasa.processors.copyrename;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import eu.balev.davicasa.processors.TestFileIdentityComparator;
import eu.balev.davicasa.util.ImageFileFilter;

@RunWith(MockitoJUnitRunner.class)
public class FreeIndexCacheTest
{
	private FreeIndexCache cacheToTest;

	@Mock
	private File mockImageTargetDir1, mockImageTargetDir2;

	private Date testImageDate = new Date();
	private String testExt = "TEST";

	@Mock
	private FileNamingUtils mockNamingUtils;
	
	@Mock
	private File mockImageFileNotExist, mockImageFileExist;

	@Before
	public void setUp()
	{
		cacheToTest = new FreeIndexCache();

		Injector injector = Guice.createInjector(new DavicasaTestModule());
		injector.injectMembers(cacheToTest);
		
		Mockito.when(mockImageFileNotExist.exists()).thenReturn(Boolean.FALSE);
		Mockito.when(mockImageFileExist.exists()).thenReturn(Boolean.TRUE);
		
		for (int i = 1; i < 10; i++)
		{
			Mockito.when(mockNamingUtils.getImageFileName(testImageDate, i, testExt)).thenReturn(String.valueOf(i));
			Mockito.when(mockNamingUtils.getImageFile(mockImageTargetDir1, String.valueOf(i))).thenReturn(mockImageFileNotExist);
			Mockito.when(mockNamingUtils.getImageFile(mockImageTargetDir2, String.valueOf(i))).thenReturn(mockImageFileNotExist);
		}
	}
	
	@Test
	public void testHole()
	{
		Mockito.when(mockNamingUtils.getImageFileName(testImageDate, 5, testExt)).thenReturn(String.valueOf(5));
		Mockito.when(mockNamingUtils.getImageFile(mockImageTargetDir1, String.valueOf(5))).thenReturn(mockImageFileExist);
		
		for (int i=1; i<6; i++)
		{
			cacheToTest.getAndUpdateFreeIndex(mockImageTargetDir1, testImageDate, testExt);
		}
		
		int actual = cacheToTest.getAndUpdateFreeIndex(mockImageTargetDir1, testImageDate, testExt);
		int expected = 7;
		
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testEmptyDir()
	{
		for (int i=1; i<10; i++)
		{
			int actual = cacheToTest.getAndUpdateFreeIndex(mockImageTargetDir1, testImageDate, testExt);
			int expected = i;
			Assert.assertEquals(expected, actual);
			
			actual = cacheToTest.getAndUpdateFreeIndex(mockImageTargetDir2, testImageDate, testExt);
			expected = i;
			Assert.assertEquals(expected, actual);
		}
	}
	
	private class DavicasaTestModule extends AbstractModule
	{
		@Override
		protected void configure()
		{
			bind(new TypeLiteral<Comparator<File>>()
			{
			}).annotatedWith(Names.named("FileIdentityComparator")).to(
					TestFileIdentityComparator.class);

			bind(FileFilter.class)
					.annotatedWith(Names.named("ImageFileFilter")).to(
							ImageFileFilter.class);

			bind(FileNamingUtils.class).toInstance(mockNamingUtils);
		}
	}
}
