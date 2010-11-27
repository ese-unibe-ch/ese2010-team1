#{if _entry.states.size() > 1}
<a href="#" class="pulldown">version</a>
<div class="pulldown version">
#{list items:_entry.states, as:'state'}
<a href="#${state.id}">${state.timestamp.format('dd.MM.yy hh:mm')} ${state.user.name}</a>
#{/list}
</div>
#{/if}