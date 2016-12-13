package jp.ac.kagawa_u.infoexpr.Sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;

public class ColorSensor extends EV3ColorSensor implements Sensor {

    private SensorMode lightMode = this.getMode(1);
    private SensorMode colorMode = this.getMode(2);

    /**
     * ColorSensorのコンストラクタ
     * @param Port SensorPort
     */
    public ColorSensor(Port port) {
        super(port);
    }

    /**
     * センサー値を取得
     * @return float[] RGBが格納されたfloat型配列
     */
    public float[] getValue() {
        float[] result = new float[colorMode.sampleSize()];
        colorMode.fetchSample(result, 0);
        return result;
    }

    /**
     * RGBを取得
     * @return float RGBが格納されたfloat型配列
     */
    public float[] getRGB() {
        return this.getValue();
    }

    /**
    * RGBの赤成分を取得
    * @return float RGBの赤成分が格納されたfloat型
    */
    public float getRed() {
        return this.getValue()[0];
    }

    /**
     * RGBの緑成分を取得
     * @return float RGBの緑成分が格納されたfloat型
     */
    public float getGreen() {
        return this.getValue()[1];
    }

    /**
     * RGBの青成分を取得
     * @return float RGBの青成分が格納されたfloat型
     */
    public float getBlue() {
        return this.getValue()[2];
    }

    /**
    * 反射光の値を取得
    * @return float 反射光の値が格納されたfloat型
    */
    public float getLight() {
        float[] result = new float[lightMode.sampleSize()];
        lightMode.fetchSample(result, 0);
        return result[0];
    }

    private float minColor(float[] colors) {
        float min = colors[0];
        if (min > colors[1]) min = colors[1];
        if (min > colors[2]) min = colors[2];
        return min;
    }

    private float maxColor (float[] colors) {
        float max = colors[0];
        if (max < colors[1]) max = colors[1];
        if (max < colors[2]) max = colors[2];
        return max;
    }

    private int minIndexOfColors(float[] colors) {
        int index = 0;
        float min = colors[0];
        if (min > colors[1]) {
            min = colors[1];
            index = 1;
        }
        if (min > colors[2]) {
            index = 2;
        }
        return index;
    }

    public float[] getHSV() {
        return this.getHSV(0);
    }

    public int getColorID() {
        float[] hsv = new float[3];
        hsv = this.getHSV();
        int colorId = -1;
        if (0 <= hsv[0] && hsv[0] < 30) colorId = 0;
        if (31 <= hsv[0] && hsv[0] < 60) colorId = 1;
        if (61 <= hsv[0] && hsv[0] < 90) colorId = 2;
        if (91 <= hsv[0] && hsv[0] < 120) colorId = 3;
        if (121 <= hsv[0] && hsv[0] < 150) colorId = 4;
        if (151 <= hsv[0] && hsv[0] < 180) colorId = 5;
        if (181 <= hsv[0] && hsv[0] < 210) colorId = 6;
        if (211 <= hsv[0] && hsv[0] < 240) colorId = 7;
        if (241 <= hsv[0] && hsv[0] < 270) colorId = 8;
        if (271 <= hsv[0] && hsv[0] < 300) colorId = 9;
        if (301 <= hsv[0] && hsv[0] < 330) colorId = 10;
        if (331 <= hsv[0] && hsv[0] <= 360) colorId = 11;
        return colorId;
    }

    public String getColorName() {
        final int colorId = this.getColorID();
        switch(colorId) {
        case 11: case 0:
            return "RED";
        case 1: case 2:
            return "YELLOW";
        case 3: case 4:
            return "GREEN";
        case 5: case 6:
            return "CYAN";
        case 7: case 8:
            return "BLUE";
        case 9: case 10:
            return "MAGENTA";
        }
        return "ERROR";
    }

    public float getBlightness() {
        float[] rgb = new float[3];
        rgb = this.getRGB();
        return 0.299F * rgb[0] + 0.587F * rgb[1] + 0.114F * rgb[2];
    }

    private final static int HSV_SIZE = 3;
    public float[] getHSV(int type) {
        float[] rgb = new float[HSV_SIZE];
        rgb = this.getRGB();
        final float min = this.minColor(rgb);
        final float max = this.maxColor(rgb);
        final float range = max - min;
        final float index = this.minIndexOfColors(rgb);
        float[] result = {0.0F, 0.0F, 0.0F};
        float hue = -1;

        if (range == 0.0F) result[0] = -1F;
        else if (index == 0) {
            hue = 60 * (rgb[2] - rgb[1]) / range + 180;
        } else if (index == 1) {
            hue = 60 * (rgb[0] - rgb[2]) / range + 300;
        } else {
            hue = 60 * (rgb[1] - rgb[0]) / range + 60;
        }

        result[0] = hue;
        result[1] = type == 0 ? range : range / max;
        result[2] = max;

        return result;
    }
}
