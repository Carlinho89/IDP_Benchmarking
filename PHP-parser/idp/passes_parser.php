<?php
//Parser for passes data
//Parameters extracted:
/*
CrossPG, ThroughBallsPG, LongBallsPG, ShortPassPG
*/
require('helper.php');

//<div id="stage-situation-stats" class="ws-panel stat-table">
//remember to click on passes



$urls=array();
$urls[]="passes_serieA_"; 
$urls[]="passes_premier_"; 
$urls[]="passes_liga_"; 
$urls[]="passes_bundesliga_"; 
echo '<div class="row"><div class="col-md-6">';



foreach ($urls as $url) {
  for ($year = 2010; $year<=2015; $year++ ){

    $league_id = setLeagueId($url);
  
    $html = file_get_html($url.$year.".html");
    echo "<br><b>Parsing Stats from ".$url.$year.".html </b><br>";

   
    $table = $html->getElementById("stage-passes-grid");

      $dataArray = getTeams($conn, $table, $year, $league_id, "tn");
        
       parseInput($conn,"Cross Per Game", "td[class=cr]", $table, $dataArray, "Sporty" , 0 , "number");

       parseInput($conn,"Through Balls Per Game", "td[class=tb]", $table, $dataArray, "Sporty" , 0 , "number");

       parseInput($conn,"Long Balls Per Game", "td[class=lb]", $table, $dataArray, "Sporty", 0 , "number" );

       parseInput($conn,"Short Pass Per Game", "td[class=sp]", $table, $dataArray, "Sporty" , 0, "number" );

    echo "<br><b>Done Parsing Stats from ".$url.$year.".html </b> <br>";   

  }

}


echo '</div><div class="col-md-6">';
echo "Done Parsing<br>";
echo '<a href="home.html#seven" class="btn btn-info" role="button">Continue</a>';
echo '</div></div>';

?>