<div>
	<div>Do you really want to mark this entry as suspicious?</div>
		
	#{if _isComment==true}
   		 <a href="#${_entry.entry.id}"  id="report">Yes.</a>
	#{/if}
		#{else}
   			 <a href="#${_entry.id}"  id="report">Yes.</a>
		#{/else}
	
	<a href=# id="dontReport">No.</a>
</div>