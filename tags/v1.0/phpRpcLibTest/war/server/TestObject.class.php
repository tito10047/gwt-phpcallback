<?php
class TestObject extends GwtRpcObjectABS{

    protected $OBJECTS_VARS_METADATA=array("String[]","int[]","double[]","char[]","long[]","byte[]","short[]","boolean[]","objects/TestObject[]","objects/TestObject2[]","String");

    public $aaa = array("ss","dd");                         /*java.lang.String[]*/
    public $bbb = array(0,0);                               /*int[]*/
    public $ccc = array(2.0,5.0,6.0,4.0);                   /*double[]*/
    public $ddd = array("","");                             /*char[]*/
    public $eee = array(0,0);                               /*long[]*/
    public $fff = array(0,0);                               /*byte[]*/
    public $ggg = null;                                     /*short[]*/
    public $hhh = array(false,false);                       /*boolean[]*/
    public $object = array(null,null);                      /*com.mostka.phprpclibtest.client.TestObject[]*/
    public $object2 = array(null,null);                     /*com.mostka.phprpclibtest.client.TestObject2[]*/
    public $iii = "nie super";                              /*java.lang.String*/

    function __construct(){
        $this->object2[1] = autoloadclass( "objects/TestObject2" );
    }
}
?>