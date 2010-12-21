$(function() {
  
  
  $('a.activateUser').click(function(){
     $('form#deactivationComment').removeClass("hidden");
     $("section").append("<div id=\"backgroundOverlay\">&nbsp;</div>");
     return false;
  });
  
  $('a.cancel').click(function() {
    $('form#deactivationComment').addClass("hidden"); 
    $("#backgroundOverlay").detach();
  });
  
   $('a.confirm').click(function(){
     confirm("Are you sure?");      
   });
  
});
