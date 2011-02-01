<?php

/**
* function used for converting properties names to short one char names
* 
* @param mixed $index
* @return mixed
*/
function getChar($index){
    //TODO must by modified for more than 22 chars
     $p__chars = array('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','r','s','t','u','v','w');
     return ($p__chars[$index]);
 }
$G__primitiveTypes= array("String","int","double","char","long","byte","short","boolean","float");
/**
* testing if property type is primitive
* 
* @param String $type
* @return boolean
*/
function isPrimitiveType($type){
    global $G__primitiveTypes;
    foreach ($G__primitiveTypes as $primitiveType){
        if ($primitiveType==$type){
            return (true);
        }
    }
    return (false);
}
/**
* testing if property type is array {String[]}, if yes deleting last 2 char {[]}
* 
* @param string $valueName
* @return boolean
*/
function isArrayType(&$valueName){
    if (substr($valueName, -2)=="[]"){
        $valueName=substr($valueName, 0, -2);
        return true;
    }
    return false;
}
/**
* import file with class by class name
* 
* @param String Class name with package
*/
function autoloadclass($name) {
    if (substr($name, -4, 4)==".php")
        $name=substr($name,0,strlen($name)-4);
    if (substr($name, -4, 4)==".class")
        $name=substr($name,0,strlen($name)-4);
    $name=str_replace(".", "/", $name);
    $name=CLASSES_DIR."/".$name.".class.php";

    if (file_exists($name)){
        include_once($name);
    }else
        throw new Exception("Unable to load  class $name");
}
?>
