function sendRequest(url, callback, postData) {
	var req = createXMLHTTPObject();
	if(!req)
		return;
	var method = (postData) ? "POST" : "GET";
	req.open(method, url, true);
	req.setRequestHeader('User-Agent','XMLHTTP/1.0');
	if(postData)
		req.setRequestHeader('Content-type','application/x-www-form-urlencoded');
	req.onreadystatechange = function () {
		if(req.readyState != 4 || (req.status != 200 && req.status != 304))
			return;
		callback(req);
	}
	if(req.readyState == 4)
		return;
	req.send(postData);
}

function createXMLHTTPObject() {
	var xmlhttp = false;
	var factories = [
		function () {return new XMLHttpRequest()},
		function () {return new ActiveXObject("Msxml2.XMLHTTP")},
		function () {return new ActiveXObject("Msxml3.XMLHTTP")},
		function () {return new ActiveXObject("Microsoft.XMLHTTP")}
	];
	for (var i=0;i<factories.length;i++) {
		try { xmlhttp = factories[i](); }
		catch (e) { continue; }
		break;
	}
	return xmlhttp;
}

function urlencode(s) {
    return encodeURIComponent(s.toString()).replace(/%20/g, '+');
}

function htmlspecialchars(s) {
	return s.toString().replace(/&/g, '&amp;').replace(/</g, '&lt;')
			.replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function mouseCoords(ev) {
	if(ev.pageX || ev.pageY)
		return {x:ev.pageX, y:ev.pageY};
	return {
		x:ev.clientX + document.body.scrollLeft - document.body.clientLeft,
		y:ev.clientY + document.body.scrollTop - document.body.clientTop
	};
}

function findBounds(elt) {
	if(elt.style.display == 'none' || elt.style.visibility == 'hidden')
		return false;
	var bounds = { left:0, top:0, right:0, bottom:0 };
	var parent = elt;
	do {
		bounds.left += parent.offsetLeft;
		bounds.top  += parent.offsetTop;
	}
	while(parent = parent.offsetParent);
	bounds.right = bounds.left + elt.offsetWidth;
	bounds.bottom = bounds.top + elt.offsetHeight;
	return bounds;
}

function contains(elt, pos) {
	var bounds = findBounds(elt); 
	return pos.x >= bounds.left && pos.x <= bounds.right && 
			pos.y >= bounds.top && pos.y <= bounds.bottom;
}