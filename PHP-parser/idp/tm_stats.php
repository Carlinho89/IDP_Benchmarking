<?php
/**
Parser for transfermarkt data.
//Parameters extracted:

N.Players, AgeAVG, N.ForeignPlayers, TeamValue_MLN€, PlayerValueAVG_MLN€
 
Needs to be executed after the other scripts since it's using tm_id

//Run more than once: parse max one league per run
**/
require('helper.php');




$leagueURL= array();
//$leagueURL[]="http://www.transfermarkt.it/serie-a/startseite/wettbewerb/IT1/plus/?saison_id=";
//$leagueURL[]="http://www.transfermarkt.it/premier-league/startseite/wettbewerb/GB1/plus/?saison_id=";
//$leagueURL[]="http://www.transfermarkt.it/primera-division/startseite/wettbewerb/ES1/plus/?saison_id=";
$leagueURL[]="http://www.transfermarkt.it/1-bundesliga/startseite/wettbewerb/L1/plus/?saison_id=";


foreach ($leagueURL as $url) {

  $league_id = setTMLeagueId($url);
  for ($year = 2010; $year<=2015; $year++ ){
    $html = file_get_html($url.$year);

    echo "<b>Parsing transfermarkt stats for league=$league_id and year=$year</b><br>";
    //result
      $dataArray = [];
      foreach($html->find('div[id=yw1]') as $div){
        foreach($div->find('table[class=items]') as $table){
          
          foreach(array_slice($table->find('tr'),2) as $tr){
            //echo $tr."<br>";

            $tm_id=$tr->find('a[class=vereinprofil_tooltip]',1)->id;

            $row = new DataRow();
            $row->getTMTeamId($conn, $tm_id);
            $row->league_id=$league_id;
            $row->year=$year;
            
            

           



            //one for each input
            $Players_Num= $tr->find('td',3)->plaintext;
            $input_name=  "Number Of Players";
            $row->input_id=getInputId($conn,$input_name,"Social");
            $row->value=$Players_Num;
            if($conn->query($row->insertQuery())){
              $row->insertMessage($input_name);
            }



            $AgeAVG= floatval(str_replace(",",".",$tr->find('td',4)->plaintext));
            $input_name=  "Average Age";
            $row->input_id=getInputId($conn,$input_name,"Social");
            $row->value=$AgeAVG;
            if($conn->query($row->insertQuery())){
              $row->insertMessage($input_name);
            }
            
            $Foreign= $tr->find('td',5)->plaintext;
            $input_name=  "Number Of Foreign Players";
            $row->input_id=getInputId($conn,$input_name,"Social");
            $row->value=$Foreign;
            if($conn->query($row->insertQuery())){
              $row->insertMessage($input_name);
            }
            
            $TeamValue_MLN€= floatval(str_replace(",",".",str_replace(" mln €","",$tr->find('td',6)->plaintext)));
            $input_name=  "Team Value (MLN €)";
            $row->input_id=getInputId($conn,$input_name,"Monetary");
            $row->value=$TeamValue_MLN€;
            if($conn->query($row->insertQuery())){
              $row->insertMessage($input_name);
            }


            $AVGPlayerValue=floatval(str_replace(",",".",str_replace(" mln €","",$tr->find('td',7)->plaintext)));
            $input_name=  "Average Player Value (MLN €)";
            $row->input_id=getInputId($conn,$input_name,"Monetary");
            if($conn->query($row->insertQuery())){
              $row->insertMessage($input_name);
            }
            echo "<br>";
          }
        }
      }


    echo "<b>Done parsing transfermarkt stats for league=$league_id and year=$year</b><br>";
    
  }


}
echo "<h1>Done Parsing TM_STATS</h1>"


?>