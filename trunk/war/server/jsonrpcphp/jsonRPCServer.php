<?php
    define("CLASSES_DIR","..");
    
    include_once "./json.php";
    include_once "./RpcException.php";
    include_once "./autoloader.php";


include_once 'json.php';
function rlog($log){
	if ($tmpFile = fopen ('./state.txt','a')) {
		fwrite($tmpFile,date('r').' - '.$log."\r\n");
		fclose($tmpFile);
		return true;
	}
} 
/*
					COPYRIGHT

Copyright 2007 Sergio Vaccaro <sergio@inservibile.org>

This file is part of JSON-RPC PHP.

JSON-RPC PHP is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

JSON-RPC PHP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with JSON-RPC PHP; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * This class build a json-RPC Server 1.0
 * http://json-rpc.org/wiki/specification
 *
 * @author sergio <jsonrpcphp@inservibile.org>
 */
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
    
    protected function __parseFromJSONobject($targetObjectParmsTypes, $properities, $JSONobject, &$targetObject){
        if ($JSONobject==null) return;
        global $G__primitiveTypes;
        $isArray = false;
        $isPrimitive = false;
        foreach ($JSONobject as $JSONobjectParmName=>$JSONobjectParmValue){
            //echo"$JSONobjectParmName ";
            //print_r($JSONobjectParmValue);
            //echo "<br>\n";
            $targetObjectProperityIndex = count($properities);
            for ($i=0;$i<$targetObjectProperityIndex;$i++){
                if ($properities[$i]==$JSONobjectParmName){
                    $targetObjectProperityIndex=$i;
                    break;
                }
            }
            if ($targetObjectProperityIndex == count($properities)) {trigger_error("variable $JSONobjectParmName not find in object");}
            
            $targetObjectParmType = $targetObjectParmsTypes[$targetObjectProperityIndex];
            if (substr($targetObjectParmType, -2)=="[]"){
                $targetObjectParmType=substr($targetObjectParmType, 0, -2);
                $isArray = true;
            }
            foreach ($G__primitiveTypes as $primitiveType){
                if ($primitiveType==$targetObjectParmType){
                    $isPrimitive = true;
                    break;
                }
            }
            $gettedValue = null;
            if ($isPrimitive){
                $targetObject->$JSONobjectParmName=$JSONobjectParmValue;
                switch ($targetObjectParmType){
                    case "String":  break;
                    case "int":     $this->__convertToInt($targetObject->$JSONobjectParmName);      break;
                    case "double":  $this->__convertToDouble($targetObject->$JSONobjectParmName);   break;
                    case "boolean": $this->__convertToBoolean($targetObject->$JSONobjectParmName);  break;
                    case "char":    $this->__convertToInt($targetObject->$JSONobjectParmName);      break;
                    case "byte":    $this->__convertToInt($targetObject->$JSONobjectParmName);      break;
                    case "short":   $this->__convertToInt($targetObject->$JSONobjectParmName);      break;
                    case "long":    $this->__convertToInt($targetObject->$JSONobjectParmName);      break;
                    case "float":   $this->__convertToDouble($targetObject->$JSONobjectParmName);   break;
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
            if ($arrayParmValue == ""){
                $array=null;
            }else
            $array = (int)$arrayParmValue;
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
            if ($arrayParmValue == ""){
                $array=null;
            }else
            $array = (double)$arrayParmValue;
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
            if ($arrayParmValue == ""){
                $array=false;
            }else
            $array = (boolean)$arrayParmValue;
        }
    }
}
class jsonRPCServer extends jsonParsingObjectABS{
    public static function process(){
        //$request = json_decode(file_get_contents('php://input'),true);
        $JSONarray = json_decode('{"class":"TestServiceImpl", "method":"getTestObject", "parmsTypes":"String/int/TestObject/", "parms":["string","15",{}]}',true);
        try {
            autoloadclass($JSONarray['class']);
            $parameters = (object) array();
            $parametersTypes = explode("/", $JSONarray['parmsTypes']);
            $properities = array();
            for ($i=0;$i<count($parametersTypes);$i++){
                $chr = getChar($i);
                $parameters->$chr = null;
                $properities[$i]=$i;
            }
            parent::__parseFromJSONobject($parametersTypes,$properities, $JSONarray, $parameters);
            return;
            parseParms($JSONarray['parmsTypes'],$JSONarray['parms']);
            $object=new RPCServerWrapper(new $JSONarray['parmtype'], new $JSONarray['class']);
            if ($result = @call_user_func_array(array($object,$JSONarray['method']),$JSONarray['params'])) {
                $response = array (
                                    //'id' => $request['id'],
                                    'result' => $result,
                                    'error' => NULL
                                    );
            } else {
                $response = array (
                                    //'id' => $request['id'],
                                    'result' => NULL,
                                    'error' => new RpcException('unknown method or incorrect parameters')
                                    );
            }
        }catch (Exception $e) {
            //$e->ClassName = get_class($e);
            print_r($e);
            $response = array (
                                //'id' => $request['id'],
                                'result' => NULL,
                                'error' => $e
                                );
        }
        
        
        //if (!empty($request['id'])) { 
            header('content-type: text/javascript');
            echo json_encode($response);
        //}
        //return true;
    }
}

jsonRPCServer::process();
?>
