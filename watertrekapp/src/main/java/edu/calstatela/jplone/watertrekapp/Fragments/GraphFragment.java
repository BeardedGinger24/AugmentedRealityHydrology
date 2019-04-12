package edu.calstatela.jplone.watertrekapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.calstatela.jplone.watertrekapp.R;

public class GraphFragment extends Fragment {
    View gView;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    // xValue ArrayList stores the values of the x-axis in the graph.
    public ArrayList<String> xValue = new ArrayList<>();
    // yValue ArrayList stores the values of the y-axis in the graph.
    public ArrayList<String> yValue = new ArrayList<>();
    // dateList ArrayList is used to create a List of Dates from xValue;
    public ArrayList<Date> dateList = new ArrayList<>();

    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    private ArrayList<String> dbgsUList = new ArrayList<>();
    private ArrayList<String> resStorageList = new ArrayList<>();
    private ArrayList<String> dischargeList = new ArrayList<>();
    private ArrayList<String> soilDepthList = new ArrayList<>();
    private ArrayList<String> sweSnotelList = new ArrayList<>();

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gView = inflater.inflate(R.layout.graph_view_fragment, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            dbgsUList = bundle.getStringArrayList("dbgs");
            ArrayList<String> dbgs = bundle.getStringArrayList("dbgs");
//            String one = dbgs.get(0);
//            String two = dbgs.get(1);
//            Toast.makeText(getContext(),"TESTING!",Toast.LENGTH_SHORT).show();
//            Log.d("graphfrag", one);
//            Log.d("graphfrag", two);
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            // Generate Dates.
            // Calender.getInstance() sets the calender to current date and time.
            calendar = Calendar.getInstance();
            // calender.getTime() returns a Date object representing the calenders.
            Date d1 = calendar.getTime();
            // calender.add() takes in two arugments which consist of a Calender value. Calender.DATE for the day of the month. The second argument being an integer have that will add a value to that specified date/time.
            calendar.add(Calendar.DATE, 1);
            Date d2 = calendar.getTime();
            calendar.add(Calendar.DATE, 1);
            Date d3 = calendar.getTime();
            calendar.add(Calendar.DATE, 1);
            Date d4 = calendar.getTime();

            graph = (GraphView) gView.findViewById(R.id.graph);

            // enable scaling and scrolling
            graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
            // Fixes issue where more than three digits would be cutoff on the y axis.
            graph.getGridLabelRenderer().setPadding(40);
            // Sets angle for label on x axis.
            graph.getGridLabelRenderer().setHorizontalLabelsAngle(110);

            DataPoint[] dp = new DataPoint[]{new DataPoint(d1, 5), new DataPoint(d2, 10), new DataPoint(d3, 15), new DataPoint(d4, 5)};
            series = new LineGraphSeries<>(dp);
            // Sets markers on the line graph.
            series.setDrawDataPoints(true);
            // add a new series to the graph

            // set title
            // Make Case Statement to set Graph Title  depending on Selected POI
            graph.setTitle("Units vs. Time (MM-DD-YY)");
            graph.addSeries(series);



        }


        // Inflate the layout for this fragment
        return gView;
    }
    //Method to find max value of an arraylist of strings
    private double findMaxDouble(ArrayList<String> arrayList) {
        double maxValue = 0;
        try {
            maxValue = Double.parseDouble(arrayList.get(0));
        } catch (Exception e) {
            Log.i("Out of bounds", String.valueOf(maxValue));
        }
        for (int i = 1; i < arrayList.size(); i++) {

            double currentValue = 0;
            try {
                currentValue = Double.parseDouble(arrayList.get(i));
                if (maxValue < currentValue) {
                    maxValue = currentValue;
                }

            } catch (Exception e) {
                Log.i("Null value detected.", "");
            }

        }
        return maxValue;
    }

    //Method to find min value of an arraylist of strings
    private double findMinDouble(ArrayList<String> arrayList) {
        double minValue = 0;
        try {
            minValue = Double.parseDouble(arrayList.get(0));
        } catch (Exception e) {
            Log.i("Out of bounds", String.valueOf(minValue));
        }
        for (int i = 1; i < arrayList.size(); i++) {
            double currentValue = 0;
            try {
                currentValue = Double.parseDouble(arrayList.get(i));
                if (minValue > currentValue) {
                    minValue = currentValue;
                }

            } catch (Exception e) {
                Log.i("Null value detected.", "");
            }
        }
        return minValue;
    }

    private boolean allValuesNull(ArrayList<String> values) {
        int counter = 0;
        for (String value : values) {
            if (value == null) {
                counter++;
            }
        }
        if (counter == values.size())
            return true;

        return false;
    }

    private void mergeSortDates(DataPoint[] dataPoints, int n) {
        if (n < 2) {
            return;
        }
        int mid = n / 2;
        DataPoint[] l = new DataPoint[mid];
        DataPoint[] r = new DataPoint[n - mid];

        for (int i = 0; i < mid; i++) {
            l[i] = dataPoints[i];
        }

        for (int i = mid; i < n; i++) {
            r[i - mid] = dataPoints[i];
        }

        mergeSortDates(l, mid);
        mergeSortDates(r, n - mid);

        merge(dataPoints, l, r, mid, n - mid);
    }

    private void merge(DataPoint[] dataPoints, DataPoint[] l, DataPoint[] r, int left, int right) {
        int i = 0, j = 0, k = 0;
        while (i < left && j < right) {
            if (l[i].getX() <= r[j].getX()) {
                dataPoints[k++] = l[i++];
            } else {
                dataPoints[k++] = r[j++];
            }
        }

        while (i < left) {
            dataPoints[k++] = l[i++];
        }

        while (j < right) {
            dataPoints[k++] = r[j++];
        }
    }



    // The big graph function.
    private void populateGraph(ArrayList<String> xValue, ArrayList<String> yValue) {

        Date earliestDate = new Date();
        Date latestDate = new Date();
        for (int i = 0; i < xValue.size(); i++) {
            Date date = new Date();
            try {
                date = simpleDateFormat.parse(simpleDateFormat.format(simpleDateFormat.parse(xValue.get(i))));
                Log.d("Date Format", date + "");
                dateList.add(date);

            } catch (ParseException e) {
            }
        }

        DataPoint[] dataPoints = new DataPoint[xValue.size()];
        DataPoint dataPoint = null;
        String isNull = "null";
        for (int i = 0; i < dateList.size(); i++) {
            try {
                if (yValue.get(i) != null && dateList.get(i) != null) {
                    dataPoint = new DataPoint(dateList.get(i), Double.parseDouble(yValue.get(i)));
                    dataPoints[i] = dataPoint;
                }
            } catch (Exception e) {
                dataPoints[i] = dataPoint;
            }
        }


        for (int i = 0; i < dataPoints.length; i++) {
            try {
                Log.i("Datapoint", String.valueOf(dataPoints[i].getX()) + "," + String.valueOf(dataPoints[i].getY()));
            } catch (Exception e) {
                Log.i("nullPointException", "");
            }
        }

        mergeSortDates(dataPoints, dataPoints.length);
        for (int i = 0; i < dataPoints.length; i++) {
            try {

                Log.i("dp", simpleDateFormat.format(new Date((long) dataPoints[i].getX())) + "," + dataPoints[i].getY());
            } catch (Exception e) {
                Log.i("Dp", "null");
            }
        }
//        Log.i("dp", "Min/Max X:" + dataPoints[0].getX() + "," + dataPoints[dataPoints.length - 1].getX());
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(10);
        graph.getGridLabelRenderer().setNumVerticalLabels(10);
        //Set Min and Max for x-axis values
        try {
            graph.getViewport().setMinX(dataPoints[0].getX());
            graph.getViewport().setMaxX(dataPoints[dataPoints.length - 1].getX());
        } catch (Exception e) {
            Log.i("Datapoint[0]", "DNE.");
        }
        graph.getViewport().setXAxisBoundsManual(true);

        //Set Min and Max for y-axis values
        double maxY = findMaxDouble(yValue);
        double minY = findMinDouble(yValue);
        Log.i("dp", "Min/Max Y:" + minY + "," + maxY);
        graph.getViewport().setMinY(minY);
        graph.getViewport().setMaxY(maxY);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getGridLabelRenderer().setLabelsSpace(10);
        graph.getGridLabelRenderer().setPadding(100);
        series.resetData(dataPoints);
    }


}