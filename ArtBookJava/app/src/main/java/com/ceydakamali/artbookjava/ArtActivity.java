package com.ceydakamali.artbookjava;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.ActivityInfoCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.ceydakamali.artbookjava.databinding.ActivityArtBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class ArtActivity extends AppCompatActivity {

    private ActivityArtBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher; //galeriye gitme izini verildiğinde ne olacağını bilmek istiyoruz.
    ActivityResultLauncher<String> permissionLauncher; //izin isteyip o izin ne olacağını bilmek istiyoruz.
    Bitmap selectedImage; //bitmap görselleri içinde tuttuğumuz sınıfdır.
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher(); // oncreate altında bunu kesinlikle çağırmalıyız

        database = this.openOrCreateDatabase("Arts", MODE_PRIVATE,null);

        Intent intent=getIntent();
        String info = intent.getStringExtra("info");

        if(info.equals("new")){
            //new art
            binding.nameText.setText("");
            binding.artistText.setText("");
            binding.yearText.setText("");
            binding.button.setVisibility(View.VISIBLE);
            binding.imageView.setImageResource(R.drawable.selectimages);
        }else {
            int artId = intent.getIntExtra("artId", 0);
            binding.button.setVisibility(View.INVISIBLE);

            try {
                Cursor cursor = database.rawQuery("SELECT * FROM arts WHERE id= ?",new String[] {String.valueOf(artId)});
                //{String.valueOf(artId)} bu sorgudaki soru işareti yerine gelir.
                int artNameIx = cursor.getColumnIndex("artname");
                int painterNameIx = cursor.getColumnIndex("paintername");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");

                while(cursor.moveToNext()){
                    binding.nameText.setText(cursor.getString(artNameIx));
                    binding.artistText.setText(cursor.getString(painterNameIx));
                    binding.yearText.setText(cursor.getString(yearIx));

                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    binding.imageView.setImageBitmap(bitmap);
                }
                cursor.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void save(View view){

        String name = binding.nameText.getText().toString();
        String artistName = binding.artistText.getText().toString();
        String year = binding.yearText.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage,300);
        //image'i byte şekline çevirmemiz lazım databaseye kaydetmek için
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        try {

            database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY, artname VARCHAR, paintername VARCHAR, year VARCHAR, image BLOB)");

            String sqlString = "INSERT INTO arts (artname, paintername, year, image) VALUES (?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);// sqlstringi databasede çalıştır diyoruz.
            sqLiteStatement.bindString(1, name);// 1.soru işareti name ile bağla diyoruz.
            sqLiteStatement.bindString(2, artistName);// 1.soru işareti artistname ile bağla diyoruz.
            sqLiteStatement.bindString(3, year);// 1.soru işareti year ile bağla diyoruz.
            sqLiteStatement.bindBlob(4, byteArray);// 1.soru işareti image ile bağla diyoruz.
            sqLiteStatement.execute();

        }catch (Exception e){

        }

        //finish(); alttaki 3 kod yerine bunuda kullanabiliriz.Bu kod save işlemi bittikten sonra main aktiviteye geçer.
        Intent intent = new Intent(ArtActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // tüm aktiviteleri kapat yeni aktiviteyi aç diyoruz.
        startActivity(intent);


    }

    public void selectImage(View view){
        //contextcompat kullanma sebebimiz READ_EXTERNAL_STORAGE izninin android 19 sürümünden sonra kullanılmasıdır.
        //PERMISSION_GRANTED izin verildi demektir.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                //izin isteme gerekçesi gösterilcekse bu adım çalışcak
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                // request permission - izin verilmedigi zaman
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        }else{
            //gallery
            //intentle aksiyon yaratılabilir.
            //MediaStore.Images.Media.EXTERNAL_CONTENT_URI buraya git ve resim seç
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }

    }

    private void registerLauncher(){
        //activityResultLauncherde permissionLauncherden dönen sonuca göre aktivite başlatıyoruz yani izin verildiyse galeriye gidiyoruz.
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) { //kullanıcı galeriye gitti ama bir şey seçti mi
                if(result.getResultCode() == RESULT_OK){ //kullanıcı galeriden resim seçmişse
                    Intent intentFromResult = result.getData();
                    if(intentFromResult !=null){
                        Uri imageData = intentFromResult.getData();//seçilen resmin konumunu verir.
                        //binding.imageView.setImageURI(imageData); imageview icerisinde bu imagedatayı yerleştiriyor ama bize imageData içerisindeki veri lazım konumu değil bu yüzden bitmape çeviricez.

                        //Bitmape çevirme
                        try {
                            if(Build.VERSION.SDK_INT >= 28){ //ImageDecoder resime dönüştürür
                                ImageDecoder.Source source = ImageDecoder.createSource(ArtActivity.this.getContentResolver(),imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }else{
                                selectedImage = MediaStore.Images.Media.getBitmap(ArtActivity.this.getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                }

            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){ //izin verildiyse
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else{ //izin verilmediyse
                    Toast.makeText(ArtActivity.this, "Permission needed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize){ //image küçültme
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if(bitmapRatio > 1){
            //landscape image
            width= maximumSize;
            height= (int) (width/bitmapRatio);
        }else{
            height= maximumSize;
            width = (int) (height * bitmapRatio);
        }
        return image.createScaledBitmap(image, width, height, true);
    }
}