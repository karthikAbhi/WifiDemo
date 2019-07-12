package com.nash.mywifiprinterlibrary;

public enum FunctionType {

    A("<A>"), B("<B>"), C("<C>");

    String type;

    FunctionType(String setType){
        this.type = setType;
    }

    public String getFunctionType(){
        return this.type;
    }
}
