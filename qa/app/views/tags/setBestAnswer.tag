#{if _answer instanceof models.Answer}

	%{
		canSet = _user && _user == _answer.question.owner && _answer.canBeBestAnswer()
	}%
	
	#{if _answer.isBestAnswer()}
		#{if canSet}
			<a href="#${_answer.question.id}" class="reset bestAnswer" title="reset best answer"></a>
		#{/if}#{else}
			<span class="bestAnswer" title="best answer"></span>
		#{/else}
	#{/if}#{elseif canSet}
			<a href="#${_answer.id}" class="set bestAnswer" title="set best answer"></a>
	#{/elseif}

#{/if}