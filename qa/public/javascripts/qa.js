function goto(url) {
	top.location = url;
}

$(function () {
	var tabContainers = $('div.tabs > div');
	tabContainers.hide().filter(':first').show();
	
	$('div.tabs ul.tabNavigation a').click(function () {
		tabContainers.hide();
		tabContainers.filter(this.hash).show();
		$('div.tabs ul.tabNavigation a').removeClass('selected');
		$(this).addClass('selected');
		if(this.hash == "#graph")
			loadGraph();
		return false;
	}).filter(':first').click();
	
	$("a.pulldown").click(function() {$(this).next().toggle();return false});
});

function loadGraph() {
	if(!$("#graph")[0].loaded) {
		jQuery.getJSON( self.location, displayGraph);
		$("#graph")[0].loaded = true;
	}
}

function displayGraph(data, status) {
	var points = [];
	
	for(i in data) {
		points.push([data[i].time, data[i].value]);
	}

    $.plot($("#graphcanvas"), [
        {
            data: points,
            lines: { show: true, steps: true },
            points: { show: false }
        }
    ],	{
    	xaxis: {
    		mode: "time"
    	}
    } );
 }
 
 
 
 
 pic1 = new Image(16, 16); 
			pic1.src = "/public/images/loader.gif";
			
			$(document).ready(function(){
			
			$("#username").change(function() { 
			
			var usr = $("#username").val();
			
			if(usr.length >= 3)
			{
			$("#status").html('<img src="/public/images/loader.gif" align="absmiddle">&nbsp;Checking availability...');
			
			    $.ajax({  
			    type: "POST",  
			    url: userCheckURL,  
			    data: "username="+ usr,  
			    success: function(msg){  
			   
			   $("#status").ajaxComplete(function(event, request, settings){ 
			
				if(msg == 'OK')
				{ 
			        $("#username").removeClass('object_error'); // if necessary
					$("#username").addClass("object_ok");
					$(this).html('&nbsp;<img src="/public/images/accepted.png" align="absmiddle"> <font color="Green"> Available </font>  ');
				}  
				else  
				{  
					$("#username").removeClass('object_ok'); // if necessary
					$("#username").addClass("object_error");
					$(this).html(msg);
				}  
			   
			   });
			
			 } 
			   
			  }); 
			
			}
			else
				{
				$("#status").html('<font color="red">The username should have at least <strong>3</strong> characters.</font>');
				$("#username").removeClass('object_ok'); // if necessary
		$("#username").addClass("object_error");
		}
	
	});
	
	});

 
function showEditBox(cid, eid){
	$("#" + cid).hide();
	$("#" + eid).show();
	
	return true;
}

 $(document).ready(function(){ 
  $('input[type=text]').focus(function(){ 
    if($(this).val() == $(this).attr('defaultValue'))
    {
      $(this).val('');
    }
  });
  
  $('input[type=text]').blur(function(){
    if($(this).val() == '')
    {
      $(this).val($(this).attr('defaultValue'));
    } 
  });
}); 
 


