
package com.example.demo;

import android.R.bool;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

@SuppressLint("HandlerLeak")
// public class MainActivity extends Activity implements OnClickListener {
public class MainActivity extends Activity {

    Button mBtnExecute;
    String mEdUrl = "http://"; // 请求的url http://192.168.1.102:8890/type/jason/action/GetCarSpeed
    EditText mEdIP;
    EditText mEdPort;
    Spinner mSpContent;
    Spinner mSpTarget;
    String[] spContent;
    String[] spTarget = {
            "服务器"
    };
    String[] targetPort = {
            "8080", "8890"
    };
    private int a;
    // EditText mEdPostJson; //请求的json字符串
    TextView mTxWebContent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 执行按钮
        // mBtnExecute = (Button) findViewById(R.id.main_btn_execute);
        // mBtnExecute.setOnClickListener(this);

        mEdIP = (EditText) findViewById(R.id.edwebIP);
        mEdPort = (EditText) findViewById(R.id.edwebPort);

        // mSpContent = (Spinner) findViewById(R.id.sp_request_content);//请求内容的下拉框
        // mEdPostJson = (EditText) findViewById(R.id.edjson);
        mTxWebContent = (TextView) findViewById(R.id.txwebcontent);// 执行结果

        spContent = new String[19];
        //给Spinner建立数据源
        spContent = getResources().getStringArray(R.array.Interface);
        add_list_target();
        add_list();

        //为请求目标下拉框设置点击监听
        mSpTarget.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent,
                    View view, int position, long id)
            {
                System.out.println("选择下拉框的值：" + position);
                // 根据选择的目标给端口号输入框赋值
                mEdPort.setText(targetPort[position]);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // showToast("Spinner1: unselected");
            }

        });

    }

    // 填充下拉框方法
    private void add_list() {

        // 得到请求内容的下拉控件
        mSpContent = (Spinner) findViewById(R.id.sp_request_content);
        //创建适配器，第二个参数是Spinner没有展开菜单时的默认样式
        ArrayAdapter<String> adapter_spContent = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spContent);

        //设置下拉框展开的样式
        adapter_spContent
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpContent.setAdapter(adapter_spContent);
        //设置点击监听
        mSpContent.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				a=position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
    }

    //给请求目标的下拉框设置数据适配器，绑定数据
    private void add_list_target() {

        // 初始化请求内容的下拉框
        mSpTarget = (Spinner) findViewById(R.id.sp_target);
        //创建Adapter并且绑定数据，第二个参数是Spinner没有展开菜单时的默认样式
        ArrayAdapter<String> adapter_spTarget = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spTarget);

        //设置展开的时候下拉菜单的样式
        adapter_spTarget
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //设置数据适配器
        mSpTarget.setAdapter(adapter_spTarget);

    }

    // 参数设置按钮方法
    public void bt_onclick(View v)
    {
        final String strUrl1;
        final EditText et_carid; // 小车编号
        final EditText et_tralightid; // 红绿灯编号
        final EditText et_ratetype; // 费率类型
        final EditText et_rate; // 费率金额

        String strUrl;
        String strJson = "{}";
        com.example.httppost.HttpThread jsonThread;

        if (isNull(mEdIP)) {
            Toast.makeText(this, "请输入IP", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isNull(mEdPort)) {
            Toast.makeText(this, "请输入端口号", Toast.LENGTH_SHORT).show();
            return;
        }

        mTxWebContent.setText("");
        // 获取URL的值
        String ip = mEdIP.getText().toString().trim();
        String port = mEdPort.getText().toString().trim();

        // 获取目标下拉框的值：服务器/终端
        final String target = mSpTarget.getSelectedItem().toString().trim();
        System.out.println("MainActivity中 target的值：" + target);

        strUrl1 = mEdUrl + ip + ":" + port + "/transportservice/type/jason/action/";
 		//http://192.168.1.155:8080/

        // strUrl1 = mEdPort.getText().toString().trim()+"type/jason/action/";
        // 获取下拉框内容
        String setContent = mSpContent.getSelectedItem().toString();
        System.out.println("选择的设置内容为：" + setContent);

        LayoutInflater inflater = getLayoutInflater();
        final View layout;
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        switch (a) {
            case 0:
                layout = inflater.inflate(R.layout.carid,
                        (ViewGroup) findViewById(R.id.dialog));
                alert.setTitle("查询小车当前速度").setView(layout);
                et_carid = (EditText) layout.findViewById(R.id.et_carid);

                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final String strUrl;
                        String carid = et_carid.getText().toString().trim();
                        if (limitId(carid, 4, new String[] {
                                "请输入小车ID", "小车的ID号为1-4"
                        })) {
                            strUrl = strUrl1 + "GetCarSpeed.do";
                            String strJson = "{\"CarId\":" + Integer.valueOf(carid) + "}";
                            System.out.println("url为：" + strUrl);
                            System.out.println("strJson为：" + strJson);
                            com.example.httppost.HttpThread jsonThread = new com.example.httppost.HttpThread(
                                    layout.getContext(), mHandler);
                            jsonThread.setUrl(strUrl);
                            jsonThread.setJsonstring(strJson);
                            jsonThread.start();
                        }

                    }
                });
                alert.setNegativeButton("取消", null).show();
                break;
            case 1:
                layout = inflater.inflate(R.layout.carid,
                        (ViewGroup) findViewById(R.id.dialog));
                alert.setTitle("查询小车当前位置").setView(layout);
                et_carid = (EditText) layout.findViewById(R.id.et_carid);

                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final String strUrl;
                        String carid = et_carid.getText().toString().trim();
                        if (limitId(carid, 4, new String[] {
                                "请输入小车ID", "小车的ID号为1-4"
                        })) {
                            strUrl = strUrl1 + "GetCarLocation.do";
                            String strJson = "{\"CarId\":" +  Integer.valueOf(carid) + "}";
                            System.out.println("url为：" + strUrl);
                            System.out.println("strJson为：" + strJson);
                            com.example.httppost.HttpThread jsonThread = new com.example.httppost.HttpThread(
                                    layout.getContext(), mHandler);
                            jsonThread.setUrl(strUrl);
                            jsonThread.setJsonstring(strJson);
                            jsonThread.start();

                        }
                    }
                });
                alert.setNegativeButton("取消", null).show();
                break;
            case 2:
                layout = inflater.inflate(R.layout.carid,
                        (ViewGroup) findViewById(R.id.dialog));
                alert.setTitle("查询小车账户余额").setView(layout);
                et_carid = (EditText) layout.findViewById(R.id.et_carid);

                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final String strUrl;
                        String carid = et_carid.getText().toString().trim();
                        if (limitId(carid, 4, new String[] {
                                "请输入小车ID", "小车的ID号为1-4"
                        })) {
                            strUrl = strUrl1 + "GetCarAccountBalance.do";
                            String strJson = "{\"CarId\":" +  Integer.valueOf(carid) + "}";
                            System.out.println("url为：" + strUrl);
                            System.out.println("strJson为：" + strJson);
                            com.example.httppost.HttpThread jsonThread = new com.example.httppost.HttpThread(
                                    layout.getContext(), mHandler);
                            jsonThread.setUrl(strUrl);
                            jsonThread.setJsonstring(strJson);
                            jsonThread.start();

                        }
                        ;
                    }
                });
                alert.setNegativeButton("取消", null).show();
                break;
            case 3:
                layout = inflater.inflate(R.layout.caraction,
                        (ViewGroup) findViewById(R.id.dialog));
                alert.setTitle("设置小车动作").setView(layout);
                et_carid = (EditText) layout.findViewById(R.id.et_carid);
                final EditText et_caraction = (EditText) layout.findViewById(R.id.et_caraction);

                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final String strUrl;
                        String carid = et_carid.getText().toString().trim();
                        if (limitId(carid, 4, new String[] {
                                "请输入小车ID", "小车的ID号为1-4"
                        })) {
                            String caraction = et_caraction.getText().toString().trim();
                            strUrl = strUrl1 + "SetCarMove.do";
                            String strJson = "{\"CarId\":" + Integer.valueOf(carid) + ",\"CarAction\":\""
                                    + caraction
                                    + "\"}";
                            System.out.println("url为：" + strUrl);
                            System.out.println("strJson为：" + strJson);
                            com.example.httppost.HttpThread jsonThread = new com.example.httppost.HttpThread(
                                    layout.getContext(), mHandler);
                            jsonThread.setUrl(strUrl);
                            jsonThread.setJsonstring(strJson);
                            jsonThread.start();

                        }
                        ;
                    }
                });
                alert.setNegativeButton("取消", null).show();
                break;
            case 4:
                layout = inflater.inflate(R.layout.caraccountrecharge,
                        (ViewGroup) findViewById(R.id.dialog));
                alert.setTitle("小车账户充值").setView(layout);
                et_carid = (EditText) layout.findViewById(R.id.et_carid);
                final EditText et_carmoney = (EditText) layout.findViewById(R.id.et_carmoney);

                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final String strUrl;
                        String carid = et_carid.getText().toString().trim();
                        if (limitId(carid, 4, new String[] {
                                "请输入小车ID", "小车的ID号为1-4"
                        })) {
                            String carmoney = et_carmoney.getText().toString().trim();
                            strUrl = strUrl1 + "SetCarAccountRecharge.do";
                            String strJson = "{\"CarId\":" +  Integer.valueOf(carid) + ",\"Money\":" +  Integer.valueOf(carmoney) + "}";
                            System.out.println("url为：" + strUrl);
                            System.out.println("strJson为：" + strJson);
                            com.example.httppost.HttpThread jsonThread = new com.example.httppost.HttpThread(
                                    layout.getContext(), mHandler);
                            jsonThread.setUrl(strUrl);
                            jsonThread.setJsonstring(strJson);
                            jsonThread.start();

                        }
                        ;
                    }
                });
                alert.setNegativeButton("取消", null).show();
                break;
            case 5:
                layout = inflater.inflate(R.layout.caraccountrecord,
                        (ViewGroup) findViewById(R.id.dialog));
                alert.setTitle("查询账户记录").setView(layout);
                et_carid = (EditText) layout.findViewById(R.id.et_carid);
                final EditText et_costtype = (EditText) layout.findViewById(R.id.et_costtype);

                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final String strUrl;
                        String carid = et_carid.getText().toString().trim();
                        if (limitId(carid, 4, new String[] {
                                "请输入小车ID", "小车的ID号为1-4"
                        })) {
                            String costtype = et_costtype.getText().toString().trim();
                            strUrl = strUrl1 + "GetCarAccountRecord.do";
                            String strJson = "{\"CarId\":" +  Integer.valueOf(carid) + ",\"CostType\":\"" + costtype
                                    + "\"}";
                            System.out.println("url为：" + strUrl);
                            System.out.println("strJson为：" + strJson);
                            com.example.httppost.HttpThread jsonThread = new com.example.httppost.HttpThread(
                                    layout.getContext(), mHandler);
                            jsonThread.setUrl(strUrl);
                            jsonThread.setJsonstring(strJson);
                            jsonThread.start();

                        }
                        ;
                    }
                });
                alert.setNegativeButton("取消", null).show();
                break;

            case 6:
                layout = inflater.inflate(R.layout.trafficlight,
                        (ViewGroup) findViewById(R.id.dialog));
                alert.setTitle("查询红绿灯的配置信息").setView(layout);
                et_tralightid = (EditText) layout.findViewById(R.id.et_trafficlightid);

                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final String strUrl;
                        String trafficid = et_tralightid.getText().toString().trim();
                        if (limitId(trafficid, 5, new String[]{"请输入红绿灯ID","红绿灯ID为1-5"})) {
                            strUrl = strUrl1 + "GetTrafficLightConfigAction.do";
                            String strJson = "{\"TrafficLightId\":" +  Integer.valueOf(trafficid) + "}";
                            System.out.println("url为：" + strUrl);
                            System.out.println("strJson为：" + strJson);
                            com.example.httppost.HttpThread jsonThread = new com.example.httppost.HttpThread(
                                    layout.getContext(), mHandler);
                            jsonThread.setUrl(strUrl);
                            jsonThread.setJsonstring(strJson);
                            jsonThread.start();

                        }
                    }
                });
                alert.setNegativeButton("取消", null).show();
                break;
            case 7:
                layout = inflater.inflate(R.layout.setrate,
                        (ViewGroup) findViewById(R.id.dialog));
                alert.setTitle("停车场费率设置").setView(layout);
                et_ratetype = (EditText) layout.findViewById(R.id.et_ratetype);
                et_rate = (EditText) layout.findViewById(R.id.et_rate);

                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final String strUrl;
                        String ratetype = et_ratetype.getText().toString().trim();
                        String rate = et_rate.getText().toString().trim();
                        strUrl = strUrl1 + "SetParkRate.do";
                        String strJson = "{\"RateType\":\"" + ratetype + "\",\"Money\":" +  Integer.valueOf(rate)
                                + "}";
                        System.out.println("url为：" + strUrl);
                        System.out.println("strJson为：" + strJson);
                        com.example.httppost.HttpThread jsonThread = new com.example.httppost.HttpThread(
                                layout.getContext(), mHandler);
                        jsonThread.setUrl(strUrl);
                        jsonThread.setJsonstring(strJson);
                        jsonThread.start();
                    }
                });
                alert.setNegativeButton("取消", null).show();
                break;

            case 8:
                strUrl = strUrl1 + "GetParkFree.do";
                strJson = "{}";
                System.out.println("url为：" + strUrl);
                System.out.println("strJson为：" + strJson);
                jsonThread = new com.example.httppost.HttpThread(
                        v.getContext(), mHandler);
                jsonThread.setUrl(strUrl);
                jsonThread.setJsonstring(strJson);
                jsonThread.start();
                break;
            case 9:
                strUrl = strUrl1 + "GetParkRate.do";
                strJson = "{}";
                System.out.println("url为：" + strUrl);
                System.out.println("strJson为：" + strJson);
                jsonThread = new com.example.httppost.HttpThread(
                        v.getContext(), mHandler);
                jsonThread.setUrl(strUrl);
                jsonThread.setJsonstring(strJson);
                jsonThread.start();
                break;
            case 10:
                strUrl = strUrl1 + "GetAllSense.do";
                strJson = "{}";
                System.out.println("url为：" + strUrl);
                System.out.println("strJson为：" + strJson);
                jsonThread = new com.example.httppost.HttpThread(
                        v.getContext(), mHandler);
                jsonThread.setUrl(strUrl);
                jsonThread.setJsonstring(strJson);
                jsonThread.start();
                break;
            case 11:
                strUrl = strUrl1 + "GetLightSenseValve.do";
                strJson = "{}";
                System.out.println("url为：" + strUrl);
                System.out.println("strJson为：" + strJson);
                jsonThread = new com.example.httppost.HttpThread(
                        v.getContext(), mHandler);
                jsonThread.setUrl(strUrl);
                jsonThread.setJsonstring(strJson);
                jsonThread.start();
                break;
            case 12:
                layout = inflater.inflate(R.layout.bussstation,
                        (ViewGroup) findViewById(R.id.dialog));
                alert.setTitle("站台信息查询").setView(layout);
                final EditText et_busstationid = (EditText) layout
                        .findViewById(R.id.et_busstationid);

                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final String strUrl;
                        String busstationid = et_busstationid.getText().toString().trim();
                        if (limitId(busstationid, 2, new String[]{"请输入站台编号","站台编号为1-2"})) {
                            strUrl = strUrl1 + "GetBusStationInfo.do";
                            String strJson = "{\"BusStationId\":" +  Integer.valueOf(busstationid) + "}";
                            System.out.println("url为：" + strUrl);
                            System.out.println("strJson为：" + strJson);
                            com.example.httppost.HttpThread jsonThread = new com.example.httppost.HttpThread(
                                    layout.getContext(), mHandler);
                            jsonThread.setUrl(strUrl);
                            jsonThread.setJsonstring(strJson);
                            jsonThread.start();
                            
                        }
                    }
                });
                alert.setNegativeButton("取消", null).show();
                break;
            case 13:
                layout = inflater.inflate(R.layout.road,
                        (ViewGroup) findViewById(R.id.dialog));
                alert.setTitle("查询道路状态").setView(layout);
                final EditText et_roadid = (EditText) layout.findViewById(R.id.et_roadid);

                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final String strUrl;
                        String roadid = et_roadid.getText().toString().trim();
                        if (limitId(roadid, 4, new String[]{"请输入道路ID","道路ID为1-4"})) {
                            strUrl = strUrl1 + "GetRoadStatus.do";
                            String strJson = "{\"RoadId\":" +  Integer.valueOf(roadid) + "}";
                            System.out.println("url为：" + strUrl);
                            System.out.println("strJson为：" + strJson);
                            com.example.httppost.HttpThread jsonThread = new com.example.httppost.HttpThread(
                                    layout.getContext(), mHandler);
                            jsonThread.setUrl(strUrl);
                            jsonThread.setJsonstring(strJson);
                            jsonThread.start();
                            
                        }
                    }
                });
                alert.setNegativeButton("取消", null).show();
                break;
        }

    }

    protected boolean limitId(String string, int limit, String[] msg) {
        if (string.equals("") || string == null) {
            Toast.makeText(MainActivity.this, msg[0], 1).show();
            return false;
        }
        int id = Integer.valueOf(string);
        if (!(0 < id && id <= limit)) {
            Toast.makeText(MainActivity.this, msg[1], 1).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*
         * if (id == R.id.action_settings) { return true; }
         */
        return super.onOptionsItemSelected(item);
    }

    private boolean isNull(EditText editText) {
        String text = editText.getText().toString().trim();
        if (text != null && text.length() > 0) {
            return false;
        }
        return true;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // if (msg.what == 1) {
            if (msg.what == 1 || msg.what == 901) {

                String strWebContent = null;
                strWebContent = (String) msg.obj;

                mTxWebContent.setText(strWebContent);

            }
        }
    };
}
