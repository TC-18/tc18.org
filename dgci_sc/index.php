<?php
require_once 'fileList.php';

// configuration..
$pattern = '.+\.*';
$columns = array('Filename', 'Size', 'Last change', 'Comments');
$fileList = new FileList('upload', $pattern, $columns, 'File list');

$output = $fileList->run();		// this is it ! put $output where you want the file list to appear
$head = FileList::head(true);	// JS and CSS inclusion, to put in <head>
FileList::header(); 			// HTTP header sending, to call before any output is sent to browser

if($fileList->isDisplayPage()): // if in page mode
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>File list</title>
		<?=$head?>
	</head>
	<body onload="FileList()" style="padding-top: 0px; margin-top: 5px">
		<h1 class="fileList">File</h1>
		<?=$output?>
	</body>
</html>
<?else: echo $output; endif // else if in AJAX mode ?>
