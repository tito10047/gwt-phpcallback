<?php

    
    include_once "./jsonrpcphp/json.php";  // use if php version on server is less than 5.2.0
    include_once "./jsonrpcphp/jsonRPCServer.php";
    
    jsonRPCServer::process();
?>
