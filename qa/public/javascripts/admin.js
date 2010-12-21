$(function() {
  
  
  $('a.activateUser').click(function(){
     $('form#deactivationComment').removeClass("hidden");
     $('section').append("<div id=\"backgroundOverlay\">&nbsp;</div>");
     var action = $(this).attr("href");
     $('form#deactivationComment').attr("action", action);
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
