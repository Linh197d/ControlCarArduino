package com.example.myappcarcontrol;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappcarcontrol.adapter.FunctionAdapter;
import com.example.myappcarcontrol.model.Function;
import com.ingenieriajhr.blujhr.BluJhr;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    // Global variables we will use in the
    private static final String TAG = "mmm";
    private static final String left = "L";
    private static final String right = "R";
    private static final String forward = "F";
    private static final String back = "B";
    private static final String next = "N";
    private static final String previous = "P";
    private static final String trai = "Z";
    private static final String phai = "X";
    private static final String cm = "C";
    private static final String control = "D";
    private static final String start = "Q";
    private static final String stop = "S";
    private static final String auto = "A";
    private static final String km = "K";
    private static final String m = "M";
    private final boolean permisosOnBluetooth = false;
    private List<String> requiredPermissions;
    private ArrayList<String> devicesBluetooth;
    private BluJhr blue;
    private ListView listDeviceBluetooth;
    private ConstraintLayout viewConn;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQ_CODE_SPEECH_INPUT = 0;
    //We will use a Handler to get the BT Connection statys
    ArrayList<Function> mListFunction;
    FunctionAdapter functionAdapter;
    TextView tvtFunction;
    TextView edt_View;
    RecyclerView rcvFunction;
    ImageView imgFunction;
    LinearLayout lnFunction;
    ImageView baseImageRcv;
    ImageButton btnLeft, btnRight, btnDown, btnUp,btnMicrophone;
    Button btnAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listDeviceBluetooth = findViewById(R.id.listDeviceBluetooth);
        viewConn = findViewById(R.id.viewConn);
        blue = new BluJhr(MainActivity.this);
        blue.onBluetooth();
        baseImageRcv= findViewById(R.id.base_rcv_image);
        edt_View = findViewById(R.id.edt_Voice);
        tvtFunction = findViewById(R.id.tvt_function);
        rcvFunction = findViewById(R.id.rcv_function);
        imgFunction = findViewById(R.id.img_dropdown_function);
        lnFunction = findViewById(R.id.ln_layout_ChooseType);
        btnDown = findViewById(R.id.btn_down);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        btnUp = findViewById(R.id.btn_up);
        btnMicrophone = findViewById(R.id.btn_voice);
        btnAuto = findViewById(R.id.btn_auto);
        initView();
        initData();
    }

    private void initView() {
        mListFunction = new ArrayList<>();
        createFunctionList();
        // chọn list Chức năng
        functionAdapter = new FunctionAdapter(this, mListFunction, function -> {
            tvtFunction.setText(function.getName());
            rcvFunction.setVisibility(View.GONE);
            btnMicrophone.setVisibility(View.GONE);
            imgFunction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_left_24);
            if (function.getName().equals("Điều khiển")) {
                baseImageRcv.setImageResource(R.drawable.ic_baseline_control_24);
                blue.bluTx(control);
                btnAuto.setVisibility(View.GONE);
                edt_View.setVisibility(View.GONE);
                btnMicrophone.setVisibility(View.GONE);
                btnDown.setVisibility(View.VISIBLE);
                btnLeft.setVisibility(View.VISIBLE);
                btnRight.setVisibility(View.VISIBLE);
                btnUp.setVisibility(View.VISIBLE);

            } else if (function.getName().equals("Giọng nói")) {
                baseImageRcv.setImageResource(R.drawable.ic_baseline_voice_24);
                blue.bluTx(control);
                btnAuto.setVisibility(View.GONE);
                edt_View.setVisibility(View.VISIBLE);
                btnMicrophone.setVisibility(View.VISIBLE);
                    btnDown.setVisibility(View.GONE);
                    btnLeft.setVisibility(View.GONE);
                    btnRight.setVisibility(View.GONE);
                    btnUp.setVisibility(View.GONE);
            } else {
                /*
                 *** cmt to check
                 */
                baseImageRcv.setImageResource(R.drawable.ic_baseline_brightness_auto_24);
                blue.bluTx(auto);
                btnAuto.setVisibility(View.VISIBLE);
                btnMicrophone.setVisibility(View.GONE);
                btnDown.setVisibility(View.GONE);
                btnLeft.setVisibility(View.GONE);
                btnRight.setVisibility(View.GONE);
                btnUp.setVisibility(View.GONE);
            }
        });
        rcvFunction.setAdapter(functionAdapter);
        rcvFunction.setLayoutManager(new LinearLayoutManager(this));
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initData() {
        listDeviceBluetooth.setOnItemClickListener((adapterView, view, i, l) -> {
            if (!devicesBluetooth.isEmpty()) {
                blue.connect(devicesBluetooth.get(i));
                blue.setDataLoadFinishedListener(connected -> {
                    switch (connected) {
                        case True: {
                            Toast.makeText(MainActivity.this, "True", Toast.LENGTH_SHORT).show();
                            listDeviceBluetooth.setVisibility(View.GONE);
                            viewConn.setVisibility(View.VISIBLE);
                            rxReceived();
                            break;
                        }


                        case Pending: {
                            Toast.makeText(MainActivity.this, "Pending", Toast.LENGTH_SHORT).show();
                            break;
                        }

                        case False: {
                            Toast.makeText(MainActivity.this, "False", Toast.LENGTH_SHORT).show();
                            break;
                        }

                        case Disconnect: {
                            Toast.makeText(MainActivity.this, "Disconnect", Toast.LENGTH_SHORT).show();
                            listDeviceBluetooth.setVisibility(View.VISIBLE);
                            viewConn.setVisibility(View.GONE);
                            break;
                        }
                    }
                });
            }
        });
        lnFunction.setOnClickListener(view -> {
            if (rcvFunction.getVisibility() == View.VISIBLE) {
                rcvFunction.setVisibility(View.GONE);
                imgFunction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_left_24);
            } else {
                rcvFunction.setVisibility(View.VISIBLE);
                imgFunction.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            }
        });
        viewConn.setOnClickListener(view -> rcvFunction.setVisibility(View.GONE));
        btnLeft.setOnTouchListener((view, motionEvent) -> {
            blue.bluTx(left);
            //btnLeft.setBackgroundResource(R.color.button);
            return false;
        });
        btnRight.setOnTouchListener((view, motionEvent) -> {
            blue.bluTx(right);
            //btnRight.setBackgroundResource(R.color.button);
            return false;
        });
        btnUp.setOnTouchListener((view, motionEvent) -> {
            blue.bluTx(forward);
            //btnUp.setBackgroundResource(R.color.button);
            return false;
        });
        btnDown.setOnTouchListener((view, motionEvent) -> {
            blue.bluTx(back);
            // btnDown.setBackgroundResource(R.color.button);
            return false;
        });
        btnMicrophone.setOnClickListener(view -> speech_TO_TEXT());
        btnAuto.setOnClickListener(view -> {
            if (btnAuto.getText() == "START") {
                btnAuto.setText("STOP");
                blue.bluTx(start);
                btnAuto.setBackgroundResource(R.drawable.bg_btn_auto_off);
            } else {
                blue.bluTx(stop);
                btnAuto.setText("START");
                btnAuto.setBackgroundResource(R.drawable.bg_btn_auto_on);
            }

        });
    }

    public void speech_TO_TEXT() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Đang nghe  --__-- ");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn\\'t support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void rxReceived() {
        //blue.loadDateRx(s -> consola.setText(consola.getText().toString() + s));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //cmt to check
        if (blue.checkPermissions(requestCode, grantResults)) {
            Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show();
            blue.initializeBluetooth();
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                blue.initializeBluetooth();
            } else {
                Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //bluetooth
        //cmt to check
        if (!blue.stateBluetoooth() && requestCode == 100) {
            blue.initializeBluetooth();
        } else {
            if (requestCode == 100) {
                devicesBluetooth = blue.deviceBluetooth();
                if (!devicesBluetooth.isEmpty()) {
                    ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, devicesBluetooth);
                    listDeviceBluetooth.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "No tienes vinculados dispositivos", Toast.LENGTH_SHORT).show();
                }

            }
        }
        //voice
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                List<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String text = result.get(0);
//                edt_View.setText(text);
                StringBuilder sendata = new StringBuilder();
                String[] words = text.split("\\s");//tach chuoi dua tren khoang trang
                //su dung vong lap foreach de in cac element cua mang chuoi thu duoc
                //tach Number
                for (String w : words) {

                    switch (w) {
                        case "thẳng":
                            // blue.bluTx(forward);
                            Log.d("abc", next);
                            sendata.append(next);
                            break;
//                            case "tiến":
//                                blue.bluTx(forward);
//                                Log.d("abc",forward);
//                                break;
                        case "không":
                            Log.d("abc", "Không-> null");
                            sendata = new StringBuilder();
                            break;
                        case "lùi":
                            // blue.bluTx(back);
                            Log.d("abc", previous);
                            sendata.append(previous);
                            break;
                        case "trái":
                            //   blue.bluTx(left);
                            Log.d("abc", trai);
                            sendata.append(trai);
                            break;
                        case "phải":
                            //  blue.bluTx(right);
                            Log.d("abc", phai);
                            sendata.append(phai);
                            break;
                        case "m":
                            Log.d("abc", cm);
                            sendata.append("00" + cm);

                            break;
                        case "cm":
                            Log.d("abc", cm);
                            sendata.append(cm);
                            break;
                        case "km":
                            Log.d("abc", cm);
                            sendata.append("00000" + cm);
                            break;
                        case "tự":
                            Log.d("abc", auto);
                            sendata.append(auto);
                            break;
                        case "khiển":
                            Log.d("abc", control);
                            sendata.append(control);
                            break;
                        case "dừng":
                            Log.d("abc", stop);
                            sendata.append(stop);
                            break;

                    }
                    //tách số từ chuỗi
                    Pattern p = Pattern.compile("\\d+");
                    Matcher m = p.matcher(w);
                    while (m.find()) {
                        //System.out.println(m.group());
                        //blue.bluTx(m.group());
                        Log.d("abc", m.group());
                        sendata.append(m.group());
                    }

                }
                // tổng hợp dữ liệu cần gửi r gửi qua bluetooth: dạng F200C = Forward 200 cm
                Log.d("abc", sendata.toString());
                if(sendata.charAt(0)=='N'){
                    blue.bluTx(sendata.toString());
                }else if(sendata.charAt(0)=='D') {
                    tvtFunction.setText("Điều khiển");
                    btnAuto.setVisibility(View.GONE);
//                    edt_View.setVisibility(View.GONE);
                    btnMicrophone.setVisibility(View.GONE);
                    btnDown.setVisibility(View.VISIBLE);
                    btnLeft.setVisibility(View.VISIBLE);
                    btnRight.setVisibility(View.VISIBLE);
                    btnUp.setVisibility(View.VISIBLE);
                    blue.bluTx("D");
                }else if(sendata.charAt(0)=='A'){
                    tvtFunction.setText("Tự hành");
                    btnAuto.setVisibility(View.VISIBLE);
                    btnMicrophone.setVisibility(View.GONE);
                    btnDown.setVisibility(View.GONE);
                    btnLeft.setVisibility(View.GONE);
                    btnRight.setVisibility(View.GONE);
                    btnUp.setVisibility(View.GONE);
                    blue.bluTx("A");
                }else {
                        String s = String.valueOf(sendata.charAt(0));
                        blue.bluTx(s);
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Receiving speech input
     */
//        @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQ_CODE_SPEECH_INPUT: {
//                if (resultCode == RESULT_OK && data != null) {
//                    List<String> result = data
//                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    String text = result.get(0);
//                    edt_View.setText(text);
//                }
//                break;
//            }
//        }
//    }
    private void createFunctionList() {
        mListFunction.add(new Function("Điều khiển", R.drawable.ic_baseline_control_24));
        mListFunction.add(new Function("Giọng nói", R.drawable.ic_baseline_voice_24));
        mListFunction.add(new Function("Tự hành ", R.drawable.ic_baseline_brightness_auto_24));
    }
}