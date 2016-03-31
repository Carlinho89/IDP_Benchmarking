

<?php


require('helper.php');

$tables= 0;
echo $sql = 'CREATE TABLE IF NOT EXISTS `fixed_data` (
  `id` int(11) NOT NULL,
  `team_id` int(11) NOT NULL,
  `team_name` varchar(50) NOT NULL,
  `league_id` int(11) NOT NULL,
  `input_id` int(11) NOT NULL,
  `value` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;';

if ($conn->query($sql)){
	 $tables++;

}
echo "<br><br>";


echo $sql = 'CREATE TABLE IF NOT EXISTS `inputs` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `type` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;';

if ($conn->query($sql)){
	 $tables++;

}
echo "<br><br>";


echo $sql = 'CREATE TABLE IF NOT EXISTS `leagues` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `teamsNumber` int(11) NOT NULL,
  `logo` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;';

if ($conn->query($sql)){
	  $tables++;

}
echo "<br><br>";


echo $sql = 'CREATE TABLE IF NOT EXISTS `seasonal_data` (
  `id` int(11) NOT NULL,
  `team_id` int(11) NOT NULL,
  `team_name` varchar(50) NOT NULL,
  `year` int(11) NOT NULL,
  `league_id` int(11) NOT NULL,
  `input_id` int(11) NOT NULL,
  `value` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;';

if ($conn->query($sql)){
	 $tables++;

}
echo "<br><br>";


echo $sql = 'CREATE TABLE IF NOT EXISTS `teams` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `tm_id` int(50) NOT NULL,
  `logo` text NOT NULL,
  `league_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;';

if ($conn->query($sql)){
	 $tables++;

}
echo "<br><br>";


echo $sql = 'ALTER TABLE `fixed_data`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_index` (`team_id`,`input_id`);
';

if ($conn->query($sql)){
	 $tables++;

}
echo "<br><br>";


echo $sql = 'ALTER TABLE `inputs`
  ADD PRIMARY KEY (`id`);';

if ($conn->query($sql)){
	 $tables++;

}
echo "<br><br>";


echo $sql = 'ALTER TABLE `leagues`
  ADD PRIMARY KEY (`id`);';

if ($conn->query($sql)){
	 $tables++;

}
echo "<br><br>";


echo $sql = 'ALTER TABLE `seasonal_data`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_index` (`team_id`,`year`,`input_id`);';

if ($conn->query($sql)){
	 $tables++;

}
echo "<br><br>";

echo $sql = 'ALTER TABLE `teams`
  ADD PRIMARY KEY (`id`);';

if ($conn->query($sql)){
	 $tables++;

}
echo "<br><br>";




echo $sql = 'ALTER TABLE `fixed_data`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;';

if ($conn->query($sql)){
	 $tables++;

}
echo "<br><br>";



echo $sql = 'ALTER TABLE `seasonal_data`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;';

if ($conn->query($sql)){
	 $tables++;

}
echo "<br><br>";

echo $sql = 'ALTER TABLE `inputs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;';

if ($conn->query($sql)){
	 $tables++;

}
echo "<br><br>";
echo $sql = 'ALTER TABLE `teams`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;';

if ($conn->query($sql)){
	 $tables++;
	

}
echo "<br><br>";

echo $sql = "INSERT INTO `leagues` (`id`, `name`, `teamsNumber`, `logo`) VALUES
(1, 'Serie A', 20, 'SerieA500.png'),
(2, 'Premier League', 20, 'Premier500.png'),
(3, 'Liga', 20, 'Liga500.png'),
(4, 'Bundes Liga', 18, 'Bundes500.png');";

if ($conn->query($sql)){
 	$tables++;

}
echo "<br><br>";


echo "$tables/15 statements executed";
echo "<br><br>";

echo '<a href="home.html#two" class="btn btn-info" role="button">Continue</a>';
 ?>


