<?php
//Parser for passes data
//Parameters extracted:
/*
CrossPG, ThroughBallsPG, LongBallsPG, ShortPassPG
*/
include('simplehtmldom_1_5/simple_html_dom.php');
//<div id="stage-situation-stats" class="ws-panel stat-table">
//remember to click on passes

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


class DataRow
{
    // property declaration
  
    public $input_id = 0;
    public $league_id = 1;
    public $team_id = 0;
    public $team_name = '';
    public $value = 0;
    public $year = 2013;
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
$urls[]="passes_serieA_"; 
$urls[]="passes_premier_"; 
$urls[]="passes_liga_"; 
$urls[]="passes_bundesliga_"; 

$league_id=1;

foreach ($urls as $url) {
  for ($year = 2010; $year<=2015; $year++ ){
    echo $url.$year.".html";
    $html = file_get_html($url.$year.".html");

    //summary
    $table = $html->getElementById("stage-passes-grid");



    //$summarydiv=$maindiv->find("div[id=stage-team-stats-summary]",0);
    //$table = $summarydiv->first_child ();


    //Summary
      $dataArray = [];
      foreach($table->find('td[class=tn]') as $element){
        $row = new DataRow();
        $row->team_name = $element->plaintext;
        $row->league_id=$league_id;
        $row->year=$year;
        array_push($dataArray, $row);

        //var_dump($row);
      }

      foreach ($dataArray as $row) {
         $row->getTeamId($conn);
        }





      $CrossPG = $dataArray;
      $name="CrossPG";
      $input_id=getInputId($conn,$name);
        $i=0; 
        echo "<br><br>".$name."<br>";
        foreach($table->find('td[class=cr]') as $element){
          
          $CrossPG[$i]->value = floatval($element->plaintext);
          $CrossPG[$i]->input_id= $input_id;
          $i++;
          //var_dump($row);
        }        
        //print_r($dataArray);
        foreach ($CrossPG as $row) {
          $conn->query($row->insertQuery());
        }




       $ThroughBallsPG = $dataArray;
      $name="ThroughBallsPG";
      $input_id=getInputId($conn,$name);
        $i=0; 
        echo "<br><br>".$name."<br>";
        foreach($table->find('td[class=tb]') as $element){
          
          $ThroughBallsPG[$i]->value = floatval($element->plaintext);
          $ThroughBallsPG[$i]->input_id= $input_id;
          $i++;
          //var_dump($row);
        }        
        //print_r($dataArray);
        foreach ($ThroughBallsPG as $row) {
          $conn->query($row->insertQuery());
        }



    $LongBallsPG = $dataArray;
      $name="LongBallsPG";
      $input_id=getInputId($conn,$name);
        $i=0; 
        echo "<br><br>".$name."<br>";
        foreach($table->find('td[class=lb]') as $element){
          
          $LongBallsPG[$i]->value = floatval($element->plaintext);
          $LongBallsPG[$i]->input_id= $input_id;
          $i++;
          //var_dump($row);
        }        
        //print_r($dataArray);
        foreach ($LongBallsPG as $row) {
          $conn->query($row->insertQuery());
        }

       $ShortPassPG = $dataArray;
      $name="ShortPassPG";
      $input_id=getInputId($conn,$name);
        $i=0; 
        echo "<br><br>".$name."<br>";
        foreach($table->find('td[class=sp]') as $element){
          
          $ShortPassPG[$i]->value = floatval($element->plaintext);
          $ShortPassPG[$i]->input_id= $input_id;
          $i++;
          //var_dump($row);
        }        
        //print_r($dataArray);
        foreach ($ShortPassPG as $row) {
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