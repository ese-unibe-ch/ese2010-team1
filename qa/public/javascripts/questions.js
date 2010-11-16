$(function() {

	// filter questions
	$('#filter a').click(function() {
		switch(this.hash) {
			case "#Mine":
				$("#nav").load(myQuestions());
				break;
			case "#Hot":
				$("#nav").load(hotQuestions());
				break;
			case "#Active":
				$("#nav").load(activeQuestions());
				break;
			case "#Search":
				$("#nav").load(searchQuestions({string: $("#search input").val()}));
				$("#search input").focus();
				break;
		}
		$('#filter a').removeClass("active");
		$(this).addClass("active");
		return false;
	});
	
	// search questions
	$("#search input").keyup(function() {
		$("#nav").load(searchQuestions({string: this.value}));
		$('#filter a').removeClass("active");
		$('#filter a[href=#Search]').addClass("active");
	});
	
	// handle absolut url
	var id = /#!(\d+)$/.exec(this.location);
	if(id) {
		$("#section").load(getQuestion({id: id[1]}));
	}
	
	// load question
	$('nav a').livequery('click', function(event) {
		var a = this;
		$.get(getQuestion({id: this.hash.substr(2)}), function(data) {
			$('#section').html(data);
			$('#nav a').removeClass("active");
			$(a).addClass("active");
		});
	});
	
	// new question
	$("#new a").click(function() {
		$.get(questionForm({type: "question"}), function(data) {
			$("#section").html(data);
			$("#section input[name=title]").foc();
			$('#nav a').removeClass("active");
			$("#section form").submit(function() {
				var title = $("#section input[name=title]").value();
				var content = $("#section textarea[name=content]").value();
				var tags = $("#section input[name=tags]").value();
				$("#section input, #section textarea").removeClass("error");
				if(!title) {
					$("#section input[name=title]").addClass("error").focus();
				} else if(!content) {
					$("#section textarea[name=content]").addClass("error").focus();
				} else {
					$.post(addQuestion(), {title: title, content: content, tags: tags}, function(data) {
						if(data.success == 1) {
							$("#section").load(getQuestion({id: data.id}));	
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
	
	// answer
	$("#section > a").livequery('click', function() {
		$(this).hide();
		var id = this.hash.substr(1);
		$.get(questionForm({type: "answer"}), function(data) {
			$("#section").append(data);
			$("#section textarea").foc();
			$("#section form").submit(function() {
				var content = $("#section textarea[name=content]").value();
				$("#section input, #section textarea").removeClass("error");
				if(!content) {
					$("#section textarea[name=content]").addClass("error").focus();
				} else {
					$.post(answerQuestion({id: id}), {content: content}, function(data) {
						if(data.success == 1) {
							$("#section article:last-child").load(getAnswer({id: data.id}));	
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