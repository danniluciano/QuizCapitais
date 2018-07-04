package br.com.danielaluciano.quizcapitais;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Variáveis
    private GoogleMap mMap;
    private ImageView fotoUsuarioImageView;
    private Button inserirNomeButton;
    private Button tirarFotoButton;
    private TextView nomeUsuarioTextView;
    private Button iniciarQuizButton;
    private TextView questaoTextView;
    private TextInputEditText respostaTextInputEditText;
    private TextView OKButton;
    private TextView scoreTextView;
    private String nomeUsuario;
    private LocationManager locationManager;  //permite acesso ao GPS
    private static final int REQ_PERMISSAO_GPS = 2; //notificado quando eventos de GPS acontecem
    private static final int REQUEST_IMAGE_CAPTURE = 1; //notificando quando eventos de fotos acontecem
    private int numQuestao, score,k, qtde_acertada; //questao que sorteou
    private LatLng loc = null;
    private boolean segundaTentativa = false;
    private double latitude, longitude; //para localização do usuario
    private Bitmap fotoBitmap; //foto do usuario
    private Localizacoes[] localizacoes = new Localizacoes[30];
    private List<Localizacoes> listaPerguntas = new ArrayList<>();
    private List<Localizacoes> listaPerguntasSorteadas;
    private InputMethodManager inputManager;

    private class Localizacoes {
        private Double lat;
        private Double log;
        private int numQuestao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Componentes
        fotoUsuarioImageView = findViewById(R.id.fotoUsuarioImageView);
        nomeUsuarioTextView = findViewById(R.id.nomeUsuarioTextView);
        questaoTextView = findViewById(R.id.questaoTextView);
        respostaTextInputEditText = findViewById(R.id.respostaTextInputEditText);
        scoreTextView = findViewById(R.id.scoreTextView);
        OKButton = findViewById(R.id.OKButton);
        inserirNomeButton = findViewById(R.id.inserirNomeButton);
        iniciarQuizButton = findViewById(R.id.iniciarQuizButton);
        tirarFotoButton = findViewById(R.id.tirarFotoButton);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Listeners dos botões
        OKButton.setOnClickListener(OKButtonEvent);
        inserirNomeButton.setOnClickListener(inserirNomeButtonEvent);
        iniciarQuizButton.setOnClickListener(iniciarQuizButtonEvent);
        tirarFotoButton.setOnClickListener(tirarFotoButtonEvent);

        OKButton.setEnabled(false); //Para iniciar desativado
        fotoBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.semfoto), 80, 80, false);
        nomeUsuario = " ";
        assignLocations();

    }

    @Override
    protected void onStart() {
        super.onStart();
        iniciarGps();
    }

    private void iniciarGps() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { //Verifica se foi concedida a permissao para gps
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, observer);
        }
        else {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSAO_GPS);
        }
    }

    public void assignLocations () {
        for (int i=0;i<localizacoes.length;i++) { //Cria o vetor de localizações
            localizacoes[i] = new Localizacoes();
            localizacoes[i].numQuestao = i;
        }

        localizacoes[0].lat = 45.4215296;
        localizacoes[1].lat = 38.9071923;
        localizacoes[2].lat = -15.7942287;
        localizacoes[3].lat = -34.6036844;
        localizacoes[4].lat = -33.44889;
        localizacoes[5].lat = 52.5200066;
        localizacoes[6].lat = 40.4167754;
        localizacoes[7].lat = 48.856614;
        localizacoes[8].lat = 37.9838096;
        localizacoes[9].lat = 55.6760968;
        localizacoes[10].lat = 41.9027835;
        localizacoes[11].lat = 59.9138688;
        localizacoes[12].lat = 38.7223263;
        localizacoes[13].lat = 55.755826;
        localizacoes[14].lat = -8.8399876;
        localizacoes[15].lat = 3.8480325;
        localizacoes[16].lat = 14.716677;
        localizacoes[17].lat = 30.0444196;
        localizacoes[18].lat = -1.2920659;
        localizacoes[19].lat = 35.7130467;
        localizacoes[20].lat = 31.768319;
        localizacoes[21].lat = 39.9041999;
        localizacoes[22].lat = 37.5560571;
        localizacoes[23].lat = 33.5138073;
        localizacoes[24].lat = -35.3100792;
        localizacoes[25].lat = -21.1393418;
        localizacoes[26].lat = 19.4326077;
        localizacoes[27].lat = -12.046374;
        localizacoes[28].lat = -25.2637399;
        localizacoes[29].lat = -34.9011127;

        //Popuplar Longitude
        localizacoes[0].log = -75.6971931;
        localizacoes[1].log = -77.0368707;
        localizacoes[2].log = -47.8821658;
        localizacoes[3].log = -58.3815591;
        localizacoes[4].log = -70.669265;
        localizacoes[5].log = 13.404954;
        localizacoes[6].log = -3.7037902;
        localizacoes[7].log = 2.3522219;
        localizacoes[8].log = 23.7275388;
        localizacoes[9].log = 12.5683372;
        localizacoes[10].log = 12.4963655;
        localizacoes[11].log = 10.7522454;
        localizacoes[12].log = -9.1392714 ;
        localizacoes[13].log = 37.6172999;
        localizacoes[14].log = 13.2894368;
        localizacoes[15].log = 11.5020752 ;
        localizacoes[16].log = -17.4676861;
        localizacoes[17].log = 31.2357116;
        localizacoes[18].log = 36.8219462;
        localizacoes[19].log = 139.7310164;
        localizacoes[20].log = 35.21371;
        localizacoes[21].log = 116.4073963;
        localizacoes[22].log = 126.9914444;
        localizacoes[23].log = 36.2765279;
        localizacoes[24].log = 149.1242583;
        localizacoes[25].log = -175.204947;
        localizacoes[26].log = -99.133208;
        localizacoes[27].log = -77.0427934;
        localizacoes[28].log = -57.575926;
        localizacoes[29].log = -56.1645314;

        for (Localizacoes l : localizacoes) {
            listaPerguntas.add(l);
        }
    }

    LocationListener observer = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    //Listener do botão Inserir Nome
    View.OnClickListener inserirNomeButtonEvent  = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //AlertDialog com EditText para usuario digitar o nome
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this); //Não tem um Input, por isso usaremos o alertDialog com editText junto
            builder.setTitle(getString(R.string.title_alert_input)); //titulo do alertDialog

            final EditText input = new EditText(MapsActivity.this); // especificar o tipo de input, no caso texto -> editText
            input.setInputType(InputType.TYPE_CLASS_TEXT); //Tipo de entrada (texto).
            builder.setView(input); //Ir mostrando a entrada
            
            builder.setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {  //Caso clique OK
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(input.getText().toString().equals("")) {  //como está vázia não coloca o nome
                        Toast.makeText(MapsActivity.this, getString(R.string.nao_digitou_nome), Toast.LENGTH_SHORT).show();
                    }
                    else {   //Se não estiver vazio
                    nomeUsuario = input.getText().toString(); //variavel recebe o que tem no input
                    StringBuilder sb = new StringBuilder (  //e vamos concatenar com Bem-vindo + o espaço + nomeUsuario
                            getString(R.string.bem_vindo));
                    sb.append(" ").  // espaço
                    append(nomeUsuario); // o que foi digitado
                    sb.toString(); //Convertemos tudo para String
                    nomeUsuarioTextView.setText(sb); //Mostramos no TextView abaixo da foto
                    }
                }
            });

            builder.setNegativeButton(getString(R.string.cancelar_button), new DialogInterface.OnClickListener() { //Caso cancele
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel(); //fecha o AlertDialog
                }
            });

            builder.show();    //Cria o AlertDialog com os argumentos fornecidos e o exibe
        }
    };

    //Listener do botão Iniciar
    View.OnClickListener iniciarQuizButtonEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                sortearPerguntas();
                inserirNomeButton.setEnabled(false);
                tirarFotoButton.setEnabled(false);
                iniciarQuizButton.setEnabled(false);
                OKButton.setEnabled(true);
                Toast.makeText(MapsActivity.this, getString(R.string.inicio), Toast.LENGTH_SHORT).show();
                k=0;
                qtde_acertada = 0;
                score = 0;
                scoreTextView.setText("");
                realizarQuiz();  //Não teve nenhum dado faltando, chamou o quiz direto
            }
            else {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSAO_GPS);
            }
        }
    };

    private void sortearPerguntas() {
        listaPerguntasSorteadas = new ArrayList<>(listaPerguntas);
        Collections.shuffle(listaPerguntasSorteadas);
    }

    public void realizarQuiz() {
        respostaTextInputEditText.setText("");
        respostaTextInputEditText.requestFocus();
        numQuestao = escolheQuestao();

        if (numQuestao == -1001) {
            Toast.makeText(MapsActivity.this,getString(R.string.fim), Toast.LENGTH_SHORT).show();
            questaoTextView.setText("");
            OKButton.setEnabled(false);
            loc = new LatLng(latitude,longitude); //localização usuário
            searchOnMapUsuario(nomeUsuario,fotoBitmap);
        }
        else {
            questaoTextView.setText(getString(retornaQuestao(numQuestao)));
        }
    }

    //Listener do botão OK
    View.OnClickListener OKButtonEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String respostaCerta = getString(retornaResposta(numQuestao));
            String respostaDigitada = respostaTextInputEditText.getText().toString().trim();
            if (TextUtils.isEmpty(respostaDigitada)) {   //Se estiver vazio
                Toast.makeText(MapsActivity.this, getString(R.string.erro_ok_button), Toast.LENGTH_SHORT).show();  //aparece abaixo do TextInputLayout
            }
            else if (respostaDigitada.equalsIgnoreCase(respostaCerta)) {
                    loc = new LatLng( localizacoes[numQuestao].lat,   //Atribui as localizações
                            localizacoes[numQuestao].log);
                    qtde_acertada++;
                if (segundaTentativa == true) {
                    segundaTentativa = false;
                }
                Toast.makeText(MapsActivity.this, getString(R.string.resposta_correta), Toast.LENGTH_SHORT).show();
                Bitmap imageBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.tesouro), 80, 80, false);
                searchOnMap(respostaCerta, imageBitmap);
                tirarTeclado();

            }
            else {
                if (segundaTentativa == false) {
                    segundaTentativa = true;
                    Toast.makeText(MapsActivity.this, getString(R.string.resposta_errada), Toast.LENGTH_SHORT).show();
                }
                else {
                    segundaTentativa=false;
                    Toast.makeText(MapsActivity.this, getString(R.string.segunda_resposta_errada) + " " + getString(R.string.capital) + respostaCerta, Toast.LENGTH_SHORT).show();
                    realizarQuiz();
                }
                score = score-1;
                String somaScore = " " + score;
                scoreTextView.setText(somaScore);
                respostaTextInputEditText.setText("");
                respostaTextInputEditText.requestFocus();
            }
        }
    };

    private void tirarTeclado() {
        if(inputManager.isActive())
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public void searchOnMap (String countryName, Bitmap imageBitmap) { //Função que recebe a imagem correta, ajusta o tamanho e busca no mapa
        Marker m = mMap.addMarker(new MarkerOptions().position(loc).title(countryName).icon(BitmapDescriptorFactory.fromBitmap(imageBitmap)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.clear();
                marker.remove();
                score = score+2;
                String somaScore = " " + score;
                scoreTextView.setText(somaScore);
                realizarQuiz();
                return true;
            }
        });
        final CameraPosition position = new CameraPosition.Builder()
                .target( loc )     //  Localização
                .bearing( 45 )        //  Rotação da câmera
                .tilt( 90 )             //  Ângulo em graus
                .zoom( 12 )           //  Zoom
                .build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
        mMap.animateCamera(update);
    }

    public void searchOnMapUsuario (String countryName, Bitmap imageBitmap) { //Função que recebe a imagem correta, ajusta o tamanho e busca no mapa
        String textResultado = getString(R.string.qtde_de_pontos) + score + " " + getString(R.string.qtde_questoes) + qtde_acertada;
        Marker m = mMap.addMarker(new MarkerOptions().position(loc).title(countryName).snippet(textResultado).icon(BitmapDescriptorFactory.fromBitmap(imageBitmap)));
        Toast.makeText(MapsActivity.this, getString(R.string.aviso_final), Toast.LENGTH_SHORT).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        final CameraPosition position = new CameraPosition.Builder()
                .target( loc )     //  Localizaçã
                .bearing( 45 )        //  Rotação da câmera
                .tilt( 90 )             //  Ângulo em graus
                .zoom( 19 )           //  Zoom
                .build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
        mMap.animateCamera(update);
        m.showInfoWindow();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                scoreTextView.setText("");
                //botões
                tirarFotoButton.setEnabled(true);
                inserirNomeButton.setEnabled(true);
                iniciarQuizButton.setEnabled(true);
                Toast.makeText(MapsActivity.this, getString(R.string.clicar_foto), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public int escolheQuestao () {
        if(k >= listaPerguntasSorteadas.size())
            return -1001;

        int numQ = listaPerguntasSorteadas.get(k).numQuestao;
        k++;
        return numQ;
    }

    public int retornaQuestao (int random) {
        String questao = "ques" + random;
        String temp = getPackageName();
        int id = getResources().getIdentifier(questao,"string",temp);
        return (id);
    }

    public int retornaResposta (int random) {
        String questao = "res" + random;
        String temp = getPackageName();
        int id = getResources().getIdentifier(questao,"string",temp);
        return (id);
    }

    //Listener do botão tirar foto
    View.OnClickListener tirarFotoButtonEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) { //Verifica se foi concedida a permissao para tirar foto
                dispatchTakePictureIntent(); //Funcão que tira a foto
            }
            else{ //Solicita a permissao
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){  //e aceito
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){ //verifica se foi a camera
                        dispatchTakePictureIntent();  //abre para tirar foto
                    }
                }
                else{  //exibe uma mensagem de que precisa ser permitido para tirar a foto
                    Toast.makeText(MapsActivity.this, getString(R.string.permissao_tirar_foto), Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case REQ_PERMISSAO_GPS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, observer);
                        Toast.makeText(this, getString(R.string.gps_ok), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, getString(R.string.permissao_gps), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {   //para esperar o resultado de alguma foto tirada
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {  //Se o usuario tirou uma foto coloca na imageView
            Bundle extras = data.getExtras();
            fotoBitmap = (Bitmap) extras.get("data");
            Bitmap.createScaledBitmap(fotoBitmap, 100, 100, false);
            fotoUsuarioImageView.setImageBitmap(fotoBitmap);
        }
    }

    private void dispatchTakePictureIntent() {            //Abre a camera para tirar foto
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {   //Requisita acesso aos mapas do google
        mMap = googleMap;
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(observer);
    }
}
