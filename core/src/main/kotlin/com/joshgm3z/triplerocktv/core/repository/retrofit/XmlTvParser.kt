package com.joshgm3z.triplerocktv.core.repository.retrofit

import android.util.Xml
import com.joshgm3z.triplerocktv.core.repository.data.XmlTvProgram
import com.joshgm3z.triplerocktv.core.util.Logger
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

class XmlTvParser {
    companion object {
        fun parse(inputStream: InputStream): List<XmlTvProgram> {
            Logger.debug("inputStream = [${inputStream}]")
            val programs = mutableListOf<XmlTvProgram>()
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            var currentProgram: XmlTvProgram? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name
                Logger.debug("tagName = [$tagName]")
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (tagName == "programme") {
                            val start = parser.getAttributeValue(null, "start")
                            val stop = parser.getAttributeValue(null, "stop")
                            val channel = parser.getAttributeValue(null, "channel")
                            // Temporary storage or create partial object
                        } else if (tagName == "title") {
                            val title = parser.nextText()
                            // capture title...
                        }
                    }
                    // Continue logic to build the list...
                }
                eventType = parser.next()
            }
            return programs
        }
    }
}