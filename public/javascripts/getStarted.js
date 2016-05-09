$(document).ready(function(){
  //the query object will hold all the parameters needed for the modeller
  var query= {};
  // Add scrollspy to <body>
  $('body').scrollspy({target: ".navbar", offset: 50});   



//Navigation Bar Events
// Add smooth scrolling on all links inside the navbar
$("#myNavbar a").on('click', function(event) {

  // Prevent default anchor click behavior
  event.preventDefault();

  // Store hash
  var hash = this.hash;
  scrollTo(hash);
});



//Auto scrolling events: after each section is completed 
//screen scrolls to next section
//League to Season
$(".league").on('click', function(event){
   event.preventDefault();
  //save the value of the selected league
  query.leagueID=$(this).attr('val');
  query.leagueName=$(this).attr('alt');
   updateResume(query);

  //Scroll to next section
   var hash="#chooseSeason";
   scrollTo(hash);

  
});


//Season to Inputs
$( "#seasonSelect" )
  .change(function () {
    var str = "";
    $( "select option:selected" ).each(function() {
      str = $( this ).text();
    });

    query.season=parseInt(str);    
     updateResume(query);
    var hash="#chooseInputs";
    scrollTo(hash);
  });
   

//Events to manage the list of inputs selected  
$("#chooseInputs input").on('click', function(event){
    var names = [];
    var selected = [];
      $('#chooseInputs input:checked').each(function() {
          selected.push(parseInt($(this).attr('value')));
          names.push($(this).attr('name'));

          
      });
    query.selectedInputs=selected;
    query.selectedInputsNames=names;
    updateResume(query);
    });

//Events to manage the list of inputs selected  
$("#chooseOutputs input").on('click', function(event){
    var names = [];
    var selected = [];
      $('#chooseOutputs input:checked').each(function() {
          selected.push(parseInt($(this).attr('value')));
          names.push($(this).attr('name'));

          
      });
    query.selectedOutputs=selected;
    query.selectedOutputsNames=names;
    updateResume(query);
  });

 

//Validation for the selected parameters:
//Checks if a league, a season and at least 2 inputs are selected
//and scrolls back to the section if the info is missing
//If all parameters are selected sends an asynchronous request to server side
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

   if(typeof query.selectedOutputs === "undefined"  || query.selectedOutputs.length < 1){
    // alert("choose >2 Inputs");
     hash="#chooseOutputs";
   }
   

   //if form is not compiled properly scrolls to
  if(hash != ""){
      scrollTo(hash);
    } else {
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


//Commodity function to scroll to right section
function scrollTo(hash){
   $('html, body').animate({
      scrollTop: $(hash).offset().top}, 800, 
      function(){
        window.location.hash = hash;
      });
}


//Function to make a post and redirect to the page (not sure if using it)
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


//Function to display a resume of the selected parameters in the Resume tab
function updateResume(query){
  console.log(query);
  var resume="";

  var league="League: ";
   if(typeof query.leagueID === "undefined" ){
    league+="NOT SELECTED<br>";
      
   } else {
    league+=query.leagueName + "<br>";
    
   }
  resume+=league;

   var year="Season: ";
   if(typeof query.season === "undefined" ){
    year+="NOT SELECTED<br>";
      
   } else {
    year+=query.season + "<br>";
    
   }
  resume+=year;


  var inputs="Inputs: ";
    if(typeof query.selectedInputs === "undefined"){
    inputs+="NOT SELECTED<br>";
      
    } else {
      for (var i = query.selectedInputsNames.length - 1; i >= 0; i--) {
        inputs+= query.selectedInputsNames[i]+", "
      }
      inputs+="<br>";    
   }
  resume+=inputs;

   var outputs="Outputs: ";
    if(typeof query.selectedOutputs === "undefined"){
    outputs+="NOT SELECTED<br>";
      
    } else {
      for (var i = query.selectedOutputsNames.length - 1; i >= 0; i--) {
        outputs+= query.selectedOutputsNames[i]+", "
      }
      outputs+="<br>";    
   }
  resume+=outputs;

  document.getElementById("selection-summary").innerHTML = resume;
}