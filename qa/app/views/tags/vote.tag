%{
	canVote = _user && _entry.canVote(_user)
	isAnswer = _entry instanceof models.Answer
}%
<span class="vote">
	#{if canVote}
	<a href="
		#{if isAnswer}
			@{Secured.voteAnswerDown(_entry.question.id, _entry.id)}
		#{/if}#{else}
			@{Secured.voteQuestionDown(_entry.id)}

		#{/else}
	">-</a>
	#{/if}
	
	${_entry.rating()}

	#{if canVote}
	<a href="
		#{if isAnswer}
			@{Secured.voteAnswerUp(_entry.question.id, _entry.id)}
		#{/if}#{else}
			@{Secured.voteQuestionUp(_entry.id)}

		#{/else}
	">+</a>
	#{/if}

</span>