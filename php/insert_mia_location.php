
/**
* Created by PhpStorm.
* User: laggu
* Date: 2017-05-24
* Time: 오후 8:44
*/
<?php
    header("Content-Type: text/html;charset=UTF-8");
    $conn = mysqli_connect("127.0.0.1","laggu","1234","midmia");
    $data_stream = "'".$_POST['latitude']."','".$_POST['longitude']."'";
    $query = "insert into mia_location(latitude,longitude) values (".$data_stream.")";
    $result = mysqli_query($conn, $query);

    if($result)
        echo "1";
    else
        echo "-1";

    mysqli_close($conn);
?>
