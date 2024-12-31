package com.example.remote_control.data

object DataProvider {

    // Detailed data for Aasee and Prinzipalmarkt
    val locationData: Map<String, Map<String, YearData>> = mapOf(
        "Aasee" to mapOf(
            "low" to YearData(
                temperatures = mapOf(
                    2020 to YearDetail(temp = 23.4, overlays = Overlays(pictures = listOf(1779), websites = listOf(1784))),
                    2040 to YearDetail(temp = 1.8, overlays = Overlays(pictures = listOf(1800), websites = listOf())),
                    2060 to YearDetail(temp = 0.7, overlays = Overlays(pictures = listOf(1789), websites = listOf())),
                    2080 to YearDetail(temp = 0.5, overlays = Overlays(pictures = listOf(1804), websites = listOf())),
                    2100 to YearDetail(temp = 0.5, overlays = Overlays(pictures = listOf(1797), websites = listOf()))
                )
            ),
            "high" to YearData(
                temperatures = mapOf(
                    2020 to YearDetail(temp = 23.4, overlays = Overlays(pictures = listOf(1804), websites = listOf(1784))),
                    2040 to YearDetail(temp = 1.9, overlays = Overlays(pictures = listOf(1779), websites = listOf())),
                    2060 to YearDetail(temp = 1.1, overlays = Overlays(pictures = listOf(1797), websites = listOf())),
                    2080 to YearDetail(temp = 1.4, overlays = Overlays(pictures = listOf(1789), websites = listOf())),
                    2100 to YearDetail(temp = 1.7, overlays = Overlays(pictures = listOf(1800), websites = listOf()))
                )
            )
        ),
        "Prinzipalmarkt" to mapOf(
            "low" to YearData(
                temperatures = mapOf(
                    2020 to YearDetail(temp = 23.4, overlays = Overlays(pictures = listOf(), websites = listOf(1784))),
                    2040 to YearDetail(temp = 1.8, overlays = Overlays(pictures = listOf(), websites = listOf())),
                    2060 to YearDetail(temp = 0.7, overlays = Overlays(pictures = listOf(), websites = listOf())),
                    2080 to YearDetail(temp = 0.5, overlays = Overlays(pictures = listOf(), websites = listOf())),
                    2100 to YearDetail(temp = 0.5, overlays = Overlays(pictures = listOf(), websites = listOf()))
                )
            ),
            "high" to YearData(
                temperatures = mapOf(
                    2020 to YearDetail(temp = 23.4, overlays = Overlays(pictures = listOf(), websites = listOf(1784))),
                    2040 to YearDetail(temp = 1.9, overlays = Overlays(pictures = listOf(), websites = listOf())),
                    2060 to YearDetail(temp = 1.1, overlays = Overlays(pictures = listOf(), websites = listOf())),
                    2080 to YearDetail(temp = 1.4, overlays = Overlays(pictures = listOf(), websites = listOf())),
                    2100 to YearDetail(temp = 1.3, overlays = Overlays(pictures = listOf(), websites = listOf()))
                )
            )
        )
    )

    // Location-specific measures
    val locationSpecificMeasures: Map<String, List<Measure>> = mapOf(
        "Aasee" to listOf(
            Measure(id = 1815, name = "Trinkbrunnen", startYear = 2060, tempChange = 0.0)
        ),
        "Prinzipalmarkt" to listOf(
            Measure(id = 1819, name = "Gruenstreifen", startYear = 2060, tempChange = 2.0),
        )
    )
}

// Helper data classes
data class YearData(
    val temperatures: Map<Int, YearDetail>
)

data class YearDetail(
    val temp: Double,
    val overlays: Overlays
)

data class Overlays(
    val pictures: List<Int>,
    val websites: List<Int>
)

data class Measure(
    val id: Int,
    val name: String,
    val startYear: Int,
    val tempChange: Double
)

