<?php
/**
Parser for transfermarkt data.
//Parameters extracted:

N.Players, AgeAVG, N.ForeignPlayers, TeamValue_MLN€, PlayerValueAVG_MLN€
 
Needs to be executed after the other scripts since it's using tm_id

//Run more than once: parse max one league per run
**/
require('helper.php');
error_reporting(0); 


$html = file_get_html("stadiums.html");
echo '<div class="row"><div class="col-md-6">';
echo "<b>Parsing transfermarkt stadiums </b><br>";
//result
$input_id = getInputId($conn, "Stadium Capacity", "Social",0, "number");

foreach($html->find('table[class=items]') as $table){

  echo "<br>New League<br>";
  foreach(array_slice($table->find('tr'),1) as $tr){

    foreach($tr->find('table') as $tab){
      $tm_id= $tab->find('a',1)->id;
      
      }

     $value = $tr->find('td[class=rechts]',0)->plaintext*1000;
     if($value>0){
      //echo "$tm_id----------$value ";
      $row = new DataRow();
          $row->getTMTeamId($conn, $tm_id);
          if($row->team_id > 0){
            $row->value = $value;
            $row->input_id= $input_id;
            echo $row->fixedInsertQuery()." ";
            if($conn->query($row->fixedInsertQuery())){
              $row->insertMessage("Stadium Capacity");
            }
          }
      echo "<br>";
     }
    
  }

}
echo '</div><div class="col-md-6">';
echo "Done Parsing<br>";

echo '<a href="home.html#thirteen" class="btn btn-info" role="button">Continue</a>';

echo '</div></div>';


?>