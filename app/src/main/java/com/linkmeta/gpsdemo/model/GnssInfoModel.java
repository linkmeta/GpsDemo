package com.linkmeta.gpsdemo.model;

public class GnssInfoModel {

    private String prn;
    private String snr;
    private String el;
    private String az;
    private String flag;
    private String usedInFix;
    public GnssInfoModel(){

    }
    public GnssInfoModel(String prn, String snr, String el, String az, String flag, String usedInFix){
        this.prn = prn;
        this.snr = snr;
        this.el = el;
        this.az = az;
        this.flag = flag;
        this.usedInFix = usedInFix;
    }

    public String getPrn(){
        return prn;
    }
    public void setPrn(String prn) {
        this.prn = prn;
    }

    public String getSnr(){
        return snr;
    }
    public void setSnr(String snr) {
        this.snr = snr;
    }

    public String getEl(){
        return el;
    }
    public void setEl(String el) {
        this.el = el;
    }

    public String getAz(){
        return az;
    }
    public void setAz(String az) {
        this.az = az;
    }

    public String getFlag(){
        return flag;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getUsedInFix(){
        return usedInFix;
    }
    public void setUsedInFix(String usedInFix) {
        this.usedInFix = usedInFix;
    }
}
