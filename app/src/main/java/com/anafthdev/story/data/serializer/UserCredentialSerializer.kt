package com.anafthdev.story.data.serializer

import androidx.datastore.core.Serializer
import com.anafthdev.story.ProtoUserCredential
import java.io.InputStream
import java.io.OutputStream

object UserCredentialSerializer: Serializer<ProtoUserCredential> {

    override val defaultValue: ProtoUserCredential
        get() = ProtoUserCredential()

    override suspend fun readFrom(input: InputStream): ProtoUserCredential {
        return ProtoUserCredential.ADAPTER.decode(input)
    }

    override suspend fun writeTo(t: ProtoUserCredential, output: OutputStream) {
        ProtoUserCredential.ADAPTER.encode(output, t)
    }
}