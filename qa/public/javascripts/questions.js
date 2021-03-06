$(function() {

	$('nav').bind('jsp-scroll-y', function(event, scrollPositionY, isAtTop, isAtBottom){
		if(isAtBottom)
			loadMore();
	});

	// filter questions
	$('#filter a').click(changeFilterMode);
	
	// search mode
	newQuestionContent = $("#topaction").html();
	$("#topaction a").livequery("click", function() {
		switch(this.hash) {
		case "#usersearch":
			userSearch = true;
			break;
			
		case "#questionsearch":
			userSearch = false;
			break;
			
		case "#newQuestion":
			newQuestion();
			
		default:
			return false;
		}
		
		$("#search input").keyup();
		searchModeLink();
		return false;
	});
	
	// search questions
	$("#search input").keyup(function() {
		if(this.value.length >= 3)
			search();
	});
	
	$("#search").submit(function() {
		search();
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
	
	
	// answer
	$(".new.entry.answer > form").livequery('submit', function() {
		var content = $("#section .entry > form textarea[name=content]").value();
		$("#section input, #section textarea").removeClass("error");
		if(!content) {
			$("#section textarea[name=content]").addClass("error").focus();
		} else {
			var id = $('.entry.question').attr('id').substr(5);
			
			if($("#section input[type=file]").val() != "") {
				return true;
			} else {
				$.post(answerQuestion({id: id}), {content: content, authenticityToken: token}, function(data) {
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
	
	// cancel edit
	$(".cancel").livequery('click', function() {
		$(this).parents('article').load(getAnswer({id: this.hash.substr(1)}));	
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
		return false;
	});
	
	// submit comments
	$('.entry div.comment.form form').livequery("submit", function() {
		var url = comment({id: $(this).find("input[name=id]").val()});
		$(this).parents("article").load(url, {authenticityToken: token, content: $(this).find("textarea[name=content]").val()});
		return false;
	});
	
	// delete comments
	$('.deleteComment').livequery('click', function() {
		$.get(deleteComment({id: this.hash.substr(1)}), noData());
		$(this).parents('div.comment').hide();
	});
	
	$(".versions a").click(function() {
		$(this).parents("article").find("div.content").load(contentVersion({id: this.hash.substr(1)}));
		$(this).parents("div.versions").hide();
	});

	
	// vote up
	$('a.up').livequery('click', function(event) {
		var entry = $(this).parents("article");
		$.get(voteUp({id: this.hash.substr(1)}), noData(), function(data) {
			entry.html(data);
		});
		return false;
	});
	
	// vote down
	$('a.down').livequery('click', function(event) {
		var entry = $(this).parents("article");
		$.get(voteDown({id: this.hash.substr(1)}), noData(), function(data) {
			entry.html(data);
		});
		return false;
	});
	
	// remove vote
	$('a.delete').livequery('click', function(event) {
		var entry = $(this).parents("article");
		$.get(removeVote({id: this.hash.substr(1)}), noData(), function(data) {
			entry.html(data);
		});
		return false;
	});
	
	// set best answer
	$('.set.bestAnswer').livequery('click', function(event) {
		$("#section").load(setBestAnswer({id: this.hash.substr(1)}), noData());
		return false;
	});
	
	// reset best answer
	$('.reset.bestAnswer').livequery('click', function(event) {
		$("#section").load(resetBestAnswer({id: this.hash.substr(1)}), noData());
		return false;
	});
	
	// report
	$('#report').livequery('click', function(event) {
		var entry = $(this).parents("article");
		$.get(report({id: this.hash.substr(1)}), function(data) {
			entry.html(data);
		});
		return false;
	});
	
	$('a.reportButton').livequery('click', function() {
		$(this).next('div.report').toggle();
		return false;
	});
	
	$('#dontReport').livequery('click', function() {
		$(this).parents('div.report').hide();
		return false;
	});
	
	// like
	$('#like').livequery('click', function(event) {
		var entry = $(this).parents("article");
		$.get(like({id: this.hash.substr(1)}), function(data) {
			entry.html(data);
		});
		return false;
	});
	$('#unlike').livequery('click', function(event) {
		var entry = $(this).parents("article");
		$.get(unlike({id: this.hash.substr(1)}), function(data) {
			entry.html(data);
		});
		return false;
	});

});

var userSearch = false;
var newQuestionContent = "";

function searchModeLink() {
	if(userSearch) {
		$("#topaction").html('<a href="#questionsearch">Are you looking for questions?</a>');
	} else {
		$("#topaction").html('<a href="#usersearch">Are you looking for users?</a>');
	}
	reinitialise();
}

function newQuestionLink() {
	$("#topaction").html(newQuestionContent);
	reinitialise();
}

var filterMode = "#Hot";
var lastQuestion = false;
var loading = false;
var page = 2;
var loaderID = 1;

function changeFilterMode() {
	if(this.hash == "#Search") {
		$("#search input").focus();
		search();
	} else {
		filterMode = this.hash;
		page = 1;
		lastQuestion = false;
		loading = false;
		loadMore(true);
		$('#filter li').removeClass("active");
		$(this).parent().addClass("active");
	}
	return false;
}

function loadMore(replace) {
	if(loading || lastQuestion)
		return;
		
	var url;
	
	switch(filterMode) {
		case "#Mine":
			url = myQuestions({page: page});
			break;
		case "#Hot":
			url = hotQuestions({page: page});
			break;
		case "#Active":
			url = activeQuestions({page: page});
			break;
		case "#Search":
			var string = $("#search input").first().val();
			url = userSearch ? searchUsers({string: string, page: page}) : searchQuestions({string: string, page: page});
			break;
	}
	
	if(replace) {
		if(filterMode == "#Search") {
			searchModeLink();
		} else {
			newQuestionLink();
		}
		$('#nav').empty("");
	}
	
	$('#nav').append('<div class="loader"></div>');
	reinitialise();
	
	loaderID++;	
	var id = loaderID;
	loading = true;
	
	$.get(url, function(data) {
		if(id != loaderID)
			return;
		if(replace) {
			var itemCount = 0;
			$("#nav").html(data);
			$("nav").data('jsp').scrollToY(0,false);
		} else {
			var itemCount = $("#nav a").length;
			$("nav .loader").detach();
			$("#nav").append(data);
		}
		page++;
		if($("#nav a").length - itemCount < NUMBER_OF_LOADED_QUESTIONS)
			lastQuestion = true;
		loading = false;
		
		if($("#nav a").length == 0) {
			$("#nav").html('<span class="info">No entries found.</span>');
		}
	});
}

function search() {
	filterMode = "#Search";
	lastQuestion = false;
	page = 1;
	$('#filter li').removeClass("active");
	$('#filter a[href=#Search]').parent().addClass("active");
	var string = $("#search input").val();
	if(string.length < 3) {
		if(string.length == 0) {
			$("#nav").html('<span class="info arrow">Type in your search term.</span>');
		} else {
			$("#nav").html('<span class="info">Your search term is too short.</span>');
		}
		$("#topaction").empty();
		reinitialise();
	} else {
		loadMore(true);
	}
}

function split( val ) {
	return val.split( /,\s*/ );
}
function extractLast( term ) {
	return split( term ).pop();
}

function newQuestion() {
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
					$.post(addQuestion(), {title: title, content: content, tags: tags, authenticityToken: token}, function(data) {
						if(data.success == 1) {
							$("#section").load(getQuestion({id: data.id}));	
							page = 1;
							lastQuestion = false;
							loadMore(true);
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
}