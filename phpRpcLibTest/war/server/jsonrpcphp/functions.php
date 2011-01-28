<?php


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
