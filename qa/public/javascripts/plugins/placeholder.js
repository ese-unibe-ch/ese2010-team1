(function($) {

$.extend($.fn, {
	value: function() {
		return this.hasClass("placeholder") ? "" : this.val();
	},
	foc: function() {
		this.focus();
		this.attr("focused", "on");
		return this;
	}
});


$(function() { 
	$('input[placeholder]').livequery(function() {
		var placeholder = $(this).attr("placeholder");
		if($(this).attr("focused") != "on")
			$(this).val(placeholder).addClass("placeholder");
		$(this).focus(function() {
			if($(this).val() == placeholder)
				$(this).val("").removeClass("placeholder");
		});
		$(this).blur(function() {
			if($(this).val() == "")
				$(this).val(placeholder).addClass("placeholder");
		});
	});
});

})(jQuery);