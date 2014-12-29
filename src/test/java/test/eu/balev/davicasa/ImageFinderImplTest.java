package test.eu.balev.davicasa;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

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
import com.google.inject.name.Names;

import eu.balev.davicasa.ImageFinderImpl;

@RunWith(MockitoJUnitRunner.class)
public class ImageFinderImplTest
{
	private ImageFinderImpl imageFinderToTest;

	@Mock
	File dirMock, subDirMock;
	@Mock
	File img1Mock, img2Mock;
	@Mock
	File nonImgMock;

	@Before
	public void setUp()
	{
		Mockito.when(dirMock.isDirectory()).thenReturn(true);
		Mockito.when(dirMock.exists()).thenReturn(true);
		Mockito.when(subDirMock.isDirectory()).thenReturn(true);
		
		Mockito.when(dirMock.listFiles()).thenReturn(new File[]{subDirMock});
		Mockito.when(subDirMock.listFiles()).thenReturn(new File[]{img1Mock, img2Mock, nonImgMock});

		imageFinderToTest = new ImageFinderImpl();

		Injector injector = Guice.createInjector(new DavicasaTestModule());
		injector.injectMembers(imageFinderToTest);
	}

	@Test
	public void testImagesRetrieved()
	{
		List<File> allImages = imageFinderToTest.listImages(dirMock);

		Assert.assertEquals("Two images should have been retrieved!", 2,
				allImages.size());

		Assert.assertTrue("the first image should be availabale",
				allImages.contains(img1Mock));
		Assert.assertTrue("the second image should be availabale",
				allImages.contains(img2Mock));
	}

	private class DavicasaTestModule extends AbstractModule
	{

		@Override
		protected void configure()
		{
			bind(FileFilter.class)
					.annotatedWith(Names.named("ImageFileFilter")).toInstance(
							new TestImageFilter());
		}

	}

	private class TestImageFilter implements FileFilter
	{
		@Override
		public boolean accept(File file)
		{
			return file == img1Mock || file == img2Mock;
		}
	}

}
