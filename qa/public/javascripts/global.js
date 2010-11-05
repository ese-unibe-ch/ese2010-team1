$(function() {

	// display scrollbars
	$('nav').jScrollPane();
	$('section').jScrollPane();
	
	// pulldown menus
	$('.pulldown a').click(function() {
		$(this).next().toggle();
		$('.pulldown a').not(this).next().hide();
		return false;
	});
	
	// load question
	$('nav a').click(function() {
		$.get(questionsGet({id: this.hash.substr(1)}), function(data) {
			$('section').html(data);
		});
		return false;
	});
	
});