<?php
//Parser for results data.
//Parameters extracted:
/*
gamesWon, gamesDrawn, gamesLost, goalsScored, goalsAgainst

//<div id="standings-11369" style="display: block;">
*/
require('helper.php');


$urls=array();
$urls[]="results_serieA_"; 
$urls[]="results_premier_"; 
$urls[]="results_liga_"; 
$urls[]="results_bundesliga_"; 


foreach ($urls as $url) {
  for ($year = 2010; $year<=2015; $year++ ){

    $league_id = setLeagueId($url);

    $html = file_get_html($url.$year.".html");
    echo "<br><b>Parsing Stats from ".$url.$year.".html </b><br>";


     $dataArray = getTeams($conn, $html, $year, $league_id, "team");
        

          parseInput($conn,"Games Won", "td[class=w]", $html, $dataArray, "Sporty" );

          parseInput($conn,"Games Drawn", "td[class=d]", $html, $dataArray, "Sporty" );

          parseInput($conn,"Games Lost", "td[class=l]", $html, $dataArray, "Sporty" );

          parseInput($conn,"Goals Scored", "td[class=gf]", $html, $dataArray, "Sporty" );

          parseInput($conn,"Goals Against", "td[class=ga]", $html, $dataArray, "Sporty" );

          parseInput($conn,"Rank", "td[class=o]", $html, $dataArray, "Sporty" );


  echo "<br><b>Done Parsing Stats from ".$url.$year.".html </b> <br>";
  }

}




?>