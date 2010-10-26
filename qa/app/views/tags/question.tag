<li onclick="goto('@@{Application.question(_question.id)}')">
	<a href="@{Application.question(_question.id)}">
		<h2>${_question.owner().name}:</h2>
	</a>
	<p>
		${_question.content()}
	</p>
	#{date _question /}
	#{vote entry:_question, user:_user /}
	
	  #{secure.check 'admin'}
	  Delete Entry
	 #{/secure.check}
</li>