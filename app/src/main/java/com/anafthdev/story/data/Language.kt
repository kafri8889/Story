package com.anafthdev.story.data

enum class Language(val code: String) {
    English("en"),
    Indonesian("in"),

    /**
     * Jika language di datastore undefined, berarti saat user membuka aplikasi,
     * aplikasi akan melihat bahasa yang digunakan oleh user saat ini (primary language) dan
     * mengubah value dari language (di datastore) menjadi primary language user. Jika tidak ada,
     * maka bahasa inggris yang akan digunakan.
     */
    Undefined("");

    companion object {
        /**
         * Cek apakah bahasa dari [code] yang diberikan didukung oleh app
         */
        fun contains(code: String): Boolean {
            return entries.any { it.code == code }
        }

        fun fromCode(code: String): Language? {
            return entries.firstOrNull { it.code == code }
        }
    }
}