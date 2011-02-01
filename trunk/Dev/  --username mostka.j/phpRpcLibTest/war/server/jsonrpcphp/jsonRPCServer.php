<?php
ob_start();

define("CLASSES_DIR","./");
    
include_once "./functions.php.php";
include_once "./GwtRpcObjectABS.class.php";
include_once "./JsonParsingObjectABS.class.php";
include_once "./RpcException.php";


/**
* Top linker. just use jsonRPCServer::process();
*/
class jsonRPCServer {
    public static function process(){
        $request = json_decode(file_get_contents('php://input'),true);
        $JSONarray = json_decode($request,true);
        //$JSONarray = json_decode('{"class":"TestServiceImpl", "method":"getTestObject", "parmsTypes":"String/int/TestObject/", "parms":["string","15",{"a":["jojo",null],"b":[null,null],"c":[null,null],"d":[null,null],"e":[null,null],"f":[null,null],"g":[null,null],"h":[false,false],"i":[null,null],"j":"super","k":{"a":["fero",null],"b":[null,null],"c":[null,null],"d":[null,null],"e":[null,null],"f":[null,null],"g":[null,null],"h":[false,false],"i":[null,null],"j":"super","k":null}}]}',true);
        try {
            autoloadclass($JSONarray['class']);
            $parameters = (object) array();
            $parametersTypes = explode("/", $JSONarray['parmsTypes']);
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
                $JSONarrayCopy[$i]=$JSONarray['parms'][$i];
            }
            $parametersTypes = $parametersTypesCopy;
            JsonParsingObjectABS::__parseFromJSONobject($parametersTypes,$properities, $JSONarrayCopy, $parameters);

            $object=new $JSONarray['class'];
            if ($result = @call_user_func_array(array($object,$JSONarray['method']),$parameters)) {
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
        return true;
    }
}

//jsonRPCServer::process();
?>
