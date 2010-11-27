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
	
	$("#search").submit(function() {
		return false;
	});
	
	// handle absolut url
	var id = /#!(\d+)$/.exec(self.location);
	if(id) {
		$("#section").load(getQuestion({id: id[1]}));
	}
	
	// load question
	$('nav a').livequery('click', function(event) {
		var a = this;
		$.get(getQuestion({id: this.hash.substr(2)}), function(data) {
			$('section').data('jsp').scrollToY(0, false);
			$('#section').html(data);
			$('#nav a').removeClass("active");
			$(a).addClass("active");
		});
	});
	
	// new question
	$("a#newQuestion").click(function() {
		$.get(questionForm(), {type: "question"}, function(data) {
			$("#section").html(data);
			$("#section input[name=title]").foc();
			
			var tags;		
			$.getJSON(tagsList(), function(data) {
					tags = data;
			});
				
			$('input#tags').autocomplete({
				minLength: 0,
				source: function( request, response ) {
					response( $.ui.autocomplete.filter(
						tags, extractLast( request.term ) ) );
				},
				focus: function() {
					return false;
				},
				select: function( event, ui ) {
					var terms = split(this.value);
					terms.pop();
					terms.push( ui.item.value );
					terms.push( "" );
					this.value = terms.join( ", " );
					return false;
				}
			});
			
			
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
							var url = /^(.*)#(.*)$/.exec(self.location);
							url = url ? url[1] : self.location;
							self.location = url + "#!" + data.id;
						} else {
							self.location = login();
						}
					}, "json");
				}
				return false;	
			});
		});
		return false;
	});
	
	// answer
	$(".entry.answer > form").livequery('submit', function() {
		var content = $("#section .entry > form textarea[name=content]").value();
		$("#section input, #section textarea").removeClass("error");
		if(!content) {
			$("#section textarea[name=content]").addClass("error").focus();
		} else {
			var id = $('.entry.question').attr('id').substr(5);
			
			if($("#section input[type=file]").value() != "") {
				return true;
			} else {
				$.post(answerQuestion({id: id}), {content: content}, function(data) {
					if(data.success == 1) {
						$("#section article:last-child").load(getAnswer({id: data.id}));	
					} else {
						self.location = login();
					}
				}, "json");
			}
		}
		return false;	
	});
	
	// edit
	$(".edit").livequery('click', function() {
		var entry = $(this).parents('article');
		$.get(questionForm(), {id: this.hash.substr(1)}, function(data) {
			entry.replaceWith(data);
		});
	});
	
	// versions
	$('a.versions').livequery('click', function() {
		$(this).next('div.versions').toggle();
		return false;
	});
	
	// write comments
	$('.entry .showform').livequery('click', function() {
		$(this).parents('article').find('div.comment.form').show();
		reinitialise();
	});
	
	// delete comments
	$('.deleteComment').livequery('click', function() {
		$.get(deleteComment({id: this.hash.substr(1)}));
		$(this).parents('div.comment').hide();
	});
	
	$(".versions a").click(function() {
		$(this).parents("article").find("div.content").load(contentVersion({id: this.hash.substr(1)}));
		$(this).parents("div.versions").hide();
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


function split( val ) {
	return val.split( /,\s*/ );
}
function extractLast( term ) {
	return split( term ).pop();
}