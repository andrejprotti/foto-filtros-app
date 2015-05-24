package com.fotofiltros;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import android.widget.AdapterView.OnItemSelectedListener;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;
import static org.opencv.imgproc.Imgproc.line;


public class FotoFiltros extends Activity implements OnItemSelectedListener {

    static{ System.loadLibrary("opencv_java"); }



    Bitmap fotoOriginal = null;
    Bitmap fotoDB = null;
    Bitmap fotoBP = null;
    Bitmap fotoCont = null;
    Bitmap fotoNeg = null;
    Bitmap fotoHSV = null;
    Bitmap fotoLP = null;
    Bitmap fotoHist = null;

    Bitmap fotoFinal = null;

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

        final MediaPlayer cameraBtn = MediaPlayer.create(this, R.raw.clic);

        fotoOriginal = BitmapFactory.decodeResource(this.getResources(),R.drawable.corgi2);
        fotoDB = BitmapFactory.decodeResource(this.getResources(),R.drawable.corgi2);
        fotoBP = BitmapFactory.decodeResource(this.getResources(),R.drawable.corgi2);
        fotoCont = BitmapFactory.decodeResource(this.getResources(),R.drawable.corgi2);
        fotoNeg = BitmapFactory.decodeResource(this.getResources(),R.drawable.corgi2);
        fotoHSV = BitmapFactory.decodeResource(this.getResources(),R.drawable.corgi2);
        fotoLP = BitmapFactory.decodeResource(this.getResources(),R.drawable.corgi2);
        fotoHist = BitmapFactory.decodeResource(this.getResources(),R.drawable.corgi2);
        fotoFinal = BitmapFactory.decodeResource(this.getResources(),R.drawable.corgi2);

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
                cameraBtn.start();
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
                fotoDB = (Bitmap) data.getExtras().get("data");
                fotoBP = (Bitmap) data.getExtras().get("data");
                fotoCont = (Bitmap) data.getExtras().get("data");
                fotoNeg = (Bitmap) data.getExtras().get("data");
                fotoHSV = (Bitmap) data.getExtras().get("data");
                fotoLP = (Bitmap) data.getExtras().get("data");
                fotoHist = (Bitmap) data.getExtras().get("data");
                fotoFinal = (Bitmap) data.getExtras().get("data");

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

        final MediaPlayer mostrarGrupoBtn = MediaPlayer.create(this, R.raw.botao);
        mostrarGrupoBtn.start();

        Toast.makeText(this, "André Protti RA: 20485671\n" +
                "Aline Castro RA: 20477413\n" +
                "Jair Bressani RA: 20331492\n" +
                "Mariana Yashima RA: 20473981",Toast.LENGTH_LONG).show();
    }

    public void btSalvar(View view){
        Toast.makeText(this, "Foto salva com sucesso!",Toast.LENGTH_LONG).show();

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/FotoFiltrosApp");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        Log.i(TAG, "" + file);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            fotoFinal.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));

    }

    public void btHistograma(View view){

        Mat hist = new Mat();

        bitmapToMat(fotoHist, hist);

        Mat histCinzas = new Mat();

        Imgproc.cvtColor(hist, histCinzas, Imgproc.COLOR_BGR2GRAY);


        java.util.List<Mat> matList = new LinkedList<Mat>();
        matList.add(histCinzas);
        Mat histogram = new Mat();
        MatOfFloat ranges=new MatOfFloat(0,256);
        MatOfInt histSize = new MatOfInt(255);
        Imgproc.calcHist(
                matList,
                new MatOfInt(0),
                new Mat(),
                histogram ,
                histSize ,
                ranges);

        Mat histImage = Mat.zeros( 100, (int)histSize.get(0, 0)[0], CvType.CV_8UC1);
        Core.normalize(histogram, histogram, 1, histImage.rows() , Core.NORM_MINMAX, -1, new Mat() );
        for( int i = 0; i < (int)histSize.get(0, 0)[0]; i++ )
        {
            line(
                    histImage,
                    new org.opencv.core.Point(i, histImage.rows()),
                    new org.opencv.core.Point(i, histImage.rows() - Math.round(histogram.get(i, 0)[0])),
                    new Scalar(255, 255, 255),
                    1, 8, 0);
        }

        Bitmap bm = Bitmap.createBitmap(histImage.cols(), histImage.rows(),Bitmap.Config.ARGB_8888);

        matToBitmap(histImage, bm);

        photoImage.setImageBitmap(bm);

        fotoFinal = bm;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        String selecao = parent.getItemAtPosition(pos).toString();

        switch(selecao){
            case "Detecção de Bordas":
                Mat teste = new Mat();
                Mat teste2 = new Mat();
                Mat resultado = new Mat();
                bitmapToMat(fotoDB, teste);
                Imgproc.cvtColor(teste, teste2, Imgproc.COLOR_BGR2GRAY);
                Imgproc.Canny(teste2, resultado, 10, 100, 3, true);
                matToBitmap(resultado, fotoDB);
                photoImage.setImageBitmap(fotoDB);
                fotoFinal = fotoDB;
                break;
            case "Branco e Preto":
                Mat bp = new Mat();
                Mat bp2 = new Mat();
                bitmapToMat(fotoBP, bp);
                Imgproc.cvtColor(bp, bp2, Imgproc.COLOR_BGR2GRAY);
                matToBitmap(bp2, fotoBP);
                photoImage.setImageBitmap(fotoBP);
                fotoFinal = fotoBP;
                break;
            case "Contraste Equalizado":
                Mat cont = new Mat();
                Mat cont2 = new Mat();
                Mat cont3 = new Mat();
                bitmapToMat(fotoCont, cont);

                Imgproc.cvtColor(cont, cont2, Imgproc.COLOR_BGR2GRAY);
                Imgproc.equalizeHist(cont2, cont3);

                matToBitmap(cont3, fotoCont);
                photoImage.setImageBitmap(fotoCont);
                fotoFinal = fotoCont;
                break;
            case "Negativo":
                Mat neg = new Mat();
                bitmapToMat(fotoNeg, neg);

                Mat invertcolormatrix= new Mat(neg.rows(),neg.cols(), neg.type(), new Scalar(255,255,255));

                Mat negResult = new Mat(neg.size(),neg.type());

                Core.subtract(invertcolormatrix, neg, negResult);

                matToBitmap(negResult, fotoNeg);
                photoImage.setImageBitmap(fotoNeg);
                fotoFinal = fotoNeg;
                break;
            case "Colourspace HSV":
                Mat hsv = new Mat();
                Mat hsv2 = new Mat();
                bitmapToMat(fotoHSV, hsv);
                Imgproc.cvtColor(hsv, hsv2, Imgproc.COLOR_BGR2HSV);
                matToBitmap(hsv2, fotoHSV);
                photoImage.setImageBitmap(fotoHSV);
                fotoFinal = fotoHSV;
                break;
            case "Filtro Laplaciano":
                Mat lp = new Mat();
                bitmapToMat(fotoLP, lp);

                Mat kernel = new Mat(3,3, CvType.CV_32FC1);
                float[] data = {0, -1, 0, -1, 4, -1, 0, -1, 0};
                kernel.put(0,0,data);

                Mat lp3 = new Mat();

                Imgproc.filter2D(lp, lp3, -1, kernel);

                matToBitmap(lp3, fotoLP);

                photoImage.setImageBitmap(fotoLP);

                fotoFinal = fotoLP;
                break;
            case "Escolha seu filtro!":
                break;
            case "Imagem original!":
                photoImage.setImageBitmap(fotoOriginal);
                fotoFinal = fotoOriginal;
            default:
                break;
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
