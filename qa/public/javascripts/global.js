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
	
});