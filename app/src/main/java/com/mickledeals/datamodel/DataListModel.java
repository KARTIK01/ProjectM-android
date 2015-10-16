package com.mickledeals.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nicky on 12/7/2014.
 */
public class DataListModel {

    private static DataListModel sInstance;

    private Map<Integer, CouponInfo> mCouponMap = new HashMap<Integer, CouponInfo>();
    private List<Integer> mFeatureSliderCouponList = new ArrayList<Integer>();
    private List<Integer> mNewAddedCouponList = new ArrayList<Integer>();
    private List<Integer> mPopularList = new ArrayList<Integer>();
    private List<Integer> mBestCouponList = new ArrayList<Integer>();


    private List<Integer> mNearbyList = new ArrayList<Integer>();
    private List<Integer> mSavedList = new ArrayList<Integer>();
    private List<Integer> mBoughtList = new ArrayList<Integer>();

    private List<Integer> mMoreCouponsList = new ArrayList<Integer>();
    private List<Integer> mSearchResultList = new ArrayList<Integer>();

    public static DataListModel getInstance() {
        if (sInstance == null) {
            sInstance = new DataListModel();
        }
        return sInstance;
    }

    private DataListModel() {
        init();
    }

    private void init() {
//        initTestData();
//        initFeatureSliderCoupon();
//        initNewAddedCoupon();
//        initPopularCoupon();
//        initBestCoupon();
    }

    public Map<Integer, CouponInfo> getCouponMap() {
        return mCouponMap;
    }

    public List<Integer> getFeatureSliderCouponList() {
        return mFeatureSliderCouponList;
    }

    public List<Integer> getNewAddedCouponList() {
        return mNewAddedCouponList;
    }

    public List<Integer> getPopularCouponList() {
        return mPopularList;
    }

    public List<Integer> getBestCouponList() {
        return mBestCouponList;
    }

    public List<Integer> getNearbyList() {
        return mNearbyList;
    }

    public List<Integer> getSavedList() {
        return mSavedList;
    }

    public List<Integer> getBoughtList() {
        return mBoughtList;
    }

    public List<Integer> getMoreCouponsList() {
        return mMoreCouponsList;
    }

    public List<Integer> getSearchResultList() {
        return mSearchResultList;
    }

    public CouponInfo getCouponInfoFromList(List<Integer> list, int position) {
        int couponId = list.get(position);
        return DataListModel.getInstance().getCouponMap().get(couponId);
    }

//    private void initFeatureSliderCoupon() {
//        mFeatureSliderCouponList.add(mCouponMap.get(2));
//        mFeatureSliderCouponList.add(mCouponMap.get(16));
//        mFeatureSliderCouponList.add(mCouponMap.get(1));
//        mFeatureSliderCouponList.add(mCouponMap.get(7));
//        mFeatureSliderCouponList.add(mCouponMap.get(6));
//        mFeatureSliderCouponList.add(mCouponMap.get(17));
//    }
//
//    private void initNewAddedCoupon() {
//        mNewAddedCouponList.add(mCouponMap.get(9));
//        mNewAddedCouponList.add(mCouponMap.get(11));
//        mNewAddedCouponList.add(mCouponMap.get(20));
//        mNewAddedCouponList.add(mCouponMap.get(10));
//        mNewAddedCouponList.add(mCouponMap.get(21));
//    }
//
//    private void initPopularCoupon() {
//        mPopularList.add(mCouponMap.get(13));
//        mPopularList.add(mCouponMap.get(5));
//        mPopularList.add(mCouponMap.get(8));
//        mPopularList.add(mCouponMap.get(10));
//        mPopularList.add(mCouponMap.get(4));
//    }
//
//    private void initBestCoupon() {
//        mBestCouponList.add(mCouponMap.get(12));
//        mBestCouponList.add(mCouponMap.get(14));
//        mBestCouponList.add(mCouponMap.get(15));
//        mBestCouponList.add(mCouponMap.get(18));
//        mBestCouponList.add(mCouponMap.get(9));
//        mBestCouponList.add(mCouponMap.get(11));
//        mBestCouponList.add(mCouponMap.get(10));
//        mBestCouponList.add(mCouponMap.get(8));
//        mBestCouponList.add(mCouponMap.get(5));
//        mBestCouponList.add(mCouponMap.get(13));
//    }


//    private void initTestData() {
//        CouponInfo holder = new CouponInfo();
//        holder.mId = 1;
//        holder.mImageResId = R.drawable.pic_1;
//        holder.mSmallImageResId = R.drawable.pic_1_s;
//        holder.mShortDescription = "1 Free Pudding";
//        holder.mDescription = "Free Strawberry Pudding when spent more than $30";
//        holder.mStoreName = "BlueSky Dessert";
//        holder.mAddress = "2522 Lawton St, San Francisco";
//        holder.mAddressShort = "Sunset • San Francisco";
//        holder.mLatLng = "37.757448, -122.490140";
//        holder.mPrice = 0.5f;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "免費布丁1份";
//        holder.mDescriptionCh = "消費滿$30即獲免費草莓布丁1分";
//        holder.mStoreNameCh ="藍天甜品屋";
//
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 2;
//        holder.mImageResId = R.drawable.pic_8;
//        holder.mSmallImageResId = R.drawable.pic_8_s;
//        holder.mShortDescription = "25% Off";
//        holder.mDescription = "25% Off Discount";
//        holder.mStoreName = "Savory Steak House";
//        holder.mAddress = "383 Woodrew St, Daly City";
//        holder.mAddressShort = "Daly City";
//        holder.mLatLng = "37.697941, -122.466850";
//        holder.mPrice = 1;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "9折優惠";
//        holder.mDescriptionCh = "結帳時享有9折優惠";
//        //this steakhouse should not have a chinese name, no need mStoreNameCh
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 3;
//        holder.mImageResId = R.drawable.pic_21;
//        holder.mSmallImageResId = R.drawable.pic_21_s;
//        holder.mShortDescription = "1 Free Potato Soup";
//        holder.mDescription = "Free Potato Soup with order of Filet Mignon";
//        holder.mStoreName = "Savory Steak House";
//        holder.mAddress = "383 Woodrew St, Daly City";
//        holder.mAddressShort = "Daly City";
//        holder.mLatLng = "37.697941, -122.466850";
//        holder.mPrice = 0;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "免費馬鈴薯湯1客";
//        holder.mDescriptionCh = "點菲力牛扒1份即送馬鈴薯湯1客";
//        mCouponMap.put(holder.mId, holder);
//
//
//
//        holder = new CouponInfo();
//        holder.mId = 8;
//        holder.mImageResId = R.drawable.pic_2;
//        holder.mSmallImageResId = R.drawable.pic_2_s;
//        holder.mShortDescription = "20% Off";
//        holder.mDescription = "20% Off Discount";
//        holder.mStoreName = "Cyberlight Karaoke";
//        holder.mAddress = "957 WildWood Ave, Daly City";
//        holder.mAddressShort = "Daly City";
//        holder.mLatLng = "37.695954, -122.489582";
//        holder.mPrice = 2;
//        holder.mCategoryId = 2;
//        holder.mShortDescriptionCh = "9折優惠";
//        holder.mDescriptionCh = "結帳時享有9折優惠";
//        holder.mStoreNameCh ="網天卡拉OK";
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 4;
//        holder.mImageResId = R.drawable.pic_3;
//        holder.mSmallImageResId = R.drawable.pic_3_s;
//        holder.mShortDescription = "1 Free Game";
//        holder.mDescription = "One free game with purchase of 3 games or more";
//        holder.mStoreName = "Imperial Bowl";
//        holder.mAddress = "1400 16th, San Francisco";
//        holder.mAddressShort = "Soma • San Francisco";
//        holder.mLatLng = "37.766347, -122.401168";
//        holder.mPrice = 1;
//        holder.mCategoryId = 2;
//        holder.mShortDescriptionCh = "免費1局保齡球遊戲";
//        holder.mDescriptionCh = "買3局遊戲或以上即送1局";
//        holder.mStatus = Constants.COUPON_STATUS_BOUGHT;
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 21;
//        holder.mImageResId = R.drawable.pic_4;
//        holder.mSmallImageResId = R.drawable.pic_4_s;
//        holder.mShortDescription = "1 Free MilkTea";
//        holder.mDescription = "One free Milk Tea with purchase of $10 or more";
//        holder.mStoreName = "Teapot Heaven";
//        holder.mAddress = "401 4th lane, South San Francisco";
//        holder.mAddressShort = "South San Francisco";
//        holder.mLatLng = "37.656826, -122.415446";
//        holder.mPrice = 1;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "免費奶茶1杯";
//        holder.mDescriptionCh = "購物滿$10或以上即獲免費奶茶1杯";
//        holder.mStoreNameCh ="茶壺天堂";
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 5;
//        holder.mImageResId = R.drawable.pic_5;
//        holder.mSmallImageResId = R.drawable.pic_5_s;
//        holder.mShortDescription = "20% Off";
//        holder.mDescription = "20% Off Discount";
//        holder.mStoreName = "Wonton city";
//        holder.mAddress = "800 3rd Ave, San Mateo";
//        holder.mAddressShort = "San Mateo";
//        holder.mLatLng = "37.568029, -122.318458";
//        holder.mPrice = 0.5f;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "9折優惠";
//        holder.mDescriptionCh = "結帳時享有9折優惠";
//        holder.mStoreNameCh ="餛飩之城";
//        holder.mStatus = Constants.COUPON_STATUS_BOUGHT;
//        mCouponMap.put(holder.mId, holder);
//
//        holder.mPrice = 1;
//        holder = new CouponInfo();
//        holder.mId = 6;
//        holder.mImageResId = R.drawable.pic_6;
//        holder.mSmallImageResId = R.drawable.pic_6_s;
//        holder.mShortDescription = "20% Off";
//        holder.mDescription = "20% Off Discount";
//        holder.mStoreName = "Mama Joane Pizza";
//        holder.mAddress = "322 El Dorado Dr, Daly City";
//        holder.mAddressShort = "Daly City";
//        holder.mLatLng = "37.680165, -122.478204";
//        holder.mPrice = 0;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "8折優惠";
//        holder.mDescriptionCh = "結帳時享有8折優惠";
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 7;
//        holder.mImageResId = R.drawable.pic_7;
//        holder.mSmallImageResId = R.drawable.pic_7_s;
//        holder.mShortDescription = "20% Off";
//        holder.mDescription = "20% Off Discount";
//        holder.mStoreName = "Green Day Spa";
//        holder.mAddress = "6600 Geary Blvd, San Francisco";
//        holder.mAddressShort = "Richmond • San Francisco";
//        holder.mLatLng = "37.779818, -122.490651";
//        holder.mPrice = 2;
//        holder.mCategoryId = 3;
//        holder.mShortDescriptionCh = "9折優惠";
//        holder.mDescriptionCh = "結帳時享有9折優惠";
//        holder.mStoreNameCh ="綠日水療";
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 9;
//        holder.mImageResId = R.drawable.pic_9;
//        holder.mSmallImageResId = R.drawable.pic_9_s;
//        holder.mShortDescription = "5% Off";
//        holder.mDescription = "5% Off Discount";
//        holder.mStoreName = "Hotpot Palace";
//        holder.mAddress = "738 Jackson St, San Francisco";
//        holder.mAddressShort = "Chinatown • San Francisco";
//        holder.mLatLng = "37.796164, -122.405760";
//        holder.mPrice = 0.5f;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "95折優惠";
//        holder.mDescriptionCh = "結帳時享有95折優惠";
//        holder.mStoreNameCh ="火鍋宮殿";
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 10;
//        holder.mImageResId = R.drawable.pic_10;
//        holder.mSmallImageResId = R.drawable.pic_10_s;
//        holder.mShortDescription = "20% Off";
//        holder.mDescription = "20% Off Discount of any Service";
//        holder.mStoreName = "Steven's Auto Service";
//        holder.mAddress = "2200 QuinTara St, San Francisco";
//        holder.mAddressShort = "Sunset • San Francisco";
//        holder.mLatLng = "37.748046, -122.489103";
//        holder.mPrice = 5;
//        holder.mCategoryId = 4;
//        holder.mShortDescriptionCh = "9折優惠";
//        holder.mDescriptionCh = "結帳時享有9折優惠";
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 11;
//        holder.mImageResId = R.drawable.pic_11;
//        holder.mSmallImageResId = R.drawable.pic_11_s;
//        holder.mShortDescription = "Free Short ribs";
//        holder.mDescription = "One free plate of premium short ribs with All you can eat";
//        holder.mStoreName = "99 Korean BBQ";
//        holder.mAddress = "200 9th Ave, San Mateo";
//        holder.mAddressShort = "San Mateo";
//        holder.mLatLng = "37.561281, -122.318920";
//        holder.mPrice = 2;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "免費牛小排1份";
//        holder.mDescriptionCh = "點任吃套餐即送牛小排一份";
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 12;
//        holder.mImageResId = R.drawable.pic_12;
//        holder.mSmallImageResId = R.drawable.pic_12_s;
//        holder.mShortDescription = "$15 Off";
//        holder.mDescription = "$15 Off when spent more than $80";
//        holder.mStoreName = "Boiling Crawfish";
//        holder.mAddress = "600 El Camino Real, Burlingame";
//        holder.mAddressShort = "Burlingame";
//        holder.mLatLng = "37.577303, -122.353424";
//        holder.mPrice = 5;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "85折優惠";
//        holder.mDescriptionCh = "消費滿$80以上享有85折優惠";
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 13;
//        holder.mImageResId = R.drawable.pic_13;
//        holder.mSmallImageResId = R.drawable.pic_13_s;
//        holder.mShortDescription = "1 Free Soup";
//        holder.mDescription = "Free Tomato Soup when spent more than $30";
//        holder.mStoreName = "La Gitana French Bistro";
//        holder.mAddress = "600 El Camino Real, Burlingame";
//        holder.mAddressShort = "Burlingame";
//        holder.mLatLng = "37.578246, -122.355592";
//        holder.mPrice = 0.5f;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "免費餐湯一客";
//        holder.mDescriptionCh = "消費滿$30以上即有免費番茄餐湯一客";
//        holder.mStatus = Constants.COUPON_STATUS_BOUGHT;
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 14;
//        holder.mImageResId = R.drawable.pic_14;
//        holder.mSmallImageResId = R.drawable.pic_14_s;
//        holder.mShortDescription = "20% Off";
//        holder.mDescription = "20% Off Discount";
//        holder.mStoreName = "Hamachi Bistro Japanese Restaurant";
//        holder.mAddress = "500 Flowers Ave, San Bruno";
//        holder.mAddressShort = "San Bruno";
//        holder.mLatLng = "37.629670, -122.414736";
//        holder.mPrice = 1;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "9折優惠";
//        holder.mDescriptionCh = "結帳時享有9折優惠";
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 15;
//        holder.mImageResId = R.drawable.pic_15;
//        holder.mSmallImageResId = R.drawable.pic_15_s;
//        holder.mShortDescription = "20% Off";
//        holder.mDescription = "20% Off Discount";
//        holder.mStoreName = "Speedy Goal Cart";
//        holder.mAddress = "612 Junipero Serra Blvd, South San Francisco";
//        holder.mAddressShort = "South San Francisco";
//        holder.mLatLng = "37.656131, -122.454355";
//        holder.mPrice = 3;
//        holder.mCategoryId = 2;
//        holder.mShortDescriptionCh = "9折優惠";
//        holder.mDescriptionCh = "結帳時享有9折優惠";
//        holder.mStoreNameCh ="超速卡丁車場";
//        holder.mStatus = Constants.COUPON_STATUS_EXPIRED;
//        mCouponMap.put(holder.mId, holder);
//
//
//        holder = new CouponInfo();
//        holder.mId = 16;
//        holder.mImageResId = R.drawable.pic_16;
//        holder.mSmallImageResId = R.drawable.pic_16_s;
//        holder.mShortDescription = "$1 Oyster";
//        holder.mDescription = "Unlimited Dollar Oyster after 8pm";
//        holder.mStoreName = "Sweet Island Oyster Bar";
//        holder.mAddress = "1800 Hayest St, San Francisco";
//        holder.mAddressShort = "Downtown • San Francisco";
//        holder.mLatLng = "37.773973, -122.446124";
//        holder.mPrice = 3;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "每隻生蠔$1";
//        holder.mDescriptionCh = "8pm後每隻生蠔降至$1";
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 17;
//        holder.mImageResId = R.drawable.pic_17;
//        holder.mSmallImageResId = R.drawable.pic_17_s;
//        holder.mShortDescription = "20% Off";
//        holder.mDescription = "20% Off Discount";
//        holder.mStoreName = "Umami Ramen";
//        holder.mAddress = "2800 Geneva Ave, Daly City";
//        holder.mAddressShort = "Daly City";
//        holder.mLatLng = "37.706836, -122.414578";
//        holder.mPrice = 3;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "9折優惠";
//        holder.mDescriptionCh = "結帳時享有9折優惠";
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 18;
//        holder.mImageResId = R.drawable.pic_18;
//        holder.mSmallImageResId = R.drawable.pic_18_s;
//        holder.mShortDescription = "20% Off";
//        holder.mDescription = "20% Off Discount";
//        holder.mStoreName = "Susan's Hair Salon";
//        holder.mAddress = "250 Chestnut Ave, South San Francisco";
//        holder.mAddressShort = "South San Francisco";
//        holder.mLatLng = "37.659517, -122.430172";
//        holder.mPrice = 2;
//        holder.mCategoryId = 3;
//        holder.mShortDescriptionCh = "9折優惠";
//        holder.mDescriptionCh = "結帳時享有9折優惠";
//        holder.mStoreNameCh ="珊珊髮廊";
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 19;
//        holder.mImageResId = R.drawable.pic_19;
//        holder.mSmallImageResId = R.drawable.pic_19_s;
//        holder.mShortDescription = "20% Off";
//        holder.mDescription = "20% Off Discount";
//        holder.mStoreName = "RedRum CheeseSteak Shop";
//        holder.mAddress = "1700 Old Bayshore Hwy, Burlingame";
//        holder.mAddressShort = "Burlingame";
//        holder.mLatLng = "37.601659, -122.370624";
//        holder.mPrice = 1;
//        holder.mCategoryId = 1;
//        holder.mShortDescriptionCh = "9折優惠";
//        holder.mDescriptionCh = "結帳時享有9折優惠";
//        holder.mStatus = Constants.COUPON_STATUS_EXPIRED;
//        mCouponMap.put(holder.mId, holder);
//
//        holder = new CouponInfo();
//        holder.mId = 20;
//        holder.mImageResId = R.drawable.pic_20;
//        holder.mSmallImageResId = R.drawable.pic_20_s;
//        holder.mShortDescription = "1 Free Entry";
//        holder.mDescription = "One Free Entry with purchse of 3 or more tickets";
//        holder.mStoreName = "Sculpture Art Museum";
//        holder.mAddress = "1285 48th Ave, San Francisco";
//        holder.mAddressShort = "Sunset • San Francisco";
//        holder.mLatLng = "37.762488, -122.508277";
//        holder.mPrice = 3;
//        holder.mCategoryId = 2;
//        holder.mShortDescriptionCh = "免費門票一張";
//        holder.mDescriptionCh = "購買門票3張即多送1張";
//        holder.mStoreNameCh ="雕刻藝術博物館";
//        mCouponMap.put(holder.mId, holder);
//    }
}
