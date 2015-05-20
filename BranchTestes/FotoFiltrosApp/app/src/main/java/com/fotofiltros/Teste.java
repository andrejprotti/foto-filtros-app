package com.fotofiltros;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import android.widget.AdapterView.OnItemSelectedListener;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;


public class Teste extends Activity implements OnItemSelectedListener {

    static{ System.loadLibrary("opencv_java"); }

    Bitmap foto = null;
    Bitmap fotoOriginal = null;

    private static final String TAG = "CallCamera";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQ = 0;

    public Uri fileUri = null;
    public ImageView photoImage = null;
    public String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss",
            Locale.ENGLISH).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);

        photoImage = (ImageView) findViewById(R.id.photo_image);

        //Codigo do Spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_filtros, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        Button callCameraButton = (Button) findViewById(R.id.button_callcamera);

        callCameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQ);



                /* //Codigo anterior
                Intent i = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getOutputPhotoFile();
                fileUri = Uri.fromFile(file);
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQ);
                */


            }
        });

    }

    private File getOutputPhotoFile() {
        File directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getPackageName());

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAG, "Falhou em criar diretorio.");
                return null;
            }
        }

        return new File(directory.getPath() + File.separator + "IMG_"
                + timeStamp + ".jpg");

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQ){
            if (resultCode == RESULT_OK) {
                fotoOriginal = (Bitmap) data.getExtras().get("data");
                foto = (Bitmap) data.getExtras().get("data");

                photoImage.setImageBitmap(fotoOriginal);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelado!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Chamada para captura de imagem falhou!",
                        Toast.LENGTH_LONG).show();
            }
        }

        /* Código anterior
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQ) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = null;
                if (data == null) {
                    Toast.makeText(this, "Imagem salva com sucesso!",
                            Toast.LENGTH_LONG).show();
                    photoUri = fileUri;
                } else {
                    photoUri = data.getData();
                    Toast.makeText(this,
                            "Imagem salva com sucesso em: " + data.getData(),
                            Toast.LENGTH_LONG).show();
                }

                showPhoto(photoUri);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelado!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Chamada para captura de imagem falhou!",
                        Toast.LENGTH_LONG).show();
            }
        }
        */
    }

    private void showPhoto(Uri photoUri) {

        File imageFile = new File(photoUri.getPath());

        if (imageFile.exists()) {
            Drawable oldDrawable = photoImage.getDrawable();
            if (oldDrawable != null) {
                ((BitmapDrawable) oldDrawable).getBitmap().recycle();
            }

            Bitmap bitmap = BitmapFactory.decodeFile(imageFile
                    .getAbsolutePath());
            BitmapDrawable drawable = new BitmapDrawable(this.getResources(),
                    bitmap);
            photoImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoImage.setImageDrawable(drawable);
        }
    }

    public void mostrarGrupo(View view){
        Toast.makeText(this, "André Protti\nAline Castro\nJair Bressani\nMariana Yashima",Toast.LENGTH_LONG).show();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        String selecao = parent.getItemAtPosition(pos).toString();

        switch(selecao){
            case "Detecção de Bordas":
                Mat teste = new Mat();
                Mat teste2 = new Mat();
                Mat resultado = new Mat();
                bitmapToMat(foto, teste);
                Imgproc.cvtColor(teste, teste2, Imgproc.COLOR_BGR2GRAY);
                Imgproc.Canny(teste2, resultado, 10, 100, 3, true);
                matToBitmap(resultado, foto);
                photoImage.setImageBitmap(foto);
                break;
            case "Escolha seu filtro!":
                break;
            case "Imagem original!":
                photoImage.setImageBitmap(fotoOriginal);
            default:
                break;
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
