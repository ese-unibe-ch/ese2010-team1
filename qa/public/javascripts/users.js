$(function() {
// profile options	
	
	$('a.editProfile').livequery('click', function(event) {
		var a = $('input.editProfile');
		var b = $('span.profileEntry');
		b.removeClass('show');
		b.addClass('hidden');
		a.removeClass('hidden');
		a.addClass('show');
	});
	 
//	$('input.editProfile[type=submit]').livequery('click', function(event) {		
//		var a = $('input.editProfile[type=text]');		
//		var entrys = { "test" , "blub", "nothing" };				
//		$.post(editProfile(), { id: userid, entrys: entrys}, function(data) {		
//			alert("a post  call");
//		});
//	});
	
	// Load Default Tab
	$('li.tab a[href=#Activities]').addClass("active");
	
	$('article.profileContent').load(profileGet({id: userid, theAction: "Activities"}));


	$('li.tab a').livequery('click', function(event) {
		var a = this;
		$.get(profileGet({id: userid, theAction: this.hash.substr(1)}), function(data) {			
			$('article.profileContent').html(data);
			$('li.tab a').removeClass("active");
			$(a).addClass("active");
			
			if(a.hash.substr(1)=="Reputation") {
				loadGraph();
			}
		});
		return false;
	});
	
	



});


function loadGraph() {
	if(!$("#graph")[0].loaded) {

		jQuery.getJSON( graphData({id: userid}), displayGraph);
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
    ], {
     xaxis: {
     mode: "time"
     }
    } );
 }