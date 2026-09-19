package red.line.tamirkar.data

/**
 * پایگاه‌داده اولیه‌ی عیب‌یابی گوشی موبایل
 * این لیست هنگام اولین اجرای برنامه در دیتابیس Room درج می‌شود (RoomDatabase.Callback)
 * تیم توسعه می‌تواند در آینده موارد بیشتری اضافه کند یا این داده را از یک API آنلاین بروزرسانی کند.
 */
object TroubleshootingSeedData {

    fun defaultGuides(): List<TroubleshootingGuide> = listOf(
        TroubleshootingGuide(
            category = TroubleshootingCategory.DISPLAY,
            brand = "همه برندها",
            title = "صفحه نمایش سیاه ولی گوشی روشن است",
            symptoms = "گوشی زنگ می‌خورد و ویبره دارد اما تصویری روی صفحه دیده نمی‌شود",
            possibleCauses = "قطعی فلت تاچ/ال‌سی‌دی، خرابی IC بک‌لایت، ضربه‌خوردگی پنل",
            solutionSteps = "۱. بررسی اتصال فلت نمایشگر به برد اصلی\n۲. تست با نمایشگر سالم دیگر\n۳. اندازه‌گیری ولتاژ بک‌لایت با مولتی‌متر\n۴. در صورت خرابی IC، ارسال به بردکار",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.DISPLAY,
            brand = "همه برندها",
            title = "لمس صفحه در قسمتی از نمایشگر کار نمی‌کند",
            symptoms = "بخشی از صفحه به لمس پاسخ نمی‌دهد یا لمس زیاده (Ghost Touch) دارد",
            possibleCauses = "آسیب فلت تاچ، رطوبت، شکستگی گلس، تداخل نرم‌افزاری",
            solutionSteps = "۱. پاک‌سازی و خشک کردن اطراف نمایشگر\n۲. تست در Safe Mode برای رد کردن مشکل نرم‌افزاری\n۳. بررسی و تعویض فلت تاچ\n۴. در صورت شکستگی گلس، تعویض کامل ال‌سی‌دی",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.BATTERY_CHARGING,
            brand = "همه برندها",
            title = "باتری خیلی سریع خالی می‌شود",
            symptoms = "کاهش شدید عمر باتری نسبت به قبل، خاموش شدن ناگهانی روی درصد بالا",
            possibleCauses = "افت سلامت باتری (Battery Health)، اپلیکیشن پرمصرف در پس‌زمینه، خرابی IC شارژ",
            solutionSteps = "۱. بررسی درصد سلامت باتری از تنظیمات یا اپ تشخیصی\n۲. شناسایی اپلیکیشن‌های پرمصرف\n۳. تست با باتری سالم دیگر\n۴. در صورت افت سلامت زیر ۸۰٪، تعویض باتری",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.BATTERY_CHARGING,
            brand = "همه برندها",
            title = "گوشی شارژ نمی‌شود یا شارژ کند است",
            symptoms = "با اتصال شارژر، آیکون شارژ نمایش داده نمی‌شود یا شارژ بسیار کند پیش می‌رود",
            possibleCauses = "خرابی کابل/آداپتور، کثیفی پورت شارژ، خرابی IC شارژ، خرابی باتری",
            solutionSteps = "۱. تست با کابل و آداپتور اورجینال دیگر\n۲. بازدید و تمیزکاری پورت شارژ با سوزن پلاستیکی\n۳. اندازه‌گیری ولتاژ ورودی روی برد\n۴. بررسی IC شارژ و در صورت نیاز تعویض",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.CHARGING_PORT,
            brand = "همه برندها",
            title = "پورت شارژ شل شده و اتصال قطع و وصل می‌شود",
            symptoms = "برای شارژ شدن باید کابل را با زاویه‌ی خاصی نگه داشت",
            possibleCauses = "فرسودگی پین‌های پورت شارژ، اکسید شدن پورت، شکستگی لحیم پورت از برد",
            solutionSteps = "۱. بازرسی چشمی پورت برای اکسید یا خم‌شدگی پین\n۲. تست تداوم اتصال با مولتی‌متر\n۳. تعویض پورت شارژ (معمولاً یک قطعه‌ی مجزا در اکثر مدل‌ها)",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "همه برندها",
            title = "گوشی در حالت بوت‌لوپ گیر کرده (ریست مداوم)",
            symptoms = "گوشی مدام لوگو را نشان می‌دهد و ریستارت می‌شود، وارد سیستم نمی‌شود",
            possibleCauses = "کرش نرم‌افزاری بعد از آپدیت، پارتیشن سیستم خراب، در موارد نادر خرابی سخت‌افزاری حافظه",
            solutionSteps = "۱. ورود به Recovery Mode و پاک کردن Cache Partition\n۲. در صورت عدم رفع، Factory Reset (با اطلاع مشتری از پاک شدن اطلاعات)\n۳. اگر مشکل ادامه داشت، فلش کامل فرم‌ور با باکس\n۴. بررسی سلامت حافظه eMMC/UFS",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "همه برندها",
            title = "گوشی قفل الگو/پین را فراموش کرده (بدون ریست کارخانه)",
            symptoms = "کاربر رمز عبور را فراموش کرده و نمی‌خواهد اطلاعات پاک شود",
            possibleCauses = "-",
            solutionSteps = "۱. بررسی امکان استفاده از Find My Device / Find My iPhone با اطلاعات ورود مشتری\n۲. توضیح به مشتری که در بیشتر موارد مدرن، پاک‌سازی اطلاعات غیرقابل‌اجتناب است\n۳. در صورت مجوز مالکیت، استفاده از ابزارهای رسمی آنباکس با تایید IMEI",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.CAMERA,
            brand = "همه برندها",
            title = "دوربین تصویر تار یا سیاه می‌گیرد",
            symptoms = "پیش‌نمایش دوربین سیاه است یا تصاویر کاملاً تار و غیرقابل استفاده‌اند",
            possibleCauses = "قطعی فلت دوربین، خرابی خود ماژول دوربین، رطوبت پشت لنز، مشکل نرم‌افزاری اپ دوربین",
            solutionSteps = "۱. تست با اپلیکیشن دوربین دیگر یا Safe Mode\n۲. بررسی اتصال فلت دوربین به برد\n۳. باز کردن و بررسی چشمی رطوبت پشت لنز\n۴. تعویض ماژول دوربین در صورت خرابی فیزیکی",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.AUDIO,
            brand = "همه برندها",
            title = "صدای مکالمه یا اسپیکر شنیده نمی‌شود",
            symptoms = "در تماس صدای طرف مقابل شنیده نمی‌شود یا اسپیکر بلندگو کار نمی‌کند",
            possibleCauses = "گرفتگی توری اسپیکر، خرابی IC صدا، قطعی فلت اسپیکر",
            solutionSteps = "۱. بررسی و تمیزکاری توری اسپیکر با برس نرم\n۲. تست صدا در حالت اسپیکر و هندزفری برای تفکیک مشکل\n۳. بازرسی فلت و کانکتور اسپیکر\n۴. بررسی IC صدا روی برد در صورت تخصصی بودن مشکل",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.AUDIO,
            brand = "همه برندها",
            title = "میکروفون در تماس کار نمی‌کند",
            symptoms = "طرف مقابل صدای کاربر را نمی‌شنود، ولی صدای اسپیکر سالم است",
            possibleCauses = "گرفتگی سوراخ میکروفون، خرابی میکروفون، تنظیمات اشتباه اپ",
            solutionSteps = "۱. تست ضبط صدا با اپ دیکتافون\n۲. بررسی و تمیزکاری سوراخ میکروفون\n۳. تعویض میکروفون در صورت خرابی فیزیکی",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.NETWORK_SIM,
            brand = "همه برندها",
            title = "آنتن‌دهی ضعیف یا قطعی مداوم شبکه",
            symptoms = "آنتن به‌طور ناگهانی افت می‌کند یا گوشی مدام از شبکه خارج می‌شود",
            possibleCauses = "خرابی IC آنتن یا پاور آمپلی‌فایر، مشکل سیم‌کارت، فلت آنتن آسیب‌دیده",
            solutionSteps = "۱. تست با سیم‌کارت دیگر و در منطقه‌ی دیگر\n۲. بررسی تنظیمات شبکه و ریست Network Settings\n۳. بازرسی فلت و کانکتور آنتن داخل گوشی\n۴. بررسی IC آنتن روی برد",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.NETWORK_SIM,
            brand = "همه برندها",
            title = "سیم‌کارت شناسایی نمی‌شود",
            symptoms = "پیام «No SIM» یا «سیم‌کارت وارد نشده» نمایش داده می‌شود",
            possibleCauses = "کثیفی یا خش پین‌های خوان سیم‌کارت، خرابی خود سیم‌کارت، خرابی IC خوان کارت",
            solutionSteps = "۱. تست سیم‌کارت روی گوشی دیگر\n۲. تمیزکاری پین‌های تری سیم‌کارت\n۳. بررسی سلامت سوکت سیم‌کارت روی برد\n۴. تعویض سوکت یا بررسی IC مربوطه در موارد تخصصی",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SENSORS,
            brand = "همه برندها",
            title = "سنسور نزدیکی در تماس درست کار نمی‌کند",
            symptoms = "صفحه در حین تماس تلفنی خاموش نمی‌شود یا برعکس، هنگام دور کردن گوشی از گوش روشن نمی‌شود",
            possibleCauses = "کثیفی روی سنسور، خرابی نرم‌افزاری کالیبراسیون، خرابی فیزیکی سنسور",
            solutionSteps = "۱. تمیزکاری سطح سنسور نزدیکی بالای صفحه\n۲. کالیبراسیون مجدد سنسور از اپ‌های تشخیصی\n۳. تعویض سنسور در صورت خرابی فیزیکی",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SENSORS,
            brand = "همه برندها",
            title = "دکمه پاور یا ولوم کار نمی‌کند",
            symptoms = "با فشار دادن دکمه، واکنشی مشاهده نمی‌شود یا واکنش نامنظم است",
            possibleCauses = "فرسودگی مکانیکی دکمه، ورود گردوغبار، قطعی فلت دکمه",
            solutionSteps = "۱. تست دکمه از منوی تشخیصی سخت‌افزار (در صورت وجود)\n۲. تمیزکاری اطراف دکمه با اسپری هوای فشرده\n۳. بررسی و تعویض فلت یا سوییچ دکمه",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.OVERHEATING,
            brand = "همه برندها",
            title = "گوشی به‌شدت داغ می‌کند",
            symptoms = "بدنه‌ی گوشی حتی در استفاده‌ی عادی خیلی گرم می‌شود",
            possibleCauses = "پردازش سنگین پس‌زمینه، خرابی باتری، اتصالی جزئی روی برد، مشکل شارژر",
            solutionSteps = "۱. بررسی اپلیکیشن‌های پرمصرف پردازنده از تنظیمات باتری\n۲. تست دمای گوشی در Safe Mode\n۳. تست با باتری و شارژر دیگر\n۴. بررسی برد از نظر اتصالی یا IC معیوب با ترموکمرا (در صورت وجود)",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.WATER_DAMAGE,
            brand = "همه برندها",
            title = "گوشی در آب افتاده و روشن نمی‌شود",
            symptoms = "بعد از تماس با آب، گوشی خاموش است یا رفتار غیرعادی دارد",
            possibleCauses = "اتصالی روی برد در اثر رطوبت، خوردگی پین‌های کانکتور، کوتاه شدن IC ها",
            solutionSteps = "۱. فوراً گوشی را خاموش نگه دارید و به برق وصل نکنید\n۲. باز کردن گوشی و خشک‌کردن با الکل ایزوپروپیل و برس نرم\n۳. بررسی برد زیر لوپ برای خوردگی\n۴. اولترازونیک برد در صورت وجود دستگاه مخصوص\n۵. تست روشن‌شدن با منبع تغذیه آزمایشگاهی قبل از اتصال باتری",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.DISPLAY,
            brand = "Samsung",
            title = "خط سبز یا سوختگی روی صفحه AMOLED سامسونگ",
            symptoms = "خطوط عمودی/افقی رنگی یا نواحی سوخته روی پنل مشاهده می‌شود",
            possibleCauses = "فرسودگی طبیعی پنل AMOLED، آسیب فیزیکی، مشکل درایور نمایشگر",
            solutionSteps = "۱. تست با نرم‌افزار Display Test برای تایید سخت‌افزاری بودن مشکل\n۲. بررسی احتمال گارانتی سامسونگ\n۳. تعویض کامل پنل در صورت لزوم",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "Apple",
            title = "آیفون خطای اتصال به آیتونز (Error 4013/9)",
            symptoms = "در حین بروزرسانی یا ریستور، خطای شماره‌دار نمایش داده می‌شود",
            possibleCauses = "مشکل کابل/پورت USB، خرابی نرم‌افزاری، در موارد نادر خرابی هارد/NAND",
            solutionSteps = "۱. تست با کابل و پورت USB دیگر\n۲. ریستور در حالت DFU Mode\n۳. بررسی سلامت NAND با ابزار تخصصی در صورت تکرار خطا",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.BATTERY_CHARGING,
            brand = "Apple",
            title = "پیام «نیاز به سرویس باتری» روی آیفون",
            symptoms = "در تنظیمات Battery Health پیام هشدار سرویس باتری نمایش داده می‌شود",
            possibleCauses = "افت ظرفیت باتری زیر آستانه، عدم تطابق باتری غیر اورجینال",
            solutionSteps = "۱. بررسی درصد Maximum Capacity\n۲. تعویض باتری با قطعه اورجینال یا با کیفیت بالا\n۳. کالیبراسیون بعد از تعویض (چند سیکل شارژ کامل)",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "Xiaomi",
            title = "گوشی شیائومی قفل Mi Account دارد",
            symptoms = "بعد از ریست، گوشی حساب Mi Account قبلی را می‌خواهد",
            possibleCauses = "فعال بودن Find Device، عدم خروج از حساب قبل از ریست",
            solutionSteps = "۱. تلاش برای دریافت اطلاعات حساب از مالک قبلی/مشتری\n۲. ورود مجدد با همان Mi Account\n۳. در صورت عدم دسترسی، امکان رفع تنها با ابزارهای تخصصی و با احراز مالکیت دستگاه امکان‌پذیر است",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.CHARGING_PORT,
            brand = "Xiaomi",
            title = "شارژر اورجینال شیائومی شارژ سریع نمی‌کند",
            symptoms = "با شارژر اصلی، سرعت شارژ کند است و حالت Fast Charging فعال نمی‌شود",
            possibleCauses = "کابل نامناسب، تنظیمات محدودیت شارژ سریع در MIUI، خرابی IC شارژ سریع",
            solutionSteps = "۱. بررسی فعال بودن گزینه Fast Charging در تنظیمات باتری\n۲. تست با کابل اورجینال دیگر\n۳. بررسی IC شارژ سریع روی برد",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.CAMERA,
            brand = "همه برندها",
            title = "دوربین سلفی در برخی اپلیکیشن‌ها باز نمی‌شود",
            symptoms = "دوربین اصلی سالم است ولی دوربین جلو در اپ خاصی کرش می‌کند",
            possibleCauses = "مشکل مجوز دسترسی اپ، کش خراب اپلیکیشن، ناسازگاری نرم‌افزاری",
            solutionSteps = "۱. بررسی و فعال کردن مجدد مجوز دوربین برای اپ\n۲. پاک کردن Cache اپلیکیشن\n۳. بروزرسانی یا نصب مجدد اپلیکیشن",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.NETWORK_SIM,
            brand = "همه برندها",
            title = "وای‌فای وصل می‌شود ولی اینترنت ندارد یا قطع و وصل می‌شود",
            symptoms = "آیکون وای‌فای متصل است اما مرورگر صفحات را باز نمی‌کند",
            possibleCauses = "مشکل از روتر، تداخل نرم‌افزاری، خرابی IC وای‌فای",
            solutionSteps = "۱. تست اتصال به شبکه وای‌فای دیگر\n۲. ریست تنظیمات شبکه گوشی\n۳. بررسی آنتن و IC وای‌فای/بلوتوث روی برد در صورت تکرار در همه شبکه‌ها",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.OVERHEATING,
            brand = "همه برندها",
            title = "گوشی حین شارژ خیلی داغ می‌شود",
            symptoms = "دمای بدنه هنگام شارژ به‌طور غیرعادی بالا می‌رود",
            possibleCauses = "شارژر یا کابل غیر استاندارد، خرابی باتری، مصرف پردازنده هنگام شارژ",
            solutionSteps = "۱. تست با شارژر و کابل استاندارد اورجینال\n۲. بستن اپلیکیشن‌های پس‌زمینه هنگام شارژ\n۳. بررسی وضعیت باتری برای تورم یا افت سلامت",
            difficulty = "آسان"
        ),

        // ===== موارد تکمیلی صفحه نمایش =====
        TroubleshootingGuide(
            category = TroubleshootingCategory.DISPLAY,
            brand = "همه برندها",
            title = "صفحه نمایش لرزش یا پرش تصویر دارد (Flickering)",
            symptoms = "تصویر روی صفحه گاهی می‌لرزد، چشمک می‌زند یا خطوط موجی نمایش داده می‌شود",
            possibleCauses = "شل بودن اتصال فلت نمایشگر، خرابی IC درایور نمایشگر، افت ولتاژ تغذیه پنل",
            solutionSteps = "۱. باز کردن گوشی و محکم‌کردن مجدد کانکتور فلت نمایشگر\n۲. تست با نمایشگر سالم دیگر برای رد کردن خرابی برد\n۳. بررسی IC درایور نمایشگر روی برد در صورت تکرار مشکل",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.DISPLAY,
            brand = "همه برندها",
            title = "سوختگی تصویر روی پنل AMOLED (Burn-in)",
            symptoms = "بعد از خاموش کردن صفحه، ردی محو از آیکون‌ها یا نوار وضعیت باقی می‌ماند",
            possibleCauses = "فرسودگی طبیعی پیکسل‌های OLED در اثر نمایش طولانی‌مدت یک تصویر ثابت",
            solutionSteps = "۱. توضیح به مشتری که این پدیده طبیعی و مربوط به فرسودگی پنل است\n۲. پیشنهاد کاهش روشنایی و فعال‌سازی چرخش خودکار آیکون‌ها\n۳. در موارد شدید، تنها راه‌حل تعویض کامل پنل است",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.DISPLAY,
            brand = "همه برندها",
            title = "رنگ صفحه به زرد یا آبی متمایل شده است",
            symptoms = "بعد از تعویض نمایشگر یا ضربه، رنگ‌های صفحه طبیعی نیستند",
            possibleCauses = "استفاده از نمایشگر غیراورجینال (کپی/کپی-اصلی)، خرابی جزئی لایه‌ی رنگ پنل",
            solutionSteps = "۱. تست کالیبراسیون رنگ از تنظیمات نمایشگر\n۲. مقایسه با نمایشگر اورجینال مشابه\n۳. در صورت استفاده از قطعه غیراصلی، توضیح محدودیت کیفیت به مشتری یا تعویض با قطعه اورجینال",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.DISPLAY,
            brand = "همه برندها",
            title = "صفحه نمایش با تاخیر به لمس واکنش می‌دهد (Touch Lag)",
            symptoms = "بین لمس صفحه و واکنش گوشی تاخیر محسوس وجود دارد",
            possibleCauses = "گلس محافظ نامناسب یا ضخیم، مشکل نرم‌افزاری، افت کیفیت IC لمسی بعد از تعمیر قبلی",
            solutionSteps = "۱. حذف گلس محافظ و تست مجدد\n۲. تست در Safe Mode برای رد مشکل نرم‌افزاری\n۳. بررسی کیفیت لحیم‌کاری IC تاچ در صورت تعمیر قبلی",
            difficulty = "متوسط"
        ),

        // ===== موارد تکمیلی باتری و شارژ =====
        TroubleshootingGuide(
            category = TroubleshootingCategory.BATTERY_CHARGING,
            brand = "همه برندها",
            title = "باتری متورم شده است (Battery Swelling)",
            symptoms = "پشت گوشی یا صفحه نمایش کمی برجسته و قوس‌دار شده است",
            possibleCauses = "پایان عمر شیمیایی باتری، شارژ مکرر بیش از حد، آسیب فیزیکی به سلول باتری",
            solutionSteps = "⚠️ خطر ایمنی: فوراً باتری را از مدار شارژ خارج کنید\n۱. خاموش نگه‌داشتن گوشی تا زمان تعویض\n۲. تعویض فوری باتری با قطعه استاندارد\n۳. عدم فشار یا سوراخ کردن باتری متورم به هیچ عنوان",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.BATTERY_CHARGING,
            brand = "همه برندها",
            title = "گوشی فقط وقتی به شارژر وصل است روشن می‌ماند",
            symptoms = "با قطع کابل شارژ، گوشی بلافاصله خاموش می‌شود",
            possibleCauses = "باتری کاملاً خراب یا اتصالات باتری قطع، خرابی IC مدیریت باتری (PMIC)",
            solutionSteps = "۱. تست با باتری سالم دیگر\n۲. بررسی کانکتور و پین‌های باتری روی برد\n۳. بررسی IC مدیریت باتری (PMIC) در صورت ادامه مشکل با باتری سالم",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.BATTERY_CHARGING,
            brand = "همه برندها",
            title = "شارژ بی‌سیم (Wireless Charging) کار نمی‌کند",
            symptoms = "روی پد شارژر بی‌سیم قرار می‌گیرد اما شارژ نمی‌شود",
            possibleCauses = "خرابی کویل شارژ بی‌سیم، جابجایی پوسته/بک‌گلس غیراستاندارد، خرابی IC مربوطه",
            solutionSteps = "۱. تست با پد شارژر بی‌سیم دیگر و در وسط دقیق پد\n۲. بررسی اورجینال بودن بک‌گلس (بک‌گلس غیراصلی می‌تواند کویل را مسدود کند)\n۳. بررسی اتصال فلت کویل شارژ بی‌سیم داخل گوشی",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.BATTERY_CHARGING,
            brand = "همه برندها",
            title = "درصد باتری ناگهان از ۵۰٪ به ۰٪ می‌پرد",
            symptoms = "درصد نمایش‌داده‌شده باتری با واقعیت همخوانی ندارد و ناگهان افت می‌کند",
            possibleCauses = "عدم کالیبراسیون درست باتری، باتری غیراورجینال بدون چیپ تشخیص هوشمند",
            solutionSteps = "۱. یک سیکل کامل شارژ (۰ تا ۱۰۰٪ بدون وقفه) برای کالیبراسیون مجدد\n۲. بررسی اورجینال بودن باتری نصب‌شده\n۳. در صورت تکرار، بررسی سلامت IC فیول‌گیج باتری",
            difficulty = "آسان"
        ),

        // ===== موارد تکمیلی پورت شارژ =====
        TroubleshootingGuide(
            category = TroubleshootingCategory.CHARGING_PORT,
            brand = "همه برندها",
            title = "پین‌های داخل پورت شارژ خم یا آسیب دیده‌اند",
            symptoms = "کابل به‌سختی وارد پورت می‌شود یا اتصال بسیار لرزان است",
            possibleCauses = "ورود ناشیانه‌ی کابل با زاویه اشتباه، ضربه به پورت",
            solutionSteps = "۱. بازرسی چشمی پورت زیر لوپ برای تشخیص خم‌شدگی پین\n۲. صاف کردن پین با ابزار ظریف در موارد جزئی\n۳. تعویض کامل پورت در صورت آسیب شدید",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.CHARGING_PORT,
            brand = "همه برندها",
            title = "گوشی فقط با یک کابل خاص شارژ می‌شود",
            symptoms = "با اکثر کابل‌ها شارژ نمی‌شود ولی با یک کابل خاص کار می‌کند",
            possibleCauses = "ساییدگی جزئی پین‌های داده/تغذیه پورت، افت کیفیت کابل‌های دیگر",
            solutionSteps = "۱. تست با چند کابل استاندارد متفاوت\n۲. تمیزکاری کامل پورت با اسپری کنتاکت‌کلینر\n۳. تعویض پورت در صورت تایید ساییدگی پین‌ها",
            difficulty = "آسان"
        ),

        // ===== موارد تکمیلی نرم‌افزار =====
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "همه برندها",
            title = "قفل FRP بعد از ریست کارخانه (Factory Reset Protection)",
            symptoms = "بعد از Hard Reset، گوشی حساب گوگل قبلی را برای تایید می‌خواهد",
            possibleCauses = "فعال بودن حساب گوگل روی دستگاه قبل از ریست",
            solutionSteps = "۱. تلاش برای ورود با همان حساب گوگل قبلی (در صورت در دسترس بودن اطلاعات مشتری)\n۲. بررسی مدارک مالکیت دستگاه قبل از هرگونه اقدام برای دور زدن قفل\n۳. طبق قوانین، دور زدن FRP بدون احراز مالکیت توصیه نمی‌شود",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "همه برندها",
            title = "IMEI یا Baseband به‌صورت Null/Unknown نمایش داده می‌شود",
            symptoms = "در تنظیمات About Phone، شماره IMEI خالی یا نامعتبر است و آنتن‌دهی وجود ندارد",
            possibleCauses = "خرابی پارتیشن EFS/Modem بعد از فلش نادرست، خرابی سخت‌افزاری ماژول مودم",
            solutionSteps = "۱. بررسی بک‌آپ پارتیشن EFS قبل از هرگونه فلش (پیشگیری در تعمیرات بعدی)\n۲. بازیابی EFS از بک‌آپ در صورت وجود\n۳. فلش مجدد فرم‌ور کامل با باکس تخصصی\n۴. در صورت خرابی سخت‌افزاری، بررسی ماژول مودم روی برد",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "همه برندها",
            title = "حافظه داخلی پر شده و گوشی کند شده است",
            symptoms = "پیام هشدار فضای کم، کندی کلی سیستم و اپلیکیشن‌ها",
            possibleCauses = "انباشت کش و فایل‌های موقت، تعداد زیاد عکس/ویدیو، اپلیکیشن‌های غیرضروری",
            solutionSteps = "۱. پاک‌سازی کش سیستم از Recovery Mode\n۲. راهنمایی مشتری برای انتقال عکس/ویدیو به فضای ابری یا کامپیوتر\n۳. حذف اپلیکیشن‌های استفاده‌نشده",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "همه برندها",
            title = "اپلیکیشن‌ها مدام کرش می‌کنند یا بسته می‌شوند",
            symptoms = "یک یا چند اپلیکیشن به‌محض باز شدن دوباره بسته می‌شوند",
            possibleCauses = "کش خراب اپلیکیشن، ناسازگاری نسخه با سیستم‌عامل، فضای رم پر",
            solutionSteps = "۱. پاک کردن Cache و Data اپلیکیشن مشکل‌دار\n۲. بروزرسانی اپلیکیشن از استور\n۳. تست در Safe Mode برای رد تداخل اپ‌های دیگر\n۴. در صورت تکرار در همه اپ‌ها، بررسی سلامت حافظه رم/سیستم",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "همه برندها",
            title = "بروزرسانی نرم‌افزاری (OTA) با خطا متوقف می‌شود",
            symptoms = "درصد آپدیت پیش نمی‌رود یا خطای نصب نمایش داده می‌شود",
            possibleCauses = "فضای ذخیره‌سازی ناکافی، قطعی اینترنت حین دانلود، خرابی پارتیشن بروزرسانی",
            solutionSteps = "۱. اطمینان از وجود حداقل ۲ برابر حجم فایل آپدیت فضای خالی\n۲. تست با شبکه وای‌فای پایدار\n۳. پاک‌کردن Cache Partition از Recovery Mode\n۴. در صورت تکرار، فلش دستی نسخه جدید با باکس",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "همه برندها",
            title = "گوشی به‌طور تصادفی ریستارت می‌شود (Random Reboot)",
            symptoms = "بدون دلیل مشخص، گوشی خاموش و روشن می‌شود",
            possibleCauses = "تداخل نرم‌افزاری، افت ولتاژ ناگهانی باتری، اتصالی جزئی روی برد",
            solutionSteps = "۱. بررسی زمان و شرایط وقوع ریستارت (هنگام اپ خاص، شارژ، دما)\n۲. تست در Safe Mode برای رد مشکل نرم‌افزاری\n۳. تست با باتری سالم دیگر\n۴. بررسی برد برای اتصالی در صورت تکرار در Safe Mode هم",
            difficulty = "متوسط"
        ),

        // ===== موارد تکمیلی دوربین =====
        TroubleshootingGuide(
            category = TroubleshootingCategory.CAMERA,
            brand = "همه برندها",
            title = "دوربین روی سوژه فوکوس نمی‌کند (مشکل AF/OIS)",
            symptoms = "تصاویر همیشه تار هستند و فوکوس خودکار کار نمی‌کند",
            possibleCauses = "خرابی موتور فوکوس خودکار، آسیب فیزیکی به ماژول دوربین، گردوغبار داخل لنز",
            solutionSteps = "۱. تست فوکوس دستی برای تفکیک مشکل نرم‌افزاری از سخت‌افزاری\n۲. پاک‌سازی سطح بیرونی لنز\n۳. تعویض ماژول دوربین در صورت خرابی موتور AF/OIS",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.CAMERA,
            brand = "همه برندها",
            title = "فلاش دوربین روشن نمی‌شود",
            symptoms = "در محیط تاریک، فلاش هنگام عکاسی یا به‌عنوان چراغ‌قوه کار نمی‌کند",
            possibleCauses = "خرابی LED فلاش، قطعی فلت مربوطه، خرابی IC درایور فلاش",
            solutionSteps = "۱. تست چراغ‌قوه و فلاش دوربین به‌صورت جداگانه\n۲. بررسی اتصال فلت فلش/دوربین\n۳. بررسی IC درایور فلاش روی برد",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.CAMERA,
            brand = "همه برندها",
            title = "لکه یا نقطه سیاه ثابت روی همه عکس‌ها دیده می‌شود",
            symptoms = "در تمام عکس‌های گرفته‌شده، یک لکه در همان موقعیت تکرار می‌شود",
            possibleCauses = "گردوغبار روی سنسور دوربین، آسیب فیزیکی جزئی به سنسور",
            solutionSteps = "۱. تمیزکاری سطح بیرونی و داخلی لنز در محیط تمیز\n۲. باز کردن ماژول دوربین برای بررسی گردوغبار روی سنسور (با احتیاط بالا)\n۳. تعویض ماژول در صورت آسیب سنسور",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.CAMERA,
            brand = "همه برندها",
            title = "ضبط ویدیو با خطا قطع می‌شود",
            symptoms = "بلافاصله بعد از شروع ضبط ویدیو، اپلیکیشن دوربین بسته یا خطا می‌دهد",
            possibleCauses = "فضای ذخیره‌سازی کم، کش اپ دوربین خراب، دمای بالای دستگاه",
            solutionSteps = "۱. بررسی فضای خالی حافظه\n۲. پاک‌کردن کش اپلیکیشن دوربین\n۳. اجازه دادن به خنک‌شدن گوشی قبل از تست مجدد",
            difficulty = "آسان"
        ),

        // ===== موارد تکمیلی صدا =====
        TroubleshootingGuide(
            category = TroubleshootingCategory.AUDIO,
            brand = "همه برندها",
            title = "صدای پخش‌شده از هندزفری بلوتوث قطع و وصل می‌شود",
            symptoms = "حین استفاده از هدفون بلوتوث، صدا بریده‌بریده پخش می‌شود",
            possibleCauses = "تداخل امواج، فاصله زیاد، خرابی جزئی آنتن بلوتوث، باتری ضعیف هندزفری",
            solutionSteps = "۱. تست با هندزفری بلوتوث دیگر\n۲. کاهش فاصله و حذف موانع/تداخل\n۳. ریست تنظیمات بلوتوث و Pair مجدد\n۴. بررسی آنتن بلوتوث/وای‌فای داخل گوشی در صورت تکرار با همه دستگاه‌ها",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.AUDIO,
            brand = "همه برندها",
            title = "صدای پخش‌شده از اسپیکر خش‌دار یا بریده است",
            symptoms = "صدای موسیقی یا زنگ گوشی واضح نیست و خش دارد",
            possibleCauses = "آسیب فیزیکی به دیافراگم اسپیکر، ورود رطوبت، اتصال شل فلت اسپیکر",
            solutionSteps = "۱. تست با فایل صوتی با کیفیت بالا برای رد مشکل فایل\n۲. بررسی و تمیزکاری توری اسپیکر\n۳. بررسی و در صورت نیاز تعویض اسپیکر",
            difficulty = "متوسط"
        ),

        // ===== سنسورها و دکمه‌ها تکمیلی =====
        TroubleshootingGuide(
            category = TroubleshootingCategory.SENSORS,
            brand = "همه برندها",
            title = "چرخش خودکار صفحه (Auto-Rotate) درست کار نمی‌کند",
            symptoms = "با چرخاندن گوشی، صفحه نمایش تغییر جهت نمی‌دهد یا با تاخیر زیاد انجام می‌شود",
            possibleCauses = "عدم کالیبراسیون سنسور ژیروسکوپ/شتاب‌سنج، خرابی نرم‌افزاری",
            solutionSteps = "۱. کالیبراسیون سنسورها از اپ‌های تشخیصی یا تنظیمات\n۲. تست در Safe Mode\n۳. در صورت خرابی فیزیکی سنسور، بررسی امکان تعویض قطعه مربوطه روی برد",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SENSORS,
            brand = "همه برندها",
            title = "موتور ویبره ضعیف شده یا کار نمی‌کند",
            symptoms = "لرزش گوشی هنگام تماس یا اعلان بسیار ضعیف است یا اصلاً حس نمی‌شود",
            possibleCauses = "فرسودگی موتور ویبره، قطعی فلت مربوطه، تنظیمات شدت ویبره",
            solutionSteps = "۱. بررسی تنظیمات شدت ویبره در منو\n۲. تست موتور ویبره از منوی تشخیصی سخت‌افزار\n۳. تعویض موتور ویبره در صورت خرابی فیزیکی",
            difficulty = "آسان"
        ),

        // ===== اثر انگشت و تشخیص چهره (دسته جدید) =====
        TroubleshootingGuide(
            category = TroubleshootingCategory.BIOMETRIC,
            brand = "همه برندها",
            title = "سنسور اثر انگشت، اثر انگشت را نمی‌شناسد",
            symptoms = "بعد از چند بار تلاش، گوشی اثر انگشت ثبت‌شده را رد می‌کند",
            possibleCauses = "کثیفی یا رطوبت روی سنسور، ثبت ناقص اثر انگشت، خرابی فیزیکی سنسور",
            solutionSteps = "۱. تمیزکاری کامل انگشت و سطح سنسور\n۲. حذف و ثبت مجدد اثر انگشت از تنظیمات\n۳. تست چند انگشت مختلف\n۴. بررسی فلت و ماژول سنسور در صورت خرابی فیزیکی",
            difficulty = "آسان"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.BIOMETRIC,
            brand = "همه برندها",
            title = "اثر انگشت زیر صفحه (In-Display) بعد از تعویض گلس کار نمی‌کند",
            symptoms = "بعد از تعمیر شکستگی گلس یا تعویض نمایشگر، سنسور اثر انگشت زیر صفحه پاسخ نمی‌دهد",
            possibleCauses = "استفاده از گلس غیراستاندارد که نور سنسور نوری را مسدود می‌کند، عدم کالیبراسیون بعد از تعمیر",
            solutionSteps = "۱. اطمینان از استفاده از گلس محافظ مخصوص سنسور اثر انگشت زیر صفحه\n۲. حذف و ثبت مجدد اثر انگشت بعد از تعویض نمایشگر\n۳. در صورت استفاده از نمایشگر غیراورجینال، احتمال ناسازگاری سخت‌افزاری را به مشتری اطلاع دهید",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.BIOMETRIC,
            brand = "همه برندها",
            title = "تشخیص چهره (Face Unlock) کار نمی‌کند یا کند است",
            symptoms = "گوشی با نگاه کردن به دوربین جلو باز نمی‌شود یا خیلی طول می‌کشد",
            possibleCauses = "کثیفی دوربین جلو، نور نامناسب محیط، خرابی سنسور مادون‌قرمز (در مدل‌های پیشرفته)",
            solutionSteps = "۱. تمیزکاری دوربین جلو و سنسورهای اطراف آن\n۲. تست در نور کافی\n۳. حذف و ثبت مجدد چهره از تنظیمات\n۴. بررسی سنسور IR/دوربین جلو در صورت خرابی سخت‌افزاری",
            difficulty = "متوسط"
        ),

        // ===== بلوتوث، جی‌پی‌اس و NFC (دسته جدید) =====
        TroubleshootingGuide(
            category = TroubleshootingCategory.CONNECTIVITY,
            brand = "همه برندها",
            title = "بلوتوث به هیچ دستگاهی متصل نمی‌شود",
            symptoms = "گوشی در جستجوی دستگاه بلوتوث گیر می‌کند یا اتصال برقرار نمی‌شود",
            possibleCauses = "خرابی نرم‌افزاری، تداخل با وای‌فای، خرابی آنتن ترکیبی وای‌فای/بلوتوث",
            solutionSteps = "۱. خاموش و روشن کردن بلوتوث و ریستارت گوشی\n۲. فراموش‌کردن (Forget) دستگاه‌های قبلی و Pair مجدد\n۳. ریست تنظیمات شبکه\n۴. بررسی آنتن/ماژول وای‌فای-بلوتوث روی برد در صورت تکرار با همه دستگاه‌ها",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.CONNECTIVITY,
            brand = "همه برندها",
            title = "جی‌پی‌اس موقعیت نادرست یا با تاخیر زیاد نشان می‌دهد",
            symptoms = "در اپ‌های نقشه، موقعیت مکانی گوشی اشتباه یا خیلی دیر بروزرسانی می‌شود",
            possibleCauses = "ضعف سیگنال در فضای بسته، تنظیمات نادرست مکان، خرابی آنتن GPS",
            solutionSteps = "۱. تست در فضای باز با دید آسمان\n۲. بررسی فعال بودن حالت مکان با دقت بالا (High Accuracy)\n۳. کالیبراسیون GPS با اپ تشخیصی\n۴. بررسی آنتن GPS در صورت تکرار مشکل در فضای باز هم",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.CONNECTIVITY,
            brand = "همه برندها",
            title = "NFC کار نمی‌کند (پرداخت یا اشتراک‌گذاری فایل)",
            symptoms = "گوشی در نزدیکی پایانه‌های پرداخت یا دستگاه‌های NFC دیگر واکنشی نشان نمی‌دهد",
            possibleCauses = "غیرفعال بودن NFC در تنظیمات، پوشش/قاب ضخیم، خرابی آنتن NFC (معمولاً روی بک‌کاور)",
            solutionSteps = "۱. بررسی فعال بودن NFC از تنظیمات سریع\n۲. تست بدون قاب گوشی\n۳. بررسی آنتن NFC که معمولاً روی بک‌گلس یا بک‌کاور نصب شده",
            difficulty = "متوسط"
        ),

        // ===== موارد تکمیلی آب‌خوردگی =====
        TroubleshootingGuide(
            category = TroubleshootingCategory.WATER_DAMAGE,
            brand = "همه برندها",
            title = "بعد از آب‌خوردگی، صدای اسپیکر خفه و کدر شده است",
            symptoms = "صدای پخش‌شده ضعیف و کدر است، انگار پارچه‌ای روی اسپیکر باشد",
            possibleCauses = "باقی‌ماندن رطوبت یا رسوب آب پشت توری اسپیکر",
            solutionSteps = "۱. خشک‌کردن کامل با هوای فشرده از پشت اسپیکر\n۲. استفاده از اپ‌های تخصصی پاک‌سازی اسپیکر با صدای فرکانس بالا\n۳. تعویض اسپیکر در صورت باقی‌ماندن رسوب دائمی",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.WATER_DAMAGE,
            brand = "همه برندها",
            title = "لنز دوربین بعد از آب‌خوردگی مه‌آلود شده است",
            symptoms = "تصاویر دوربین تار و بخارگرفته دیده می‌شوند",
            possibleCauses = "نفوذ رطوبت به داخل ماژول دوربین",
            solutionSteps = "۱. باز کردن ماژول دوربین در محیط تمیز و خشک‌کردن کامل\n۲. در صورت باقی‌ماندن بخار داخلی، تعویض ماژول دوربین ضروری است",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.WATER_DAMAGE,
            brand = "همه برندها",
            title = "پورت شارژ بعد از آب‌خوردگی خوردگی زده و اکسید شده",
            symptoms = "با گذشت چند روز از آب‌خوردگی، شارژ بی‌ثبات یا قطع شده است",
            possibleCauses = "واکنش اکسیداسیون فلزات پورت در اثر تماس با آب و هوا",
            solutionSteps = "۱. بررسی چشمی پورت زیر لوپ برای رسوب سبز/سفید (نشانه خوردگی)\n۲. تمیزکاری با الکل ایزوپروپیل و برس فایبرگلاس\n۳. تعویض پورت در صورت خوردگی شدید پین‌ها",
            difficulty = "متوسط"
        ),

        // ===== موارد اختصاصی برند =====
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "Samsung",
            title = "گوشی سامسونگ در حالت Download Mode گیر کرده",
            symptoms = "با روشن کردن گوشی، فقط صفحه‌ی زرد رنگ Download Mode نمایش داده می‌شود",
            possibleCauses = "فلش ناقص یا اشتباه با Odin، خرابی پارتیشن بوت",
            solutionSteps = "۱. اتصال به کامپیوتر و فلش مجدد فرم‌ور رسمی با نرم‌افزار Odin\n۲. استفاده از فایل فرم‌ور دقیقاً متناسب با مدل و منطقه دستگاه\n۳. در صورت تکرار، بررسی سلامت حافظه eMMC/UFS",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SENSORS,
            brand = "Samsung",
            title = "قلم S-Pen شناسایی نمی‌شود (مدل‌های Note و S Ultra)",
            symptoms = "با نزدیک‌کردن یا فشار دکمه S-Pen، گوشی واکنشی نشان نمی‌دهد",
            possibleCauses = "باتری S-Pen خالی (در مدل‌های دارای بلوتوث)، خرابی آنتن دریافت داخل گوشی، آسیب فیزیکی به خود قلم",
            solutionSteps = "۱. شارژ کامل S-Pen با قرار دادن داخل گوشی به مدت کافی\n۲. تست با S-Pen اورجینال دیگر\n۳. بررسی آنتن/کویل دریافت S-Pen داخل گوشی در صورت تکرار با چند قلم",
            difficulty = "متوسط"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.NETWORK_SIM,
            brand = "Apple",
            title = "آیفون بعد از تعمیر آنتن پیام «بدون سرویس» می‌دهد",
            symptoms = "بعد از تعویض قاب یا آنتن، آیفون آنتن‌دهی ندارد",
            possibleCauses = "اتصال نادرست کانکتور آنتن، پیچ‌های اتصال بدنه به آنتن سفت نشده",
            solutionSteps = "۱. بررسی دقیق اتصال کانکتورهای آنتن به برد اصلی\n۲. اطمینان از سفت‌بودن پیچ‌های فریم که اتصال زمین آنتن را برقرار می‌کنند\n۳. تست با سیم‌کارت در منطقه با آنتن‌دهی مناسب",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.CAMERA,
            brand = "Apple",
            title = "پیام «دوربین اصلی غیر اورجینال است» بعد از تعویض دوربین آیفون",
            symptoms = "بعد از تعویض ماژول دوربین، هشدار زرد رنگ Non-Genuine Part نمایش داده می‌شود",
            possibleCauses = "عدم انجام برنامه‌ریزی نرم‌افزاری (Programming/Pairing) قطعه با ابزار تخصصی بعد از تعویض",
            solutionSteps = "۱. استفاده از قطعه دوربین با کیفیت اورجینال یا اورجینال-تعمیراتی\n۲. انجام Programming قطعه با نرم‌افزار/باکس تخصصی متناسب با مدل آیفون\n۳. اطلاع‌رسانی شفاف به مشتری درباره محدودیت‌های قطعات غیر اورجینال اپل",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.BIOMETRIC,
            brand = "Apple",
            title = "Face ID بعد از تعویض نمایشگر آیفون کار نمی‌کند",
            symptoms = "بعد از تعویض گلس یا نمایشگر آیفون، پیام خطای Face ID نمایش داده می‌شود",
            possibleCauses = "عدم انتقال یا آسیب به سنسورهای TrueDepth حین تعویض نمایشگر",
            solutionSteps = "۱. اطمینان از انتقال صحیح براکت و سنسورهای دوربین جلو از نمایشگر قدیم (در مدل‌های نیازمند این کار)\n۲. بررسی آسیب فیزیکی به فلت یا دوربین TrueDepth حین تعمیر\n۳. در صورت آسیب دائمی، توضیح به مشتری که تعمیر Face ID نیازمند تخصص و ابزار میکروسولدرینگ است",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "Xiaomi",
            title = "گوشی شیائومی روی MI Logo گیر کرده (Fastboot Loop)",
            symptoms = "گوشی مدام لوگوی MI را نشان می‌دهد یا وارد حالت Fastboot می‌شود",
            possibleCauses = "خرابی نرم‌افزاری بعد از آپدیت ناقص، خرابی پارتیشن بوت",
            solutionSteps = "۱. ورود به Recovery Mode و پاک کردن Cache\n۲. فلش مجدد فرم‌ور رسمی با Mi Flash Tool\n۳. در صورت قفل Bootloader، بررسی امکان باز کردن قفل از حساب Mi Account مشتری",
            difficulty = "تخصصی"
        ),
        TroubleshootingGuide(
            category = TroubleshootingCategory.SOFTWARE,
            brand = "Huawei/Honor",
            title = "بعد از حذف گوگل (GMS)، اپلیکیشن‌های گوگل نصب یا اجرا نمی‌شوند",
            symptoms = "Play Store یا سرویس‌های گوگل روی مدل‌های بدون GMS نصب یا اجرا نمی‌شوند",
            possibleCauses = "عدم پشتیبانی رسمی گوگل روی برخی مدل‌های جدید هوآوی/آنر",
            solutionSteps = "۱. اطلاع‌رسانی شفاف به مشتری درباره محدودیت مدل خریداری‌شده\n۲. بررسی و نصب گزینه‌های جایگزین مثل AppGallery یا روش‌های نصب دستی معتبر\n۳. توضیح ریسک‌های امنیتی روش‌های نصب غیررسمی",
            difficulty = "متوسط"
        )
    )
}
