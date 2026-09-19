package red.line.tamirkar.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import red.line.tamirkar.data.Customer
import red.line.tamirkar.data.RepairTicket
import red.line.tamirkar.data.Transaction
import java.io.File
import java.io.FileOutputStream

/**
 * خروجی‌گیری CSV از داده‌های برنامه (قابل باز شدن در Excel، Google Sheets و ...)
 * از UTF-8 BOM استفاده می‌شود تا متن فارسی در Excel به‌درستی نمایش داده شود.
 */
object CsvExporter {

    private const val BOM = "\uFEFF"

    private fun writeCsv(context: Context, fileName: String, content: String): Uri {
        val outDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(outDir, fileName)
        FileOutputStream(file).use { it.write((BOM + content).toByteArray(Charsets.UTF_8)) }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun escape(value: String): String = "\"${value.replace("\"", "\"\"")}\""

    fun exportCustomers(context: Context, customers: List<Customer>): Uri {
        val sb = StringBuilder()
        sb.appendLine("نام,شماره تماس,آدرس,تاریخ ثبت")
        customers.forEach {
            sb.appendLine(
                listOf(
                    escape(it.fullName),
                    escape(it.phoneNumber),
                    escape(it.address ?: ""),
                    escape(PersianDateUtils.formatDate(it.createdAt))
                ).joinToString(",")
            )
        }
        return writeCsv(context, "customers_${System.currentTimeMillis()}.csv", sb.toString())
    }

    fun exportTickets(context: Context, tickets: List<RepairTicket>, customerNameOf: (Long) -> String): Uri {
        val sb = StringBuilder()
        sb.appendLine("مشتری,برند,مدل,ایراد,وضعیت,قیمت توافقی,قیمت نهایی,تاریخ پذیرش,تاریخ تحویل")
        tickets.forEach {
            sb.appendLine(
                listOf(
                    escape(customerNameOf(it.customerId)),
                    escape(it.deviceBrand),
                    escape(it.deviceModel),
                    escape(it.problemDescription),
                    escape(it.status.label),
                    it.agreedPrice.toString(),
                    (it.finalPrice ?: "").toString(),
                    escape(PersianDateUtils.formatDate(it.receivedAt)),
                    escape(it.deliveredAt?.let { d -> PersianDateUtils.formatDate(d) } ?: "")
                ).joinToString(",")
            )
        }
        return writeCsv(context, "tickets_${System.currentTimeMillis()}.csv", sb.toString())
    }

    fun exportTransactions(context: Context, transactions: List<Transaction>): Uri {
        val sb = StringBuilder()
        sb.appendLine("نوع,دسته‌بندی,مبلغ,توضیحات,تاریخ")
        transactions.forEach {
            sb.appendLine(
                listOf(
                    escape(it.type.label),
                    escape(it.category.label),
                    it.amount.toString(),
                    escape(it.note ?: ""),
                    escape(PersianDateUtils.formatDateTime(it.date))
                ).joinToString(",")
            )
        }
        return writeCsv(context, "transactions_${System.currentTimeMillis()}.csv", sb.toString())
    }
}
