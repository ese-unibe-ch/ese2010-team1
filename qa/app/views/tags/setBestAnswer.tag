<span class="setBestAnswer">

#{if !_answer.question().isBestAnswerSet()}
	#{if _user && _answer.question().owner() == _user}
		#{if _user != _answer.owner()}
			<a href="
				#{if _answer.type() == "Answer"}
					@{Secured.setBestAnswer(_answer.id)}
				#{/if}
			">best Answer</a>
		#{/if}
	#{/if}
#{/if}

#{if _answer.isBestAnswer()}
					*	
	#{if _answer.bestAnswerSetter().canBeUndone()}
		<a href="
			@{Secured.undoBestAnswer(_answer.id)}
		">undo</a>
	#{/if}
#{/if}

</span>