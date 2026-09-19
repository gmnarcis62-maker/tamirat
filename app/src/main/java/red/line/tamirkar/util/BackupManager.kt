package red.line.tamirkar.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import red.line.tamirkar.data.Customer
import red.line.tamirkar.data.InventoryPart
import red.line.tamirkar.data.RepairTicket
import red.line.tamirkar.data.Reminder
import red.line.tamirkar.data.ShopProfile
import red.line.tamirkar.data.Technician
import red.line.tamirkar.data.Transaction
import red.line.tamirkar.data.TransactionCategory
import red.line.tamirkar.data.TransactionType
import red.line.tamirkar.data.TicketStatus
import java.io.File
import java.io.FileOutputStream

data class BackupData(
    val customers: List<Customer>,
    val tickets: List<RepairTicket>,
    val parts: List<InventoryPart>,
    val transactions: List<Transaction>,
    val technicians: List<Technician> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val shopProfile: ShopProfile = ShopProfile()
)

/**
 * پشتیبان‌گیری محلی از اطلاعات برنامه به فایل JSON (بدون نیاز به سرور یا حساب ابری).
 * فایل خروجی از طریق اشتراک‌گذاری (Share) در اختیار کاربر قرار می‌گیرد تا هرجا دوست دارد
 * (گوگل درایو، تلگرام، ایمیل و ...) ذخیره‌اش کند و در آینده برای بازیابی انتخاب کند.
 */
object BackupManager {

    fun export(context: Context, data: BackupData): Uri {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val customersArray = JSONArray()
        data.customers.forEach { c ->
            customersArray.put(
                JSONObject().apply {
                    put("id", c.id); put("fullName", c.fullName); put("phoneNumber", c.phoneNumber)
                    put("address", c.address ?: JSONObject.NULL); put("note", c.note ?: JSONObject.NULL)
                    put("createdAt", c.createdAt)
                }
            )
        }
        root.put("customers", customersArray)

        val ticketsArray = JSONArray()
        data.tickets.forEach { t ->
            ticketsArray.put(
                JSONObject().apply {
                    put("id", t.id); put("customerId", t.customerId)
                    put("deviceBrand", t.deviceBrand); put("deviceModel", t.deviceModel)
                    put("problemDescription", t.problemDescription)
                    put("accessories", t.accessories ?: JSONObject.NULL)
                    put("agreedPrice", t.agreedPrice); put("finalPrice", t.finalPrice ?: JSONObject.NULL)
                    put("status", t.status.name)
                    put("receivedAt", t.receivedAt); put("deliveredAt", t.deliveredAt ?: JSONObject.NULL)
                    put("technicianNote", t.technicianNote ?: JSONObject.NULL)
                    put("incomeRecorded", t.incomeRecorded)
                }
            )
        }
        root.put("tickets", ticketsArray)

        val partsArray = JSONArray()
        data.parts.forEach { p ->
            partsArray.put(
                JSONObject().apply {
                    put("id", p.id); put("name", p.name)
                    put("compatibleModels", p.compatibleModels ?: JSONObject.NULL)
                    put("quantity", p.quantity); put("purchasePrice", p.purchasePrice); put("sellPrice", p.sellPrice)
                    put("lowStockThreshold", p.lowStockThreshold)
                }
            )
        }
        root.put("parts", partsArray)

        val txArray = JSONArray()
        data.transactions.forEach { tr ->
            txArray.put(
                JSONObject().apply {
                    put("id", tr.id); put("type", tr.type.name); put("category", tr.category.name)
                    put("amount", tr.amount); put("note", tr.note ?: JSONObject.NULL)
                    put("date", tr.date); put("relatedTicketId", tr.relatedTicketId ?: JSONObject.NULL)
                }
            )
        }
        root.put("transactions", txArray)

        val techArray = JSONArray()
        data.technicians.forEach { t ->
            techArray.put(
                JSONObject().apply {
                    put("id", t.id); put("name", t.name); put("pinCode", t.pinCode)
                    put("isOwner", t.isOwner); put("active", t.active); put("createdAt", t.createdAt)
                }
            )
        }
        root.put("technicians", techArray)

        val remArray = JSONArray()
        data.reminders.forEach { r ->
            remArray.put(
                JSONObject().apply {
                    put("id", r.id); put("title", r.title); put("note", r.note ?: JSONObject.NULL)
                    put("dateTimeMillis", r.dateTimeMillis); put("ticketId", r.ticketId ?: JSONObject.NULL)
                    put("isDone", r.isDone); put("createdAt", r.createdAt)
                }
            )
        }
        root.put("reminders", remArray)

        root.put(
            "shopProfile",
            JSONObject().apply {
                put("shopName", data.shopProfile.shopName)
                put("ownerName", data.shopProfile.ownerName)
                put("phone", data.shopProfile.phone)
                put("address", data.shopProfile.address)
                put("logoUri", data.shopProfile.logoUri ?: JSONObject.NULL)
            }
        )

        val outDir = File(context.cacheDir, "backups").apply { mkdirs() }
        val file = File(outDir, "tamirkar_backup_${System.currentTimeMillis()}.json")
        FileOutputStream(file).use { it.write(root.toString(2).toByteArray(Charsets.UTF_8)) }

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun import(context: Context, uri: Uri): BackupData? {
        val text = context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) } ?: return null
        val root = JSONObject(text)

        val customers = mutableListOf<Customer>()
        val customersArray = root.optJSONArray("customers") ?: JSONArray()
        for (i in 0 until customersArray.length()) {
            val o = customersArray.getJSONObject(i)
            customers.add(
                Customer(
                    id = o.optLong("id"),
                    fullName = o.optString("fullName"),
                    phoneNumber = o.optString("phoneNumber"),
                    address = o.optString("address", null),
                    note = o.optString("note", null),
                    createdAt = o.optLong("createdAt")
                )
            )
        }

        val tickets = mutableListOf<RepairTicket>()
        val ticketsArray = root.optJSONArray("tickets") ?: JSONArray()
        for (i in 0 until ticketsArray.length()) {
            val o = ticketsArray.getJSONObject(i)
            tickets.add(
                RepairTicket(
                    id = o.optLong("id"),
                    customerId = o.optLong("customerId"),
                    deviceBrand = o.optString("deviceBrand"),
                    deviceModel = o.optString("deviceModel"),
                    problemDescription = o.optString("problemDescription"),
                    accessories = if (o.isNull("accessories")) null else o.optString("accessories"),
                    agreedPrice = o.optDouble("agreedPrice"),
                    finalPrice = if (o.isNull("finalPrice")) null else o.optDouble("finalPrice"),
                    status = try { TicketStatus.valueOf(o.optString("status")) } catch (e: Exception) { TicketStatus.PENDING },
                    receivedAt = o.optLong("receivedAt"),
                    deliveredAt = if (o.isNull("deliveredAt")) null else o.optLong("deliveredAt"),
                    technicianNote = if (o.isNull("technicianNote")) null else o.optString("technicianNote"),
                    incomeRecorded = o.optBoolean("incomeRecorded")
                )
            )
        }

        val parts = mutableListOf<InventoryPart>()
        val partsArray = root.optJSONArray("parts") ?: JSONArray()
        for (i in 0 until partsArray.length()) {
            val o = partsArray.getJSONObject(i)
            parts.add(
                InventoryPart(
                    id = o.optLong("id"),
                    name = o.optString("name"),
                    compatibleModels = if (o.isNull("compatibleModels")) null else o.optString("compatibleModels"),
                    quantity = o.optInt("quantity"),
                    purchasePrice = o.optDouble("purchasePrice"),
                    sellPrice = o.optDouble("sellPrice"),
                    lowStockThreshold = o.optInt("lowStockThreshold", 2)
                )
            )
        }

        val transactions = mutableListOf<Transaction>()
        val txArray = root.optJSONArray("transactions") ?: JSONArray()
        for (i in 0 until txArray.length()) {
            val o = txArray.getJSONObject(i)
            transactions.add(
                Transaction(
                    id = o.optLong("id"),
                    type = try { TransactionType.valueOf(o.optString("type")) } catch (e: Exception) { TransactionType.INCOME },
                    category = try { TransactionCategory.valueOf(o.optString("category")) } catch (e: Exception) { TransactionCategory.OTHER_INCOME },
                    amount = o.optDouble("amount"),
                    note = if (o.isNull("note")) null else o.optString("note"),
                    date = o.optLong("date"),
                    relatedTicketId = if (o.isNull("relatedTicketId")) null else o.optLong("relatedTicketId")
                )
            )
        }

        val technicians = mutableListOf<Technician>()
        val techArray = root.optJSONArray("technicians") ?: JSONArray()
        for (i in 0 until techArray.length()) {
            val o = techArray.getJSONObject(i)
            technicians.add(
                Technician(
                    id = o.optLong("id"),
                    name = o.optString("name"),
                    pinCode = o.optString("pinCode"),
                    isOwner = o.optBoolean("isOwner"),
                    active = o.optBoolean("active", true),
                    createdAt = o.optLong("createdAt")
                )
            )
        }

        val reminders = mutableListOf<Reminder>()
        val remArray = root.optJSONArray("reminders") ?: JSONArray()
        for (i in 0 until remArray.length()) {
            val o = remArray.getJSONObject(i)
            reminders.add(
                Reminder(
                    id = o.optLong("id"),
                    title = o.optString("title"),
                    note = if (o.isNull("note")) null else o.optString("note"),
                    dateTimeMillis = o.optLong("dateTimeMillis"),
                    ticketId = if (o.isNull("ticketId")) null else o.optLong("ticketId"),
                    isDone = o.optBoolean("isDone"),
                    createdAt = o.optLong("createdAt")
                )
            )
        }

        val shopProfileObj = root.optJSONObject("shopProfile")
        val shopProfile = if (shopProfileObj != null) {
            ShopProfile(
                shopName = shopProfileObj.optString("shopName"),
                ownerName = shopProfileObj.optString("ownerName"),
                phone = shopProfileObj.optString("phone"),
                address = shopProfileObj.optString("address"),
                logoUri = if (shopProfileObj.isNull("logoUri")) null else shopProfileObj.optString("logoUri")
            )
        } else ShopProfile()

        return BackupData(customers, tickets, parts, transactions, technicians, reminders, shopProfile)
    }
}
