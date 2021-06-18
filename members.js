function readTextFile(file)
{
    var rawFile = new XMLHttpRequest();
    rawFile.open("GET", file, false);
    rawFile.onreadystatechange = function ()
    {
        if(rawFile.readyState === 4)
        {
            if(rawFile.status === 200 || rawFile.status == 0)
            {
                var allText = rawFile.responseText;
                document.getElementById('memberslist').innerHTML = allText 
            }
        }
    }
    rawFile.send(null);
}

//readTextFile("https://tc18.org/membersList.html");
readTextFile("membersList.html");




