<li>
	#{vote entry:_question, user:_user /}
	
	 #{secure.check 'admin'}
	  <a href="@{Secured.deleteEntry(_question.id)}">Delete Question</a>
	 #{/secure.check}
	 
	 <a href="@{Application.question(_question.id)}">
	 <h3>${_question.title()}</h3>
	 </a>
	 
	<p>
		#{content entry:_question, user:_user /}
	</p>
	
	#{list items:_question.tags, as:'tag'}
     <a href="#" class="tags">${tag.name}</a>
     #{/list}
	
	#{date _question /}

	<a href="@{UserFuncs.showProfile(_question.owner().id)}">
		<h3>by ${_question.owner().name} (${_question.owner().reputation()})</h3>
	</a>

</li>

