<li onclick="goto('@@{Application.question(_question.id)}')">
	<a href="@{Application.question(_question.id)}">
		<h2>${_question.owner().name}:</h2>
	</a>
	<h3>${_question.title()} </h3>
	<p>
		${_question.content().nl2br()}
	</p>
	#{date _question /}
</li>