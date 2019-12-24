<?php
    // Generate a random number to use to identify the visitor throughout the survey
    $user_id = rand(1000000, 9999999);
?>

<!DOCTYPE html>

<!--
Created by: Clare Trunkett, Eddie Estevez, Ingrid Zhou, Pedro Goulart
Author: Bryan Knowles code given as template to UW-Madison LIS 500 class students
Last updated: Nov 25, 2019
License: Creative Commons Attribution 4.0 International license (https://creativecommons.org/licenses/by/4.0/)
-->

<html>
	<head>
		<meta charset="utf-8" />
		<title>Home</title>
		
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
				<h2>Introductory Video</h2>
				<iframe width="560" height="315" src="https://www.youtube.com/embed/Rt14x7_5C5o" 
				style="margin-right:auto;margin-left:auto;display:block;" frameborder="0" 
				allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen>
				</iframe>
				<h2>Questionnaire Overview</h2>
				<p>In this survey you will be shown a series of statements inspired by the Harvard Implicit Bias test regarding race and ethnicity, and you will be asked to what extent you agree or disagree with those statements, based on the Likert scale.</p>
				<p>Since these questions are designed to be thought provoking and to cover a few different topics, you may find the results upsetting or objectionable, though we try to provide resources for you to better analyze and understand your results. Your score will be based on a scale of 0 to 114, where a higher score means you might have more implicit biases that you are not aware of.</p>		
				<p>Here are a few instructions before you start:</p>
				<ul>
					<li><strong>Be yourself:</strong> Please answer the questions honestly, even if you do not like the answer.</li>
					<li><strong>Select insightful answers:</strong> Please make an effort to not leave any 'neutral' answers.</li>
				</ul>
				<p>Once you are ready to strat, please click the button below. The test should take about 5 minutes to complete</p>

				<form method="post" class="centered margin-vertical" action="questions/transportation.php">
                    <input type="hidden" name="user_id" value="<?= $user_id ?>" />
                    <input class="btn" type="submit" value="Begin Survey" />
                </form>

                <h2>About the Authors: </h2>
                <p><strong>Clare Trunkett</strong> is a third year undergraduate student at the University of Wisconsin-Madison studying Electrical Engineering with a focus on microprocessor system design, low-level programming, and mathematics. In addition, she works at the Center for Financial Security processing information and research of the Center's affiliates and fellows.</p>
                <p><strong>Eddie Estevez</strong> is a third year just like Clare, but studying Computer Engineering and Computer Science, as well pursuing a certificate within Digital Studies. On his free time, you might see him editing and creating digital content, whether it be videos or just another meme.</p>
                <p><strong>Ingrid Zhou</strong> is a senior majoring in food science. With a science degree, she is just as interested in food science as food-related social issues.</p>
                <p><strong>Pedro Goulart</strong> is a fourth year undergraduate student at the University of Wisconsin-Madison studying Computer Science with a focus on web development and digital studies. He works at both the DoIT Help Desk and the Vice Chancellor Office for Graduate Education and Research as a student developer.</p>
			</section>

			<footer>
				<small>The content of this website is licensed under <a href="https://creativecommons.org/licenses/by/4.0/" target="_blank">CC BY 4.0</a>.</small>
			</footer>
		</main>
	</body>
</html>