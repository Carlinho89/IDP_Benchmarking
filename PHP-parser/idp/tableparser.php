<?php
//Parser for sportive data
//Parameters extracted:
/*
shotsPerGame, possession, passSuccess, RedCards, ShotsOnTargetPG, 
dribbleWonPerGame, shotsConcededPerGame, tacklePerGame, interceptionPerGame 
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
        $sql2="INSERT INTO `idp`.`teams`  VALUES (NULL, '".$this->team_name."','', '')";
        $conn->query($sql2);
        $this->team_id = $conn->insert_id;
        

      }

    }
}

$urls=array();
//$urls[]="stats_serieA_"; 
//$urls[]="stats_premier_"; 
//$urls[]="stats_liga_"; 
$urls[]="stats_bundesliga_"; 

$league_id=4;

foreach ($urls as $url) {
  for ($year = 2010; $year<=2015; $year++ ){
    echo $url.$year.".html";
    $html = file_get_html($url.$year.".html");


      //summary
      $table = $html->getElementById("statistics-team-table-summary")->childNodes(0);

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





          $shotsPerGame = $dataArray;
          $name="ShotsPG";
          $input_id=getInputId($conn,$name);
            $i=0; 
            echo "<br><br>shots per game<br>";
            foreach($table->find('td[class=shotsPerGame]') as $element){
              
              $shotsPerGame[$i]->value = floatval($element->plaintext);
              $shotsPerGame[$i]->input_id= $input_id;
              $i++;
              //var_dump($row);
            }        
            //print_r($dataArray);
            foreach ($shotsPerGame as $row) {
              $conn->query($row->insertQuery());
            }

          $possession = $dataArray;
          $name="Possession";
          $input_id=getInputId($conn,$name);
            $i=0; 
            echo "<br><br>possession<br>";
            foreach($table->find('td[class=possession]') as $element){
              
              $possession[$i]->value = floatval($element->plaintext);
              $possession[$i]->input_id= $input_id;
              $i++;
              //var_dump($row);
            }        
            //print_r($dataArray);
            foreach ($possession as $row) {
                $conn->query($row->insertQuery());
            }

          $passSuccess = $dataArray;
          $name="PassSuccess";
          $input_id=getInputId($conn,$name);
            $i=0; 
            echo "<br><br>passSuccess<br>";
            foreach($table->find('td[class=passSuccess]') as $element){
              
              $passSuccess[$i]->value = floatval($element->plaintext);
              $passSuccess[$i]->input_id= $input_id;
              $i++;
              //var_dump($row);
            }        
            //print_r($dataArray);
            foreach ($passSuccess as $row) {
                $conn->query($row->insertQuery());
            }

          $redcards = $dataArray;
          $name="N.RedCards";
          $input_id=getInputId($conn,$name);
            $i=0; 
            echo "<br><br>redcards<br>";
            foreach($table->find('span[class=red-card-box]') as $element){
              
              $redcards[$i]->value = floatval($element->plaintext);
              $redcards[$i]->input_id= $input_id;
              $i++;
              //var_dump($row);
            }        
            //print_r($dataArray);
            foreach ($redcards as $row) {

              $conn->query($row->insertQuery());
                
            }


      //offensive
      $table = $html->getElementById("statistics-team-table-offensive")->childNodes(0);

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





        $ShotsOnTargetPG = $dataArray;
        $name="ShotsOnTargetPG";
        $input_id=getInputId($conn,$name);
          $i=0; 
          echo "<br><br>ShotsOnTargetPG<br>";
          foreach($table->find('td[class=shotOnTargetPerGame]') as $element){
            
            $ShotsOnTargetPG[$i]->value = floatval($element->plaintext);
            $ShotsOnTargetPG[$i]->input_id= $input_id;
            $i++;
            //var_dump($row);
          }        
          //print_r($dataArray);
          foreach ($ShotsOnTargetPG as $row) {
            $conn->query($row->insertQuery());
          }

        $dribbleWonPerGame = $dataArray;
        $name="dribbleWonPerGame";
        $input_id=getInputId($conn,$name);
          $i=0; 
          echo "<br><br>dribbleWonPerGame<br>";
          foreach($table->find('td[class=dribbleWonPerGame]') as $element){
            
            $dribbleWonPerGame[$i]->value = floatval($element->plaintext);
            $dribbleWonPerGame[$i]->input_id= $input_id;
            $i++;
            //var_dump($row);
          }        
          //print_r($dataArray);
          foreach ($dribbleWonPerGame as $row) {
              $conn->query($row->insertQuery());
          }

       

      //defensive
      $table = $html->getElementById("statistics-team-table-defensive")->childNodes(0);

        $dataArray = [];
        foreach($table->find('td[class=tn]') as $element){
          $row = new DataRow();
          $row->team_name = $element->plaintext;
          $row->league_id=$league_id;
          $row->year=$year;
          array_push($dataArray, $row);
        }

        foreach ($dataArray as $row) {
           $row->getTeamId($conn);
          }





        $shotsConcededPerGame = $dataArray;
        $name="shotsConcededPerGame";
        $input_id=getInputId($conn,$name);
          $i=0; 
          echo "<br><br>shotsConcededPerGame<br>";
          foreach($table->find('td[class=shotsConcededPerGame]') as $element){
            
            $shotsConcededPerGame[$i]->value = floatval($element->plaintext);
            $shotsConcededPerGame[$i]->input_id= $input_id;
            $i++;
            //var_dump($row);
          }        
          //print_r($dataArray);
          foreach ($shotsConcededPerGame as $row) {
            $conn->query($row->insertQuery());
          }

        $tacklePerGame = $dataArray;
        $name="tacklePerGame";
        $input_id=getInputId($conn,$name);
          $i=0; 
          echo "<br><br>tacklePerGame<br>";
          foreach($table->find('td[class=tacklePerGame]') as $element){
            
            $tacklePerGame[$i]->value = floatval($element->plaintext);
            $tacklePerGame[$i]->input_id= $input_id;
            $i++;
            //var_dump($row);
          }        
          //print_r($dataArray);
          foreach ($tacklePerGame as $row) {
              $conn->query($row->insertQuery());
          }

        $interceptionPerGame = $dataArray;
        $name="interceptionPerGame";
        $input_id=getInputId($conn,$name);
          $i=0; 
          echo "<br><br>interceptionPerGame<br>";
          foreach($table->find('td[class=interceptionPerGame]') as $element){
            
            $interceptionPerGame[$i]->value = floatval($element->plaintext);
            $interceptionPerGame[$i]->input_id= $input_id;
            $i++;
            //var_dump($row);
          }        
          //print_r($dataArray);
          foreach ($interceptionPerGame as $row) {
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