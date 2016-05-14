<?php
/**
66 Valencia 1049
68 Siviglia 368
76 - Name: Zaragoza 142
 84 - Name: Real Betis 150
 85 - Name: Rayo Vallecano 367
 86 - Name: Granada 16795
  88 - Name: Celta Vigo 940


**/
require('helper.php');


$sql = "SELECT * FROM team";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
     // echo "<br> year-$i league_id-$league_id tm_id-$tm_id position-$position ";
             
        echo "id: " . $row["id"]. " - Name: " . $row["name"]. "<img src='img/".$row["logo"]."'  height='25' width='25'><br>";
    }
} else {
    echo "0 results";
}
$conn->close();

?>


