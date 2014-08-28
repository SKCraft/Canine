package com.skcraft.canine.asm;

import java.util.HashMap;
import java.util.Map;

public class MethodDescriptor {

    private String returnType;
    private String[] params;

    public MethodDescriptor(String returnType, String[] params) {
        this.returnType = returnType;
        this.params = params;
    }

    /**
     * Searches this method's parameters for the desired parameters and returns
     * a map containing the indexes of said parameters. Each index is mapped to
     * their parameter type and any subsequent index of the same parameter type
     * will be mapped to their type plus an integer(starting with 0).
     *
     * @param desiredParams An array of parameters whose indexes are to be returned
     * @return A map containing the indexes
     */
    public Map<String, Integer> getParamIndexes(String[] desiredParams) {
        Map<String, Integer> indexes = new HashMap<String, Integer>();
        for (String desired : desiredParams) {
            for (int i = 0; i < params.length; i++) {
                if ((desired.equals(params[i]) || isChildClass(params[i].replace("/", "."), desired.replace("/", ".")))
                        && !indexes.containsValue(i + 1)) {
                    int j = 0;
                    while(indexes.containsKey(desired + (j > 0 ? j : ""))) {
                        j++;
                    }
                    indexes.put(desired + (j > 0 ? j : ""), i + 1);
                    break;
                }
            }
        }
        return indexes;
    }
    
    private boolean isChildClass(String className, String superClassName) {
        try {
            return Class.forName(superClassName).isAssignableFrom(Class.forName(className));
        } catch(ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (String param : params) {
            if (ASMHelper.primitives.containsKey(param)) {
                sb.append(ASMHelper.primitives.get(param));
            } else if (!param.equals("")) {
                sb.append("L").append(param).append(";");
            }
        }
        sb.append(")");
        if(ASMHelper.primitives.containsKey(returnType)) {
            sb.append(ASMHelper.primitives.get(returnType));
        } else {
            sb.append("L").append(returnType).append(";");
        }
        return sb.toString();
    }

    public String getReturnType() {
        return returnType;
    }

    public String[] getParams() {
        return params;
    }
}
