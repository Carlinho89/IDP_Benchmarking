<?php
/*Parser for the stats_league.html files.
Column approach: parse the first column to get the order of teams then parse all wanted columns containing inputs for the teams
The array of DataRow objects is first filled with league_id, team_id, team_name, year by the getTeams() method and inserts the team in the db if not there
A copy of this array is passed to every call to the parseInput() method which fills in value and input_id and inserts the input in the db if not there

Parameters extracted:

shotsPerGame, possession, passSuccess, RedCards, 



//<div id="stage-team-stats" class="ws-panel stat-table">
//remember to click on difensive offensive
*/
require('helper.php');


//Select leagues to parse
$urls=array();
$urls[]="stats_serieA_"; 
$urls[]="stats_premier_"; 
$urls[]="stats_liga_"; 
$urls[]="stats_bundesliga_"; 





foreach ($urls as $url) {
  for ($year = 2010; $year<=2015; $year++ ){

    $league_id = setLeagueId($url);
    
    $html = file_get_html($url.$year.".html");
    echo "<br><b>Parsing Stats from ".$url.$year.".html </b><br>";

      //summary
      $table = $html->getElementById("statistics-team-table-summary")->childNodes(0);

        //Summary
          $dataArray = getTeams($conn, $table, $year, $league_id, "tn");
        

          parseInput($conn,"Shots Per Game", "td[class=shotsPerGame]", $table, $dataArray, "Sporty" );

          parseInput($conn,"Ball Possession", "td[class=possession]", $table, $dataArray, "Sporty" );

          parseInput($conn,"Pass Success", "td[class=passSuccess]", $table, $dataArray, "Sporty" );

          parseInput($conn,"Red Cards", "span[class=red-card-box]", $table, $dataArray, "Sporty" );

     echo "<br><b>Done Parsing Stats from ".$url.$year.".html </b> <br>";
    }
    
  }
echo "Done Parsing<br>";
echo '<a href="home.html#four" class="btn btn-info" role="button">Continue</a>'




?>