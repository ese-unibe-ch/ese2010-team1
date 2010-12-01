function reinitialise() {	
	$('html').height($(window).height()-20);
	var height = $('body').height() - 70;
	$('section, nav').height(height);
	if($('#topaction').size() >= 1) {
		var margin = $('#topaction').height()
		$('nav').css({'margin-top': margin + 'px'});
		$('nav').height(height - margin);
	}
	$('section').data('jsp').reinitialise();
	if($('nav').data('jsp')) {
		$('nav').data('jsp').reinitialise();
		$('section').css({width: "600px"});
		$('nav').css({width: "360px"});
	}
}

function token() {
	return $("#authenticityToken input").val();
}

function noData() {
	return {authenticityToken: token()};
}

$(function() {
	
	$('section').jScrollPane();
	$('nav').jScrollPane({scrollbarOnLeft: true});
	
	reinitialise();
	$(window).resize(reinitialise);
	$(window).ajaxComplete(reinitialise);
	
	// pulldown menus
	$('.pulldown > a').click(function() {
		$(this).next().toggle();
		$('.pulldown > a').not(this).next().hide();
		return false;
	});
	
	$('.pulldown li').click(function() {
		if($(this).find("a").length > 0 && !$(this).find("a").hasClass("notification"))
			self.location = $(this).find("a").attr("href");
	});
	
	
	$('a.notification').livequery('click', function(event) {
		var url = $(this).attr('href');
		$.get(setNotificationAsRed({id: this.hash.substr(1) }), function() {
			self.location = url;
		});
		return false;
	});


});

