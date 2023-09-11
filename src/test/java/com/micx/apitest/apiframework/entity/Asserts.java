package com.micx.apitest.apiframework.entity;

import lombok.Data;
import org.junit.jupiter.api.Assertions;

@Data
public class Asserts {

    private Object actual;
    private Object expect;

    //表达式
    private String expression;
    private String message;



    public boolean assertions(){
        boolean result = false;
        if("eq".equalsIgnoreCase(this.expression)){
            Assertions.assertEquals(this.expect,this.actual,this.message);
            result = true;
        }
        return result;
    }


}
