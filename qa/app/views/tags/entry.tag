%{
	question = _type == "question" || (_entry != null && _entry instanceof models.Question && _type != "answer")
	answer = !question
}%

*{ navigation representation }*
#{if _display == "nav"}
		
		<a href="#!${_entry.id}"#{if _active} class="active"#{/if} title="${_entry.title}">${_entry.title.trim(35)}</a>
	
#{/if}

*{ form representation }*
#{elseif _display == "form"}

	<article class="#{if _type}new #{/if}entry ${question?"question":"answer"}">
	<form method="post" action="#{if _type}@{Questions.answer(_entry?.id)}#{/if}#{else}@{Questions.edit(_entry?.id)}#{/else}" enctype="multipart/form-data">
	
		<menu>
		<li><a href="#" class="up disabled"></a></li>
		<li class="rating">0</li>
		<li><a href="#" class="down disabled"></a></li>
		</menu>
	
		<h3>
		#{if question}
			#{if _type}
				#{field 'title'}
					<input type="text" name="${field.name}" placeholder="Title" class="${field.errorClass}" />
				#{/field}
			#{/if}#{else}
				${_entry?.title}
			#{/else}
		#{/if}#{else}
			#{if _type}New #{/if}Answer
		#{/else}
		</h3>
		<div>
		#{field 'content'}
        	<textarea name="${field.name}" class="${field.errorClass}">#{ifnot _type}${_entry?.content}#{/ifnot}</textarea>
		#{/field}
		
		#{if question}
			<input type="text" id="tags" name="tags" placeholder="Tags" value="${_entry?.tagsToString()}" />
		#{/if}#{else}
			<input type="file" name ="file" />
		#{/else}
		
		<input type="submit" value="Post" />
		#{if _entry && !_type}
			<a class="cancel" href="#${_entry.id}">cancel</a>
		#{/if}
		</div>
	</form>
	</article>

#{/elseif}


*{ full representation }*
#{else}

	#{if _display != "innerHTML"}
		<article id="entry${_entry.id}" class="entry ${question.yesno('question','answer')}">
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
		<a href="@{Users.profile(_entry.owner.id)}" class="owner">
			${_entry.owner.name} (${_entry.owner.reputation})
		</a>
	
		${question ? _entry.title.slice(55) : "Answer"}
		#{setBestAnswer answer:_entry, user:_user /}
	</h3>	
	
	<div>
	
		*{ content }*
	
		<div class="content">
		${_entry.content.nl2br()}
		</div>
		
		*{ tags }*
		
		#{if question}
			<div class="tags">
			#{list items:_entry.tags , as:'tag'}
				<span>${tag.name}</span>
			#{/list}
			</div>
		#{/if}
		
		
		*{ files }*
		
		#{elseif _entry.getFiles().size()>0}
			#{list items:_entry.getFiles(), as:'file'}
				<span class="file">
					<a href="@{Questions.getFile(file.id)}">${file.getFilename()}</a>
					#{if _user==file.owner}
						<a class="deleteFile" href="@{Questions.deleteFileEntry(file.id, file.entry.question.id)}">x</a>
					#{/if}
				</span>
			#{/list}
		#{/elseif}
		
		
		*{ controls }*
		
		<div class="controls">
			#{if _user == _entry.owner || _user?.isAdmin}
				<a href="#${_entry.id}" class="edit">edit</a>
			#{/if}
			
			#{if _entry.isReported() && _user}
				<span class="attention" title="This entry is reported as suspicious from ${_entry.reports.size()} users!"></span>
			#{/if}
			
			#{secure.check 'isAdmin'}
		  		<a href="@{Questions.delete(_entry.id)}" title="delete entry">delete</a>
			#{/secure.check}
			
			#{if _entry.states.size() > 1}
				<a href="#" class="versions">versions</a>
				<div class="versions">
					#{list items:_entry.states, as:'state'}
						<a href="#${state.id}">${state.timestamp.format('dd.MM.yy hh:mm')} ${state.user.name}</a>
					#{/list}
				</div>
				
			#{/if}

			
			#{if _user}
				<a href="#" class="showform">comment</a>
			#{/if}
			
			
			#{if _user != _entry.owner && !_entry.isReportedFrom(_user) && _user}
                <a href="#" class="reportButton">report</a>
                
                <div class="report">
                    #{report entry:_entry, isComment:'false'/}
                </div>
            #{/if}
		</div>
		
		<span class="date">
			${_entry.timestamp.format('dd.MM.yy hh:mm')}
		</span>
		
	</div>
	
	*{ comments }*
	
	#{if _entry.listComments().size() > 0}
		#{list items:_entry.listComments(), as:'comment'}
			<div class="comment">
				<a href="@{Users.profile(comment.owner.id)}" class="owner">
					${comment.owner.name} (${comment.owner.reputation})
				</a>
				<p>${comment.content.nl2br()}</p>
				<div class="controls">
					#{if _user== comment.owner || _user?.isAdmin}
						<a href="#${comment.id}" class="deleteComment">delete</a>
					#{/if}
					#{if _user}
						<a href="#" class="showform">reply</a>
					#{/if}
					#{if _user != comment.owner && !comment.isReportedFrom(_user) && _user}
						<a href="#" class="reportButton">report</a>
				
						<div class="report">
							#{report entry:comment, isComment:'true'/}
						</div>
					#{/if}
					#{if comment.isReported() && _user}
						<span class="attention" title="This entry is reported as suspicious from ${_entry.reports.size()} users!"></span>
					#{/if}
					#{if _user!=comment.owner}
						#{if comment.likedBy(_user)}
						<a href="#${comment.id}" id="unlike" class="likeComment">unlike</a>
						#{/if}#{else}
						<a href="#${comment.id}" id="like" class="likeComment">like</a>
						#{/else}
					#{/if}
					<span class="like">liked by ${comment.fans.size()} people</span>
				</div>
				
			 </div>
		#{/list}
	#{/if}

	#{if _user}
	<div class="comment form">
	#{form @Questions.comment(_entry.id)}
		#{field 'content'}
			<textarea name="${field.name}" class="${field.errorClass}"></textarea>
		#{/field}
		<input type="hidden" name="id" value="${_entry.id}" />
		<input type="submit" value="Post" />
	#{/form}
	</div>
	#{/if}

	#{if _display != "innerHTML"}
		</article>
	#{/if}
#{/else}
