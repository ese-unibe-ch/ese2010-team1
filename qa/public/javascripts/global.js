$(function() {

	var reinitialise = function() {
			$('section, nav').height($('body').height() - 70);
			$('section').data('jsp').reinitialise();
			if($('nav').data('jsp'))
				$('nav').data('jsp').reinitialise();
			$('nav').css({width: "380px"});
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

	
	// vote down
	$('a.down').livequery('click', function(event) {
		$.get(voteDown({id: this.hash.substr(1)}), function(data) {
			$('div#content').html(data)
	
		});
		return false;
	});
	
	// load profile tabs
	$('.profileTabs a').click(function() {
		
		
	
	});
	
	//		recommanded Questions
	$('input.rq').keyup(function(event){
		
		$.get(recommendedQuestions({title: this.value}), function(data){
				$('div#rqs').html(data)
		
		});
		
		return false;
	});


});

