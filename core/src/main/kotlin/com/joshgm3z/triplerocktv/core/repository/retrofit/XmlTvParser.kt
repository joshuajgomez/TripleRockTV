package com.joshgm3z.triplerocktv.core.repository.retrofit

import android.util.Xml

import com.joshgm3z.triplerocktv.core.repository.data.XmlTvProgram
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
                                        currentProgram = XmlTvProgram().apply {
                                            id = channelId
                                            start = parser.getAttributeValue(null, "start")
                                            stop = parser.getAttributeValue(null, "stop")
                                        }
                                    } else {
                                        currentProgram = null
                                    }
                                }

                                "title" -> currentProgram?.title = parser.nextText()
                                "desc" -> currentProgram?.description = parser.nextText()
                                "icon" -> currentProgram?.icon =
                                    parser.getAttributeValue(null, "src")
                            }
                        }

                        XmlPullParser.END_TAG -> {
                            if (tagName == "programme" && currentProgram != null) {
                                // Final validation: Ensure we have at least an ID and a Title
                                if (!currentProgram.id.isNullOrEmpty() && !currentProgram.title.isNullOrEmpty()) {
                                    programs.add(currentProgram)
                                }
                                currentProgram = null
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