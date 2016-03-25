<?php

include('simplehtmldom_1_5/simple_html_dom.php');
//this file is getting the names of the teams by rank 
//in a season in order to add the column 
//transfermarkt_name in the team table
//It's needed to create consistency on team names in the two sites


//<div id="standings-11369" style="display: block;">
//DB
  $servername = "localhost";
  $username = "root";
  $password = "toxik89";
  $dbname = "idpdoc";



  $conn = new mysqli($servername, $username, $password, $dbname);
  // Check connection
  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  } 

class tm_data
{
    public $id = 0;
   
    public $position = 0;
    public $name = '';
    public $league_id = 4;
    public $year = 2014;

    public function insertQuery(){
      $sql = "INSERT INTO transfermarkt_teams VALUES (NULL,'".$this->name."', ".$this->position.", ".$this->year.", ".$this->league_id.")";
      return $sql;
    }

    public function setTeamId($conn){
      $sql = "SELECT team_id FROM `seasonal_data` WHERE year = ".$this->year." AND league_id = ".$this->league_id." AND input_id = 19 AND value = ".$this->position;
      $result = $conn->query($sql);
      if ($result->num_rows > 0) {
          $row = $result->fetch_assoc();
          echo $team_id = $row['team_id'];
          $update= "UPDATE `teams` SET transfermarkt_name= '".$this->name."' WHERE id = " .   $team_id;
          $conn->query($update);
        }
    }

}
class DataRow
{
    // property declaration
  
    public $input_id = 0;
    public $league_id = 1;
    public $team_id = 0;
    public $team_name = '';
    public $value = 0;
    public $year = 2019;
    // method declaration
    public function displayVal() {
        echo $this->value;
    }

    public function insertQuery(){
      $sql = "INSERT INTO `seasonal_data` VALUES (null,".$this->team_id.",'".$this->team_name."',".$this->year.",".$this->league_id.",".$this->input_id.",".$this->value.")";
      return $sql;
    }

    public function getTeamId($conn){
      $sql = "SELECT id FROM `teams` WHERE `name` LIKE '".$this->team_name."' ";
      $result = $conn->query($sql);
      if ($result->num_rows > 0) {
          $row = $result->fetch_assoc();
          $this->team_id = $row['id'];
      } else {
        $sql2="INSERT INTO `teams` (`id`, `name`, `logo`) VALUES (NULL, '".$this->team_name."', '')";
        $conn->query($sql2);
        $this->team_id = $conn->insert_id;
        

      }

    }
}

//run this for all leagues, remember to change league id
$leagueURL= array();
$leagueURL[]="http://www.transfermarkt.it/serie-a/tabelle/wettbewerb/IT1?saison_id=";
$leagueURL[]="http://www.transfermarkt.it/premier-league/tabelle/wettbewerb/GB1/saison_id/";
$leagueURL[]="http://www.transfermarkt.it/primera-division/tabelle/wettbewerb/ES1/saison_id/";
$leagueURL[]="http://www.transfermarkt.it/1-bundesliga/tabelle/wettbewerb/L1/saison_id/";
//serieA: 
//premier: 
//liga: 
//bundes: 
$league_id=1;
foreach ($leagueURL as $url) {
  for ($i = 2010; $i<=2015; $i++ ){
    $html = file_get_html($url.$i);


    //result
      $dataArray = [];
      foreach($html->find('div[class=responsive-table]') as $div){
        foreach($div->find('table') as $table){
          //echo $table;
          foreach($table->find('tr') as $tr){
            echo $position= $tr->find('td',0)->plaintext * 1;

            $tm_name = trim (html_entity_decode ($tr->find('td',2)->plaintext));
            $tm_name =  preg_replace('/\*/', '', $tm_name);
            $tm_name =  preg_replace('/\s+/', '', $tm_name);
            echo $tm_name=  trim(preg_replace('/\t+/', '', $tm_name));
            echo "<br>";
            if($tm_name == "Bor. M'gladbach" ){ $tm_name="Borussia Mönchengladbach";}
             if($tm_name == "1.FC K'lautern" ){ $tm_name="1.FC Kaiserslautern";}
            if($position>0){
              $row = new tm_data();
              $row->name = $tm_name;
              $row->position = $position;
              $row->year=$i;
              $row->league_id=$league_id;
              $dataArray[]= $row;
            }
            

          }
        }
        
       // echo $table->find('tbody',0)->plaintext;
      }

      foreach ($dataArray as $row) {
        
        $conn->query($row->insertQuery());
        $row->setTeamId($conn);
        
        }


  }

  $league_id++;
}





?>