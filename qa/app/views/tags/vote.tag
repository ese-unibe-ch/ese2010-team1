<span class="vote">
	#{if _user && _user != _entry.owner()}
	<a href="

		#{if _arg.type() == "Answer"}
			@{Secured.voteAnswerDown(_arg.question().id, _arg.id)}
		#{/if}#{else}
			@{Secured.voteQuestionDown(_arg.id)}
		#{/else}
	">-</a>
	#{/if}
	
	${_entry.rating()}

	#{if _user && _user != _entry.owner()}
	<a href="
		#{if _arg.type() == "Answer"}
			@{Secured.voteAnswerUp(_arg.question().id, _arg.id)}
		#{/if}#{else}
			@{Secured.voteQuestionUp(_arg.id)}
		#{/else}
	">+</a>
	#{/if}

</span>