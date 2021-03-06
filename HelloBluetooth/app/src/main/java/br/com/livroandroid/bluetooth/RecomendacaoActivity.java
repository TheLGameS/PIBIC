package br.com.livroandroid.bluetooth;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

public class RecomendacaoActivity extends ActionBarActivity implements BeaconConsumer {

    private final String TAG = "RECOMENDACAO_ACTIVITY";
    private final String UTF_8 = "UTF-8";
    private final String REGION1 = "REGIAO_1";
    private BeaconManager beaconManager;
    private boolean CHANGE_BEACON_QTD = true;
    private List<Produto> produtos;
    private boolean LOG_ENABLED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendacao);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LOG(TAG, "Activity created");

        // Configuração do detector de beacons.
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setBackgroundMode(true);
        beaconManager.setBackgroundScanPeriod(1000l);
        beaconManager.setBackgroundBetweenScanPeriod(1l);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

        LOG(TAG, "Configuração de beacon efetuada");
    }

    @Override
    protected void onDestroy() {
        beaconManager.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                CHANGE_BEACON_QTD = true;
                LOG(TAG, "Beacon entrou na região");
            }

            @Override
            public void didExitRegion(Region region) {
                CHANGE_BEACON_QTD = true;

                LOG(TAG, "Beacon saiu na região");
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
             /*   if (Util.isWifiConnected(getBaseContext()) == false) {
                    Toast.makeText(getBaseContext(), "Sem rede de internet!", Toast.LENGTH_LONG).show();
                }*/

                if (collection.size() > 0 && CHANGE_BEACON_QTD == true) {

                    LOG(TAG, "Iniciando detecção de beacons: " + collection.size() + " beacon(s) encontrado(s).");

                    for (Beacon beacon : collection) {
                        String nome, major, minor, rssi, distance, _url;


                        LOG(TAG, "Comunicando com o servidor.");
                        try {
                            nome = beacon.getBluetoothName();

                            if (nome == null) {
                                nome = "wgx-beacon";
                            }

                            nome = URLEncoder.encode(nome, UTF_8);
                            major = URLEncoder.encode(beacon.getId2().toString(), UTF_8);
                            minor = URLEncoder.encode(beacon.getId3().toString(), UTF_8);
                            rssi = URLEncoder.encode(String.valueOf(beacon.getRssi()), UTF_8);

                            DecimalFormat format = new DecimalFormat("#.#");
                            distance = URLEncoder.encode(format.format(beacon.getDistance()), UTF_8);

                            _url = "http://54.207.46.165:8081/apsearch/APService?";
                            _url += "device=" + nome;
                            _url += "&major=" + major;
                            _url += "&minor" + minor;
                            _url += "&distance" + distance;
                            _url += "&signal=" + rssi + "d";
                            _url += "&option=beacon";

                            // http://54.207.46.165:8081/apsearch/APService?device=wx-beacon&major=1&minor=21&distance=0.3&signal=-50d&option=beacon

                            LOG(TAG, "URL:\t" + _url);

                            URL url = new URL(_url);
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                            try {
                                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                    LOG(TAG, "Conexão efetuada");

                                    String response = Util.convertStreamToString(urlConnection.getInputStream());
                                    LOG(TAG, "Resposta:\t" + response);

                                    // ----------- PROCESSAR RESPOSTAS! ----------- //

                                    LOG(TAG, "Parsing JSON...");
                                    LOG(TAG, "-----------------");

                                    produtos = Util.parserJason(response);

                                    for (Produto produto : produtos) {
                                        LOG(TAG, "PRODUTO:\tID:\t" + produto.getId() + "\tDESCRICAO:\t" + produto.getDescricao());
                                    }

                                    LOG(TAG, "-----------------");

                                } else {
                                    LOG(TAG, "Conexão não efetuada");
                                }
                            } catch (Exception e) {
                                LOG(TAG, "Erro:\t" + e.getMessage());
                            } finally {
                                urlConnection.disconnect();
                                LOG(TAG, "Conexão encerrada");
                            }

                            CHANGE_BEACON_QTD = false;
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        try {
            // Inicia o monitoramento

            LOG(TAG, "Iniciando monitoramento.");
            beaconManager.startRangingBeaconsInRegion(new Region(REGION1, null, null, null));
            beaconManager.startMonitoringBeaconsInRegion(new Region(REGION1, null, null, null));
        } catch (RemoteException e) {
            LOG(TAG, e.getMessage());
        }
    }

    public void LOG(String tag, String msg) {
        if (Util.LOG_ENABLED) {
            Log.d(tag, msg);
        }
    }
}
