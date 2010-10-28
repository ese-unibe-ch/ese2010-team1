%{
	canSet = _user && _user == _answer.question.owner && _answer.canBeBestAnswer()
}%

#{if _answer.isBestAnswer()}
	#{if canSet}
		<a href="@{Secured.resetBestAnswer(_answer.question.id)}">
			<img src="@{'/public/images/star_orange.png'}" alt="best answer" title="reset best answer" />
		</a>
	#{/if}#{else}
		<img src="@{'/public/images/star_orange.png'}" alt="best answer" title="best answer" />
	#{/else}
#{/if}#{elseif canSet}
	<a href="@{Secured.setBestAnswer(_answer.id)}">
		<img src="@{'/public/images/star_grey.png'}" alt="set best answer" title="select best answer" />
	</a>
#{/elseif}
