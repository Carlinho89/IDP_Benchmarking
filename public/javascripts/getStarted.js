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
        $("select option:selected").each(function () {
            str = $(this).text();
        });

        query.season = parseInt(str);
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
        var hash = "#chooseMethod";

        scrollTo(hash);


    });

    //Method to input
    $("#simpleSolver").on('click', function (event) {
        event.preventDefault();
        //save the value of the selected league
        query.solver = "simple";

        updateResume(query);
        document.getElementById("chooseMethodAlert").style.visibility = "hidden";

        //Scroll to next section
        var hash = "#chooseInputs";

        scrollTo(hash);


    });

    //Method to input
    $("#complexSolver").on('click', function (event) {
        event.preventDefault();
        //save the value of the selected league
        query.solver = "complex";

        updateResume(query);
        document.getElementById("chooseMethodAlert").style.visibility = "hidden";

        //Scroll to next section
        var hash = "#chooseInputs";

        scrollTo(hash);


    });



//Events to manage the list of inputs selected  
    $("#chooseInputs input").on('click', function (event) {
        var names = [];
        var selected = [];
        $('#chooseInputs input:checked').each(function () {
            selected.push(parseInt($(this).attr('value')));
            names.push($(this).attr('name'));


        });
        query.selectedInputs = selected;
        query.selectedInputsNames = names;

        var offNames = [];
        var offSelected = [];
        $('#offensiveInput input:checked').each(function () {
            offSelected.push(parseInt($(this).attr('value')));
            offNames.push($(this).attr('name'));


        });
        query.offSelectedInputs = offSelected;
        query.offSelectedInputsNames = offNames;

        var defNames = [];
        var defSelected = [];
        $('#defensiveInput input:checked').each(function () {
            defSelected.push(parseInt($(this).attr('value')));
            defNames.push($(this).attr('name'));


        });
        query.defSelectedInputs = defSelected;
        query.defSelectedInputsNames = defNames;
        
        updateResume(query);
        document.getElementById("chooseInputsAlert").style.visibility = "hidden";
    });

//Events to manage the list of inputs selected  
    $("#chooseOutputs input").on('click', function (event) {
        var names = [];
        var selected = [];
        $('#chooseOutputs input:checked').each(function () {
            selected.push(parseInt($(this).attr('value')));
            names.push($(this).attr('name'));


        });
        query.selectedOutputs = selected;
        query.selectedOutputsNames = names;

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
        query.defSelectedOutputsNames = defnames;
        updateResume(query);
        document.getElementById("chooseOutputsAlert").style.visibility = "hidden";
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

        if (typeof query.selectedInputs === "undefined" || query.selectedInputs.length < 2) {

            hash = "#chooseInputs";
            alert = "chooseInputsAlert";
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

    var offinputs = "Offensive Inputs: ";
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
    resume += offoutputs;

    document.getElementById("selection-summary").innerHTML = resume;
}


function displayTeams(query) {
    appRoutes.controllers.Application.getLeagueTeamsBySeason(query.season, query.leagueID).ajax({
        success: function (data) {
            $("#teams").empty();
            var html='<div class="row"><div class="col-md-1"></div>';
            
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