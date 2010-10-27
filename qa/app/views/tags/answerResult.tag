<li onclick="goto('@@{Application.question(_answer.question.id)}')">
	<a href="@{Application.question(_answer.question.id)}">
		<h2>${_answer.owner().name}:</h2>
	</a>
	<p class="hl-content">
		${_answer.content().nl2br()}
	</p>
	#{date _answer /}
</li>