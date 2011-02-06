<?php
ob_start();

define("CLASSES_DIR","./");
    
include_once "./jsonrpcphp/functions.php";
include_once "./jsonrpcphp/GwtRpcObjectABS.class.php";
include_once "./jsonrpcphp/JsonParsingObjectABS.class.php";
include_once "./jsonrpcphp/ServiceLinker.class.php";
include_once "./jsonrpcphp/RpcException.php";


/**
* Top linker. just use jsonRPCServer::process();
*/
class jsonRPCServer {
    public static function process(){
        //$request = json_decode(file_get_contents('php://input'),true);
        //$JSONarray = json_decode($request,true);
        $JSONarray = json_decode('{"A":"0", "B":"0", "C":["string","15",{"a":["ss","dd"], "b":[0,0], "c":[2,5,6,4], "d":[0,0], "e":[0,0], "f":[0,0], "h":["false","false"], "i":[null,null], "j":[null,null], "k":"supera"}]}',true);
        try {
            $serviceLinker = new ServiceLinker((int) $JSONarray['A'],(int) $JSONarray['B']);
            $serviceImpl = autoloadclass($serviceLinker->serviceName);
            
            $parameters = (object) array();
            $parametersTypes = explode("|", $serviceImpl->{"ARGUMENTS_".$serviceLinker->methodName});
            $properities = array();
            $JSONarrayCopy = array();
            $parametersTypesCopy = array();
            for ($i=0;$i<count($parametersTypes);$i++){
                if ($parametersTypes[$i]=="")
                    continue;
                $parametersTypesCopy[]=$parametersTypes[$i];
                $chr = getChar($i);
                $parameters->$i = null;
                $properities[$i]= $i;
                $JSONarrayCopy[$i]=$JSONarray['C'][$i];
            }
            $parametersTypes = $parametersTypesCopy;
            JsonParsingObjectABS::__parseFromJSONobject($parametersTypes,$properities, $JSONarrayCopy, $parameters);
            if ($result = @call_user_func_array(array($serviceImpl,$serviceLinker->methodName),(array)$parameters)) {
                $response = array (
                                    'result' => $result,
                                    'error' => NULL
                                    );
            } else {
                $response = array (
                                    'result' => NULL,
                                    'error' => new RpcException('unknown method or incorrect parameters')
                                    );
            }
        }catch (Exception $e) {
            $response = array (
                                'result' => NULL,
                                'error' => new RpcException($e)
                                );
        }
        if (ob_get_length()>1){
            $ob_content = ob_get_contents();
            $response = array (
                                'result' => NULL,
                                'error' => new RpcException('PRINTED BY ECHO is unacceptable!\n\n<br><br>'.($ob_content))
                                );
            ob_end_clean();
        }
        
        header('content-type: text/javascript');
        echo json_encode($response);
        //print_r( $response);
        return true;
    }
}

//jsonRPCServer::process();
?>
