package xin.softdev.kuleuven.goodnight.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import xin.softdev.kuleuven.goodnight.R;

public class ShowChartActivity extends AppCompatActivity {

    private BarChart barChart;
    private String phoneNumber;
    private RequestQueue mQueue;
    private ArrayList<BarEntry> yVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chart);

        barChart=(BarChart) findViewById(R.id.barchart);
        barChart.getDescription().setEnabled(false);

        Intent i =getIntent();
        Bundle b=i.getExtras();
        phoneNumber=b.getString("parseUser");

        yVals=new ArrayList<>();
        mQueue= Volley.newRequestQueue(this);
        this.jsonParse();
        barChart.setDrawBorders(false);
        barChart.setHighlightFullBarEnabled(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setEnabled(false);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setDrawLabels(false);
        barChart.getXAxis().setGranularity(1f);
        barChart.setTouchEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

private void jsonParse(){
    String url="https://studev.groept.be/api/a18_sd610/getDuration";
    JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try{
                        for(int i=0;i<response.length();i++) {
                            JSONObject user = response.getJSONObject(i);
                            String username = user.getString("USERNAME");
                            String duration = user.getString("DURATION");
                            if (yVals.size()<7&&username.equals(phoneNumber)) {
                                float f=Float.parseFloat(duration);
                                yVals.add(new BarEntry(yVals.size(),f));
                            }
                            else if(yVals.size()==7)
                            {
                                BarDataSet set=new BarDataSet(yVals,"");
                                set.setColors(ColorTemplate.LIBERTY_COLORS);
                                set.setDrawValues(true);
                                set.setBarBorderColor(Color.WHITE);
                                set.setValueTextColor(Color.WHITE);
                                set.setValueTextSize(16f);
                                BarData data = new BarData(set);
                                barChart.setData(data);
                                barChart.invalidate();
                                barChart.animateY(500);
                                break;
                            }

                            if(i==response.length()-1)
                            {
                                BarDataSet set=new BarDataSet(yVals,"");
                                set.setColors(ColorTemplate.LIBERTY_COLORS);
                                set.setDrawValues(true);
                                set.setBarBorderColor(Color.WHITE);
                                set.setValueTextColor(Color.WHITE);
                                set.setValueTextSize(16f);
                                BarData data = new BarData(set);
                                barChart.setData(data);
                                barChart.invalidate();
                                barChart.animateY(500);
                                break;
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    });
    mQueue.add(request);
}
}
