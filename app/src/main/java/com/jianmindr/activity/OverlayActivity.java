package com.jianmindr.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.service.LocationService;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.jianmindr.EDaijiaApp;
import com.jianmindr.R;
import com.jianmindr.util.AppUtils;
import com.jianmindr.view.CarInfoDialog;

import java.util.ArrayList;


/**
 * 演示覆盖物的用法
 */
public class OverlayActivity extends MapMainActivity implements View.OnClickListener, OnGetGeoCoderResultListener {

    MapView mMapView;
    BaiduMap mBaiduMap;

    GeoCoder mSearch = null;
    GeoCoder mSearchCar = null;
    TextView text_address, text_address_car;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);

        View img_head = findViewById(R.id.img_head);
        img_head.setOnClickListener(this);
        View img_gift = findViewById(R.id.img_gift);
        img_gift.setOnClickListener(this);
        View button_request = findViewById(R.id.button_request);
        button_request.setOnClickListener(this);
        View button_call = findViewById(R.id.button_call);
        button_call.setOnClickListener(this);
        View button_0 = findViewById(R.id.button_0);
        button_0.setOnClickListener(this);
        View button_1 = findViewById(R.id.button_1);
        button_1.setOnClickListener(this);
        View button_2 = findViewById(R.id.button_2);
        button_2.setOnClickListener(this);
        View button_3 = findViewById(R.id.button_3);
        button_3.setOnClickListener(this);
        View button_4 = findViewById(R.id.button_4);
        button_4.setOnClickListener(this);
        View button_5 = findViewById(R.id.button_5);
        button_5.setOnClickListener(this);

        text_address = (TextView) findViewById(R.id.text_address);
        text_address_car = (TextView) findViewById(R.id.text_address_car);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mMapView.showZoomControls(false);
        mBaiduMap.setMapStatus(msu);

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        mSearchCar = GeoCoder.newInstance();
        mSearchCar.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                isInit = false;
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    text_address_car.setText("定位失败");
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR)
                {
                    text_address_car.setText(result.getAddress());
                }
            }
        });

        View img_cur_pos = findViewById(R.id.img_cur_pos);
        img_cur_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reFindMapLocation(m_curPos);
            }
        });

        m_curPos = new LatLng(AppUtils.getCurLat(this), AppUtils.getCurLon(this));

        // for TEST
        //refreshCurPos();

        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                int position = markerList.indexOf(marker);
                if (position >= 0) {
                    clickItem(position);
                }
                return true;
            }
        });

        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (mInfoWindow != null) {
                    mBaiduMap.hideInfoWindow();
                    mInfoWindow = null;
                }
            }
        });

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
                if (isManual)
                    isManual = false;
                else
                    refreshSearch();
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    boolean isManual = false;
    boolean isInit = true;
    void refreshSearch()
    {
        LatLng ptCenter = mBaiduMap.getMapStatus().target; // 获取地图中心点坐标
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(ptCenter));
    }

    //
    BitmapDescriptor bdCurrent = BitmapDescriptorFactory.fromResource(R.drawable.map_pos_cur);
    Marker mCurMarker = null;

    final static int POINT_CNT = 20;
    final static int MARK_CNT = 4;

    LatLng[] offsetLatLngs = new LatLng[]{
            new LatLng(34.201665, 108.901367),
            new LatLng(34.221055, 108.947049),
            new LatLng(34.246659, 108.952196),
            new LatLng(34.262825, 108.9620178),
            new LatLng(34.284595, 108.921932),
            new LatLng(34.300921, 108.988748),
            new LatLng(34.347746, 108.90295),
            new LatLng(34.380902, 108.944850),
            new LatLng(34.369773, 108.939139),
            new LatLng(34.856280, 108.930793),
            new LatLng(34.396035, 108.938384),
            new LatLng(34.393359, 108.956318),
            new LatLng(34.380225, 108.962057),
            new LatLng(34.370609, 108.961040),
            new LatLng(34.427746, 108.988295),
            new LatLng(34.442825, 108.900178),
            new LatLng(34.464595, 108.957932),
            new LatLng(34.480921, 108.972748),
            new LatLng(34.507746, 108.949295),
            new LatLng(34.406077, 108.915156)};

    String[] details = new String[]{
            "潘佳怡是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "郑豪是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "徐国杰是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "村安静是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "老革命是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "平安福是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "女服装是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "哈哈哈是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "您好我是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "我爱你是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "拜拜拜是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "你你你是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "甄汉书是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "金光影是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "票老师是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "楚老师是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "帮师傅是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "康师傅是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "李连杰是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",
            "王丽萍是好师傅发生发生发顺丰啊发发发生哀伤的发生法哀伤的发生法史蒂夫",};

    String[] manNames = new String[]{
            "潘医生",
            "郑医生",
            "徐医生",
            "村医生",
            "老医生",
            "平医生",
            "女医生",
            "哈医生",
            "您医生",
            "我医生",
            "拜医生",
            "你医生",
            "甄医生",
            "金医生",
            "票医生",
            "楚医生",
            "帮医生",
            "康医生",
            "李医生",
            "王医生",};

    int[] imgIds = new int[]{R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3, R.drawable.avatar_4};

    String[] carNums = new String[]{"内科","外科","儿科","妇科"};
    float[] rates = new float[]{5, 4.5f, 3, 3.5f};

    LatLng m_curPos = new LatLng(34.381511, 108.945149);
    LatLng m_centerPos = new LatLng(39.941272, 116.400819);

    ArrayList<Marker> markerList = new ArrayList<>();
    ArrayList<BitmapDescriptor> descriptors = new ArrayList<>();

    InfoWindow mInfoWindow;
    InfoWindow.OnInfoWindowClickListener listener = null;

    void clickItem(int position) {
        mSearchCar.reverseGeoCode(new ReverseGeoCodeOption()
                .location(offsetLatLngs[position]));

        if (listener == null) {
            listener = new InfoWindow.OnInfoWindowClickListener() {
                public void onInfoWindowClick() {
                    mBaiduMap.hideInfoWindow();
                }
            };
        }

        View headerView = LayoutInflater.from(this).inflate(R.layout.info_overlay, null);

        ((TextView) headerView.findViewById(R.id.item_overlay_name)).setText(manNames[position]);
        ((TextView) headerView.findViewById(R.id.item_overlay_no)).setText(carNums[position%MARK_CNT]);
        ((ImageView) headerView.findViewById(R.id.item_overlay_img)).setImageResource(imgIds[position%MARK_CNT]);
        ((RatingBar) headerView.findViewById(R.id.rate_driver)).setRating(rates[position%MARK_CNT]);
        ((TextView) headerView.findViewById(R.id.text_detail)).setText(details[position]);

        LatLng ll = markerList.get(position).getPosition();
        mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(headerView), ll, -80, listener);
        mBaiduMap.showInfoWindow(mInfoWindow);
        /*
        dispText(position);
        */
    }

    CarInfoDialog mDialog;
    void dispText(int position) {

        String strMsg = "昵称：" + manNames[position] + "\n";
        strMsg += "车号：" + carNums[position%MARK_CNT] + "\n";
        strMsg += "详细：" + details[position];

        mDialog = new CarInfoDialog(this, strMsg, imgIds[position%MARK_CNT], rates[position%MARK_CNT]);
        mDialog.show();
        mDialog.positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    public void initOverlay() {
        // center
        MarkerOptions ooB = new MarkerOptions().position(m_curPos).icon(bdCurrent).zIndex(7);
        mCurMarker = (Marker) (mBaiduMap.addOverlay(ooB));

        for (int i = 0; i < POINT_CNT; i++) {
            LatLng latLng = new LatLng(offsetLatLngs[i].latitude, offsetLatLngs[i].longitude);

            // load marker view
            View headerView = LayoutInflater.from(this).inflate(R.layout.item_overlay, null);
            ((TextView) headerView.findViewById(R.id.item_overlay_name)).setText(manNames[i]);
            ((TextView) headerView.findViewById(R.id.item_overlay_no)).setText(carNums[i%MARK_CNT]);
            ((ImageView) headerView.findViewById(R.id.item_overlay_img)).setImageResource(imgIds[i%MARK_CNT]);
            ((RatingBar) headerView.findViewById(R.id.rate_driver)).setRating(rates[i%MARK_CNT]);

            // add marker
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromView(headerView);
            descriptors.add(descriptor);

            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                    .icon(descriptor).zIndex(9).draggable(true);
            markerOptions.animateType(MarkerAnimateType.grow);
            markerList.add((Marker)mBaiduMap.addOverlay(markerOptions));
        }
    }

    /**
     * 清除所有Overlay
     *
     * @param view
     */
    public void backClicked(View view) {
        finish();
    }

    /**
     * 清除所有Overlay
     *
     * @param view
     */
    public void clearOverlay(View view) {
        mBaiduMap.clear();
        for (Marker marker : markerList) {
            marker.remove();
        }

        markerList.clear();
        for (BitmapDescriptor descriptor : descriptors) {
            descriptor.recycle();
        }
        descriptors.clear();
        mCurMarker = null;
    }

    /**
     * 重新添加Overlay
     *
     * @param view
     */
    public void resetOverlay(View view) {
        clearOverlay(null);
        initOverlay();
    }

    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.e("kcm", "onDestroy");
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        locationService.stop();
        mMapView.onDestroy();
        super.onDestroy();

        mCurMarker = null;
        // 回收 bitmap 资源
        bdCurrent.recycle();
        for (BitmapDescriptor descriptor : descriptors) {
            descriptor.recycle();
        }
        descriptors.clear();
    }

    Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                gotoCurPos();
            } else if (msg.what == 2) {
                refreshCurPos();
            } else if (msg.what == 3) {
                text_address.setText((String)msg.obj);
            }
        }
    };

    void refreshAllMarket() {
        initOverlay();
    }

    // 更新当前位置
    // 初次时 移动到当前位置
    void refreshCurPos() {
        try {
            if (mCurMarker != null) {
                mCurMarker.setPosition(m_curPos);

                if (isInit) {
                    reFindMapLocation(m_curPos);
                }

            } else {
                reFindMapLocation(m_curPos);
                refreshAllMarket();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /*
        m_handler.removeMessages(1);
        m_handler.sendEmptyMessageDelayed(1, 600);
        */
    }

    void gotoCurPos()
    {
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);// 初始化的时候 设置显示地图的标尺
        //resetOverlay(null);
    }

    // 重新定向到 最开始的位置
    private void reFindMapLocation(LatLng curPos) {
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(curPos);// 描述地图状态将要发生的变化
        // 设置地图新中心点
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        if (msu != null)
            mBaiduMap.setMapStatus(msu);// 初始化的时候 设置显示地图的标尺

        mBaiduMap.animateMapStatus(u);// 以动画方式更新地图状态，动画耗时 300 ms
    }


    ////////////////////////////////////////////////////////
    // 定位相关
    ////////////////////////////////////////////////////////

    LocationService locationService;
    MyLocationListenner myListener = new MyLocationListenner();

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // -----------location config ------------
        locationService = ((EDaijiaApp) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(myListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }

        locationService.start();
        Log.e("ttt", "started");
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.stop();
        locationService.unregisterListener(myListener);
        Log.e("kcm", "onStop");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_head:
                dispMsg("点击了头像");
                break;
            case R.id.img_gift:
                dispMsg("点击了奖励");
                break;
            case R.id.button_0:
                dispMsg("点击了其他");
                break;
            case R.id.button_1:
                dispMsg("点击了家庭医生");
                break;
            case R.id.button_2:
                dispMsg("点击了私人医生");
                break;
            case R.id.button_3:
                dispMsg("点击了病历");
                break;
            case R.id.button_4:
                dispMsg("点击了影像");
                break;
            case R.id.button_5:
                dispMsg("点击了检验");
                break;
            case R.id.button_request:
                dispMsg("点击了预约");
                break;
            case R.id.button_call:
                dispMsg("点击了签约");
                break;
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        isInit = false;
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            text_address.setText("定位失败");
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR)
        {
            isInit = false;
            m_centerPos = result.getLocation();
            text_address.setText(result.getAddress());
            //text_address.setText(m_centerPos.latitude + " " + m_centerPos.longitude);
        }
    }

    public class MyLocationListenner implements BDLocationListener {

        // 回到函数
        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuffer sb = new StringBuffer();
            try{
                if (location != null) {
                    sb.append("百度地图经度为：");
                    sb.append(location.getLongitude());
                    sb.append(",百度地图纬度为：");
                    sb.append(location.getLatitude());
                    sb.append("。Country：");
                    sb.append(location.getCountry());
                    sb.append("，getCountryCode");
                    sb.append(location.getCountryCode());
                    sb.append("，Address:");
                    sb.append(location.getAddress());

                    if (location.getAddress() != null) {
                        sb.append(location.getAddress().address);
                    }

                    Log.e("kcm", sb.toString());

                    /*
                    String address = location.getAddrStr();

                    if (!TextUtils.isEmpty(location.getLocationDescribe()))
                    {
                        if (!TextUtils.isEmpty(address))
                            address = address + " " + location.getLocationDescribe();
                        else
                            address = location.getLocationDescribe();
                    }
                    */

                    if (isInit) {
                        if (!TextUtils.isEmpty(location.getAddrStr())) {
                            Message msg = new Message();
                            msg.what = 3;
                            msg.obj = location.getAddrStr();
                            m_handler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = 3;
                            msg.obj = location.getLocTypeDescription();
                            m_handler.sendMessage(msg);
                        }
                    }

                    if (location.getLatitude() > 10 && location.getLongitude() > 10) {
                        m_curPos = new LatLng(location.getLatitude(), location.getLongitude());

                        AppUtils.setCurLon(OverlayActivity.this, (float)m_curPos.longitude);
                        AppUtils.setCurAddr(OverlayActivity.this, location.getAddrStr());
                        /* 获取当前详细地址
                        if (!TextUtils.isEmpty(location.getProvince()))
                            gData.currProvince = location.getProvince().replace("省", ""); //辽宁
                        if (!TextUtils.isEmpty(location.getCity()))
                            gData.currCity = location.getCity().replace("市", ""); // 沈阳
                        if (!TextUtils.isEmpty(location.getDistrict()))
                            gData.currDistrict = location.getDistrict().replace("区", ""); // 东陵
                        if (!TextUtils.isEmpty(location.getStreet()))
                            gData.currStreet = location.getStreet(); // 长白南路
                        if (!TextUtils.isEmpty(location.getAddrStr()))
                            gData.currFullAddr = location.getAddrStr(); //中国辽宁省沈阳市东陵区长白南路

                        if (!TextUtils.isEmpty(gData.currCity) && TextUtils.isEmpty(AppUtils.getCurrCity(D40_LocationActivity.this))) {
                            AppUtils.setCurrCity(D40_LocationActivity.this, gData.currCity);
                        }
                        */
                        //mLocClient.stop();

                        m_handler.removeMessages(2);
                        m_handler.sendEmptyMessage(2);
                    }

                } else {
                    sb.append("百度地图经纬度为null");
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
