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
	
	// profile options
	$('a.tab').livequery('click', function(event) {
		var a = this;
		$.get(profileGet({id: userid, theAction: this.hash.substr(1)}), function(data) {
			
			$('article.profileContent').html(data);
			$('a.tab').removeClass("active");
			$(a).addClass("active");
		});
		return false;
	});
	
	
	$('article.profileContent').load(profileGet({id: userid, theAction: "activities"}));
	
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
			case "#Active":
				$("#nav").load(active());
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
		$('#filter a').removeClass("active");
		$('#filter a[href=#Search]').addClass("active");
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
			$("#section input[name=title]").foc();
			$('#nav a').removeClass("active");
			$("#section form").submit(function() {
				var title = $("#section input[name=title]").value();
				var content = $("#section textarea[name=content]").value();
				var tags = $("#section input[name=tags]").value();
				$("#section input, #section textarea").removeClass("error");
				if(!title) {
					$("#section input[name=title]").addClass("error");
				} else if(!content) {
					$("#section textarea[name=content]").addClass("error");
				} else {
					$.post(add(), {title: title, content: content, tags: tags}, function(data) {
						if(data.success == 1) {
							$("#section").load(questionsGet({id: data.id}));	
							$("#filter a.active").click();
						} else {
							alert("Error");
						}
					}, "json");
				}
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
