<?php
include_once "jsonrpcphp/jsonRPCServer.php";
class GwtRpcObject{
    private $firstIterator = 0;
    private $properities = array();
       
    public function __compress(){
        if (count($this->properities)!=0) return;
        $vals = array_keys ( get_object_vars ( $this ) );
        for ($i=$this->firstIterator;$i<count($vals)-2;$i++){ 
            $char = getChar($i-$this->firstIterator);
            $val = $vals[$i];
            $this->$char=$this->$val;
            $this->properities[]=$val;
            unset( $this->$val );    
        }
        $this->firstIterator=2;
    }
    public function __unCompress(){
        if (count($this->properities)==0) return;
        $vals = array_keys ( get_object_vars ( $this ) );
        for ($i=$this->firstIterator;$i<count($vals)-2;$i++){ 
            $varName = $this->properities[$i-$this->firstIterator];
            $val = $vals[$i];
            $this->$varName=$this->$val;
            unset( $this->$val );   
        }
        $this->properities = array();
    }
     /*
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
    }*/
}


class TestObject extends GwtRpcObject{

    protected static $OBJECTS_VARS_METADATA=array("String[]","int[]","double[]","char[]","long[]","byte[]","short[]","boolean[]","TestObject[]","String");
    
    public $stringArr = array(1=>null);
    public $intArr = array(1=>null);
    public $doubleArr = array(1=>null);
    public $charArr = array(1=>null);
    public $longArr = array(1=>null);
    public $byteArr = array(1=>null);
    public $shortArr = array(1=>null);
    public $boleanArr = array(1=>null);
    public $testObjectArr = array(1=>null);
    public $string = "super";

}

$t = new TestObject();
$t->stringArr[0]="jojo";
$t->__compress();
$t->__unCompress();


echo $t->stringArr[0];
?>

