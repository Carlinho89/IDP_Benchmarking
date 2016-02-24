<?php
//Parser for results data.
//Parameters extracted:
/*
gamesWon, gamesDrawn, gamesLost, goalsScored, goalsAgainst
*/
include('simplehtmldom_1_5/simple_html_dom.php');



  $servername = "localhost";
  $username = "root";
  $password = "toxik89";
  $dbname = "idp";



  $conn = new mysqli($servername, $username, $password, $dbname);
  // Check connection
  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  } 


class DataRow
{
    // property declaration
  
    public $input_id = 100000;
    public $league_id = 100000;
    public $team_id = 100000;
    public $team_name = '100000';
    public $value = 100000;
    public $year = 100000;
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
        $sql2="INSERT INTO `idp`.`teams` (`id`, `name`, `logo`) VALUES (NULL, '".$this->team_name."', '')";
        $conn->query($sql2);
        $this->team_id = $conn->insert_id;
        

      }

    }
}

$urls=array();
$urls[]="results_serieA_"; 
$urls[]="results_premier_"; 
$urls[]="results_liga_"; 
$urls[]="results_bundesliga_"; 

$league_id=1;
foreach ($urls as $url) {
  for ($year = 2010; $year<=2015; $year++ ){

    $html = file_get_html($url.$year.".html");

    //result
      $dataArray = [];
      foreach($html->find('td[class=team]') as $element){
        $row = new DataRow();
        $row->team_name = $element->plaintext;
        $row->league_id=$league_id;
        $row->year=$year;
        array_push($dataArray, $row);

        
      }

      foreach ($dataArray as $row) {
         $row->getTeamId($conn);
         
        }





      $win = $dataArray;
      $name="gamesWon";
      $input_id=getInputId($conn,$name);
        $i=0; 
        echo "<br><br>".$name."<br>";
        foreach($html->find('td[class=w]') as $element){
          
          $win[$i]->value = floatval($element->plaintext);
          $win[$i]->input_id= $input_id;
          $i++;
          //var_dump($row);
        }        
        //print_r($dataArray);
        foreach ($win as $row) {
          $conn->query($row->insertQuery());
        }

      $draw = $dataArray;
      $name="gamesDrawn";
      $input_id=getInputId($conn,$name);
        $i=0; 
        echo "<br><br>".$name."<br>";
        foreach($html->find('td[class=d]') as $element){
          
          $draw[$i]->value = floatval($element->plaintext);
          $draw[$i]->input_id= $input_id;
          $i++;
          //var_dump($row);
        }        
        //print_r($dataArray);
        foreach ($draw as $row) {
            $conn->query($row->insertQuery());
        }

      $lost = $dataArray;
      $name="gamesLost";
      $input_id=getInputId($conn,$name);
        $i=0; 
        echo "<br><br>".$name."<br>";
        foreach($html->find('td[class=l]') as $element){
          
          $lost[$i]->value = floatval($element->plaintext);
          $lost[$i]->input_id= $input_id;
          $i++;
          //var_dump($row);
        }        
        //print_r($dataArray);
        foreach ($lost as $row) {
            $conn->query($row->insertQuery());
        }


      $goalsScored = $dataArray;
      $name="goalsScored";
      $input_id=getInputId($conn,$name);
        $i=0; 
        echo "<br><br>".$name."<br>";
        foreach($html->find('td[class=gf]') as $element){
          
          $goalsScored[$i]->value = floatval($element->plaintext);
          $goalsScored[$i]->input_id= $input_id;
          $i++;
          //var_dump($row);
        }        
        //print_r($dataArray);
        foreach ($goalsScored as $row) {
            $conn->query($row->insertQuery());
        }

      $goalsAgainst = $dataArray;
      $name="goalsAgainst";
      $input_id=getInputId($conn,$name);
        $i=0; 
        echo "<br><br>".$name."<br>";
        foreach($html->find('td[class=ga]') as $element){
          
          $goalsAgainst[$i]->value = floatval($element->plaintext);
          $goalsAgainst[$i]->input_id= $input_id;
          $i++;
          //var_dump($row);
        }        
        //print_r($dataArray);
        foreach ($goalsAgainst as $row) {
            $conn->query($row->insertQuery());
        }


      $rank = $dataArray;
      $name="Rank";
      $input_id=getInputId($conn,$name);
        $i=0; 
        echo "<br><br>".$name."<br>";
        foreach($html->find('td[class=o]') as $element){
          
          $rank[$i]->value = floatval($element->plaintext);
          $rank[$i]->input_id= $input_id;
          $i++;
          //var_dump($row);
        }        
        //print_r($dataArray);
        foreach ($rank as $row) {
            $conn->query($row->insertQuery());
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