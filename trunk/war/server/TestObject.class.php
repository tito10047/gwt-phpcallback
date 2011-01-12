<?php
include_once "jsonrpcphp/jsonRPCServer.php";
include_once 'jsonrpcphp/json.php';
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
        }
        $this->properities = array();
    }
     
    public function parseFromJSONobject($JSONobject){
        if (count($this->properities)==0) $this->__compress();
        $properities = array();
        $vals = array_keys ( get_object_vars ( $this ) );
        $cnt = count($vals)-$this->firstIterator;
        for ($i=0;$i<$cnt;$i++)
            $properities[]=$vals[$i+$this->firstIterator];
        $this->__parseFromJSONobject($this->OBJECTS_VARS_METADATA,$properities, $JSONobject, $this);
    }
    protected function __parseFromJSONobject($parms, $properities, $JSONobject, $targetObject){
        global $G__primitiveTypes;
        $cnt = count($parms);
        $isArray = false;
        $isPrimitive = false;
        foreach ($parms as $keyIndex=>$parm){
            $properityName = $properities[$keyIndex];
            if (substr($parm, -2)=="[]"){
                $isArray = true;
                $parm=substr($parm, -2);
                $targetObject->$properityName=array();
            }
            foreach ($G__primitiveTypes as $primitiveType){
                if ($primitiveType==$parm){
                    $isPrimitive = true;
                    break;
                }
            }
            $gettedValue = null;
            if ($isPrimitive){
                /*switch ($parm){
                    case "String": 
                    case "int":
                    case "double":
                    case "char":
                    case "long":
                    case "byte":
                    case "short":
                    case "boolean":
                }*/
            }
            
            $isArray = false;
            $isPrimitive = false;
        }
    }
}


class TestObject extends GwtRpcObject{

    protected $OBJECTS_VARS_METADATA=array("String[]","int[]","double[]","char[]","long[]","byte[]","short[]","boolean[]","TestObject[]","String","TestObject");
    
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
    public $testObject = null;

}

$t = new TestObject();
$t->stringArr[0]="jojo";
$e = new TestObject();
$e->stringArr[0]="fero";
$t->testObject = $e;
$t->__compress();
//$t->__unCompress();


echo "<br />".$t->stringArr[0]."<br />";
$jsonObject = (array) json_decode(json_encode(array($t)));
$a = (array)$jsonObject[0];
//$a = (array)$a[0];
print_r($a);
$t = new TestObject();
$t->parseFromJSONobject($jsonObject[0]);
echo $t->stringArr[0];
?>

