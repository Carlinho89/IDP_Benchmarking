$(document).ready(function(){
  var query= {};
  // Add scrollspy to <body>
  $('body').scrollspy({target: ".navbar", offset: 50});   

  // Add smooth scrolling on all links inside the navbar
  $("#myNavbar a").on('click', function(event) {

    // Prevent default anchor click behavior
    event.preventDefault();

    // Store hash
    var hash = this.hash;
    scrollTo(hash);
  });





  $(".league").on('click', function(event){

     event.preventDefault();
    //save the value of the selected league
    //console.log($(this).attr('val'));
    query.leagueID=$(this).attr('val');


    //Scroll to next section
     var hash="#chooseSeason";
     scrollTo(hash);
  });


  $( "#seasonSelect" )
    .change(function () {
      var str = "";
      $( "select option:selected" ).each(function() {
        str = $( this ).text();
      });
      
      query.season=parseInt(str);
      
      var hash="#chooseInputs";
      scrollTo(hash);
    });
   
    
  $("#chooseInputs input").on('click', function(event){
    var selected = [];
      $('#chooseInputs input:checked').each(function() {
          selected.push(parseInt($(this).attr('value')));
          
      });
    query.selectedInputs=selected;
    
    });

 /*  $("#resume input").on('click', function(event){
    var obj= {};
    obj.name="aa";
    $.ajax({
            type:  'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(query),
            url: '/sayHello',
           // async:false,
            success: function(json) {
                console.log('/sayHello POST was successful.');
                console.log(json);
            },
            error: function(error) {
              console.log(error);
            },

        });
   }); */



    $("#resume input").on('click', function(event){
    // console.log(query);
     var hash="";

     if(typeof query.selectedInputs === "undefined"  || query.selectedInputs.length < 2){
      // alert("choose >2 Inputs");
       hash="#chooseInputs";
     }

     if(typeof query.season === "undefined"){
      // alert("choose a season");
       hash="#chooseSeason";
     }

     if(typeof query.leagueID === "undefined"){
      //alert("choose a leagueID");
       hash="#chooseLeague";
     }
     

     //if form is not compiled properly scrolls to
    if(hash != ""){
      scrollTo(hash);
    } else {

   
//one or other
  var obj= {};
    obj.name="aa";
    $.ajax({
            type:  'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(query),
            url: '/sayHello',
           // async:false,
            success: function(json) {
                //console.log('/sayHello POST was successful.');
                console.log(json);
            },
            error: function(error) {
              console.log(error);
            },

        });

    

   // post('/sayHello', {name: JSON.stringify(query)});
}


  }); 





});

function scrollTo(hash){
   $('html, body').animate({
      scrollTop: $(hash).offset().top}, 800, 
      function(){
        window.location.hash = hash;
      });

}



function post(path, params, method) {
    method = method || "post"; // Set method to post by default if not specified.

    // The rest of this code assumes you are not using a library.
    // It can be made less wordy if you use one.
    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);

    for(var key in params) {
        if(params.hasOwnProperty(key)) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
         }
    }

    document.body.appendChild(form);
    form.submit();
}