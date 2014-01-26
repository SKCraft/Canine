package com.skcraft.alicefixes.jsongenerator;

public class JsonHelperObject {

    public String className;
    public String methodName;
    public String returnType;
    public String[] params;
    public boolean blacklist;
    public boolean tileEntity;
    public String facingVar;
    public String varType;


    public JsonHelperObject(String className, String methodName, String returnType, String params, boolean blacklist,
                            boolean tileEntity, String facingVar, String varType) {
        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
        this.params = params.split("\n");
        this.blacklist = blacklist;
        this.tileEntity = tileEntity;
        this.facingVar = facingVar;
        this.varType = varType;
    }
}
