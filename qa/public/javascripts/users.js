$(function() {
// profile options
	
	$('a.tab[href=#Activities]').addClass("active");


	$('a.tab').livequery('click', function(event) {
		var a = this;
		$.get(profileGet({id: userid, theAction: this.hash.substr(1)}), function(data) {			
			$('article.profileContent').html(data);
			$('a.tab').removeClass("active");
			$(a).addClass("active");
			
			if(a.hash.substr(1)=="graph") {
				loadGraph();
			}
		});
		return false;
	});
	

	
	$('article.profileContent').load(profileGet({id: userid, theAction: "Activities"}));



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