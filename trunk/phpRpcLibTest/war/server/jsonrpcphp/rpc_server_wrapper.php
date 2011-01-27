<?php
class RPCServerWrapper{
	private $transferObject;
	private $server;

	/**
	 * Create a new RPCServerWrapper
	 * @param unknown_type $p_transferObject an instance of tranfert object
	 * @param unknown_type $p_server the server object which will deal with the RPC requests
	 */
	public function __construct($p_transferObject, $p_server){
		$this->transferObject=$p_transferObject;
		$this->server=$p_server;
	}

	public function __call($p_name , $p_arguments){
		if($this->transferObject!=null){
			$this->convertArrayToObject($p_arguments[0],$this->transferObject);
			return $this->server->$p_name($this->transferObject);
		}
		return $this->server->$p_name();
	}
	
	/**
	 * Return true of the property is an array of Object
	 * Example of $objectClass: Person[]
	 * @param string $p_objectClass 
	 * @return boolean true if the property is an array of Object 
	 */
	private function isPropertyArray($p_objectClass){
		return (strlen($p_objectClass)-(strpos($p_objectClass, "[]")+2))==0;
	}
	
	/**
	 * Return a class name from a property
	 * @param string $p_objectClass the name of the class as extracted from meta-data
	 * @return string a class name
	 */
	private function extractClassNameFromArrayProperty($p_objectClass){
		return substr($value,0,strpos($value, "[]"));
	}

	/**
	 * Convert an array (extracted from JSON parsing) into an Object
	 * @param array an array of ... something
	 * @param object an Object
	 * @return an Object unserialized
	 */
	private function convertArrayToObject($p_array, $p_object){
		$objectMetaData=$p_object->getMETADATA();
		foreach($p_array as $key => $value){
			//If the property is an object (as defined in the meta data of the transfer object)
			if(array_key_exists($key,$objectMetaData)){
				$objectClass=$objectMetaData[$key];
				if((strlen($objectClass)-(strpos($objectClass, "[]")+2))==0){
					//If the property is an array of objects
					$objectClass=substr($objectClass,0,strpos($objectClass, "[]"));
					$objectProperty=array();
					foreach($value as $item){
						$objectPropertyInstance=new $objectClass;
						$objectProperty[]=$this->convertArrayToObject($item,$objectPropertyInstance);
					}
					$p_object->$key=$objectProperty;
				}else{
					//If the property is a single object 
					$objectProperty=new $objectClass;
					$p_object->$key=$this->convertArrayToObject($value,$objectProperty);
				}
			}else{
				//A simple property (string, int, array of simple type)
				$p_object->$key=$value;
			}
		}
		return $p_object;
	}
}
?>