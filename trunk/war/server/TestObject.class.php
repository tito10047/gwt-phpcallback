<?php
/*
include_once "jsonrpcphp/jsonRPCServer.php";
include_once 'jsonrpcphp/json.php';
*/

class GwtRpcObject extends jsonParsingObjectABS{
    private $firstIterator = 1;
    private $properities = array();
       
    public function __compress(){
        if (count($this->properities)!=0) return;
        $vals = array_keys ( get_object_vars ( $this ) );
        $cnt = count($vals);
        $cnt = ($this->firstIterator==1?$cnt-2:$cnt);
        for ($i=$this->firstIterator;$i<$cnt;$i++){ 
            $char = getChar($i-$this->firstIterator);
            $val = $vals[$i];
            $this->$char=$this->$val;
            $this->properities[]=$val;
            unset( $this->$val );    
            
            $properityType=$this->OBJECTS_VARS_METADATA[$i-$this->firstIterator];
            $isArray = isArrayType($properityType);
            if (!isPrimitiveType($properityType)){
                if ($isArray){
                    if ($this->$char == null) continue;
                    foreach ($this->$char as $object){
                        if ($object == null) continue;
                        $object->__compress();
                    }
                }else{
                    if ($this->$char == null) continue;
                    $this->$char->__compress();
                }
            }
        }
        $this->firstIterator=3;
    }
    public function __unCompress(){
        if (count($this->properities)==0) return;
        $vals = array_keys ( get_object_vars ( $this ) );
        $cnt = count($this->properities);
        for ($i=0;$i<$cnt;$i++){ 
            $varName = $this->properities[$i];
            $val = $vals[$i+$this->firstIterator];
            $this->$varName=$this->$val;
            unset( $this->$val );  
            $properityType=$this->OBJECTS_VARS_METADATA[$i];
            $isArray = isArrayType($properityType);
            if (!isPrimitiveType($properityType)){
                if ($isArray){
                    if ($this->$varName == null) continue;
                    foreach ($this->$varName as $object){
                        if ($object == null) continue;
                        $object->__unCompress();
                    }
                }else{
                    if ($this->$varName == null) continue;
                    $this->$varName->__unCompress();
                }
            }
        }
        $this->properities = array();
    }
    public function parseFromJSONstring($JSONstring){
        $this->parseFromJSONarray(json_decode($JSONstring,true));
    }
    public function parseFromJSONarray($JSONobject){
        if (count($this->properities)==0) $this->__compress();
        $properities = array();
        $vals = array_keys ( get_object_vars ( $this ) );
        $cnt = count($vals)-$this->firstIterator;
        for ($i=0;$i<$cnt;$i++)
            $properities[]=$vals[$i+$this->firstIterator];
        parent::__parseFromJSONobject($this->OBJECTS_VARS_METADATA,$properities, $JSONobject, $this);
        $this->__unCompress();
    }
    public function parseToJSONstring(){
        if (count($this->properities)==0) $this->__compress();
        $str = json_encode($this);
        $this->__unCompress();
        return $str;
    }
    //trigger_error("Form method is not defined");
}


class TestObject extends GwtRpcObject{

    protected $OBJECTS_VARS_METADATA=array("String[]","int[]","double[]","char[]","long[]","byte[]","short[]","boolean[]","TestObject[]","String","TestObject");
    
    public $stringArr = array(null,null);
    public $intArr = array(null,null);
    public $doubleArr = array(null,null);
    public $charArr = array(null,null);
    public $longArr = array(null,null);
    public $byteArr = array(null,null);
    public $shortArr = array(null,null);
    public $boleanArr = array(false,false);
    public $testObjectArr = array(null,null);
    public $string = "super";
    public $testObject = null;

}
/* DEBUDDING
$t = new TestObject();
$t->stringArr[0]="jojo";
$e = new TestObject();
$e->stringArr[0]="fero";
$t->testObject = $e;

$jsonString =  $t->parseToJSONstring();

echo "$jsonString<br><br>\n\n";

$f = new TestObject();
$f->parseFromJSONstring($jsonString);

print_r($f);
*/
?>

