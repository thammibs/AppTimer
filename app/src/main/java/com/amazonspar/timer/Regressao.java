package com.amazonspar.timer;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Regressao extends CountDownTimer {
    private Context context;
    private long tempoFuturo;
    private TextView tempo;
    private long seg;

    public Regressao(Context context, TextView tempo, long tempoFuturo, long intervalo) {
        super(tempoFuturo,intervalo);
        this.tempo = tempo;
        this.context=context;
    }
    @Override
    public void onTick(long tempoRestante) {
        tempoFuturo=tempoRestante;
        tempo.setText(getCorretoTempo(true,tempoFuturo)+":"+getCorretoTempo(false,tempoFuturo));
    }
    @Override
    public void onFinish() {
        try {
            finalize();
        } catch (Throwable throwable) {
            Toast.makeText(context,"Erro ao finalizar o tempo", Toast.LENGTH_LONG).show();
        }
    }
    private String getCorretoTempo(boolean minuto,long tempoRestante){
        String modeloFormatadoTempo;
        int tempoCalendar= minuto ? Calendar.MINUTE : Calendar.SECOND;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tempoRestante);
        seg=calendar.get(tempoCalendar);
        modeloFormatadoTempo = calendar.get(tempoCalendar)<10? "0"+calendar.get(tempoCalendar): ""+calendar.get(tempoCalendar);
        return modeloFormatadoTempo;
    }
    public long getTempoFuturo() {
        return tempoFuturo;
    }
    public long getTempoRestante(){
        return seg;
    }
}

