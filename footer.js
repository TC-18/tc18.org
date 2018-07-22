var pagename = location.pathname.substring(location.pathname.lastIndexOf("/") + 1);
document.getElementById("footerSite").innerHTML = '  \
<hr /> \
<div class="footer"> \
<p><a href="index.php">IAPR-TC18 webpage</a> \
You can conribute to the edtion trough the <a href="https://github.com/TC-18/tc18.org">Github source page</a> and directly edit this page <a href="https://github.com/TC-18/tc18.org/blob/master/'+pagename+'">here</a>. </p>\
</div> \
';

