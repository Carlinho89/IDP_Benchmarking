<?php

require('helper.php');



$leagueURL= array();
$leagueURL[]="http://www.transfermarkt.it/serie-a/tabelle/wettbewerb/IT1?saison_id=";
$leagueURL[]="http://www.transfermarkt.it/premier-league/tabelle/wettbewerb/GB1/saison_id/";
$leagueURL[]="http://www.transfermarkt.it/primera-division/tabelle/wettbewerb/ES1/saison_id/";
$leagueURL[]="http://www.transfermarkt.it/1-bundesliga/tabelle/wettbewerb/L1/saison_id/";




$rank_id=findRankID($conn);
if($rank_id>0){

foreach ($leagueURL as $url) {
  $league_id = setTMLeagueId($url);
  for ($i = 2010; $i<=2015; $i++ ){

    $html = file_get_html($url.$i);
    echo "<br><h1>$league_id----$i</h1></br>";

    //result
      
      foreach($html->find('div[class=responsive-table]') as $div){
        foreach($div->find('table') as $table){
          //echo $table;
          foreach($table->find('tr') as $tr){
             $position= $tr->find('td',0)->plaintext * 1;
           
              $tm_id=$tr->find('a[class=vereinprofil_tooltip]',0)->id;

            if($position>0){
          
              echo "$position---$tm_id<img src='img/$tm_id.png'  height='25' width='25'><br>";
               echo $sql="UPDATE `team` SET `tm_id`=$tm_id, logo='".$tm_id.".png' WHERE id in (SELECT `team_id` FROM `seasonal_data` WHERE `input_id` = $rank_id and `year` = $i and value= $position and `league_id` = $league_id)";
              //echo "<br>";
             $conn->query($sql);
            }

             
            }
            
            
          }
        }
        
      }
     

    


  }

  echo "<h1>Manual update of spanish teams..</h1>";
  $sql = "UPDATE `team` SET `tm_id`=1049, logo='1049.png' WHERE `name` LIKE 'Valencia'";
  $conn->query($sql);

  $sql = "UPDATE `team` SET `tm_id`=368, logo='368.png' WHERE `name` LIKE 'Sevilla'";
  $conn->query($sql);

  $sql = "UPDATE `team` SET `tm_id`=142, logo='142.png' WHERE `name` LIKE 'Zaragoza'";
  $conn->query($sql);

  $sql = "UPDATE `team` SET `tm_id`=150, logo='150.png' WHERE `name` LIKE 'Real Betis'";
  $conn->query($sql);

  $sql = "UPDATE `team` SET `tm_id`=367, logo='367.png' WHERE `name` LIKE 'Rayo Vallecano'";
  $conn->query($sql);

  $sql = "UPDATE `team` SET `tm_id`=16795, logo='16795.png' WHERE `name` LIKE 'Granada'";
  $conn->query($sql);

  $sql = "UPDATE `team` SET `tm_id`=940, logo='940.png' WHERE `name` LIKE 'Celta Vigo'";
  $conn->query($sql);
  echo "Done Parsing<br>";
echo '<a href="home.html#eight" class="btn btn-info" role="button">Continue</a>';

  
} else{
  echo "Impossible to run the script. You must run result_parser.php first. If you did, check if the input Rank has been parsed. If it has been saved with a different name modify findRankID() in this script.";
  

}



/**
function to find the id for the input Rank. 
If you change the name of the input change it in the query below also
**/
function findRankID($conn){
$sql = "SELECT id FROM `input` WHERE `name` LIKE 'Rank'";
      $rank_id=-1;
      $result = $conn->query($sql);
      if ($result->num_rows > 0){
        $row = $result->fetch_assoc();
          $rank_id = $row['id'];
      }

return $rank_id;

}




?>