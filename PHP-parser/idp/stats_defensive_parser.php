<?php
/*Parser for the stats_league.html files.

Parameters extracted:

shotsConcededPerGame, tacklePerGame, interceptionPerGame 
*/
require('helper.php');


//Select leagues to parse
$urls=array();
$urls[]="stats_serieA_"; 
$urls[]="stats_premier_"; 
$urls[]="stats_liga_"; 
$urls[]="stats_bundesliga_"; 





foreach ($urls as $url) {

  $league_id = setLeagueId($url);
  for ($year = 2010; $year<=2015; $year++ ){

    
    
    $html = file_get_html($url.$year.".html");
    echo "<br><b>Parsing Stats from ".$url.$year.".html </b><br>";

   //defensive
      $table = $html->getElementById("statistics-team-table-defensive")->childNodes(0);

        $dataArray = getTeams($conn, $table, $year, $league_id, "tn");
        
          parseInput($conn,"Shots Conceded Per Game", "td[class=shotsConcededPerGame]", $table, $dataArray, "Sporty" );

          parseInput($conn,"Tackles Per Game", "td[class=tacklePerGame]", $table, $dataArray, "Sporty" );

          parseInput($conn,"Interceptions Per Game", "td[class=interceptionPerGame]", $table, $dataArray, "Sporty" );

     echo "<br><b>Done Parsing Stats from ".$url.$year.".html </b> <br>";
    }
    
  }


echo "Done Parsing<br>";
echo '<a href="home.html#five" class="btn btn-info" role="button">Continue</a>'


?>