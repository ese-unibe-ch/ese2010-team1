<li>
	<a href="#${_hash?_hash:_arg}"#{if _class || _selected} class="${_class?_class:""}${_selected?" active":""}"#{/if}>
		${_arg}
	</a>
</li>