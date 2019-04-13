package com.lontyu.dwjadmin.vo;

import com.lontyu.dwjadmin.entity.MoneyRecord;
import lombok.Data;

/**
 * @author as
 * @desc
 * @date 2019/1/15
 */
@Data
public class MoneyRecordSaveReqVO extends MoneyRecord {
    private String nickName;
}
