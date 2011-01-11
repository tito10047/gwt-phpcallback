<?php
class GwtRpcObject{
    private static $p__chars = array('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','r','s','t','u','v','w');
    protected function getChar($index){
        return ($p__chars[$index]);
    }    
    function __construct(){
        $i=0;
        foreach ($this->vars as $key=>$atributes){
            $char = self::$p__chars[$i];
            if ($atributes[3]==true)
                $this->$char=null;
                else
                $this->$char=$atributes[4];
            foreach ( array_keys ( get_object_vars ( $this ) ) as $val){ 
                if ($val==$key)
                unset( $this->$val );    
            }
            $i++;
        }     
    }
    
    function __get($name){       
        $i=0;  
        foreach ($this->vars as $key=>$atributes){     
            if ($name==$key){
                $char = self::$p__chars[$i];
                return ($this->$char);
            }
            $i++;
        }                          
        trigger_error("undefined variable $name");
    }
    function __set($name,$value){
        $i=0;
        foreach ($this->vars as $key=>$atributes){  
            if ($name==$key){
                switch ($atributes[0]){
                    case 'int':     $this->setInt($key,self::$p__chars[$i],$value,$atributes);break;
                    case 'varchar': $this->setString($key,self::$p__chars[$i],$value,$atributes);break;
                    case 'text':    $this->setString($key,self::$p__chars[$i],$value,$atributes);break;
                }
            }
            $i++;
        } 
    }
    private function count_digit($number) { return  strlen((string) $number);   }
    
    private function setInt($key,$char, $value, $atributes){
        if ($atributes[5]=='auto_increment')
            trigger_error("Variable <b>$key</b> is <b>auto increment</b>.");
        if (!is_integer((int)$value)){
            trigger_error("Variable <b>$key::$value</b> is not integer");
            return;
        }
        $value = (int) $value;
        if ($this->count_digit($value)>$atributes[1])
            trigger_error("Value of variable is too long. <b>$key:$value</b> , akcepted $atributes[1] digits");
        
        $this->$char=$value;
    }
    private function setString($key,$char, $value, $atributes){
        if ($atributes[5]=='auto_increment')
            trigger_error("Variable <b>$key</b> is <b>auto increment</b>.");
        $value = (string) $value;
        if (strlen($value)>$atributes[1])
            trigger_error("Value of variable is too long. <b>$key</b> length:".strlen($value)." , akcepted $atributes[1] digits");
        if ($value=='')
            $this->$char=$value;
            else
            $this->$char=$atributes[4];
    }
}


class IndikackaRecord extends GwtRpcObject{
    private static $OBJECTS_METADATA=array(
        );
    public function getMETADATA() {
        return self::$OBJECTS_METADATA;
    }
    
    var $id, $signatura, $verzia, $name_czech, $name_deutsch, $other_name, $township, $obce, $year, $note, $AEC_CM, $NAZ_PUV_CS, $NAZ_PUV_NE;
    
    protected $vars = array("id"=>          array('int',    7,  false,'PRI',0 ,'auto_increment'),
                          "signatura"=>     array('varchar',4,  false,'MUL','',''),
                          "verzia"=>        array('int',    1,  false,'',   0 ,''),
                          "name_czech"=>    array('varchar',255,false,'',   '',''),
                          "name_deutsch"=>  array('varchar',255,false,'',   '',''),
                          "other_name"=>    array('text',   0,  true ,'',   '',''),
                          "township"=>      array('varchar',255,false,'',   '',''),
                          "obce"=>          array('text',   0,  false,'',   '',''),
                          "year"=>          array('int',    4,  false,'',   0 ,''),
                          "note"=>          array('text',   0,  false,'',   '',''),
                          "AEC_CM"=>        array('varchar',8,false,'',     '',''),
                          "NAZ_PUV_CS"=>    array('varchar',50,false,'',    '',''),
                          "NAZ_PUV_NE"=>    array('varchar',50,false,'',    '','')
                          );
    
    public $a, $b, $c, $d, $e, $f, $g, $h, $i, $j, $k, $l, $m;

}
?>

