%{
	question = _entry instanceof models.Question
	answer = !question
}%

*{ navigation representation }*

#{if _display == "nav"}
	<a href="#${_entry.id}">${_entry.title}</a>
#{/if}


*{ full representation }*

#{else}
	<article class="entry ${question.yesno('question','answer')}">
	
	
	*{ vote }*
	<menu>
		<li>+</li>
		<li>${_entry.rating()}</li>
		<li>-</li>
	</menu>
	*{ title }*
	<h3>
		<div style="float: left; width: 50%;">${question ? _entry.title : "Answer"}</div>
		<div style="float: left; width: 50%; text-align: right;"><a href="@{Users.profile(_entry.owner.id)}"> ${_entry.owner.name}</a></div>
	</h3>
	
	
	
	*{ content }*
	<p>${_entry.content.nl2br()}</p>
	
	</article>
#{/else}




*{

%{
	question = _entry instanceof models.Question
	answer = !question
	comments = _entry.listComments()
	files = _entry.getFiles()
	
	if(_user!=null) {
	
		isAdmin = _user.isAdmin
	}
	else {
		isAdmin = false;
	}
	
	
}%

<li class="entry" id="${question ? 'question' : _entry.id}">
	*{ title 
	#{if question}
		<a href="@{Application.question(_entry.id)}">
			<h3>
				${_entry.title}
			</h3>
		</a>
	#{/if}
	
	*{ content 
	<div id= "content${_entry.id }">
		<p>
			${_entry.content.nl2br()}
		</p>

	#{if comments.size() > 0}
		<h3>Comments</h3>
		<ul>
			#{list items:comments, as:'comment'}
				<li>${comment.owner.name} -  ${comment.content.nl2br()} 
				#{if _user== comment.owner || isAdmin}
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


		#{if files.size()>0}
		File(s):
		#{list items:files, as:'file'}
		
		<a href="@{Application.getFile(file.id)}" target="_blank">${file.uploadFilename}</a>
		#{if _user==file.owner}
		<a href="@{Secured.deleteFileEntry(file.id, file.entry.question.id)}">delete</a>
		#{/if}
		
		#{/list}
		#{/if}
		
	</div>
	
	*{ edit form 
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

	*{ tags 
	#{if question}
		<div class="tags">
			#{list items:_entry.tags, as:'tag'}
     			<a href="#" class="tags">${tag.name}</a>
    		 #{/list}
		</div>
	#{/if}

	*{ actions 
	<div class="actions">
		#{secure.check 'isAdmin'}
		  <a href="@{Secured.deleteEntry(_entry.id)}">
		  	<img src="@{'/public/images/delete.png'}" alt="delete" title="delete" />
		  </a>
		#{/secure.check}
		#{if answer}
			#{setBestAnswer answer:_entry, user:_user /}
		#{/if}

		#{if _user == _entry.owner|| isAdmin}
			<a href="#" onclick="return showEditBox('content${_entry.id }', 'edit${_entry.id }');">
		  		<img src="@{'/public/images/edit.png'}" alt="edit" title="edit" />
			</a>
		#{/if }
		#{version entry:_entry /}
	</div>
	
	*{ info 
	<div class="info">
		<span>${_entry.timestamp.format('dd.MM.yyyy hh:mm:ss')}</span>
		<a href="@{UserFuncs.showProfile(_entry.owner.id)}">
			${_entry.owner.name} (${_entry.owner.reputation()})
		</a>
	</div>
	
	*{ vote 
	#{vote entry:_entry, user:_user /}
	
</li>

}*