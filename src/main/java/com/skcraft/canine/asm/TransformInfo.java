package com.skcraft.canine.asm;

public class TransformInfo {

    public String className;
    public String methodName;
    public MethodDescriptor desc;
    public String facingVar;
    public String varType;

    public TransformInfo(String className, String methodName, MethodDescriptor desc, String facingVar, String varType) {
        this.className = className;
        this.methodName = methodName;
        this.desc = desc;
        this.facingVar = facingVar;
        this.varType = varType;
    }

}
