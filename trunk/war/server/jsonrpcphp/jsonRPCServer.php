<?php
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
$G__primitiveTypes= array("String","int","double","char","long","byte","short","boolean");

 
class jsonParsingObjectABS{
    protected function __parseFromJSONobject($parms, $JSONobject){
        $parms = $pieces = explode(",", $parms);
        $cnt = count($parms);
        $parameters = array();
        $isArray = false;
        $isPrimitive = false;
        foreach ($parms as $parm){
            if (substr($parm, -2)=="[]"){
                $isArray = true;
                $parm=substr($parm, -2);
                $parameters[]=array();
            }
            foreach ($primitiveTypes as $primitiveType){
                if ($primitiveType==$parm){
                    $isPrimitive = true;
                    break;
                }
            }
            if ($isPrimitive){}
            
            $isArray = false;
            $isPrimitive = false;
        }
    }
}
class jsonRPCServer {
    public static function process(){
        //$request = json_decode(file_get_contents('php://input'),true);
        $request = json_decode('{"class":"TestService", "method":"getTestObject", "parmsTypes":"String/int/TestObject/", "parms":["string","15",{}]}');
        try {
            autoloadclass($request['class']);
            parseParms($request['parmsTypes'],$request['parms']);
            $object=new RPCServerWrapper(new $request['parmtype'], new $request['class']);
            if ($result = @call_user_func_array(array($object,$request['method']),$request['params'])) {
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
            $e->ClassName = get_class($e);
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
    
    
    
	/**
	 * This function handle a request binding it to a given object
	 *
	 * @param object $object
	 * @return boolean
	 */
	public static function handle($object) {	
		// checks if a JSON-RCP request has been received
		//PYC note: this test is not valid since the content-type is not correct
		/*
		 if (
			$_SERVER['REQUEST_METHOD'] != 'POST' || 
			empty($_SERVER['CONTENT_TYPE']) ||
			$_SERVER['CONTENT_TYPE']!="application/json"
			) {
			// This is not a JSON-RPC request
			return false;
		}*/
				
		// reads the input data
		$request = json_decode(file_get_contents('php://input'),true);
		
		// executes the task on local object
		try {
			if ($result = @call_user_func_array(array($object,$request['method']),$request['params'])) {
				$response = array (
									'id' => $request['id'],
									'result' => $result,
									'error' => NULL
									);
			} else {
				$response = array (
									'id' => $request['id'],
									'result' => NULL,
									'error' => 'unknown method or incorrect parameters'
									);
			}
		} catch (Exception $e) {
			$response = array (
								'id' => $request['id'],
								'result' => NULL,
								'error' => $e->getMessage()
								);
		}
		
		// output the response
		if (!empty($request['id'])) { // notifications don't want response
			header('content-type: text/javascript');
			echo json_encode($response);
		}
		
		// finish
		return true;
	}
}
?>
