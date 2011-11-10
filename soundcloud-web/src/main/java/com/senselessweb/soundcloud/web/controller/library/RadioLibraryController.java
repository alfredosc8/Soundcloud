package com.senselessweb.soundcloud.web.controller.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.senselessweb.soundcloud.domain.library.LibraryItem;
import com.senselessweb.soundcloud.domain.library.RadioLibraryItem;
import com.senselessweb.soundcloud.library.service.radio.RemoteRadioLibraryService;
import com.senselessweb.soundcloud.library.service.radio.UserRadioLibraryService;
import com.senselessweb.soundcloud.library.service.radio.impl.IcecastRadioService;
import com.senselessweb.soundcloud.library.service.radio.impl.ShoutcastRadioService;
import com.senselessweb.soundcloud.mediasupport.service.MediaPlayer;
import com.senselessweb.soundcloud.mediasupport.service.Playlist;

/**
 * Web controller interface for the radio library.
 * 
 * @author thomas
 */
@Controller
@RequestMapping("/library/radio/*")
public class RadioLibraryController
{
	
	/**
	 * The UserRadioLibraryService
	 */
	@Autowired UserRadioLibraryService userRadioLibraryService;

	/**
	 * The icecastRadioService
	 */
	@Resource(name="icecastRadioService") RemoteRadioLibraryService icecastRadioService;
	
	/**
	 * The shoutcastRadioService
	 */
	@Resource(name="shoutcastRadioService") RemoteRadioLibraryService shoutcastRadioService;
	
	/**
	 * The mediaPlayer
	 */
	@Autowired MediaPlayer mediaPlayer;

	/**
	 * The playlist
	 */
	@Autowired Playlist playlist;
		
	/**
	 * Returns all user radio stations
	 * 
	 * @param keyword A keyword to filter the radio stations
	 * 
	 * @return All user radio stations.
	 */
	@RequestMapping("/getUserStations")
	@ResponseBody
	public Collection<? extends LibraryItem> getUserStations(@RequestParam(required=false) String keyword)
	{
		return this.userRadioLibraryService.getItems(keyword);
	}
	
	/**
	 * Returns remote radio stations
	 * 
	 * @param service The service to use. Either 'shoutcast' or 'icecast'
	 * @param limit The maximum number of radio stations to return
	 * @param keyword A keyword to filter the radio stations
	 * 
	 * @return Some random remote radio stations.
	 */
	@RequestMapping("/getRemoteStations")
	@ResponseBody
	public Collection<? extends LibraryItem> getRemoteStations(@RequestParam String service, @RequestParam(defaultValue="30") int limit, @RequestParam(required=false) String keyword)
	{
		final List<LibraryItem> items = new ArrayList<LibraryItem>(
				service.equals("icecast") ? this.icecastRadioService.getItems(keyword) : this.shoutcastRadioService.getItems(keyword));
		return items.subList(0, Math.min(limit, items.size()));
	}
	
	
	/**
	 * Adds a new user radio station to the library
	 * 
	 * @param name The name of the new station
	 * @param url The url of the new station 
	 * @param genres The genres of the new station
	 * 
	 * @return The created {@link RadioLibraryItem} 
	 */
	@RequestMapping("/create")
	@ResponseBody
	public RadioLibraryItem create(final @RequestParam String name, final @RequestParam String url, final @RequestParam String genres)
	{
		return this.userRadioLibraryService.store(name, Collections.singleton(url), genres.split(","));
	}
	
	/**
	 * Stores a remote item in the user storage service.
	 * 
	 * @param id The id of the remote item.
	 * 
	 * @return The new stored user item.
	 */
	@RequestMapping("/store")
	@ResponseBody
	public RadioLibraryItem store(final @RequestParam String id)
	{
		final RadioLibraryItem item = this.findRemoteRadioStation(id);
		return this.userRadioLibraryService.store(item.getLongTitle(), item.getUrls(), item.getGenres().toArray(new String[0]));
	}
	
	/**
	 * Plays the user station with the given id.
	 * 
	 * @param type The type. Either remote od my-stations
	 * @param id The id.
	 */
	@RequestMapping("/play")
	@ResponseStatus(HttpStatus.OK)
	public void play(final @RequestParam String type, final @RequestParam String id)
	{
		final LibraryItem station;
		
		if (type.equals("remote")) station = this.findRemoteRadioStation(id);
		else station = this.userRadioLibraryService.findById(id);
		
		this.mediaPlayer.stop();
		this.playlist.set(station.asMediaSources());
		this.mediaPlayer.play();
	}
	
	/**
	 * Deletes the user station with the given id.
	 * 
	 * @param id The id.
	 */
	@RequestMapping("/delete")
	@ResponseStatus(HttpStatus.OK)
	public void delete(final @RequestParam String id)
	{
		this.userRadioLibraryService.delete(id);
	}
	
	/**
	 * Locates a {@link RadioLibraryItem} in {@link ShoutcastRadioService} and {@link IcecastRadioService}.
	 * 
	 * @param id The id
	 * 
	 * @return The {@link RadioLibraryItem} or null if it could not be found.
	 */
	private RadioLibraryItem findRemoteRadioStation(final String id)
	{
		final LibraryItem item = this.icecastRadioService.findById(id);
		return (RadioLibraryItem) (item != null ? item : this.shoutcastRadioService.findById(id));
	}
}
