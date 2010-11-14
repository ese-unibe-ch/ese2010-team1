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
			$('div#content').html(data);
			
		});
		return false;
	});
	
	// vote up
	$('a.up').livequery('click', function(event) {
		$.get(voteUp({id: this.hash.substr(1)}), function(data) {
			$('#div#content').html(data)
			
		});
		return false;
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
	
	//recommanded Questions
//	$('#rq').keyup(function(event){
//		if(this.value.length > 2){
//		$.get(recommandedQuestions({title: this.value}), function(data){
//				$('div#recommandedQuestions').html(data)
//		
//		});
//		}
//		return false;
//	});

	
});
