<?php
/**
* Abstract object for compressing properties and JSONParsing
*/
class GwtRpcObjectABS{
    /**
    * before first time compresing is properties ordered like :
    * $OBJECTS_VARS_METADATA, {object properties}, $firstIterator, $properities
    * after first compress order is like :
    * $OBJECTS_VARS_METADATA, $properities, $firstIterator , {object properties}
    * $firstIterator is for controling this
    * @var int
    */
    private $firstIterator = 1;
    /**
    * Array for save original properties names compressed by method __compress()
    * 
    * @var array(String)
    */
    private $properities = array();
       
    /**
    * Compresing all properties names to one char name
    * like: from $fooProperty=5 to $a=5
    * 
    */
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
    
    /**
    * Uncompresing properties compresed by __compress() if is compressed
    * 
    */
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
    /**
    * Parse JSONString to this object values
    * 
    * @param String $JSONstring
    */
    public function parseFromJSONstring($JSONstring){
        $this->parseFromJSONarray(json_decode($JSONstring,true));
    }
    /**
    * Parse JSONArray to this object values
    * If object is not compressed, will be compressed
    * 
    * @param String $JSONstring
    */
    public function parseFromJSONarray($JSONobject){
        if (count($this->properities)==0) $this->__compress();
        $properities = array();
        $vals = array_keys ( get_object_vars ( $this ) );
        $cnt = count($vals)-$this->firstIterator;
        for ($i=0;$i<$cnt;$i++)
            $properities[]=$vals[$i+$this->firstIterator];
        JsonParsingObjectABS::__parseFromJSONobject($this->OBJECTS_VARS_METADATA,$properities, $JSONobject, $this);
        $this->__unCompress();
    }
    /**
    * Parse object to JSONString
    * 
    * @return String JSONString
    */
    public function parseToJSONstring(){
        if (count($this->properities)==0) $this->__compress();
        $str = json_encode($this);
        $this->__unCompress();
        return $str;
    }
    //trigger_error("Form method is not defined");
}
?>
