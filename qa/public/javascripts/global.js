$(function() {
	// display scrollbars
	$('nav').jScrollPane();
	var scrollPane = $('section');
	scrollPane.jScrollPane();

	// pulldown menus
	$('.pulldown > a').click(function() {
		$(this).next().toggle();
		$('.pulldown > a').not(this).next().hide();
		return false;
	});
	
	// load question
	$('nav a').livequery('click', function(event) {
		$.get(questionsGet({id: this.hash.substr(1)}), function(data) {
			$('#section').html(data);
		});
		return false;
	});
	
	// vote up
	$('a.up').livequery('click', function(event) {
		var entry = $(this).parents("article");
		$.getJSON(voteUp({id: this.hash.substr(1)}), function(data) {
			if(data['success']) {
				entry.find(".rating").html(data['rating']);
				entry.find(".reputation").html(data['reputation']);
			}
		});
		return false;
	});
	
	// vote down
	$('a.down').livequery('click', function(event) {
		var entry = $(this).parents("article");
		$.getJSON(voteDown({id: this.hash.substr(1)}), function(data) {
			if(data['success']) {
				entry.find(".rating").html(data['rating']);
				entry.find(".reputation").html(data['reputation']);
			}
		});
		return false;
	});

});
