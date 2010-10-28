<li onclick="goto('@@{Application.question(_question.id)}')">
	
	<h3 class="hl-content">${_question.title} </h3>
	<p class="hl-content">
		${_question.content.nl2br()}
	</p>
	#{list items:_question.tags, as:'tag'}
     <a href="#" class="tags">${tag.name}</a>
     #{/list}
	
	#{date _question /}
	<a href="@{Application.question(_question.id)}">
		<h3>by ${_question.owner.name} (${_question.owner.reputation()})</h3>
	</a>
</li>