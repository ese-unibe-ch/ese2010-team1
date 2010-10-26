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
});

function loadGraph() {
	if(!$("#graph").loaded) {
		jQuery.getJSON( self.location, displayGraph);
		$("#graph").loaded = true;
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

