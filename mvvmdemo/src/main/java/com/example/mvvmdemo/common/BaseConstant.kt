package com.example.mvvmdemo.common

object BaseConstant {

    /*
    // 有getter
    val A = "A";
    // 可以不通过companion调用,有getter
    @JvmStatic
    val C = "C";
    // 只能用在object/companion/顶层函数,直接访问,没有getter
    const val B = "B";
    // 无使用限制,直接暴露,没有getter
    @JvmField
    val D = "D";
    */

    const val USER_NAME:String = "TeaOf"
    const val USER_PWD:String = "123456"

    // 数据库名字
    const val TABLE_PREFS = "jetpack"
    const val IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH"
    const val SP_USER_ID = "SP_USER_ID"
    const val SP_USER_NAME = "SP_USER_NAME"

    // 传递的参数
    const val ARGS_NAME = "ARGS_NAME"
    const val ARGS_EMAIL = "ARGS_EMAIL"

    // 单个页面大小
    const val SINGLE_PAGE_SIZE = 10

    // MeModel
    const val KEY_IMAGE_URI = "KEY_IMAGE_URI"


    // DetailActivity 传输的数据
    const val DETAIL_SHOE_ID = "DETAIL_SHOE_ID"

    // Name of Notification Channel for verbose notifications of background work
    @JvmField val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
        "Verbose WorkManager Notifications"
    const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
        "Shows notifications whenever work starts"
    @JvmField val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
    const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
    const val NOTIFICATION_ID = 1

    // The name of the image manipulation work
    const val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"

    // Other keys
    const val OUTPUT_PATH = "blur_filter_outputs"
    const val TAG_OUTPUT = "OUTPUT"

    const val DELAY_TIME_MILLIS: Long = 3000
}



