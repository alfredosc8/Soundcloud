package com.senselessweb.soundcloud.library.domain;

import java.util.Collection;

import com.senselessweb.soundcloud.domain.MediaSource;

/**
 * Base interface for all library items.
 * 
 * @author thomas
 */
public interface LibraryItem
{

	/**
	 * Returns a collection of {@link MediaSource}s for this library 
	 * item (i.e. the items of an album or the single files of a playlist)
	 *  
	 * @return The {@link MediaSource}s. 
	 */
	public Collection<? extends MediaSource> getMediaSources();
	
	/**
	 * Returns the genres of this item.
	 * 
	 * @return The genres.
	 */
	public Collection<String> getGenres();
	
	
	/**
	 * Returns the name of this item.
	 * 
	 * @return The name.
	 */
	public String getName();
	
	
	/**
	 * Returns the bitrate of this item.
	 * 
	 * @return The bitrate or -1 if it is unknown.
	 */
	public int getBitrate();
	
}
