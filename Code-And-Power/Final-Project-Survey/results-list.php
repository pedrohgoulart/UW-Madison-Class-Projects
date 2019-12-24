<?php
  // Database settings
  $mysql_server="localhost";
  $mysql_db="raroyst1_raroystonorgmain";
  $mysql_port="3306";
  $mysql_user="raroyst1_cfbd_cg";
  $mysql_password="W!SCsin2018";

  // Connect to the database
  $conn = new mysqli($mysql_server, $mysql_user, $mysql_password, $mysql_db);
  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }
  // Prepare our query
  $query_results = $conn->prepare("SELECT user_id, question, answer FROM pedro_ingrid_clare_eddie ORDER BY question");

  // Run our query to get the list of questions/answers for this visitor
  $query_results->execute();
  $results = $query_results->get_result();
  
  // Close the query and connection
  $query_results->close();
  $conn->close();

  // Compile data
  $users_total = 1;
  $sum_total = 0;
  $questions_list = [];
  
  $temp_user_count = true;
  $temp_saved_question = "";
  $temp_index = 0;
  
  foreach ($results as $value) {
      if ($value["question"] != $temp_saved_question) {
          // Add item to the list
          $questions_list[] = [$value["question"], $value["answer"]];
          
          // Update temp variables
          if ($temp_index == 1) {
              $temp_user_count = false;
          }
          $temp_saved_question = $value["question"];
          $temp_index++;
      } else {
          // Update question on the list
          $questions_list[$temp_index - 1][1] += $value["answer"];
          
          // Update number of unique users
          if ($temp_user_count) {
              $users_total++;
          }
      }
      $sum_total += $value["answer"];
  }
  
  // Get average from data
  $sum_total = round($sum_total/$users_total, 2);
  
  foreach ($questions_list as $key => $value) {
      $questions_list[$key][1] = round($questions_list[$key][1]/$users_total, 2);
  }
  
  // Get string
  $sum_total_string = "error: unexpected result";
  
  if ($sum_total >= 90) {
      $sum_total_string = "most users have implicit biases that they should keep track of";
  } else if ($sum_total >= 70) {
      $sum_total_string = "most users some implicit biases that they may want to keep track of";
  } else if ($sum_total >= 50) {
    $sum_total_string = "most users might have some implicit biases that they are unaware of";
  } else if ($sum_total >= 30) {
    $sum_total_string = "most users are generally unbiased";
  } else {
    $sum_total_string = "most users are generally unbiased and show signs that they accept people different from themselves";
  }
?>

<!DOCTYPE html>

<html>
	<head>
		<meta charset="utf-8" />
		<title>Average Results</title>
		
		<link rel="stylesheet" type="text/css" href="content/css/site.css">
	</head>
	
	<body>
	    <nav>
            <div class="navbar">
                <a href="index.php" class="active">Home</a>
                <a href="analysis/team-analysis.php">Team Analysis</a>
                <div class="dropdown">
                    <button class="dropbtn">Individual Analyses
                        <i class="dropdown-content"></i>
                    </button>
                    <div class="dropdown-content">
                        <a href="analysis/clare.php">Clare</a>
                        <a href="analysis/eddie.php">Eddie</a>
                        <a href="analysis/ingrid.php">Ingrid</a>
                        <a href="analysis/pedro.php">Pedro</a>
                    </div>
                </div>
                <a href="resources.php">Resources</a>
                <a href="results-list.php">Average Results</a>
	        </div>
	    </nav>
	    
		<main>
			<header class="header-main">
				<h1 class="header-main-title header-main-title">Implicit Bias Test</h1>
			</header>

			<section>
			    <h2>Average results</h2>
			    <p>Here’s you’ll see the average score for our test, which ranges from 0 to 114. Each question had the possibility of you achieving a number between 0 to 6, the higher the number is, the more it may suggests that you may have a bias against those who are not in the same racial and ethnic group as yourself.<p>
				<p>The current average score is <strong><?=$sum_total?>/114</strong> on our implicit bias test, and <strong><?= $users_total ?></strong> users have taken it. This means that <strong><?=$sum_total_string?></strong>. The higher the number, the higher the implicit bias.</p>
			    
				<h2>Results per questions</h2>
                <?php foreach ($questions_list as $key=>$item) { ?>
                    <p><strong>Question <?=($key + 1)?>:</strong> <?=$item[0]?></p>
                    <label>Average score: <?=$item[1]?>/6</label>
                <?php } ?>
			</section>

			<footer>
				<small>The content of this website is licensed under <a href="https://creativecommons.org/licenses/by/4.0/" target="_blank">CC BY 4.0</a>.</small>
			</footer>
		</main>
	</body>
</html>