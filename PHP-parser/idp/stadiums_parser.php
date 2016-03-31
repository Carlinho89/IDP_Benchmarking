<?php
/**
Parser to join data from different leagues (also second leagues) on stadiums
**/
require('helper.php');




$leagueURL= array();
$leagueURL[]="http://www.transfermarkt.it/serie-a/stadien/wettbewerb/IT1";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/IT2";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/IT3A";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/IT3B";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/IT3C";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/IT4D";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/GB1";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/GB2";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/GB3";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/GB4";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/ES1";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/ES2";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/ES3A";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/ES3B";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/ES3C";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/ES3D";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/L1";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/L2";
$leagueURL[]="http://www.transfermarkt.it/jumplist/stadien/wettbewerb/L3";



foreach ($leagueURL as $url) {

 
    $html = file_get_html($url);


      foreach($html->find('table[class=items]') as $div){
          echo $div;
            }
            echo "<br>";
          }
        
      


  



?>