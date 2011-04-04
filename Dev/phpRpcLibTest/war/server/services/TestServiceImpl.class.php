<?php
class TestServiceImpl{
    public $ARGUMENTS_getTestObject="String|int|objects/TestObject";
    public function getTestObject($arg1, $arg2, TestObject $objTest){
        $object = new TestObject();
        $object->iii = "returned from php";
        return ($object);
    }
    public $ARGUMENTS_getTestObjectMesage="String|objects/TestObject";
    public function getTestObjectMesage($arg1, TestObject $objTest){
        
    }
}

?>