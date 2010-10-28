<div id= "content${_entry.id }">
	<p>
		${_entry.content }
	</p>
</div>

<div id="edit${_entry.id }" style="display:none">
	#{form @Secured.edit(_entry.id)} 
		#{field 'content'}
			<textarea name="${field.name}" class="${field.errorClass}">${_entry.content }</textarea> 
		#{/ field}
		<input type="submit" value="Save" /> 
		#{if _entry instanceof models.Question}
			<input type="button" value="Cancel" onclick="goto('@@{Application.question(_entry.id)}')" />
		#{/if }	
		#{if _entry instanceof models.Answer}
			<input type="button" value="Cancel" onclick="goto('@@{Application.question(_entry.question.id)}')" />
		#{/if }
	#{/form }
</div>

#{if _user == _entry.owner }
	<a class="edit" onclick="return showEditBox('content${_entry.id }', 'edit${_entry.id }');">edit</a>
#{/if }
