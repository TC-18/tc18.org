var path = window.location.pathname;
var pagename = path.split("/").pop();
if (!pagename || 0 === pagename.length|| pagename=="tc18.org" || pagename=="www.tc18.org" || !pagename || 0 === pagename.length)
{
  pagename="index.html"; 
}
document.getElementById("footerSite").innerHTML = '  \
<hr /> \
<div class="footer"> \
<p><a href="index.php">IAPR-TC18 webpage</a> \
You can contribute to the edition through the <a href="https://github.com/TC-18/tc18.org">Github source page</a> and directly edit this page <a href="https://github.com/TC-18/tc18.org/blob/master/'+pagename+'">here</a>. </p>\
</div> \
';

