package id.mentoring.pakanotomatis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MaterialButton mBtnAddPakan;
    private EditText mEdtInputHours;
    private EditText mEdtInputMinute;
    private TextView mTvNotify;
    private TextView mTvHours;
    private TextView mTvMinute;
    private Switch mSwAuto;
    private Handler handler = new Handler();
    private String mHoursValue;
    private String mMinuteValue;
    private int mPakanValue;
    private FloatingActionButton mFabAddJadwal;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        mFabAddJadwal.setOnClickListener(this);

        notifikasi();

        mSwAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference btnAuto = database.getReference("Kontrol/Auto");
                if (mSwAuto.isChecked()==true){
                    mBtnAddPakan.setEnabled(true);
                    mBtnAddPakan.setOnClickListener(MainActivity.this);
                    mSwAuto.setText("Manual");
                    btnAuto.setValue(true);
                }
                else if (mSwAuto.isChecked()==false){
                    mBtnAddPakan.setEnabled(false);
                    mSwAuto.setText("Otomatis");
                    btnAuto.setValue(false);
                }
            }
        });

        initPresenter();
    }

    private void notifikasi() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPakanValue = Integer.parseInt(snapshot.child("Data/Pakan").getValue().toString());
                if (mPakanValue > 20){
                    mTvNotify.setText("Pakan Habis");
                }
                else if (mPakanValue >= 10 && mPakanValue <= 20){
                    mTvNotify.setText("Pakan Setengah");
                }
                else if (mPakanValue < 10){
                    mTvNotify.setText("Pakan Penuh");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initPresenter() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHoursValue = snapshot.child("Waktu/Hours").getValue().toString();
                mTvHours.setText(mHoursValue);

                mMinuteValue = snapshot.child("Waktu/Minute").getValue().toString();
                mTvMinute.setText(mMinuteValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView() {
        mBtnAddPakan = findViewById(R.id.BtnAddPakan);
        mEdtInputHours = findViewById(R.id.EtHours);
        mEdtInputMinute = findViewById(R.id.EtMinute);
        mTvHours = findViewById(R.id.TvHours);
        mTvMinute = findViewById(R.id.TvMinute);
        mTvNotify = findViewById(R.id.TvNotify);
        mSwAuto = findViewById(R.id.SwAuto);
        mFabAddJadwal = findViewById(R.id.FabAdd);
    }

    @Override
    public void onClick(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        switch (view.getId()){
            case R.id.BtnAddPakan:
                DatabaseReference AddPakan = database.getReference("Kontrol/AddPakan");
                AddPakan.setValue(true);
                Toast.makeText(getApplicationContext(), "Berhasil menambahkan pakan", Toast.LENGTH_SHORT).show();

                break;

            case R.id.FabAdd:

                String HoursValue = mEdtInputHours.getText().toString();
                String MinuteValue = mEdtInputMinute.getText().toString();
                DatabaseReference AddHoursJadwal = database.getReference("Waktu/Hours");
                DatabaseReference AddMinuteJadwal = database.getReference("Waktu/Minute");

                AddHoursJadwal.setValue(HoursValue);
                AddMinuteJadwal.setValue(MinuteValue);
                Toast.makeText(getApplicationContext(), "Berhasil mengupdate jadwal", Toast.LENGTH_SHORT).show();

                break;
        }
    }
}