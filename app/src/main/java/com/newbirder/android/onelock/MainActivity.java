package com.newbirder.android.onelock;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private DevicePolicyManager policyManager;
    private ComponentName componentName;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取设备管理服务
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, LockReceiver.class);

        if (policyManager.isAdminActive(componentName)) {
            policyManager.lockNow();
//            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            activeManage();
        }
        setContentView(R.layout.activity_main);
    }

    //获取权限
    private void activeManage()
    {
        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        //权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);

        //描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后才能使用锁屏功能");

        startActivityForResult(intent, REQUEST_CODE);
    }

//    @Override
//    protected void onResume() {//重写此方法用来在第一次激活设备管理器之后锁定屏幕
//        if (policyManager.isAdminActive(componentName)) {
//            policyManager.lockNow();
//            android.os.Process.killProcess(android.os.Process.myPid());
//        }
//        super.onResume();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //获取权限成功，立即锁屏并finish自己，否则继续获取权限
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            policyManager.lockNow();
            finish();
        }
        else
        {
            activeManage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
