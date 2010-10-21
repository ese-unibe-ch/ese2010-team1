<span class="vote">
	#{if _user && _user != _entry.owner()}
	<a href="
		#{if _entry instanceof models.Answer}
			@{Secured.voteAnswerDown(_entry.question().id, _entry.id)}
		#{/if}#{else}
			@{Secured.voteQuestionDown(_entry.id)}

		#{/else}
	">-</a>
	#{/if}
	
	${_entry.rating()}

	#{if _user && _user != _entry.owner()}
	<a href="
		#{if _entry instanceof models.Answer}
			@{Secured.voteAnswerUp(_entry.question().id, _entry.id)}
		#{/if}#{else}
			@{Secured.voteQuestionUp(_entry.id)}

		#{/else}
	">+</a>
	#{/if}

</span>