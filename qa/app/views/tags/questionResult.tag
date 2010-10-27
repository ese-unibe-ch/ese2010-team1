<li onclick="goto('@@{Application.question(_question.id)}')">
	
	<h3 class="hl-content">${_question.title()} </h3>
	<p class="hl-content">
		${_question.content().nl2br()}
	</p>
	#{date _question /}
	<a href="@{Application.question(_question.id)}">
		<h2>by ${_question.owner().name} (${_question.owner().reputation()})</h2>
	</a>
</li>