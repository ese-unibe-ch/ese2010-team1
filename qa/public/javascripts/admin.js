$(function() {
  
  
  $('a.activateUser').click(function(){
     $('form#deactivationComment').removeClass("hidden");
     $('section').append("<div id=\"backgroundOverlay\">&nbsp;</div>");
     var action = $(this).attr("href");
     var userid = $(this).attr("id");
     var text = $(this).find('img').attr("alt");
     $('form#deactivationComment').attr("action", action)
     if(text=="deactivate") {
       jQuery.get(getFraudPointViolations({id: userid}),function(data){
          var content = "You have violated the following rules:\n";
          content += data;
          $('textarea[name="deactivationReason"]').val(content);  
       });
     }
     
     return false;
  });
  
  $('a.cancel').click(function() {
    $('form#deactivationComment').addClass("hidden").attr("action", "");
    $('div#backgroundOverlay').detach();
    $('textarea[name="deactivationReason"]').val("");
    return false;
  });
  
   $('a.confirm').click(function(){
     return confirm("Are you sure?");      
   });
  
});
