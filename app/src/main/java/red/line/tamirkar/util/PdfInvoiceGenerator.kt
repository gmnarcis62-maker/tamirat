package red.line.tamirkar.util

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import red.line.tamirkar.data.RepairTicket
import red.line.tamirkar.data.ShopProfile
import java.io.File
import java.io.FileOutputStream

/**
 * تولید فاکتور/فیش تعمیر به‌صورت PDF با استفاده از android.graphics.pdf.PdfDocument
 * (بدون نیاز به هیچ کتابخانه‌ی خارجی) و به‌همراه لوگوی اختصاصی فروشگاه در صورت تنظیم.
 */
object PdfInvoiceGenerator {

    private const val PAGE_WIDTH = 595  // A4 در ۷۲dpi
    private const val PAGE_HEIGHT = 842

    fun generate(context: Context, ticket: RepairTicket, customerName: String, customerPhone: String, shop: ShopProfile): Uri {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply { color = Color.BLACK; textSize = 20f; isFakeBoldText = true; textAlign = Paint.Align.RIGHT }
        val normalPaint = Paint().apply { color = Color.DKGRAY; textSize = 12f; textAlign = Paint.Align.RIGHT }
        val labelPaint = Paint().apply { color = Color.BLACK; textSize = 13f; isFakeBoldText = true; textAlign = Paint.Align.RIGHT }
        val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }
        val redPaint = Paint().apply { color = Color.rgb(211, 47, 47); textSize = 14f; isFakeBoldText = true; textAlign = Paint.Align.RIGHT }

        val rightMargin = PAGE_WIDTH - 40f
        var y = 50f

        // لوگو (در صورت وجود) - گوشه‌ی بالا سمت چپ
        shop.logoUri?.let { uriString ->
            try {
                val bitmap = context.contentResolver.openInputStream(Uri.parse(uriString))?.use {
                    BitmapFactory.decodeStream(it)
                }
                bitmap?.let {
                    val logoSize = 60
                    canvas.drawBitmap(it, null, android.graphics.RectF(40f, 30f, 40f + logoSize, 30f + logoSize), null)
                }
            } catch (_: Exception) {
                // لوگو در دسترس نبود؛ بدون لوگو ادامه بده
            }
        }

        canvas.drawText(shop.shopName.ifBlank { "دستیار تعمیرکار" }, rightMargin, y, titlePaint)
        y += 22f
        if (shop.phone.isNotBlank()) {
            canvas.drawText("تلفن: ${shop.phone}", rightMargin, y, normalPaint)
            y += 16f
        }
        if (shop.address.isNotBlank()) {
            canvas.drawText("آدرس: ${shop.address}", rightMargin, y, normalPaint)
            y += 16f
        }

        y += 10f
        canvas.drawLine(40f, y, rightMargin, y, linePaint)
        y += 30f

        canvas.drawText("فاکتور / فیش تعمیر شماره ${PersianDateUtils.toPersianNumber(ticket.id)}", rightMargin, y, labelPaint)
        y += 20f
        canvas.drawText("تاریخ پذیرش: ${PersianDateUtils.formatDateLong(ticket.receivedAt)}", rightMargin, y, normalPaint)
        y += 30f

        canvas.drawText("مشخصات مشتری", rightMargin, y, labelPaint)
        y += 18f
        canvas.drawText("نام: $customerName", rightMargin, y, normalPaint)
        y += 16f
        canvas.drawText("تلفن: $customerPhone", rightMargin, y, normalPaint)
        y += 30f

        canvas.drawText("مشخصات دستگاه", rightMargin, y, labelPaint)
        y += 18f
        canvas.drawText("برند و مدل: ${ticket.deviceBrand} ${ticket.deviceModel}", rightMargin, y, normalPaint)
        y += 16f
        canvas.drawText("شرح ایراد: ${ticket.problemDescription}", rightMargin, y, normalPaint)
        y += 16f
        ticket.accessories?.let {
            canvas.drawText("لوازم همراه: $it", rightMargin, y, normalPaint)
            y += 16f
        }
        y += 20f

        canvas.drawLine(40f, y, rightMargin, y, linePaint)
        y += 30f

        val finalAmount = ticket.finalPrice ?: ticket.agreedPrice
        canvas.drawText("مبلغ قابل پرداخت: ${PersianDateUtils.formatToman(finalAmount)}", rightMargin, y, redPaint)
        y += 40f

        canvas.drawLine(40f, y, rightMargin, y, linePaint)
        y += 20f
        val footerPaint = Paint().apply { color = Color.GRAY; textSize = 10f; textAlign = Paint.Align.CENTER }
        canvas.drawText("توسعه‌یافته توسط تیم نرم‌افزاری ردلاین سافت البرز", PAGE_WIDTH / 2f, PAGE_HEIGHT - 30f, footerPaint)

        document.finishPage(page)

        val fileName = "Invoice_${ticket.id}_${System.currentTimeMillis()}.pdf"
        val outDir = File(context.cacheDir, "invoices").apply { mkdirs() }
        val file = File(outDir, fileName)
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
