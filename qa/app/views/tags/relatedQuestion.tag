<script type="text/javascript" src="jquery-1.4.2.min.js"></script>
<script type="text/javascript">

function showRelated(value){
	if(value!= ""){
		$("#ajaxresults").show();
		
	$.post("{{=URL(r=request,f='Secured.relatedQuestions')}}", {title:value},function(result) {
		$("#ajaxresults").html(result);
	});

	}
		
	else{
		$("#ajaxresults").hide();
	}
}
</script>

<div id = "ajaxresults">test 
#{list items:relatedQuestion, as:'question'}
	<h1>${question.title}</h1>
#{/list}
</div>

<p>
  <label for="relatedQuestion">Test:</label>
  <br />
  <input id="relatedQuestion" autocomplete="off"  type="text" onkeyup="showRelated(this.value);" />
</p>
