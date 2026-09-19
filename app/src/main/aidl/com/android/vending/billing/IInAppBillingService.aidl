package com.android.vending.billing;

import android.os.Bundle;

/**
 * این فایل AIDL همان اینترفیس استاندارد Google In-App Billing نسخه ۳ است که
 * مایکت (و سایر مارکت‌های ایرانی مثل کافه‌بازار) برای سازگاری با اپ‌های موجود
 * از همین اینترفیس استفاده می‌کنند. نیازی به تغییر این فایل نیست.
 *
 * مستندات کامل: پنل توسعه‌دهندگان مایکت > بخش In-App Billing
 */
interface IInAppBillingService {
    int isBillingSupported(int apiVersion, String packageName, String type);

    /**
     * bundle خروجی شامل:
     * RESPONSE_CODE
     * DETAILS_LIST: فهرستی از رشته‌های JSON با جزئیات هر محصول
     */
    Bundle getSkuDetails(int apiVersion, String packageName, String type, in Bundle skusBundle);

    /**
     * bundle خروجی شامل:
     * RESPONSE_CODE
     * BUY_INTENT: یک PendingIntent برای شروع فرآیند خرید
     */
    Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type, String developerPayload);

    /**
     * bundle خروجی شامل:
     * RESPONSE_CODE
     * INAPP_PURCHASE_ITEM_LIST: فهرست شناسه‌های محصولات خریداری‌شده
     * INAPP_PURCHASE_DATA_LIST: فهرست رشته‌های JSON با جزئیات خرید
     * INAPP_DATA_SIGNATURE_LIST: امضای دیجیتال هر خرید
     * INAPP_CONTINUATION_TOKEN: برای صفحه‌بندی نتایج (در صورت وجود)
     */
    Bundle getPurchases(int apiVersion, String packageName, String type, String continuationToken);

    int consumePurchase(int apiVersion, String packageName, String purchaseToken);
}
