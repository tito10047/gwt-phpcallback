<?php
include_once 'jsonrpcphp/jsonRPCServer.php';
include_once 'jsonrpcphp/rpc_server_wrapper.php';

class TestServiceImpl{
    public function search(GwtRpcObject $indikacka, String $sss=null){
        return "{}";
    }
}
?>
