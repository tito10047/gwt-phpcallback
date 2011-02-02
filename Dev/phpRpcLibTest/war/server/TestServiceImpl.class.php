<?php
//include_once 'jsonrpcphp/jsonRPCServer.php';
//include_once 'jsonrpcphp/rpc_server_wrapper.php';
class GwtRpcObject{
    function __toString(){
        return "<br />ssssss";
    }
}
class TestServiceImpl{
    public function search(Array $indikacka,GwtRpcObject $sss){
      echo $indikacka[0];
      echo $sss;
    }
    public function getTestObject($string, $integer, $testObject){

        return true;
    }
}
$e = new TestServiceImpl();
$e->search(array("s"),new GwtRpcObject());
?>
