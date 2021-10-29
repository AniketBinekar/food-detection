package com.example.fooddetection.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddetection.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private BarChart barChart;
    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputimageBuffer;
    private int imagesizeX;
    private int imagesizeY;
    private TextView dishNameTV;
    private TensorBuffer outputimageBuffer;
    private TensorProcessor processorBuffer;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROABABILITY_MEAN = 0.0f;
    private static final float PROABABILITY_STD = 255.0f;
    private Bitmap bitmap;
    private List<String> labels;
    ImageView imageView;
    Uri imageUri;
    Button classifyBtn,out;
    TextView prediction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        classifyBtn = findViewById(R.id.captureBtn2);
        prediction = findViewById(R.id.prdictTv);
        dishNameTV = findViewById(R.id.idTVDishName);
        out=findViewById(R.id.captureBtn3);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ans=new Intent(MainActivity.this,DailyView.class);
                startActivity(ans);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 12);
            }
        });

        //define Interpreter for tfLite
        try {
            tflite = new Interpreter(loadModelFile(this));
            Log.e("TAG", "TFLIT INITIALIZED");
        } catch (IOException e) {
            Log.e("TAG", "ERROR IS " + e.getMessage());
            e.printStackTrace();
        }

        //Classify Button Onclick

        classifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imageTensorindx = 0;
                int[] imageShape = tflite.getInputTensor(imageTensorindx).shape();
                imagesizeX = imageShape[1];
                imagesizeY = imageShape[2];
                DataType imageDataType = tflite.getInputTensor(imageTensorindx).dataType();

                int probabilitiyTensorIndex = 0;
                int[] probabilityShape = tflite.getOutputTensor(probabilitiyTensorIndex).shape();
                DataType probabilityDataType = tflite.getInputTensor(probabilitiyTensorIndex).dataType();

                inputimageBuffer = new TensorImage(imageDataType);
                outputimageBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                processorBuffer = new TensorProcessor.Builder().add(getPostProcessorNormalizeOp()).build();

                inputimageBuffer = loadImage(bitmap);
                tflite.run(inputimageBuffer.getBuffer(), outputimageBuffer.getBuffer().rewind());
                showResult();
//                loadPieChartData();
            }
        });

//
    }

    //Load Image
    private TensorImage loadImage(final Bitmap bitmap) {
        inputimageBuffer.load(bitmap);

        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                .add(new ResizeOp(imagesizeX, imagesizeX, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(getPreProcessorNormalizeOp()).build();
        return imageProcessor.process(inputimageBuffer);
    }

    //Load TfLiteModel
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("model_unquant.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declareLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength);
    }

    //normalize image
    private TensorOperator getPreProcessorNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }

    private TensorOperator getPostProcessorNormalizeOp() {
        return new NormalizeOp(PROABABILITY_MEAN, PROABABILITY_STD);
    }

//    private void setupPieChart(){
//        pieChart.setDrawHoleEnabled(true);
//        pieChart.setUsePercentValues(true);
//        pieChart.setEntryLabelTextSize(12);
//        pieChart.setEntryLabelColor(Color.BLACK);
//        pieChart.setCenterText("Nutrients by Category");
//        pieChart.setCenterTextSize(25);
//        pieChart.getDescription().setEnabled(false);
//
//        Legend legend = pieChart.getLegend();
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
//        legend.setDrawInside(false);
//        legend.setEnabled(true);
//    }
//
//    private void loadPieChartData(){
//        ArrayList<PieEntry> entries = new ArrayList<>();
//        entries.add(new PieEntry(0.2f,"Fats"));
//        entries.add(new PieEntry(0.15f,"Carbohydrates"));
//        entries.add(new PieEntry(0.10f,"Proteins"));
//        entries.add(new PieEntry(0.25f,"Vitamins"));
//        entries.add(new PieEntry(0.3f,"Colestrol"));
//
//
//        ArrayList<Integer> colors = new ArrayList<>();
//        for(int color: ColorTemplate.MATERIAL_COLORS){
//            colors.add(color);
//        }
//        for(int color: ColorTemplate.VORDIPLOM_COLORS){
//            colors.add(color);
//        }
//
//        PieDataSet pieDataSet = new PieDataSet(entries,"Nutrients");
//        pieDataSet.setColors(colors);
//
//        PieData pieData = new PieData(pieDataSet);
//        pieData.setDrawValues(true);
//        pieData.setValueFormatter(new PercentFormatter(pieChart));
//        pieData.setValueTextSize(12f);
//        pieData.setValueTextColor(Color.BLACK);
//
//        pieChart.setData(pieData);
//        pieChart.invalidate();
//        pieChart.animateY(1400, Easing.EaseInOutQuad);
//
//    }

    private void showResult() {
        try {
            labels = FileUtil.loadLabels(MainActivity.this, "food_recognition.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Float> labelsProbability = new TensorLabel(labels, processorBuffer.process(outputimageBuffer))
                .getMapWithFloatValue();
        float maxValinMap = (Collections.max(labelsProbability.values()));
        Log.e("TAG","LABEL PRIORITY IS "+labelsProbability+"\n\n"+"MAX VALUE IS "+maxValinMap);

        for (Map.Entry<String, Float> entry : labelsProbability.entrySet()) {

            if(entry.getValue()==maxValinMap){
                dishNameTV.setText(entry.getKey());
            }
        }

//            String[] label = labelsProbability.keySet().toArray(new String[0]);
//            Float[] label_probability = labelsProbability.values().toArray(new Float[0]);
//
//            barChart = findViewById(R.id.bar_chart);
//            barChart.getXAxis().setDrawGridLines(true);
//            barChart.getAxisLeft().setDrawGridLines(true);
//
//            ArrayList<BarEntry> entries = new ArrayList<>();
//            ArrayList<Float> priority = new ArrayList<>();
//            for (int i = 0; i < label_probability.length; i++) {
//                priority.add(i, label_probability[i] * 100);
//                entries.add(new BarEntry(i, label_probability[i] * 100));
//            }
//
//            ArrayList<String> xAxisName = new ArrayList<>();
//            for (int i = 0; i < label.length; i++) {
//                xAxisName.add(label[i]);
//            }
//            barChart(barChart, entries, xAxisName);
//            printDishes(xAxisName, priority);
//
//            prediction.setText("Prediction");
    }

//    private void printDishes(ArrayList<String> dishName, ArrayList<Float> percentage) {
//        ArrayList<Integer> priorityList = new ArrayList<>();
//        for (int i = 0; i < percentage.size(); i++) {
//            priorityList.add(Math.round(percentage.get(i)));
//            Log.e("TAG","DISH NAME IS "+dishName.get(i)+"   priority is "+(percentage.get(i))+"\n");
//        }
//        //Log.e("TAG")
//        Integer maxInt = Collections.max(priorityList);
//        int maxIndex = priorityList.indexOf(maxInt);
//        dishNameTV.setText(dishName.get(maxIndex).substring(2));
//    }

    public static void barChart(BarChart barChart, ArrayList<BarEntry> arrayList, final ArrayList<String> xAxisValues) {
        barChart.setDrawBarShadow(false);
        barChart.setFitBars(true);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(25);
        barChart.setPinchZoom(true);
        barChart.setDrawGridBackground(true);

        BarDataSet barDataSet = new BarDataSet(arrayList, "Class");
        barDataSet.setColors(new int[]{Color.parseColor("#03A9F4"), Color.parseColor("#FF9800"),
                Color.parseColor("#76FF03"), Color.parseColor("#E91E63"), Color.parseColor("#2962FF")});

        BarData barData = new BarData(barDataSet);
        Log.e("TAG","BAR DATA SET IS "+barDataSet.toString());
        Log.e("TAG","X AXIS VALUE IS "+xAxisValues);
        Log.e("TAG","Y AXIS VALUE IS "+arrayList);

        barData.setBarWidth(0.9f);
        barData.setValueTextSize(0f);

        barChart.setBackgroundColor(Color.WHITE);
        barChart.setDrawGridBackground(false);
        barChart.animateY(2000);


        //X axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(13f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setDrawGridLines(false);
        barChart.setData(barData);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}