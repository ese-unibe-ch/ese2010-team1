%{
	canVote = _user && _entry.canVote(_user)
	isAnswer = _entry instanceof models.Answer
}%
<span class="vote">
	#{if canVote}
		#{if _entry.alreadyVoted(_user, false)}
			<a href="
				@{Secured.removeEntryVote( _entry.id)}
			">x</a>
		#{/if}#{else}
			<a href="
					@{Secured.voteEntryDown(_entry.id)}
			">-</a>
		#{/else}
	#{/if}
	
	${_entry.rating()}

	#{if canVote}
		#{if _entry.alreadyVoted(_user, true)}
			<a href="
				@{Secured.removeEntryVote( _entry.id)}
			">x</a>
		#{/if}#{else}
			<a href="
					@{Secured.voteEntryUp(_entry.id)}
			">+</a>
		#{/else}
	#{/if}

</span>