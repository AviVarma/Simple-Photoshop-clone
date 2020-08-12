/*
CS-255 Getting started code for the assignment
I do not give you permission to post this code online
Do not post your solution online
Do not copy code
Do not use JavaFX functions or other libraries to do the main parts of the assignment:
	Gamma Correction
	Contrast Stretching
	Histogram calculation and equalisation
	Cross correlation
All of those functions must be written by yourself
You may use libraries to achieve a better GUI
Student number: 957552
Student Name: Avi varma
I declare that the work presented below is my own work.
*/
import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;  
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Photoshop extends Application {
	
	double xAxisLoc1 = 50.0;
	double yAxisLoc1 = 20.0;
	double xAxisLoc2 = 200.0;
	double yAxisLoc2 = 225.0;

    @Override
    /*
     * Creates a pane where you can display the image and interact with the image.
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    public void start(Stage stage) throws FileNotFoundException {
		stage.setTitle("Photoshop");
		Image image = new Image(new FileInputStream("raytrace.jpg"));  
		ImageView imageView = new ImageView(image); 
		Button invert_button = new Button("Invert");
		Button gamma_button = new Button("Gamma Correct");
		Button contrast_button = new Button("Contrast Stretching");
		Button histogram_button = new Button("Histograms");
		Button cc_button = new Button("Cross Correlation");
		TextField gamma_input = new TextField();
		gamma_input.setVisible(true);
		gamma_input.setPromptText("Gamma value");
		
		
		final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        
        xAxis.setUpperBound(255);
        yAxis.setUpperBound(255);
        
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        
        lineChart.setAnimated(false);
        lineChart.setLegendVisible(false);
        lineChart.setTitle("Contrast Streaching");
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        
        final double MINIMUM = 0.0;
        final double MAXIMUM = 255.0;
    	Data<Number,Number> node_1 = new XYChart.Data<>(50,20);
    	Data<Number,Number> node_2 = new XYChart.Data<>(200,225);
    
    	series.getData().add(new XYChart.Data<>(MINIMUM, MINIMUM));
    	series.getData().add(node_1);
    	series.getData().add(node_2);
    	series.getData().add(new XYChart.Data<>(MAXIMUM, MAXIMUM));
    	lineChart.getData().add(series);
    	
        Node node1 = node_1.getNode();
        node1.setCursor(Cursor.HAND);
    	node1.setOnMouseDragged(e -> {
    		Point2D pointInScene = new Point2D(e.getSceneX(), e.getSceneY());
            xAxisLoc1 = xAxis.sceneToLocal(pointInScene).getX();
            yAxisLoc1 = yAxis.sceneToLocal(pointInScene).getY();
            Number x1 = xAxis.getValueForDisplay(xAxisLoc1);
            Number y1 = yAxis.getValueForDisplay(yAxisLoc1);
            node_1.setXValue(x1);
            node_1.setYValue(y1);
    	});
    	
        Node node2 = node_2.getNode();
        node2.setCursor(Cursor.HAND);
    	node2.setOnMouseDragged(e -> {
    		Point2D pointInScene = new Point2D(e.getSceneX(), e.getSceneY());
            xAxisLoc2 = xAxis.sceneToLocal(pointInScene).getX();
            yAxisLoc2 = yAxis.sceneToLocal(pointInScene).getY();
            Number x2 = xAxis.getValueForDisplay(xAxisLoc2);
            Number y2 = yAxis.getValueForDisplay(yAxisLoc2);
            node_2.setXValue(x2);
            node_2.setYValue(y2);
    	});
    	
		invert_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Invert");
				Image inverted_image=ImageInverter(imageView.getImage());
				imageView.setImage(inverted_image);
            }
        });

		gamma_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	double val = Double.parseDouble(gamma_input.getText());
            	
                System.out.println("Gamma Correction");
                Image gamma_correction = gammaCorrector(imageView.getImage(), val);
                imageView.setImage(gamma_correction);
            }
        });
		
		contrast_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Contrast Stretching");
                Image gamma_correction = contrastStreacher(imageView.getImage(), (int) xAxisLoc1, (int) yAxisLoc1, (int) xAxisLoc2, (int) yAxisLoc2);
                imageView.setImage(gamma_correction);
            }
        });
		
		
		histogram_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	System.out.println("Histogram");
            	Image grey_Image = histogramStage(imageView.getImage());
            	imageView.setImage(grey_Image);
                
            }
        });
		
		cc_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Cross Correlation");
                Image ccImage = crossCorrelation(imageView.getImage());
                imageView.setImage(ccImage);
            }
        });
		
		FlowPane root = new FlowPane();
		root.setVgap(10);
        root.setHgap(5);

		root.getChildren().addAll(invert_button, gamma_button, gamma_input, contrast_button, histogram_button, cc_button, imageView, lineChart);

        Scene scene = new Scene(root, 1024, 768);
        stage.setScene(scene);
        stage.show();
    }
    
    
    /**
     * New Scene for all the Histograms an image equalisation where the image channels are averaged out
     * to get the grey-scale image.
     * @param input_Image
     */
	public Image histogramStage(Image input_Image) {
		// Basic GUI setup
		Stage stage = new Stage();
		Button red_Button = new Button("Red");
		Button green_Button = new Button("Green");
		Button blue_Button = new Button("Blue");
		Button greyScale_Button = new Button("Grey Scale");
		Button equalize_Button = new Button("Equalize image");
		Button remove_Button = new Button("Clear Chart");
		
		stage.setTitle("Histogram");
		
        final NumberAxis Xaxis = new NumberAxis(0,256,100);
        final NumberAxis Yaxis = new NumberAxis(0,256,100);
		
		final NumberAxis xAxis = new NumberAxis(0,256,100);
        final NumberAxis yAxis = new NumberAxis(0,256,100);
        
        Xaxis.setAutoRanging(false);
        Yaxis.setAutoRanging(true);
        
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(true);
        
        final LineChart<Number,Number> histoChart = new LineChart<Number,Number>(xAxis,yAxis);
        final LineChart<Number, Number> equalizationChart = new LineChart<Number,Number>(Xaxis,Yaxis);
        
        final XYChart.Series<Number, Number> redSeries = new XYChart.Series<>();
        final XYChart.Series<Number, Number> greenSeries = new XYChart.Series<>();
        final XYChart.Series<Number, Number> blueSeries = new XYChart.Series<>();
        final XYChart.Series<Number, Number> greySeries = new XYChart.Series<>();
        final XYChart.Series<Number, Number> cumulativeSeries = new XYChart.Series<>();
		
        histoChart.setTitle("R-G-B Histogram");
        histoChart.setCreateSymbols(false);
        histoChart.setLegendVisible(false);
        histoChart.setAnimated(false);
        
        equalizationChart.setCreateSymbols(false);
        equalizationChart.setAnimated(false);
        equalizationChart.setLegendVisible(false);
        equalizationChart.setTitle("Histogram Equalization");
        
		
		int[][] histogram = new int[256][4];
		double[] cumulativeArray = new double[256];
		double[] mapping = new double[256];
		
		final int i1 = 0;
		final int i2 = 255;
		
		final int MAX_PIXEL_VAL = 255;
		double h = input_Image.getHeight();
		double w = input_Image.getWidth();
		double red;
		double green;
		double blue;
		double grey;
		double size = h*w;
		
		PixelReader image_reader = input_Image.getPixelReader();
		
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				Color color = image_reader.getColor(x, y);
				red = color.getRed()*MAX_PIXEL_VAL;
				green = color.getGreen()*MAX_PIXEL_VAL;
				blue = color.getBlue()*MAX_PIXEL_VAL;
				grey = (red+green+blue)/3;
				histogram[(int) red][0]++;
				histogram[(int) green][1]++;
				histogram[(int) blue][2]++;
				histogram[(int) grey][3]++;
			}
		}
		
		for(int i = 0; i < cumulativeArray.length; i++) {
			if(i == 0) {
				cumulativeArray[i] = histogram[0][3];
			}
			else if(i == 1) {
				cumulativeArray[i] = cumulativeArray[0] + histogram[1][3]; 
			} else {
				cumulativeArray[i] = cumulativeArray[i-1] + histogram[i][3];
			}
			System.out.println(cumulativeArray[i]);
		}
		
		for(int j = 0; j < mapping.length; j++) {
			mapping[j]=(((i2-i1)*(cumulativeArray[j]/size))+i1);
		}

		int width = (int)input_Image.getWidth();
        int height = (int)input_Image.getHeight();
        
        
		red_Button.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				System.out.println("red_Button");
				redSeries.getData().clear();
				for(int i=0; i< histogram.length; i++) {
					redSeries.getData().add(new XYChart.Data<>(i,histogram[i][0]));
				}
				histoChart.getData().add(redSeries);
			}
		});
		
		green_Button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("green_button");
				greenSeries.getData().clear();
				for(int i=0; i<histogram.length; i++) {
					greenSeries.getData().add(new XYChart.Data<>(i,histogram[i][1]));
				}
				histoChart.getData().add(greenSeries);
			}

		});
		
		blue_Button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("blue_Button");
				blueSeries.getData().clear();
				for(int i=0; i<histogram.length; i++) {
					blueSeries.getData().add(new XYChart.Data<>(i,histogram[i][2]));
				}
				histoChart.getData().add(blueSeries);
			}

		});
		
		// this is the brightness of the histogram
		greyScale_Button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("greyScale_Button");
				greySeries.getData().clear();
				for(int i=0; i<histogram.length; i++) {
					greySeries.getData().add(new XYChart.Data<>(i,histogram[i][3]));
				}
				histoChart.getData().add(greySeries);
				
				for(int i=0; i<histogram.length; i++) {
					cumulativeSeries.getData().add(new XYChart.Data<>(i,cumulativeArray[i]));
				}
				equalizationChart.getData().add(cumulativeSeries);
			}

		});
		
		remove_Button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				histoChart.getData().removeAll(redSeries);
				histoChart.getData().removeAll(greenSeries);
				histoChart.getData().removeAll(blueSeries);
				histoChart.getData().removeAll(greySeries);
			}
		});
		
		ImageView imageView = new ImageView(input_Image);
		WritableImage grey_image = new WritableImage(width, height);
		PixelWriter grey_image_writer = grey_image.getPixelWriter();
		
		equalize_Button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				
				for(int y = 0; y < height; y++) {
					for(int x = 0; x < width; x++) {
						Color color = image_reader.getColor(x, y);
						double greyVal = mapping[(int) (((color.getRed()+color.getGreen()+color.getBlue())/3.0)*255.0)]/255.0;
						color = Color.color(greyVal, greyVal, greyVal);
						grey_image_writer.setColor(x, y, color);
						imageView.setImage(grey_image);
					}
				}
			}
		});
		
		
		
				FlowPane pane = new FlowPane();
				pane.setVgap(10);
		        pane.setHgap(5);
				pane.getChildren().addAll(red_Button, green_Button, blue_Button, greyScale_Button, remove_Button, equalize_Button, histoChart, equalizationChart);
		        Scene scene = new Scene(pane, 1024, 768);
		        stage.setScene(scene);
		        stage.show();
		        return grey_image;
	}

	
	
	
	
	public Image ImageInverter(Image image) {

		int width = (int)image.getWidth();
        int height = (int)image.getHeight();

		WritableImage inverted_image = new WritableImage(width, height);

		PixelWriter inverted_image_writer = inverted_image.getPixelWriter();

		PixelReader image_reader=image.getPixelReader();
		

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);
				color=Color.color(1.0-color.getRed(), 1.0-color.getGreen(), 1.0-color.getBlue());
				inverted_image_writer.setColor(x, y, color);
			}
		}
		return inverted_image;
	}
	

	public Image gammaCorrector(Image image, double gammaVal) {

		int width = (int)image.getWidth();
        int height = (int)image.getHeight();

		WritableImage gamma_corrected_image = new WritableImage(width, height);

		PixelWriter gamma_corrected_image_writer = gamma_corrected_image.getPixelWriter();

		PixelReader image_reader=image.getPixelReader();
		
		double[] table = getIndex(gammaVal);
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) { 
				
				Color color = image_reader.getColor(x, y);
				double red = table[(int) (color.getRed()*255)];
				double green = table[(int) (color.getGreen()*255)];
				double blue = table[(int) (color.getBlue()*255)];
				color=Color.color(red, green, blue);
				gamma_corrected_image_writer.setColor(x, y, color);
			}
		}
		return gamma_corrected_image;
	}
	
	public double[] getIndex(double gamma) 
	{
		// 1D array
		double[] lookup = new double[256];
		
		for(int i = 0; i < lookup.length; i++) 
		{
			 lookup[i] = ((Math.pow( i / 255.0 , 1/gamma)));
			 System.out.println(lookup[i]);
		}
		
		return lookup;
	}
	
	public double contrastStreachFunction(int r1, int s1, int r2, int s2, double colorVal) {
		
		final int MAX_VAL = 255;
		
		if(colorVal < r1) {
			return ((s1/r1)*colorVal)/MAX_VAL;
		}
		else if(r1 <= colorVal && colorVal <= r2) {
			return ((((s2-s1)/(r2-r1))*(colorVal - r1)) + s1)/MAX_VAL;
		}
		else {
			return ((((MAX_VAL-s2)/(MAX_VAL-r2))*(colorVal - r2)) + s2)/MAX_VAL;
		}
	}
	
	public Image contrastStreacher(Image image, int r1, int s1, int r2, int s2) {
		int width = (int)image.getWidth();
        int height = (int)image.getHeight();
		WritableImage contrast_streached_image = new WritableImage(width, height);
		PixelWriter contrast_streached_image_writer = contrast_streached_image.getPixelWriter();
		PixelReader image_reader=image.getPixelReader();
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) { 
				
				Color color = image_reader.getColor(x, y);
				double red = contrastStreachFunction(r1, s1, r2, s2,color.getRed()*255);
				double green = contrastStreachFunction(r1, s1, r2, s2,color.getGreen()*255);
				double blue = contrastStreachFunction(r1, s1, r2, s2,color.getBlue()*255);
				color=Color.color(red, green, blue);
				contrast_streached_image_writer.setColor(x, y, color);
			}
		}
		return contrast_streached_image;
	}
	
	int [][] filter =    {{-4,-1, 0,-1,-4},
					   	  {-1, 2, 3, 2,-1},
		 			      { 0, 3, 4, 3, 0},
		 			      {-1, 2, 3, 2,-1},
		 			      {-4,-1, 0,-1,-4}};
	
	public Image crossCorrelation (Image image) {
        int offset = 2;
        double maxVal = 0.0;
        double minVal = 0.0;
		
		int width = (int)image.getWidth();
        int height = (int)image.getHeight();
        
        final int filterSize = 5;
        
        WritableImage filtered_Image = new WritableImage(width, height);
        
        PixelWriter image_writer = filtered_Image.getPixelWriter();
        PixelReader image_reader=image.getPixelReader();
        
        int[][][] sum = new int[3][height][width];
        
		for(int y = offset; y < height-offset; y++) {
			for(int x = offset; x < width-offset; x++) {	
				
				double rSum = 0.0;
				double gSum = 0.0;
				double bSum = 0.0;

				for(int h = 0; h < filterSize; h++) {
					for(int w = 0; w < filterSize; w++) {
						
						int tempX = x;
						int tempY = y;
						
						Color tempColor = image_reader.getColor(tempX+w-offset, tempY+h-offset);
						
						rSum = (rSum + (filter[h][w] * (tempColor.getRed()*255)));
						gSum = (gSum + (filter[h][w] * (tempColor.getGreen()*255)));
						bSum = (bSum + (filter[h][w] * (tempColor.getBlue()*255)));
						
						tempX++;
						tempY++;
					}
				}
				sum[0][y][x] = (int) rSum;
				sum[1][y][x] = (int) gSum;
				sum[2][y][x] = (int) bSum;
				
				if(rSum >= maxVal) {
					maxVal = rSum;
				}
				else if(gSum >= maxVal) {
					maxVal = gSum;
				}
				else if(bSum >= maxVal){
					maxVal = bSum;
				}
				
				if (rSum <= minVal) {
					minVal = rSum;
				}
				else if (gSum <= minVal) {
					minVal = gSum;
				}
				else if(bSum <= minVal){
					minVal = bSum;
				}
			}
		}
		
		for(int y = offset; y < height-offset; y++) {
			for(int x = offset; x < width-offset; x++) {
				sum[0][y][x] = (int) (((sum[0][y][x]-minVal)*255.0)/(maxVal-minVal));
				sum[1][y][x] = (int) (((sum[1][y][x]-minVal)*255.0)/(maxVal-minVal));
				sum[2][y][x] = (int) (((sum[2][y][x]-minVal)*255.0)/(maxVal-minVal));
				
				double red = sum[0][y][x]/255.0;
				double green = sum[1][y][x]/255.0;
				double blue = sum[2][y][x]/255.0;
				
				Color color = image_reader.getColor(x, y);
				color = Color.color(red, green, blue);
				image_writer.setColor(x, y, color);
				
			}
		}
		return filtered_Image;
	}
	
    public static void main(String[] args) {
        launch();
    }

}