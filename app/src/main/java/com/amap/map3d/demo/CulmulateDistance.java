package com.amap.map3d.demo;

import com.autonavi.amap.mapcore.IPoint;
import com.autonavi.amap.mapcore.interfaces.IPolygon;
import com.autonavi.amap.mapcore.interfaces.IPolyline;

/**
 * Created by yys on 2017/4/24.
 */

public class CulmulateDistance {









    /// <summary>
    /// 向量的模
    /// </summary>
    /// <param name="v"></param>
    /// <returns></returns>

    /// <summary>
    /// 2D数量积，点乘
    /// </summary>
    /// <param name="u"></param>
    /// <param name="v"></param>
    /// <returns></returns>

    /// <param name="x">增量X</param>
    /// <param name="y">增量Y</param>
    /// <returns>象限角</returns>
    public static double GetQuadrantAngle(double x, double y)
    {
        double theta = Math.atan(y / x);
        if (x > 0 && y == 0) return 0;
        if (x == 0 && y > 0) return Math.PI / 2;
        if (x < 0 && y == 0) return Math.PI;
        if (x == 0 && y < 0) return 3 * Math.PI / 2;

        if (x > 0 && y > 0) return theta;
        if (x > 0 && y < 0) return Math.PI * 2 + theta;
        if (x < 0 && y > 0) return theta + Math.PI;
        if (x < 0 && y < 0) return theta + Math.PI;
        return theta;
    }

}
