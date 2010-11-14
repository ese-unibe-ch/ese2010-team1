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
	
	// filter questions
	$('#filter a').click(function() {
		switch(this.hash) {
			case "#Mine":
				$("#nav").load(mine());
				break;
			case "#Hot":
				$("#nav").load(hot());
				break;
			case "#Search":
				$("#nav").load(search({string: $("#search input").val()}));
				$("#search input").focus();
				break;
		}
		$('#filter a').removeClass("active");
		$(this).addClass("active");
		return false;
	});
	
	// search questions
	$("#search input").keyup(function() {
		$("#nav").load(search({string: this.value}));
	});
	
	// load question
	$('nav a').livequery('click', function(event) {
		var a = this;
		$.get(questionsGet({id: this.hash.substr(1)}), function(data) {
			$('#section').html(data);
			$('#nav a').removeClass("active");
			$(a).addClass("active");
		});
		return false;
	});
	
	// new question
	$("#new a").click(function() {
		$.get(form({type: "question"}), function(data) {
			$("#section").html(data);
			$('#nav a').removeClass("active");
			$("#section form").submit(function() {
				var title = $("#section input[name=title]").val();
				var content = $("#section textarea[name=content]").val();
				var tags = $("#section input[name=tags]").val();
				$.get(add({title: title, content: content, tags: tags}), function(data, status) {
					if(status=="success") {
						$("#section").html(data);
						$("#filter a.active").click();
					} else {
						alert(status);
					}
				});
				return false;	
			});
		});
		return false;
	});
	
	// vote up
	$('a.up').livequery('click', function(event) {
		var entry = $(this).parents("article");
		$.get(voteUp({id: this.hash.substr(1)}), function(data) {
			entry.html(data);
		});
		return false;
	});
	
	// vote down
	$('a.down').livequery('click', function(event) {
		var entry = $(this).parents("article");
		$.get(voteDown({id: this.hash.substr(1)}), function(data) {
			entry.html(data);
		});
		return false;
	});
	
	// remove vote
	$('a.delete').livequery('click', function(event) {
		var entry = $(this).parents("article");
		$.get(removeVote({id: this.hash.substr(1)}), function(data) {
			entry.html(data);
		});
		return false;
	});
	
	// set best answer
	$('.set.bestAnswer').livequery('click', function(event) {
		$("#section").load(setBestAnswer({id: this.hash.substr(1)}));
		return false;
	});
	
	// reset best answer
	$('.reset.bestAnswer').livequery('click', function(event) {
		$("#section").load(resetBestAnswer({id: this.hash.substr(1)}));
		return false;
	});

});
