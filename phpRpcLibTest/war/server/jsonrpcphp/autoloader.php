<?php


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
