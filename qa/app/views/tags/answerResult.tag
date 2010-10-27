<li onclick="goto('@@{Application.question(_answer.question.id)}')">
	
	<p class="hl-content">
		${_answer.content().nl2br()}
	</p>
	#{date _answer /}
	
	<a href="@{Application.question(_answer.question.id)}#${_answer.id}">
		<h3>by ${_answer.owner().name} (${_answer.owner().reputation()})</h3>
	</a>
</li>