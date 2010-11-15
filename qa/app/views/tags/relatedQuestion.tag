<script type="text/javascript" src="jquery-1.4.2.min.js"></script>

%{
<script type="text/javascript">
	var relatedQuestions;
	function related(value){
		relatedQuestions = #{jsAction @Application.relatedQuestions(value) /};
	}

	$(function()  {
		
	$("input#title").autocomplete({
		source: relatedQuestions,
		delay: 0,
	});

});

</script>

<input type="text" name="title" id="title" value="Title" onkeyup="related(this.value);" />
