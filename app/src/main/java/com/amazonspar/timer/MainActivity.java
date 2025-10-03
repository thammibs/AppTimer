package com.amazonspar.timer;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tracoPalavra;
    private TextView questao;
    private TextView tempo;
    private TextView rankingLabel;
    private ImageView imagemForca;
    private Button bA, bB, bC, bD, bE, bF, bG, bH, bI, bJ, bK, bL, bM, bN, bO, bP, bQ, bR, bS, bT, bU, bV, bW, bX, bY, bZ;
    private View gameOver;
    private View winner;
    private ListView rankeados;
    private String palavraOculta;
    private String alternativa;
    private String tracos;
    private String tipoJogo;
    private int quantidadeTentivas;
    private char letra;
    private Regressao regressao;
    private NotificationListenerService.Ranking rankingGlobal;
    private MediaPlayer mediaPlayer;
    private List<NotificationListenerService.Ranking> rankings = new ArrayList<NotificationListenerService.Ranking>();
    private ArrayList<String> listaRankings = new ArrayList<String>();
    private ArrayAdapter<NotificationListenerService.Ranking> rankingArrayAdapter;
    private long tempoParado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tipoJogo = getIntent().getExtras().getString("tipo");
        iniciarObjetosView();
        iniciarJogo();
    }

    public void iniciarRegressao() {
        regressao = new Regressao(this, tempo, 1 * 60 * 1000, 1000);
        regressao.start();
    }
    public void reiniciarRegressao() {
        regressao = new Regressao(this, tempo, 1*tempoParado*1000, 1000);
        regressao.start();
    }
    public void pararRegressao() {
        if (regressao != null) {
            regressao.cancel();
            regressao=null;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart","onStart");
        if (tipoJogo.equals("jogador1")) {
            if (mediaPlayer == null&&regressao==null) {
                iniciarRegressao();

            } else {
                if (!mediaPlayer.isPlaying()&&regressao==null) {
                    iniciarRegressao();

                }
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume","onResume");
        if (tipoJogo.equals("jogador1")) {
            if(!mediaPlayer.isPlaying()&&regressao==null){
                reiniciarRegressao();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause","onPause "+tempoParado);
        try{
            tempoParado=regressao.getTempoRestante();
            if(mediaPlayer.isPlaying()){
                pararRegressao();
                mediaPlayer.pause();
            }
        }catch (Exception e){
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.e("onStop","onStop");
        try {
            if (mediaPlayer.isPlaying()) {
                pararRegressao();
                mediaPlayer.pause();
            }
        }catch (Exception e){
        }
    }
    @Override
    protected void onRestart() {
        Log.e("onRestart","onRestart");
        super.onRestart();
        if(!mediaPlayer.isPlaying()){
            reiniciarRegressao();
            mediaPlayer.start();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        pararRegressao();
    }

    private void iniciarObjetosView() {
        tracoPalavra.setTextSize(24);
        questao.setTextSize(24);
        tempo = findViewById(R.id.regressao);

        tempo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (tempo.getText().toString().equalsIgnoreCase("00:00")) {
                        gameOverPotTempo(gameOver);
                    }
                } catch (Exception e) {
                }
            }
        });

    }


    public void iniciarJogo() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        if (tipoJogo.equals("jogador1")) {
        } else {
            final EditText input = new EditText(this);
            dialogBuilder.setTitle("Palavra Secreta")
                    .setMessage("Digite uma palavra usando apenas letras do Alfabeto para seu adversário. Caso contrário exibiremos novamente esse diálogo!")
                    .setView(input)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            palavraOculta = input.getText().toString().toUpperCase();
                            if( palavraOculta .matches("[A-Z]*")) {
                                iniciarRegressao();
                            }else{
                                iniciarJogo();
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    })
                    .create().show();
        }
    }
    public void exibirDialogoFinal(String titulo, String mensagem, View telaFinal) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setTitle(titulo)
                .setMessage(mensagem)
                .setView(telaFinal)
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (tipoJogo.equals("jogador1")) {
                            iniciarRegressao();

                        }
                        iniciarJogo();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        pararRegressao();
                        finish();
                    }
                })
                .create().show();
    }
    public void winnerGame(long pontos, View winner) {
        final String TITULO = "Parabéns! pela Vitória";
        final String MENSAGEM = "Sua pontuação:" + pontos + "\nDeseja Continuar?";
        exibirDialogoFinal(TITULO, MENSAGEM, winner);
    }
    public void gameOverPotTentativas(View gameOver) {
        final String TITULO = "Perdeu!";
        final String MENSAGEM = "\nDeseja Continuar?";
        exibirDialogoFinal(TITULO, MENSAGEM, gameOver);
    }
    public void gameOverPotTempo(View gameOver) {
        pararRegressao();
        final String TITULO = "Perdeu!";
        final String MENSAGEM = "\nO tempo esgotou!\n\nDeseja jogar novamente?\n\n";
        exibirDialogoFinal(TITULO, MENSAGEM, gameOver);
    }

    @Override
    public void onClick(View v) {

    }
}