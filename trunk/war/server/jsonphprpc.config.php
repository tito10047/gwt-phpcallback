<?php

    define("CLASSES_DIR","./");
    
    include_once "./jsonrpcphp/json.php";
    include_once "./jsonrpcphp/RpcException.php";
    include_once "./jsonrpcphp/jsonRPCServer.php";
    include_once "./jsonrpcphp/autoloader.php";
    
    jsonRPCServer::process();
?>
