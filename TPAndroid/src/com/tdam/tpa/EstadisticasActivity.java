package com.tdam.tpa;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class EstadisticasActivity extends Activity { 

    private static int[] COLORS = new int[] { Color.parseColor("#FF8800"), Color.parseColor("#99CC00") };
    
    private double cantidad_enviados=0;
    
    private double cantidad_recibidos=0;

    private static double[] VALUES = new double[] { 0, 0 };

    private static String[] NAME_LIST = new String[] { "Enviados", "Recibidos" };

    private CategorySeries mSeries = new CategorySeries("");

    private DefaultRenderer mRenderer = new DefaultRenderer();

    private GraphicalView mChartView;
    
    private ArrayList<MensajeWebEntidad> messages;
    
    private BDInterface db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estadisticas);

        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setChartTitle("Mensajes Web");
        mRenderer.setLegendTextSize(15);
        mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(90);

        messages = new ArrayList<MensajeWebEntidad>();
        db = UsuarioEntidad.db;
		messages = db.getMessages();

		for (MensajeWebEntidad msg : messages) {
			if (msg.getDirection()==false) {
				cantidad_enviados++;
			} else {
				cantidad_recibidos++;
			}
		}		
		VALUES = new double[] { cantidad_enviados, cantidad_recibidos };
		
        for (int i = 0; i < VALUES.length; i++) {
            mSeries.add(NAME_LIST[i] + " " + VALUES[i], VALUES[i]);
            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
            renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
            mRenderer.addSeriesRenderer(renderer);
        }

        if (mChartView != null) {
            mChartView.repaint();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
            mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);

            mChartView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();

                    if (seriesSelection == null) {
                        Toast.makeText(EstadisticasActivity.this,"Debe seleccionar un elemento del gráfico",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(EstadisticasActivity.this,"Cantidad de "+NAME_LIST[seriesSelection.getPointIndex()]+": "+ seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mChartView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    if (seriesSelection == null) {
                        Toast.makeText(EstadisticasActivity.this,"Debe seleccionar un elemento del gráfico", Toast.LENGTH_SHORT);
                        return false; 
                    }
                    else {
                        Toast.makeText(EstadisticasActivity.this,"Cantidad de "+NAME_LIST[seriesSelection.getPointIndex()]+": "+ seriesSelection.getValue(),Toast.LENGTH_SHORT);
                        return true;       
                    }
                }
            });
            layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        }
        else {
        mChartView.repaint();
        }
    }
}