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
		if($('nav .jspVerticalBar').length >= 1){
			$('nav .jspPane').css({'margin-left': '15px'}).width($('nav .jspPane').width()+9);
		}
	}
}

function token() {
	return $("#authenticityToken input").val();
}

function noData() {
	return {authenticityToken: token()};
}

$(function() {
	
	$('section').jScrollPane();
	$('nav').jScrollPane({scrollbarOnLeft: true});
	
	reinitialise();
	$(window).resize(reinitialise);
	$(window).ajaxComplete(reinitialise);
	
	$('#topaction a').livequery(function() {
		$('nav').addClass('noshadow');
	}, function() {
		if($('#topaction a').size() == 0)
			$('nav').removeClass('noshadow');
	});
	
	// pulldown menus
	$('.pulldown > a').click(function() {
		$(this).toggleClass('active').next().toggle();
		$('.pulldown > a').not(this).removeClass('active').next().hide();
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
	
	var validEmail = false;
	var validUsername = false;
	var validPW = false;
	$('input#registrationSubmit').hide();
	
	$('input#registrationEmail').keyup(function(){
		var email = $('input#registrationEmail').val();
		if (email != 0) {
			
			if (isValidEmailAddress(email)) {
				$('input#registrationEmail').addClass("valid").removeClass("invalid");
				validEmail = true;
			}
			else {
				$('input#registrationEmail').addClass("invalid").removeClass("valid");
				validEmail = false;
			}
		}
		else {
			$('input#registrationEmail').removeClass("valid").removeClass("invalid");
			validEmail = false;
		}		
	});
	
	$('input#registrationUser').keyup(function() {	
	      var username = $('input#registrationUser').val();
	      if(username!=0) {
	       
	         jQuery.getJSON(checkUserExists({name: username}), function(data) {
	           if(!data) {
	             $('input#registrationUser').addClass("valid").removeClass("invalid");
	             validUser = true;
	           }
	           else{
	             $('input#registrationUser').addClass("invalid").removeClass("valid"); 
	             validUser = false;
	           }	         
	         }); 
	      }
	      else {
	       $('input#registrationUser').removeClass("valid").removeClass("invalid");
	       validUser = false;
	      }	
	});
	
	$('input#registrationPw2').keyup(function() {
	   var password = $('input#registrationPw').val();
	   var password2 = $('input#registrationPw2').val();
	   if(password!=0 && password2!=0 && password == password2) {
	       validPW = true;	     
	   }
	   else{
	       validPW = false;
	   }
	});
	
	$('div.pulldown input').keyup(function(){
	   if(validEmail && validUser && validPW) {
	     $('input#registrationSubmit').show();  
	   }
	   else {
	     $('input#registrationSubmit').hide();
	   }
	});


});

function isValidEmailAddress(emailAddress) {
	var pattern = new RegExp(/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i);
	return pattern.test(emailAddress);
}

function isValidPW(password){
	var pattern = new RegExp(/^[A-Za-z0-9!@#$%^&*()_]{4,20}$/);
	return pattern.test(password);
}

