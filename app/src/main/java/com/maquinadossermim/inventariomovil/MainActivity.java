package com.maquinadossermim.inventariomovil;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.view.WindowManager;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private WebView web;
    private String pendingJson, pendingName;
    private static final int REQ_CAMERA=20, REQ_CREATE=21;
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);web=new WebView(this);setContentView(web);WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(true);s.setMediaPlaybackRequiresUserGesture(false);web.addJavascriptInterface(new Bridge(),"Android");web.setWebViewClient(new WebViewClient());web.setWebChromeClient(new WebChromeClient(){@Override public void onPermissionRequest(PermissionRequest r){runOnUiThread(()->{if(checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){requestPermissions(new String[]{Manifest.permission.CAMERA},REQ_CAMERA);}r.grant(r.getResources());});}});if(checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.CAMERA},REQ_CAMERA);web.loadUrl("file:///android_asset/index.html");}
    public class Bridge { @JavascriptInterface public void exportJson(String txt,String name){runOnUiThread(()->{pendingJson=txt;pendingName=name;Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/json");i.putExtra(Intent.EXTRA_TITLE,name);startActivityForResult(i,REQ_CREATE);});}}
    @Override protected void onActivityResult(int req,int res,Intent data){super.onActivityResult(req,res,data);if(req==REQ_CREATE&&res==RESULT_OK&&data!=null&&pendingJson!=null){try(OutputStream out=getContentResolver().openOutputStream(data.getData())){out.write(pendingJson.getBytes(StandardCharsets.UTF_8));web.evaluateJavascript("alert('Archivo exportado correctamente.')",null);}catch(Exception e){web.evaluateJavascript("alert('No se pudo guardar el archivo.')",null);}pendingJson=null;pendingName=null;}}
    @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}
