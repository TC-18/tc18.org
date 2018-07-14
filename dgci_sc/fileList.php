<?php

/**
 * File or directory entry that will be displayed in the list. 
 * This class is just a place-holder for documentation.
 */
class FileList_Entry {
	var /*string*/	$relpath;	// relative path w.r.t. the upload directory 
	var /*string*/	$name;		// filename (UTF-8 encoded)
	var /*boolean*/	$dir;		// weather this is a directory or not
	var /*boolean*/	$link;		// weather this is a link entry or not
	var /*integer*/	$level;		// level in the folder hierarchy (starts at 0)
	var /*string*/	$hID;		// hierarchical ID in the form 1-1-2 (starts at 1)
	var /*string*/	$comment;	// comment associated to this entry (stored in metadata file)
	var /*string*/	$url;		// URL to access the file, or URL if entry is a link
	var /*boolean*/	$selected;	// weather this entry is checked (selected) in the list or not 
	var /*string*/	$date;		// last modification date of the file in d/m/Y format 
	var /*string*/	$size;		// file size in MB (with MB suffix), empty for directories
	var /*boolean*/	$hidden;	// weather this entry is currently hidden or not
	var /*boolean*/	$expanded;	// folder only, weather it is expanded or not 
	var /*string*/	$image;		// folder only, URL of the correct image depending on expansion
}

/**
 * Hierarchical file list displayed as a tree-table, supporting upload, AJAX renaming, comments, 
 * drag and drop move, external links, and download. This PHP class is the server side of the
 * file list and should be used with the related CSS, JS scripts and template.
 */
class FileList {
	const LINK_PATTERN = '/^(.+)\.url$/i';
	const URL_PATTERN = '%^https?://[\w_\-]+(?:\.[\w_\-]+)+(?:/[\w/&;#.:@\%$()~_?=+\-\\[\]]*)?$%i'; 

	// configuration
	private $uploadDirName;	// upload directory name, w.r.t. the main PHP script
	private $columnTitles = array('File', 'Size', 'Last mod', 'Comment'); // column headers
	private $pattern;		// filename filter pattern, as a standard regular expression (regex)
	private $downloadFileName; // default filename prefix for multiple files download (zip)
	private $metaDataFileName; // metadata filename (this file holds comments data) 
	
	// request params
	private $selected;		// list of selected (checked) entries relative paths
	private $expanded;		// list of expanded folder entries relative paths
	private $relpath;		// relative path of a one-entry modification request (AJAX)
	private $newName;		// new name for an entry renaming
	private $newDir;		// new directory name for a mkdir, or link name for a mklink
	private $newLink;		// new link URL for a mklink
	private $newRelpath;	// destination directory relative path for an entry moving (AJAX)
	private $newComment;	// new comment for an entry comment edition
	private $newFile;		// new file name (and relative path) for a file upload

	// operational
	private $list;			// entry list, indexed by relative path
	private $message;		// message text (usually displayed below the file list table)
	private $messageClass = 'noError';	// CSS class of the message <P>
	private $displayMessage; // weather the message should be displayed below the table
	private $displayPage = true; // weather we are in page mode (true) or AJAX mode (false)
	private $output;		// HTML output after calling the run() method
	
	/** Constructs a new file list.. */
	public function __construct($uploadDirName = 'upload', $pattern = '.*', $columnTitles = null, 
			$downloadFileName = 'files', $metaDataFileName = '.metadata', $displayMessage = true) {
		$this->pattern = "/^$pattern$/i";
		$this->uploadDirName = $uploadDirName;
		if(is_array($columnTitles)) {
			if(!count($columnTitles) == 4)
				throw new Exception('$columnTitle array must have 4 elements!');
			$this->columnTitles = $columnTitles;
		}
		$this->downloadFileName = $downloadFileName;
		$this->metaDataFileName = $metaDataFileName;
		$this->uploadDir = dirname(__FILE__).'/'.$uploadDirName;
		$this->displayMessage = $displayMessage;
	}
	
	/** convenience static method to send adequate HTTP headers */
	public static function header() {
		header("Cache-Control: no-cache, must-revalidate"); // disable caching for HTTP/1.1
		header("Expires: Mon, 30 Jul 1995 05:00:00 GMT"); // Date in the past (to disable caching)
		header('Content-type: text/html; charset=UTF-8'); // ensure we use UTF-8 encoding
	}
	
	/** 
	 * Convenience static method to include the necessary CSS stylesheet and JS scripts.
	 * Should be called or included in the <head> of the document. If $outputBuffering is true, 
	 * the HTML is returned as a PHP string, if false, it is directly output to the browser.
	 */
	public static function head($outputBuffering = FALSE) {
		if($outputBuffering)
			ob_start();
		?><link rel="stylesheet" type="text/css" href="fileList.css">
		<script type="text/javascript" src="util.js"></script>
		<script type="text/javascript" src="fileList.js"></script><?
		if($outputBuffering)
			return ob_get_clean();
	}
	
	/** Get the file list related request parameters as member variables */
	public function readParam() {
		$this->selected = $_REQUEST['selected'];
		$this->expanded = $_REQUEST['expanded'];
		$this->relpath = $_REQUEST['relpath'];
		$this->newName = $_REQUEST['newName'];
		$this->newDir = $_REQUEST['newDir'];
		$this->newLink = $_REQUEST['newLink'];
		$this->newRelpath = $_REQUEST['newRelpath'];
		$this->newComment = $_REQUEST['newComment'];
		$this->newFile = $_FILES['newFile'];
	}
	
	/** Parse the metadata file and builds the comment list */
	public function parseMetaData() {
		if(!file_exists($this->uploadDir))
			mkdir($this->uploadDir); // warning, created with 0777 mode by default
		$metaDataFile = $this->uploadDir.'/'.$this->metaDataFileName;
		$this->comment = array();
		if(file_exists($metaDataFile))
			foreach(file($metaDataFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES) as $line)
				if(preg_match('/^[ \t]*"(.*)"[ \t]*=[ \t]*"(.*)"[ \t]*$/', $line, $regs))
					$this->comment[$regs[1]] = $regs[2];
	}
	
	/** Try to delete the selected (checked) file(s) or folder(s), keeping track of errors */
	public function delete() {
		rsort($this->selected);
		foreach($this->selected as $name) {
			$path = $this->uploadDir.'/'.utf8_decode($name);
			$entry = $this->list[$name];
			if($entry->dir && @rmdir($path) || !$entry->dir && @unlink($path))
				$ok[] = $name;
			else {
				$error = ($error = error_get_last())? $error['message']: '';
				$errors[] = "$name ($error)";
			}
		}
		if(!empty($ok))
			$this->message('Deleted ['.implode('], [', $ok).']');
		if(!empty($errors))
			$this->appendMessage('Could not delete ['.implode('], [', $errors).']', true);
	}
	
	/** Rename the file of the edited entry (AJAX mode), checking the new file name */
	public function rename() {
		$relpath = self::strip($this->relpath);
		$newName = self::strip($this->newName);
		$entry =& $this->list[$relpath];
		if($entry->link)
			$newName .= '.url';
		$path = $this->uploadDir.'/'.utf8_decode($relpath);
		$newPath = dirname($path).'/'.utf8_decode($newName);
		$newRelpath = strpos($relpath, '/')? dirname($relpath).'/'.$newName: $newName;
		if(!$entry->dir && !$entry->link && !preg_match($this->pattern, $newName))
			$error = 'invalid file extension';
		elseif($path != $newPath)
			if(@rename($path, $newPath))
				echo "OKRenamed [$relpath] to [$newRelpath]";
			else
				$error = ($error = error_get_last())? $error['message']: '';
		if($error)
			echo "KOCould not rename [$relpath] to [$newRelpath] ($error)";
		else
			$this->adjustPaths($relpath, $newRelpath);
		$this->displayPage = FALSE;
	}
	
	/** Adjust the paths of the "selected" and "expanded" lists in case of rename or move */
	private function adjustPaths($relpath, $newRelpath) {
		$prefixLength = strlen($relpath);
		foreach($this->list as $rp => $e) {
			if(strncmp($rp, $relpath, $prefixLength) == 0) {
				$newRp = $newRelpath.substr($rp, $prefixLength);
				$this->comment[$newRp] = $this->comment[$rp];
				if($e->selected)
					$this->selected[] = $newRp;
				if($e->expanded)
					$this->expanded[] = $newRp;
			}
		}
	}
	
	/** Moves the file that was dragged and droped to a valid destination folder (AJAX mode)*/
	public function move() {
		$relpath = self::strip($this->relpath);
		$destRelpath = self::strip($this->newRelpath);
		if($destRelpath == '.')
			$newRelpath = basename($relpath);
		else
			$newRelpath = $destRelpath.'/'.basename($relpath);
		$path = $this->uploadDir.'/'.utf8_decode($relpath);
		$newPath = $this->uploadDir.'/'.utf8_decode($newRelpath);
		if($path == $newPath)
			return;
		if(@rename($path, $newPath)) {
			$this->message("Moved [$relpath] to [$newRelpath]");
			$this->adjustPaths($relpath, $newRelpath);
			if($destRelpath != '.')
				$this->expanded[] = $destRelpath; // expand destination folder
		}
		else {
			$error = ($error = error_get_last())? $error['message']: '';
			$this->message("Could not move [$relpath] to [$newRelpath] ($error)", true);
		}
	}
	
	/** Update the comment of the edited entry (AJAX mode) */
	public function editComment() {
		$relpath = self::strip($this->relpath);
		$this->comment[$relpath] = self::strip($this->newComment);
		echo "OKUpdated comment for [$relpath]";
		$this->displayPage = FALSE;
	}
	
	/**
	 * Download the non-folder and non-link selected entries in the list.
	 * If multiple files are selected, builds a ZIP archive and serve it (needs the zip PHP ext).
	 * If only one file is selected, serve it as is (without wrapping it in a zip archive).
	 */
	public function download() {
		foreach($this->selected as $name) {
			$entry = $this->list[$name];
			if(!$entry->dir && !$entry->link)
				$selected[] = $name;
		}
		if(count($selected)==0)
			return;
		elseif(count($selected)==1) {
			header('Content-Type: application/octetstream');
			header('Content-Disposition: attachment; filename="'.basename($selected[0]).'"');
			readfile($this->uploadDir.'/'.$selected[0]);
			$this->displayPage = FALSE;
		}
		else {
			$zipFile = $this->uploadDir.'/'.$this->downloadFileName.'.zip';
			if(file_exists($zipFile))
				unlink($zipFile);
			$zip = new ZipArchive;
			if(!($code = $zip->open($zipFile, ZIPARCHIVE::CREATE)))
				$this->message("Error preparing the zip archive file (code $code)", true);
			else {
				foreach($selected as $file) {
					$zip->addFile($this->uploadDir.'/'.$file, $file);
					$zip->setCommentName($entry, $this->comment[$file]);
				}
				$zip->close();
				header('Content-Type: application/octetstream');
				header('Content-Disposition: attachment; filename="'.basename($zipFile).'"');
				readfile($zipFile);
				unlink($zipFile);
				$this->displayPage = FALSE;
			}
		}
	}
	
	/** Uploads the file of the "newFile" file input, checking its name and URL if it's a link */
	public function upload() {
		$name = $this->newFile['name'];
		$path = $this->uploadDir.'/'.utf8_decode($name);
		$tmpPath  = $this->newFile['tmp_name'];
		if($isLink = preg_match(self::LINK_PATTERN, $name))
			$url = $this->getUrl($tmpPath, $name);
		if($isLink && (!$url || !preg_match(self::URL_PATTERN, $url)))
			$this->appendMessage("Could not upload link [$name], invalid URL (see file content)", true);
		elseif(!$isLink && !preg_match($this->pattern, $name))
			$this->message("Could not upload [$name] (invalid file extension)", true);
		elseif(move_uploaded_file($tmpPath, $path))
			$this->message("Uploaded [$name]");
		else
			$this->message("Could not upload [$name] ({$this->newFile['error']})", true);
	}
	
	/** (Re)Set the message text and adjusts the message CSS class */
	public function message($msg, $error = FALSE) {
		$this->messageClass = $error? 'error': 'noError';
		$this->message = $msg;
	}
	
	/** Appends a new line to the message text, using a <span> to apply the message CSS class */
	public function appendMessage($msg, $error = FALSE) {
		$messageClass = $error? 'error': 'noError';
		if($this->message && strlen(trim($this->message))>0)
			$this->message .= '<br>';
		$this->message .= "<span class=\"$messageClass\">$msg</span>";
	}

	/** Makes a new directory, using the name specified by the "newDir" text field */
	public function mkdir() {
		$newDir = self::strip($this->newDir);
		$newPath = $this->uploadDir.'/'.utf8_decode($newDir);
		if(!$this->checkName($newDir))
			return;
		if(@mkdir($newPath))
			$this->message = "Created directory [$newDir]";
		else {
			$error = ($error = error_get_last())? $error['message']: '';
			$this->message("Could not make directory '$newDir' ($error)", true);
		}
	}
	
	protected function checkName($name) {
		if($name[0] == '/' || $name[0] == '.')
			$this->message("Name cannot start with '.' or '/'", true);
		elseif(in_array('..', explode('/', $name)))
			$this->message("Name cannot contain '..' path segments", true);
		else
			return true;
		return false;
	}
	
	/** Makes a new link, by creating a ".url" file in the upload dir. with the URL as content */
	public function mklink() {
		$newDir = self::strip($this->newDir);
		$newPath = $this->uploadDir.'/'.utf8_decode($newDir).'.url';
		$newLink = self::strip($this->newLink);
		if(!isset($this->newDir) || strlen($newDir)==0)
			$this->message('You must specify a link name in the center text field', true);
		elseif(!$this->checkName($newDir))
			return;
		elseif(file_exists($newPath))
			$this->message('File or link with this name already exists', true);
		elseif(!preg_match(self::URL_PATTERN, $newLink))
			$this->message("Invalid URL [$newLink] (only absolute http/https URLs accepted)", true);
		else {
			if(file_put_contents($newPath, $newLink))
				$this->message("Created link [$newDir] with URL [$newLink]");
			else {
				$error = ($error = error_get_last())? $error['message']: '';
				$this->message("Could not create link [$newDir] ($error)", true);
			}
		}
	}

	/**
	 * Scans the upload directory and constructs the entry list as an array of FileList_Entry
	 * instances indexed by their relative path. 
	 */
	public function dir() {
		$this->list = array();
		$this->recurseDir('', 0, '', false);
	}
	
	/** Recursive function that scans each (sub)folder and actually creates the entries */
	protected function recurseDir($dir, $level, $hIDprefix, $hidden) {
		$base = $this->uploadDir.'/'.utf8_decode($dir);
		$files = scandir($base);
		natcasesort($files);
		$i = 1;
		foreach($files as $name) {
			$path = $base.'/'.$name;
			$isDir = $name[0] != '.' && is_dir($path);
			$regs = array();
			$isLink = preg_match(self::LINK_PATTERN, $name, $regs);
			if($isDir || $isLink ||  (preg_match($this->pattern, $name) && $name[0] != '.')) {
				$entry = new FileList_Entry();
				$entry->relpath = $dir.utf8_encode($name);
				$entry->name = utf8_encode($isLink? $regs[1]: $name);
				$entry->dir = $isDir;
				$entry->link = $isLink;
				$entry->level = $level;
				$entry->hID = $hIDprefix.($i++);
				$entry->hidden = $hidden;
				$entry->comment = $this->comment[$entry->relpath];
				$entry->selected = $this->selected && in_array($entry->relpath, $this->selected);
				$entry->date = date('d/m/Y', filemtime($path));
				$this->list[$entry->relpath] = $entry;
				if($isDir) {
					$entry->expanded = $this->expanded && in_array($entry->relpath, $this->expanded);
					$entry->image = $entry->expanded? 'folder-open.gif': 'folder-closed.gif';
					$this->recurseDir($entry->relpath.'/', $level+1, $entry->hID.'-', 
							!$entry->expanded || $hidden);
				}
				elseif($isLink) {
					$entry->url = $this->getUrl($path, $entry->relpath);
				}
				else {
					$entry->url = $this->uploadDirName.'/'.$dir.rawurlencode($entry->name);
					$entry->size = number_format((filesize($path)/1024)/1024, 1).' MB';
				}
			}
		}
	}
	
	/** get a URL from a link file specified by $path */
	protected function getUrl($path, $relpath) {
		$regs = array();
		$url = trim(file_get_contents($path));
		if(!preg_match('/^(?:\[\w+\]\s*URL=)?(\S+)$/i', $url, $regs))
			$this->appendMessage("Could not parse URL in link file [$relpath]", true);
		elseif(!preg_match(self::URL_PATTERN, $regs[1]))
			$this->appendMessage("Invalid URL in link file [$relpath]", true);
		else
			return $regs[1];
		return false;
	}
	
	/** write the metadata (i.e. the comments) back to the metadata file */
	public function writeMetaData() {
		$metaDataFile = fopen($this->uploadDir.'/'.$this->metaDataFileName, 'w');
		foreach($this->list as $filename => $entry)
			fwrite($metaDataFile, "\"$filename\" = \"{$entry->comment}\"\n");
	}
	
	/** switch to the correct action method according to the request params */
	public function actionSwitch() {
		if(isset($_REQUEST['delete']) && !empty($this->selected))
			$this->delete();
		elseif(isset($this->relpath) && isset($this->newName))
			$this->rename();
		elseif(isset($this->relpath) && isset($this->newComment))
			$this->editComment();
		elseif(isset($this->relpath) && isset($this->newRelpath))
			$this->move();
		elseif(isset($_REQUEST['download']) && !empty($this->selected))
			$this->download();
		elseif(isset($_REQUEST['upload']) && isset($this->newFile))
			$this->upload();
		elseif(isset($_REQUEST['mkdir']) && !empty($this->newDir))
			$this->mkdir();
		elseif(isset($_REQUEST['mklink']) && !empty($this->newLink))
			$this->mklink();
	}

	/** 
	 * Runs the filelist by processing the request params, parsing the metadata, scanning the 
	 * upload directory, invoking the correct action according to the params, rewriting the metadata
	 * file and finally displaying the template (except if in AJAX mode). 
	 */
	public function run() {
		ob_start();
		$this->readParam();
		$this->dir();
		$this->parseMetaData();
		$this->actionSwitch();
		$this->dir();
		$this->writeMetaData();
		if($this->displayPage)
			$this->display();
		return $this->output = ob_get_clean();
	}
	
	/** Displays the fileList.tpl template, after putting members in local naming scope */
	public function display() {
		$_vars = get_object_vars($this);
		foreach($_vars as $_key => &$_value)
			if($_key[0] != '_')
				$$_key =& $this->$_key;
		include 'fileList.tpl';
	}

	/** Gets ride of possible quote escaping and trims the given string */
	public static function strip($s) {
		return get_magic_quotes_gpc()? trim(stripslashes($s)): trim($s);
	}

	// accessors..
	public function isDisplayPage() { return $this->displayPage; }
	public function getOutput() { return $this->output; }
	public function getMessage() { return $this->message; }
}
?>