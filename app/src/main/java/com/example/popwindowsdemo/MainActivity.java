package com.example.popwindowsdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button but;
    private PopupWindow pop;
    private RecyclerView recycler;
    private ArrayList<String> list;//所有格式集合
    private LinearLayout layout;
    private ArrayList<String> listdatas = new ArrayList<>();//记录选择的数据
    private MyAdapter adapter;
    private RecyclerView mRecyclerView;
    private TextView tv_text;
    private ItemAdapter mListAdapter;
    ArrayList<String> arrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initRecycleView();
    }
    private void initData() {
        //设置适配器
        adapter = new MyAdapter(MainActivity.this, list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recycler.setLongClickable(true);
        recycler.addItemDecoration(new GridSpacingItemDecoration(3, 80, true));
    }
    private void initView() {
        //手动添加 格式列表数据
        list = new ArrayList<>();
        list.add("txt");
        list.add("pdf");
        list.add("doc");
        list.add("zip");
        list.add("html");
        list.add("umd");
        but = (Button) findViewById(R.id.but);
        layout = findViewById(R.id.layout);
        mRecyclerView = findViewById(R.id.scene_rv);
        tv_text = findViewById(R.id.get_text);
        but.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but:
                initPopwindow();
                break;
        }
    }
    private void initPopwindow() {
        //加载布局
        View contentView = View.inflate(MainActivity.this, R.layout.pop, null);
        pop = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //在此pop的区域 外点击关闭此窗口
        pop.showAtLocation(layout, Gravity.CENTER,0,0);
        pop.setOutsideTouchable(true);
        // 设置背景图片， 必须设置，不然动画没作用
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha=0.3f;
        getWindow().setAttributes(lp);
        pop.setOutsideTouchable(true);
        //添加pop窗口关闭事件  
        pop.setOnDismissListener(new poponDismissListener());
        recycler = contentView.findViewById(R.id.recyView);
        TextView yes = contentView.findViewById(R.id.yes);
        TextView no = contentView.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = "";
                listdatas.clear();
                Map<Integer, Boolean> map = adapter.getMap();
                for (int i = 0; i < list.size(); i++) {
                    if (map.get(i)) {
                        listdatas.add(list.get(i));
                    }
                }
                for (int j = 0; j < listdatas.size(); j++) {
                    content += listdatas.get(j) + ",";
                }
                if (content.length() == 0) {
                    Toast.makeText(MainActivity.this, "请您选择格式", Toast.LENGTH_SHORT).show();
                } else {
                    fillData();
                }
                pop.dismiss();
            }
        });
        initData();
    }
/** 
      * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来 
      * @author cg 
      * 
      */
             class poponDismissListener implements PopupWindow.OnDismissListener{
            @Override
            public void onDismiss() {
            WindowManager.LayoutParams lp=getWindow().getAttributes();
            lp.alpha=1f;
             getWindow().setAttributes(lp);
          }
             }
    private void fillData() {
        arrayList.clear();
        // 获得SD卡根目录路径
        File path = Environment.getExternalStorageDirectory();
        // 判断SD卡是否存在，并且是否具有读写权限
        if (isStoragePermissionGranted()) {
            if (Environment.getExternalStorageState().
                    equals(Environment.MEDIA_MOUNTED)) {
                File[] files = path.listFiles();// 读取文件夹下文件
                getFileName(files, listdatas);
                mListAdapter.setData(arrayList);
                mListAdapter.notifyDataSetChanged();
            }
        }
    }
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            final Context context = getApplicationContext();
            int readPermissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (readPermissionCheck == PackageManager.PERMISSION_GRANTED
                    && writePermissionCheck == PackageManager.PERMISSION_GRANTED) {
                Log.v("juno", "Permission is granted");
                return true;
            } else {
                Log.v("juno", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("juno", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("juno", "onRequestPermissionsResult requestCode ： " + requestCode
                + " Permission: " + permissions[0] + " was " + grantResults[0]
                + " Permission: " + permissions[1] + " was " + grantResults[1]
        );
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            File path = Environment.getExternalStorageDirectory();
        //    File path = new File(path1 + file_path);
            File[] files = path.listFiles();// 读取文件夹下文件
            getFileName(files, listdatas);
            mListAdapter.setData(arrayList);
            mListAdapter.notifyDataSetChanged();
        }
    }


    private String getFileName(File[] files, ArrayList<String> list) {
        String str = "";
        if (files != null) {    // 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if (file.isDirectory()) {//检查此路径名的文件是否是一个目录(文件夹)
                    Log.i("zeng", "若是文件目录。继续读1"
                            + file.getName().toString() + file.getPath().toString());
                    getFileName(file.listFiles(), list);
                    Log.i("zeng", "若是文件目录。继续读2"
                            + file.getName().toString() + file.getPath().toString());
                } else {
                    String fileName = file.getName();
                    for (int i = 0; i < list.size(); i++) {
                        if (fileName.endsWith(list.get(i))) {
                            arrayList.add(fileName);
                        }
                    }
                }
            }
        }
        return str;
    }

    private void initRecycleView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mListAdapter = new ItemAdapter(this);
        mRecyclerView.setAdapter(mListAdapter);
    }

    public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private String TAG = "ItemAdapter";

        private Context mContext;
        private List<String> mLists = new ArrayList<>();

        public ItemAdapter(Context context) {
            mContext = context;
        }

        public void setData(List<String> mWyzeDevices) {
            mLists = mWyzeDevices;
        }

        public List<String> getData() {
            return mLists;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(mContext);
            return new ChildViewHolder(inflater.inflate(R.layout.wyze_file_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            ((ChildViewHolder) holder).update(position);
        }

        @Override
        public int getItemCount() {
            return mLists.size();
        }


        private class ChildViewHolder extends RecyclerView.ViewHolder {
            public TextView mText;
            public ImageView mCheck;
            public View rootView;

            public ChildViewHolder(View itemView) {
                super(itemView);
                mText = (TextView) itemView.findViewById(R.id.description_text);
                rootView = itemView;
            }

            public void update(final int position) {
                mText.setText(mLists.get(position));
                rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
            }
        }
    }
}
