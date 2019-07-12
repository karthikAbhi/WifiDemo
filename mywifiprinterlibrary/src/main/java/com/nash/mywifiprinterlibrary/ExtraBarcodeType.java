package com.nash.mywifiprinterlibrary;

public enum ExtraBarcodeType {

    PDF417("0"),
    CODE128("1");

    private String extraBarcodeType;

    ExtraBarcodeType(String extraBarcodeType) {
        this.extraBarcodeType = extraBarcodeType;
    }

    public String getExtraBarcodeType() {
        return extraBarcodeType;
    }
}
