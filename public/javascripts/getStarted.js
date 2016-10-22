$(document).ready(function () {
    //the query object will hold all the parameters needed for the modeller
    var query = {};

    // Add scrollspy to <body>
    $('body').scrollspy({target: ".navbar", offset: 50});


//Navigation Bar Events
// Add smooth scrolling on all links inside the navbar
    $("#myNavbar a").on('click', function (event) {

        // Prevent default anchor click behavior
        event.preventDefault();

        // Store hash
        var hash = this.hash;
        scrollTo(hash);
    });


//Auto scrolling events: after each section is completed
//screen scrolls to next section

//Method to league
    $("#simpleSolver").on('click', function (event) {
        event.preventDefault();
        //save the value of the selected league
        query.solver = "simple";
        document.getElementById("chooseOrder").setAttribute("style","display: none;");
        updateResume(query);
        document.getElementById("chooseMethodAlert").style.visibility = "hidden";

        //Scroll to next section
        var hash = "#chooseLeague";

        scrollTo(hash);


    });

//Method to league
    $("#complexSolver").on('click', function (event) {
        event.preventDefault();
        //save the value of the selected league
        query.solver = "complex";
        query.selectedMethod = "BCC";
        document.getElementById("chooseOrder").setAttribute("style","display: block;");
        updateResume(query);
        document.getElementById("chooseMethodAlert").style.visibility = "hidden";

        //Scroll to next section
        var hash = "#chooseLeague";

        scrollTo(hash);


    });


//League to Season
    $(".league").on('click', function (event) {
        event.preventDefault();
        //save the value of the selected league
        query.leagueID = $(this).attr('val');
        query.leagueName = $(this).attr('alt');
        updateResume(query);
        document.getElementById("chooseLeagueAlert").style.visibility = "hidden";
        if (query.season > 0) {
            displayTeams(query);
        }
        //Scroll to next section
        var hash = "#chooseSeason";

        scrollTo(hash);


    });


//Season to Team
    $("#seasonSelect").change(function () {
        var str = "";

        $("#seasonSelect option:selected").each(function () {
            str = $(this).text();
        });

        query.season = parseInt(str);
        console.log(query.season);
        var remainingSeasons=2015-query.season;
        if(remainingSeasons>0){
            console.log("remaining season"+remainingSeasons);
            $( "#numberOfSeasonsSelect" ).empty();
            for(var i=0;i<=remainingSeasons;i++){
                $( "#numberOfSeasonsSelect" ).append( " <option>"+(i+1)+"</option>" );
            }
        }
        updateResume(query);
        document.getElementById("chooseSeasonAlert").style.visibility = "hidden";
        displayTeams(query);
        var hash = "#chooseTeam";

        scrollTo(hash);
    });


//Team to method
    $("#teams").on('click', '.favorite_team', function (event) {
        event.preventDefault();
        //save the value of the selected league
        query.teamID = $(this).attr('val');
        query.teamName = $(this).attr('title');
        updateResume(query);
        document.getElementById("chooseTeamAlert").style.visibility = "hidden";

        //Scroll to next section
        var hash = "#chooseInputs";

        scrollTo(hash);


    });


//Event to manage selection of super-efficiency

    $("#chooseMethod input").on('click', function (event) {
        var superEff = false;
        if ($('#efficiency').is(':checked')){
            superEff=true;
        }




        query.superEff = superEff;



        updateResume(query);
        //document.getElementById("chooseMethodsAlert").style.visibility = "hidden";
    });


    //Season to Team
    $("#numberOfSeasonsSelect").change(function () {
        var str = "";

        $("#numberOfSeasonsSelect option:selected").each(function () {
            str = $(this).text();
        });

        query.numberOfSeasons = parseInt(str);

        updateResume(query);

    });

//Events to manage the list of inputs selected
    $("#chooseInputs input").on('click', function (event) {
        var names = [];
        var selected = [];
        var alreadySelected = "";
        var value=-1;
        $('#chooseInputs input:checked').each(function () {
            value=parseInt($(this).attr('value'));
            console.log(value);

            if (query.selectedOutputs && query.selectedOutputs.indexOf(value)>=0  ){
                alreadySelected=$(this).attr('name');
                $(this).prop( "checked", false );

            } else {
                selected.push(parseInt($(this).attr('value')));
                names.push($(this).attr('name'));
            }



        });
        if(alreadySelected!=""){
            alert("The "+alreadySelected+" parameter is already selected as output");
        }
        query.selectedInputs = selected;
        query.selectedInputsNames = names;

     /*   var offNames = [];
        var offSelected = [];
        $('#offensiveInput input:checked').each(function () {
            offSelected.push(parseInt($(this).attr('value')));
            offNames.push($(this).attr('name'));


        });
        query.offSelectedInputs = offSelected;
        query.offSelectedInputsNames = offNames;

        var socNames = [];
        var socSelected = [];
        $('#socialInput input:checked').each(function () {
            socSelected.push(parseInt($(this).attr('value')));
            socNames.push($(this).attr('name'));


        });
        query.socSelectedInputs = socSelected;
        query.socSelectedInputsNames = socNames;

        var defNames = [];
        var defSelected = [];
        $('#defensiveInput input:checked').each(function () {
            defSelected.push(parseInt($(this).attr('value')));
            defNames.push($(this).attr('name'));


        });
        query.defSelectedInputs = defSelected;
        query.defSelectedInputsNames = defNames;*/

        updateResume(query);
        document.getElementById("chooseInputsAlert").style.visibility = "hidden";
    });

//Events to manage the list of outputs selected
    $("#chooseOutputs input").on('click', function (event) {
        var names = [];
        var selected = [];
        var alreadySelected = "";
        var value=-1;
        $('#chooseOutputs input:checked').each(function () {


            value=parseInt($(this).attr('value'));
            console.log(value);

            if (query.selectedInputs && query.selectedInputs.indexOf(value)>=0  ){
                alreadySelected=$(this).attr('name');
                $(this).prop( "checked", false );

            } else {
                selected.push(parseInt($(this).attr('value')));
                names.push($(this).attr('name'));
            }


        });
        if(alreadySelected!=""){
            alert("The "+alreadySelected+" parameter is already selected as input");
        }
        query.selectedOutputs = selected;
        query.selectedOutputsNames = names;

       /* var spnames = [];
        var spselected = [];
        $('#sportiveOutput input:checked').each(function () {
            spselected.push(parseInt($(this).attr('value')));
            spnames.push($(this).attr('name'));


        });
        query.spSelectedOutputs = spselected;
        query.spSelectedOutputsNames = spnames;


        var offnames = [];
        var offselected = [];
        $('#offensiveOutput input:checked').each(function () {
            offselected.push(parseInt($(this).attr('value')));
            offnames.push($(this).attr('name'));


        });
        query.offSelectedOutputs = offselected;
        query.offSelectedOutputsNames = offnames;

        var defnames = [];
        var defselected = [];
        $('#defensiveOutput input:checked').each(function () {
            defselected.push(parseInt($(this).attr('value')));
            defnames.push($(this).attr('name'));


        });
        query.defSelectedOutputs = defselected;
        query.defSelectedOutputsNames = defnames;*/
        updateResume(query);
        document.getElementById("chooseOutputsAlert").style.visibility = "hidden";
    });


//Events to manage multi stage dea solving

//CCR-BCC selection
    $("#methodSelect").change(function () {
        var str = "";

        $("#methodSelect option:selected").each(function () {
            str = $(this).text();
        });

        query.selectedMethod = str;
        console.log(query.selectedMethod);

        updateResume(query);

        var hash = "#setStages";

        scrollTo(hash);
    });
//Stage 1 manager
    $("#stage1").on('click', function (event) {
        if(  query.selectedInputs.length>0 && query.selectedOutputs.length>0){
            if(typeof query.stage1DEA === "undefined")
            document.getElementById("stage1-rem").setAttribute("style","display: block;");
            var index = 0;
            var dea= {};
            dea.selectedInputs=[];
            dea.selectedOutputs=[];
            dea.previousResults=[];
            dea.stage=1;
            dea.inputOriented=true;
            dea.deaID=0;

            if(typeof query.stage1DEA === "undefined"){
                query.stage1DEA= [];
                query.stage1DEA.push(dea);

            } else {
                console.log(query.stage1DEA.length);
                index= query.stage1DEA.length;
                dea.deaID=index;
                query.stage1DEA.push(dea);

            }




            var node = document.getElementById("dea-templates").cloneNode(true);
            var newID= "dea"+(index);
            console.log(newID);
            node.setAttribute("id",newID);
            //  var h4 = document.createElement("h4").setAttribute("class","text-center ");
            // h4.innerHTML="AAA";

            node.insertAdjacentHTML("afterbegin", "<h4 class='text-center'>DEA"+(index+1)+"</h4>");
            //$( "#"+newID+".dea-title" ).replaceWith( '<h4>AAAAAA</h4>' );
            //console.log(document.getElementById(newID).innerHTML);
            //console.log("aaaaa" + JSON.stringify($('#'+newID+' .dea-title')));


            var container = document.getElementById('stage1-dea');
            console.log(JSON.stringify(query.stage1DEA));

            var deaInputs= node.getElementsByClassName("dea-inputs-container")[0];
            //deaInputs.innerHTML="AA";
            var inputlist="";
            for(var i=0; i< query.selectedInputs.length;i++){
                inputlist+=' <div class="col-md-4 ">';
                inputlist+=' <div class="dea-element">';
                inputlist+='<input style=" margin-left:5%" type="checkbox" class="dea-input" stage="1" value='+query.selectedInputs[i]+' dea="'+index+'" parameter-type="input" >';
                inputlist+='<label>'+query.selectedInputsNames[i]+'</label>';
                inputlist+='</div></div>';
            }
            deaInputs.innerHTML=inputlist;

            var deaOutputs= node.getElementsByClassName("dea-outputs-container")[0];
            //deaInputs.innerHTML="AA";
            var outputlist="";
            for(var i=0; i< query.selectedOutputs.length;i++){
                outputlist+=' <div class="col-md-4 ">';
                outputlist+=' <div class="dea-element">';
                outputlist+='<input style=" margin-left:5%" type="checkbox" class="dea-input" stage="1" value='+query.selectedOutputs[i]+' dea="'+index+'" parameter-type="output" >';
                outputlist+='<label>'+query.selectedOutputsNames[i]+'</label>';
                outputlist+='</div></div>';
            }
            deaOutputs.innerHTML=outputlist;

            var deaOrientation= node.getElementsByClassName("dea-orientation-container")[0];
            var orientationDiv="";
            orientationDiv+='<div class="col-md-4">Orienation:</div>';
            orientationDiv+='<div class="col-md-4"><input class="col-md-4" type="radio" stage="1" name="orientation" value="Input" checked dea="'+index+'" parameter-type="orientation"> Input</div>';
            orientationDiv+='<div class="col-md-4"><input class="col-md-4" type="radio" stage="1" name="orientation" value="Output" dea="'+index+'" parameter-type="orientation"> Output</div>';
            deaOrientation.innerHTML=orientationDiv;


            container.appendChild(node);
        }
        else {
            alert("Choose inputs and outputs first!");
        }

    });

    $("#stage1-rem").on('click', function (event) {


        if(typeof query.stage1DEA === "undefined"){


        } else {
            var last= query.stage1DEA.length;
            if(last == 1){
                document.getElementById("stage1-rem").setAttribute("style","display: none;")
            }
            var parent = document.getElementById("stage1-dea");
            var child = parent.lastElementChild;
            parent.removeChild(child);
            query.stage1DEA.pop();

            console.log(JSON.stringify(query.stage1DEA));




        }



    });

//Stage 2 manager
    $("#stage2").on('click', function (event) {
        if(query.stage1DEA.length>0) {
            document.getElementById("stage2-rem").setAttribute("style", "display: block;");
            var index = 0;
            var dea = {};
            dea.selectedInputs = [];
            dea.selectedOutputs = [];
            dea.previousResults = [];
            dea.stage = 2;
            dea.inputOriented = true;
            dea.deaID = 0;

            if (typeof query.stage2DEA === "undefined") {
                query.stage2DEA = [];
                query.stage2DEA.push(dea);

            } else {
                console.log(query.stage2DEA.length);
                index = query.stage2DEA.length;
                dea.deaID = index;
                query.stage2DEA.push(dea);

            }


            var node = document.getElementById("dea-templates").cloneNode(true);
            var newID = "dea" + (index);
            console.log(newID);
            node.setAttribute("id", newID);
            //  var h4 = document.createElement("h4").setAttribute("class","text-center ");
            // h4.innerHTML="AAA";

            node.insertAdjacentHTML("afterbegin", "<h4 class='text-center'>DEA" + (index + 1) + "</h4>");
            //$( "#"+newID+".dea-title" ).replaceWith( '<h4>AAAAAA</h4>' );
            //console.log(document.getElementById(newID).innerHTML);
            //console.log("aaaaa" + JSON.stringify($('#'+newID+' .dea-title')));


            var container = document.getElementById('stage2-dea');
            console.log(JSON.stringify(query.stage1DEA));

            var deaInputs = node.getElementsByClassName("dea-inputs-container")[0];
            //deaInputs.innerHTML="AA";
            var inputlist = "";
            for (var i = 0; i < query.selectedInputs.length; i++) {
                inputlist += ' <div class="col-md-4 ">';
                inputlist += ' <div class="dea-element">';
                inputlist += '<input style=" margin-left:5%" type="checkbox" class="dea-input" stage="2" value=' + query.selectedInputs[i] + ' dea="' + index + '"  parameter-type="input">';
                inputlist += '<label>' + query.selectedInputsNames[i] + '</label>';
                inputlist += '</div></div>';
            }
            for (var i = 0; i < query.stage1DEA.length; i++) {
                inputlist += ' <div class="col-md-4 ">';
                inputlist += ' <div class="dea-element">';
                inputlist += '<input style=" margin-left:5%" type="checkbox" class="dea-input" stage="2" value="' + (1 * i) + '" dea="' + index + '"  parameter-type="result">';
                inputlist += '<label>Stage1 DEA' + (i + 1) + ' result</label>';
                inputlist += '</div></div>';
            }
            deaInputs.innerHTML = inputlist;

            var deaOutputs = node.getElementsByClassName("dea-outputs-container")[0];
            //deaInputs.innerHTML="AA";
            var outputlist = "";
            for (var i = 0; i < query.selectedOutputs.length; i++) {
                outputlist += ' <div class="col-md-4 ">';
                outputlist += ' <div class="dea-element">';
                outputlist += '<input style=" margin-left:5%" type="checkbox" class="dea-input" stage="2" value=' + query.selectedOutputs[i] + ' dea="' + index + '" parameter-type="output" >';
                outputlist += '<label>' + query.selectedOutputsNames[i] + '</label>';
                outputlist += '</div></div>';
            }
            deaOutputs.innerHTML = outputlist;


            var deaOrientation = node.getElementsByClassName("dea-orientation-container")[0];
            var orientationDiv = "";
            orientationDiv += '<div class="col-md-4">Orienation:</div>';
            orientationDiv += '<div class="col-md-4"><input class="col-md-4" type="radio" stage="2" name="orientation" value="Input" checked dea="' + index + '" parameter-type="orientation"> Input</div>';
            orientationDiv += '<div class="col-md-4"><input class="col-md-4" type="radio" stage="2" name="orientation" value="Output" dea="' + index + '" parameter-type="orientation"> Output</div>';
            deaOrientation.innerHTML = orientationDiv;

            container.appendChild(node);
        }
        else {
            alert("There is no DEA set in the first stage ");
        }
    });

    $("#stage2-rem").on('click', function (event) {


        if(typeof query.stage2DEA === "undefined"){


        } else {
            var last= query.stage2DEA.length;
            if(last == 1){
                document.getElementById("stage2-rem").setAttribute("style","display: none;")
            }
            var parent = document.getElementById("stage2-dea");
            var child = parent.lastElementChild;
            parent.removeChild(child);
            query.stage2DEA.pop();

            console.log(JSON.stringify(query.stage2DEA));




        }



    });

//Stage 3 manager
    $("#stage3").on('click', function (event) {
        if(query.stage2DEA.length>0) {
            document.getElementById("stage3-rem").setAttribute("style","display: block;");
            var index = 0;
            var dea= {};
            dea.selectedInputs=[];
            dea.selectedOutputs=[];
            dea.previousResults=[];
            dea.stage=3;
            dea.inputOriented=true;
            dea.deaID=0;

            if(typeof query.stage3DEA === "undefined"){
                query.stage3DEA= [];
                query.stage3DEA.push(dea);

            } else {
                console.log(query.stage3DEA.length);
                index= query.stage3DEA.length;
                dea.deaID=index;
                query.stage3DEA.push(dea);

            }




            var node = document.getElementById("dea-templates").cloneNode(true);
            var newID= "dea"+(index);
            console.log(newID);
            node.setAttribute("id",newID);
            //  var h4 = document.createElement("h4").setAttribute("class","text-center ");
            // h4.innerHTML="AAA";

            node.insertAdjacentHTML("afterbegin", "<h4 class='text-center'>DEA"+(index+1)+"</h4>");
            //$( "#"+newID+".dea-title" ).replaceWith( '<h4>AAAAAA</h4>' );
            //console.log(document.getElementById(newID).innerHTML);
            //console.log("aaaaa" + JSON.stringify($('#'+newID+' .dea-title')));


            var container = document.getElementById('stage3-dea');
            console.log(JSON.stringify(query.stage3DEA));

            var deaInputs= node.getElementsByClassName("dea-inputs-container")[0];
            //deaInputs.innerHTML="AA";
            var inputlist="";
            for(var i=0; i< query.selectedInputs.length;i++){
                inputlist+=' <div class="col-md-4 ">';
                inputlist+=' <div class="dea-element">';
                inputlist+='<input style=" margin-left:5%" type="checkbox" class="dea-input" stage="3" value='+query.selectedInputs[i]+' dea="'+index+'" parameter-type="input">';
                inputlist+='<label>'+query.selectedInputsNames[i]+'</label>';
                inputlist+='</div></div>';
            }
            for(var i=0; i< query.stage2DEA.length;i++){
                inputlist+=' <div class="col-md-4 ">';
                inputlist+=' <div class="dea-element">';
                inputlist+='<input style=" margin-left:5%" type="checkbox" class="dea-input" stage="3" value="'+(1*i)+'" dea="'+index+'" parameter-type="result">';
                inputlist+='<label>Stage2 DEA'+(i+1)+' result</label>';
                inputlist+='</div></div>';
            }
            deaInputs.innerHTML=inputlist;

            var deaOutputs= node.getElementsByClassName("dea-outputs-container")[0];
            //deaInputs.innerHTML="AA";
            var outputlist="";
            for(var i=0; i< query.selectedOutputs.length;i++){
                outputlist+=' <div class="col-md-4 ">';
                outputlist+=' <div class="dea-element">';
                outputlist+='<input style=" margin-left:5%" type="checkbox" class="dea-input" stage="3" value='+query.selectedOutputs[i]+' dea="'+index+'" parameter-type="output">';
                outputlist+='<label>'+query.selectedOutputsNames[i]+'</label>';
                outputlist+='</div></div>';
            }
            deaOutputs.innerHTML=outputlist;

            var deaOrientation= node.getElementsByClassName("dea-orientation-container")[0];
            var orientationDiv="";
            orientationDiv+='<div class="col-md-4">Orienation:</div>';
            orientationDiv+='<div class="col-md-4"><input class="col-md-4" type="radio" stage="3" name="orientation" value="Input" checked dea="'+index+'" parameter-type="orientation"> Input</div>';
            orientationDiv+='<div class="col-md-4"><input class="col-md-4" type="radio" stage="3" name="orientation" value="Output" dea="'+index+'" parameter-type="orientation"> Output</div>';
            deaOrientation.innerHTML=orientationDiv;


            container.appendChild(node);
        }
        else {
            alert("There is no DEA set in the second stage ");

        }
    });

    $("#stage3-rem").on('click', function (event) {


        if(typeof query.stage3DEA === "undefined"){


        } else {
            var last= query.stage3DEA.length;
            if(last == 1){
                document.getElementById("stage3-rem").setAttribute("style","display: none;")
            }
            var parent = document.getElementById("stage3-dea");
            var child = parent.lastElementChild;
            parent.removeChild(child);
            query.stage3DEA.pop();

            console.log(JSON.stringify(query.stage3DEA));




        }



    });


//Events to control selected values in each dea
    $(".stage").on('click', function (event) {

        if(event.target.tagName.localeCompare("INPUT")==0){
            var inputObject={};
            inputObject.stage= event.target.getAttribute('stage');
            inputObject.inputType=event.target.getAttribute('parameter-type');
            inputObject.deaIndex = parseInt(event.target.getAttribute('dea'));
            inputObject.value = event.target.getAttribute('value');
            console.log(JSON.stringify(inputObject));


            if(inputObject.stage.localeCompare("1")==0){
                setDEAParameters(query.stage1DEA,inputObject);

                console.log(JSON.stringify(query.stage1DEA));

            }
            else if(inputObject.stage.localeCompare("2")==0){
                setDEAParameters(query.stage2DEA,inputObject);
                console.log(JSON.stringify(query.stage2DEA));

            }
            else if (inputObject.stage.localeCompare("3")==0){
                setDEAParameters(query.stage3DEA,inputObject);
                console.log(JSON.stringify(query.stage3DEA));

            }



        }
        else console.log("Not an input");



       // updateResume(query);
    });


//Validation for the selected parameters:
//Checks if a league, a season and at least 2 inputs are selected
//and scrolls back to the section if the info is missing
//If all parameters are selected sends an asynchronous request to server side
    $("#resume input").on('click', function (event) {
        // console.log(query);
        var hash = "";
        var alert = "";

        if (typeof query.selectedOutputs === "undefined" || query.selectedOutputs.length < 1) {
            alert = "chooseOutputsAlert";
            hash = "#chooseOutputs";
        }

        if (typeof query.selectedInputs === "undefined" || query.selectedInputs.length < 1) {

            hash = "#chooseInputs";
            alert = "chooseInputsAlert";
        }

        if (query.selectedInputs &&  query.selectedOutputs &&  query.numberOfTeams ) {
          if (query.numberOfTeams < 3*(query.selectedInputs.length + query.selectedOutputs.length)) {
            hash = "#chooseInputs";
            alert = "cooperAlert";
          }

        }


        if (typeof query.teamID === "undefined") {

            hash = "#chooseTeam";
            alert = "chooseTeamAlert";
        }

        if (typeof query.season === "undefined") {

            hash = "#chooseSeason";
            alert = "chooseSeasonAlert";
        }

        if (typeof query.leagueID === "undefined") {
            alert = "chooseLeagueAlert";
            hash = "#chooseLeague";
        }

        if (typeof query.superEff === "undefined") {
            query.superEff=false;

        }

        if (typeof query.numberOfSeasons === "undefined") {
            query.numberOfSeasons=1;

        }


        //if form is not compiled properly scrolls to
        if (hash != "") {
            scrollTo(hash);
            document.getElementById(alert).style.visibility = "visible";

            // document.getElementById(hash+"Alert").innerHTML = error;
            //alert(error);
        } else {
            var url="";
            if(query.solver.localeCompare("simple")==0){ url = './simple-solver';}

            else
            {url = './complex-solver';}
            console.log(url);

           /* var form = $('<form action="' + url + '" method="post">' +
                '<input type="text" name="query" value="' + JSON.stringify(query) + '" />' +
                '</form>');
            $('body').append(form);
            form.submit();*/

            var newForm = $('<form>', {
                'method':'post',
                'action': url,
                'target': '_top'
            }).append($('<input>', {
                'name': 'query',
                'value': JSON.stringify(query),
                'type': 'hidden'
            }));

            $('body').append(newForm);
            newForm.submit();
        }
    });
});


//Commodity function to scroll to right section
function scrollTo(hash) {
    $('html, body').animate({
            scrollTop: $(hash).offset().top
        }, 800,
        function () {
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

    for (var key in params) {
        if (params.hasOwnProperty(key)) {
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
function updateResume(query) {
    console.log(JSON.stringify(query));
    var resume = "";

    var league = "League: ";
    if (typeof query.leagueID === "undefined") {
        league += "NOT SELECTED<br>";

    } else {
        league += query.leagueName + "<br>";

    }
    resume += league;

    var year = "Season: ";
    if (typeof query.season === "undefined") {
        year += "NOT SELECTED<br>";

    } else {
        year += query.season + "<br>";

    }
    resume += year;

    var favoriteTeam = "Selected Team: ";
    if (typeof query.teamID === "undefined") {
        favoriteTeam += "NOT SELECTED<br>";

    } else {
        favoriteTeam += query.teamName + "<br>";

    }
    resume += favoriteTeam;

    var solver= "Solver Method: ";
    if(typeof query.solver === "undefined")
        solver+= "NOT SELECTED<br>";
    else solver+= query.solver + "<br>";
    resume += solver;

    var efficiency= "Super Efficiency: ";
    if(typeof query.superEff === "undefined")
        efficiency+= "false <br>";
    else efficiency+= query.superEff + "<br>";
    resume += efficiency;

    var numOfSeasons= "Number Of Seasons: ";
    if(typeof query.numberOfSeasons === "undefined")
        numOfSeasons+= "1 <br>";
    else numOfSeasons+= query.numberOfSeasons + "<br>";
    resume += numOfSeasons;

    var inputs = "Inputs: ";
    if (typeof query.selectedInputs === "undefined") {
        inputs += "NOT SELECTED<br>";

    } else {
        for (var i = query.selectedInputsNames.length - 1; i >= 0; i--) {
            inputs += query.selectedInputsNames[i] + ", "
        }
        inputs += "<br>";
    }
    resume += inputs;

    /*var offinputs = "Offensive Inputs: ";
    if (typeof query.offSelectedInputs === "undefined") {
        offinputs += "NOT SELECTED<br>";

    } else {
        for (var i = query.offSelectedInputsNames.length - 1; i >= 0; i--) {
            offinputs += query.offSelectedInputsNames[i] + ", "
        }
        offinputs += "<br>";
    }
    resume += offinputs;

    var definputs = "Defensive Inputs: ";
    if (typeof query.defSelectedInputsNames === "undefined") {
        definputs += "NOT SELECTED<br>";

    } else {
        for (var i = query.defSelectedInputsNames.length - 1; i >= 0; i--) {
            definputs += query.defSelectedInputsNames[i] + ", "
        }
        definputs += "<br>";
    }
    resume += definputs;

    var socinputs = "Social Inputs: ";
    if (typeof query.socSelectedInputsNames === "undefined") {
        socinputs += "NOT SELECTED<br>";

    } else {
        for (var i = query.socSelectedInputsNames.length - 1; i >= 0; i--) {
            socinputs += query.socSelectedInputsNames[i] + ", "
        }
        socinputs += "<br>";
    }
    resume += socinputs; */

    var outputs = "Outputs: ";
    if (typeof query.selectedOutputs === "undefined") {
        outputs += "NOT SELECTED<br>";

    } else {
        for (var i = query.selectedOutputsNames.length - 1; i >= 0; i--) {
            outputs += query.selectedOutputsNames[i] + ", "
        }
        outputs += "<br>";
    }
    resume += outputs;

    /*var spoutputs = "Sportive Outputs: ";
    if (typeof query.spSelectedOutputsNames === "undefined") {
        spoutputs += "NOT SELECTED<br>";

    } else {
        for (var i = query.spSelectedOutputsNames.length - 1; i >= 0; i--) {
            spoutputs += query.spSelectedOutputsNames[i] + ", "
        }
        spoutputs += "<br>";
    }
    resume += spoutputs;

    var defoutputs = "Defensive Outputs: ";
    if (typeof query.defSelectedOutputsNames === "undefined") {
        defoutputs += "NOT SELECTED<br>";

    } else {
        for (var i = query.defSelectedOutputsNames.length - 1; i >= 0; i--) {
            defoutputs += query.defSelectedOutputsNames[i] + ", "
        }
        defoutputs += "<br>";
    }
    resume += defoutputs;

    var offoutputs = "Offensive Outputs: ";
    if (typeof query.offSelectedOutputsNames === "undefined") {
        offoutputs += "NOT SELECTED<br>";

    } else {
        for (var i = query.offSelectedOutputsNames.length - 1; i >= 0; i--) {
            offoutputs += query.offSelectedOutputsNames[i] + ", "
        }
        offoutputs += "<br>";
    }
    resume += offoutputs;*/

    document.getElementById("selection-summary").innerHTML = resume;
}


function displayTeams(query) {
    appRoutes.controllers.Application.getLeagueTeamsBySeason(query.season, query.leagueID).ajax({
        success: function (data) {
            $("#teams").empty();
            var html='<div class="row"><div class="col-md-1"></div>';
            query.numberOfTeams=data.length;
// <a href=""><img class="  img-thumbnail img-circle img-responsive favorite_team" src="/assets/images/team_logo/' + data[i].logo + '"  val="' + data[i].tm_id + '" title="' + data[i].name + '" ></a>
            for (var i = 0; i < data.length; i++) {
                html+='<div class="col-md-1 "><div class="team-box" ><img class="team-logo img-thumbnail img-responsive favorite_team" src="/assets/images/team_logo/' + data[i].logo + '"  val="' + data[i].tm_id + '" title="' + data[i].name + '" ></div></div>';
                if(i==9){
               html+='<div class="col-md-1"></div></div><div class="row"><div class="col-md-1"></div>';

                }

            }
            html+='<div class="col-md-1"></div></div>';
            console.log(html);
            $("#teams").append(html+"<br><br>");
        }
    });
}

//Function to set the selected parameters of each dea in multistage solving
function setDEAParameters(stageArray,inputObject){
    if(inputObject.inputType.localeCompare("input")==0) {
        var found= stageArray[inputObject.deaIndex].selectedInputs.indexOf(parseInt(inputObject.value));
        if(found >= 0)stageArray[inputObject.deaIndex].selectedInputs.splice(found,1);
        else  stageArray[inputObject.deaIndex].selectedInputs.push(parseInt(inputObject.value));
    }
    else if (inputObject.inputType.localeCompare("output")==0) {
        var found= stageArray[inputObject.deaIndex].selectedOutputs.indexOf(parseInt(inputObject.value));
        if(found >= 0)stageArray[inputObject.deaIndex].selectedOutputs.splice(found,1);
        else  stageArray[inputObject.deaIndex].selectedOutputs.push(parseInt(inputObject.value));

    }
    else if (inputObject.inputType.localeCompare("result")==0) {
        var found= stageArray[inputObject.deaIndex].previousResults.indexOf(parseInt(inputObject.value));
        if(found >= 0)stageArray[inputObject.deaIndex].previousResults.splice(found,1);
        else  stageArray[inputObject.deaIndex].previousResults.push(parseInt(inputObject.value));

    }
    else if (inputObject.inputType.localeCompare("orientation")==0 && inputObject.value.localeCompare("Output")) {
      stageArray[inputObject.deaIndex].inputOriented=true;
      console.log("true");
    }
    else if (inputObject.inputType.localeCompare("orientation")==0 && inputObject.value.localeCompare("Input")) {stageArray[inputObject.deaIndex].inputOriented=false;}

}
