package com.lontyu.dwjwap.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Created by Cory on 2018/10/6.
 */
@ApiModel("赔率配置")
public class OddsVo {

    @ApiModelProperty(value = "押注大小 1、闲 ；2、闲对；3 、和；4、庄对；5、庄")
    private int  selectedSize;
    @ApiModelProperty(value="赔率")
    private BigDecimal odds;

    public OddsVo(){

    }

    public OddsVo( int selectedSize,BigDecimal odds){
        this.selectedSize=selectedSize
        ;this.odds=odds;
    }

    public int getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(int selectedSize) {
        this.selectedSize = selectedSize;
    }

    public BigDecimal getOdds() {
        return odds;
    }

    public void setOdds(BigDecimal odds) {
        this.odds = odds;
    }
}
