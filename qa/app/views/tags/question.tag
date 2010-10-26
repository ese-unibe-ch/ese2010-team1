<li onclick="goto('@@{Application.question(_question.id)}')">
	#{vote entry:_question, user:_user /}
	
	 #{secure.check 'admin'}
	  Delete Entry
	 #{/secure.check}
	<p>
		${_question.content().nl2br()}
	</p>
	#{date _question /}

	
	
	<a href="@{UserFuncs.showProfile(_question.owner().id)}">
		<h3>by ${_question.owner().name} (${_question.owner().reputation()})</h3>
	</a>

</li>