<form id="fileListForm" action="" method="POST" enctype="multipart/form-data">
	<table id="fileListTable" class="fileList">
		<col style="width: 400px">
		<col style="width: 80px">
		<col style="width: 120px; text-align: center">
		<col style="min-width: 300px">
		<thead><tr>
			<th style="padding-left: 3px">
				<img src="folder-open.gif" class="folder" style="float:left">
				<?=$columnTitles[0]?>
			</th>
			<?for($i = 1; $i < 4; $i++) echo "<th>$columnTitles[$i]</th>"?>
		</tr></thead>
		<tbody>
		<?foreach($list as $relpath => $entry): $id = $entry->hID; 
				$space = $entry->level * 22; $remaining = ($entry->dir? 345: 365) - $space?>
			<tr id="<?=$id?>" <?if($entry->hidden):?>style="display: none"<?endif?>>
				<td>
					<input id="selected<?=$id?>" type="checkbox" name="selected[]" 
							value="<?=$relpath?>" <?if($entry->selected):?>checked<?endif?>
							style="margin-left: <?=$space?>px">
					<?if($entry->dir):?>
						<span id="folderSpan<?=$id?>" class="folder">
							<img id="folder<?=$id?>" src="<?=$entry->image?>" class="folder">
							<span id="name<?=$id?>" class="folder">
								<?=htmlspecialchars($entry->name)?>
							</span>
						</span>
						<input id="expanded<?=$id?>" type="hidden" name="expanded[]" 
								value="<?=$relpath?>" <?if(!$entry->expanded):?>disabled<?endif?>>
					<?else:?>
						<a id="name<?=$id?>" href="<?=$entry->url?>">
							<?=htmlspecialchars($entry->name)?>
						</a>
					<?endif?>
						<input type="text" id="newName<?=$id?>" value="<?=$entry->name?>" disabled 
								class="edit" autocomplete=off style="width: <?=$remaining?>px">
				</td>
				<td style="text-align: right"><?=$entry->size?></td>
				<td style="text-align: center"><?=$entry->date?></td>
				<td>
					<span class="text" id="comment<?=$id?>">
						<?=htmlspecialchars($entry->comment)?>
					</span>
					<input type=text id="newComment<?=$id?>" value="<?=$entry->comment?>" 
							class="edit" autocomplete=off disabled>
				</td>
			</tr>		
		<?endforeach?>
		</tbody>
	</table>
	<table id="fileListActions"><tr>
		<td>
			<input type=submit name=refresh id=refresh value="Refresh">
			<input type=submit name=delete id=delete value="Delete">
			<input type=submit name=download id=download value="Download">
			<input type=file name=newFile id=newFile size=25>
			<input type=submit name=upload id=upload value="Upload">
		</td>
		<td style="width: 40%"><input type=text name=newDir id=newDir style="width:100%"></td>
		<td><input type=submit name=mkdir id=mkdir value="New folder"></td>
		<td style="width: 60%"><input type=text name=newLink id=newLink style="width:100%"></td>
		<td><input type=submit name=mklink id=mklink value="New link"></td>
	</tr></table>
</form>
<?if($displayMessage):?>
	<p id=message class="<?=$messageClass?>"><?=$message?></p>
<?endif?>