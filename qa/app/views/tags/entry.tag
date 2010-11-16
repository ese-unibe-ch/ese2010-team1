%{
	question = _type == "question" || (_entry != null && _entry instanceof models.Question)
	answer = !question
}%

*{ navigation representation }*
#{if _display == "nav"}
		
		<a href="#${_entry.id}"#{if _active} class="active"#{/if}>${_entry.title}</a>
	
#{/if}

*{ form representation }*
#{elseif _display == "form"}

	<article class="entry ${question?"question":"answer"}">
	<form>
	
		<menu>
		<li><a href="#" class="up disabled"></a></li>
		<li class="rating">0</li>
		<li><a href="#" class="down disabled"></a></li>
		</menu>
	
		<h3>
		#{if question}
			#{field 'title'}
				<input type="text" name="${field.name}" placeholder="Title" class="${field.errorClass}" />
			#{/field}
		#{/if}#{else}
			Answer
		#{/else}
		</h3>
		<p>
		#{field 'content'}
        	<textarea name="${field.name}" class="${field.errorClass}"></textarea>
		#{/field}
		</p>
		#{if question}
			<input type="text" name="tags" placeholder="Tags" />
		#{/if}
		
		<input type="submit" value="Post" />
	</form>
	</article>

#{/elseif}


*{ full representation }*
#{else}

	#{if _display != "innerHTML"}
		<article class="entry ${question.yesno('question','answer')}">
	#{/if}
	
	*{ vote }*
	%{ 	
		up = _entry.alreadyVoted(_user, true) ? "delete" : "up";
		down =  _entry.alreadyVoted(_user, false) ? "delete" : "down";
		if(!_user || !_entry.canVote(_user)) {
			up += " disabled";
			down += " disabled";
		}
	}%
	<menu>
		<li><a href="#${_entry.id}" class="${up}"></a></li>
		<li class="rating">${_entry.rating()}</li>
		<li><a href="#${_entry.id}" class="${down}"></a></li>
	</menu>
	
	*{ title }*
	<h3>
		${question ? _entry.title : "Answer"}
		#{setBestAnswer answer:_entry, user:_user /}
		<a href="@{Users.profile(_entry.owner.id)}" class="owner">
			${_entry.owner.name} (${_entry.owner.reputation()})
		</a>
	</h3>
	
	*{ content }*
	<p>${_entry.content.nl2br()}</p>
	#{if question}
		#{list items:_entry.tags , as:'tag'}
		
		<div class="tags">${tag.name}</div>
		
		#{/list}
	#{/if}
	
	#{if _display != "innerHTML"}
		</article>
	#{/if}
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
			#{if question}
				<input type="text" name="newTags" id="newTags" value="${_entry.tagsToString()}" />
			<input type="submit" value="Save" /> 
				<input type="button" value="Cancel" onclick="goto('@@{Application.question(_entry.id)}')" />
			#{/if }	
			#{if answer}
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
	
	<div style="clear: both;"></div>
	
	
		#{if comments.size() > 0}
		<h4>Comments</h4>
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

	<h4>Comment</h4>
	#{form @Secured.newComment(_entry.id)}
		#{field 'content'}
			<textarea name="${field.name}" class="${field.errorClass}"></textarea>
		#{/field}
		<input type="submit" value="Post" />
	#{/form}


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
