<?php

require('helper.php');



$leagueURL= array();
$leagueURL[]="http://www.transfermarkt.it/serie-a/tabelle/wettbewerb/IT1?saison_id=";
$leagueURL[]="http://www.transfermarkt.it/premier-league/tabelle/wettbewerb/GB1/saison_id/";
$leagueURL[]="http://www.transfermarkt.it/primera-division/tabelle/wettbewerb/ES1/saison_id/";
$leagueURL[]="http://www.transfermarkt.it/1-bundesliga/tabelle/wettbewerb/L1/saison_id/";




$context = stream_context_create(array("http" => array("header" => "User-Agent: Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")));
echo file_get_contents("http://www.google.it", false, $context);

    
  // echo $html = file_get_html("http://www.google.it");







?>