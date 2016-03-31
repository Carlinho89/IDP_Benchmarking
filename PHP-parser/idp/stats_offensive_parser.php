<?php
/*Parser for the stats_league.html files.
Parameters extracted:

ShotsOnTargetPG, dribbleWonPerGame
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



      //offensive
      $table = $html->getElementById("statistics-team-table-offensive")->childNodes(0);

        $dataArray = getTeams($conn, $table, $year, $league_id, "tn");
        

          parseInput($conn,"Shots On Target Per Game", "td[class=shotOnTargetPerGame]", $table, $dataArray, "Sporty" );

          parseInput($conn,"Dribbles Won Per Game", "td[class=dribbleWonPerGame]", $table, $dataArray, "Sporty" );


        
       
 
     echo "<br><b>Done Parsing Stats from ".$url.$year.".html </b> <br>";
    }
    
  }


echo "Done Parsing<br>";
echo '<a href="home.html#six" class="btn btn-info" role="button">Continue</a>'



?>