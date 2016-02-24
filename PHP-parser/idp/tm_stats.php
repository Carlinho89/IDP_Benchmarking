<?php
//Parser for transfermarkt data.
//Parameters extracted:
/*
N.Players, AgeAVG, N.ForeignPlayers, TeamValue_MLN€, PlayerValueAVG_MLN€
*/
include('simplehtmldom_1_5/simple_html_dom.php');
//parsing stats from transfermarkt. Needs to be executed after the other scripts
//since it's using tranfermarkt_names
//Run in 4 times changing $league_id



//<div id="standings-11369" style="display: block;">
//DB
  $servername = "localhost";
  $username = "root";
  $password = "toxik89";
  $dbname = "idp";



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

    public function getTeamId($conn, $team_name){
      $sql = 'SELECT id, name FROM `teams` WHERE `transfermarkt_name` LIKE "'.$team_name.'" ';
      $result = $conn->query($sql);
      if ($result->num_rows > 0) {
          $row = $result->fetch_assoc();
          $this->team_id = $row['id'];
          $this->team_name = $row['name'];
      } else {
        echo "<br>No team name matching for ".$team_name." <br>";
        

      }

    }
}

//run this for all leagues, remember to change league id
$leagueURL= array();
//$leagueURL[]="http://www.transfermarkt.it/serie-a/startseite/wettbewerb/IT1/plus/?saison_id=";
//$leagueURL[]="http://www.transfermarkt.it/premier-league/startseite/wettbewerb/GB1/plus/?saison_id=";
//$leagueURL[]="http://www.transfermarkt.it/primera-division/startseite/wettbewerb/ES1/plus/?saison_id=";
$leagueURL[]="http://www.transfermarkt.it/1-bundesliga/startseite/wettbewerb/L1/plus/?saison_id=";
//serieA: 
//premier: 
//liga: 
//bundes: 
$league_id=4;
foreach ($leagueURL as $url) {
  for ($year = 2010; $year<=2015; $year++ ){
    $html = file_get_html($url.$year);


    //result
      $dataArray = [];
      foreach($html->find('div[id=yw1]') as $div){
        foreach($div->find('table[class=items]') as $table){
          //echo $table;
          foreach(array_slice($table->find('tr'),2) as $tr){
            //echo $tr."<br>";
           $name= $tr->find('td',2)->plaintext;
           $name =  preg_replace('/\s+/', '', $name);
           $name=  trim(preg_replace('/\t+/', '', $name));
           $name= str_replace("'",".",$name);
            //initialize
            $row = new DataRow();
            $row->getTeamId($conn, $name);
            $row->league_id=$league_id;
            $row->year=$year;


            //one for each input
            $Players_Num= $tr->find('td',3)->plaintext;
            $input_name=  "N.Players";
            $row->input_id=getInputId($conn,$input_name);
            $row->value=$Players_Num;
            $conn->query($row->insertQuery());



            $AgeAVG= floatval(str_replace(",",".",$tr->find('td',4)->plaintext));
            $input_name=  "AgeAVG";
            $row->input_id=getInputId($conn,$input_name);
            $row->value=$AgeAVG;
            $conn->query($row->insertQuery());
            
            $Foreign= $tr->find('td',5)->plaintext;
            $input_name=  "N.ForeignPlayers";
            $row->input_id=getInputId($conn,$input_name);
            $row->value=$Foreign;
            $conn->query($row->insertQuery());
            
            $TeamValue_MLN€= floatval(str_replace(",",".",str_replace(" mln €","",$tr->find('td',6)->plaintext)));
            $input_name=  "TeamValue_MLN€";
            $row->input_id=getInputId($conn,$input_name);
            $row->value=$TeamValue_MLN€;
            $conn->query($row->insertQuery());


            $AVGPlayerValue=floatval(str_replace(",",".",str_replace(" mln €","",$tr->find('td',7)->plaintext)));
            $input_name=  "PlayerValueAVG_MLN€";
            $row->input_id=getInputId($conn,$input_name);
            $row->value=$AVGPlayerValue;
            $conn->query($row->insertQuery());

            

          }
        }
      }



  }

  $league_id++;
}


function getInputId($conn,$name){

      $sql = "SELECT id FROM `inputs` WHERE `name` LIKE '".$name."' ";
      $result = $conn->query($sql);
      if ($result->num_rows > 0) {
          $row = $result->fetch_assoc();
          return $row['id'];
      } else {
        $sql2="INSERT INTO `idp`.`inputs` (`id`, `name`, `type`) VALUES (NULL, '".$name."', 'Sporty');";
        $conn->query($sql2);
        return $conn->insert_id;
        

      }

    }


?>