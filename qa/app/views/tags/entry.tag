%{
	question = _entry instanceof models.Question
	answer = !question
	comments = _entry.listComments();
}%

<li class="entry" id="${question ? 'question' : _entry.id}">
	*{ title }*
	#{if question}
		<a href="@{Application.question(_entry.id)}">
			<h3>
				${_entry.title}
			</h3>
		</a>
	#{/if}
	
	*{ content }*
	<div id= "content${_entry.id }">
		<p>
			${_entry.content.nl2br() }
		</p>
	

	#{if comments.size() > 0}
		<h3>Comments</h3>
		<ul>
			#{list items:comments, as:'comment'}
				<li>${comment.owner.name} -  ${comment.content.nl2br()} 
				#{if _user.isAdmin || _user== comment.owner}
				<a href="@{Secured.deleteComment(comment.id)}">delete</a>
				#{/if}
				
				 </li>
			#{/list}
		</ul>
	#{/if}

	#{if _user}

	<h3>Comment</h3>
	#{form @Secured.newComment(_entry.id)}
		#{field 'content'}
			<textarea name="${field.name}" class="${field.errorClass}"></textarea>
		#{/field}
		<input type="submit" value="Post" />
	#{/form}


	#{/if}

	</div>
	
	*{ edit form }*
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

	*{ tags }*
	#{if question}
		<div class="tags">
			#{list items:_entry.tags, as:'tag'}
     			<a href="#" class="tags">${tag.name}</a>
    		 #{/list}
		</div>
	#{/if}

	*{ actions }*
	<div class="actions">
		#{secure.check 'admin'}
		  <a href="@{Secured.deleteEntry(_entry.id)}">
		  	<img src="@{'/public/images/delete.png'}" alt="delete" title="delete" />
		  </a>
		#{/secure.check}
		#{if answer}
			#{setBestAnswer answer:_entry, user:_user /}
		#{/if}
		#{if _user == _entry.owner }
			<a href="#" onclick="return showEditBox('content${_entry.id }', 'edit${_entry.id }');">
		  		<img src="@{'/public/images/edit.png'}" alt="edit" title="edit" />
			</a>
		#{/if }
	</div>
	
	*{ info }*
	<div class="info">
		<span>${_entry.timestamp.format('dd.MM.yyyy hh:mm:ss')}</span>
		<a href="@{UserFuncs.showProfile(_entry.owner.id)}">
			${_entry.owner.name} (${_entry.owner.reputation()})
		</a>
	</div>
	
	*{ vote }*
	#{vote entry:_entry, user:_user /}
</li>

