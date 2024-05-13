package com.anafthdev.story.data.serializer

import androidx.datastore.core.Serializer
import com.anafthdev.story.ProtoUserPreference
import com.anafthdev.story.data.Language
import java.io.InputStream
import java.io.OutputStream

object UserPreferenceSerializer: Serializer<ProtoUserPreference> {

    override val defaultValue: ProtoUserPreference
        get() = ProtoUserPreference(
            language = Language.Undefined.code
        )

    override suspend fun readFrom(input: InputStream): ProtoUserPreference {
        return ProtoUserPreference.ADAPTER.decode(input)
    }

    override suspend fun writeTo(t: ProtoUserPreference, output: OutputStream) {
        ProtoUserPreference.ADAPTER.encode(output, t)
    }

}