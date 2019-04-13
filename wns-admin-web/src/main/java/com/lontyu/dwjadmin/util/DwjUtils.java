package com.lontyu.dwjadmin.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author as
 * @since 2018/12/29 15:24
 */
public class DwjUtils {

    /**
     * 查询牌是否成对（规则为匹配前两张）
     *
     * @param points 牌信息，逗号分隔
     * @return 是否成对
     */
    public static boolean checkDoublePoint(String points) {
        if (StringUtils.isBlank(points)) {
            return false;
        }
        String[] ps = points.split(",");
        if (ps.length < 2) {
            return false;
        }
        return ps[0].equals(ps[1]);
    }
}
