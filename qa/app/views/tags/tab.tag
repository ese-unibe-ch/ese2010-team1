<li#{if _class || _selected} class="${_class?_class:""}${_selected?" active":""}"#{/if}>
	<a href="#${_hash?_hash:_arg}">
		${_arg}
	</a>
</li>