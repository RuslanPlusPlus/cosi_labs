package com.rusned.laba1.app;

import com.rusned.laba1.util.AppFileNameFilter;
import com.rusned.laba1.util.ImageManager;
import com.rusned.laba1.util.PreparationType;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class App {

    private static final String CURRENT_DIRECTORY = System.getProperty("user.dir");
    private static final Scanner in = new Scanner(System.in);
    private final ImageManager imageManager = new ImageManager();
    private final AppFileNameFilter fileNameFilter = new AppFileNameFilter(".jpg");

    private static final ImageView[] resultImageView = new ImageView[3];
    private static final BufferedImage[] resultImage = new BufferedImage[4];
    private static ImageView originalImageView;

    private static int OPERATION_COUNTER = 0;
    private static File imageFile;

    public void launch(){
        menu();
        createWindow();
    }

    private void createWindow() {
        try {
            HBox hBoxOrig = new HBox(createHistogram(ImageIO.read(imageFile)));
            HBox[] hBoxesImage = new HBox[3];
            for (int i = 0; i < hBoxesImage.length; i++)
                hBoxesImage[i] = new HBox(createHistogram(resultImage[i]));
            VBox mainBox = new VBox(hBoxOrig, hBoxesImage[0], hBoxesImage[1], hBoxesImage[2]);

            ScrollPane scrollPane = new ScrollPane(mainBox);

            Scene scene = new Scene(scrollPane, 1820, 920);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Image processing");
            stage.setFullScreen(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BarChart createHistogram(BufferedImage resImage) {
        WritableRaster raster = resImage.getRaster();
        int[] percent = new int[256];
        for (int i = 0; i < raster.getWidth(); i++) {
            for (int j = 0; j < raster.getHeight(); j++) {
                int[] pixel = raster.getPixel(i, j, new int[4]);
                float luminance = pixel[0] * 0.2126f + pixel[1] * 0.7152f + pixel[2] * 0.0722f;

                percent[(int) luminance]++;
            }
        }

        CategoryAxis x = new CategoryAxis();
        x.setLabel("Brightness");
        NumberAxis y = new NumberAxis();
        y.setLabel("Amount");
        BarChart barChart = new BarChart(x, y);
        XYChart.Series ds = new XYChart.Series();
        for (int i = 0; i < percent.length; i++) {
            ds.getData().add(new XYChart.Data(i + "", percent[i]));
        }
        barChart.getData().add(ds);
        return barChart;
    }

    private void menu() {

        int fileCounter = 1;
        String imageName = null;
        Map<Integer, String> dialogMap = new HashMap<>();
        File imageDirectory = new File(CURRENT_DIRECTORY);
        File[] listFiles = imageDirectory.listFiles(fileNameFilter);
        System.out.println("Choose image");
        if (Objects.nonNull(listFiles)){
            for(File f : listFiles) {
                System.out.println(fileCounter + " - " + f.getName());
                dialogMap.put(fileCounter++, f.getName());
            }
        }
        do{
            int choice;
            try{
                choice = Integer.parseInt(in.nextLine());
                imageName = dialogMap.get(choice);
                if (Objects.nonNull(imageName)){
                    imageFile = new File(imageName);
                }
            }catch (NumberFormatException e){
                System.out.println("Not valid input");
            }
        } while (!openImage());

        int computationParam;
        System.out.print("Enter preparation limit  (0 - 255)\n >");
        computationParam = in.nextInt();
        imageManager.compute(resultImage[0], PreparationType.BINARIZATION, computationParam);
        System.out.print("Enter luma slice interval (0 - 255)\n >");
        computationParam = in.nextInt();
        imageManager.compute(resultImage[1], PreparationType.LUMA_SLICE, computationParam);
        imageManager.filter(resultImage[2], resultImage[3]);

        try {
            originalImageView = new ImageView(new Image(new FileInputStream(imageName)));
            for (int i = 0; i < resultImageView.length; i++) {
                resultImageView[i] = new ImageView(new Image(new FileInputStream(saveImage(resultImage[i]))));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean openImage() {
        boolean result = false;
        if (Objects.nonNull(imageFile)){
            try {
                for (int i = 0; i < resultImage.length; i++)
                    resultImage[i] = ImageIO.read(imageFile);
                result = true;
            } catch (IOException e) {
                System.out.print("Not correct file name, try again\n >>> ");
            }
        }
        return result;
    }

    private static String saveImage(BufferedImage image) {
        String resPath = imageFile.getName().split("\\.")[0] + "_result_"
                + (++OPERATION_COUNTER) + "." + imageFile.getName().split("\\.")[1];
        try {
            ImageIO.write(image, imageFile.getName().split("\\.")[1], new File(resPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resPath;
    }
}

