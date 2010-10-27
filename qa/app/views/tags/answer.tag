<li>	
	#{secure.check 'admin'}
	  <a href="@{Secured.deleteEntry(_answer.id)}">Delete Answer</a>
	#{/secure.check}		
			<a name="${_answer.id}"></a>
			<p>
				${_answer.content().nl2br()}
			</p>
			#{date _answer /}

			#{vote entry:_answer, user:_user /}
			<h3><a href="@{UserFuncs.showProfile(_answer.owner().id)}">by ${_answer.owner().name} (${_answer.owner().reputation()})</a></h3>											
			#{setBestAnswer answer:_answer, user:_user /}
											
</li>