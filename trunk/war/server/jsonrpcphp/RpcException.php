<?php
                                
class RpcException extends Exception{
    public $message="Undefined exception"  ;
    public $code=0;
    public $file='';
    public $line=0;
    public $trace;
    public $tracestr=null;
     
    function __construct( $exception){
        $this->ClassName = "RpcException";
        if ($exception instanceof Exception){
            $this->message=$exception->getMessage()  ;
            $this->code=$exception->getCode();
            $this->file=$exception->getFile();
            $this->line=$exception->getLine();
            $this->trace=$exception->getTrace();
            $this->tracestr=$exception->getTraceAsString();
        }elseif (is_string($exception))
            $this->message=$exception;
    }
 }
?>
