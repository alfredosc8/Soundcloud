package com.senselessweb.soundcloud.storage.mongodb.service;

import java.net.MalformedURLException;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.senselessweb.soundcloud.domain.library.RadioLibraryItem;
import com.senselessweb.soundcloud.storage.mongodb.ApplicationContextTestBase;
import com.senselessweb.storage.RadioStationStorageService;

/**
 * Testcases for the {@link RadioStationStorageServiceImpl}
 * 
 * @author thomas
 */
public class RadioStationStorageServiceImplTest extends ApplicationContextTestBase
{

	/**
	 * Try to store some radio stations
	 * @throws MalformedURLException 
	 */
	@Test
	public void saveRadioStations() throws MalformedURLException
	{
		final RadioStationStorageService service = this.context.getBean(RadioStationStorageService.class);
		
		Assert.assertTrue(service.getAllRadioStations().isEmpty());
		
		final RadioLibraryItem radio1 = new RadioLibraryItem("WDR 2", "https://www.wdr2-radio.de", 0, Lists.newArrayList("pop", "schrott", "gelaber", "werbung"), null);
		final RadioLibraryItem radio2 = new RadioLibraryItem("WDR 4", "https://www.wdr4-radio.de", 0, Lists.newArrayList("volksmusik", "schrott", "gelaber"), null);
		final RadioLibraryItem radio2Clone = new RadioLibraryItem("WDR 4", "https://www.wdr4-radio.de", 0, Lists.newArrayList("volksmusik", "schrott", "unsinn"), null);
		
		// Add a station
		service.createRadioStation(radio1);
		Assert.assertEquals(1, service.getAllRadioStations().size());
		Assert.assertTrue(service.getAllRadioStations().contains(radio1));
		
		// Add a second station
		service.createRadioStation(radio2);
		Assert.assertEquals(2, service.getAllRadioStations().size());
		Assert.assertTrue(service.getAllRadioStations().containsAll(Lists.newArrayList(radio1, radio2)));

		// Add a clone of the second station
		service.createRadioStation(radio2Clone);
		Assert.assertEquals(2, service.getAllRadioStations().size());
		Assert.assertTrue(service.getAllRadioStations().containsAll(Lists.newArrayList(radio1, radio2Clone)));
		
	}
}
