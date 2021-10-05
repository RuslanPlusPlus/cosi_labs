package com.rusned.laba1.util;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ImageManager {
    public void compute(BufferedImage image, PreparationType type, int param){

        WritableRaster raster = image.getRaster();

        for (int i = 0; i < raster.getWidth(); i++) {
            for (int j = 0; j < raster.getHeight(); j++) {
                int[] pixel = raster.getPixel(i, j, new int[4]);
                float luminance = pixel[0] * 0.2126f + pixel[1] * 0.7152f + pixel[2] * 0.0722f;

                switch(type){
                    case BINARIZATION:{
                        if (luminance > param) pixel = new int[]{255, 255, 255, 0};
                        else pixel = new int[]{0, 0, 0, 0};
                        break;
                    }
                    case LUMA_SLICE:{
                        if (luminance > 128 + param || luminance < 128 - param) pixel = new int[]{255, 255, 255, 0};
                        else pixel = new int[]{0, 0, 0, 0};
                    }
                }

                raster.setPixel(i, j, pixel);
            }
        }
        image.setData(raster);
    }

    public void filter(BufferedImage resImage, BufferedImage tmpImage) {
        WritableRaster raster = resImage.getRaster();
        WritableRaster tmpRaster = tmpImage.getRaster();

        for(int i = 0; i < raster.getWidth(); i++) {
            for (int j = 0; j < raster.getHeight(); j++) {
                int[] pixel = raster.getPixel(i, j, new int[4]);

                tmpRaster.setPixel(i, j, new int[]{pixel[0], pixel[1], pixel[2], pixel[3]});
            }
        }

        for (int i = 0; i < raster.getWidth(); i++) {
            for (int j = 0; j < raster.getHeight(); j++) {
                if (i != 0 && i != raster.getWidth() - 1 && j != 0 && j != raster.getHeight() - 1) {
                    int[][] pixelsSobel = initPixels(tmpRaster, i, j);

                    float sobelX = ((pixelsSobel[6][0] + 2 * pixelsSobel[7][0] + pixelsSobel[8][0]) * 0.2126f +
                            (pixelsSobel[6][1] + 2 * pixelsSobel[7][1] + pixelsSobel[8][1]) * 0.7152f +
                            (pixelsSobel[6][2] + 2 * pixelsSobel[7][2] + pixelsSobel[8][2]) * 0.0722f) -
                            ((pixelsSobel[0][0] + 2 * pixelsSobel[1][0] + pixelsSobel[2][0]) * 0.2126f +
                                    (pixelsSobel[0][1] + 2 * pixelsSobel[1][1] + pixelsSobel[2][1]) * 0.7152f +
                                    (pixelsSobel[0][2] + 2 * pixelsSobel[1][2] + pixelsSobel[2][2]) * 0.0722f);

                    float sobelY = ((pixelsSobel[2][0] + 2 * pixelsSobel[5][0] + pixelsSobel[8][0]) * 0.2126f +
                            (pixelsSobel[2][1] + 2 * pixelsSobel[5][1] + pixelsSobel[8][1]) * 0.7152f +
                            (pixelsSobel[2][2] + 2 * pixelsSobel[5][2] + pixelsSobel[8][2]) * 0.0722f) -
                            ((pixelsSobel[0][0] + 2 * pixelsSobel[3][0] + pixelsSobel[6][0]) * 0.2126f +
                                    (pixelsSobel[0][1] + 2 * pixelsSobel[3][1] + pixelsSobel[6][1]) * 0.7152f +
                                    (pixelsSobel[0][2] + 2 * pixelsSobel[3][2] + pixelsSobel[6][2]) * 0.0722f);

                    int pixel = (int) ((Math.sqrt(Math.pow(sobelX, 2) + Math.pow(sobelY, 2))) / (4 * Math.sqrt(2)));

                    raster.setPixel(i, j, new int[]{pixel, pixel, pixel, 0});
                }
            }
        }

        resImage.setData(raster);
    }

    private static int[][] initPixels(WritableRaster raster, int i, int j) {
        int[][] pixelsSobel = new int[9][];

        pixelsSobel[0] = raster.getPixel(   i - 1,  j - 1, new int[4]);
        pixelsSobel[1] = raster.getPixel(      i,      j - 1, new int[4]);
        pixelsSobel[2] = raster.getPixel(   i + 1,  j - 1, new int[4]);
        pixelsSobel[3] = raster.getPixel(   i - 1,     j,     new int[4]);
        pixelsSobel[4] = raster.getPixel(      i,         j,     new int[4]);
        pixelsSobel[5] = raster.getPixel(   i + 1,     j,     new int[4]);
        pixelsSobel[6] = raster.getPixel(   i - 1,  j + 1, new int[4]);
        pixelsSobel[7] = raster.getPixel(      i,      j + 1, new int[4]);
        pixelsSobel[8] = raster.getPixel(   i + 1,  j + 1, new int[4]);

        return pixelsSobel;
    }
}
