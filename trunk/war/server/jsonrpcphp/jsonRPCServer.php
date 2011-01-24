<?php
ob_start();

function rlog($log){
	if ($tmpFile = fopen ('./state.txt','a')) {
		fwrite($tmpFile,date('r').' - '.$log."\r\n");
		fclose($tmpFile);
		return true;
	}
} 

function getChar($index){
     $p__chars = array('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','r','s','t','u','v','w');
     return ($p__chars[$index]);
 }
$G__primitiveTypes= array("String","int","double","char","long","byte","short","boolean","float");
function isPrimitiveType($type){
    global $G__primitiveTypes;
    foreach ($G__primitiveTypes as $primitiveType){
        if ($primitiveType==$type){
            return (true);
        }
    }
    return (false);
}
function isArrayType(&$valueName){
    if (substr($valueName, -2)=="[]"){
        $valueName=substr($valueName, 0, -2);
        return true;
    }
    return false;
}
 
class jsonParsingObjectABS{
    
    public static function __parseFromJSONobject($targetObjectParmsTypes, $properities, $JSONobject, &$targetObject){
        if ($JSONobject==null) return;
        global $G__primitiveTypes;
        $isArray = false;
        $isPrimitive = false;
        foreach ($JSONobject as $JSONobjectParmName=>$JSONobjectParmValue){
            $targetObjectProperityIndex = count($properities);
            for ($i=0;$i<$targetObjectProperityIndex;$i++){
                if ($properities[$i]==$JSONobjectParmName){
                    $targetObjectProperityIndex=$i;
                    break;
                }
            }
            if ($targetObjectProperityIndex == count($properities)) {trigger_error("variable $JSONobjectParmName not find in object");}
            
            $targetObjectParmType = $targetObjectParmsTypes[$targetObjectProperityIndex];
            if (isArrayType($targetObjectParmType)){
                $isArray = true;
            }
            if (isPrimitiveType($targetObjectParmType)){
                $isPrimitive = true;
            }
            if ($isPrimitive){
                $targetObject->$JSONobjectParmName=$JSONobjectParmValue;
                switch ($targetObjectParmType){
                    case "String":  break;
                    case "int":     self::__convertToInt($targetObject->$JSONobjectParmName);      break;
                    case "double":  self::__convertToDouble($targetObject->$JSONobjectParmName);   break;
                    case "boolean": self::__convertToBoolean($targetObject->$JSONobjectParmName);  break;
                    case "char":    self::__convertToInt($targetObject->$JSONobjectParmName);      break;
                    case "byte":    self::__convertToInt($targetObject->$JSONobjectParmName);      break;
                    case "short":   self::__convertToInt($targetObject->$JSONobjectParmName);      break;
                    case "long":    self::__convertToInt($targetObject->$JSONobjectParmName);      break;
                    case "float":   self::__convertToDouble($targetObject->$JSONobjectParmName);   break;
                    default: trigger_error("cant find primitive type for '$parm'");
                }
            }else{
                autoloadclass($targetObjectParmType);
                if ($isArray){
                    $objectArray = array();
                    foreach($JSONobjectParmValue as $JSONsubObject){
                        if ($JSONsubObject==null) {
                            $objectArray[]=null;
                            continue;
                        }
                        $object = new $targetObjectParmType();
                        $object->parseFromJSONobject($JSONsubObject);
                        $objectArray[]=$object;
                    }
                    $targetObject->$JSONobjectParmName=$objectArray;
                }else{
                    $object = new $targetObjectParmType();
                    if ($JSONobjectParmValue==null) {
                        $targetObject->$JSONobjectParmName=null;
                        continue;
                    }
                    $object->parseFromJSONarray($JSONobjectParmValue);
                    $targetObject->$JSONobjectParmName=$object;
                }
            }
            
            $isPrimitive = false;
            $isArray = false;
        }
    }
    protected function __convertToInt(&$array){
        if (is_array($array)){
            foreach ($array as $arrayParmName=>$arrayParmValue){
                if ($arrayParmValue == ""){
                    $array[$arrayParmName]=null;
                    continue;
                }
                $array[$arrayParmName]=(int)$arrayParmValue;
            }
        }else{
            if ($array == ""){
                $array=null;
            }else
            $array = (int)$array;
        }
    }
    protected function __convertToDouble(&$array){
        if (is_array($array)){
            foreach ($array as $arrayParmName=>$arrayParmValue){
                if ($arrayParmValue == ""){
                    $array[$arrayParmName]=null;
                    continue;
                }
                $array[$arrayParmName]=(double)$arrayParmValue;
            }
        }else{
            if ($array == ""){
                $array=null;
            }else
            $array = (double)$array;
        }
    }
    protected function __convertToBoolean(&$array){
        if (is_array($array)){
            foreach ($array as $arrayParmName=>$arrayParmValue){
                if ($arrayParmValue == ""){
                    $array[$arrayParmName]=false;
                    continue;
                }
                $array[$arrayParmName]=(boolean)$arrayParmValue;
            }
        }else{
            if ($array == ""){
                $array=false;
            }else
            $array = (boolean)$array;
        }
    }
}
class jsonRPCServer {
    public static function process(){
        //$request = json_decode(file_get_contents('php://input'),true);
        $JSONarray = json_decode('{"class":"TestServiceImpl", "method":"getTestObject", "parmsTypes":"String/int/TestObject/", "parms":["string","15",{"a":["jojo",null],"b":[null,null],"c":[null,null],"d":[null,null],"e":[null,null],"f":[null,null],"g":[null,null],"h":[false,false],"i":[null,null],"j":"super","k":{"a":["fero",null],"b":[null,null],"c":[null,null],"d":[null,null],"e":[null,null],"f":[null,null],"g":[null,null],"h":[false,false],"i":[null,null],"j":"super","k":null}}]}',true);
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
            jsonParsingObjectABS::__parseFromJSONobject($parametersTypes,$properities, $JSONarrayCopy, $parameters);

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
