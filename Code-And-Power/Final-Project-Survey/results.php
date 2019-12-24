<?php
  // Grab the user id from the POST data sent to us from the previous page
  $user_id = $_POST["user_id"];

  // Store the answer to the previous question, if applicable
  include "store-answer.php";

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
  $query_filtered_results = $conn->prepare("SELECT question, answer FROM pedro_ingrid_clare_eddie WHERE user_id = ?");
  $query_filtered_results->bind_param("i", $user_id);

  // Run our query to get the list of questions/answers for this visitor
  $query_filtered_results->execute();
  $results_filtered = $query_filtered_results->get_result();
  
  // Close the query and connection
  $query_filtered_results->close();
  $conn->close();

  // Compile data
  $sum_user_total = 0;
  
  foreach ($results_filtered as $key => $value) {
    $sum_user_total += $value["answer"];
  }
  
  $sum_total_string = "error: unexpected result";
  
  if ($sum_user_total >= 90) {
      $sum_total_string = "you have implicit biases that you should keep track of";
  } else if ($sum_user_total >= 70) {
      $sum_total_string = "you have some implicit biases that you may want to keep track of";
  } else if ($sum_user_total >= 50) {
    $sum_total_string = "you might have some implicit biases that you are unaware of";
  } else if ($sum_user_total >= 30) {
    $sum_total_string = "you are generally unbiased";
  } else {
    $sum_total_string = "you are generally unbiased and show signs that you accept people different from yourself";
  }
?>

<!DOCTYPE html>

<html>
	<head>
		<meta charset="utf-8" />
		<title>Results</title>
		
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
				<h1 class="header-main-title header-main-title">Implicit Bias Test Results</h1>
            </header>
      
			<section>
				<h2>Results</h2>
        <iframe width="560" height="315" src="https://www.youtube.com/embed/3Z7cNFscLgQ" frameborder="0" 
          style="margin-right:auto;margin-left:auto;display:block;"
        allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen>
        </iframe>
                <p>Here’s you’ll see your score ranging from 0 to 114. Each question had the possibility of you achieving a number between 0 to 6, the higher the number is, the more it may suggests that you may have a bias against those who are not in the same racial and ethnic group as yourself.<p>
				<p>You have scored <strong><?=$sum_user_total?></strong> on our implicit bias test. This means that <strong><?=$sum_total_string?></strong>. If you would like to learn how you compare to other users, check our our <a href="results-list.php">average results page</a>. Additionaly, please check your answers below to learn more about your score and check if there are any patterns to your answers. The higher the number, the higher the implicit bias.</p>
            </section>
            
            <section>
                <h2>Want to learn more about how to eliminate bias? Check out these <a href="resources.php">Resources</a></h2>
      </section>

    <section>
        <h2>Overview of Questions</h2>
        
        <?php foreach ($results_filtered as $key => $value) { ?>
          <p><strong>Question <?=($key + 1)?>:</strong> <?=$value["question"]?></p>
          <label>Answer score: <?=$value["answer"]?></label>
        <?php } ?>
	</section>

			<footer>
				<small>The content of this website is licensed under <a href="https://creativecommons.org/licenses/by/4.0/" target="_blank">CC BY 4.0</a>.</small>
			</footer>
		</main>
	</body>
</html>