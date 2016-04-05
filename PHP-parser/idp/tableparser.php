<?php
/*Parser for the stats_league.html files.
Column approach: parse the first column to get the order of teams then parse all wanted columns containing inputs for the teams
The array of DataRow objects is first filled with league_id, team_id, team_name, year by the getTeams() method and inserts the team in the db if not there
A copy of this array is passed to every call to the parseInput() method which fills in value and input_id and inserts the input in the db if not there

Parameters extracted:

shotsPerGame, possession, passSuccess, RedCards, ShotsOnTargetPG, 
dribbleWonPerGame, shotsConcededPerGame, tacklePerGame, interceptionPerGame 

Notice: the limit of execution time for php doesn't give a chance of running the file only once. 
My suggestion  is to parse 1 or 2 leagues at a time. This can be done be commenting/uncommenting the strings set in the $urls array
ex: This will parse serieA and premier. 
$urls=array();
$urls[]="stats_serieA_"; 
$urls[]="stats_premier_"; 
//$urls[]="stats_liga_"; 
//$urls[]="stats_bundesliga_";
Re run the script with the first two commented and the last two uncommented to add liga and bundes
*/
include('simplehtmldom_1_5/simple_html_dom.php');

  $servername = "localhost";
  $username = "root";
  $password = "toxik89";
  $dbname = "idpdoc";



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
    public $year = 0;
    // method declaration
    public function toString() {
        
        echo "team_id: $this->team_id <br>";
        echo "team_name: $this->team_name <br>";
        echo "league_id: $this->league_id <br>";    
        echo "year: $this->year <br>";
        echo "input_id: $this->input_id <br>";
        echo "value: $this->value <br>";
      
        echo "<br>";
    }

    public function insertMessage($input){
      echo "<br> Insert $input for team $this->team_name year $this->year";
    }

    public function insertQuery(){
      $sql = "INSERT INTO `seasonal_data` VALUES (null,".$this->team_id.",'".$this->team_name."',".$this->year.",".$this->league_id.",".$this->input_id.",".$this->value.")";
      return $sql;
    }

    public function getTeamId($conn){
      
      $sql = "SELECT id FROM `team` WHERE `name` LIKE '".$this->team_name."' ";
      $result = $conn->query($sql);
      if ($result->num_rows > 0) {
        
          $row = $result->fetch_assoc();
          $this->team_id = $row['id'];
      } else {
        $sql2="INSERT INTO `team`  VALUES (NULL, '".$this->team_name."','', '')";
        
        $conn->query($sql2);
        $this->team_id = $conn->insert_id;
        

      }

    }
}


//Select leagues to parse
$urls=array();
$urls[]="stats_serieA_"; 
//$urls[]="stats_premier_"; 
//$urls[]="stats_liga_"; 
//$urls[]="stats_bundesliga_"; 





foreach ($urls as $url) {
  for ($year = 2010; $year<=2015; $year++ ){

    $league_id = setLeagueId($url);
    
    $html = file_get_html($url.$year.".html");
    echo "<br><b>Parsing Stats from ".$url.$year.".html </b><br>";

      //summary
      $table = $html->getElementById("statistics-team-table-summary")->childNodes(0);

        //Summary
          $dataArray = getTeams($conn, $table, $year, $league_id, "tn");
        

          parseInput($conn,"Shots Per Game", "td[class=shotsPerGame]", $table, $dataArray, "Sporty" );

          parseInput($conn,"Ball Possession", "td[class=possession]", $table, $dataArray, "Sporty" );

          parseInput($conn,"Pass Success", "td[class=passSuccess]", $table, $dataArray, "Sporty" );

          parseInput($conn,"Red Cards", "span[class=red-card-box]", $table, $dataArray, "Sporty" );


      //offensive
      $table = $html->getElementById("statistics-team-table-offensive")->childNodes(0);

        $dataArray = getTeams($conn, $table, $year, $league_id, "tn");
        

          parseInput($conn,"Shots On Target Per Game", "td[class=shotOnTargetPerGame]", $table, $dataArray, "Sporty" );

          parseInput($conn,"Dribbles Won Per Game", "td[class=dribbleWonPerGame]", $table, $dataArray, "Sporty" );


        
       
      //defensive
      $table = $html->getElementById("statistics-team-table-defensive")->childNodes(0);

        $dataArray = getTeams($conn, $table, $year, $league_id, "tn");
        
          parseInput($conn,"Shots Conceded Per Game", "td[class=shotsConcededPerGame]", $table, $dataArray, "Sporty" );

          parseInput($conn,"Tackles Per Game", "td[class=tacklePerGame]", $table, $dataArray, "Sporty" );

          parseInput($conn,"Interceptions Per Game", "td[class=interceptionPerGame]", $table, $dataArray, "Sporty" );

     echo "<br><b>Done Parsing Stats from ".$url.$year.".html </b> <br>";
    }
    
  }






 //Function to set the league id by reading the url of the file parsed.
 //Notice this means that if the files are not named properly the leagueId will be wrong     
function setLeagueId($url){
  $id=0;
  if (strpos($url, 'serieA') !== false){$id=1;}
  elseif (strpos($url, 'premier') !== false){$id=2;}
  elseif (strpos($url, 'liga') !== false && strpos($url, 'bundesliga') == false){$id=3;}
  elseif (strpos($url, 'bundesliga') !== false){$id=4;}
  if(! $id > 0 ){
    echo "the file is not named correctly. Couldn't find league name";
  }

  return $id;

}





//Retrieves the input id if exists, creates a new one of the defined type otherwise
function getInputId($conn, $name, $type){

  $sql = "SELECT id FROM `input` WHERE `name` LIKE '".$name."' ";
  $result = $conn->query($sql);
  if ($result->num_rows > 0) {
      $row = $result->fetch_assoc();
      return $row['id'];
  } else {
    $sql2="INSERT INTO `input` (`id`, `name`, `type`) VALUES (NULL, '".$name."', '".$type."');";
    $conn->query($sql2);
    return $conn->insert_id;
    

  }

}

//Creates dataArray which is used as basis for every insert. This array holds 
//DataRow objects which only have name year and league set.
//Notice it retreaves only team name (tn) from the html
function getTeams($conn, $table, $year, $league_id, $selector){
  $dataArray = [];
  foreach($table->find('td[class='.$selector.']') as $element){
    $row = new DataRow();
    $row->team_name = $element->plaintext;
    $row->league_id=$league_id;
    $row->year=$year;
    array_push($dataArray, $row);
  }

  foreach ($dataArray as $row) {
    $row->getTeamId($conn);
  }  

  return $dataArray;
}




/*
Function to parse an input column. 
Parameters:
$conn- connection object to make sql calls
$name- name to give to the input in the db
$selector- selector string (css style) to find the element in the html to parse
$table- node of the table holding the data
$dataArray- basis array which holds team name, id and year. It is set with the getTeams(..) method.
$type- type of the input being parsed. Might be Sporty or Social or Monetary

*/
function parseInput($conn, $name, $selector, $table, $dataArray, $type){
  $inputArray = $dataArray;
  $input_id=getInputId($conn,$name, $type);
    $i=0; 
    foreach($table->find("$selector") as $element){
      
      $inputArray[$i]->value = floatval($element->plaintext);
      $inputArray[$i]->input_id= $input_id;
      $i++;
      
    }        
    
    foreach ($inputArray as $row) {
      
      if($conn->query($row->insertQuery())){
        $row->insertMessage($name);
      }
      
    }
  
}


?>