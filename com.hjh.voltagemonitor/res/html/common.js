/**document.onselectstart = function() {
	return false;
};

document.onmousedown = function() {
	return false;
};

document.onmouseup = function() {
	return false;
};

document.oncontextmenu = function() {
	return false;
};*/

(function($) {
	$.getUrlParam = function(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null)
			return unescape(r[2]);
		return null;
	}
})(jQuery);

if (typeof console == 'undefined') {
	window.console = {
		debug : function() {
		}
	};
}

$(document).ready(function() {
	$('<div style="display:none;"><script src="http://s23.cnzz.com/stat.php?id=5821592&web_id=5821592" language="JavaScript"></script></div>').appendTo(document.body);
});