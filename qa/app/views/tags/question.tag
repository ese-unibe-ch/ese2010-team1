<li onclick="goto('@@{Application.question(_question.id)}')">
	#{vote entry:_question, user:_user /}
	
	 #{secure.check 'admin'}
	  <a href="@{Secured.deleteEntry(_question.id)}">Delete Question</a>
	 #{/secure.check}
	 <h3>${_question.title()}</h3>
	<p>
		${_question.content().nl2br()}
	</p>
	#{date _question /}

	
	
	<a href="@{UserFuncs.showProfile(_question.owner().id)}">
		<h3>by ${_question.owner().name} (${_question.owner().reputation()})</h3>
	</a>

</li>

