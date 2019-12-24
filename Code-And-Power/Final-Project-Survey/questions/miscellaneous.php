<?php
    // Grab the user id from the POST data sent to us from the previous page
    $user_id = $_POST["user_id"];

    // Store the answer to the previous question, if applicable
    include "../store-answer.php";

    // Specify the question text to be displayed on this page
    $topic = "Miscellaneous (part 8 of 8)";
    $questions = [
        ["I believe most stereotypes stem from some truth.", [6, 5, 4, 3, 2, 1, 0]], 
        ["I believe that speaking a different language than my own is a valuable asset.", [0, 1, 2, 3 , 4, 5, 6]],
		["In the United States, data collected from white people is more valuable.", [6, 5, 4, 3, 2, 1, 0]]
    ];
?>

<!DOCTYPE html>

<html>
	<head>
		<meta charset="utf-8" />
		<title>Questions (part 8 of 8)</title>
		
		<link rel="stylesheet" type="text/css" href="../content/css/site.css">
	</head>
	
	<body>
		<main>
			<header class="header-main">
				<h1 class="header-main-title header-main-title">Implicit Bias Test</h1>
			</header>

			<section>
                <form method="post" action="../results.php">
                    <h2><?= $topic ?></h2>
                    <input type="hidden" name="user_id" value="<?= $user_id ?>" />
                    
                    <?php for ($i = 0; $i < count($questions); $i++) { ?> 
                        <div class="question-item">
                            <h4><?= $questions[$i][0] ?></h4>
                            <input type="hidden" name="questions[]" value="<?= $questions[$i][0] ?>" />

                            <div class="question-options">
                                <label class="question-options-agree">Agree</label>
                                <?php for ($j = 0; $j < count($questions[$i][1]); $j++) { ?>
                                    <input class="question-options-button" type="radio" name="answer-<?=$i?>" value="<?= $questions[$i][1][$j] ?>" required />
                                <?php } ?>
                                <label class="question-options-disagree">Disagree</label>
                            </div>
                                </div> 
                    <?php } ?>

                    <input class="btn" type="submit" value="View Results" />
                </form>
			</section>

			<footer>
				<small>The content of this website is licensed under <a href="https://creativecommons.org/licenses/by/4.0/" target="_blank">CC BY 4.0</a>.</small>
			</footer>
		</main>
	</body>
</html>




