<?php

if (isset($_POST["questions"])){
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
    
    foreach ($_POST["questions"] as $key => $value) {
        if (isset($_POST["answer-{$key}"])) {
            $query = $conn->prepare("INSERT INTO pedro_ingrid_clare_eddie (user_id, question, answer) VALUES (?, ?, ?)");
            $query->bind_param("iss", $user_id, $value, $_POST["answer-{$key}"]);
            $query->execute();
            $query->close();
        }
    }
    
    // Close the query and connection since we're done with them
    $conn->close();
}

?>