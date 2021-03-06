package br.com.livroandroid.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Exemplo de bluetooth
 *
 * @author rlecheta
 */

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private final String TAG = "MAIN_ACTIVITY";
    private boolean LOG_ENABLED = false;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.activity_main);

        String[] items = new String[] {
                "Verificar e ativar Bluetooth",
                "Buscar devices",
                "Recomendação",
                "Imagens",
                "Configuração",
                "Sair"};

        // Atribuição dos itens ao List View
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            switch (position) {
                case 0:
                    startActivity(new Intent(this, BluetoothCheckActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(this, ListaDevicesActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(this, RecomendacaoActivity.class));
                    break;
                case 3:
                    Toast.makeText(getBaseContext(), "Selecionar imagens", Toast.LENGTH_LONG).show();
                    //startActivity(new Intent(this, BeaconTest.class));
                    break;
                case 4:
                    startActivity(new Intent(this, ConfiguracaoActivity.class));
                    /*
                    File file = Environment.getExternalStorageDirectory();

                    String img = "pc.png";
                    String path = file.getAbsolutePath().toString() + "/" + img;

                    BancoController controller = new BancoController(getBaseContext());
                    // controller.insereImagem(path);

                    Bitmap bitmap = Util.getPhoto(controller.selectImagem(2));

                    ImageView imageView = (ImageView) findViewById(R.id.imageView);

                    imageView.setImageBitmap(bitmap);*/
                    break;
                default:
                    finish();
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public void LOG(String tag, String msg) {
        if (Util.LOG_ENABLED) {
            Log.d(tag, msg);
        }
    }

}