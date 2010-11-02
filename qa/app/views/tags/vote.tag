%{
	canVote = _user && _entry.canVote(_user)
	isAnswer = _entry instanceof models.Answer
}%
<span class="vote">
	#{if canVote}
		#{if _entry.alreadyVoted(_user, false)}
			<a href="
				#{if isAnswer}
					@{Secured.removeAnswerVote(_entry.question.id, _entry.id)}
				#{/if}#{else}
						@{Secured.removeQuestionVote(_entry.id)}
				#{/else}
			">x</a>
		#{/if}#{else}
			<a href="
				#{if isAnswer}
					@{Secured.voteAnswerDown(_entry.question.id, _entry.id)}
				#{/if}#{else}
					@{Secured.voteQuestionDown(_entry.id)}
				#{/else}
			">-</a>
		#{/else}
	#{/if}
	
	${_entry.rating()}

	#{if canVote}
		#{if _entry.alreadyVoted(_user, true)}
			<a href="
				#{if isAnswer}
					@{Secured.removeAnswerVote(_entry.question.id, _entry.id)}
				#{/if}#{else}
						@{Secured.removeQuestionVote(_entry.id)}
				#{/else}
			">x</a>
		#{/if}#{else}
			<a href="
				#{if isAnswer}
					@{Secured.voteAnswerUp(_entry.question.id, _entry.id)}
				#{/if}#{else}
					@{Secured.voteQuestionUp(_entry.id)}
				#{/else}
			">+</a>
		#{/else}
	#{/if}

</span>