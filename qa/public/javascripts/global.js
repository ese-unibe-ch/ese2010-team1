function reinitialise() {	
	$('html').height($(window).height()-20);
	var height = $('body').height() - 70;
	$('section, nav').height(height);
	if($('#topaction').size() >= 1) {
		var margin = $('#topaction').height()
		$('nav').css({'margin-top': margin + 'px'});
		$('nav').height(height - margin);
	}
	$('section').data('jsp').reinitialise();
	if($('nav').data('jsp')) {
		$('nav').data('jsp').reinitialise();
		$('section').css({width: "600px"});
		$('nav').css({width: "360px"});
	}
}

$(function() {
	
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
		if($(this).find("a").length > 0 && !$(this).find("a").hasClass("notification"))
			self.location = $(this).find("a").attr("href");
	});
	
	
	$('a.notification').livequery('click', function(event) {
		var url = $(this).attr('href');
		$.get(setNotificationAsRed({id: this.hash.substr(1) }), function() {
			self.location = url;
		});
		return false;
	});
	
	
	// registration validation
	$('input#registrationSubmit').hide();
	
	$('input#registrationEmail').keyup(function(){
		var email = $('input#registrationEmail').val();
		if (email != 0) {
			
			if (isValidEmailAddress(email)) {
				$('input#registrationEmail').addClass("valid").removeClass("invalid");
			}
			else {
				$('input#registrationEmail').addClass("invalid").removeClass("valid");
			}
		}
		else {
			$('input#registrationEmail').removeClass("valid").removeClass("invalid");
		}
		
	});


});

function isValidEmailAddress(emailAddress) {
	var pattern = new RegExp(/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i);
	return pattern.test(emailAddress);
}

function isValidPW(password, password2){
	var pattern = new RegExp(/^[A-Za-z0-9!@#$%^&*()_]{6,20}$/);
	return pattern.test(password) && password == password2;
}

