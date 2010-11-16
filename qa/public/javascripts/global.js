$(function() {

	var reinitialise = function() {
			var height = $('body').height() - 70;
			$('section, nav').height(height);
			if($('.bottomleft').size() >= 1)
				$('nav').height(height - $('.bottomleft').height());
			$('section').data('jsp').reinitialise();
			if($('nav').data('jsp')) {
				$('nav').data('jsp').reinitialise();
				$('section').css({width: "600px"});
			}
	}
	
	
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
		if($(this).find("a").length > 0)
			self.location = $(this).find("a").attr("href");
	});

});

