<?php
class Fo{}
class test{
    var $ss = string;
    var $ee = Fo;
    
    function __construct(){
        
        foreach ( array_keys ( get_object_vars ( $this ) ) as $key=>$val){ 
            echo "$key=>$val<br />";   
        }
    }
}

new test();

echo "<br />";
?>
