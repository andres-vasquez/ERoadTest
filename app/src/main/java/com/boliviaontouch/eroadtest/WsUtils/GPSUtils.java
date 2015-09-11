package com.boliviaontouch.eroadtest.WsUtils;

/**
 * Created by andresvasquez on 9/3/15.
 */
public class GPSUtils {
    static public double DistanciaDosPuntos(double latPunto1, double lonPunto1, double latPunto2, double lonPunto2)
    {
        double a=latPunto1;
        double b=latPunto2;
        double c=lonPunto1-lonPunto2;
        double delta= Math.acos(Math.sin(a) * Math.sin(b) + Math.cos(a) * Math.cos(b) * Math.cos(c));
        double distancia=2* Math.PI*6375*delta/360;
        return distancia;
    }

}
