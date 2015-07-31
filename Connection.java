package com.tap.mobileestatemodule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tap.mobileestatemodule.database.Database;
import com.tap.mobileestatemodule.database.TM_AFD;
import com.tap.mobileestatemodule.database.TM_BLOCK;
import com.tap.mobileestatemodule.database.TM_COMP;
import com.tap.mobileestatemodule.database.TM_CONTENT_INSPECTION;
import com.tap.mobileestatemodule.database.TM_EMPLOYEE;
import com.tap.mobileestatemodule.database.TM_KUALITAS_PANEN;
import com.tap.mobileestatemodule.database.TM_LOGIN;
import com.tap.mobileestatemodule.database.TM_PARAMETER;
import com.tap.mobileestatemodule.database.TM_REGION;
import com.tap.mobileestatemodule.database.TM_USER_AUTH;
import com.tap.mobileestatemodule.database.TM_EST;
import com.tap.mobileestatemodule.database.TR_HS_AFD;
import com.tap.mobileestatemodule.database.TR_HS_BLOCK;
import com.tap.mobileestatemodule.database.TR_HS_COMP;
import com.tap.mobileestatemodule.database.TR_HS_EST;
import com.tap.mobileestatemodule.database.TR_HS_REGION;
import com.tap.mobileestatemodule.database.T_RANGE;


import com.tap.mobileestatemodule.database.TM_HS_ATTRIBUTE;
import com.tap.mobileestatemodule.database.TM_POI;
import com.tap.mobileestatemodule.database.TR_HS_LAND_USE;
import com.tap.mobileestatemodule.database.TR_HS_LAND_USE_DETAIL;
import com.tap.mobileestatemodule.database.TR_HS_SUB_BLOCK;
import com.tap.mobileestatemodule.database.TR_HS_UNPLANTED;
import com.tap.mobileestatemodule.database.TR_PALM;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Connection extends Activity {

    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;
    public static Connection sharedObjectConnection;
    
    private Bundle bundle;
    private String sUrl,jsonTASK;
    private URL url;
    private HttpURLConnection urlc;
    private String sNoData = "";
    private String sNext;
	
    
    JSONObject jsonobj;
    JSONObject jsonobjforimage;
    ArrayList<String> alImageName = new ArrayList<String>();
    JSONObject jObj,jObjIMAGE;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        bundle = getIntent().getExtras();
        jsonTASK = bundle.getString("JSONTASK");
        if(jsonTASK.equals("Check_Update"))
    	{
    		setJsonObjectCheck();
    	}
    	else if(jsonTASK.equals("Reset"))
    	{
    		mResetPassword();
    	}
        //Initialize a LoadViewTask object and call the execute() method
        new LoadViewTask().execute();
        
    }
	
    public void mResetPassword() {
    	jsonobj = new JSONObject();
    	try {
    		ObjectTransfer ot = (ObjectTransfer) getApplication();
            jsonobj.put("DEVICE_ID", ot.getDeviceID());
            jsonobj.put("NIK", ot.getUSNIK());
    	} catch (JSONException ex) {
    	    ex.printStackTrace();
    	}
    }
    
	public void setJsonObjectInstall()
	{
		jsonobj = new JSONObject();
    	try 
    	{
    		ObjectTransfer ot = (ObjectTransfer) getApplication();
            jsonobj.put("DEVICE_ID", ot.getDeviceID());
            jsonobj.put("DATA", "YES");
    	} catch (JSONException ex) {
    	    ex.printStackTrace();
    	}
	}

	public void setJsonObjectCheck()
	{
		jsonobj = new JSONObject();
    	try 
    	{
    		Resources res = getResources();
    		ObjectTransfer ot = (ObjectTransfer) getApplication();
    		int versionID = res.getInteger(R.integer.Label_Version_Code);
        	ot.setVersionID(versionID);
            jsonobj.put("DEVICE_ID", ot.getDeviceID());
            jsonobj.put("VERSION", versionID);
            jsonobj.put("NIK", ot.getUSNIK());
            jsonobj.put("REFERENCE_ROLE", ot.getReference_Role());
            jsonobj.put("LOCATION_CODE", ot.getLocation_Code());
            jsonobj.put("COMPANY", ot.getUSComp_Code());
    	} catch (JSONException ex) {
    	    ex.printStackTrace();
    	}
	}

	public void setJsonObjectDownload(String pData, String pMAP, String pManual, String pAPK)
	{
		jsonobj = new JSONObject();
    	try 
    	{
    		Resources res = getResources();
    		ObjectTransfer ot = (ObjectTransfer) getApplication();
            jsonobj.put("DEVICE_ID", ot.getDeviceID());
            jsonobj.put("VERSION", res.getInteger(R.integer.Label_Version_Code));
            jsonobj.put("NIK", ot.getUSNIK());
            jsonobj.put("REFERENCE_ROLE", ot.getReference_Role());
            jsonobj.put("LOCATION_CODE", ot.getLocation_Code());
            jsonobj.put("COMPANY", ot.getUSComp_Code());
            jsonobj.put("DATA", pData);
            jsonobj.put("MAP", pMAP);
            jsonobj.put("MANUAL", pManual);
            jsonobj.put("APK", pAPK);
    	} catch (JSONException ex) {
    	    ex.printStackTrace();
    	}
	}

    public void dialogUpdate(boolean pData, boolean pMAP, boolean pManual, boolean pAPK)
    {
    	final boolean[] states = {pData, pMAP, pManual, pAPK};
    	
    	new Thread()
    	{
    	    public void run()
    	    {
    	        Connection.this.runOnUiThread(new Runnable()
    	        {
    	            public void run()
    	            {
    	                //Do your UI operations like dialog opening or Toast here
    	            	builder.setTitle(getString(R.string.UpdateConfirmation));
    	        	    builder.setMultiChoiceItems(R.array.Option_Update, states, new DialogInterface.OnMultiChoiceClickListener(){
    	        	        public void onClick(DialogInterface dialogInterface, int item, boolean state) {
    	        	        }
    	        	    });
    	        	    builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
    	        	        public void onClick(DialogInterface dialog, int id) {
    	        	    	    String sData = "NO";
    	        	    	    String sMap = "NO";
    	        	    	    String sManual = "NO";
    	        	    	    String sApk = "NO";
    	        	            SparseBooleanArray CheCked = ((AlertDialog)dialog).getListView().getCheckedItemPositions();
    	        	            if (CheCked.get(0))
    	        	            {
    	        	                Toast.makeText(Connection.this, "Data", Toast.LENGTH_SHORT).show();
    	        	                sData = "YES";
    	        	            }
    	        	            if (CheCked.get(1))
    	        	            {
    	        	                Toast.makeText(Connection.this, "Map", Toast.LENGTH_SHORT).show();
    	        	                sMap = "YES";
    	        	            }
    	        	            if (CheCked.get(2))
    	        	            {
    	        	                Toast.makeText(Connection.this, "User Manual", Toast.LENGTH_SHORT).show();
    	        	                sManual = "YES";
    	        	            }
    	        	            if (CheCked.get(3))
    	        	            {
    	        	                Toast.makeText(Connection.this, "APK", Toast.LENGTH_SHORT).show();
    	        	                sApk = "YES";
    	        	            }
    	        	            
    	        	            setJsonObjectDownload(sData, sMap, sManual, sApk);
    	        	            jsonTASK = "download";
    	        	            new LoadViewTask().execute();
    	        	        }
    	        	    });
    	        	    builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
    	        	        public void onClick(DialogInterface dialog, int id) {
    	        	             dialog.cancel();
    	        	             
    	        	             Intent intent = new Intent(Connection.this, MainMenu.class);
    	        		         startActivity(intent);
    	        		         Connection.this.finish();  
    	        	        }
    	        	    });
    	        	  builder.create().show();  
    	            }
    	        });
    	    }
    	}.start();
    	
	    
    	
	}
	
	//To use the AsyncTask, it must be subclassed
    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
        //Before running code in the separate thread
    	ObjectTransfer ot = (ObjectTransfer) getApplication();
        
    	String wurl = ot.getLinkServerData();
    	String wurlImage = ot.getLinkServerImage();
    	String sStatus="";
		String sMessage="";

		private ArrayList<JSONObject> aljObjTM_REGION;
		private ArrayList<JSONObject> aljObjTM_COMP;
		private ArrayList<JSONObject> aljObjTM_EST;
		private ArrayList<JSONObject> aljObjTM_AFD;
		private ArrayList<JSONObject> aljObjTM_BLOCK;
		private ArrayList<JSONObject> aljObjTM_EMPLOYEE;
		private ArrayList<JSONObject> aljObjTM_LOGIN;
		private ArrayList<JSONObject> aljObjTM_USER_AUTH;
		private ArrayList<JSONObject> aljObjTM_PARAMETER;
		private ArrayList<JSONObject> aljObjTM_CONTENT_INSPECTION;
		private ArrayList<JSONObject> aljObjT_RANGE;
		private ArrayList<JSONObject> aljObjTM_KUALITAS_PANEN;
		private ArrayList<JSONObject> aljObjTR_HS_REGION;
		private ArrayList<JSONObject> aljObjTR_HS_COMP;
		private ArrayList<JSONObject> aljObjTR_HS_EST;
		private ArrayList<JSONObject> aljObjTR_HS_AFD;
		private ArrayList<JSONObject> aljObjTR_HS_BLOCK;
		private ArrayList<JSONObject> aljObjTM_HS_ATTRIBUTE;
		private ArrayList<JSONObject> aljObjTM_POI;
		private ArrayList<JSONObject> aljObjTR_HS_LAND_USE_DETAIL;
		private ArrayList<JSONObject> aljObjTR_HS_LAND_USE;
		private ArrayList<JSONObject> aljObjTR_HS_SUB_BLOCK;
		private ArrayList<JSONObject> aljObjTR_HS_UNPLANTED;
		private ArrayList<JSONObject> aljObjTR_PALM;
		private ArrayList<JSONObject> aljObjTM_CONTENT_LABEL;
		private ArrayList<JSONObject> aljObjTM_SERVER;
		@Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Connection.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Sync... ");
            progressDialog.setMessage("Sedang diproses, mohon tunggu...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
            
            builder = new AlertDialog.Builder(Connection.this);
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            try {
                synchronized (this) {
                	System.out.println("isNetworkAvailable: "+isNetworkAvailable());
                	if(isNetworkAvailable() == true)
                    {
                		if (jsonTASK.equals("Install")) 
                		{
                			wurl = wurl+"sync-data/download-installation-data";
                			/*
                			// Added by Robin 20140814
							String URL = "";
							String URL_ENCODE = "";
							try {
								byte[] bs = URL.getBytes("UTF-8");
								URL_ENCODE = wurl + Base64.encodeBytes(bs);
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//
							*/
                			getTotalMaster();
                            sentJSONMaster();
                            if(sStatus.equals("YES"))
                            {
                            	if(!sNoData.equals(""))
                        		{
                        			Intent intent = new Intent(Connection.this, InstallationSetup.class);
                                	intent.putExtra("SyncResult", "NO");
                                	intent.putExtra("SyncMsg", getString(R.string.Sync_Failed)+" | "+sNoData+" EMPTY!");
                                	startActivity(intent);
                                    Connection.this.finish();  
                        		}
                            	else
                            	{
                            		if(Database.sharedObject.cekMasterData() == true)
                            		{
                                		Intent intent = new Intent(Connection.this, Database.class);
                                        intent.putExtra("MENU", "TO_LOGIN");
                                        startActivity(intent);
                                        Connection.this.finish(); 
                            		}
                            		else
                            		{
                                    	Intent intent = new Intent(Connection.this, InstallationSetup.class);
                                    	intent.putExtra("SyncResult", "NO");
                                    	intent.putExtra("SyncMsg", getString(R.string.Sync_Failed)+" | Data yang di download belum lengkap");
                                    	startActivity(intent);
                                        Connection.this.finish();  
                            		}
                            	}                     	
                            }
                            else
                            {
                            	Intent intent = new Intent(Connection.this, InstallationSetup.class);
                            	intent.putExtra("SyncResult", sStatus);
                            	intent.putExtra("SyncMsg", getString(R.string.Sync_Failed)+" | "+sMessage);
                            	startActivity(intent);
                                Connection.this.finish();  
                            }
                		}
                		else if (jsonTASK.equals("Check_Update")) 
                		{
                			wurl = wurl+"sync-data/download-updated-data";
                            publishProgress(10);
                            sentJSON();                  
                            if(sStatus.equals("YES"))
                            {
                            	parseJSONCheck();             	
                            }
                            else
                            {
                            	publishProgress(100);
                            	ot.setErrorReset("Tidak ada data yang perlu di Download");
                        		Intent intent = new Intent(Connection.this, MainMenu.class);
	               		        startActivity(intent);
	               		        Connection.this.finish();    
                            }
                		}
                		else if (jsonTASK.equals("Reset")) 
                		{
                			wurl = wurl+"sync-data/reset-password";
                            publishProgress(50);
                            mResetPassword();
                            sentJSON();
                            
                            if(sStatus.equals("YES"))
                            {
                            	ObjectTransfer ot = (ObjectTransfer) getApplication();
                                
                            	ArrayList<TM_LOGIN> alTM_LOGIN = new ArrayList<TM_LOGIN>();
                            	try {
                					JSONArray jaTM_LOGIN = jObj.getJSONArray("TM_LOGIN");
                					for (int i = 0; i < jaTM_LOGIN.length(); i++) {
                						JSONObject joTM_LOGIN = jaTM_LOGIN.getJSONObject(i);
                						alTM_LOGIN.add(new TM_LOGIN(joTM_LOGIN
                								.getString("EMPLOYEE_NIK"), joTM_LOGIN
                								.getString("PASSWORD"), joTM_LOGIN
                								.getString("DEFAULT_PASSWORD"), joTM_LOGIN
                								.getInt("LOG_LOGIN"), "NO", joTM_LOGIN
                								.getString("INSERT_TIME"), joTM_LOGIN
                								.getString("UPDATE_TIME")));
                					}
                				} catch (Exception e) {
                					e.printStackTrace();
                					sNoData = sNoData +" | "+ "TM_LOGIN";
                				}
                            	
                            	TM_LOGIN[] TM_LOGIN = new TM_LOGIN[alTM_LOGIN.size()];
                        		for(int i=0; i< alTM_LOGIN.size(); i++)
                        		{
                        			TM_LOGIN[i] = alTM_LOGIN.get(i);
                        		}

                                ot.setTm_login(TM_LOGIN);
                                
                                ArrayList<TM_USER_AUTH> alTM_USER_AUTH = new ArrayList<TM_USER_AUTH>();
                            	try {
                					JSONArray jaTM_USER_AUTH = jObj.getJSONArray("TM_USER_AUTH");
                					for (int i = 0; i < jaTM_USER_AUTH.length(); i++) {
                						JSONObject joTM_USER_AUTH = jaTM_USER_AUTH.getJSONObject(i);
                						alTM_USER_AUTH.add(new TM_USER_AUTH(joTM_USER_AUTH
                								.getString("EMPLOYEE_NIK"), joTM_USER_AUTH
                								.getString("USER_ROLE"), joTM_USER_AUTH
                								.getString("REFERENCE_ROLE"), joTM_USER_AUTH
                								.getString("LOCATION_CODE"), joTM_USER_AUTH
                								.getString("INSERT_USER"), joTM_USER_AUTH
                								.getString("INSERT_TIME"), joTM_USER_AUTH
                								.getString("UPDATE_USER"), joTM_USER_AUTH
                								.getString("UPDATE_TIME"), joTM_USER_AUTH
                								.getString("DELETE_USER"), joTM_USER_AUTH
                								.getString("DELETE_TIME")));
                					}
                				} catch (Exception e) {
                					e.printStackTrace();
                				}

                            	TM_USER_AUTH[] TM_USER_AUTH = new TM_USER_AUTH[alTM_USER_AUTH.size()];
                        		for(int i=0; i< alTM_USER_AUTH.size(); i++)
                        		{
                        			TM_USER_AUTH[i] = alTM_USER_AUTH.get(i);
                        		}

                                ot.setTm_user_auth(TM_USER_AUTH);
                                
                        		Intent intent = new Intent(Connection.this, Database.class);
                                intent.putExtra("MENU", "RESET_PASSWORD");
                                startActivity(intent);
                                Connection.this.finish();                  	
                            }
                            else
                            {
                            	ot.setErrorReset(getString(R.string.Sync_Failed)+ " | "+sMessage + " | Username yang anda masukan tidak sesuai'");
                                Intent intent = new Intent(Connection.this, Login.class);
                                startActivity(intent);
                                Connection.this.finish();  
                            }
                        }
                		else //for download data
                		{
                    		sNext = "NO";
                    		wurl = wurl+"sync-data/download-updated-data";
                    		wurlImage = wurlImage+"sync-data/download-image-file";  
                            publishProgress(10);
                            int loopProg=1;
            				System.out.println("sentJSON pertama");  
                			sentJSON();
                			if(jObj.getString("DATA").equals("YES"))
                			{
                    			do
                    			{
                                    if(this.isCancelled()) break;
                    				
                                    if (loopProg < 85) 
                                    {
    									publishProgress(10 + loopProg);
    								}
    								loopProg = loopProg + 1;
                                    
    								parseJSONDownload();                                	
                                	sentJSONforImage();
                                	
									try {
										sNext = jObj.getString("NEXT");
										System.out.println("sNext:" + sNext);
										if (sNext.equals("NO")) {
											publishProgress(100);
											Database.sharedObject.saveToDB();
											Database.sharedObject.saveTransaction();
											if (ot.getsStatus().contains("SUCCESS")) {
												/*
												System.out.println("sentJSON kedua");  
												sentJSON();
												
												if(sStatus.equals("YES"))
												{
													Intent intent = new Intent(Connection.this, Database.class);
												    intent.putExtra("MENU", "TO_MAIN_MENU");
												    startActivity(intent);
												    Connection.this.finish();
												}
												 */
												Intent intent = new Intent(
														Connection.this,
														Database.class);
												intent.putExtra("MENU",
														"TO_MAIN_MENU");
												startActivity(intent);
												Connection.this.finish();
											} else {
												ot.setErrorReset(getString(R.string.Msg_ins_Failed));
												Intent intent = new Intent(
														Connection.this,
														Login.class);
												startActivity(intent);
												Connection.this.finish();
											}
										} else {
											Database.sharedObject.saveToDB();
											Database.sharedObject.saveTransaction();

											if (ot.getsStatus().contains(
													"SUCCESS")) {
												System.out
														.println("sentJSON kedua");
												sentJSON();
											}
										}
									} catch (Exception e) {
										// TODO: handle exception org.json.JSONException: No value for NEXT
										this.cancel(true);
						    			
		                				ot.setErrorReset(getString(R.string.Download_not_Complete)+" | "+e);
		                            	Intent intent = new Intent(Connection.this, MainMenu.class);
			               		        startActivity(intent);
			               		        Connection.this.finish();  
									}
									
                    			} while (sNext.equals("YES"));
                			}
                			else
                			{
                				ot.setErrorReset(getString(R.string.Download_not_Complete));
                            	Intent intent = new Intent(Connection.this, MainMenu.class);
	               		        startActivity(intent);
	               		        Connection.this.finish();  
                			}
                		}
                    }/*
                		else //for download data
                		{
                    		sNext = "NO";
                    		wurl = wurl+"sync-data/download-updated-data";
                    		wurlImage = wurlImage+"sync-data/download-image-file";  
                            publishProgress(10);
                            int loopProg=1;
                			do
                			{

                                if(this.isCancelled()) break;
                				
                                if (loopProg < 85) 
                                {
									publishProgress(10 + loopProg);
								}
								loopProg = loopProg + 1;
                				System.out.println("sentJSON pertama");  
                    			sentJSON();
                                
                                if(sStatus.equals("YES"))
                                {
                                	parseJSONDownload();
                    				System.out.println("sentJSON kedua");  
                                	sentJSON();
                                	if(sStatus.equals("YES"))
                                    {               
                                    	sentJSONforImage();

										sNext = jObj.getString("NEXT");
System.out.println("sNext:"+sNext);  
										if(sNext.equals("NO"))
										{
											publishProgress(100);
	                                    	Intent intent = new Intent(Connection.this, Database.class);
	                                        intent.putExtra("MENU", "TO_MAIN_MENU");
	                                        startActivity(intent);
	                                        Connection.this.finish();
										}
										else
										{
											Database.sharedObject.saveToDB();
											Database.sharedObject.saveTransaction();
										}
                                    }
                                    else
                                    {
                                    	//Toast.makeText(Connection.this, R.string.error_connection, Toast.LENGTH_SHORT).show();
                    	                ot.setErrorReset(getString(R.string.Download_not_Complete));
                                    	
                                    	Intent intent = new Intent(Connection.this, MainMenu.class);
        	               		        startActivity(intent);
        	               		        Connection.this.finish();  
                                    } 
                                }
                                else
                                {
                                	ot.setErrorReset(getString(R.string.Download_not_Complete));
                                	Intent intent = new Intent(Connection.this, MainMenu.class);
    	               		        startActivity(intent);
    	               		        Connection.this.finish();    
                                }
                			} while (sNext.equals("YES"));
                		}
                    }*/
                    else
                    {
                    	if (jsonTASK.equals("Install")) 
                		{
                            Intent intent = new Intent(Connection.this, InstallationSetup.class);
                        	intent.putExtra("SyncResult", "0");
                        	intent.putExtra("SyncMsg", getString(R.string.Sync_Failed)+" | Jaringan tidak terkoneksi");
                        	startActivity(intent);
                            Connection.this.finish();  
                		}
                    	else if (jsonTASK.equals("Check_Update")) 
                		{
                    		ot.setErrorReset("Anda Belum Terhubung Dengan Jaringan Sehingga Tidak Dapat Melakukan Download Data.");
                    		Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
                		}
                		else if (jsonTASK.equals("Reset")) 
                		{
                			ot.setErrorReset(getString(R.string.Sync_Failed)+ " | "+getString(R.string.error_connection));
                        	Intent intent = new Intent(Connection.this, Login.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
                		}
                    	else // for download data
                		{
                    		Toast.makeText(Connection.this, R.string.error_connection, Toast.LENGTH_SHORT).show();
        	                Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
                		}
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
        }
        
        public void parseJSONDownload()
        {
        	ArrayList<TM_REGION> alTM_REGION = new ArrayList<TM_REGION>();
        	ArrayList<TM_COMP> alTM_COMP = new ArrayList<TM_COMP>();
        	ArrayList<TM_EST> alTM_EST = new ArrayList<TM_EST>();
        	ArrayList<TM_AFD> alTM_AFD = new ArrayList<TM_AFD>();
        	ArrayList<TM_BLOCK> alTM_BLOCK = new ArrayList<TM_BLOCK>();
        	ArrayList<TM_EMPLOYEE> alTM_EMPLOYEE = new ArrayList<TM_EMPLOYEE>();
        	ArrayList<TM_LOGIN> alTM_LOGIN = new ArrayList<TM_LOGIN>();
        	ArrayList<TM_USER_AUTH> alTM_USER_AUTH = new ArrayList<TM_USER_AUTH>();
        	ArrayList<TM_PARAMETER> alTM_PARAMETER = new ArrayList<TM_PARAMETER>();
        	ArrayList<TM_CONTENT_INSPECTION> alTM_CONTENT_INSPECTION = new ArrayList<TM_CONTENT_INSPECTION>();
        	ArrayList<T_RANGE> alT_RANGE = new ArrayList<T_RANGE>();
        	ArrayList<TM_KUALITAS_PANEN> alTM_KUALITAS_PANEN = new ArrayList<TM_KUALITAS_PANEN>();
        	ArrayList<TM_CONTENT_LABEL> alTM_CONTENT_LABEL = new ArrayList<TM_CONTENT_LABEL>();
        	
        	ArrayList<TR_HS_REGION> alTR_HS_REGION = new ArrayList<TR_HS_REGION>();
        	ArrayList<TR_HS_COMP> alTR_HS_COMP = new ArrayList<TR_HS_COMP>();
        	ArrayList<TR_HS_EST> alTR_HS_EST = new ArrayList<TR_HS_EST>();
        	ArrayList<TR_HS_AFD> alTR_HS_AFD = new ArrayList<TR_HS_AFD>();
        	ArrayList<TR_HS_BLOCK> alTR_HS_BLOCK = new ArrayList<TR_HS_BLOCK>();

        	ArrayList<TM_HS_ATTRIBUTE> alTM_HS_ATTRIBUTE = new ArrayList<TM_HS_ATTRIBUTE>();
        	ArrayList<TM_POI> alTM_POI = new ArrayList<TM_POI>();
        	ArrayList<TR_HS_LAND_USE_DETAIL> alTR_HS_LAND_USE_DETAIL = new ArrayList<TR_HS_LAND_USE_DETAIL>();
        	ArrayList<TR_HS_LAND_USE> alTR_HS_LAND_USE = new ArrayList<TR_HS_LAND_USE>();
        	ArrayList<TR_HS_SUB_BLOCK> alTR_HS_SUB_BLOCK = new ArrayList<TR_HS_SUB_BLOCK>();
        	ArrayList<TR_HS_UNPLANTED> alTR_HS_UNPLANTED = new ArrayList<TR_HS_UNPLANTED>();
        	ArrayList<TR_PALM> alTR_PALM = new ArrayList<TR_PALM>();

        	// Added by Robin 20140805
        	ArrayList<TR_FAC_TODOLIST> alTR_FAC_TODOLIST = new ArrayList<TR_FAC_TODOLIST>();
        	ArrayList<TR_INFRA_TODOLIST> alTR_INFRA_TODOLIST = new ArrayList<TR_INFRA_TODOLIST>();
        	ArrayList<TR_BLOCK_TODOLIST> alTR_BLOCK_TODOLIST = new ArrayList<TR_BLOCK_TODOLIST>();
        	ArrayList<TR_BLOCK_COLOR_REPORT> alTR_BLOCK_COLOR_REPORT = new ArrayList<TR_BLOCK_COLOR_REPORT>();
        	ArrayList<TR_INSPECTION_HISTORY> alTR_INSPECTION_HISTORY = new ArrayList<TR_INSPECTION_HISTORY>();
        	ArrayList<TR_IMAGE> alTR_IMAGE = new ArrayList<TR_IMAGE>();
        	ArrayList<TR_TODOLIST_HISTORY> alTR_TODOLIST_HISTORY = new ArrayList<TR_TODOLIST_HISTORY>();
        	ArrayList<TR_NEWS> alTR_NEWS = new ArrayList<TR_NEWS>();
        	ArrayList<TM_SERVER> alTM_SERVER = new ArrayList<TM_SERVER>();
        	//
        	
        	// added by Adit, 20140806
        	ArrayList<TR_PERFORMANCE_DAILY_QUALITY> alTR_PERFORMANCE_DAILY_QUALITY = new ArrayList<TR_PERFORMANCE_DAILY_QUALITY>();
        	ArrayList<TR_PERFORMANCE_DAILY_PINALTY> alTR_PERFORMANCE_DAILY_PINALTY = new ArrayList<TR_PERFORMANCE_DAILY_PINALTY>();
        	ArrayList<TR_PERFORMANCE_DAILY_DELIVERY> alTR_PERFORMANCE_DAILY_DELIVERY = new ArrayList<TR_PERFORMANCE_DAILY_DELIVERY>();
        	ArrayList<TR_PERFORMANCE_PRODUCTIVITY> alTR_PERFORMANCE_PRODUCTIVITY = new ArrayList<TR_PERFORMANCE_PRODUCTIVITY>();
        	ArrayList<TR_PERFORMANCE_ESTATE_PRODUCTION> alTR_PERFORMANCE_ESTATE_PRODUCTION = new ArrayList<TR_PERFORMANCE_ESTATE_PRODUCTION>();
        	ArrayList<TR_PERFORMANCE_DAILY_HARV> alTR_PERFORMANCE_DAILY_HARV = new ArrayList<TR_PERFORMANCE_DAILY_HARV>();
        	ArrayList<TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK> alTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK = new ArrayList<TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK>();
        	ArrayList<T_DELIVERY_FAVORITE> alT_DELIVERY_FAVORITE = new ArrayList<T_DELIVERY_FAVORITE>();
        	ArrayList<TR_BLOCK_INSPECTION> alTR_BLOCK_INSPECTION = new ArrayList<TR_BLOCK_INSPECTION>();
        	ArrayList<TR_BLOCK_INDICATOR> alTR_BLOCK_INDICATOR = new ArrayList<TR_BLOCK_INDICATOR>();
        	ArrayList<TR_AREAL_INSPECTION> alTR_AREAL_INSPECTION = new ArrayList<TR_AREAL_INSPECTION>();
        	ArrayList<TR_POKOK_INSPECTION> alTR_POKOK_INSPECTION = new ArrayList<TR_POKOK_INSPECTION>();
        	ArrayList<TR_CONTENT_VALUE> alTR_CONTENT_VALUE = new ArrayList<TR_CONTENT_VALUE>();
        	ArrayList<TR_CONTENT_INDICATOR_SUM> alTR_CONTENT_INDICATOR_SUM = new ArrayList<TR_CONTENT_INDICATOR_SUM>();
        	ArrayList<TR_CONTENT_LOSSES_SUM> alTR_CONTENT_LOSSES_SUM = new ArrayList<TR_CONTENT_LOSSES_SUM>();
        	//
        	
        	try
        	{
        		try {
					JSONArray jaTM_SERVER = jObj
							.getJSONArray("TM_SERVER");
					for (int i = 0; i < jaTM_SERVER.length(); i++) {
						JSONObject joTM_SERVER = jaTM_SERVER
								.getJSONObject(i);
						alTM_SERVER.add(new TM_SERVER(joTM_SERVER
								.getString("COMP_CODE"), joTM_SERVER
								.getString("TYPE"), joTM_SERVER
								.getString("SERVER_PATH"), joTM_SERVER
								.getString("START_VALID"), joTM_SERVER
								.getString("END_VALID"), joTM_SERVER
								.getString("INSERT_USER"), joTM_SERVER
								.getString("INSERT_TIME"), joTM_SERVER
								.getString("UPDATE_USER"), joTM_SERVER
								.getString("UPDATE_TIME")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		
        		try {
					JSONArray jaTR_HS_REGION = jObj
							.getJSONArray("TR_HS_REGION");
					for (int i = 0; i < jaTR_HS_REGION.length(); i++) {
						JSONObject joTR_HS_REGION = jaTR_HS_REGION
								.getJSONObject(i);
						alTR_HS_REGION.add(new TR_HS_REGION(joTR_HS_REGION
								.getString("NATIONAL"), joTR_HS_REGION
								.getString("REGION_CODE"), joTR_HS_REGION
								.getString("SPMON"), joTR_HS_REGION
								.getInt("HA_SAP"), joTR_HS_REGION
								.getInt("HA_PLANTED_SAP"), joTR_HS_REGION
								.getInt("HA_UNPLANTED_SAP"), joTR_HS_REGION
								.getInt("PALM_SAP"), joTR_HS_REGION
								.getInt("SPH_SAP"), joTR_HS_REGION
								.getInt("HA_GIS"), joTR_HS_REGION
								.getInt("HA_PLANTED_GIS"), joTR_HS_REGION
								.getInt("HA_UNPLANTED_GIS"), joTR_HS_REGION
								.getInt("PALM_GIS"), joTR_HS_REGION
								.getInt("SPH_GIS"), joTR_HS_REGION
								.getInt("JML_COMP"), joTR_HS_REGION
								.getInt("JML_EST"), joTR_HS_REGION
								.getInt("JML_AFD"), joTR_HS_REGION
								.getInt("JML_BLOCK"), joTR_HS_REGION
								.getString("GEOM"), joTR_HS_REGION
								.getString("INSERT_TIME_DW"), joTR_HS_REGION
								.getString("UPDATE_TIME_DW"), joTR_HS_REGION
								.getInt("HA_LC_SAP"), joTR_HS_REGION
								.getInt("HA_LC_GIS"), joTR_HS_REGION
								.getInt("JML_SUB_BLOCK"), joTR_HS_REGION
								.getInt("JML_BLOCK_LC"), joTR_HS_REGION
								.getInt("HA_TM_SAP"), joTR_HS_REGION
								.getInt("HA_TBM_SAP")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(2);
				try {
					JSONArray jaTR_HS_COMP = jObj.getJSONArray("TR_HS_COMP");
					for (int i = 0; i < jaTR_HS_COMP.length(); i++) {
						JSONObject joTR_HS_COMP = jaTR_HS_COMP.getJSONObject(i);
						alTR_HS_COMP.add(new TR_HS_COMP(joTR_HS_COMP
								.getString("NATIONAL"), joTR_HS_COMP
								.getString("REGION_CODE"), joTR_HS_COMP
								.getString("COMP_CODE"), joTR_HS_COMP
								.getString("SPMON"), joTR_HS_COMP
								.getInt("HA_SAP"), joTR_HS_COMP
								.getInt("HA_PLANTED_SAP"), joTR_HS_COMP
								.getInt("HA_UNPLANTED_SAP"), joTR_HS_COMP
								.getInt("PALM_SAP"), joTR_HS_COMP
								.getInt("SPH_SAP"), joTR_HS_COMP
								.getInt("HA_GIS"), joTR_HS_COMP
								.getInt("HA_PLANTED_GIS"), joTR_HS_COMP
								.getInt("HA_UNPLANTED_GIS"), joTR_HS_COMP
								.getInt("PALM_GIS"), joTR_HS_COMP
								.getInt("SPH_GIS"), joTR_HS_COMP
								.getInt("JML_AFD"), joTR_HS_COMP
								.getInt("JML_BLOCK"), joTR_HS_COMP
								.getString("GEOM"), joTR_HS_COMP
								.getString("INSERT_TIME_DW"), joTR_HS_COMP
								.getString("UPDATE_TIME_DW"), joTR_HS_COMP
								.getInt("HA_LC_SAP"), joTR_HS_COMP
								.getInt("HA_LC_GIS"), joTR_HS_COMP
								.getInt("JML_SUB_BLOCK"), joTR_HS_COMP
								.getInt("JML_BLOCK_LC"), joTR_HS_COMP
								.getInt("HA_TM_SAP"), joTR_HS_COMP
								.getInt("HA_TBM_SAP")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(4);
				try {
					JSONArray jaTR_HS_EST = jObj.getJSONArray("TR_HS_EST");
					for (int i = 0; i < jaTR_HS_EST.length(); i++) {
						JSONObject joTR_HS_EST = jaTR_HS_EST.getJSONObject(i);
						alTR_HS_EST.add(new TR_HS_EST(joTR_HS_EST
								.getString("NATIONAL"), joTR_HS_EST
								.getString("REGION_CODE"), joTR_HS_EST
								.getString("COMP_CODE"), joTR_HS_EST
								.getString("EST_CODE"), joTR_HS_EST
								.getString("WERKS"), joTR_HS_EST
								.getString("SPMON"), joTR_HS_EST
								.getInt("HA_SAP"), joTR_HS_EST
								.getInt("HA_PLANTED_SAP"), joTR_HS_EST
								.getInt("HA_UNPLANTED_SAP"), joTR_HS_EST
								.getInt("PALM_SAP"), joTR_HS_EST
								.getInt("SPH_SAP"), joTR_HS_EST
								.getInt("HA_GIS"), joTR_HS_EST
								.getInt("HA_PLANTED_GIS"), joTR_HS_EST
								.getInt("HA_UNPLANTED_GIS"), joTR_HS_EST
								.getInt("PALM_GIS"), joTR_HS_EST
								.getInt("SPH_GIS"), joTR_HS_EST
								.getInt("JML_AFD"), joTR_HS_EST
								.getInt("JML_BLOCK"), joTR_HS_EST
								.getString("GEOM"), joTR_HS_EST
								.getString("INSERT_TIME_DW"), joTR_HS_EST
								.getString("UPDATE_TIME_DW"), joTR_HS_EST
								.getInt("HA_LC_SAP"), joTR_HS_EST
								.getInt("HA_LC_GIS"), joTR_HS_EST
								.getInt("JML_SUB_BLOCK"), joTR_HS_EST
								.getInt("JML_BLOCK_LC"), joTR_HS_EST
								.getInt("HA_TM_SAP"), joTR_HS_EST
								.getInt("HA_TBM_SAP")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(6);
				try {
					JSONArray jaTR_HS_AFD = jObj.getJSONArray("TR_HS_AFD");
					for (int i = 0; i < jaTR_HS_AFD.length(); i++) {
						JSONObject joTR_HS_AFD = jaTR_HS_AFD.getJSONObject(i);
						alTR_HS_AFD.add(new TR_HS_AFD(joTR_HS_AFD
								.getString("NATIONAL"), joTR_HS_AFD
								.getString("REGION_CODE"), joTR_HS_AFD
								.getString("COMP_CODE"), joTR_HS_AFD
								.getString("EST_CODE"), joTR_HS_AFD
								.getString("WERKS"), joTR_HS_AFD
								.getString("SUB_BA_CODE"), joTR_HS_AFD
								.getString("KEBUN_CODE"), joTR_HS_AFD
								.getString("AFD_CODE"), joTR_HS_AFD
								.getString("AFD_NAME"), joTR_HS_AFD
								.getString("AFD_CODE_GIS"), joTR_HS_AFD
								.getString("SPMON"), joTR_HS_AFD
								.getInt("HA_SAP"), joTR_HS_AFD
								.getInt("HA_PLANTED_SAP"), joTR_HS_AFD
								.getInt("HA_UNPLANTED_SAP"), joTR_HS_AFD
								.getInt("PALM_SAP"), joTR_HS_AFD
								.getInt("SPH_SAP"), joTR_HS_AFD
								.getInt("HA_GIS"), joTR_HS_AFD
								.getInt("HA_PLANTED_GIS"), joTR_HS_AFD
								.getInt("HA_UNPLANTED_GIS"), joTR_HS_AFD
								.getInt("PALM_GIS"), joTR_HS_AFD
								.getInt("SPH_GIS"), joTR_HS_AFD
								.getInt("JML_BLOCK"), joTR_HS_AFD
								.getString("GEOM"), joTR_HS_AFD
								.getString("INSERT_TIME_DW"), joTR_HS_AFD
								.getString("UPDATE_TIME_DW"), joTR_HS_AFD
								.getInt("HA_LC_SAP"), joTR_HS_AFD
								.getInt("HA_LC_GIS"), joTR_HS_AFD
								.getInt("JML_SUB_BLOCK"), joTR_HS_AFD
								.getInt("JML_BLOCK_LC"), joTR_HS_AFD
								.getInt("HA_TM_SAP"), joTR_HS_AFD
								.getInt("HA_TBM_SAP")
								));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(6);
				try {
					JSONArray jaTR_HS_BLOCK = jObj.getJSONArray("TR_HS_BLOCK");
					for (int i = 0; i < jaTR_HS_BLOCK.length(); i++) {
						JSONObject joTR_HS_BLOCK = jaTR_HS_BLOCK
								.getJSONObject(i);
						alTR_HS_BLOCK.add(new TR_HS_BLOCK(joTR_HS_BLOCK
								.getString("NATIONAL"), joTR_HS_BLOCK
								.getString("REGION_CODE"), joTR_HS_BLOCK
								.getString("COMP_CODE"), joTR_HS_BLOCK
								.getString("EST_CODE"), joTR_HS_BLOCK
								.getString("WERKS"), joTR_HS_BLOCK
								.getString("SUB_BA_CODE"), joTR_HS_BLOCK
								.getString("KEBUN_CODE"), joTR_HS_BLOCK
								.getString("AFD_CODE"), joTR_HS_BLOCK
								.getString("AFD_NAME"), joTR_HS_BLOCK
								.getString("BLOCK_CODE"), joTR_HS_BLOCK
								.getString("BLOCK_NAME"), joTR_HS_BLOCK
								.getString("BLOCK_CODE_GIS"), joTR_HS_BLOCK
								.getString("SPMON"), joTR_HS_BLOCK
								.getInt("HA_SAP"), joTR_HS_BLOCK
								.getInt("HA_PLANTED_SAP"), joTR_HS_BLOCK
								.getInt("HA_UNPLANTED_SAP"), joTR_HS_BLOCK
								.getInt("PALM_SAP"), joTR_HS_BLOCK
								.getInt("SPH_SAP"), joTR_HS_BLOCK
								.getInt("HA_GIS"), joTR_HS_BLOCK
								.getInt("HA_PLANTED_GIS"), joTR_HS_BLOCK
								.getInt("HA_UNPLANTED_GIS"), joTR_HS_BLOCK
								.getInt("PALM_GIS"), joTR_HS_BLOCK
								.getInt("SPH_GIS"), joTR_HS_BLOCK
								.getInt("JML_SUB_BLOCK"), joTR_HS_BLOCK
								.getInt("JML_BLOCK_LC"), joTR_HS_BLOCK
								.getString("GEOM"), joTR_HS_BLOCK
								.getString("INSERT_TIME_DW"), joTR_HS_BLOCK
								.getString("UPDATE_TIME_DW"), joTR_HS_BLOCK
								.getInt("HA_LC_SAP"), joTR_HS_BLOCK
								.getInt("HA_LC_GIS"), joTR_HS_BLOCK
								.getInt("HA_TM_SAP"), joTR_HS_BLOCK
								.getInt("HA_TBM_SAP")
								));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(7);
				try {
					JSONArray jaTM_HS_ATTRIBUTE = jObj
							.getJSONArray("TM_HS_ATTRIBUTE");
					for (int i = 0; i < jaTM_HS_ATTRIBUTE.length(); i++) {
						JSONObject joTM_HS_ATTRIBUTE = jaTM_HS_ATTRIBUTE
								.getJSONObject(i);
						alTM_HS_ATTRIBUTE.add(new TM_HS_ATTRIBUTE(
								joTM_HS_ATTRIBUTE.getString("NATIONAL"),
								joTM_HS_ATTRIBUTE.getString("REGION_CODE"),
								joTM_HS_ATTRIBUTE.getString("COMP_CODE"),
								joTM_HS_ATTRIBUTE.getString("EST_CODE"),
								joTM_HS_ATTRIBUTE.getString("WERKS"),
								joTM_HS_ATTRIBUTE.getString("SUB_BA_CODE"),
								joTM_HS_ATTRIBUTE.getString("KEBUN_CODE"),
								joTM_HS_ATTRIBUTE.getString("AFD_CODE"),
								joTM_HS_ATTRIBUTE.getString("BLOCK_CODE"),
								joTM_HS_ATTRIBUTE.getString("START_VALID"),
								joTM_HS_ATTRIBUTE.getString("END_VALID"),
								joTM_HS_ATTRIBUTE.getString("BLOCK_NAME"),
								joTM_HS_ATTRIBUTE.getString("LAND_TYPE"),
								joTM_HS_ATTRIBUTE.getString("REPLANT"),
								joTM_HS_ATTRIBUTE.getString("BLOCK_TYPE"),
								joTM_HS_ATTRIBUTE.getString("TOPOGRAPHY"),
								joTM_HS_ATTRIBUTE.getString("PROGENY"),
								joTM_HS_ATTRIBUTE.getString("LAND_SUIT"),
								joTM_HS_ATTRIBUTE.getString("YEAR_PLAN"),
								joTM_HS_ATTRIBUTE.getString("BY_PLAN"),
								joTM_HS_ATTRIBUTE.getString("INIT_PERIOD"),
								joTM_HS_ATTRIBUTE.getString("MAINT_PERIOD"),
								joTM_HS_ATTRIBUTE.getString("SH_PERIOD"),
								joTM_HS_ATTRIBUTE.getString("HARV_PERIOD"),
								joTM_HS_ATTRIBUTE.getString("INSERT_TIME_DW"),
								joTM_HS_ATTRIBUTE.getString("UPDATE_TIME_DW")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(7);
				try {
					JSONArray jaTM_POI = jObj.getJSONArray("TM_POI");
					for (int i = 0; i < jaTM_POI.length(); i++) {
						JSONObject joTM_POI = jaTM_POI.getJSONObject(i);
						alTM_POI.add(new TM_POI(joTM_POI.getString("NATIONAL"),
								joTM_POI.getString("REGION_CODE"), joTM_POI
										.getString("COMP_CODE"), joTM_POI
										.getString("EST_CODE"), joTM_POI
										.getString("WERKS"), joTM_POI
										.getString("SUB_BA_CODE"), joTM_POI
										.getString("KEBUN_CODE"), joTM_POI
										.getString("AFD_CODE"), joTM_POI
										.getString("AFD_NAME"), joTM_POI
										.getString("BLOCK_CODE"), joTM_POI
										.getString("BLOCK_NAME"), joTM_POI
										.getString("BLOCK_CODE_GIS"), joTM_POI
										.getString("POI_CODE"), joTM_POI
										.getString("POI_TYPE"), joTM_POI
										.getString("POI_NAME"), joTM_POI
										.getString("POI_ATTR"), joTM_POI
										.getString("GEOM"), joTM_POI
										.getString("INSERT_TIME_DW"), joTM_POI
										.getString("UPDATE_TIME_DW"), joTM_POI
										.getString("POI_CATEGORY")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(7);
				try {
					JSONArray jaTR_HS_LAND_USE_DETAIL = jObj
							.getJSONArray("TR_HS_LAND_USE_DETAIL");
					for (int i = 0; i < jaTR_HS_LAND_USE_DETAIL.length(); i++) {
						JSONObject joTR_HS_LAND_USE_DETAIL = jaTR_HS_LAND_USE_DETAIL
								.getJSONObject(i);
						alTR_HS_LAND_USE_DETAIL
								.add(new TR_HS_LAND_USE_DETAIL(
										joTR_HS_LAND_USE_DETAIL
												.getString("NATIONAL"),
										joTR_HS_LAND_USE_DETAIL
												.getString("REGION_CODE"),
										joTR_HS_LAND_USE_DETAIL
												.getString("COMP_CODE"),
										joTR_HS_LAND_USE_DETAIL
												.getString("EST_CODE"),
										joTR_HS_LAND_USE_DETAIL
												.getString("WERKS"),
										joTR_HS_LAND_USE_DETAIL
												.getString("SUB_BA_CODE"),
										joTR_HS_LAND_USE_DETAIL
												.getString("KEBUN_CODE"),
										joTR_HS_LAND_USE_DETAIL
												.getString("AFD_CODE"),
										joTR_HS_LAND_USE_DETAIL
												.getString("AFD_NAME"),
										joTR_HS_LAND_USE_DETAIL
												.getString("BLOCK_CODE"),
										joTR_HS_LAND_USE_DETAIL
												.getString("BLOCK_NAME"),
										joTR_HS_LAND_USE_DETAIL
												.getString("BLOCK_CODE_GIS"),
										joTR_HS_LAND_USE_DETAIL
												.getString("LAND_USE_CODE"),
										joTR_HS_LAND_USE_DETAIL
												.getString("LAND_USE_NAME"),
										joTR_HS_LAND_USE_DETAIL
												.getString("LAND_USE_CODE_GIS"),
										joTR_HS_LAND_USE_DETAIL
												.getString("SPMON"),
										joTR_HS_LAND_USE_DETAIL
												.getString("LAND_CAT"),
										joTR_HS_LAND_USE_DETAIL
												.getString("LAND_CAT_L1_CODE"),
										joTR_HS_LAND_USE_DETAIL
												.getString("LAND_CAT_L1"),
										joTR_HS_LAND_USE_DETAIL
												.getString("LAND_CAT_L2_CODE"),
										joTR_HS_LAND_USE_DETAIL
												.getString("LAND_CAT_L2"),
										joTR_HS_LAND_USE_DETAIL
												.getString("MATURITY_STATUS"),
										joTR_HS_LAND_USE_DETAIL
												.getString("SCOUT_STATUS"),
										joTR_HS_LAND_USE_DETAIL.getInt("AGES"),
										joTR_HS_LAND_USE_DETAIL
												.getInt("HA_SAP"),
										joTR_HS_LAND_USE_DETAIL
												.getInt("PALM_SAP"),
										joTR_HS_LAND_USE_DETAIL
												.getInt("SPH_GIS"),
										joTR_HS_LAND_USE_DETAIL
												.getInt("PANJANG"),
										joTR_HS_LAND_USE_DETAIL
												.getString("GEOM"),
										joTR_HS_LAND_USE_DETAIL
												.getString("INSERT_TIME_DW"),
										joTR_HS_LAND_USE_DETAIL
												.getString("UPDATE_TIME_DW"),
										joTR_HS_LAND_USE_DETAIL.getInt("HA_GIS"),
										joTR_HS_LAND_USE_DETAIL.getInt("PALM_GIS"),
										joTR_HS_LAND_USE_DETAIL.getInt("SPH_SAP")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(8);
				try {
					JSONArray jaTR_HS_LAND_USE = jObj
							.getJSONArray("TR_HS_LAND_USE");
					for (int i = 0; i < jaTR_HS_LAND_USE.length(); i++) {
						JSONObject joTR_HS_LAND_USE = jaTR_HS_LAND_USE
								.getJSONObject(i);
						alTR_HS_LAND_USE
								.add(new TR_HS_LAND_USE(
										joTR_HS_LAND_USE.getString("NATIONAL"),
										joTR_HS_LAND_USE
												.getString("REGION_CODE"),
										joTR_HS_LAND_USE.getString("COMP_CODE"),
										joTR_HS_LAND_USE.getString("EST_CODE"),
										joTR_HS_LAND_USE.getString("WERKS"),
										joTR_HS_LAND_USE
												.getString("SUB_BA_CODE"),
										joTR_HS_LAND_USE
												.getString("KEBUN_CODE"),
										joTR_HS_LAND_USE.getString("AFD_CODE"),
										joTR_HS_LAND_USE.getString("AFD_NAME"),
										joTR_HS_LAND_USE
												.getString("BLOCK_CODE"),
										joTR_HS_LAND_USE
												.getString("BLOCK_NAME"),
										joTR_HS_LAND_USE
												.getString("BLOCK_CODE_GIS"),
										joTR_HS_LAND_USE
												.getString("LAND_USE_CODE"),
										joTR_HS_LAND_USE
												.getString("LAND_USE_NAME"),
										joTR_HS_LAND_USE
												.getString("LAND_USE_CODE_GIS"),
										joTR_HS_LAND_USE.getString("SPMON"),
										joTR_HS_LAND_USE.getString("LAND_CAT"),
										joTR_HS_LAND_USE
												.getString("LAND_CAT_L1_CODE"),
										joTR_HS_LAND_USE
												.getString("LAND_CAT_L1"),
										joTR_HS_LAND_USE
												.getString("LAND_CAT_L2_CODE"),
										joTR_HS_LAND_USE
												.getString("LAND_CAT_L2"),
										joTR_HS_LAND_USE
												.getString("MATURITY_STATUS"),
										joTR_HS_LAND_USE
												.getString("SCOUT_STATUS"),
										joTR_HS_LAND_USE.getInt("AGES"),
										joTR_HS_LAND_USE.getInt("HA_SAP"),
										joTR_HS_LAND_USE.getInt("PALM_SAP"),
										joTR_HS_LAND_USE.getInt("SPH_GIS"),
										joTR_HS_LAND_USE.getString("GEOM"),
										joTR_HS_LAND_USE
												.getString("INSERT_TIME_DW"),
										joTR_HS_LAND_USE
												.getString("UPDATE_TIME_DW"),
										joTR_HS_LAND_USE.getInt("HA_GIS"),
										joTR_HS_LAND_USE.getInt("PALM_GIS"),
										joTR_HS_LAND_USE.getInt("SPH_SAP")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(8);
				try {
					JSONArray jaTR_HS_SUB_BLOCK = jObj
							.getJSONArray("TR_HS_SUB_BLOCK");
					for (int i = 0; i < jaTR_HS_SUB_BLOCK.length(); i++) {
						JSONObject joTR_HS_SUB_BLOCK = jaTR_HS_SUB_BLOCK
								.getJSONObject(i);

						alTR_HS_SUB_BLOCK
								.add(new TR_HS_SUB_BLOCK(
										joTR_HS_SUB_BLOCK.getString("NATIONAL"),
										joTR_HS_SUB_BLOCK
												.getString("REGION_CODE"),
										joTR_HS_SUB_BLOCK
												.getString("COMP_CODE"),
										joTR_HS_SUB_BLOCK.getString("EST_CODE"),
										joTR_HS_SUB_BLOCK.getString("WERKS"),
										joTR_HS_SUB_BLOCK
												.getString("SUB_BA_CODE"),
										joTR_HS_SUB_BLOCK
												.getString("KEBUN_CODE"),
										joTR_HS_SUB_BLOCK.getString("AFD_CODE"),
										joTR_HS_SUB_BLOCK.getString("AFD_NAME"),
										joTR_HS_SUB_BLOCK
												.getString("BLOCK_CODE"),
										joTR_HS_SUB_BLOCK
												.getString("BLOCK_NAME"),
										joTR_HS_SUB_BLOCK
												.getString("BLOCK_CODE_GIS"),
										joTR_HS_SUB_BLOCK
												.getString("SUB_BLOCK_CODE"),
										joTR_HS_SUB_BLOCK
												.getString("SUB_BLOCK_NAME"),
										joTR_HS_SUB_BLOCK
												.getString("LAND_USE_CODE_GIS"),
										joTR_HS_SUB_BLOCK.getString("SPMON"),
										joTR_HS_SUB_BLOCK.getString("LAND_CAT"),
										joTR_HS_SUB_BLOCK
												.getString("LAND_CAT_L1_CODE"),
										joTR_HS_SUB_BLOCK
												.getString("LAND_CAT_L1"),
										joTR_HS_SUB_BLOCK
												.getString("LAND_CAT_L2_CODE"),
										joTR_HS_SUB_BLOCK
												.getString("LAND_CAT_L2"),
										joTR_HS_SUB_BLOCK
												.getString("MATURITY_STATUS"),
										joTR_HS_SUB_BLOCK
												.getString("SCOUT_STATUS"),
										joTR_HS_SUB_BLOCK.getInt("AGES"),
										joTR_HS_SUB_BLOCK.getInt("HA_SAP"),
										joTR_HS_SUB_BLOCK.getInt("PALM_SAP"),
										joTR_HS_SUB_BLOCK.getInt("SPH_SAP"),
										joTR_HS_SUB_BLOCK.getInt("HA_GIS"),
										joTR_HS_SUB_BLOCK.getInt("PALM_GIS"),
										joTR_HS_SUB_BLOCK.getInt("SPH_GIS"),
										joTR_HS_SUB_BLOCK
												.getString("INSERT_TIME_DW"),
										joTR_HS_SUB_BLOCK
												.getString("UPDATE_TIME_DW")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(9);
				try {
					JSONArray jaTR_HS_UNPLANTED = jObj
							.getJSONArray("TR_HS_UNPLANTED");
					for (int i = 0; i < jaTR_HS_UNPLANTED.length(); i++) {
						JSONObject joTR_HS_UNPLANTED = jaTR_HS_UNPLANTED
								.getJSONObject(i);
						alTR_HS_UNPLANTED
								.add(new TR_HS_UNPLANTED(
										joTR_HS_UNPLANTED.getString("NATIONAL"),
										joTR_HS_UNPLANTED
												.getString("REGION_CODE"),
										joTR_HS_UNPLANTED
												.getString("COMP_CODE"),
										joTR_HS_UNPLANTED.getString("EST_CODE"),
										joTR_HS_UNPLANTED.getString("WERKS"),
										joTR_HS_UNPLANTED
												.getString("SUB_BA_CODE"),
										joTR_HS_UNPLANTED
												.getString("KEBUN_CODE"),
										joTR_HS_UNPLANTED.getString("AFD_CODE"),
										joTR_HS_UNPLANTED.getString("AFD_NAME"),
										joTR_HS_UNPLANTED
												.getString("AFD_CODE_GIS"),
										joTR_HS_UNPLANTED.getString("SPMON"),
										joTR_HS_UNPLANTED.getString("LAND_CAT"),
										joTR_HS_UNPLANTED
												.getString("LAND_CAT_L1_CODE"),
										joTR_HS_UNPLANTED
												.getString("LAND_CAT_L1"),
										joTR_HS_UNPLANTED
												.getString("LAND_CAT_L2_CODE"),
										joTR_HS_UNPLANTED
												.getString("LAND_CAT_L2"),
										joTR_HS_UNPLANTED.getInt("HA_SAP"),
										joTR_HS_UNPLANTED.getInt("HA_GIS"),
										joTR_HS_UNPLANTED
												.getString("INSERT_TIME_DW"),
										joTR_HS_UNPLANTED
												.getString("UPDATE_TIME_DW")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(9);
				try {
					JSONArray jaTR_PALM = jObj.getJSONArray("TR_PALM");
					for (int i = 0; i < jaTR_PALM.length(); i++) {
						JSONObject joTR_PALM = jaTR_PALM.getJSONObject(i);

						alTR_PALM.add(new TR_PALM(joTR_PALM
								.getString("NATIONAL"), joTR_PALM
								.getString("REGION_CODE"), joTR_PALM
								.getString("COMP_CODE"), joTR_PALM
								.getString("EST_CODE"), joTR_PALM
								.getString("WERKS"), joTR_PALM
								.getString("SUB_BA_CODE"), joTR_PALM
								.getString("KEBUN_CODE"), joTR_PALM
								.getString("AFD_CODE"), joTR_PALM
								.getString("AFD_NAME"), joTR_PALM
								.getString("BLOCK_CODE"), joTR_PALM
								.getString("BLOCK_NAME"), joTR_PALM
								.getString("BLOCK_CODE_GIS"), joTR_PALM
								.getString("LAND_USE_CODE_GIS"), joTR_PALM
								.getString("SPMON"), joTR_PALM.getInt("PALM"),
								joTR_PALM.getString("GEOM"), joTR_PALM
										.getString("INSERT_TIME_DW"), joTR_PALM
										.getString("UPDATE_TIME_DW")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(10);
				try {
					JSONArray jaTM_REGION = jObj.getJSONArray("TM_REGION");
					for (int i = 0; i < jaTM_REGION.length(); i++) {
						JSONObject joTM_REGION = jaTM_REGION.getJSONObject(i);
						alTM_REGION.add(new TM_REGION(joTM_REGION
								.getString("NATIONAL"), joTM_REGION
								.getString("REGION_CODE"), joTM_REGION
								.getString("REGION_NAME"), joTM_REGION
								.getString("INSERT_TIME_DW"), joTM_REGION
								.getString("UPDATE_TIME_DW")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(11);
				try {
					JSONArray jaTM_COMPANY = jObj.getJSONArray("TM_COMP");
					for (int i = 0; i < jaTM_COMPANY.length(); i++) {
						JSONObject joTM_COMPANY = jaTM_COMPANY.getJSONObject(i);
						alTM_COMP.add(new TM_COMP(joTM_COMPANY
								.getString("NATIONAL"), joTM_COMPANY
								.getString("REGION_CODE"), joTM_COMPANY
								.getString("COMP_CODE"), joTM_COMPANY
								.getString("COMP_NAME"), joTM_COMPANY
								.getString("ADDRESS"), joTM_COMPANY
								.getString("INSERT_TIME_DW"), joTM_COMPANY
								.getString("UPDATE_TIME_DW")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(12);
				try {
					JSONArray jaTM_EST = jObj.getJSONArray("TM_EST");
					for (int i = 0; i < jaTM_EST.length(); i++) {
						JSONObject joTM_EST = jaTM_EST.getJSONObject(i);
						alTM_EST.add(new TM_EST(joTM_EST
								.getString("NATIONAL"), joTM_EST
								.getString("REGION_CODE"), joTM_EST
								.getString("COMP_CODE"), joTM_EST
								.getString("EST_CODE"), joTM_EST
								.getString("WERKS"), joTM_EST
								.getString("EST_NAME"), joTM_EST
								.getString("START_VALID"), joTM_EST
								.getString("END_VALID"), joTM_EST
								.getString("INSERT_TIME_DW"), joTM_EST
								.getString("UPDATE_TIME_DW")));
					}
				} catch (Exception e) {
					e.printStackTrace();
					e.printStackTrace();
				}
        		//publishProgress(13);
				try {
					JSONArray jaTM_AFD = jObj.getJSONArray("TM_AFD");
					for (int i = 0; i < jaTM_AFD.length(); i++) {
						JSONObject joTM_AFD = jaTM_AFD.getJSONObject(i);
						alTM_AFD.add(new TM_AFD(joTM_AFD.getString("NATIONAL"),
								joTM_AFD.getString("REGION_CODE"), joTM_AFD
										.getString("COMP_CODE"), joTM_AFD
										.getString("EST_CODE"), joTM_AFD
										.getString("WERKS"), joTM_AFD
										.getString("SUB_BA_CODE"), joTM_AFD
										.getString("KEBUN_CODE"), joTM_AFD
										.getString("AFD_CODE"), joTM_AFD
										.getString("AFD_NAME"), joTM_AFD
										.getString("AFD_CODE_GIS"), joTM_AFD
										.getString("START_VALID"), joTM_AFD
										.getString("END_VALID"), joTM_AFD
										.getString("INSERT_TIME_DW"), joTM_AFD
										.getString("UPDATE_TIME_DW")));
					}
				} catch (Exception e) {
					e.printStackTrace();
					e.printStackTrace();
				}
        		//publishProgress(14);
				try {
					JSONArray jaTM_BLOCK = jObj.getJSONArray("TM_BLOCK");
					for (int i = 0; i < jaTM_BLOCK.length(); i++) {
						JSONObject joTM_BLOCK = jaTM_BLOCK.getJSONObject(i);
						alTM_BLOCK.add(new TM_BLOCK(joTM_BLOCK
								.getString("NATIONAL"), joTM_BLOCK
								.getString("REGION_CODE"), joTM_BLOCK
								.getString("COMP_CODE"), joTM_BLOCK
								.getString("EST_CODE"), joTM_BLOCK
								.getString("WERKS"), joTM_BLOCK
								.getString("SUB_BA_CODE"), joTM_BLOCK
								.getString("KEBUN_CODE"), joTM_BLOCK
								.getString("AFD_CODE"), joTM_BLOCK
								.getString("BLOCK_CODE"), joTM_BLOCK
								.getString("BLOCK_NAME"), joTM_BLOCK
								.getString("BLOCK_CODE_GIS"), joTM_BLOCK
								.getString("START_VALID"), joTM_BLOCK
								.getString("END_VALID"), joTM_BLOCK
								.getString("INSERT_TIME_DW"), joTM_BLOCK
								.getString("UPDATE_TIME_DW")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(15);
				try {
					JSONArray jaTM_EMPLOYEE = jObj.getJSONArray("TM_EMPLOYEE");
					for (int i = 0; i < jaTM_EMPLOYEE.length(); i++) {
						JSONObject joTM_EMPLOYEE = jaTM_EMPLOYEE
								.getJSONObject(i);
						alTM_EMPLOYEE.add(new TM_EMPLOYEE(joTM_EMPLOYEE
								.getString("EMPLOYEE_NIK"), joTM_EMPLOYEE
								.getString("EMPLOYEE_FULLNAME"), joTM_EMPLOYEE
								.getString("EMPLOYEE_POSITIONCODE"),
								joTM_EMPLOYEE.getString("EMPLOYEE_POSITION"),
								joTM_EMPLOYEE.getString("EMPLOYEE_RESIGNDATE"),
								joTM_EMPLOYEE.getString("INSERT_TIME_DW"),
								joTM_EMPLOYEE.getString("UPDATE_TIME_DW")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(16);
				try {
					JSONArray jaTM_LOGIN = jObj.getJSONArray("TM_LOGIN");
					for (int i = 0; i < jaTM_LOGIN.length(); i++) {
						JSONObject joTM_LOGIN = jaTM_LOGIN.getJSONObject(i);
						alTM_LOGIN.add(new TM_LOGIN(joTM_LOGIN
								.getString("EMPLOYEE_NIK"), joTM_LOGIN
								.getString("PASSWORD"), joTM_LOGIN
								.getString("DEFAULT_PASSWORD"), joTM_LOGIN
								.getInt("LOG_LOGIN"), "NO", joTM_LOGIN
								.getString("INSERT_TIME"), joTM_LOGIN
								.getString("UPDATE_TIME")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(17);
				try {
					JSONArray jaTM_USER_AUTH = jObj
							.getJSONArray("TM_USER_AUTH");
					for (int i = 0; i < jaTM_USER_AUTH.length(); i++) {
						JSONObject joTM_USER_AUTH = jaTM_USER_AUTH
								.getJSONObject(i);
						alTM_USER_AUTH.add(new TM_USER_AUTH(joTM_USER_AUTH
								.getString("EMPLOYEE_NIK"), joTM_USER_AUTH
								.getString("USER_ROLE"), joTM_USER_AUTH
								.getString("REFERENCE_ROLE"), joTM_USER_AUTH
								.getString("LOCATION_CODE"), joTM_USER_AUTH
								.getString("INSERT_USER"), joTM_USER_AUTH
								.getString("INSERT_TIME"), joTM_USER_AUTH
								.getString("UPDATE_USER"), joTM_USER_AUTH
								.getString("UPDATE_TIME"), joTM_USER_AUTH
								.getString("DELETE_USER"), joTM_USER_AUTH
								.getString("DELETE_TIME")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(18);
				try {
					JSONArray jaTM_PARAMETER = jObj
							.getJSONArray("TM_PARAMETER");
					for (int i = 0; i < jaTM_PARAMETER.length(); i++) {
						JSONObject joTM_PARAMETER = jaTM_PARAMETER
								.getJSONObject(i);
						alTM_PARAMETER.add(new TM_PARAMETER(joTM_PARAMETER
								.getString("GROUP_CODE"), joTM_PARAMETER
								.getString("PARAM_CODE"), joTM_PARAMETER
								.getString("DESCRIPTION"), joTM_PARAMETER
								.getString("DATE_TIME"), joTM_PARAMETER
								.getString("INSERT_USER"), joTM_PARAMETER
								.getString("INSERT_TIME"), joTM_PARAMETER
								.getString("UPDATE_USER"), joTM_PARAMETER
								.getString("UPDATE_TIME")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(19);
				try {
					JSONArray jaTM_CONTENT_INSPECTION = jObj
							.getJSONArray("TM_CONTENT_INSPECTION");
					for (int i = 0; i < jaTM_CONTENT_INSPECTION.length(); i++) {
						JSONObject joTM_CONTENT_INSPECTION = jaTM_CONTENT_INSPECTION
								.getJSONObject(i);
						alTM_CONTENT_INSPECTION.add(new TM_CONTENT_INSPECTION(
								joTM_CONTENT_INSPECTION
										.getString("CONTENT_INSPECT_CODE"),
								joTM_CONTENT_INSPECTION.getString("CATEGORY"),
								joTM_CONTENT_INSPECTION
										.getString("CONTENT_NAME"),
								joTM_CONTENT_INSPECTION
										.getString("CONTENT_TYPE"),
								joTM_CONTENT_INSPECTION.getString("UOM"),
								joTM_CONTENT_INSPECTION.getString("FLAG_TYPE"),
								joTM_CONTENT_INSPECTION.getInt("PRIORITY"),
								joTM_CONTENT_INSPECTION
										.getString("INSERT_USER"),
								joTM_CONTENT_INSPECTION
										.getString("INSERT_TIME"),
								joTM_CONTENT_INSPECTION
										.getString("UPDATE_USER"),
								joTM_CONTENT_INSPECTION
										.getString("UPDATE_TIME"),
								joTM_CONTENT_INSPECTION
										.getString("TBM0"),
								joTM_CONTENT_INSPECTION
										.getString("TBM1"),
								joTM_CONTENT_INSPECTION
										.getString("TBM2"),
								joTM_CONTENT_INSPECTION
										.getString("TBM3"),
								joTM_CONTENT_INSPECTION
										.getString("TM")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(20);
				try {
					JSONArray jaT_RANGE = jObj.getJSONArray("T_RANGE");
					for (int i = 0; i < jaT_RANGE.length(); i++) {
						JSONObject joT_RANGE = jaT_RANGE.getJSONObject(i);
						alT_RANGE.add(new T_RANGE(joT_RANGE
								.getString("RANGE_CODE"), joT_RANGE
								.getString("VALUE_1"), joT_RANGE
								.getString("VALUE_2"), joT_RANGE
								.getString("VALUE_3"), joT_RANGE
								.getString("VALUE_4"), joT_RANGE
								.getString("VALUE_5"), joT_RANGE
								.getString("START_VALID"), joT_RANGE
								.getString("END_VALID")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(21);
				try {
					JSONArray jaTM_KUALITAS_PANEN = jObj
							.getJSONArray("TM_KUALITAS_PANEN");
					for (int i = 0; i < jaTM_KUALITAS_PANEN.length(); i++) {
						JSONObject joTM_KUALITAS_PANEN = jaTM_KUALITAS_PANEN
								.getJSONObject(i);
						alTM_KUALITAS_PANEN
								.add(new TM_KUALITAS_PANEN(
										joTM_KUALITAS_PANEN
												.getString("ID_KUALITAS"),
										joTM_KUALITAS_PANEN
												.getString("NAMA_KUALITAS"),
										joTM_KUALITAS_PANEN.getString("UOM"),
										joTM_KUALITAS_PANEN
												.getString("GROUP_KUALITAS"),
										joTM_KUALITAS_PANEN.getString("PENALTY_STATUS"),
										joTM_KUALITAS_PANEN
												.getString("DATE_TIME"),
										joTM_KUALITAS_PANEN
												.getString("ACTIVE_STATUS"),
										joTM_KUALITAS_PANEN
												.getString("SHORT_NAME"),
										joTM_KUALITAS_PANEN
												.getString("INSERT_USER"),
										joTM_KUALITAS_PANEN
												.getString("INSERT_TIME"),
										joTM_KUALITAS_PANEN
												.getString("UPDATE_USER"),
										joTM_KUALITAS_PANEN
												.getString("UPDATE_TIME")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

        		//publishProgress(21);
				try {
					JSONArray jaTM_CONTENT_LABEL = jObj
							.getJSONArray("TM_CONTENT_LABEL");
					for (int i = 0; i < jaTM_CONTENT_LABEL.length(); i++) {
						JSONObject joTM_CONTENT_LABEL = jaTM_CONTENT_LABEL
								.getJSONObject(i);
						alTM_CONTENT_LABEL
								.add(new TM_CONTENT_LABEL(
										joTM_CONTENT_LABEL
												.getString("CONTENT_INSPECT_CODE"),
										joTM_CONTENT_LABEL
												.getString("LABEL_CODE"), 
										joTM_CONTENT_LABEL
												.getString("LABEL_NAME"), 
										joTM_CONTENT_LABEL
												.getString("INSERT_USER"), 
										joTM_CONTENT_LABEL
												.getString("INSERT_TIME"), 
										joTM_CONTENT_LABEL
												.getString("UPDATE_USER"), 
										joTM_CONTENT_LABEL
												.getString("UPDATE_TIME")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(22);

        		try {
					// Added by Robin 20140805
					JSONArray jaTR_FAC_TODOLIST = jObj
							.getJSONArray("TR_FAC_TODOLIST");
					for (int i = 0; i < jaTR_FAC_TODOLIST.length(); i++) {
						JSONObject joTR_FAC_TODOLIST = jaTR_FAC_TODOLIST
								.getJSONObject(i);
						alTR_FAC_TODOLIST.add(new TR_FAC_TODOLIST(
								joTR_FAC_TODOLIST.getString("FAC_TDL_CODE"),
								joTR_FAC_TODOLIST.getString("WERKS"),
								joTR_FAC_TODOLIST.getString("AFD_CODE"),
								joTR_FAC_TODOLIST.getString("BLOCK_CODE"),
								joTR_FAC_TODOLIST.getString("POI_CODE"),
								joTR_FAC_TODOLIST.getString("POI_TYPE"),
								joTR_FAC_TODOLIST.getString("POI_NAME"),
								joTR_FAC_TODOLIST.getString("PRIORITY_CODE"),
								joTR_FAC_TODOLIST.getString("SUBJECT"),
								joTR_FAC_TODOLIST.getString("DESCRIPTION"),
								joTR_FAC_TODOLIST.getString("DUE_DATE"),
								joTR_FAC_TODOLIST.getString("STATUS"),
								joTR_FAC_TODOLIST.getString("SYNC_FLAG"),
								joTR_FAC_TODOLIST.getString("MAP_LONG"),
								joTR_FAC_TODOLIST.getString("MAP_LAT"),
								joTR_FAC_TODOLIST.getString("USER_LONG"),
								joTR_FAC_TODOLIST.getString("USER_LAT"),
								joTR_FAC_TODOLIST.getString("DATE_TIME"),
								joTR_FAC_TODOLIST.getString("INSERT_USER"),
								joTR_FAC_TODOLIST.getString("INSERT_TIME"),
								joTR_FAC_TODOLIST.getString("UPDATE_USER"),
								joTR_FAC_TODOLIST.getString("UPDATE_TIME")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(23);
				try {
					JSONArray jaTR_INFRA_TODOLIST = jObj
							.getJSONArray("TR_INFRA_TODOLIST");
					for (int i = 0; i < jaTR_INFRA_TODOLIST.length(); i++) {
						JSONObject joTR_INFRA_TODOLIST = jaTR_INFRA_TODOLIST
								.getJSONObject(i);
						alTR_INFRA_TODOLIST
								.add(new TR_INFRA_TODOLIST(
										joTR_INFRA_TODOLIST
												.getString("INFRA_TDL_CODE"),
										joTR_INFRA_TODOLIST.getString("WERKS"),
										joTR_INFRA_TODOLIST
												.getString("AFD_CODE"),
										joTR_INFRA_TODOLIST
												.getString("BLOCK_CODE"),
										joTR_INFRA_TODOLIST
												.getString("POI_CODE"),
										joTR_INFRA_TODOLIST
												.getString("POI_TYPE"),
										joTR_INFRA_TODOLIST
												.getString("POI_NAME"),
										joTR_INFRA_TODOLIST
												.getString("PRIORITY_CODE"),
										joTR_INFRA_TODOLIST
												.getString("SUBJECT"),
										joTR_INFRA_TODOLIST
												.getString("DESCRIPTION"),
										joTR_INFRA_TODOLIST
												.getString("DUE_DATE"),
										joTR_INFRA_TODOLIST.getString("STATUS"),
										joTR_INFRA_TODOLIST
												.getString("SYNC_FLAG"),
										joTR_INFRA_TODOLIST
												.getString("MAP_LONG"),
										joTR_INFRA_TODOLIST
												.getString("MAP_LAT"),
										joTR_INFRA_TODOLIST
												.getString("USER_LONG"),
										joTR_INFRA_TODOLIST
												.getString("USER_LAT"),
										joTR_INFRA_TODOLIST
												.getString("DATE_TIME"),
										joTR_INFRA_TODOLIST
												.getString("INSERT_USER"),
										joTR_INFRA_TODOLIST
												.getString("INSERT_TIME"),
										joTR_INFRA_TODOLIST
												.getString("UPDATE_USER"),
										joTR_INFRA_TODOLIST
												.getString("UPDATE_TIME")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(24);
				try {
					JSONArray jaTR_BLOCK_TODOLIST = jObj
							.getJSONArray("TR_BLOCK_TODOLIST");
					for (int i = 0; i < jaTR_BLOCK_TODOLIST.length(); i++) {
						JSONObject joTR_BLOCK_TODOLIST = jaTR_BLOCK_TODOLIST
								.getJSONObject(i);
						alTR_BLOCK_TODOLIST
								.add(new TR_BLOCK_TODOLIST(
										joTR_BLOCK_TODOLIST
												.getString("BLOCK_TDL_CODE"),
										joTR_BLOCK_TODOLIST.getString("WERKS"),
										joTR_BLOCK_TODOLIST
												.getString("AFD_CODE"),
										joTR_BLOCK_TODOLIST
												.getString("BLOCK_CODE"),
										joTR_BLOCK_TODOLIST
												.getString("PRIORITY_CODE"),
										joTR_BLOCK_TODOLIST
												.getString("SUBJECT"),
										joTR_BLOCK_TODOLIST
												.getString("DESCRIPTION"),
										joTR_BLOCK_TODOLIST
												.getString("DUE_DATE"),
										joTR_BLOCK_TODOLIST.getString("STATUS"),
										joTR_BLOCK_TODOLIST
												.getString("SYNC_FLAG"),
										joTR_BLOCK_TODOLIST
												.getString("USER_LONG"),
										joTR_BLOCK_TODOLIST
												.getString("USER_LAT"),
										joTR_BLOCK_TODOLIST
												.getString("BLOCK_INSPECT_CODE"),
										joTR_BLOCK_TODOLIST
												.getString("DATE_TIME"),
										joTR_BLOCK_TODOLIST
												.getString("INSERT_USER"),
										joTR_BLOCK_TODOLIST
												.getString("INSERT_TIME"),
										joTR_BLOCK_TODOLIST
												.getString("UPDATE_USER"),
										joTR_BLOCK_TODOLIST
												.getString("UPDATE_TIME")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(25);
				/*
				try {
					JSONArray jaTR_INSPECTION_HISTORY = jObj
							.getJSONArray("TR_INSPECTION_HISTORY");
					for (int i = 0; i < jaTR_INSPECTION_HISTORY.length(); i++) {
						JSONObject joTR_INSPECTION_HISTORY = jaTR_INSPECTION_HISTORY
								.getJSONObject(i);
						alTR_INSPECTION_HISTORY
								.add(new TR_INSPECTION_HISTORY(
										joTR_INSPECTION_HISTORY
												.getString("INSPECT_CODE"),
										joTR_INSPECTION_HISTORY
												.getString("INSPECT_TYPE"),
										joTR_INSPECTION_HISTORY
												.getString("WERKS"),
										joTR_INSPECTION_HISTORY
												.getString("AFD_CODE"),
										joTR_INSPECTION_HISTORY
												.getString("BLOCK_CODE"),
										joTR_INSPECTION_HISTORY
												.getString("INSERT_USER"),
										joTR_INSPECTION_HISTORY
												.getString("INSERT_TIME"),
										joTR_INSPECTION_HISTORY
												.getString("UPDATE_USER"),
										joTR_INSPECTION_HISTORY
												.getString("UPDATE_TIME")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}*/
        		//publishProgress(26);
				try {
					JSONArray jaTR_TODOLIST_HISTORY = jObj.getJSONArray("TR_TODOLIST_HISTORY");
					ObjectTransfer ot = (ObjectTransfer) getApplication();
					for (int i = 0; i < jaTR_TODOLIST_HISTORY.length(); i++) {
						JSONObject joTR_TODOLIST_HISTORY = jaTR_TODOLIST_HISTORY.getJSONObject(i);
						alTR_TODOLIST_HISTORY.add(new TR_TODOLIST_HISTORY(joTR_TODOLIST_HISTORY
								.getString("TR_CODE"), joTR_TODOLIST_HISTORY
								.getString("CREATED_TIME"), joTR_TODOLIST_HISTORY
								.getString("CREATED_USER"), joTR_TODOLIST_HISTORY
								.getString("TR_TYPE"), joTR_TODOLIST_HISTORY
								.getString("IMAGE_NAME"), joTR_TODOLIST_HISTORY
								.getString("PERCENTAGE_PROGRESS"), joTR_TODOLIST_HISTORY
								.getString("REMARKS"), joTR_TODOLIST_HISTORY
								.getString("SYNC_FLAG"), joTR_TODOLIST_HISTORY
								.getString("INSERT_USER"), joTR_TODOLIST_HISTORY
								.getString("INSERT_TIME"), joTR_TODOLIST_HISTORY
								.getString("UPDATE_USER"), joTR_TODOLIST_HISTORY
								.getString("UPDATE_TIME")));
						/*
						String filename = new File(ot.getDirectoryPhoto(),
								joTR_TODOLIST_HISTORY.getString("IMAGE_NAME") + ".jpg")
								.getAbsolutePath();
						Base64.decodeToFile(joTR_TODOLIST_HISTORY.getString("IMAGE_FILE"),
								filename);
						*/
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(27);
				try {
					JSONArray jaTR_NEWS = jObj.getJSONArray("TR_NEWS");
					for (int i = 0; i < jaTR_NEWS.length(); i++) {
						JSONObject joTR_NEWS = jaTR_NEWS.getJSONObject(i);
						alTR_NEWS.add(new TR_NEWS(joTR_NEWS
								.getString("WERKS"), joTR_NEWS
								.getString("NEWS_CODE"), joTR_NEWS
								.getString("HEADLINE"), joTR_NEWS
								.getString("ARTICLE"), joTR_NEWS
								.getString("FILE_NAME"), joTR_NEWS
								.getString("DATE_PUBLISH"), joTR_NEWS
								.getString("DATE_TIME"), joTR_NEWS
								.getString("EXPIRY_DATE"), joTR_NEWS
								.getString("INSERT_USER"), joTR_NEWS
								.getString("INSERT_TIME"), joTR_NEWS
								.getString("UPDATE_USER"), joTR_NEWS
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(28);
				
				try {
					JSONArray jaTR_PERFORMANCE_DAILY_QUALITY = jObj.getJSONArray("TR_PERFORMANCE_DAILY_QUALITY");
					for (int i = 0; i < jaTR_PERFORMANCE_DAILY_QUALITY.length(); i++) {
						JSONObject joTR_PERFORMANCE_DAILY_QUALITY = jaTR_PERFORMANCE_DAILY_QUALITY.getJSONObject(i);						
						alTR_PERFORMANCE_DAILY_QUALITY.add(new TR_PERFORMANCE_DAILY_QUALITY(joTR_PERFORMANCE_DAILY_QUALITY
								.getString("WERKS"), joTR_PERFORMANCE_DAILY_QUALITY
								.getString("AFD_CODE"),joTR_PERFORMANCE_DAILY_QUALITY
								.getString("BLOCK_CODE"),joTR_PERFORMANCE_DAILY_QUALITY
								.getString("NIK_PEMANEN"),joTR_PERFORMANCE_DAILY_QUALITY
								.getString("NAMA_PEMANEN"),joTR_PERFORMANCE_DAILY_QUALITY
								.getString("DATE_TIME"),joTR_PERFORMANCE_DAILY_QUALITY
								.getInt("QUALITY_JJG"),joTR_PERFORMANCE_DAILY_QUALITY
								.getInt("QUALITY_MASAK"),joTR_PERFORMANCE_DAILY_QUALITY
								.getInt("QUALITY_MENGKAL"),joTR_PERFORMANCE_DAILY_QUALITY
								.getInt("QUALITY_MENTAH"),joTR_PERFORMANCE_DAILY_QUALITY
								.getInt("QUALITY_OVERRIPE"),joTR_PERFORMANCE_DAILY_QUALITY
								.getInt("QUALITY_BUSUK"),joTR_PERFORMANCE_DAILY_QUALITY
								.getString("INSERT_USER"),joTR_PERFORMANCE_DAILY_QUALITY
								.getString("INSERT_TIME"),joTR_PERFORMANCE_DAILY_QUALITY
								.getString("UPDATE_USER"),joTR_PERFORMANCE_DAILY_QUALITY
								.getString("UPDATE_TIME"),joTR_PERFORMANCE_DAILY_QUALITY
								.getString("AFD_PEMANEN")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(29);
				
				try {
					JSONArray jaTR_PERFORMANCE_DAILY_PINALTY = jObj.getJSONArray("TR_PERFORMANCE_DAILY_PENALTY");
					for (int i = 0; i < jaTR_PERFORMANCE_DAILY_PINALTY.length(); i++) {
						JSONObject joTR_PERFORMANCE_DAILY_PINALTY = jaTR_PERFORMANCE_DAILY_PINALTY.getJSONObject(i);	
						alTR_PERFORMANCE_DAILY_PINALTY.add(new TR_PERFORMANCE_DAILY_PINALTY(joTR_PERFORMANCE_DAILY_PINALTY
								.getString("WERKS"),joTR_PERFORMANCE_DAILY_PINALTY
								.getString("AFD_CODE"),joTR_PERFORMANCE_DAILY_PINALTY
								.getString("BLOCK_CODE"),joTR_PERFORMANCE_DAILY_PINALTY
								.getString("NIK_PEMANEN"),joTR_PERFORMANCE_DAILY_PINALTY
								.getString("NAMA_PEMANEN"),joTR_PERFORMANCE_DAILY_PINALTY
								.getString("DATE_TIME"),joTR_PERFORMANCE_DAILY_PINALTY
								.getInt("PENALTY_BUAH_TINGGAL_PKK"),joTR_PERFORMANCE_DAILY_PINALTY
								.getInt("PENALTY_BUAH_TINGGAL"),joTR_PERFORMANCE_DAILY_PINALTY
								.getInt("PENALTY_BRONDOLAN"),joTR_PERFORMANCE_DAILY_PINALTY
								.getInt("PENALTY_BUAH_BUSUK"),joTR_PERFORMANCE_DAILY_PINALTY
								.getInt("PENALTY_ALAS_BRD"),joTR_PERFORMANCE_DAILY_PINALTY
								.getInt("PENALTY_BUAH_MATAHARI"),joTR_PERFORMANCE_DAILY_PINALTY
								.getInt("PENALTY_JANJANG_KOSONG"),joTR_PERFORMANCE_DAILY_PINALTY
								.getString("INSERT_USER"),joTR_PERFORMANCE_DAILY_PINALTY
								.getString("INSERT_TIME"),joTR_PERFORMANCE_DAILY_PINALTY
								.getString("UPDATE_USER"),joTR_PERFORMANCE_DAILY_PINALTY
								.getString("UPDATE_TIME"),joTR_PERFORMANCE_DAILY_PINALTY
								.getString("AFD_PEMANEN"),joTR_PERFORMANCE_DAILY_PINALTY
								.getInt("PENALTY_TANGKAI_PANJANG")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(30);
				
				try {
					JSONArray jaTR_PERFORMANCE_DAILY_DELIVERY = jObj.getJSONArray("TR_PERFORMANCE_DAILY_DELIVERY");
					for (int i = 0; i < jaTR_PERFORMANCE_DAILY_DELIVERY.length(); i++) {
						JSONObject joTR_PERFORMANCE_DAILY_DELIVERY = jaTR_PERFORMANCE_DAILY_DELIVERY.getJSONObject(i);							
						alTR_PERFORMANCE_DAILY_DELIVERY.add(new TR_PERFORMANCE_DAILY_DELIVERY(joTR_PERFORMANCE_DAILY_DELIVERY
								.getString("INTERNAL_ORDER"),joTR_PERFORMANCE_DAILY_DELIVERY
								.getString("WERKS"),joTR_PERFORMANCE_DAILY_DELIVERY
								.getString("NOMOR_POLISI"),joTR_PERFORMANCE_DAILY_DELIVERY
								.getString("FIRST_TIME"),joTR_PERFORMANCE_DAILY_DELIVERY
								.getString("LAST_TIME"),joTR_PERFORMANCE_DAILY_DELIVERY
								.getString("DATE_TIME"),joTR_PERFORMANCE_DAILY_DELIVERY
								.getInt("TOTAL_RITASE"),joTR_PERFORMANCE_DAILY_DELIVERY
								.getDouble("TOTAL_TONASE"),joTR_PERFORMANCE_DAILY_DELIVERY
								.getString("INSERT_USER"),joTR_PERFORMANCE_DAILY_DELIVERY
								.getString("INSERT_TIME"),joTR_PERFORMANCE_DAILY_DELIVERY
								.getString("UPDATE_USER"),joTR_PERFORMANCE_DAILY_DELIVERY
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(32);
				
				try {
					JSONArray jaTR_PERFORMANCE_PRODUCTIVITY = jObj.getJSONArray("TR_PERFORMANCE_PRODUCTIVITY");
					for (int i = 0; i < jaTR_PERFORMANCE_PRODUCTIVITY.length(); i++) {
						JSONObject joTR_PERFORMANCE_PRODUCTIVITY = jaTR_PERFORMANCE_PRODUCTIVITY.getJSONObject(i);	
						alTR_PERFORMANCE_PRODUCTIVITY.add(new TR_PERFORMANCE_PRODUCTIVITY(joTR_PERFORMANCE_PRODUCTIVITY
								.getString("WERKS"),joTR_PERFORMANCE_PRODUCTIVITY
								.getString("AFD_CODE"),joTR_PERFORMANCE_PRODUCTIVITY
								.getString("BLOCK_CODE"),joTR_PERFORMANCE_PRODUCTIVITY
								.getInt("TOTAL_PEMANEN"),joTR_PERFORMANCE_PRODUCTIVITY
								.getDouble("DAILY_HA"),joTR_PERFORMANCE_PRODUCTIVITY
								.getInt("DAILY_JJG"),joTR_PERFORMANCE_PRODUCTIVITY
								.getDouble("MTD_HA"),joTR_PERFORMANCE_PRODUCTIVITY
								.getInt("MTD_JJG"),joTR_PERFORMANCE_PRODUCTIVITY
								.getDouble("YTD_HA"),joTR_PERFORMANCE_PRODUCTIVITY
								.getInt("YTD_JJG"),joTR_PERFORMANCE_PRODUCTIVITY
								.getString("DATE_TIME"),joTR_PERFORMANCE_PRODUCTIVITY
								.getString("INSERT_USER"),joTR_PERFORMANCE_PRODUCTIVITY
								.getString("INSERT_TIME"),joTR_PERFORMANCE_PRODUCTIVITY
								.getString("UPDATE_USER"),joTR_PERFORMANCE_PRODUCTIVITY
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}

        		//publishProgress(34);
				try {
					JSONArray jaTR_PERFORMANCE_ESTATE_PRODUCTION = jObj.getJSONArray("TR_PERFORMANCE_ESTATE_PRODUCTION");
					for (int i = 0; i < jaTR_PERFORMANCE_ESTATE_PRODUCTION.length(); i++) {
						JSONObject joTR_PERFORMANCE_ESTATE_PRODUCTION = jaTR_PERFORMANCE_ESTATE_PRODUCTION.getJSONObject(i);	
						alTR_PERFORMANCE_ESTATE_PRODUCTION.add(new TR_PERFORMANCE_ESTATE_PRODUCTION(joTR_PERFORMANCE_ESTATE_PRODUCTION
								.getString("WERKS"),joTR_PERFORMANCE_ESTATE_PRODUCTION
								.getDouble("YTD_ACTUAL"),joTR_PERFORMANCE_ESTATE_PRODUCTION
								.getDouble("YTD_BUDGET"),joTR_PERFORMANCE_ESTATE_PRODUCTION
								.getDouble("YTD_ACTUAL_BUDGET"),joTR_PERFORMANCE_ESTATE_PRODUCTION
								.getString("COLOUR"),joTR_PERFORMANCE_ESTATE_PRODUCTION
								.getString("DATE_TIME"),joTR_PERFORMANCE_ESTATE_PRODUCTION
								.getString("INSERT_USER"),joTR_PERFORMANCE_ESTATE_PRODUCTION
								.getString("INSERT_TIME"),joTR_PERFORMANCE_ESTATE_PRODUCTION
								.getString("UPDATE_USER"),joTR_PERFORMANCE_ESTATE_PRODUCTION
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}

        		//publishProgress(36);
				try {
					JSONArray jaTR_PERFORMANCE_DAILY_HARV = jObj.getJSONArray("TR_PERFORMANCE_DAILY_HARV");
					for (int i = 0; i < jaTR_PERFORMANCE_DAILY_HARV.length(); i++) {
						JSONObject joTR_PERFORMANCE_DAILY_HARV = jaTR_PERFORMANCE_DAILY_HARV.getJSONObject(i);	
						alTR_PERFORMANCE_DAILY_HARV.add(new TR_PERFORMANCE_DAILY_HARV(joTR_PERFORMANCE_DAILY_HARV
								.getString("WERKS"),joTR_PERFORMANCE_DAILY_HARV
								.getString("AFD_CODE"),joTR_PERFORMANCE_DAILY_HARV
								.getString("BLOCK_CODE"),joTR_PERFORMANCE_DAILY_HARV
								.getString("NIK_PEMANEN"),joTR_PERFORMANCE_DAILY_HARV
								.getString("NAMA_PEMANEN"), joTR_PERFORMANCE_DAILY_HARV
								.getInt("PANEN_JJG"),joTR_PERFORMANCE_DAILY_HARV
								.getInt("PANEN_BASIS"),joTR_PERFORMANCE_DAILY_HARV
								.getInt("PANEN_VAR"),joTR_PERFORMANCE_DAILY_HARV
								.getDouble("PANEN_TON"),joTR_PERFORMANCE_DAILY_HARV
								.getDouble("PANEN_BRD"),joTR_PERFORMANCE_DAILY_HARV
								.getDouble("RESTAN_TON"),joTR_PERFORMANCE_DAILY_HARV
								.getInt("TINGGAL_JJG_POKOK"), joTR_PERFORMANCE_DAILY_HARV
								.getInt("TINGGAL_BRD"), joTR_PERFORMANCE_DAILY_HARV
								.getString("DATE_TIME"), joTR_PERFORMANCE_DAILY_HARV
								.getString("INSERT_USER"),joTR_PERFORMANCE_DAILY_HARV
								.getString("INSERT_TIME"),joTR_PERFORMANCE_DAILY_HARV
								.getString("UPDATE_USER"),joTR_PERFORMANCE_DAILY_HARV
								.getString("UPDATE_TIME"),joTR_PERFORMANCE_DAILY_HARV
								.getDouble("PANEN_BRD_PERCENT"),joTR_PERFORMANCE_DAILY_HARV
								.getInt("TINGGAL_JJG_GAWANGAN"),joTR_PERFORMANCE_DAILY_HARV
								.getString("AFD_PEMANEN")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}

        		//publishProgress(38);
				try {
					JSONArray jaTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK = jObj.getJSONArray("TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK");
					for (int i = 0; i < jaTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK.length(); i++) {
						JSONObject joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK = jaTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK.getJSONObject(i);	
						alTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK.add(new TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK(joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK
								.getString("WERKS"),joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK
								.getString("AFD_CODE"),joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK
								.getString("BLOCK_CODE"),joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK
								.getDouble("MTD_ACTUAL"),joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK
								.getDouble("MTD_BUDGET"),joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK
								.getDouble("MTD_ACTUAL_BUDGET"),joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK
								.getString("COLOUR"),joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK
								.getString("DATE_TIME"),joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK
								.getString("INSERT_USER"),joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK
								.getString("INSERT_TIME"),joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK
								.getString("UPDATE_USER"),joTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}

        		//publishProgress(40);
				try {
					JSONArray jaT_DELIVERY_FAVORITE = jObj.getJSONArray("T_DELIVERY_FAVORITE");
					for (int i = 0; i < jaT_DELIVERY_FAVORITE.length(); i++) {
						JSONObject joT_DELIVERY_FAVORITE = jaT_DELIVERY_FAVORITE.getJSONObject(i);	
						alT_DELIVERY_FAVORITE.add(new T_DELIVERY_FAVORITE(joT_DELIVERY_FAVORITE
								.getString("INTERNAL_ORDER"),joT_DELIVERY_FAVORITE
								.getString("WERKS"), "NO", joT_DELIVERY_FAVORITE
								.getString("INSERT_USER"),joT_DELIVERY_FAVORITE
								.getString("INSERT_TIME"),joT_DELIVERY_FAVORITE
								.getString("UPDATE_USER"),joT_DELIVERY_FAVORITE
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(42);
				
				try {
					JSONArray jaTR_BLOCK_INSPECTION = jObj.getJSONArray("TR_BLOCK_INSPECTION");
					for (int i = 0; i < jaTR_BLOCK_INSPECTION.length(); i++) {
						JSONObject joTR_BLOCK_INSPECTION = jaTR_BLOCK_INSPECTION.getJSONObject(i);	
						alTR_BLOCK_INSPECTION.add(new TR_BLOCK_INSPECTION(joTR_BLOCK_INSPECTION
								.getString("BLOCK_INSPECT_CODE"),joTR_BLOCK_INSPECTION
								.getString("WERKS"),joTR_BLOCK_INSPECTION
								.getString("AFD_CODE"),joTR_BLOCK_INSPECTION
								.getString("BLOCK_CODE"),joTR_BLOCK_INSPECTION
								.getString("DATE_TIME"),joTR_BLOCK_INSPECTION
								.getString("STATUS_INSPECT"),joTR_BLOCK_INSPECTION
								.getString("SYNC_FLAG"),joTR_BLOCK_INSPECTION
								.getString("INSERT_USER"),joTR_BLOCK_INSPECTION
								.getString("INSERT_TIME"),joTR_BLOCK_INSPECTION
								.getString("UPDATE_USER"),joTR_BLOCK_INSPECTION
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					JSONArray jaTR_BLOCK_COLOR_REPORT = jObj.getJSONArray("TR_BLOCK_COLOR_REPORT");
					for (int i = 0; i < jaTR_BLOCK_COLOR_REPORT.length(); i++) {
						JSONObject joTR_BLOCK_COLOR_REPORT = jaTR_BLOCK_COLOR_REPORT.getJSONObject(i);	
						alTR_BLOCK_COLOR_REPORT.add(new TR_BLOCK_COLOR_REPORT(joTR_BLOCK_COLOR_REPORT
								.getString("WERKS"),joTR_BLOCK_COLOR_REPORT
								.getString("AFD_CODE"),joTR_BLOCK_COLOR_REPORT
								.getString("BLOCK_CODE"),joTR_BLOCK_COLOR_REPORT
								.getString("DATE_TIME"),joTR_BLOCK_COLOR_REPORT
								.getString("STR_DATA"),joTR_BLOCK_COLOR_REPORT
								.getString("INSERT_USER"),joTR_BLOCK_COLOR_REPORT
								.getString("INSERT_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(44);
				
				try {
					JSONArray jaTR_BLOCK_INDICATOR = jObj.getJSONArray("TR_BLOCK_INDICATOR");
					for (int i = 0; i < jaTR_BLOCK_INDICATOR.length(); i++) {
						JSONObject joTR_BLOCK_INDICATOR = jaTR_BLOCK_INDICATOR.getJSONObject(i);
						alTR_BLOCK_INDICATOR.add(new TR_BLOCK_INDICATOR(joTR_BLOCK_INDICATOR
								.getString("WERKS"),joTR_BLOCK_INDICATOR
								.getString("AFD_CODE"),joTR_BLOCK_INDICATOR
								.getString("BLOCK_CODE"),joTR_BLOCK_INDICATOR
								.getString("VALUE_CODE"), joTR_BLOCK_INDICATOR
								.getString("VALUE"),joTR_BLOCK_INDICATOR
								.getString("SYNC_FLAG"),joTR_BLOCK_INDICATOR
								.getString("INSERT_USER"),joTR_BLOCK_INDICATOR
								.getString("INSERT_TIME"),joTR_BLOCK_INDICATOR
								.getString("UPDATE_USER"),joTR_BLOCK_INDICATOR
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("ERROR: "+e);
				}

        		//publishProgress(46);
				try {
					JSONArray jaTR_AREAL_INSPECTION = jObj.getJSONArray("TR_AREAL_INSPECTION");
					for (int i = 0; i < jaTR_AREAL_INSPECTION.length(); i++) {
						JSONObject joTR_AREAL_INSPECTION = jaTR_AREAL_INSPECTION.getJSONObject(i);	
						alTR_AREAL_INSPECTION.add(new TR_AREAL_INSPECTION(joTR_AREAL_INSPECTION
								.getString("BLOCK_INSPECT_CODE"),joTR_AREAL_INSPECTION
								.getInt("AREAL_NUMBER"),joTR_AREAL_INSPECTION
								.getString("AREAL_TYPE"),joTR_AREAL_INSPECTION
								.getString("INSERT_USER"),joTR_AREAL_INSPECTION
								.getString("INSERT_TIME"),joTR_AREAL_INSPECTION
								.getString("UPDATE_USER"),joTR_AREAL_INSPECTION
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(48);
				
				try {
					JSONArray jaTR_POKOK_INSPECTION = jObj.getJSONArray("TR_POKOK_INSPECTION");
					for (int i = 0; i < jaTR_POKOK_INSPECTION.length(); i++) {
						JSONObject joTR_POKOK_INSPECTION = jaTR_POKOK_INSPECTION.getJSONObject(i);	
						alTR_POKOK_INSPECTION.add(new TR_POKOK_INSPECTION(joTR_POKOK_INSPECTION
								.getString("BLOCK_INSPECT_CODE"),joTR_POKOK_INSPECTION
								.getInt("AREAL_NUMBER"),joTR_POKOK_INSPECTION
								.getInt("PALM_NUMBER"),joTR_POKOK_INSPECTION
								.getString("USER_LONG"),joTR_POKOK_INSPECTION
								.getString("USER_LAT"),joTR_POKOK_INSPECTION
								.getString("MAP_LONG"),joTR_POKOK_INSPECTION
								.getString("MAP_LAT"),joTR_POKOK_INSPECTION
								.getString("INSERT_USER"),joTR_POKOK_INSPECTION
								.getString("INSERT_TIME"),joTR_POKOK_INSPECTION
								.getString("UPDATE_USER"),joTR_POKOK_INSPECTION
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}
        		//publishProgress(50);
				
				try {
					JSONArray jaTR_CONTENT_VALUE = jObj.getJSONArray("TR_CONTENT_VALUE");
					for (int i = 0; i < jaTR_CONTENT_VALUE.length(); i++) {
						JSONObject joTR_CONTENT_VALUE = jaTR_CONTENT_VALUE.getJSONObject(i);
						alTR_CONTENT_VALUE.add(new TR_CONTENT_VALUE(joTR_CONTENT_VALUE
								.getString("CONTENT_INSPECT_CODE"),joTR_CONTENT_VALUE
								.getString("BLOCK_INSPECT_CODE"),joTR_CONTENT_VALUE
								.getInt("AREAL_NUMBER"),joTR_CONTENT_VALUE
								.getInt("PALM_NUMBER"),joTR_CONTENT_VALUE
								.getString("CONTENT_VALUE"),joTR_CONTENT_VALUE
								.getString("INSERT_USER"),joTR_CONTENT_VALUE
								.getString("INSERT_TIME"),joTR_CONTENT_VALUE
								.getString("UPDATE_USER"),joTR_CONTENT_VALUE
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}

        		//publishProgress(50);
				
				try {
					JSONArray jaTR_CONTENT_INDICATOR_SUM = jObj.getJSONArray("TR_CONTENT_INDICATOR_SUM");
					for (int i = 0; i < jaTR_CONTENT_INDICATOR_SUM.length(); i++) {
						JSONObject joTR_CONTENT_INDICATOR_SUM = jaTR_CONTENT_INDICATOR_SUM.getJSONObject(i);
						alTR_CONTENT_INDICATOR_SUM.add(new TR_CONTENT_INDICATOR_SUM(joTR_CONTENT_INDICATOR_SUM
								.getString("BLOCK_INSPECT_CODE"), joTR_CONTENT_INDICATOR_SUM
								.getString("CONTENT_INSPECT_CODE"), joTR_CONTENT_INDICATOR_SUM
								.getString("CONTENT_NAME"), joTR_CONTENT_INDICATOR_SUM
								.getString("TOTAL_YES"), joTR_CONTENT_INDICATOR_SUM
								.getString("PERSENTASE"), joTR_CONTENT_INDICATOR_SUM
								.getString("NILAI"), joTR_CONTENT_INDICATOR_SUM
								.getString("INSERT_USER"), joTR_CONTENT_INDICATOR_SUM
								.getString("INSERT_TIME"), joTR_CONTENT_INDICATOR_SUM
								.getString("UPDATE_USER"), joTR_CONTENT_INDICATOR_SUM
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}

        		//publishProgress(50);
				
				try {
					JSONArray jaTR_CONTENT_LOSSES_SUM = jObj.getJSONArray("TR_CONTENT_LOSSES_SUM");
					for (int i = 0; i < jaTR_CONTENT_LOSSES_SUM.length(); i++) {
						JSONObject joTR_CONTENT_LOSSES_SUM = jaTR_CONTENT_LOSSES_SUM.getJSONObject(i);
						alTR_CONTENT_LOSSES_SUM.add(new TR_CONTENT_LOSSES_SUM(joTR_CONTENT_LOSSES_SUM
								.getString("BLOCK_INSPECT_CODE"), joTR_CONTENT_LOSSES_SUM
								.getString("CONTENT_INSPECT_CODE"), joTR_CONTENT_LOSSES_SUM
								.getString("CONTENT_NAME"), joTR_CONTENT_LOSSES_SUM
								.getString("QTY"), joTR_CONTENT_LOSSES_SUM
								.getString("AVG"), joTR_CONTENT_LOSSES_SUM
								.getString("INSERT_USER"), joTR_CONTENT_LOSSES_SUM
								.getString("INSERT_TIME"), joTR_CONTENT_LOSSES_SUM
								.getString("UPDATE_USER"), joTR_CONTENT_LOSSES_SUM
								.getString("UPDATE_TIME")));
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
				}

        		//publishProgress(51);
				try {
					JSONArray jaTR_IMAGE = jObj.getJSONArray("TR_IMAGE");
					ObjectTransfer ot = (ObjectTransfer) getApplication();
					for (int i = 0; i < jaTR_IMAGE.length(); i++) {
						JSONObject joTR_IMAGE = jaTR_IMAGE.getJSONObject(i);
						alTR_IMAGE.add(new TR_IMAGE(joTR_IMAGE
								.getString("TR_CODE"), joTR_IMAGE
								.getString("TR_TYPE"), joTR_IMAGE
								.getString("IMAGE_NAME"), joTR_IMAGE
								.getString("SYNC_FLAG"), joTR_IMAGE
								.getString("USER_LONG"), joTR_IMAGE
								.getString("USER_LAT"), joTR_IMAGE
								.getString("INSERT_USER"), joTR_IMAGE
								.getString("INSERT_TIME"), joTR_IMAGE
								.getString("UPDATE_USER"), joTR_IMAGE
								.getString("UPDATE_TIME")));
						/*
						// Added by Robin 20130814
						String filename = new File(ot.getDirectoryPhoto(),
								joTR_IMAGE.getString("IMAGE_NAME") + ".jpg")
								.getAbsolutePath();
						Base64.decodeToFile(joTR_IMAGE.getString("IMAGE_FILE"),
								filename);
						//
						*/
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}finally
        	{
        		// added by Adit, 20140806
        		System.out.println("STAGE 1");
        		Resources res;
            	jsonobj = new JSONObject();
				try {
	        		System.out.println("STAGE 2");
					res = getResources();
	        		System.out.println("STAGE 3");
	        		jsonobj.put("DEVICE_ID", ot.getDeviceID());
	                jsonobj.put("VERSION", res.getInteger(R.integer.Label_Version_Code));
	                jsonobj.put("NIK", ot.getUSNIK());
	                jsonobj.put("REFERENCE_ROLE", ot.getReference_Role());
	                jsonobj.put("LOCATION_CODE", ot.getLocation_Code());
	                jsonobj.put("COMPANY", ot.getUSComp_Code());
	        		jsonobj.put("DATA", "YES");
	        		jsonobj.put("WRITE", "YES");
	        		System.out.println("STAGE 4");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
	        		System.out.println("STAGE 5");
				}
        		JSONArray jsonArray = new JSONArray();

        		//publishProgress(51);
        		TM_SERVER[] TM_SERVER = new TM_SERVER[alTM_SERVER.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_SERVER.size(); i++)
        		{
        			TM_SERVER[i] = alTM_SERVER.get(i);
        			
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_SERVER[i].getCOMP_CODE().equals("null")||!TM_SERVER[i].getTYPE().equals("null")||
			    				!TM_SERVER[i].getSTART_VALID().equals("null")||!TM_SERVER[i].getEND_VALID().equals("null")) 
			    		{
							jsonReturn.put("COMP_CODE",TM_SERVER[i].getCOMP_CODE());
							jsonReturn.put("TYPE",TM_SERVER[i].getTYPE());
							jsonReturn.put("START_VALID",TM_SERVER[i].getSTART_VALID());
							jsonReturn.put("END_VALID",TM_SERVER[i].getEND_VALID());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
						jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
		    	try 
		    	{
					jsonobj.put("TM_SERVER", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		TR_CONTENT_VALUE[] TR_CONTENT_VALUE = new TR_CONTENT_VALUE[alTR_CONTENT_VALUE.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_CONTENT_VALUE.size(); i++)
        		{
        			TR_CONTENT_VALUE[i] = alTR_CONTENT_VALUE.get(i);
        			
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_CONTENT_VALUE[i].getCONTENT_INSPECT_CODE().equals("null")||
			    				!TR_CONTENT_VALUE[i].getBLOCK_INSPECT_CODE().equals("null")) 
			    		{
							jsonReturn.put("CONTENT_INSPECT_CODE",
									TR_CONTENT_VALUE[i]
											.getCONTENT_INSPECT_CODE());
							jsonReturn
									.put("BLOCK_INSPECT_CODE",
											TR_CONTENT_VALUE[i]
													.getBLOCK_INSPECT_CODE());
							jsonReturn.put("AREAL_NUMBER",
									TR_CONTENT_VALUE[i].getAREAL_NUMBER());
							jsonReturn.put("PALM_NUMBER",
									TR_CONTENT_VALUE[i].getPALM_NUMBER());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
						jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
		    	try 
		    	{
					jsonobj.put("TR_CONTENT_VALUE", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(51);
        		TR_CONTENT_INDICATOR_SUM[] TR_CONTENT_INDICATOR_SUM = new TR_CONTENT_INDICATOR_SUM[alTR_CONTENT_INDICATOR_SUM.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_CONTENT_INDICATOR_SUM.size(); i++)
        		{
        			TR_CONTENT_INDICATOR_SUM[i] = alTR_CONTENT_INDICATOR_SUM.get(i);
        			
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_CONTENT_INDICATOR_SUM[i].getCONTENT_INSPECT_CODE().equals("null")||
			    				!TR_CONTENT_INDICATOR_SUM[i].getBLOCK_INSPECT_CODE().equals("null")) 
			    		{
			    			jsonReturn.put("CONTENT_INSPECT_CODE", TR_CONTENT_INDICATOR_SUM[i].getCONTENT_INSPECT_CODE());
				    		jsonReturn.put("BLOCK_INSPECT_CODE", TR_CONTENT_INDICATOR_SUM[i].getBLOCK_INSPECT_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
		    	try 
		    	{
					jsonobj.put("TR_CONTENT_INDICATOR_SUM", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(51);
        		TR_CONTENT_LOSSES_SUM[] TR_CONTENT_LOSSES_SUM = new TR_CONTENT_LOSSES_SUM[alTR_CONTENT_LOSSES_SUM.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_CONTENT_LOSSES_SUM.size(); i++)
        		{
        			TR_CONTENT_LOSSES_SUM[i] = alTR_CONTENT_LOSSES_SUM.get(i);
        			
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_CONTENT_LOSSES_SUM[i].getCONTENT_INSPECT_CODE().equals("null")||
			    				!TR_CONTENT_LOSSES_SUM[i].getBLOCK_INSPECT_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("CONTENT_INSPECT_CODE", TR_CONTENT_LOSSES_SUM[i].getCONTENT_INSPECT_CODE());
				    		jsonReturn.put("BLOCK_INSPECT_CODE", TR_CONTENT_LOSSES_SUM[i].getBLOCK_INSPECT_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
		    	try 
		    	{
					jsonobj.put("TR_CONTENT_LOSSES_SUM", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(52);
				TR_POKOK_INSPECTION[] TR_POKOK_INSPECTION = new TR_POKOK_INSPECTION[alTR_POKOK_INSPECTION.size()];
				jsonArray = new JSONArray();
        		for(int i=0; i< alTR_POKOK_INSPECTION.size(); i++)
        		{
        			TR_POKOK_INSPECTION[i] = alTR_POKOK_INSPECTION.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_POKOK_INSPECTION[i].getBLOCK_INSPECT_CODE().equals("null")) 
			    		{
			    			jsonReturn.put("BLOCK_INSPECT_CODE", TR_POKOK_INSPECTION[i].getBLOCK_INSPECT_CODE());
				    		jsonReturn.put("AREAL_NUMBER", TR_POKOK_INSPECTION[i].getAREAL_NUMBER());
				    		jsonReturn.put("PALM_NUMBER", TR_POKOK_INSPECTION[i].getPALM_NUMBER());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_POKOK_INSPECTION", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(53);
        		TR_AREAL_INSPECTION[] TR_AREAL_INSPECTION = new TR_AREAL_INSPECTION[alTR_AREAL_INSPECTION.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_AREAL_INSPECTION.size(); i++)
        		{
        			TR_AREAL_INSPECTION[i] = alTR_AREAL_INSPECTION.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_AREAL_INSPECTION[i].getBLOCK_INSPECT_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("BLOCK_INSPECT_CODE", TR_AREAL_INSPECTION[i].getBLOCK_INSPECT_CODE());
				    		jsonReturn.put("AREAL_NUMBER", TR_AREAL_INSPECTION[i].getAREAL_NUMBER());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_AREAL_INSPECTION", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(54);
        		TR_BLOCK_INDICATOR[] TR_BLOCK_INDICATOR = new TR_BLOCK_INDICATOR[alTR_BLOCK_INDICATOR.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_BLOCK_INDICATOR.size(); i++)
        		{
        			TR_BLOCK_INDICATOR[i] = alTR_BLOCK_INDICATOR.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_BLOCK_INDICATOR[i].getWERKS().equals("null")||
		    				!TR_BLOCK_INDICATOR[i].getAFD_CODE().equals("null")||
		    				!TR_BLOCK_INDICATOR[i].getBLOCK_CODE().equals("null")||
		    				!TR_BLOCK_INDICATOR[i].getVALUE_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("WERKS", TR_BLOCK_INDICATOR[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_BLOCK_INDICATOR[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_BLOCK_INDICATOR[i].getBLOCK_CODE());
				    		jsonReturn.put("VALUE_CODE", TR_BLOCK_INDICATOR[i].getVALUE_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_BLOCK_INDICATOR", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(55);
        		TR_BLOCK_INSPECTION[] TR_BLOCK_INSPECTION = new TR_BLOCK_INSPECTION[alTR_BLOCK_INSPECTION.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_BLOCK_INSPECTION.size(); i++)
        		{
        			TR_BLOCK_INSPECTION[i] = alTR_BLOCK_INSPECTION.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_BLOCK_INSPECTION[i].getBLOCK_INSPECT_CODE().equals("null")||
		    				!TR_BLOCK_INSPECTION[i].getWERKS().equals("null")||
		    				!TR_BLOCK_INSPECTION[i].getAFD_CODE().equals("null")||
		    				!TR_BLOCK_INSPECTION[i].getBLOCK_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("BLOCK_INSPECT_CODE", TR_BLOCK_INSPECTION[i].getBLOCK_INSPECT_CODE());
				    		jsonReturn.put("WERKS", TR_BLOCK_INSPECTION[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_BLOCK_INSPECTION[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_BLOCK_INSPECTION[i].getBLOCK_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_BLOCK_INSPECTION", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
        		
        		TR_BLOCK_COLOR_REPORT[] TR_BLOCK_COLOR_REPORT = new TR_BLOCK_COLOR_REPORT[alTR_BLOCK_COLOR_REPORT.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_BLOCK_COLOR_REPORT.size(); i++)
        		{
        			TR_BLOCK_COLOR_REPORT[i] = alTR_BLOCK_COLOR_REPORT.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_BLOCK_COLOR_REPORT[i].getWERKS().equals("null")||
		    				!TR_BLOCK_COLOR_REPORT[i].getAFD_CODE().equals("null")||
		    				!TR_BLOCK_COLOR_REPORT[i].getBLOCK_CODE().equals("null")||
		    				!TR_BLOCK_COLOR_REPORT[i].getSTR_DATA().equals("null")||
		    				!TR_BLOCK_COLOR_REPORT[i].getDATE_TIME().equals("null")) 
			    		{
				    		jsonReturn.put("WERKS", TR_BLOCK_COLOR_REPORT[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_BLOCK_COLOR_REPORT[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_BLOCK_COLOR_REPORT[i].getBLOCK_CODE());
				    		jsonReturn.put("STR_DATA", TR_BLOCK_COLOR_REPORT[i].getSTR_DATA());
				    		jsonReturn.put("DATE_TIME", TR_BLOCK_COLOR_REPORT[i].getDATE_TIME());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_BLOCK_COLOR_REPORT", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(56);
        		T_DELIVERY_FAVORITE[] T_DELIVERY_FAVORITE = new T_DELIVERY_FAVORITE[alT_DELIVERY_FAVORITE.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alT_DELIVERY_FAVORITE.size(); i++)
        		{
        			T_DELIVERY_FAVORITE[i] = alT_DELIVERY_FAVORITE.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!T_DELIVERY_FAVORITE[i].getINTERNAL_ORDER().equals("null")||
		    				!T_DELIVERY_FAVORITE[i].getWERKS().equals("null")) 
			    		{
				    		jsonReturn.put("INTERNAL_ORDER", T_DELIVERY_FAVORITE[i].getINTERNAL_ORDER());
				    		jsonReturn.put("WERKS", T_DELIVERY_FAVORITE[i].getWERKS());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("T_DELIVERY_FAVORITE", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(57);
        		TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK[] TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK = new TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK[alTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK.size(); i++)
        		{
        			TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK[i] = alTR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK[i].getWERKS().equals("null")||
		    				!TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK[i].getAFD_CODE().equals("null")||
		    				!TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK[i].getBLOCK_CODE().equals("null")||
		    				!TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK[i].getDATE_TIME().equals("null")) 
			    		{
				    		jsonReturn.put("WERKS", TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK[i].getBLOCK_CODE());
				    		jsonReturn.put("DATE_TIME", TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK[i].getDATE_TIME());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(58);
        		TR_PERFORMANCE_DAILY_HARV[] TR_PERFORMANCE_DAILY_HARV = new TR_PERFORMANCE_DAILY_HARV[alTR_PERFORMANCE_DAILY_HARV.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_PERFORMANCE_DAILY_HARV.size(); i++)
        		{
        			TR_PERFORMANCE_DAILY_HARV[i] = alTR_PERFORMANCE_DAILY_HARV.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_PERFORMANCE_DAILY_HARV[i].getWERKS().equals("null")||
		    				!TR_PERFORMANCE_DAILY_HARV[i].getAFD_CODE().equals("null")||
		    				!TR_PERFORMANCE_DAILY_HARV[i].getBLOCK_CODE().equals("null")||
		    				!TR_PERFORMANCE_DAILY_HARV[i].getNIK_PEMANEN().equals("null")||
		    				!TR_PERFORMANCE_DAILY_HARV[i].getDATE_TIME().equals("null")) 
			    		{
				    		jsonReturn.put("WERKS", TR_PERFORMANCE_DAILY_HARV[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_PERFORMANCE_DAILY_HARV[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_PERFORMANCE_DAILY_HARV[i].getBLOCK_CODE());
				    		jsonReturn.put("NIK_PEMANEN", TR_PERFORMANCE_DAILY_HARV[i].getNIK_PEMANEN());
				    		jsonReturn.put("DATE_TIME", TR_PERFORMANCE_DAILY_HARV[i].getDATE_TIME());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_PERFORMANCE_DAILY_HARV", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(59);
        		TR_PERFORMANCE_ESTATE_PRODUCTION[] TR_PERFORMANCE_ESTATE_PRODUCTION = new TR_PERFORMANCE_ESTATE_PRODUCTION[alTR_PERFORMANCE_ESTATE_PRODUCTION.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_PERFORMANCE_ESTATE_PRODUCTION.size(); i++)
        		{
        			TR_PERFORMANCE_ESTATE_PRODUCTION[i] = alTR_PERFORMANCE_ESTATE_PRODUCTION.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_PERFORMANCE_ESTATE_PRODUCTION[i].getWERKS().equals("null")||
		    				!TR_PERFORMANCE_ESTATE_PRODUCTION[i].getDATE_TIME().equals("null")) 
			    		{
				    		jsonReturn.put("WERKS", TR_PERFORMANCE_ESTATE_PRODUCTION[i].getWERKS());
				    		jsonReturn.put("DATE_TIME", TR_PERFORMANCE_ESTATE_PRODUCTION[i].getDATE_TIME());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_PERFORMANCE_ESTATE_PRODUCTION", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(60);
        		TR_PERFORMANCE_PRODUCTIVITY[] TR_PERFORMANCE_PRODUCTIVITY = new TR_PERFORMANCE_PRODUCTIVITY[alTR_PERFORMANCE_PRODUCTIVITY.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_PERFORMANCE_PRODUCTIVITY.size(); i++)
        		{
        			TR_PERFORMANCE_PRODUCTIVITY[i] = alTR_PERFORMANCE_PRODUCTIVITY.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_PERFORMANCE_PRODUCTIVITY[i].getWERKS().equals("null")||
		    				!TR_PERFORMANCE_PRODUCTIVITY[i].getAFD_CODE().equals("null")||
		    				!TR_PERFORMANCE_PRODUCTIVITY[i].getBLOCK_CODE().equals("null")||
		    				!TR_PERFORMANCE_PRODUCTIVITY[i].getDATE_TIME().equals("null")) 
			    		{
				    		jsonReturn.put("WERKS", TR_PERFORMANCE_PRODUCTIVITY[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_PERFORMANCE_PRODUCTIVITY[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_PERFORMANCE_PRODUCTIVITY[i].getBLOCK_CODE());
				    		jsonReturn.put("DATE_TIME", TR_PERFORMANCE_PRODUCTIVITY[i].getDATE_TIME());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_PERFORMANCE_PRODUCTIVITY", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(61);
        		TR_PERFORMANCE_DAILY_DELIVERY[] TR_PERFORMANCE_DAILY_DELIVERY = new TR_PERFORMANCE_DAILY_DELIVERY[alTR_PERFORMANCE_DAILY_DELIVERY.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_PERFORMANCE_DAILY_DELIVERY.size(); i++)
        		{
        			TR_PERFORMANCE_DAILY_DELIVERY[i] = alTR_PERFORMANCE_DAILY_DELIVERY.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_PERFORMANCE_DAILY_DELIVERY[i].getINTERNAL_ORDER().equals("null")||
		    				!TR_PERFORMANCE_DAILY_DELIVERY[i].getWERKS().equals("null")||
		    				!TR_PERFORMANCE_DAILY_DELIVERY[i].getDATE_TIME().equals("null")) 
			    		{
				    		jsonReturn.put("INTERNAL_ORDER", TR_PERFORMANCE_DAILY_DELIVERY[i].getINTERNAL_ORDER());
				    		jsonReturn.put("WERKS", TR_PERFORMANCE_DAILY_DELIVERY[i].getWERKS());
				    		jsonReturn.put("DATE_TIME", TR_PERFORMANCE_DAILY_DELIVERY[i].getDATE_TIME());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_PERFORMANCE_DAILY_DELIVERY", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(62);
        		TR_PERFORMANCE_DAILY_PINALTY[] TR_PERFORMANCE_DAILY_PINALTY = new TR_PERFORMANCE_DAILY_PINALTY[alTR_PERFORMANCE_DAILY_PINALTY.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_PERFORMANCE_DAILY_PINALTY.size(); i++)
        		{
        			TR_PERFORMANCE_DAILY_PINALTY[i] = alTR_PERFORMANCE_DAILY_PINALTY.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_PERFORMANCE_DAILY_PINALTY[i].getWERKS().equals("null")||
		    				!TR_PERFORMANCE_DAILY_PINALTY[i].getAFD_CODE().equals("null")||
		    				!TR_PERFORMANCE_DAILY_PINALTY[i].getBLOCK_CODE().equals("null")||
		    				!TR_PERFORMANCE_DAILY_PINALTY[i].getNIK_PEMANEN().equals("null")||
		    				!TR_PERFORMANCE_DAILY_PINALTY[i].getDATE_TIME().equals("null")) 
			    		{
				    		jsonReturn.put("WERKS", TR_PERFORMANCE_DAILY_PINALTY[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_PERFORMANCE_DAILY_PINALTY[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_PERFORMANCE_DAILY_PINALTY[i].getBLOCK_CODE());
				    		jsonReturn.put("NIK_PEMANEN", TR_PERFORMANCE_DAILY_PINALTY[i].getNIK_PEMANEN());
				    		jsonReturn.put("DATE_TIME", TR_PERFORMANCE_DAILY_PINALTY[i].getDATE_TIME());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_PERFORMANCE_DAILY_PENALTY", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(63);
        		TR_PERFORMANCE_DAILY_QUALITY[] TR_PERFORMANCE_DAILY_QUALITY = new TR_PERFORMANCE_DAILY_QUALITY[alTR_PERFORMANCE_DAILY_QUALITY.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_PERFORMANCE_DAILY_QUALITY.size(); i++)
        		{
        			TR_PERFORMANCE_DAILY_QUALITY[i] = alTR_PERFORMANCE_DAILY_QUALITY.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_PERFORMANCE_DAILY_QUALITY[i].getWERKS().equals("null")||
		    				!TR_PERFORMANCE_DAILY_QUALITY[i].getAFD_CODE().equals("null")||
		    				!TR_PERFORMANCE_DAILY_QUALITY[i].getBLOCK_CODE().equals("null")||
		    				!TR_PERFORMANCE_DAILY_QUALITY[i].getNIK_PEMANEN().equals("null")||
		    				!TR_PERFORMANCE_DAILY_QUALITY[i].getDATE_TIME().equals("null")) 
			    		{
				    		jsonReturn.put("WERKS", TR_PERFORMANCE_DAILY_QUALITY[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_PERFORMANCE_DAILY_QUALITY[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_PERFORMANCE_DAILY_QUALITY[i].getBLOCK_CODE());
				    		jsonReturn.put("NIK_PEMANEN", TR_PERFORMANCE_DAILY_QUALITY[i].getNIK_PEMANEN());
				    		jsonReturn.put("DATE_TIME", TR_PERFORMANCE_DAILY_QUALITY[i].getDATE_TIME());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_PERFORMANCE_DAILY_QUALITY", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(64);
        		TR_HS_REGION[] TR_HS_REGION = new TR_HS_REGION[alTR_HS_REGION.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_HS_REGION.size(); i++)
        		{
        			TR_HS_REGION[i] = alTR_HS_REGION.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_HS_REGION[i].getNATIONAL().equals("null")||
		    				!TR_HS_REGION[i].getREGION_CODE().equals("null")||
		    				!TR_HS_REGION[i].getSPMON().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TR_HS_REGION[i].getNATIONAL());
				    		jsonReturn.put("REGION_CODE", TR_HS_REGION[i].getREGION_CODE());
				    		jsonReturn.put("SPMON", TR_HS_REGION[i].getSPMON());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_HS_REGION", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(65);
        		TR_HS_COMP[] TR_HS_COMP = new TR_HS_COMP[alTR_HS_COMP.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_HS_COMP.size(); i++)
        		{
        			TR_HS_COMP[i] = alTR_HS_COMP.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_HS_COMP[i].getNATIONAL().equals("null")||
		    				!TR_HS_COMP[i].getREGION_CODE().equals("null")||
		    				!TR_HS_COMP[i].getCOMP_CODE().equals("null")||
		    				!TR_HS_COMP[i].getSPMON().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TR_HS_COMP[i].getNATIONAL());
				    		jsonReturn.put("REGION_CODE", TR_HS_COMP[i].getREGION_CODE());
				    		jsonReturn.put("COMP_CODE", TR_HS_COMP[i].getCOMP_CODE());
				    		jsonReturn.put("SPMON", TR_HS_COMP[i].getSPMON());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_HS_COMP", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(66);
        		TR_HS_EST[] TR_HS_EST = new TR_HS_EST[alTR_HS_EST.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_HS_EST.size(); i++)
        		{
        			TR_HS_EST[i] = alTR_HS_EST.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_HS_EST[i].getNATIONAL().equals("null")||
		    				!TR_HS_EST[i].getREGION_CODE().equals("null")||
		    				!TR_HS_EST[i].getCOMP_CODE().equals("null")||
		    				!TR_HS_EST[i].getEST_CODE().equals("null")||
		    				!TR_HS_EST[i].getWERKS().equals("null")||
		    				!TR_HS_EST[i].getSPMON().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TR_HS_EST[i].getNATIONAL());
				    		jsonReturn.put("REGION_CODE", TR_HS_EST[i].getREGION_CODE());
				    		jsonReturn.put("COMP_CODE", TR_HS_EST[i].getCOMP_CODE());
				    		jsonReturn.put("EST_CODE", TR_HS_EST[i].getEST_CODE());
				    		jsonReturn.put("WERKS", TR_HS_EST[i].getWERKS());
				    		jsonReturn.put("SPMON", TR_HS_EST[i].getSPMON());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_HS_EST", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(67);
        		TR_HS_AFD[] TR_HS_AFD = new TR_HS_AFD[alTR_HS_AFD.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_HS_AFD.size(); i++)
        		{
        			TR_HS_AFD[i] = alTR_HS_AFD.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_HS_AFD[i].getNATIONAL().equals("null")||
		    				!TR_HS_AFD[i].getREGION_CODE().equals("null")||
		    				!TR_HS_AFD[i].getCOMP_CODE().equals("null")||
		    				!TR_HS_AFD[i].getEST_CODE().equals("null")||
		    				!TR_HS_AFD[i].getWERKS().equals("null")||
		    				!TR_HS_AFD[i].getAFD_CODE().equals("null")||
		    				!TR_HS_AFD[i].getAFD_CODE_GIS().equals("null")||
		    				!TR_HS_AFD[i].getSPMON().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TR_HS_AFD[i].getNATIONAL());
				    		jsonReturn.put("REGION_CODE", TR_HS_AFD[i].getREGION_CODE());
				    		jsonReturn.put("COMP_CODE", TR_HS_AFD[i].getCOMP_CODE());
				    		jsonReturn.put("EST_CODE", TR_HS_AFD[i].getEST_CODE());
				    		jsonReturn.put("WERKS", TR_HS_AFD[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_HS_AFD[i].getAFD_CODE());
				    		jsonReturn.put("AFD_CODE_GIS", TR_HS_AFD[i].getAFD_CODE_GIS());
				    		jsonReturn.put("SPMON", TR_HS_AFD[i].getSPMON());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_HS_AFD", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(68);
        		TR_HS_BLOCK[] TR_HS_BLOCK = new TR_HS_BLOCK[alTR_HS_BLOCK.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_HS_BLOCK.size(); i++)
        		{
        			TR_HS_BLOCK[i] = alTR_HS_BLOCK.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_HS_BLOCK[i].getNATIONAL().equals("null")||
		    				!TR_HS_BLOCK[i].getREGION_CODE().equals("null")||
		    				!TR_HS_BLOCK[i].getCOMP_CODE().equals("null")||
		    				!TR_HS_BLOCK[i].getEST_CODE().equals("null")||
		    				!TR_HS_BLOCK[i].getWERKS().equals("null")||
		    				!TR_HS_BLOCK[i].getAFD_CODE().equals("null")||
		    				!TR_HS_BLOCK[i].getBLOCK_CODE().equals("null")||
		    				!TR_HS_BLOCK[i].getBLOCK_CODE_GIS().equals("null")||
		    				!TR_HS_BLOCK[i].getSPMON().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TR_HS_BLOCK[i].getNATIONAL());
				    		jsonReturn.put("REGION_CODE", TR_HS_BLOCK[i].getREGION_CODE());
				    		jsonReturn.put("COMP_CODE", TR_HS_BLOCK[i].getCOMP_CODE());
				    		jsonReturn.put("EST_CODE", TR_HS_BLOCK[i].getEST_CODE());
				    		jsonReturn.put("WERKS", TR_HS_BLOCK[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_HS_BLOCK[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_HS_BLOCK[i].getBLOCK_CODE());
				    		jsonReturn.put("BLOCK_CODE_GIS", TR_HS_BLOCK[i].getBLOCK_CODE_GIS());
				    		jsonReturn.put("SPMON", TR_HS_BLOCK[i].getSPMON());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_HS_BLOCK", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(69);
        		TM_HS_ATTRIBUTE[] TM_HS_ATTRIBUTE = new TM_HS_ATTRIBUTE[alTM_HS_ATTRIBUTE.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_HS_ATTRIBUTE.size(); i++)
        		{
        			TM_HS_ATTRIBUTE[i] = alTM_HS_ATTRIBUTE.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_HS_ATTRIBUTE[i].getNATIONAL().equals("null")||
		    				!TM_HS_ATTRIBUTE[i].getREGION_CODE().equals("null")||
		    				!TM_HS_ATTRIBUTE[i].getCOMP_CODE().equals("null")||
		    				!TM_HS_ATTRIBUTE[i].getEST_CODE().equals("null")||
		    				!TM_HS_ATTRIBUTE[i].getWERKS().equals("null")||
		    				!TM_HS_ATTRIBUTE[i].getAFD_CODE().equals("null")||
		    				!TM_HS_ATTRIBUTE[i].getBLOCK_CODE().equals("null")||
		    				!TM_HS_ATTRIBUTE[i].getSTART_VALID().equals("null")||
		    				!TM_HS_ATTRIBUTE[i].getEND_VALID().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TM_HS_ATTRIBUTE[i].getNATIONAL());
				    		jsonReturn.put("REGION_CODE", TM_HS_ATTRIBUTE[i].getREGION_CODE());
				    		jsonReturn.put("COMP_CODE", TM_HS_ATTRIBUTE[i].getCOMP_CODE());
				    		jsonReturn.put("EST_CODE", TM_HS_ATTRIBUTE[i].getEST_CODE());
				    		jsonReturn.put("WERKS", TM_HS_ATTRIBUTE[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TM_HS_ATTRIBUTE[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TM_HS_ATTRIBUTE[i].getBLOCK_CODE());
				    		jsonReturn.put("START_VALID", TM_HS_ATTRIBUTE[i].getSTART_VALID());
				    		jsonReturn.put("END_VALID", TM_HS_ATTRIBUTE[i].getEND_VALID());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_HS_ATTRIBUTE", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(70);
        		TM_POI[] TM_POI = new TM_POI[alTM_POI.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_POI.size(); i++)
        		{
        			TM_POI[i] = alTM_POI.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_POI[i].getNATIONAL().equals("null")||
		    				!TM_POI[i].getREGION_CODE().equals("null")||
		    				!TM_POI[i].getCOMP_CODE().equals("null")||
		    				!TM_POI[i].getEST_CODE().equals("null")||
		    				!TM_POI[i].getWERKS().equals("null")||
		    				!TM_POI[i].getAFD_CODE().equals("null")||
		    				!TM_POI[i].getBLOCK_CODE().equals("null")||
		    				!TM_POI[i].getBLOCK_CODE_GIS().equals("null")||
		    				!TM_POI[i].getPOI_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TM_POI[i].getNATIONAL());
				    		jsonReturn.put("REGION_CODE", TM_POI[i].getREGION_CODE());
				    		jsonReturn.put("COMP_CODE", TM_POI[i].getCOMP_CODE());
				    		jsonReturn.put("EST_CODE", TM_POI[i].getEST_CODE());
				    		jsonReturn.put("WERKS", TM_POI[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TM_POI[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TM_POI[i].getBLOCK_CODE());
				    		jsonReturn.put("BLOCK_CODE_GIS", TM_POI[i].getBLOCK_CODE_GIS());
				    		jsonReturn.put("POI_CODE", TM_POI[i].getPOI_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_POI", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(71);
        		TR_HS_LAND_USE_DETAIL[] TR_HS_LAND_USE_DETAIL = new TR_HS_LAND_USE_DETAIL[alTR_HS_LAND_USE_DETAIL.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_HS_LAND_USE_DETAIL.size(); i++)
        		{
        			TR_HS_LAND_USE_DETAIL[i] = alTR_HS_LAND_USE_DETAIL.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_HS_LAND_USE_DETAIL[i].getNATIONAL().equals("null")||
		    				!TR_HS_LAND_USE_DETAIL[i].getREGION_CODE().equals("null")||
		    				!TR_HS_LAND_USE_DETAIL[i].getCOMP_CODE().equals("null")||
		    				!TR_HS_LAND_USE_DETAIL[i].getEST_CODE().equals("null")||
		    				!TR_HS_LAND_USE_DETAIL[i].getWERKS().equals("null")||
		    				!TR_HS_LAND_USE_DETAIL[i].getAFD_CODE().equals("null")||
		    				!TR_HS_LAND_USE_DETAIL[i].getBLOCK_CODE().equals("null")||
		    				!TR_HS_LAND_USE_DETAIL[i].getBLOCK_CODE_GIS().equals("null")||
		    				!TR_HS_LAND_USE_DETAIL[i].getLAND_USE_CODE().equals("null")||
		    				!TR_HS_LAND_USE_DETAIL[i].getLAND_USE_CODE_GIS().equals("null")||
		    				!TR_HS_LAND_USE_DETAIL[i].getSPMON().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TR_HS_LAND_USE_DETAIL[i].getNATIONAL());
				    		jsonReturn.put("REGION_CODE", TR_HS_LAND_USE_DETAIL[i].getREGION_CODE());
				    		jsonReturn.put("COMP_CODE", TR_HS_LAND_USE_DETAIL[i].getCOMP_CODE());
				    		jsonReturn.put("EST_CODE", TR_HS_LAND_USE_DETAIL[i].getEST_CODE());
				    		jsonReturn.put("WERKS", TR_HS_LAND_USE_DETAIL[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_HS_LAND_USE_DETAIL[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_HS_LAND_USE_DETAIL[i].getBLOCK_CODE());
				    		jsonReturn.put("BLOCK_CODE_GIS", TR_HS_LAND_USE_DETAIL[i].getBLOCK_CODE_GIS());
				    		jsonReturn.put("LAND_USE_CODE", TR_HS_LAND_USE_DETAIL[i].getLAND_USE_CODE());
				    		jsonReturn.put("LAND_USE_CODE_GIS", TR_HS_LAND_USE_DETAIL[i].getLAND_USE_CODE_GIS());
				    		jsonReturn.put("SPMON", TR_HS_LAND_USE_DETAIL[i].getSPMON());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_HS_LAND_USE_DETAIL", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(72);
        		TR_HS_LAND_USE[] TR_HS_LAND_USE = new TR_HS_LAND_USE[alTR_HS_LAND_USE.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_HS_LAND_USE.size(); i++)
        		{
        			TR_HS_LAND_USE[i] = alTR_HS_LAND_USE.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_HS_LAND_USE[i].getNATIONAL().equals("null")||
		    				!TR_HS_LAND_USE[i].getREGION_CODE().equals("null")||
		    				!TR_HS_LAND_USE[i].getCOMP_CODE().equals("null")||
		    				!TR_HS_LAND_USE[i].getEST_CODE().equals("null")||
		    				!TR_HS_LAND_USE[i].getWERKS().equals("null")||
		    				!TR_HS_LAND_USE[i].getAFD_CODE().equals("null")||
		    				!TR_HS_LAND_USE[i].getBLOCK_CODE().equals("null")||
		    				!TR_HS_LAND_USE[i].getBLOCK_CODE_GIS().equals("null")||
		    				!TR_HS_LAND_USE[i].getLAND_USE_CODE().equals("null")||
		    				!TR_HS_LAND_USE[i].getLAND_USE_CODE_GIS().equals("null")||
		    				!TR_HS_LAND_USE[i].getSPMON().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TR_HS_LAND_USE[i].getNATIONAL());
				    		jsonReturn.put("REGION_CODE", TR_HS_LAND_USE[i].getREGION_CODE());
				    		jsonReturn.put("COMP_CODE", TR_HS_LAND_USE[i].getCOMP_CODE());
				    		jsonReturn.put("EST_CODE", TR_HS_LAND_USE[i].getEST_CODE());
				    		jsonReturn.put("WERKS", TR_HS_LAND_USE[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_HS_LAND_USE[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_HS_LAND_USE[i].getBLOCK_CODE());
				    		jsonReturn.put("BLOCK_CODE_GIS", TR_HS_LAND_USE[i].getBLOCK_CODE_GIS());
				    		jsonReturn.put("LAND_USE_CODE", TR_HS_LAND_USE[i].getLAND_USE_CODE());
				    		jsonReturn.put("LAND_USE_CODE_GIS", TR_HS_LAND_USE[i].getLAND_USE_CODE_GIS());
				    		jsonReturn.put("SPMON", TR_HS_LAND_USE[i].getSPMON());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_HS_LAND_USE", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(73);
        		TR_HS_SUB_BLOCK[] TR_HS_SUB_BLOCK = new TR_HS_SUB_BLOCK[alTR_HS_SUB_BLOCK.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_HS_SUB_BLOCK.size(); i++)
        		{
        			TR_HS_SUB_BLOCK[i] = alTR_HS_SUB_BLOCK.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_HS_SUB_BLOCK[i].getNATIONAL().equals("null")||
		    				!TR_HS_SUB_BLOCK[i].getREGION_CODE().equals("null")||
		    				!TR_HS_SUB_BLOCK[i].getCOMP_CODE().equals("null")||
		    				!TR_HS_SUB_BLOCK[i].getEST_CODE().equals("null")||
		    				!TR_HS_SUB_BLOCK[i].getWERKS().equals("null")||
		    				!TR_HS_SUB_BLOCK[i].getAFD_CODE().equals("null")||
		    				!TR_HS_SUB_BLOCK[i].getBLOCK_CODE().equals("null")||
		    				!TR_HS_SUB_BLOCK[i].getBLOCK_CODE_GIS().equals("null")||
		    				!TR_HS_SUB_BLOCK[i].getSUB_BLOCK_CODE().equals("null")||
		    				!TR_HS_SUB_BLOCK[i].getLAND_USE_CODE_GIS().equals("null")||
		    				!TR_HS_SUB_BLOCK[i].getSPMON().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TR_HS_SUB_BLOCK[i].getNATIONAL());
				    		jsonReturn.put("REGION_CODE", TR_HS_SUB_BLOCK[i].getREGION_CODE());
				    		jsonReturn.put("COMP_CODE", TR_HS_SUB_BLOCK[i].getCOMP_CODE());
				    		jsonReturn.put("EST_CODE", TR_HS_SUB_BLOCK[i].getEST_CODE());
				    		jsonReturn.put("WERKS", TR_HS_SUB_BLOCK[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_HS_SUB_BLOCK[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_HS_SUB_BLOCK[i].getBLOCK_CODE());
				    		jsonReturn.put("BLOCK_CODE_GIS", TR_HS_SUB_BLOCK[i].getBLOCK_CODE_GIS());
				    		jsonReturn.put("SUB_BLOCK_CODE", TR_HS_SUB_BLOCK[i].getSUB_BLOCK_CODE());
				    		jsonReturn.put("LAND_USE_CODE_GIS", TR_HS_SUB_BLOCK[i].getLAND_USE_CODE_GIS());
				    		jsonReturn.put("SPMON", TR_HS_SUB_BLOCK[i].getSPMON());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}

			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_HS_SUB_BLOCK", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(74);
        		TR_HS_UNPLANTED[] TR_HS_UNPLANTED = new TR_HS_UNPLANTED[alTR_HS_UNPLANTED.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_HS_UNPLANTED.size(); i++)
        		{
        			TR_HS_UNPLANTED[i] = alTR_HS_UNPLANTED.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_HS_UNPLANTED[i].getNATIONAL().equals("null")||
		    				!TR_HS_UNPLANTED[i].getREGION_CODE().equals("null")||
		    				!TR_HS_UNPLANTED[i].getCOMP_CODE().equals("null")||
		    				!TR_HS_UNPLANTED[i].getEST_CODE().equals("null")||
		    				!TR_HS_UNPLANTED[i].getWERKS().equals("null")||
		    				!TR_HS_UNPLANTED[i].getAFD_CODE().equals("null")||
		    				!TR_HS_UNPLANTED[i].getAFD_CODE_GIS().equals("null")||
		    				!TR_HS_UNPLANTED[i].getLAND_CAT().equals("null")||
		    				!TR_HS_UNPLANTED[i].getLAND_CAT_L1_CODE().equals("null")||
		    				!TR_HS_UNPLANTED[i].getSPMON().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TR_HS_UNPLANTED[i].getNATIONAL());
				    		jsonReturn.put("REGION_CODE", TR_HS_UNPLANTED[i].getREGION_CODE());
				    		jsonReturn.put("COMP_CODE", TR_HS_UNPLANTED[i].getCOMP_CODE());
				    		jsonReturn.put("EST_CODE", TR_HS_UNPLANTED[i].getEST_CODE());
				    		jsonReturn.put("WERKS", TR_HS_UNPLANTED[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_HS_UNPLANTED[i].getAFD_CODE());
				    		jsonReturn.put("AFD_CODE_GIS", TR_HS_UNPLANTED[i].getAFD_CODE_GIS());
				    		jsonReturn.put("SPMON", TR_HS_UNPLANTED[i].getSPMON());
				    		jsonReturn.put("LAND_CAT", TR_HS_UNPLANTED[i].getLAND_CAT());
				    		jsonReturn.put("LAND_CAT_L1_CODE", TR_HS_UNPLANTED[i].getLAND_CAT_L1_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_HS_UNPLANTED", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(76);
        		TR_PALM[] TR_PALM = new TR_PALM[alTR_PALM.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_PALM.size(); i++)
        		{
        			TR_PALM[i] = alTR_PALM.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_PALM[i].getNATIONAL().equals("null")||
		    				!TR_PALM[i].getREGION_CODE().equals("null")||
		    				!TR_PALM[i].getCOMP_CODE().equals("null")||
		    				!TR_PALM[i].getEST_CODE().equals("null")||
		    				!TR_PALM[i].getWERKS().equals("null")||
		    				!TR_PALM[i].getAFD_CODE().equals("null")||
		    				!TR_PALM[i].getBLOCK_CODE().equals("null")||
		    				!TR_PALM[i].getBLOCK_CODE_GIS().equals("null")||
		    				!TR_PALM[i].getLAND_USE_CODE_GIS().equals("null")||
		    				!TR_PALM[i].getSPMON().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TR_PALM[i].getNATIONAL());
							jsonReturn.put("REGION_CODE", TR_PALM[i].getREGION_CODE());
							jsonReturn.put("COMP_CODE", TR_PALM[i].getCOMP_CODE());
							jsonReturn.put("EST_CODE", TR_PALM[i].getEST_CODE());
							jsonReturn.put("WERKS", TR_PALM[i].getWERKS());
							jsonReturn.put("AFD_CODE", TR_PALM[i].getAFD_CODE());
							jsonReturn.put("BLOCK_CODE", TR_PALM[i].getBLOCK_CODE());
							jsonReturn.put("BLOCK_CODE_GIS", TR_PALM[i].getBLOCK_CODE_GIS());
							jsonReturn.put("LAND_USE_CODE_GIS", TR_PALM[i].getLAND_USE_CODE_GIS());
							jsonReturn.put("SPMON", TR_PALM[i].getSPMON());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_PALM", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(78);
        		TM_REGION[] TM_REGION = new TM_REGION[alTM_REGION.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_REGION.size(); i++)
        		{
        			TM_REGION[i] = alTM_REGION.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_REGION[i].getNATIONAL().equals("null")||
		    				!TM_REGION[i].getREGION_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TM_REGION[i].getNATIONAL());
							jsonReturn.put("REGION_CODE", TM_REGION[i].getREGION_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_REGION", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(79);
        		TM_COMP[] TM_COMP = new TM_COMP[alTM_COMP.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_COMP.size(); i++)
        		{
        			TM_COMP[i] = alTM_COMP.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_COMP[i].getNATIONAL().equals("null")||
		    				!TM_COMP[i].getREGION_CODE().equals("null")||
		    				!TM_COMP[i].getCOMP_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TM_COMP[i].getNATIONAL());
							jsonReturn.put("REGION_CODE", TM_COMP[i].getREGION_CODE());
							jsonReturn.put("COMP_CODE", TM_COMP[i].getCOMP_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_COMP", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(80);
        		TM_EST[] TM_EST = new TM_EST[alTM_EST.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_EST.size(); i++)
        		{
        			TM_EST[i] = alTM_EST.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_EST[i].getNATIONAL().equals("null")||
		    				!TM_EST[i].getREGION_CODE().equals("null")||
		    				!TM_EST[i].getCOMP_CODE().equals("null")||
		    				!TM_EST[i].getEST_CODE().equals("null")||
		    				!TM_EST[i].getWERKS().equals("null")||
		    				!TM_EST[i].getSTART_VALID().equals("null")||
		    				!TM_EST[i].getEND_VALID().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TM_EST[i].getNATIONAL());
							jsonReturn.put("REGION_CODE", TM_EST[i].getREGION_CODE());
							jsonReturn.put("COMP_CODE", TM_EST[i].getCOMP_CODE());
							jsonReturn.put("EST_CODE", TM_EST[i].getEST_CODE());
							jsonReturn.put("WERKS", TM_EST[i].getWERKS());
							jsonReturn.put("START_VALID", TM_EST[i].getSTART_VALID());
							jsonReturn.put("END_VALID", TM_EST[i].getEND_VALID());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_EST", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(85);
        		TM_AFD[] TM_AFD = new TM_AFD[alTM_AFD.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_AFD.size(); i++)
        		{
        			TM_AFD[i] = alTM_AFD.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_AFD[i].getNATIONAL().equals("null")||
		    				!TM_AFD[i].getREGION_CODE().equals("null")||
		    				!TM_AFD[i].getCOMP_CODE().equals("null")||
		    				!TM_AFD[i].getEST_CODE().equals("null")||
		    				!TM_AFD[i].getWERKS().equals("null")||
		    				!TM_AFD[i].getAFD_CODE().equals("null")||
		    				!TM_AFD[i].getAFD_CODE_GIS().equals("null")||
		    				!TM_AFD[i].getSTART_VALID().equals("null")||
		    				!TM_AFD[i].getEND_VALID().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TM_AFD[i].getNATIONAL());
							jsonReturn.put("REGION_CODE", TM_AFD[i].getREGION_CODE());
							jsonReturn.put("COMP_CODE", TM_AFD[i].getCOMP_CODE());
							jsonReturn.put("EST_CODE", TM_AFD[i].getEST_CODE());
							jsonReturn.put("WERKS", TM_AFD[i].getWERKS());
							jsonReturn.put("AFD_CODE", TM_AFD[i].getAFD_CODE());
							jsonReturn.put("AFD_CODE_GIS", TM_AFD[i].getAFD_CODE_GIS());
							jsonReturn.put("START_VALID", TM_AFD[i].getSTART_VALID());
							jsonReturn.put("END_VALID", TM_AFD[i].getEND_VALID());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_AFD", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(87);
        		TM_BLOCK[] TM_BLOCK = new TM_BLOCK[alTM_BLOCK.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_BLOCK.size(); i++)
        		{
        			TM_BLOCK[i] = alTM_BLOCK.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_BLOCK[i].getNATIONAL().equals("null")||
		    				!TM_BLOCK[i].getREGION_CODE().equals("null")||
		    				!TM_BLOCK[i].getCOMP_CODE().equals("null")||
		    				!TM_BLOCK[i].getEST_CODE().equals("null")||
		    				!TM_BLOCK[i].getWERKS().equals("null")||
		    				!TM_BLOCK[i].getAFD_CODE().equals("null")||
		    				!TM_BLOCK[i].getBLOCK_CODE().equals("null")||
		    				!TM_BLOCK[i].getBLOCK_CODE_GIS().equals("null")||
		    				!TM_BLOCK[i].getSTART_VALID().equals("null")||
		    				!TM_BLOCK[i].getEND_VALID().equals("null")) 
			    		{
				    		jsonReturn.put("NATIONAL", TM_BLOCK[i].getNATIONAL());
							jsonReturn.put("REGION_CODE", TM_BLOCK[i].getREGION_CODE());
							jsonReturn.put("COMP_CODE", TM_BLOCK[i].getCOMP_CODE());
							jsonReturn.put("EST_CODE", TM_BLOCK[i].getEST_CODE());
							jsonReturn.put("WERKS", TM_BLOCK[i].getWERKS());
							jsonReturn.put("AFD_CODE", TM_BLOCK[i].getAFD_CODE());
							jsonReturn.put("BLOCK_CODE", TM_BLOCK[i].getBLOCK_CODE());
							jsonReturn.put("BLOCK_CODE_GIS", TM_BLOCK[i].getBLOCK_CODE_GIS());
							jsonReturn.put("START_VALID", TM_BLOCK[i].getSTART_VALID());
							jsonReturn.put("END_VALID", TM_BLOCK[i].getEND_VALID());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_BLOCK", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				} 

        		//publishProgress(88);
        		TM_EMPLOYEE[] TM_EMPLOYEE = new TM_EMPLOYEE[alTM_EMPLOYEE.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_EMPLOYEE.size(); i++)
        		{
        			TM_EMPLOYEE[i] = alTM_EMPLOYEE.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_EMPLOYEE[i].getEMPLOYEE_NIK().equals("null")) 
			    		{
				    		jsonReturn.put("EMPLOYEE_NIK", TM_EMPLOYEE[i].getEMPLOYEE_NIK());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			sNext = "NO";/*
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  */
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_EMPLOYEE", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(89);
        		TM_LOGIN[] TM_LOGIN = new TM_LOGIN[alTM_LOGIN.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_LOGIN.size(); i++)
        		{
        			TM_LOGIN[i] = alTM_LOGIN.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_LOGIN[i].getEMPLOYEE_NIK().equals("null")) 
			    		{
				    		jsonReturn.put("EMPLOYEE_NIK", TM_LOGIN[i].getEMPLOYEE_NIK());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_LOGIN", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(90);
        		TM_USER_AUTH[] TM_USER_AUTH = new TM_USER_AUTH[alTM_USER_AUTH.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_USER_AUTH.size(); i++)
        		{
        			TM_USER_AUTH[i] = alTM_USER_AUTH.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_USER_AUTH[i].getEMPLOYEE_NIK().equals("null")||
			    			!TM_USER_AUTH[i].getUSER_ROLE().equals("null")||
			    			!TM_USER_AUTH[i].getREFERENCE_ROLE().equals("null")||
			    			!TM_USER_AUTH[i].getLOCATION_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("EMPLOYEE_NIK", TM_USER_AUTH[i].getEMPLOYEE_NIK());
							jsonReturn.put("USER_ROLE", TM_USER_AUTH[i].getUSER_ROLE());
							jsonReturn.put("REFERENCE_ROLE", TM_USER_AUTH[i].getREFERENCE_ROLE());
							jsonReturn.put("LOCATION_CODE", TM_USER_AUTH[i].getLOCATION_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_USER_AUTH", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(91);
        		TM_PARAMETER[] TM_PARAMETER = new TM_PARAMETER[alTM_PARAMETER.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_PARAMETER.size(); i++)
        		{
        			TM_PARAMETER[i] = alTM_PARAMETER.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_PARAMETER[i].getGROUP_CODE().equals("null")||
			    			!TM_PARAMETER[i].getPARAM_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("GROUP_CODE", TM_PARAMETER[i].getGROUP_CODE());
							jsonReturn.put("PARAM_CODE", TM_PARAMETER[i].getPARAM_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_PARAMETER", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(92);
        		TM_CONTENT_INSPECTION[] TM_CONTENT_INSPECTION = new TM_CONTENT_INSPECTION[alTM_CONTENT_INSPECTION.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_CONTENT_INSPECTION.size(); i++)
        		{
        			TM_CONTENT_INSPECTION[i] = alTM_CONTENT_INSPECTION.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_CONTENT_INSPECTION[i].getCONTENT_INSPECT_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("CONTENT_INSPECT_CODE", TM_CONTENT_INSPECTION[i].getCONTENT_INSPECT_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_CONTENT_INSPECTION", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(93);
        		T_RANGE[] T_RANGE = new T_RANGE[alT_RANGE.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alT_RANGE.size(); i++)
        		{
        			T_RANGE[i] = alT_RANGE.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!T_RANGE[i].getRANGE_CODE().equals("null")||
			    			!T_RANGE[i].getSTART_VALID().equals("null")||
			    			!T_RANGE[i].getEND_VALID().equals("null")||
			    			!T_RANGE[i].getVALUE_5().equals("null")) 
			    		{
				    		jsonReturn.put("RANGE_CODE", T_RANGE[i].getRANGE_CODE());
							jsonReturn.put("START_VALID", T_RANGE[i].getSTART_VALID());
							jsonReturn.put("END_VALID", T_RANGE[i].getEND_VALID());
							jsonReturn.put("VALUE_5", T_RANGE[i].getVALUE_5());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("T_RANGE", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(94);
        		TM_KUALITAS_PANEN[] TM_KUALITAS_PANEN = new TM_KUALITAS_PANEN[alTM_KUALITAS_PANEN.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_KUALITAS_PANEN.size(); i++)
        		{
        			TM_KUALITAS_PANEN[i] = alTM_KUALITAS_PANEN.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_KUALITAS_PANEN[i].getID_KUALITAS().equals("null")) 
			    		{
				    		jsonReturn.put("ID_KUALITAS", TM_KUALITAS_PANEN[i].getID_KUALITAS());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_KUALITAS_PANEN", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(94);
        		TM_CONTENT_LABEL[] TM_CONTENT_LABEL = new TM_CONTENT_LABEL[alTM_CONTENT_LABEL.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTM_CONTENT_LABEL.size(); i++)
        		{
        			TM_CONTENT_LABEL[i] = alTM_CONTENT_LABEL.get(i);
        		
        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TM_CONTENT_LABEL[i].getCONTENT_INSPECT_CODE().equals("null")||
			    			!TM_CONTENT_LABEL[i].getLABEL_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("CONTENT_INSPECT_CODE", TM_CONTENT_LABEL[i].getCONTENT_INSPECT_CODE());
				    		jsonReturn.put("LABEL_CODE", TM_CONTENT_LABEL[i].getLABEL_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TM_CONTENT_LABEL", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(95);
                TR_FAC_TODOLIST[] TR_FAC_TODOLIST = new TR_FAC_TODOLIST[alTR_FAC_TODOLIST.size()];
                jsonArray = new JSONArray();
        		for(int i=0; i< alTR_FAC_TODOLIST.size(); i++)
        		{
        			TR_FAC_TODOLIST[i] = alTR_FAC_TODOLIST.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_FAC_TODOLIST[i].getFAC_TDL_CODE().equals("null")||
			    			!TR_FAC_TODOLIST[i].getWERKS().equals("null")||
			    			!TR_FAC_TODOLIST[i].getAFD_CODE().equals("null")||
			    			!TR_FAC_TODOLIST[i].getBLOCK_CODE().equals("null")||
			    			!TR_FAC_TODOLIST[i].getPOI_CODE().equals("null")||
			    			!TR_FAC_TODOLIST[i].getPOI_TYPE().equals("null")||
			    			!TR_FAC_TODOLIST[i].getPOI_NAME().equals("null")) 
			    		{
				    		jsonReturn.put("FAC_TDL_CODE", TR_FAC_TODOLIST[i].getFAC_TDL_CODE());
				    		jsonReturn.put("WERKS", TR_FAC_TODOLIST[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_FAC_TODOLIST[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_FAC_TODOLIST[i].getBLOCK_CODE());
				    		jsonReturn.put("POI_CODE", TR_FAC_TODOLIST[i].getPOI_CODE());
				    		jsonReturn.put("POI_TYPE", TR_FAC_TODOLIST[i].getPOI_TYPE());
				    		jsonReturn.put("POI_NAME", TR_FAC_TODOLIST[i].getPOI_NAME());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_FAC_TODOLIST", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(95);
        		TR_INFRA_TODOLIST[] TR_INFRA_TODOLIST = new TR_INFRA_TODOLIST[alTR_INFRA_TODOLIST.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_INFRA_TODOLIST.size(); i++)
        		{
        			TR_INFRA_TODOLIST[i] = alTR_INFRA_TODOLIST.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_INFRA_TODOLIST[i].getINFRA_TDL_CODE().equals("null")||
			    			!TR_INFRA_TODOLIST[i].getWERKS().equals("null")||
			    			!TR_INFRA_TODOLIST[i].getAFD_CODE().equals("null")||
			    			!TR_INFRA_TODOLIST[i].getBLOCK_CODE().equals("null")||
			    			!TR_INFRA_TODOLIST[i].getPOI_CODE().equals("null")||
			    			!TR_INFRA_TODOLIST[i].getPOI_TYPE().equals("null")||
			    			!TR_INFRA_TODOLIST[i].getPOI_NAME().equals("null")) 
			    		{
				    		jsonReturn.put("INFRA_TDL_CODE", TR_INFRA_TODOLIST[i].getINFRA_TDL_CODE());
				    		jsonReturn.put("WERKS", TR_INFRA_TODOLIST[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_INFRA_TODOLIST[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_INFRA_TODOLIST[i].getBLOCK_CODE());
				    		jsonReturn.put("POI_CODE", TR_INFRA_TODOLIST[i].getPOI_CODE());
				    		jsonReturn.put("POI_TYPE", TR_INFRA_TODOLIST[i].getPOI_TYPE());
				    		jsonReturn.put("POI_NAME", TR_INFRA_TODOLIST[i].getPOI_NAME());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_INFRA_TODOLIST", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(96);
        		TR_BLOCK_TODOLIST[] TR_BLOCK_TODOLIST = new TR_BLOCK_TODOLIST[alTR_BLOCK_TODOLIST.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_BLOCK_TODOLIST.size(); i++)
        		{
        			TR_BLOCK_TODOLIST[i] = alTR_BLOCK_TODOLIST.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_BLOCK_TODOLIST[i].getBLOCK_TDL_CODE().equals("null")||
			    			!TR_BLOCK_TODOLIST[i].getWERKS().equals("null")||
			    			!TR_BLOCK_TODOLIST[i].getAFD_CODE().equals("null")||
			    			!TR_BLOCK_TODOLIST[i].getBLOCK_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("BLOCK_TDL_CODE", TR_BLOCK_TODOLIST[i].getBLOCK_TDL_CODE());
				    		jsonReturn.put("WERKS", TR_BLOCK_TODOLIST[i].getWERKS());
				    		jsonReturn.put("AFD_CODE", TR_BLOCK_TODOLIST[i].getAFD_CODE());
				    		jsonReturn.put("BLOCK_CODE", TR_BLOCK_TODOLIST[i].getBLOCK_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_BLOCK_TODOLIST", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
        		//publishProgress(97);
        		/*
        		TR_INSPECTION_HISTORY[] TR_INSPECTION_HISTORY = new TR_INSPECTION_HISTORY[alTR_INSPECTION_HISTORY.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_INSPECTION_HISTORY.size(); i++)
        		{
        			TR_INSPECTION_HISTORY[i] = alTR_INSPECTION_HISTORY.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		jsonReturn.put("BLOCK_INSPECT_CODE", TR_INSPECTION_HISTORY[i].getBLOCK_INSPECT_CODE());
			    		jsonReturn.put("WERKS", TR_INSPECTION_HISTORY[i].getWERKS());
			    		jsonReturn.put("AFD_CODE", TR_INSPECTION_HISTORY[i].getAFD_CODE());
			    		jsonReturn.put("BLOCK_CODE", TR_INSPECTION_HISTORY[i].getBLOCK_CODE());
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_INSPECTION_HISTORY", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
        		*/      		

        		//publishProgress(97);
        		TR_TODOLIST_HISTORY[] TR_TODOLIST_HISTORY = new TR_TODOLIST_HISTORY[alTR_TODOLIST_HISTORY.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_TODOLIST_HISTORY.size(); i++)
        		{
        			TR_TODOLIST_HISTORY[i] = alTR_TODOLIST_HISTORY.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_TODOLIST_HISTORY[i].getTR_CODE().equals("null")||
			    			!TR_TODOLIST_HISTORY[i].getCREATED_TIME().equals("null")||
			    			!TR_TODOLIST_HISTORY[i].getCREATED_USER().equals("null")) 
			    		{
				    		jsonReturn.put("TR_CODE", TR_TODOLIST_HISTORY[i].getTR_CODE());
				    		jsonReturn.put("CREATED_TIME", TR_TODOLIST_HISTORY[i].getCREATED_TIME());
				    		jsonReturn.put("CREATED_USER", TR_TODOLIST_HISTORY[i].getCREATED_USER());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_TODOLIST_HISTORY", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
        		
        		//publishProgress(98);
        		TR_NEWS[] TR_NEWS = new TR_NEWS[alTR_NEWS.size()];
        		jsonArray = new JSONArray();
        		for(int i=0; i< alTR_NEWS.size(); i++)
        		{
        			TR_NEWS[i] = alTR_NEWS.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_NEWS[i].getWERKS().equals("null")||
			    			!TR_NEWS[i].getNEWS_CODE().equals("null")) 
			    		{
				    		jsonReturn.put("WERKS", TR_NEWS[i].getWERKS());
				    		jsonReturn.put("NEWS_CODE", TR_NEWS[i].getNEWS_CODE());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_NEWS", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}

        		//publishProgress(99);
        		TR_IMAGE[] TR_IMAGE = new TR_IMAGE[alTR_IMAGE.size()];
        		jsonArray = new JSONArray();
        		alImageName = new ArrayList<String>();
        		for(int i=0; i< alTR_IMAGE.size(); i++)
        		{
        			TR_IMAGE[i] = alTR_IMAGE.get(i);

        			JSONObject jsonReturn = new JSONObject();
			    	try 
			    	{
			    		if (!TR_IMAGE[i].getTR_CODE().equals("null")||
			    			!TR_IMAGE[i].getTR_TYPE().equals("null")||
			    			!TR_IMAGE[i].getIMAGE_NAME().equals("null")) 
			    		{
				    		jsonReturn.put("TR_CODE", TR_IMAGE[i].getTR_CODE());
				    		jsonReturn.put("TR_TYPE", TR_IMAGE[i].getTR_TYPE());
				    		jsonReturn.put("IMAGE_NAME", TR_IMAGE[i].getIMAGE_NAME());
						}
			    		else
			    		{
			    			this.cancel(true);
			    			
			    			ot.setErrorReset(getString(R.string.Download_not_Complete));
                        	Intent intent = new Intent(Connection.this, MainMenu.class);
               		        startActivity(intent);
               		        Connection.this.finish();  
			    		}
			    		
			    		jsonArray.put(jsonReturn);
			    		
			    		alImageName.add(TR_IMAGE[i].getIMAGE_NAME());
			    	} catch (JSONException ex) {
			    	    ex.printStackTrace();
			    	}
        		}
        		
        		try 
		    	{
					jsonobj.put("TR_IMAGE", jsonArray);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}  
        		
        		ObjectTransfer ot = (ObjectTransfer) getApplication();
        		ot.setTr_hs_region(TR_HS_REGION);
                ot.setTr_hs_comp(TR_HS_COMP);
                ot.setTr_hs_est(TR_HS_EST);
                ot.setTr_hs_afd(TR_HS_AFD);
                ot.setTr_hs_block(TR_HS_BLOCK);
            	
                ot.setTm_hs_attribute(TM_HS_ATTRIBUTE);
                ot.setTm_poi(TM_POI);
                ot.setTr_hs_land_use_detail(TR_HS_LAND_USE_DETAIL);
                ot.setTr_hs_land_use(TR_HS_LAND_USE);
                ot.setTr_hs_sub_block(TR_HS_SUB_BLOCK);
                ot.setTr_hs_unplanted(TR_HS_UNPLANTED);
                ot.setTr_palm(TR_PALM);
        		
                ot.setTm_region(TM_REGION);
                ot.setTm_comp(TM_COMP);
                ot.setTm_werks(TM_EST);
                ot.setTm_afd(TM_AFD);
                ot.setTm_block(TM_BLOCK);
                ot.setTm_employee(TM_EMPLOYEE);
                ot.setTm_login(TM_LOGIN);
                ot.setTm_user_auth(TM_USER_AUTH);
                ot.setTm_parameter(TM_PARAMETER);
                ot.setTm_content_inspection(TM_CONTENT_INSPECTION);
                ot.setT_range(T_RANGE);
                ot.setTm_kualitas_panen(TM_KUALITAS_PANEN);
                ot.setTm_content_label(TM_CONTENT_LABEL);

                // Added by Robin 20140805
                ot.setTr_fac_todolist(TR_FAC_TODOLIST);
                ot.setTr_infra_todolist(TR_INFRA_TODOLIST);
                ot.setTr_block_todolist(TR_BLOCK_TODOLIST);
                //ot.setTr_inspection_history(TR_INSPECTION_HISTORY);
                ot.setTr_image(TR_IMAGE);
                ot.setTr_todolist_history(TR_TODOLIST_HISTORY);
                ot.setTr_news(TR_NEWS);
                //
                ot.setTm_server(TM_SERVER);
                
                // added by Adit, 20140806
                ot.setTr_performance_daily_quality(TR_PERFORMANCE_DAILY_QUALITY);
                ot.setTr_performance_daily_pinalty(TR_PERFORMANCE_DAILY_PINALTY);
                ot.setTr_performance_daily_delivery(TR_PERFORMANCE_DAILY_DELIVERY);
                ot.setTr_performance_productivity(TR_PERFORMANCE_PRODUCTIVITY);
                ot.setTr_performance_estate_production(TR_PERFORMANCE_ESTATE_PRODUCTION);
                ot.setTr_performance_daily_harv(TR_PERFORMANCE_DAILY_HARV);
                ot.setTr_performance_estate_production_block(TR_PERFORMANCE_ESTATE_PRODUCTION_BLOCK);
                ot.setT_delivery_favorite(T_DELIVERY_FAVORITE);
                ot.setTr_block_inspection(TR_BLOCK_INSPECTION);
                ot.setTr_block_indicator(TR_BLOCK_INDICATOR);
                ot.setTr_block_color_report(TR_BLOCK_COLOR_REPORT);
                ot.setTr_areal_inspection(TR_AREAL_INSPECTION);
                ot.setTr_pokok_inspection(TR_POKOK_INSPECTION);
                ot.setTr_content_value(TR_CONTENT_VALUE);
                //
            	

        		//publishProgress(100);
        	}
        }
        
        public void parseJSONCheck()
        {
        	boolean Data = false;
        	boolean Map = false;
        	boolean Manual = false;
        	boolean Apk = false;
        	String sData, sMap, sManual, sApk;
        	try
        	{
        		sData = jObj.getString("DATA");
    			sManual = jObj.getString("MANUAL");
        		sMap = jObj.getString("MAP");
        		sApk = jObj.getString("APK");
        		
        		if(sData.equals("YES"))
        		{
        			Data = true;
        		}
        		if(sManual.equals("YES"))
        		{
        			Map = true;
        		}
        		if(sMap.equals("YES"))
        		{
        			Manual = true;
        		}
        		if(sApk.equals("YES"))
        		{
        			Apk = true;
        		}
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
        	finally
        	{
        		publishProgress(100);
                dialogUpdate(Data, Map, Manual, Apk);
        	}
        }
        
        public void sentJSON()
        {
        	// Now lets begin with the server part
            try {
            	DefaultHttpClient httpclient = new DefaultHttpClient();
            	HttpPost httppostreq = new HttpPost(wurl);
            	System.out.println("wurl: "+wurl);
            	StringEntity se = new StringEntity(jsonobj.toString());
            	System.out.println("OUTPUT HERE: "+jsonobj.toString());
            	
            	String feedback=jsonobj.toString();
            	if(feedback.contains("WRITE"))
            	{
            		FileWriter fwr = new FileWriter(new File(ot.getDirectory().getAbsolutePath(), "feedback.txt"));
                	fwr.append(jsonobj.toString());
                	fwr.flush();
                	fwr.close();
            	}
            	
            	se.setContentType("application/json;charset=UTF-8");
            	se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
            	httppostreq.setEntity(se);
            	HttpResponse httpresponse = httpclient.execute(httppostreq);
            	HttpEntity resultentity = httpresponse.getEntity();
            	if(resultentity != null) {
            		InputStream inputstream = resultentity.getContent();
            		Header contentencoding = httpresponse.getFirstHeader("Content-Encoding");
            		if(contentencoding != null && contentencoding.getValue().equalsIgnoreCase("gzip")) {
            			inputstream = new GZIPInputStream(inputstream);
            		}
            		//publishProgress(20);
                    
            		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, "iso-8859-1"), 8);
            		StringBuilder sb = new StringBuilder();
            		String line = null;
            		while ((line = reader.readLine()) != null)
            		{
            			sb.append(line+" \n");
            		}
            		String resultstring = sb.toString();
            		
            		//String resultstring = convertStreamToString(inputstream);
            		inputstream.close();
            		resultstring = "{"+resultstring.substring(1,resultstring.length()-1)+"}";
            		System.out.println(resultstring);
            		
            		ObjectTransfer ot = (ObjectTransfer) getApplication();
                	FileWriter fw = new FileWriter(new File(ot.getDirectory().getAbsolutePath(), "log.txt"));
            		fw.append(resultstring);
            		fw.flush();
            		fw.close();
            		//publishProgress(30);
            		
            		jObj = new JSONObject( resultstring );
            		if(jsonTASK.equals("Check_Update"))
            		{
                		sStatus = jObj.getString("UPDATE");
            		}
            		else
            		{
                		sStatus = jObj.getString("SUCCESS");
            		}
            		sMessage = jObj.getString("MESSAGE");
            	}
            } catch (Exception e) {
            	e.printStackTrace();
            	System.out.println("ERROR sentJSON :" + e);
    		}
        }

        public void sentJSONforImage()
        {
        	// Now lets begin with the server part
            try {
            	DefaultHttpClient httpclient = new DefaultHttpClient();
System.out.println("alImageName Size: "+alImageName.size());            	
            	for(int x=0; x<alImageName.size(); x++)
            	{
                	HttpPost httppostreq = new HttpPost(wurlImage);
                	
					jsonobjforimage = new JSONObject();
					jsonobjforimage.put("DATA", "YES");
					jsonobjforimage.put("IMAGE_NAME", alImageName.get(x));
					
                	StringEntity se = new StringEntity(jsonobjforimage.toString());
System.out.println(jsonobjforimage.toString());
                	se.setContentType("application/json;charset=UTF-8");
                	se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                	httppostreq.setEntity(se);
                	HttpResponse httpresponse = httpclient.execute(httppostreq);
                	HttpEntity resultentity = httpresponse.getEntity();
                	if(resultentity != null) {
                		InputStream inputstream = resultentity.getContent();
                		Header contentencoding = httpresponse.getFirstHeader("Content-Encoding");
                		if(contentencoding != null && contentencoding.getValue().equalsIgnoreCase("gzip")) {
                			inputstream = new GZIPInputStream(inputstream);
                		}
                		//publishProgress(15);
                		
                		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, "iso-8859-1"), 8);
                		StringBuilder sb = new StringBuilder();
                		String line = null;
                		while ((line = reader.readLine()) != null)
                		{
                			sb.append(line+" \n");
                		}
                		String resultstring = sb.toString();
                		
                		//String resultstring = convertStreamToString(inputstream);
                		inputstream.close();
                		resultstring = "{"+resultstring.substring(1,resultstring.length()-1)+"}";
                		System.out.println(resultstring);
                		
                		ObjectTransfer ot = (ObjectTransfer) getApplication();
                    	FileWriter fw = new FileWriter(new File(ot.getDirectory().getAbsolutePath(), "log.txt"));
                		fw.append(resultstring);
                		fw.flush();
                		fw.close();
                		//publishProgress(18);
                		
                		jObjIMAGE = new JSONObject(resultstring);
                		if (jObjIMAGE != null) 
                		{
                        	int progress = 0;
                			try 
                			{
                				JSONArray jaTR_IMAGE = jObjIMAGE.getJSONArray("TR_IMAGE");
                				
            					for (int i = 0; i < jaTR_IMAGE.length(); i++) 
            					{
            						JSONObject joTR_IMAGE = jaTR_IMAGE.getJSONObject(i);
            						
            						String filename = new File(ot.getDirectoryPhoto(),
            								joTR_IMAGE.getString("IMAGE_NAME") + ".jpg")
            								.getAbsolutePath();
            						Base64.decodeToFile(joTR_IMAGE.getString("IMAGE_FILE"),
            								filename);
            						/*
                    				progress = 20 + i;
                    				if(progress < 95)
                    				{
                                		publishProgress(progress);
                    				}
                    				*/
            					}
            				} 
                			catch (Exception e) 
            				{
            					e.printStackTrace();
            				}
                		}
                	}
            	}

        		//publishProgress(100);
            } catch (Exception e) {
            	e.printStackTrace();
    		}
        }
        
        public void getTotalMaster()
        {
        	// Now lets begin with the server part
            try {
            	String array[] = new String[]{"TM_REGION","TM_COMP","TM_EST","TM_AFD","TM_BLOCK","TM_EMPLOYEE","TM_LOGIN","TM_USER_AUTH",""
            			+ "TM_PARAMETER","TM_CONTENT_INSPECTION","T_RANGE","TM_KUALITAS_PANEN","TR_HS_REGION","TR_HS_COMP","TR_HS_EST",""
            					+ "TR_HS_AFD","TR_HS_BLOCK","TM_HS_ATTRIBUTE","TM_POI","TR_HS_LAND_USE_DETAIL","TR_HS_LAND_USE",""
            							+ "TR_HS_SUB_BLOCK","TR_HS_UNPLANTED","TR_PALM","TM_CONTENT_LABEL","TM_SERVER"}; 
            	
            	DefaultHttpClient httpclient = new DefaultHttpClient();
            	HttpPost httppostreq = new HttpPost(wurl);
            	
            	JSONObject jsonTotalMaster = new JSONObject();
            	ObjectTransfer ot = (ObjectTransfer) getApplication();
            	jsonTotalMaster.put("DEVICE_ID", ot.getDeviceID());
            	jsonTotalMaster.put("DATA", "YES");
            	jsonTotalMaster.put("TABLE", "JML_DATA");
				
            	StringEntity se = new StringEntity(jsonTotalMaster.toString());
System.out.println(jsonTotalMaster.toString());
            	se.setContentType("application/json;charset=UTF-8");
            	se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
            	httppostreq.setEntity(se);
            	HttpResponse httpresponse = httpclient.execute(httppostreq);
            	HttpEntity resultentity = httpresponse.getEntity();
            	if(resultentity != null) {
            		InputStream inputstream = resultentity.getContent();
            		Header contentencoding = httpresponse.getFirstHeader("Content-Encoding");
            		if(contentencoding != null && contentencoding.getValue().equalsIgnoreCase("gzip")) {
            			inputstream = new GZIPInputStream(inputstream);
            		}
            		
            		BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, "iso-8859-1"), 8);
            		StringBuilder sb = new StringBuilder();
            		String line = null;
            		while ((line = reader.readLine()) != null)
            		{
            			sb.append(line+" \n");
            		}
            		String resultstring = sb.toString();
            		
            		//String resultstring = convertStreamToString(inputstream);
            		inputstream.close();
            		resultstring = "{"+resultstring.substring(1,resultstring.length()-1)+"}";
            		System.out.println(resultstring);
            		
            		FileWriter fw = new FileWriter(new File(ot.getDirectory().getAbsolutePath(), "log.txt"));
            		fw.append(resultstring);
            		fw.flush();
            		fw.close();
            		//publishProgress(18);
            		
            		jObj = new JSONObject(resultstring);
            		if (jObj != null) 
            		{
                    	int progress = 0;
            			try 
            			{
            				for (int i = 0; i < array.length; i++) 
            				{
                				Database.sharedObject.insertT_TOTAL_MASTER(array[i], jObj.getInt(array[i]));
            				}
        				} 
            			catch (Exception e) 
        				{
        					e.printStackTrace();
        				}
            		}
            	}

        		//publishProgress(100);
            } catch (Exception e) {
            	e.printStackTrace();
    		}
        }
        
		public void sentJSONMaster()
        {
        	String array[] = new String[]{"TM_REGION","TM_COMP","TM_EST","TM_AFD","TM_BLOCK","TM_EMPLOYEE","TM_LOGIN","TM_USER_AUTH",""
        			+ "TM_PARAMETER","TM_CONTENT_INSPECTION","T_RANGE","TM_KUALITAS_PANEN","TR_HS_REGION","TR_HS_COMP","TR_HS_EST",""
        					+ "TR_HS_AFD","TR_HS_BLOCK","TM_HS_ATTRIBUTE","TM_POI","TR_HS_LAND_USE_DETAIL","TR_HS_LAND_USE",""
        							+ "TR_HS_SUB_BLOCK","TR_HS_UNPLANTED","TR_PALM","TM_CONTENT_LABEL","TM_SERVER"}; 
        	
        	// Now lets begin with the server part
        	DefaultHttpClient httpclient = new DefaultHttpClient();
        	
        	/**
        	 *  Loop connection for each table
        	 */
        	for (int i = 0; i < array.length; i++) {
        		if(isNetworkAvailable() == true)
                {
        			if(this.isCancelled()) break;
            		/**
            		 * Variable flag if there is next data from same table
            		 */
            		String nextDataSameTable = "NO";
            		
            		/**
            		 * Variable counter of the loop connection from the same table
            		 */
            		int numberLoop = 0;
            		
    				try {
    	        		
    	        		/**
    	        		 * Loop connection if nextDataSameTable == "YES"
    	        		 */
    					jsonobj = new JSONObject();
    					do {
    						numberLoop++;
    						HttpPost httppostreq = new HttpPost(wurl);
    						
    						ObjectTransfer ot = (ObjectTransfer) getApplication();
    			            jsonobj.put("DEVICE_ID", ot.getDeviceID());
    			            jsonobj.put("DATA", "YES");
    						jsonobj.put("TABLE", array[i]);
    						jsonobj.put("LOOP", numberLoop);
    						StringEntity se = new StringEntity(jsonobj.toString());
    						System.out.println(jsonobj.toString());
    						se.setContentType("application/json;charset=UTF-8");
    						se.setContentEncoding(new BasicHeader(
    								HTTP.CONTENT_TYPE,
    								"application/json;charset=UTF-8"));
    						httppostreq.setEntity(se);
    						HttpResponse httpresponse = httpclient
    								.execute(httppostreq);
    						HttpEntity resultentity = httpresponse.getEntity();
    						if (resultentity != null) {
    							InputStream inputstream = resultentity.getContent();
    							Header contentencoding = httpresponse
    									.getFirstHeader("Content-Encoding");
    							if (contentencoding != null
    									&& contentencoding.getValue()
    											.equalsIgnoreCase("gzip")) {
    								inputstream = new GZIPInputStream(inputstream);
    							}

    							BufferedReader reader = new BufferedReader(
    									new InputStreamReader(inputstream,
    											"iso-8859-1"), 8);
    							StringBuilder sb = new StringBuilder();
    							String line = null;
    							while ((line = reader.readLine()) != null) {
    								sb.append(line + " \n");
    							}
    							String resultstring = sb.toString();

    							inputstream.close();
    							resultstring = "{"
    									+ resultstring.substring(1,
    											resultstring.length() - 1) + "}";
    							System.out.println(resultstring);

    							/**
    							 * Write string from server to log.txt
    							 * log.txt path refer to ObjectTransfer.getDirectory()
    							 */
    							FileWriter fw = new FileWriter(new File(ot
    									.getDirectory().getAbsolutePath(),
    									"log.txt"));
    							fw.append(resultstring);
    							fw.flush();
    							fw.close();

    							jObj = new JSONObject(resultstring);
    							if (jObj != null) {
    								
    								/**
    								 * Parse JSON Object to each TABLE JSON Object
    								 */
    								if (i == 0) {
    									aljObjTM_REGION = new ArrayList<JSONObject>();
    									aljObjTM_REGION.add(jObj);
    									
    									try {
    										ArrayList<TM_REGION> alTM_REGION = new ArrayList<TM_REGION>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_REGION.size(); i2++) {
    												JSONArray jaTM_REGION = aljObjTM_REGION.get(i2)
    														.getJSONArray("TM_REGION");
    												for (int i1 = 0; i1 < jaTM_REGION.length(); i1++) {
    													JSONObject joTM_REGION = jaTM_REGION
    															.getJSONObject(i1);
    													alTM_REGION.add(new TM_REGION(joTM_REGION
    															.getString("NATIONAL"), joTM_REGION
    															.getString("REGION_CODE"), joTM_REGION
    															.getString("REGION_NAME"), joTM_REGION
    															.getString("INSERT_TIME_DW"), joTM_REGION
    															.getString("UPDATE_TIME_DW")));
    												}
    											}
    											TM_REGION[] TM_REGION = new TM_REGION[alTM_REGION.size()];
    											for(int i3=0; i3< alTM_REGION.size(); i3++)
    							        		{
    							        			TM_REGION[i3] = alTM_REGION.get(i3);
    							        		}
    											ot.setTm_region(TM_REGION);
    											jsonobj.put("TM_REGION", Database.sharedObject.insertTM_REGION_JSON(TM_REGION));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_REGION";
    									}
    									
    								}
    								else if (i == 1) {
    									aljObjTM_COMP = new ArrayList<JSONObject>();
    									aljObjTM_COMP.add(jObj);
    									
    									try {
    										ArrayList<TM_COMP> alTM_COMP = new ArrayList<TM_COMP>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_COMP.size(); i2++) {
    												JSONArray jaTM_COMP = aljObjTM_COMP.get(i2)
    														.getJSONArray("TM_COMP");
    												for (int i1 = 0; i1 < jaTM_COMP.length(); i1++) {
    													JSONObject joTM_COMP = jaTM_COMP.getJSONObject(i1);
    													alTM_COMP.add(new TM_COMP(joTM_COMP
    															.getString("NATIONAL"), joTM_COMP
    															.getString("REGION_CODE"), joTM_COMP
    															.getString("COMP_CODE"), joTM_COMP
    															.getString("COMP_NAME"), joTM_COMP
    															.getString("ADDRESS"), joTM_COMP
    															.getString("INSERT_TIME_DW"), joTM_COMP
    															.getString("UPDATE_TIME_DW")));
    												}
    											}
    											TM_COMP[] TM_COMP = new TM_COMP[alTM_COMP.size()];
    							        		for(int i3=0; i3< alTM_COMP.size(); i3++)
    							        		{
    							        			TM_COMP[i3] = alTM_COMP.get(i3);
    							        		}
    							        		ot.setTm_comp(TM_COMP);
    											jsonobj.put("TM_COMP", Database.sharedObject.insertTM_COMP_JSON(TM_COMP));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_COMP";
    									}
    								}
    								else if (i == 2) {
    									aljObjTM_EST = new ArrayList<JSONObject>();
    									aljObjTM_EST.add(jObj);
    									
    									try {
    										ArrayList<TM_EST> alTM_EST = new ArrayList<TM_EST>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_EST.size(); i2++) {
    												JSONArray jaTM_EST = aljObjTM_EST.get(i2).getJSONArray("TM_EST");
    												for (int i1 = 0; i1 < jaTM_EST.length(); i1++) {
    													JSONObject joTM_EST = jaTM_EST.getJSONObject(i1);
    													alTM_EST.add(new TM_EST(joTM_EST
    															.getString("NATIONAL"), joTM_EST
    															.getString("REGION_CODE"), joTM_EST
    															.getString("COMP_CODE"), joTM_EST
    															.getString("EST_CODE"), joTM_EST
    															.getString("WERKS"), joTM_EST
    															.getString("EST_NAME"), joTM_EST
    															.getString("START_VALID"), joTM_EST
    															.getString("END_VALID"), joTM_EST
    															.getString("INSERT_TIME_DW"), joTM_EST
    															.getString("UPDATE_TIME_DW")));
    												}
    											}
    											
    											TM_EST[] TM_EST = new TM_EST[alTM_EST.size()];
    							        		for(int i3=0; i3< alTM_EST.size(); i3++)
    							        		{
    							        			TM_EST[i3] = alTM_EST.get(i3);
    							        		}
    							                ot.setTm_werks(TM_EST);
    											jsonobj.put("TM_EST", Database.sharedObject.insertTM_EST_JSON(TM_EST));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_EST";
    									}
    								}
    								else if (i == 3) {
    									aljObjTM_AFD = new ArrayList<JSONObject>();
    									aljObjTM_AFD.add(jObj);
    									
    									try {
    										ArrayList<TM_AFD> alTM_AFD = new ArrayList<TM_AFD>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_AFD.size(); i2++) {
    												JSONArray jaTM_AFD = aljObjTM_AFD.get(i2).getJSONArray("TM_AFD");
    												for (int i1 = 0; i1 < jaTM_AFD.length(); i1++) {
    													JSONObject joTM_AFD = jaTM_AFD.getJSONObject(i1);
    													alTM_AFD.add(new TM_AFD(joTM_AFD
    															.getString("NATIONAL"), joTM_AFD
    															.getString("REGION_CODE"), joTM_AFD
    															.getString("COMP_CODE"), joTM_AFD
    															.getString("EST_CODE"), joTM_AFD
    															.getString("WERKS"), joTM_AFD
    															.getString("SUB_BA_CODE"), joTM_AFD
    															.getString("KEBUN_CODE"), joTM_AFD
    															.getString("AFD_CODE"), joTM_AFD
    															.getString("AFD_NAME"), joTM_AFD
    															.getString("AFD_CODE_GIS"), joTM_AFD
    															.getString("START_VALID"), joTM_AFD
    															.getString("END_VALID"), joTM_AFD
    															.getString("INSERT_TIME_DW"), joTM_AFD
    															.getString("UPDATE_TIME_DW")));
    												}
    											}

    											TM_AFD[] TM_AFD = new TM_AFD[alTM_AFD.size()];
    							        		for(int i3=0; i3< alTM_AFD.size(); i3++)
    							        		{
    							        			TM_AFD[i3] = alTM_AFD.get(i3);
    							        		}
    							        		ot.setTm_afd(TM_AFD);
    											jsonobj.put("TM_AFD", Database.sharedObject.insertTM_AFD_JSON(TM_AFD));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_AFD";
    									}
    								}
    								else if (i == 4) {
    									aljObjTM_BLOCK = new ArrayList<JSONObject>();
    									aljObjTM_BLOCK.add(jObj);
    									
    									try {
    										ArrayList<TM_BLOCK> alTM_BLOCK = new ArrayList<TM_BLOCK>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_BLOCK.size(); i2++) {
    												JSONArray jaTM_BLOCK = aljObjTM_BLOCK.get(i2)
    														.getJSONArray("TM_BLOCK");
    												for (int i1 = 0; i1 < jaTM_BLOCK.length(); i1++) {
    													JSONObject joTM_BLOCK = jaTM_BLOCK.getJSONObject(i1);
    													alTM_BLOCK.add(new TM_BLOCK(joTM_BLOCK
    															.getString("NATIONAL"), joTM_BLOCK
    															.getString("REGION_CODE"), joTM_BLOCK
    															.getString("COMP_CODE"), joTM_BLOCK
    															.getString("EST_CODE"), joTM_BLOCK
    															.getString("WERKS"), joTM_BLOCK
    															.getString("SUB_BA_CODE"), joTM_BLOCK
    															.getString("KEBUN_CODE"), joTM_BLOCK
    															.getString("AFD_CODE"), joTM_BLOCK
    															.getString("BLOCK_CODE"), joTM_BLOCK
    															.getString("BLOCK_NAME"), joTM_BLOCK
    															.getString("BLOCK_CODE_GIS"), joTM_BLOCK
    															.getString("START_VALID"), joTM_BLOCK
    															.getString("END_VALID"), joTM_BLOCK
    															.getString("INSERT_TIME_DW"), joTM_BLOCK
    															.getString("UPDATE_TIME_DW")));
    												}
    											}
    											TM_BLOCK[] TM_BLOCK = new TM_BLOCK[alTM_BLOCK.size()];
    							        		for(int i3=0; i3< alTM_BLOCK.size(); i3++)
    							        		{
    							        			TM_BLOCK[i3] = alTM_BLOCK.get(i3);
    							        		}
    							        		ot.setTm_block(TM_BLOCK);
    											jsonobj.put("TM_BLOCK", Database.sharedObject.insertTM_BLOCK_JSON(TM_BLOCK));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_BLOCK";
    									}
    								}
    								else if (i == 5) {
    									aljObjTM_EMPLOYEE = new ArrayList<JSONObject>();
    									aljObjTM_EMPLOYEE.add(jObj);
    									
    									try {
    										ArrayList<TM_EMPLOYEE> alTM_EMPLOYEE = new ArrayList<TM_EMPLOYEE>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_EMPLOYEE.size(); i2++) {
    												JSONArray jaTM_EMPLOYEE = aljObjTM_EMPLOYEE.get(i2)
    														.getJSONArray("TM_EMPLOYEE");
    												for (int i1 = 0; i1 < jaTM_EMPLOYEE.length(); i1++) {
    													JSONObject joTM_EMPLOYEE = jaTM_EMPLOYEE
    															.getJSONObject(i1);
    													alTM_EMPLOYEE
    															.add(new TM_EMPLOYEE(
    																	joTM_EMPLOYEE
    																			.getString("EMPLOYEE_NIK"),
    																	joTM_EMPLOYEE
    																			.getString("EMPLOYEE_FULLNAME"),
    																	joTM_EMPLOYEE
    																			.getString("EMPLOYEE_POSITIONCODE"),
    																	joTM_EMPLOYEE
    																			.getString("EMPLOYEE_POSITION"),
    																	joTM_EMPLOYEE
    																			.getString("EMPLOYEE_RESIGNDATE"),
    																	joTM_EMPLOYEE
    																			.getString("INSERT_TIME_DW"),
    																	joTM_EMPLOYEE
    																			.getString("UPDATE_TIME_DW")));
    												}
    											}
    											
    											TM_EMPLOYEE[] TM_EMPLOYEE = new TM_EMPLOYEE[alTM_EMPLOYEE.size()];
    							        		for(int i3=0; i3< alTM_EMPLOYEE.size(); i3++)
    							        		{
    							        			TM_EMPLOYEE[i3] = alTM_EMPLOYEE.get(i3);
    							        		}
    							                ot.setTm_employee(TM_EMPLOYEE);
    											jsonobj.put("TM_EMPLOYEE", Database.sharedObject.insertTM_EMPLOYEE_JSON(TM_EMPLOYEE));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_EMPLOYEE";
    									}
    								}
    								else if (i == 6) {
    									aljObjTM_LOGIN = new ArrayList<JSONObject>();
    									aljObjTM_LOGIN.add(jObj);
    									
    									try {
    										ArrayList<TM_LOGIN> alTM_LOGIN = new ArrayList<TM_LOGIN>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_LOGIN.size(); i2++) {
    												JSONArray jaTM_LOGIN = aljObjTM_LOGIN.get(i2)
    														.getJSONArray("TM_LOGIN");
    												for (int i1 = 0; i1 < jaTM_LOGIN.length(); i1++) {
    													JSONObject joTM_LOGIN = jaTM_LOGIN.getJSONObject(i1);
    													alTM_LOGIN.add(new TM_LOGIN(joTM_LOGIN
    															.getString("EMPLOYEE_NIK"), joTM_LOGIN
    															.getString("PASSWORD"), joTM_LOGIN
    															.getString("DEFAULT_PASSWORD"), joTM_LOGIN
    															.getInt("LOG_LOGIN"), "NO",
    															joTM_LOGIN.getString("INSERT_TIME"),
    															joTM_LOGIN.getString("UPDATE_TIME")));
    												}
    											}
    											TM_LOGIN[] TM_LOGIN = new TM_LOGIN[alTM_LOGIN.size()];
    							        		for(int i3=0; i3< alTM_LOGIN.size(); i3++)
    							        		{
    							        			TM_LOGIN[i3] = alTM_LOGIN.get(i3);
    							        		}
    							        		ot.setTm_login(TM_LOGIN);
    											jsonobj.put("TM_LOGIN", Database.sharedObject.insertTM_LOGIN_JSON(TM_LOGIN));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_LOGIN";
    									}
    								}
    								else if (i == 7) {
    									aljObjTM_USER_AUTH = new ArrayList<JSONObject>();
    									aljObjTM_USER_AUTH.add(jObj);
    									
    									try {
    										ArrayList<TM_USER_AUTH> alTM_USER_AUTH = new ArrayList<TM_USER_AUTH>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_USER_AUTH.size(); i2++) {
    												JSONArray jaTM_USER_AUTH = aljObjTM_USER_AUTH.get(i2)
    														.getJSONArray("TM_USER_AUTH");
    												for (int i1 = 0; i1 < jaTM_USER_AUTH.length(); i1++) {
    													JSONObject joTM_USER_AUTH = jaTM_USER_AUTH
    															.getJSONObject(i1);
    													alTM_USER_AUTH.add(new TM_USER_AUTH(joTM_USER_AUTH
    																	.getString("EMPLOYEE_NIK"), joTM_USER_AUTH
    																	.getString("USER_ROLE"), joTM_USER_AUTH
    																	.getString("REFERENCE_ROLE"), joTM_USER_AUTH
    																	.getString("LOCATION_CODE"), joTM_USER_AUTH
    																	.getString("INSERT_USER"), joTM_USER_AUTH
    																	.getString("INSERT_TIME"), joTM_USER_AUTH
    																	.getString("UPDATE_USER"), joTM_USER_AUTH
    																	.getString("UPDATE_TIME"), joTM_USER_AUTH
    																	.getString("DELETE_USER"), joTM_USER_AUTH
    																	.getString("DELETE_TIME")));
    												}
    											}
    											TM_USER_AUTH[] TM_USER_AUTH = new TM_USER_AUTH[alTM_USER_AUTH.size()];
    							        		for(int i3=0; i3< alTM_USER_AUTH.size(); i3++)
    							        		{
    							        			TM_USER_AUTH[i3] = alTM_USER_AUTH.get(i3);
    							        		}
    							        		ot.setTm_user_auth(TM_USER_AUTH);
    											jsonobj.put("TM_USER_AUTH", Database.sharedObject.insertTM_USER_AUTH_JSON(TM_USER_AUTH));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_USER_AUTH";
    									}
    								}
    								
    								else if (i == 8) {
    									aljObjTM_PARAMETER = new ArrayList<JSONObject>();
    									aljObjTM_PARAMETER.add(jObj);
    									
    									try {
    										ArrayList<TM_PARAMETER> alTM_PARAMETER = new ArrayList<TM_PARAMETER>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_PARAMETER.size(); i2++) {
    												JSONArray jaTM_PARAMETER = aljObjTM_PARAMETER.get(i2)
    														.getJSONArray("TM_PARAMETER");
    												for (int i1 = 0; i1 < jaTM_PARAMETER.length(); i1++) {
    													JSONObject joTM_PARAMETER = jaTM_PARAMETER
    															.getJSONObject(i1);
    													alTM_PARAMETER.add(new TM_PARAMETER(joTM_PARAMETER
    															.getString("GROUP_CODE"), joTM_PARAMETER
    															.getString("PARAM_CODE"), joTM_PARAMETER
    															.getString("DESCRIPTION"), joTM_PARAMETER
    															.getString("DATE_TIME"), joTM_PARAMETER
    															.getString("INSERT_USER"), joTM_PARAMETER
    															.getString("INSERT_TIME"), joTM_PARAMETER
    															.getString("UPDATE_USER"), joTM_PARAMETER
    															.getString("UPDATE_TIME")));
    												}
    											}
    											TM_PARAMETER[] TM_PARAMETER = new TM_PARAMETER[alTM_PARAMETER.size()];
    							        		for(int i3=0; i3< alTM_PARAMETER.size(); i3++)
    							        		{
    							        			TM_PARAMETER[i3] = alTM_PARAMETER.get(i3);
    							        		}
    							        		ot.setTm_parameter(TM_PARAMETER);
    											jsonobj.put("TM_PARAMETER", Database.sharedObject.insertTM_PARAMETER_JSON(TM_PARAMETER));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_PARAMETER";
    									}
    								}
    								else if (i == 9) {
    									aljObjTM_CONTENT_INSPECTION = new ArrayList<JSONObject>();
    									aljObjTM_CONTENT_INSPECTION.add(jObj);
    									
    									try {
    										ArrayList<TM_CONTENT_INSPECTION> alTM_CONTENT_INSPECTION = new ArrayList<TM_CONTENT_INSPECTION>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_CONTENT_INSPECTION.size(); i2++) {
    												JSONArray jaTM_CONTENT_INSPECTION = aljObjTM_CONTENT_INSPECTION.get(i2)
    														.getJSONArray("TM_CONTENT_INSPECTION");
    												for (int i1 = 0; i1 < jaTM_CONTENT_INSPECTION.length(); i1++) {
    													JSONObject joTM_CONTENT_INSPECTION = jaTM_CONTENT_INSPECTION
    															.getJSONObject(i1);
    													alTM_CONTENT_INSPECTION
    															.add(new TM_CONTENT_INSPECTION(
    																	joTM_CONTENT_INSPECTION
    																			.getString("CONTENT_INSPECT_CODE"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("CATEGORY"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("CONTENT_NAME"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("CONTENT_TYPE"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("UOM"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("FLAG_TYPE"),
    																	joTM_CONTENT_INSPECTION
    																			.getInt("PRIORITY"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("INSERT_USER"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("INSERT_TIME"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("UPDATE_USER"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("UPDATE_TIME"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("TBM0"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("TBM1"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("TBM2"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("TBM3"),
    																	joTM_CONTENT_INSPECTION
    																			.getString("TM")));
    												}
    											}
    											TM_CONTENT_INSPECTION[] TM_CONTENT_INSPECTION = new TM_CONTENT_INSPECTION[alTM_CONTENT_INSPECTION.size()];
    							        		for(int i3=0; i3< alTM_CONTENT_INSPECTION.size(); i3++)
    							        		{
    							        			TM_CONTENT_INSPECTION[i3] = alTM_CONTENT_INSPECTION.get(i3);
    							        		}
    							        		ot.setTm_content_inspection(TM_CONTENT_INSPECTION);
    											jsonobj.put("TM_CONTENT_INSPECTION", Database.sharedObject.insertTM_CONTENT_INSPECTION_JSON(TM_CONTENT_INSPECTION));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_CONTENT_INSPECTION";
    									}
    								}
    								else if (i == 10) {
    									aljObjT_RANGE = new ArrayList<JSONObject>();
    									aljObjT_RANGE.add(jObj);
    									
    									try {
    										ArrayList<T_RANGE> alT_RANGE = new ArrayList<T_RANGE>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjT_RANGE.size(); i2++) {
    												JSONArray jaT_RANGE = aljObjT_RANGE.get(i2)
    														.getJSONArray("T_RANGE");
    												for (int i1 = 0; i1 < jaT_RANGE.length(); i1++) {
    													JSONObject joT_RANGE = jaT_RANGE.getJSONObject(i1);
    													alT_RANGE.add(new T_RANGE(joT_RANGE
    															.getString("RANGE_CODE"), joT_RANGE
    															.getString("VALUE_1"), joT_RANGE
    															.getString("VALUE_2"), joT_RANGE
    															.getString("VALUE_3"), joT_RANGE
    															.getString("VALUE_4"), joT_RANGE
    															.getString("VALUE_5"), joT_RANGE
    															.getString("START_VALID"), joT_RANGE
    															.getString("END_VALID")));
    												}
    											}
    											T_RANGE[] T_RANGE = new T_RANGE[alT_RANGE.size()];
    							        		for(int i3=0; i3< alT_RANGE.size(); i3++)
    							        		{
    							        			T_RANGE[i3] = alT_RANGE.get(i3);
    							        		}
    							        		ot.setT_range(T_RANGE);
    											jsonobj.put("T_RANGE", Database.sharedObject.insertT_RANGE_JSON(T_RANGE));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "T_RANGE";
    									}
    								}
    								else if (i == 11) {
    									aljObjTM_KUALITAS_PANEN = new ArrayList<JSONObject>();
    									aljObjTM_KUALITAS_PANEN.add(jObj);
    									
    									try {
    										ArrayList<TM_KUALITAS_PANEN> alTM_KUALITAS_PANEN = new ArrayList<TM_KUALITAS_PANEN>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_KUALITAS_PANEN.size(); i2++) {
    												JSONArray jaTM_KUALITAS_PANEN = aljObjTM_KUALITAS_PANEN.get(i2)
    														.getJSONArray("TM_KUALITAS_PANEN");
    												for (int i1 = 0; i1 < jaTM_KUALITAS_PANEN.length(); i1++) {
    													JSONObject joTM_KUALITAS_PANEN = jaTM_KUALITAS_PANEN
    															.getJSONObject(i1);
    													alTM_KUALITAS_PANEN.add(new TM_KUALITAS_PANEN(
    															joTM_KUALITAS_PANEN
    																	.getString("ID_KUALITAS"),
    															joTM_KUALITAS_PANEN
    																	.getString("NAMA_KUALITAS"),
    															joTM_KUALITAS_PANEN.getString("UOM"),
    															joTM_KUALITAS_PANEN
    																	.getString("GROUP_KUALITAS"),
    															joTM_KUALITAS_PANEN
    																	.getString("PENALTY_STATUS"),
    															joTM_KUALITAS_PANEN.getString("DATE_TIME"),
    															joTM_KUALITAS_PANEN
    																	.getString("ACTIVE_STATUS"),
    															joTM_KUALITAS_PANEN
    																	.getString("SHORT_NAME"),
    															joTM_KUALITAS_PANEN
    																	.getString("INSERT_USER"),
    															joTM_KUALITAS_PANEN
    																	.getString("INSERT_TIME"),
    															joTM_KUALITAS_PANEN
    																	.getString("UPDATE_USER"),
    															joTM_KUALITAS_PANEN
    																	.getString("UPDATE_TIME")));
    												}
    											}
    											TM_KUALITAS_PANEN[] TM_KUALITAS_PANEN = new TM_KUALITAS_PANEN[alTM_KUALITAS_PANEN.size()];
    							        		for(int i3=0; i3< alTM_KUALITAS_PANEN.size(); i3++)
    							        		{
    							        			TM_KUALITAS_PANEN[i3] = alTM_KUALITAS_PANEN.get(i3);
    							        		}
    							        		ot.setTm_kualitas_panen(TM_KUALITAS_PANEN);
    											jsonobj.put("TM_KUALITAS_PANEN", Database.sharedObject.insertTM_KUALITAS_PANEN_JSON(TM_KUALITAS_PANEN));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_KUALITAS_PANEN";
    									}
    								}
    								else if (i == 12) {
    									aljObjTR_HS_REGION = new ArrayList<JSONObject>();
    									aljObjTR_HS_REGION.add(jObj);
    									
    									try {
    										ArrayList<TR_HS_REGION> alTR_HS_REGION = new ArrayList<TR_HS_REGION>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTR_HS_REGION.size(); i2++) {
    												JSONArray jaTR_HS_REGION = aljObjTR_HS_REGION.get(i2)
    														.getJSONArray("TR_HS_REGION");
    												for (int i1 = 0; i1 < jaTR_HS_REGION.length(); i1++) {
    													JSONObject joTR_HS_REGION = jaTR_HS_REGION
    															.getJSONObject(i1);
    													alTR_HS_REGION
    															.add(new TR_HS_REGION(
    																	joTR_HS_REGION
    																			.getString("NATIONAL"),
    																	joTR_HS_REGION
    																			.getString("REGION_CODE"),
    																	joTR_HS_REGION.getString("SPMON"),
    																	joTR_HS_REGION.getInt("HA_SAP"),
    																	joTR_HS_REGION
    																			.getInt("HA_PLANTED_SAP"),
    																	joTR_HS_REGION
    																			.getInt("HA_UNPLANTED_SAP"),
    																	joTR_HS_REGION.getInt("PALM_SAP"),
    																	joTR_HS_REGION.getInt("SPH_SAP"),
    																	joTR_HS_REGION.getInt("HA_GIS"),
    																	joTR_HS_REGION
    																			.getInt("HA_PLANTED_GIS"),
    																	joTR_HS_REGION
    																			.getInt("HA_UNPLANTED_GIS"),
    																	joTR_HS_REGION.getInt("PALM_GIS"),
    																	joTR_HS_REGION.getInt("SPH_GIS"),
    																	joTR_HS_REGION.getInt("JML_COMP"),
    																	joTR_HS_REGION.getInt("JML_EST"),
    																	joTR_HS_REGION.getInt("JML_AFD"),
    																	joTR_HS_REGION.getInt("JML_BLOCK"),
    																	joTR_HS_REGION.getString("GEOM"),
    																	joTR_HS_REGION
    																			.getString("INSERT_TIME_DW"),
    																	joTR_HS_REGION
    																			.getString("UPDATE_TIME_DW"), 
																		joTR_HS_REGION
																		.getInt("HA_LC_SAP"), joTR_HS_REGION
																		.getInt("HA_LC_GIS"), joTR_HS_REGION
																		.getInt("JML_SUB_BLOCK"), joTR_HS_REGION
																		.getInt("JML_BLOCK_LC"), joTR_HS_REGION
																		.getInt("HA_TM_SAP"), joTR_HS_REGION
																		.getInt("HA_TBM_SAP")));
    												}
    											}
    											TR_HS_REGION[] TR_HS_REGION = new TR_HS_REGION[alTR_HS_REGION.size()];
    							        		for(int i3=0; i3< alTR_HS_REGION.size(); i3++)
    							        		{
    							        			TR_HS_REGION[i3] = alTR_HS_REGION.get(i3);
    							        		}
    							        		ot.setTr_hs_region(TR_HS_REGION);
    											jsonobj.put("TR_HS_REGION", Database.sharedObject.insertTR_HS_REGION_JSON(TR_HS_REGION));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TR_HS_REGION";
    									}
    								}
    								else if (i == 13) {
    									aljObjTR_HS_COMP = new ArrayList<JSONObject>();
    									aljObjTR_HS_COMP.add(jObj);
    									
    									try {
    										ArrayList<TR_HS_COMP> alTR_HS_COMP = new ArrayList<TR_HS_COMP>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTR_HS_COMP.size(); i2++) {
    												JSONArray jaTR_HS_COMP = aljObjTR_HS_COMP.get(i2)
    														.getJSONArray("TR_HS_COMP");
    												for (int i1 = 0; i1 < jaTR_HS_COMP.length(); i1++) {
    													JSONObject joTR_HS_COMP = jaTR_HS_COMP
    															.getJSONObject(i1);
    													alTR_HS_COMP.add(new TR_HS_COMP(joTR_HS_COMP
    															.getString("NATIONAL"), joTR_HS_COMP
    															.getString("REGION_CODE"), joTR_HS_COMP
    															.getString("COMP_CODE"), joTR_HS_COMP
    															.getString("SPMON"), joTR_HS_COMP
    															.getInt("HA_SAP"), joTR_HS_COMP
    															.getInt("HA_PLANTED_SAP"), joTR_HS_COMP
    															.getInt("HA_UNPLANTED_SAP"), joTR_HS_COMP
    															.getInt("PALM_SAP"), joTR_HS_COMP
    															.getInt("SPH_SAP"), joTR_HS_COMP
    															.getInt("HA_GIS"), joTR_HS_COMP
    															.getInt("HA_PLANTED_GIS"), joTR_HS_COMP
    															.getInt("HA_UNPLANTED_GIS"), joTR_HS_COMP
    															.getInt("PALM_GIS"), joTR_HS_COMP
    															.getInt("SPH_GIS"), joTR_HS_COMP
    															.getInt("JML_AFD"), joTR_HS_COMP
    															.getInt("JML_BLOCK"), joTR_HS_COMP
    															.getString("GEOM"), joTR_HS_COMP
    															.getString("INSERT_TIME_DW"), joTR_HS_COMP
    															.getString("UPDATE_TIME_DW"), joTR_HS_COMP
    															.getInt("HA_LC_SAP"), joTR_HS_COMP
    															.getInt("HA_LC_GIS"), joTR_HS_COMP
    															.getInt("JML_SUB_BLOCK"), joTR_HS_COMP
    															.getInt("JML_BLOCK_LC"), joTR_HS_COMP
    															.getInt("HA_TM_SAP"), joTR_HS_COMP
    															.getInt("HA_TBM_SAP")));
    												}
    											}
    											TR_HS_COMP[] TR_HS_COMP = new TR_HS_COMP[alTR_HS_COMP.size()];
    							        		for(int i3=0; i3< alTR_HS_COMP.size(); i3++)
    							        		{
    							        			TR_HS_COMP[i3] = alTR_HS_COMP.get(i3);
    							        		}
    							        		ot.setTr_hs_comp(TR_HS_COMP);
    											jsonobj.put("TR_HS_COMP", Database.sharedObject.insertTR_HS_COMP_JSON(TR_HS_COMP));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TR_HS_COMP";
    									}
    								}
    								else if (i == 14) {
    									aljObjTR_HS_EST = new ArrayList<JSONObject>();
    									aljObjTR_HS_EST.add(jObj);
    									
    									try {
    										ArrayList<TR_HS_EST> alTR_HS_EST = new ArrayList<TR_HS_EST>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTR_HS_EST.size(); i2++) {
    												JSONArray jaTR_HS_EST = aljObjTR_HS_EST.get(i2)
    														.getJSONArray("TR_HS_EST");
    												for (int i1 = 0; i1 < jaTR_HS_EST.length(); i1++) {
    													JSONObject joTR_HS_EST = jaTR_HS_EST
    															.getJSONObject(i1);
    													alTR_HS_EST.add(new TR_HS_EST(joTR_HS_EST
    															.getString("NATIONAL"), joTR_HS_EST
    															.getString("REGION_CODE"), joTR_HS_EST
    															.getString("COMP_CODE"), joTR_HS_EST
    															.getString("EST_CODE"), joTR_HS_EST
    															.getString("WERKS"), joTR_HS_EST
    															.getString("SPMON"), joTR_HS_EST
    															.getInt("HA_SAP"), joTR_HS_EST
    															.getInt("HA_PLANTED_SAP"), joTR_HS_EST
    															.getInt("HA_UNPLANTED_SAP"), joTR_HS_EST
    															.getInt("PALM_SAP"), joTR_HS_EST
    															.getInt("SPH_SAP"), joTR_HS_EST
    															.getInt("HA_GIS"), joTR_HS_EST
    															.getInt("HA_PLANTED_GIS"), joTR_HS_EST
    															.getInt("HA_UNPLANTED_GIS"), joTR_HS_EST
    															.getInt("PALM_GIS"), joTR_HS_EST
    															.getInt("SPH_GIS"), joTR_HS_EST
    															.getInt("JML_AFD"), joTR_HS_EST
    															.getInt("JML_BLOCK"), joTR_HS_EST
    															.getString("GEOM"), joTR_HS_EST
    															.getString("INSERT_TIME_DW"), joTR_HS_EST
    															.getString("UPDATE_TIME_DW"), joTR_HS_EST
    															.getInt("HA_LC_SAP"), joTR_HS_EST
    															.getInt("HA_LC_GIS"), joTR_HS_EST
    															.getInt("JML_SUB_BLOCK"), joTR_HS_EST
    															.getInt("JML_BLOCK_LC"), joTR_HS_EST
    															.getInt("HA_TM_SAP"), joTR_HS_EST
    															.getInt("HA_TBM_SAP")));
    												}
    											}
    											TR_HS_EST[] TR_HS_EST = new TR_HS_EST[alTR_HS_EST.size()];
    							        		for(int i3=0; i3< alTR_HS_EST.size(); i3++)
    							        		{
    							        			TR_HS_EST[i3] = alTR_HS_EST.get(i3);
    							        		}
    							        		ot.setTr_hs_est(TR_HS_EST);
    											jsonobj.put("TR_HS_EST", Database.sharedObject.insertTR_HS_EST_JSON(TR_HS_EST));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TR_HS_EST";
    									}
    								}
    								else if (i == 15) {
    									aljObjTR_HS_AFD = new ArrayList<JSONObject>();
    									aljObjTR_HS_AFD.add(jObj);
    									
    									try {
    										ArrayList<TR_HS_AFD> alTR_HS_AFD = new ArrayList<TR_HS_AFD>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTR_HS_AFD.size(); i2++) {
    												JSONArray jaTR_HS_AFD = aljObjTR_HS_AFD.get(i2)
    														.getJSONArray("TR_HS_AFD");
    												for (int i1 = 0; i1 < jaTR_HS_AFD.length(); i1++) {
    													JSONObject joTR_HS_AFD = jaTR_HS_AFD
    															.getJSONObject(i1);
    													alTR_HS_AFD.add(new TR_HS_AFD(joTR_HS_AFD
    															.getString("NATIONAL"), joTR_HS_AFD
    															.getString("REGION_CODE"), joTR_HS_AFD
    															.getString("COMP_CODE"), joTR_HS_AFD
    															.getString("EST_CODE"), joTR_HS_AFD
    															.getString("WERKS"), joTR_HS_AFD
    															.getString("SUB_BA_CODE"), joTR_HS_AFD
    															.getString("KEBUN_CODE"), joTR_HS_AFD
    															.getString("AFD_CODE"), joTR_HS_AFD
    															.getString("AFD_NAME"), joTR_HS_AFD
    															.getString("AFD_CODE_GIS"), joTR_HS_AFD
    															.getString("SPMON"), joTR_HS_AFD
    															.getInt("HA_SAP"), joTR_HS_AFD
    															.getInt("HA_PLANTED_SAP"), joTR_HS_AFD
    															.getInt("HA_UNPLANTED_SAP"), joTR_HS_AFD
    															.getInt("PALM_SAP"), joTR_HS_AFD
    															.getInt("SPH_SAP"), joTR_HS_AFD
    															.getInt("HA_GIS"), joTR_HS_AFD
    															.getInt("HA_PLANTED_GIS"), joTR_HS_AFD
    															.getInt("HA_UNPLANTED_GIS"), joTR_HS_AFD
    															.getInt("PALM_GIS"), joTR_HS_AFD
    															.getInt("SPH_GIS"), joTR_HS_AFD
    															.getInt("JML_BLOCK"), joTR_HS_AFD
    															.getString("GEOM"), joTR_HS_AFD
    															.getString("INSERT_TIME_DW"), joTR_HS_AFD
    															.getString("UPDATE_TIME_DW"), joTR_HS_AFD
    															.getInt("HA_LC_SAP"), joTR_HS_AFD
    															.getInt("HA_LC_GIS"), joTR_HS_AFD
    															.getInt("JML_SUB_BLOCK"), joTR_HS_AFD
    															.getInt("JML_BLOCK_LC"), joTR_HS_AFD
    															.getInt("HA_TM_SAP"), joTR_HS_AFD
    															.getInt("HA_TBM_SAP")));
    												}
    											}

    											TR_HS_AFD[] TR_HS_AFD = new TR_HS_AFD[alTR_HS_AFD.size()];
    							        		for(int i3=0; i3< alTR_HS_AFD.size(); i3++)
    							        		{
    							        			TR_HS_AFD[i3] = alTR_HS_AFD.get(i3);
    							        		}
    							        		ot.setTr_hs_afd(TR_HS_AFD);
    											jsonobj.put("TR_HS_AFD", Database.sharedObject.insertTR_HS_AFD_JSON(TR_HS_AFD));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TR_HS_AFD";
    									}
    								}
    								else if (i == 16) {
    									aljObjTR_HS_BLOCK = new ArrayList<JSONObject>();
    									aljObjTR_HS_BLOCK.add(jObj);
    									
    									try {
    										ArrayList<TR_HS_BLOCK> alTR_HS_BLOCK = new ArrayList<TR_HS_BLOCK>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTR_HS_BLOCK.size(); i2++) {
    												JSONArray jaTR_HS_BLOCK = aljObjTR_HS_BLOCK.get(i2)
    														.getJSONArray("TR_HS_BLOCK");
    												for (int i1 = 0; i1 < jaTR_HS_BLOCK.length(); i1++) {
    													JSONObject joTR_HS_BLOCK = jaTR_HS_BLOCK
    															.getJSONObject(i1);
    													alTR_HS_BLOCK.add(new TR_HS_BLOCK(joTR_HS_BLOCK
    															.getString("NATIONAL"), joTR_HS_BLOCK
    															.getString("REGION_CODE"), joTR_HS_BLOCK
    															.getString("COMP_CODE"), joTR_HS_BLOCK
    															.getString("EST_CODE"), joTR_HS_BLOCK
    															.getString("WERKS"), joTR_HS_BLOCK
    															.getString("SUB_BA_CODE"), joTR_HS_BLOCK
    															.getString("KEBUN_CODE"), joTR_HS_BLOCK
    															.getString("AFD_CODE"), joTR_HS_BLOCK
    															.getString("AFD_NAME"), joTR_HS_BLOCK
    															.getString("BLOCK_CODE"), joTR_HS_BLOCK
    															.getString("BLOCK_NAME"), joTR_HS_BLOCK
    															.getString("BLOCK_CODE_GIS"), joTR_HS_BLOCK
    															.getString("SPMON"), joTR_HS_BLOCK
    															.getInt("HA_SAP"), joTR_HS_BLOCK
    															.getInt("HA_PLANTED_SAP"), joTR_HS_BLOCK
    															.getInt("HA_UNPLANTED_SAP"), joTR_HS_BLOCK
    															.getInt("PALM_SAP"), joTR_HS_BLOCK
    															.getInt("SPH_SAP"), joTR_HS_BLOCK
    															.getInt("HA_GIS"), joTR_HS_BLOCK
    															.getInt("HA_PLANTED_GIS"), joTR_HS_BLOCK
    															.getInt("HA_UNPLANTED_GIS"), joTR_HS_BLOCK
    															.getInt("PALM_GIS"), joTR_HS_BLOCK
    															.getInt("SPH_GIS"), joTR_HS_BLOCK
    															.getInt("JML_SUB_BLOCK"), joTR_HS_BLOCK
    															.getInt("JML_BLOCK_LC"), joTR_HS_BLOCK
    															.getString("GEOM"), joTR_HS_BLOCK
    															.getString("INSERT_TIME_DW"), joTR_HS_BLOCK
    															.getString("UPDATE_TIME_DW"), joTR_HS_BLOCK
    															.getInt("HA_LC_SAP"), joTR_HS_BLOCK
    															.getInt("HA_LC_GIS"), joTR_HS_BLOCK
    															.getInt("HA_TM_SAP"), joTR_HS_BLOCK
    															.getInt("HA_TBM_SAP")));
    												}
    											}
    											TR_HS_BLOCK[] TR_HS_BLOCK = new TR_HS_BLOCK[alTR_HS_BLOCK.size()];
    							        		for(int i3=0; i3< alTR_HS_BLOCK.size(); i3++)
    							        		{
    							        			TR_HS_BLOCK[i3] = alTR_HS_BLOCK.get(i3);
    							        		}
    							        		ot.setTr_hs_block(TR_HS_BLOCK);
    											jsonobj.put("TR_HS_BLOCK", Database.sharedObject.insertTR_HS_BLOCK_JSON(TR_HS_BLOCK));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TR_HS_BLOCK";
    									}
    								}
    								else if (i == 17) {
    									aljObjTM_HS_ATTRIBUTE = new ArrayList<JSONObject>();
    									aljObjTM_HS_ATTRIBUTE.add(jObj);
    									
    									try {
    										ArrayList<TM_HS_ATTRIBUTE> alTM_HS_ATTRIBUTE = new ArrayList<TM_HS_ATTRIBUTE>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_HS_ATTRIBUTE.size(); i2++) {
    												JSONArray jaTM_HS_ATTRIBUTE = aljObjTM_HS_ATTRIBUTE.get(i2)
    														.getJSONArray("TM_HS_ATTRIBUTE");
    												for (int i1 = 0; i1 < jaTM_HS_ATTRIBUTE.length(); i1++) {
    													JSONObject joTM_HS_ATTRIBUTE = jaTM_HS_ATTRIBUTE
    															.getJSONObject(i1);
    													alTM_HS_ATTRIBUTE
    															.add(new TM_HS_ATTRIBUTE(
    																	joTM_HS_ATTRIBUTE
    																			.getString("NATIONAL"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("REGION_CODE"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("COMP_CODE"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("EST_CODE"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("WERKS"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("SUB_BA_CODE"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("KEBUN_CODE"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("AFD_CODE"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("BLOCK_CODE"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("START_VALID"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("END_VALID"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("BLOCK_NAME"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("LAND_TYPE"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("REPLANT"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("BLOCK_TYPE"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("TOPOGRAPHY"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("PROGENY"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("LAND_SUIT"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("YEAR_PLAN"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("BY_PLAN"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("INIT_PERIOD"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("MAINT_PERIOD"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("SH_PERIOD"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("HARV_PERIOD"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("INSERT_TIME_DW"),
    																	joTM_HS_ATTRIBUTE
    																			.getString("UPDATE_TIME_DW")));
    												}
    											}
    											TM_HS_ATTRIBUTE[] TM_HS_ATTRIBUTE = new TM_HS_ATTRIBUTE[alTM_HS_ATTRIBUTE.size()];
    							        		for(int i3=0; i3< alTM_HS_ATTRIBUTE.size(); i3++)
    							        		{
    							        			TM_HS_ATTRIBUTE[i3] = alTM_HS_ATTRIBUTE.get(i3);
    							        		}
    							        		ot.setTm_hs_attribute(TM_HS_ATTRIBUTE);
    											jsonobj.put("TM_HS_ATTRIBUTE", Database.sharedObject.insertTM_HS_ATTRIBUTE_JSON(TM_HS_ATTRIBUTE));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_HS_ATTRIBUTE";
    									}
    								}
    								else if (i == 18) {
    									aljObjTM_POI = new ArrayList<JSONObject>();
    									aljObjTM_POI.add(jObj);
    									
    									try {
    										ArrayList<TM_POI> alTM_POI = new ArrayList<TM_POI>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_POI.size(); i2++) {
    												JSONArray jaTM_POI = aljObjTM_POI.get(i2).getJSONArray("TM_POI");
    												for (int i1 = 0; i1 < jaTM_POI.length(); i1++) {
    													JSONObject joTM_POI = jaTM_POI.getJSONObject(i1);
    													alTM_POI.add(new TM_POI(joTM_POI
    															.getString("NATIONAL"), joTM_POI
    															.getString("REGION_CODE"), joTM_POI
    															.getString("COMP_CODE"), joTM_POI
    															.getString("EST_CODE"), joTM_POI
    															.getString("WERKS"), joTM_POI
    															.getString("SUB_BA_CODE"), joTM_POI
    															.getString("KEBUN_CODE"), joTM_POI
    															.getString("AFD_CODE"), joTM_POI
    															.getString("AFD_NAME"), joTM_POI
    															.getString("BLOCK_CODE"), joTM_POI
    															.getString("BLOCK_NAME"), joTM_POI
    															.getString("BLOCK_CODE_GIS"), joTM_POI
    															.getString("POI_CODE"), joTM_POI
    															.getString("POI_TYPE"), joTM_POI
    															.getString("POI_NAME"), joTM_POI
    															.getString("POI_ATTR"), joTM_POI
    															.getString("GEOM"), joTM_POI
    															.getString("INSERT_TIME_DW"), joTM_POI
    															.getString("UPDATE_TIME_DW"), joTM_POI
    															.getString("POI_CATEGORY")));
    												}
    											}
    											TM_POI[] TM_POI = new TM_POI[alTM_POI.size()];
    							        		for(int i3=0; i3< alTM_POI.size(); i3++)
    							        		{
    							        			TM_POI[i3] = alTM_POI.get(i3);
    							        		}
    							        		ot.setTm_poi(TM_POI);
    											jsonobj.put("TM_POI", Database.sharedObject.insertTM_POI_JSON(TM_POI));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_POI";
    									}
    								}
    								else if (i == 19) {
    									aljObjTR_HS_LAND_USE_DETAIL = new ArrayList<JSONObject>();
    									aljObjTR_HS_LAND_USE_DETAIL.add(jObj);
    									
    									try {
    										ArrayList<TR_HS_LAND_USE_DETAIL> alTR_HS_LAND_USE_DETAIL = new ArrayList<TR_HS_LAND_USE_DETAIL>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTR_HS_LAND_USE_DETAIL.size(); i2++) {
    												JSONArray jaTR_HS_LAND_USE_DETAIL = aljObjTR_HS_LAND_USE_DETAIL.get(i2)
    														.getJSONArray("TR_HS_LAND_USE_DETAIL");
    												for (int i1 = 0; i1 < jaTR_HS_LAND_USE_DETAIL.length(); i1++) {
    													JSONObject joTR_HS_LAND_USE_DETAIL = jaTR_HS_LAND_USE_DETAIL
    															.getJSONObject(i1);
    													alTR_HS_LAND_USE_DETAIL
    															.add(new TR_HS_LAND_USE_DETAIL(
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("NATIONAL"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("REGION_CODE"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("COMP_CODE"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("EST_CODE"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("WERKS"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("SUB_BA_CODE"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("KEBUN_CODE"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("AFD_CODE"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("AFD_NAME"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("BLOCK_CODE"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("BLOCK_NAME"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("BLOCK_CODE_GIS"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("LAND_USE_CODE"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("LAND_USE_NAME"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("LAND_USE_CODE_GIS"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("SPMON"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("LAND_CAT"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("LAND_CAT_L1_CODE"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("LAND_CAT_L1"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("LAND_CAT_L2_CODE"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("LAND_CAT_L2"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("MATURITY_STATUS"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("SCOUT_STATUS"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getInt("AGES"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getInt("HA_SAP"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getInt("PALM_SAP"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getInt("SPH_GIS"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getInt("PANJANG"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("GEOM"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("INSERT_TIME_DW"),
    																	joTR_HS_LAND_USE_DETAIL
    																			.getString("UPDATE_TIME_DW"),
																		joTR_HS_LAND_USE_DETAIL.getInt("HA_GIS"),
																		joTR_HS_LAND_USE_DETAIL.getInt("PALM_GIS"),
																		joTR_HS_LAND_USE_DETAIL.getInt("SPH_SAP")));
    												}
    											}
    											TR_HS_LAND_USE_DETAIL[] TR_HS_LAND_USE_DETAIL = new TR_HS_LAND_USE_DETAIL[alTR_HS_LAND_USE_DETAIL.size()];
    							        		for(int i3=0; i3< alTR_HS_LAND_USE_DETAIL.size(); i3++)
    							        		{
    							        			TR_HS_LAND_USE_DETAIL[i3] = alTR_HS_LAND_USE_DETAIL.get(i3);
    							        		}
    							        		ot.setTr_hs_land_use_detail(TR_HS_LAND_USE_DETAIL);
    											jsonobj.put("TR_HS_LAND_USE_DETAIL", Database.sharedObject.insertTR_HS_LAND_USE_DETAIL_JSON(TR_HS_LAND_USE_DETAIL));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TR_HS_LAND_USE_DETAIL";
    									}
    								}
    								else if (i == 20) {
    									aljObjTR_HS_LAND_USE = new ArrayList<JSONObject>();
    									aljObjTR_HS_LAND_USE.add(jObj);
    									
    									try {
    										ArrayList<TR_HS_LAND_USE> alTR_HS_LAND_USE = new ArrayList<TR_HS_LAND_USE>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTR_HS_LAND_USE.size(); i2++) {
    												JSONArray jaTR_HS_LAND_USE = aljObjTR_HS_LAND_USE.get(i2)
    														.getJSONArray("TR_HS_LAND_USE");
    												for (int i1 = 0; i1 < jaTR_HS_LAND_USE.length(); i1++) {
    													JSONObject joTR_HS_LAND_USE = jaTR_HS_LAND_USE
    															.getJSONObject(i1);
    													alTR_HS_LAND_USE
    															.add(new TR_HS_LAND_USE(
    																	joTR_HS_LAND_USE
    																			.getString("NATIONAL"),
    																	joTR_HS_LAND_USE
    																			.getString("REGION_CODE"),
    																	joTR_HS_LAND_USE
    																			.getString("COMP_CODE"),
    																	joTR_HS_LAND_USE
    																			.getString("EST_CODE"),
    																	joTR_HS_LAND_USE.getString("WERKS"),
    																	joTR_HS_LAND_USE
    																			.getString("SUB_BA_CODE"),
    																	joTR_HS_LAND_USE
    																			.getString("KEBUN_CODE"),
    																	joTR_HS_LAND_USE
    																			.getString("AFD_CODE"),
    																	joTR_HS_LAND_USE
    																			.getString("AFD_NAME"),
    																	joTR_HS_LAND_USE
    																			.getString("BLOCK_CODE"),
    																	joTR_HS_LAND_USE
    																			.getString("BLOCK_NAME"),
    																	joTR_HS_LAND_USE
    																			.getString("BLOCK_CODE_GIS"),
    																	joTR_HS_LAND_USE
    																			.getString("LAND_USE_CODE"),
    																	joTR_HS_LAND_USE
    																			.getString("LAND_USE_NAME"),
    																	joTR_HS_LAND_USE
    																			.getString("LAND_USE_CODE_GIS"),
    																	joTR_HS_LAND_USE.getString("SPMON"),
    																	joTR_HS_LAND_USE
    																			.getString("LAND_CAT"),
    																	joTR_HS_LAND_USE
    																			.getString("LAND_CAT_L1_CODE"),
    																	joTR_HS_LAND_USE
    																			.getString("LAND_CAT_L1"),
    																	joTR_HS_LAND_USE
    																			.getString("LAND_CAT_L2_CODE"),
    																	joTR_HS_LAND_USE
    																			.getString("LAND_CAT_L2"),
    																	joTR_HS_LAND_USE
    																			.getString("MATURITY_STATUS"),
    																	joTR_HS_LAND_USE
    																			.getString("SCOUT_STATUS"),
    																	joTR_HS_LAND_USE.getInt("AGES"),
    																	joTR_HS_LAND_USE.getInt("HA_SAP"),
    																	joTR_HS_LAND_USE.getInt("PALM_SAP"),
    																	joTR_HS_LAND_USE.getInt("SPH_GIS"),
    																	joTR_HS_LAND_USE.getString("GEOM"),
    																	joTR_HS_LAND_USE
    																			.getString("INSERT_TIME_DW"),
    																	joTR_HS_LAND_USE
    																			.getString("UPDATE_TIME_DW"),
    																	joTR_HS_LAND_USE.getInt("HA_GIS"),
    																	joTR_HS_LAND_USE.getInt("PALM_GIS"),
    																	joTR_HS_LAND_USE.getInt("SPH_SAP")));
    												}
    											}
    											TR_HS_LAND_USE[] TR_HS_LAND_USE = new TR_HS_LAND_USE[alTR_HS_LAND_USE.size()];
    							        		for(int i3=0; i3< alTR_HS_LAND_USE.size(); i3++)
    							        		{
    							        			TR_HS_LAND_USE[i3] = alTR_HS_LAND_USE.get(i3);
    							        		}
    							        		ot.setTr_hs_land_use(TR_HS_LAND_USE);
    											jsonobj.put("TR_HS_LAND_USE", Database.sharedObject.insertTR_HS_LAND_USE_JSON(TR_HS_LAND_USE));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TR_HS_LAND_USE";
    									}
    								}
    								else if (i == 21) {
    									aljObjTR_HS_SUB_BLOCK = new ArrayList<JSONObject>();
    									aljObjTR_HS_SUB_BLOCK.add(jObj);
    									
    									try {
    										ArrayList<TR_HS_SUB_BLOCK> alTR_HS_SUB_BLOCK = new ArrayList<TR_HS_SUB_BLOCK>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTR_HS_SUB_BLOCK.size(); i2++) {
    												JSONArray jaTR_HS_SUB_BLOCK = aljObjTR_HS_SUB_BLOCK.get(i2)
    														.getJSONArray("TR_HS_SUB_BLOCK");
    												for (int i1 = 0; i1 < jaTR_HS_SUB_BLOCK.length(); i1++) {
    													JSONObject joTR_HS_SUB_BLOCK = jaTR_HS_SUB_BLOCK
    															.getJSONObject(i1);

    													alTR_HS_SUB_BLOCK
    															.add(new TR_HS_SUB_BLOCK(
    																	joTR_HS_SUB_BLOCK
    																			.getString("NATIONAL"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("REGION_CODE"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("COMP_CODE"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("EST_CODE"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("WERKS"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("SUB_BA_CODE"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("KEBUN_CODE"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("AFD_CODE"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("AFD_NAME"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("BLOCK_CODE"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("BLOCK_NAME"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("BLOCK_CODE_GIS"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("SUB_BLOCK_CODE"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("SUB_BLOCK_NAME"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("LAND_USE_CODE_GIS"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("SPMON"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("LAND_CAT"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("LAND_CAT_L1_CODE"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("LAND_CAT_L1"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("LAND_CAT_L2_CODE"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("LAND_CAT_L2"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("MATURITY_STATUS"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("SCOUT_STATUS"),
    																	joTR_HS_SUB_BLOCK.getInt("AGES"),
    																	joTR_HS_SUB_BLOCK.getInt("HA_SAP"),
    																	joTR_HS_SUB_BLOCK
    																			.getInt("PALM_SAP"),
    																	joTR_HS_SUB_BLOCK.getInt("SPH_SAP"),
    																	joTR_HS_SUB_BLOCK.getInt("HA_GIS"),
    																	joTR_HS_SUB_BLOCK
    																			.getInt("PALM_GIS"),
    																	joTR_HS_SUB_BLOCK.getInt("SPH_GIS"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("INSERT_TIME_DW"),
    																	joTR_HS_SUB_BLOCK
    																			.getString("UPDATE_TIME_DW")));
    												}
    											}
    											TR_HS_SUB_BLOCK[] TR_HS_SUB_BLOCK = new TR_HS_SUB_BLOCK[alTR_HS_SUB_BLOCK.size()];
    							        		for(int i3=0; i3< alTR_HS_SUB_BLOCK.size(); i3++)
    							        		{
    							        			TR_HS_SUB_BLOCK[i3] = alTR_HS_SUB_BLOCK.get(i3);
    							        		}
    							        		ot.setTr_hs_sub_block(TR_HS_SUB_BLOCK);
    											jsonobj.put("TR_HS_SUB_BLOCK", Database.sharedObject.insertTR_HS_SUB_BLOCK_JSON(TR_HS_SUB_BLOCK));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TR_HS_SUB_BLOCK";
    									}
    								}
    								else if (i == 22) {
    									aljObjTR_HS_UNPLANTED = new ArrayList<JSONObject>();
    									aljObjTR_HS_UNPLANTED.add(jObj);
    									
    									try {
    										ArrayList<TR_HS_UNPLANTED> alTR_HS_UNPLANTED = new ArrayList<TR_HS_UNPLANTED>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTR_HS_UNPLANTED.size(); i2++) {
    												JSONArray jaTR_HS_UNPLANTED = aljObjTR_HS_UNPLANTED.get(i2)
    														.getJSONArray("TR_HS_UNPLANTED");
    												for (int i1 = 0; i1 < jaTR_HS_UNPLANTED.length(); i1++) {
    													JSONObject joTR_HS_UNPLANTED = jaTR_HS_UNPLANTED
    															.getJSONObject(i1);
    													alTR_HS_UNPLANTED
    															.add(new TR_HS_UNPLANTED(
    																	joTR_HS_UNPLANTED
    																			.getString("NATIONAL"),
    																	joTR_HS_UNPLANTED
    																			.getString("REGION_CODE"),
    																	joTR_HS_UNPLANTED
    																			.getString("COMP_CODE"),
    																	joTR_HS_UNPLANTED
    																			.getString("EST_CODE"),
    																	joTR_HS_UNPLANTED
    																			.getString("WERKS"),
    																	joTR_HS_UNPLANTED
    																			.getString("SUB_BA_CODE"),
    																	joTR_HS_UNPLANTED
    																			.getString("KEBUN_CODE"),
    																	joTR_HS_UNPLANTED
    																			.getString("AFD_CODE"),
    																	joTR_HS_UNPLANTED
    																			.getString("AFD_NAME"),
    																	joTR_HS_UNPLANTED
    																			.getString("AFD_CODE_GIS"),
    																	joTR_HS_UNPLANTED
    																			.getString("SPMON"),
    																	joTR_HS_UNPLANTED
    																			.getString("LAND_CAT"),
    																	joTR_HS_UNPLANTED
    																			.getString("LAND_CAT_L1_CODE"),
    																	joTR_HS_UNPLANTED
    																			.getString("LAND_CAT_L1"),
    																	joTR_HS_UNPLANTED
    																			.getString("LAND_CAT_L2_CODE"),
    																	joTR_HS_UNPLANTED
    																			.getString("LAND_CAT_L2"),
    																	joTR_HS_UNPLANTED.getInt("HA_SAP"),
    																	joTR_HS_UNPLANTED.getInt("HA_GIS"),
    																	joTR_HS_UNPLANTED
    																			.getString("INSERT_TIME_DW"),
    																	joTR_HS_UNPLANTED
    																			.getString("UPDATE_TIME_DW")));
    												}
    											}
    											TR_HS_UNPLANTED[] TR_HS_UNPLANTED = new TR_HS_UNPLANTED[alTR_HS_UNPLANTED.size()];
    							        		for(int i3=0; i3< alTR_HS_UNPLANTED.size(); i3++)
    							        		{
    							        			TR_HS_UNPLANTED[i3] = alTR_HS_UNPLANTED.get(i3);
    							        		}
    							        		ot.setTr_hs_unplanted(TR_HS_UNPLANTED);
    											jsonobj.put("TR_HS_UNPLANTED", Database.sharedObject.insertTR_HS_UNPLANTED_JSON(TR_HS_UNPLANTED));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TR_HS_UNPLANTED";
    									}
    								}
    								else if (i == 23) {
    									aljObjTR_PALM = new ArrayList<JSONObject>();
    									aljObjTR_PALM.add(jObj);
    									
    									try {
    										ArrayList<TR_PALM> alTR_PALM = new ArrayList<TR_PALM>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTR_PALM.size(); i2++) {
    												JSONArray jaTR_PALM = aljObjTR_PALM.get(i2)
    														.getJSONArray("TR_PALM");
    												for (int i1 = 0; i1 < jaTR_PALM.length(); i1++) {
    													JSONObject joTR_PALM = jaTR_PALM.getJSONObject(i1);

    													alTR_PALM.add(new TR_PALM(joTR_PALM
    															.getString("NATIONAL"), joTR_PALM
    															.getString("REGION_CODE"), joTR_PALM
    															.getString("COMP_CODE"), joTR_PALM
    															.getString("EST_CODE"), joTR_PALM
    															.getString("WERKS"), joTR_PALM
    															.getString("SUB_BA_CODE"), joTR_PALM
    															.getString("KEBUN_CODE"), joTR_PALM
    															.getString("AFD_CODE"), joTR_PALM
    															.getString("AFD_NAME"), joTR_PALM
    															.getString("BLOCK_CODE"), joTR_PALM
    															.getString("BLOCK_NAME"), joTR_PALM
    															.getString("BLOCK_CODE_GIS"), joTR_PALM
    															.getString("LAND_USE_CODE_GIS"), joTR_PALM
    															.getString("SPMON"), joTR_PALM
    															.getInt("PALM"), joTR_PALM
    															.getString("GEOM"), joTR_PALM
    															.getString("INSERT_TIME_DW"), joTR_PALM
    															.getString("UPDATE_TIME_DW")));
    												}
    											}
    											TR_PALM[] TR_PALM = new TR_PALM[alTR_PALM.size()];
    							        		for(int i3=0; i3< alTR_PALM.size(); i3++)
    							        		{
    							        			TR_PALM[i3] = alTR_PALM.get(i3);
    							        		}
    							        		ot.setTr_palm(TR_PALM);
    											jsonobj.put("TR_PALM", Database.sharedObject.insertTR_PALM_JSON(TR_PALM));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    											
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TR_PALM";
    									}
    								}
    								else if (i == 24) {
    									aljObjTM_CONTENT_LABEL = new ArrayList<JSONObject>();
    									aljObjTM_CONTENT_LABEL.add(jObj);
    									
    									try {
    										ArrayList<TM_CONTENT_LABEL> alTM_CONTENT_LABEL = new ArrayList<TM_CONTENT_LABEL>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_CONTENT_LABEL.size(); i2++) {
    												JSONArray jaTM_CONTENT_LABEL = aljObjTM_CONTENT_LABEL.get(i2)
    														.getJSONArray("TM_CONTENT_LABEL");
    												for (int i1 = 0; i1 < jaTM_CONTENT_LABEL.length(); i1++) {
    													JSONObject joTM_CONTENT_LABEL = jaTM_CONTENT_LABEL.getJSONObject(i1);

    													alTM_CONTENT_LABEL.add(new TM_CONTENT_LABEL(joTM_CONTENT_LABEL
    															.getString("CONTENT_INSPECT_CODE"), joTM_CONTENT_LABEL
    															.getString("LABEL_CODE"), joTM_CONTENT_LABEL
    															.getString("LABEL_NAME"), joTM_CONTENT_LABEL
    															.getString("INSERT_USER"), joTM_CONTENT_LABEL
    															.getString("INSERT_TIME"), joTM_CONTENT_LABEL
    															.getString("UPDATE_USER"), joTM_CONTENT_LABEL
    															.getString("UPDATE_TIME")));
    												}
    											}
    											TM_CONTENT_LABEL[] TM_CONTENT_LABEL = new TM_CONTENT_LABEL[alTM_CONTENT_LABEL.size()];
    							        		for(int i3=0; i3< alTM_CONTENT_LABEL.size(); i3++)
    							        		{
    							        			TM_CONTENT_LABEL[i3] = alTM_CONTENT_LABEL.get(i3);
    							        		}
    							        		ot.setTm_content_label(TM_CONTENT_LABEL);
    											jsonobj.put("TM_CONTENT_LABEL", Database.sharedObject.insertTM_CONTENT_LABEL_JSON(TM_CONTENT_LABEL));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    											
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_CONTENT_LABEL";
    									}
    								}
    								else if (i == 25) {
    									aljObjTM_SERVER = new ArrayList<JSONObject>();
    									aljObjTM_SERVER.add(jObj);
    									
    									try {
    										ArrayList<TM_SERVER> alTM_SERVER = new ArrayList<TM_SERVER>();
    										nextDataSameTable = jObj.getString("NEXT");
    										if(nextDataSameTable.equals("YES"))
    										{
    											for (int i2 = 0; i2 < aljObjTM_SERVER.size(); i2++) {
    												JSONArray jaTM_SERVER = aljObjTM_SERVER.get(i2)
    														.getJSONArray("TM_SERVER");
    												for (int i1 = 0; i1 < jaTM_SERVER.length(); i1++) {
    													JSONObject joTM_SERVER = jaTM_SERVER.getJSONObject(i1);

    													alTM_SERVER.add(new TM_SERVER(joTM_SERVER
    															.getString("COMP_CODE"), joTM_SERVER
    															.getString("TYPE"), "http://"+joTM_SERVER
    															.getString("SERVER_PATH")+"//", joTM_SERVER
    															.getString("START_VALID"), joTM_SERVER
    															.getString("END_VALID"), joTM_SERVER
    															.getString("INSERT_USER"), joTM_SERVER
    															.getString("INSERT_TIME"), joTM_SERVER
    															.getString("UPDATE_USER"), joTM_SERVER
    															.getString("UPDATE_TIME")));
    												}
    											}
    											TM_SERVER[] TM_SERVER = new TM_SERVER[alTM_SERVER.size()];
    							        		for(int i3=0; i3< alTM_SERVER.size(); i3++)
    							        		{
    							        			TM_SERVER[i3] = alTM_SERVER.get(i3);
    							        		}
    							        		ot.setTm_server(TM_SERVER);
    											jsonobj.put("TM_SERVER", Database.sharedObject.insertTM_SERVER_JSON(TM_SERVER));
    											
    											if(ot.isStopAsyncTask()==true)
    											{
    												this.cancel(true);
    			                                    if(this.isCancelled()) break;
    											}
    											
    										}
    										
    									} catch (Exception e) {
    										e.printStackTrace();
    										sNoData = sNoData +" | "+ "TM_SERVER";
    									}
    								}

    								sStatus = jObj.getString("SUCCESS");
    								sMessage = jObj.getString("MESSAGE");
    								
    								// Get next paramater from server
    								nextDataSameTable = jObj.getString("NEXT");
    								
    								if(nextDataSameTable.equals("NO"))
    								{
    									jsonobj = new JSONObject();
    								}
    							}
    						}
    					} while (nextDataSameTable.equals("YES"));
    				} catch (UnsupportedEncodingException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} catch (ClientProtocolException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} catch (IllegalStateException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} catch (JSONException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    					sNoData = sNoData +" | "+ "Jaringan Putus!";
    				}
    				
    				int prog = (i+1) * 4;
    				if (prog > 100) {
    					prog = 100;
    				}
    				publishProgress(prog);
        		}
        		else
        		{
        			sStatus = "NO";
					sMessage = "SYNC FAILED | Jaringan Putus!";
					i = array.length;
        		}
        		
			}
        }
        /*
        private String convertStreamToString(InputStream is) {
            String line = "";
            StringBuilder total = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Stream Exception", Toast.LENGTH_SHORT).show();
            }
            return total.toString();
        }*/
    }

    public final static String getElementValue(Node elem) {
        Node kid;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
                    if (kid.getNodeType() == Node.TEXT_NODE) {
                        return kid.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public static String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return Connection.getElementValue(n.item(0));
    }
    
    public boolean isNetworkAvailable() {
    	boolean rslt = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null, otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            try 
            {
            	ObjectTransfer ot = (ObjectTransfer) getApplication();
        		
            	sUrl = ot.getLinkServerData();
                url = new URL(sUrl);
                urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "Android Application:");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(100 * 100); // mTimeout is in seconds
                System.out.println("sUrl: "+sUrl);
                try {
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                    	rslt = true;
                    }
                } catch (SocketTimeoutException ste) {
                    ste.printStackTrace();
                } catch (UnknownHostException uhe) {
                    uhe.printStackTrace();
                }
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
            	return rslt;
            }
        }
        return false;
    }
}
