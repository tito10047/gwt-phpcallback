<?php

class ServiceLinker{
    private static $PATH_TO_LINK = "./linker.ini";
    public $methodName;
    public $serviceName;
    
    function __construct($serviceNum, $methodPos){
        $fileDataArray = file(self::$PATH_TO_LINK, FILE_SKIP_EMPTY_LINES);
        if (count($fileDataArray)==0 || $fileDataArray==null) throw new Exception("Linker not found or is empty");
        for ($i=2;$i<count($fileDataArray);$i++) {
            $data = explode("=",$fileDataArray[$i],2);
            if (count($data)==2){
                $dataArray[$data[0]]=substr($data[1],0,-2);
            }
        }
        if ($serviceNum>=$dataArray["ServicesCount"]) throw new Exception('error in ServicesLinker. service does exists for num : '.$serviceNum);
        $this->serviceName = $dataArray['Service_'.$serviceNum]."Impl";
        if (!isset($dataArray['Service_'.$serviceNum.'_'.$methodPos])) throw new Exception('error in ServicesLinker.method does exists for pos: '.$this->serviceName.'_'.$methodPos);
        $this->methodName  = $dataArray['Service_'.$serviceNum.'_'.$methodPos];
    }
    public function __set($i,$val){}
}
?>
