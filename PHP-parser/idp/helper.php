

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <title>Benchmarking DB Doc</title>

    <!-- Bootstrap -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="css/blog.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>



<?php

include('simplehtmldom_1_5/simple_html_dom.php');


/**
Setting connection to db. remember to change dbname
**/
  $servername = "localhost";
  $username = "root";
  $password = "toxik89";
  $dbname = "idpdoc";

  $conn = new mysqli($servername, $username, $password, $dbname);
  // Check connection
  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }  


/**
The class is used for data collection. Every row that will be added to the input table will be saved as an instance of this class first. As explained in the comments on the next functions these objects are first partially populated (when the column holding the team names on the web page is parsed). Then a copy of these objects is made for every value being parsed from the webpage.
Parameters: same as input table
Methods: 
	-toString():helper method to print the values in a formatted way
	-insertMessage(): prints to video the string informing the user that the instance has 	  been saved to db
	-insertQuery(): outputs the query to insert the object in the db
	-getTeamId(): checks if the teams exists and retrieves the id. If the team does not exist a new row is added to the teams table and the id is returned.
	-getTMTeamId(): retrieves team id for transfermarkt parser
**/
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
      echo "Insert $input for team $this->team_name year $this->year<br>";
    }

    public function insertQuery(){
      $sql = "INSERT INTO `seasonal_data` VALUES (null,".$this->team_id.",'".$this->team_name."',".$this->year.",".$this->league_id.",".$this->input_id.",".$this->value.")";
      return $sql;
    }

    public function fixedInsertQuery(){
      $sql = "INSERT INTO `fixed_data`(`id`, `team_id`, `team_name`, `league_id`, `input_id`, `value`) VALUES (null, $this->team_id,'$this->team_name', $this->league_id, $this->input_id, $this->value)";
      return $sql;
    }

    public function getTeamId($conn,$league_id){
      
      $sql = "SELECT id FROM `team` WHERE `name` LIKE '".$this->team_name."' ";
      $result = $conn->query($sql);
      if ($result->num_rows > 0) {
        
          $row = $result->fetch_assoc();
          $this->team_id = $row['id'];
      } else {
        $sql2="INSERT INTO `team`  VALUES (NULL, '".$this->team_name."','', '',$league_id)";
        
        $conn->query($sql2);
        $this->team_id = $conn->insert_id;
        

      }

    }

     public function getTMTeamId($conn, $tm_id){
      $sql = "SELECT * FROM `team` WHERE `tm_id` = $tm_id";
      $result = $conn->query($sql);
      if ($result->num_rows > 0) {
          $row = $result->fetch_assoc();
          $this->team_id = $row['id'];
          $this->team_name = $row['name'];
          $this->league_id=$row['league_id'];
      } else {
      	 $this->team_id= -1;
        echo "<br>No team id matching for ".$tm_id." <br>";
        

      }

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



//Function to set the league id by reading the url of the page parsed in transfermarkt.
function setTMLeagueId($url){
  $id=0;
  if (strpos($url, 'serie') !== false){$id=1;}
  elseif (strpos($url, 'premier') !== false){$id=2;}
  elseif (strpos($url, 'primera') !== false){$id=3;}
  elseif (strpos($url, 'bundesliga') !== false){$id=4;}
  if(! $id > 0 ){
    echo "Error: couldn't recognize league_id from url $url ";
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

//Creates dataArray which is used as basis for every input insert. This array holds 
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
    $row->getTeamId($conn,$league_id);
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

 <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
 <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
 <!-- Include all compiled plugins (below), or include individual files as needed -->
 <script src="js/bootstrap.min.js"></script>
   </body>
</html>