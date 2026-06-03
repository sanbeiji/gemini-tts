package com.example.geminittsdialects.models

import kotlinx.serialization.Serializable

@Serializable
data class Dialect(
    val id: String,
    val name: String,
    val prompt: String
)

@Serializable
data class Language(
    val id: String,
    val name: String,
    val defaultText: String,
    val dialects: List<Dialect>
)

object DialectData {
    val languages = listOf(
        Language(
            id = "en",
            name = "English",
            defaultText = "Hello there, my friend! We have a wonderful day ahead of us, so let's make the absolute most of it.",
            dialects = listOf(
                Dialect(
                    id = "en_london",
                    name = "London",
                    prompt = "[Voice Style: Speak in a natural London accent (Estuary/Modern Received Pronunciation).] Deliver with natural British English phrasing, glottal stops for 't' in the middle/end of words, slightly rounded vowels, and typical London cadence."
                ),
                Dialect(
                    id = "en_scotland",
                    name = "Inverness",
                    prompt = "[Voice Style: Speak in a soft, melodic Scottish accent characteristic of Inverness and the Highlands.] Deliver with clear, rolled or tapped 'r's, monophthongal vowels (e.g. 'go' sounds like a pure long o), and a gentle, slightly rising lilt at the ends of sentences."
                ),
                Dialect(
                    id = "en_ireland",
                    name = "Galway",
                    prompt = "[Voice Style: Speak in a soft, musical West Irish accent from Galway.] Deliver with a rhythmic lilt, softened consonants (like 't' sounding close to 'sh' or 'ts'), and distinct Irish English vowel shifts (e.g., 'film' with a slight extra vowel 'fill-um')."
                ),
                Dialect(
                    id = "en_bostonian",
                    name = "Bostonian",
                    prompt = "[Voice Style: Speak in a classic working-class Boston accent.] Drop the 'r' sound at the end of words or before consonants (non-rhoticity, e.g. 'car' sounds like 'cah', 'park' like 'pahk'), and open up vowels like 'o' (e.g., 'Boston' sounds like 'Bahston')."
                ),
                Dialect(
                    id = "en_nyc",
                    name = "NYC",
                    prompt = "[Voice Style: Speak in a theatrical, classic New York City / Brooklyn working-class 'mobster' voice.] Speak with a tough, raspy, animated Brooklyn accent, dropping 'r's, shifting 'th' to 'd' or 't' (e.g., 'these' -> 'dese', 'thing' -> 'ting'), and using dramatic NY stress and cadence."
                ),
                Dialect(
                    id = "en_texas",
                    name = "Texas",
                    prompt = "[Voice Style: Speak in a friendly, slow-paced Southern Texas drawl.] Monophthongize vowels (e.g., 'time' sounds like 'tahm'), draw out vowels with a distinct lazy lilt (the Southern drawl), and speak with a relaxed, warm, hospitable pace."
                ),
                Dialect(
                    id = "en_surfer",
                    name = "California",
                    prompt = "[Voice Style: Speak in a highly casual, laid-back California 'surfer dude' voice.] Deliver with a slow, relaxed, drawling cadence, slightly elongated vowels, and enthusiastic, breezy, informal intonation."
                ),
                Dialect(
                    id = "en_australia",
                    name = "Adelaide",
                    prompt = "[Voice Style: Speak in a standard South Australian Adelaide accent.] Deliver with slightly more rounded, prestigious British-leaning vowels than eastern Australia, a subtle rising inflection at the end of sentences, and casual Australian cadence."
                ),
                Dialect(
                    id = "en_taiwanese",
                    name = "Taiwanese",
                    prompt = "[Voice Style: Speak English with a distinct, natural Taiwanese regional accent.] Deliver with slightly shortened vowels, gentle shifts in consonants (e.g., 'th' sounds like 'd' or 's', 'l' and 'r' are relaxed, and word endings are soft), with a polite, melodic Taiwanese rhythm."
                ),
                Dialect(
                    id = "en_japanese",
                    name = "Japanese",
                    prompt = "[Voice Style: Speak English with a distinct, natural Japanese accent.] Render English vowels and consonants shifted toward Japanese phonology (e.g. slight epenthetic vowels at the ends of words, simplified consonant clusters, and a flat pitch-accent influenced rhythm), spoken politely and clearly."
                ),
                Dialect(
                    id = "en_shakespearean",
                    name = "Shakespearean",
                    prompt = "[Voice Style: Speak in the style of an actor performing in a dramatic Shakespearean play. The speech should be highly emphatic, theatrical, and slightly exaggerated, delivered with a strong, projecting stage voice, rich Received Pronunciation (RP) vowels, and a dramatic, grand cadence.]"
                )
            )
        ),
        Language(
            id = "es",
            name = "Spanish",
            defaultText = "¡Hola, mi amigo! Tenemos un día maravilloso por delante, así que vamos a aprovecharlo al máximo.",
            dialects = listOf(
                Dialect(
                    id = "es_madrid",
                    name = "Madrid",
                    prompt = "[Voice Style: Speak in a clear, standard Castilian Spanish accent from Madrid.] Pronounce 'z' and 'c' (before e/i) with a clear interdental 'th' sound (distinción, ceceo), drop syllable-final 's' very subtly, and use standard central Spanish rhythm and intonation."
                ),
                Dialect(
                    id = "es_barcelona",
                    name = "Barcelona",
                    prompt = "[Voice Style: Speak Spanish with a subtle Catalan accent as heard in Barcelona.] Deliver with slightly open vowels, a mildly dark 'l' (velarized), and the characteristic intonation and cadence of bilingual Catalan-Spanish speakers."
                ),
                Dialect(
                    id = "es_mexico",
                    name = "Mexico",
                    prompt = "[Voice Style: Speak in a warm, polite, standard Mexican Spanish accent.] Speak with clear seseo (pronouncing 's', 'c', and 'z' as 's'), distinct melodic rising intonation at the end of phrases (el cantadito mexicano), and clear, crisp consonant articulation."
                )
            )
        ),
        Language(
            id = "de",
            name = "German",
            defaultText = "Hallo, mein Freund! Wir haben einen wunderbaren Tag vor uns, also lasst uns das Beste daraus machen.",
            dialects = listOf(
                Dialect(
                    id = "de_berlin",
                    name = "Berlin",
                    prompt = "[Voice Style: Speak German with a casual Berlin regional accent (Berlinerisch).] Deliver with relaxed consonants, shifting 'g' to 'j' (e.g. 'gut' sounds like 'jut'), softening 'ich' to 'ick', and using a friendly, direct, urban Berlin lilt."
                ),
                Dialect(
                    id = "de_vienna",
                    name = "Vienna",
                    prompt = "[Voice Style: Speak German with a charming, melodious Viennese accent (Wienerisch).] Deliver with slightly elongated, open vowels, soft consonants, a relaxed tempo, and the distinctive musical cadence of Vienna, Austria."
                )
            )
        ),
        Language(
            id = "fr",
            name = "French",
            defaultText = "Bonjour mon ami ! Nous avons une journée magnifique devant nous, alors profitons-en au maximum.",
            dialects = listOf(
                Dialect(
                    id = "fr_paris",
                    name = "Paris",
                    prompt = "[Voice Style: Speak in standard, modern Parisian French.] Deliver with crisp, clean pronunciation, uvular 'r's, standard nasal vowels, and a flat, elegant metropolitan French cadence."
                ),
                Dialect(
                    id = "fr_quebec",
                    name = "Quebec",
                    prompt = "[Voice Style: Speak in a warm, authentic Quebec French (Québécois) accent.] Deliver with affrication of 't' and 'd' before high front vowels (e.g. 'tu' sounds like 'tsu', 'dire' like 'dzire'), relaxed vowels in closed syllables, and the distinct rhythmic cadence of French Canada."
                )
            )
        ),
        Language(
            id = "zh",
            name = "Chinese",
            defaultText = "哈囉，我的朋友！今天的天氣真好，我們一起出去散散步吧。",
            dialects = listOf(
                Dialect(
                    id = "zh_beijing",
                    name = "Beijing",
                    prompt = "[Voice Style: Speak in standard Mandarin with a classic Beijing accent (北京話).] Deliver with frequent curl-tongue erhua (兒化音) endings, relaxed and blended consonants, and a fast, confident, northern Chinese cadence."
                ),
                Dialect(
                    id = "zh_shanghai",
                    name = "Shanghai",
                    prompt = "[Voice Style: Speak Mandarin with a Shanghainese accent.] Deliver with a softer, slightly voiced consonant quality, vowels shifted toward Wu language phonology, and the distinctive rise-fall intonation of Shanghai speakers."
                ),
                Dialect(
                    id = "zh_cantonese",
                    name = "Cantonese",
                    prompt = "[Voice Style: Read the text in Cantonese (廣東話/粵語).] Pronounce the characters using standard Cantonese readings (with 6 or 9 tones), distinct final consonants (-p, -t, -k, -m, -n, -ng), and authentic Hong Kong/Guangzhou Cantonese speech flow."
                ),
                Dialect(
                    id = "zh_taiwan_standard",
                    name = "Taipei",
                    prompt = "[Voice Style: Speak in a natural, standard, clear reading style.]\nRead in standard, clear Taiwanese Mandarin (都會風格台北/台灣腔) as heard in public announcements (like the Taipei MRT) or urban professional settings.\n- Speak in a natural, clean, moderately fast, modern Taiwanese tempo.\n- Retroflex sounds (zh, ch, sh) are relaxed and naturally simplified, avoiding any dry retroflex friction or thick northern Beijing acoustics. No \"er\" (no 兒化音).\n- Render neutral tones (輕聲) in accordance with general urban Taiwanese Mandarin usage (typically pronounced as lighter full tones rather than clipped neutral vowels).\n- Deliver with a clean, melodic, polite, and professional Taiwanese tone."
                ),
                Dialect(
                    id = "zh_taiwan_southern",
                    name = "Kaohsiung",
                    prompt = "[Voice Style: Speak gently, softly, and reassuringly, at a relaxed pace with extreme warmth.]\nRead in a warm, relaxed, authentic Southern Taiwanese Mandarin (台灣國語) regional accent (popular in Tainan, Kaohsiung, and Pingtung).\n- Speak with a friendly, local Taiwanese cadence and relaxed mouth positioning.\n- Strictly avoid Beijing-style speech: absolutely no curl-tongue \"er\" (no 兒化音) and do not retroflex sounds like zh, ch, sh (pronounce them shifted toward z, c, s, e.g. 知道 sounds like zīdào, 是 sounds like sì).\n- Do not suppress tones into neutral short tones (輕聲), pronounce grammatically light words with their full traditional Taiwanese Mandarin tones (e.g. 舒服 is shūfú, 先生 is xiānshēng).\n- Keep any natural sentence-final particles from Taiwan (like '啦', '齁', '喔', '欸') represented with authentic, comfortable, musical southern cadence."
                ),
                Dialect(
                    id = "zh_taiwan_heavy_southern",
                    name = "Kaohsiung + Hoklo",
                    prompt = "[Voice Style: Speak extremely casually, off-the-cuff, like chatting with a close family member or childhood friend.]\nRead in a local, very down-to-earth, thick Southern Taiwanese Mandarin colloquial style (重度南部腔台灣國語) with strong Taiwanese (Minnan/Hokkien) substrate.\n- Sound like a friendly neighbor from Tainan or Kaohsiung speaking casual Mandarin.\n- Strongly dentalize retroflexes (zh, ch, sh -> z, c, s, e.g., 船 sounds like cuán, 睡 sounds like suì).\n- Use Minnan speech substrate pitch and rhythm. Syllable-final nasals \"eng\" and \"en\" can merge with \"ing\" and \"in\", or open slightly (e.g. 朋友 sounds like píngyǒu or péng-ǐou).\n- Do not use neutral/light tones (輕聲); give everything comfortable, rich Taiwanese tones.\n- If natural, blend f and h sounds gently (e.g., 飯 fàn sounds like huàn, 發生 fāshēng sounds like huāshēng).\n- Keep the cadence relaxed, friendly, and expressive, showing regional southern warmth."
                )
            )
        )
    )
}
