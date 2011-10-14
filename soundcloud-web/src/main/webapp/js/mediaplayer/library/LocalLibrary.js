
$.include('js/mediaplayer/library/Item.js');
$.include('js/mediaplayer/library/Folder.js');
$.include('css/medialibrary.css');

/**
 * The parent element
 */
var parent;


/**
 * Creates a new local library
 * 
 * @param parent The parent element
 */
function LocalLibrary(parent) 
{
	var _this = this;
	
	parent.append('<div class="library-container"><table><tr></tr></table></div>');
	this.parent = $('table tr', parent);
	
	$.getJSON('controller/library/local/getFolder', function(data) {
		_this.initLevel(0, data);
	});	
}

/**
 * Initializes a new level and removes all previous item of the same and higher level.
 * 
 * @param level The new level  
 * @param data The data for this level
 */
LocalLibrary.prototype.initLevel = function(level, data)
{
	var _this = this;
	
	var onSelectSubfolder = function(level, folder) {
		$.getJSON('controller/library/local/getFolder?folder=' + encodeURIComponent(folder), function(data) {
			_this.initLevel(level + 1, data);
		});
	};
	
	var onPlayFolder = function(folder) {
		$.getJSON('controller/library/local/playFolder?folder=' + encodeURIComponent(folder));
	};
	
	var onEnqueueFolder = function(folder) {
		$.getJSON('controller/library/local/enqueueFolder?folder=' + encodeURIComponent(folder));
	};
	
	// Removes level columns that are to replace by the new level
	$('td.level-' + level, _this.parent).remove();
	
	// Create the new folder column
	var levelClass = "";
	for (var i = 0; i <= level; i++) levelClass += " level-" + i;
	this.parent.append('<td class="' + levelClass + '"><div class="scollable-content"></div></td>');
	
	// Create the new folder
	var folder = new Folder(data.name, level, data.path, this.createItems(data.files), data.subfolders);
	
	// Append the new folder to the new folder column.
	folder.appendAsElement($('.level-' + level + ' div', this.parent), onSelectSubfolder, onPlayFolder, onEnqueueFolder);	
	
	// Scroll to the new element
	$('.library-container').scrollTo('max', 600);	
};

/**
 * Creates an Item array from a json callback array
 * 
 * @param data The json callback array
 * 
 * @returns {Array} The created items
 */
LocalLibrary.prototype.createItems = function(data)
{
	var playFunction = function(id) {
		$.ajax('controller/library/local/playFile?file=' + id);
	};
	var enqueueFunction = function(id) {
		$.ajax('controller/library/local/enqueueFile?file=' + id);
	};
	
	var items = new Array();
	for (var i = 0; i < data.length; i++)
	{
		var item = new Item(data[i].id, data[i].shortTitle, data[i].genres, playFunction, enqueueFunction, null, null);
		items.push(item);
	}
	return items;
};

