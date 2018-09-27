package app.com.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.bobekos.bobek.scanner.BarcodeView;
import com.bobekos.bobek.scanner.overlay.BarcodeRectOverlay;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;

import app.com.myapp.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ScanActivity extends AppCompatActivity {

    private Disposable disposable = null;
    private boolean isFlashOn = false;
    private boolean isBeepOn = false;
    private boolean isVibrateOn = true;
    private BarcodeView barcodeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scan);
        barcodeView = findViewById(R.id.barcodeView);
        barcodeView.setVibration(isVibrateOn ? 500 : 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        disposable = barcodeView.drawOverlay(new BarcodeRectOverlay(ScanActivity.this)).setAutoFocus(true).setBeepSound(isBeepOn).setVibration(isVibrateOn ? 500 : 0).setFlash(isFlashOn).setFacing(CameraSource.CAMERA_FACING_BACK).getObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Barcode>() {
            @Override
            public void accept(Barcode barcode) throws Exception {
                Intent intent = getIntent();
                intent.putExtra("barcode_value", barcode);
                setResult(RESULT_OK, intent);
                finish();
                //Toast.makeText(ScanActivity.this, barcode.displayValue, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
