package com.joshgm3z.triplerocktv.core.repository.retrofit

import android.util.Xml

import com.joshgm3z.triplerocktv.core.repository.data.XmlTvProgram
import com.joshgm3z.triplerocktv.core.repository.impl.parseXmlTvDate
import com.joshgm3z.triplerocktv.core.util.Logger
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

class XmlTvParser {
    companion object {
        fun parse(inputStream: InputStream): List<XmlTvProgram> {
            Logger.debug("Starting XML parsing...")
            val programs = mutableListOf<XmlTvProgram>()
            val parser = Xml.newPullParser()

            try {
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(inputStream, null)

                var eventType = parser.eventType
                var currentProgram: XmlTvProgram? = null
                var start: String? = null
                var stop: String? = null
                var id: String? = null
                var title: String? = null
                var desc: String? = null
                var icon: String? = null


                while (eventType != XmlPullParser.END_DOCUMENT) {
                    val tagName = parser.name

                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            when (tagName) {
                                "programme" -> {
                                    val channelId = parser.getAttributeValue(null, "channel")
                                        ?: parser.getAttributeValue(null, "id")

                                    // FIX: If there's no ID, we don't initialize currentProgram.
                                    // This causes the subsequent START_TAGS (title, desc)
                                    // to be ignored via the null-safe check (currentProgram?.title)
                                    if (!channelId.isNullOrEmpty()) {
                                        id = channelId
                                        start = parser.getAttributeValue(null, "start")
                                        stop = parser.getAttributeValue(null, "stop")
                                    } else {
                                        start = null
                                        stop = null
                                        id = null
                                        title = null
                                        desc = null
                                        icon = null
                                    }
                                }

                                "title" -> title = parser.nextText()
                                "desc" -> desc = parser.nextText()
                                "icon" -> icon = parser.getAttributeValue(null, "src")
                            }
                        }

                        XmlPullParser.END_TAG -> {
                            if (tagName == "programme") {
                                // Final validation: Ensure we have at least an ID and a Title
                                if (!start.isNullOrEmpty() && !title.isNullOrEmpty() && !stop.isNullOrEmpty() && !id.isNullOrEmpty()) {
                                    programs.add(
                                        XmlTvProgram(
                                            start = start.parseXmlTvDate(),
                                            stop = stop.parseXmlTvDate(),
                                            id = id,
                                            title = title,
                                            description = desc,
                                            icon = icon
                                        )
                                    )
                                }
                                start = null
                                stop = null
                                id = null
                                title = null
                                desc = null
                                icon = null
                            }
                        }
                    }
                    eventType = parser.next()
                }
            } catch (e: Exception) {
                Logger.error("Error parsing XMLTV: ${e.message}")
            } finally {
                inputStream.close()
            }

            Logger.debug("Parsed ${programs.size} programs")
            return programs
        }
    }
}