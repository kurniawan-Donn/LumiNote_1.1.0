package com.example.LumiNote

import android.graphics.Color
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class StatistikActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var tvJumlahCatatan: TextView
    private lateinit var tvJumlahTugas: TextView
    private lateinit var tvJumlahSelesai: TextView
    private lateinit var tvJumlahTertunda: TextView
    private lateinit var progressCircle: ProgressBar
    private lateinit var tvProgress: TextView
    private lateinit var tvProgressText: TextView
    private lateinit var tvQuote1: TextView
    private lateinit var tvQuote2: TextView
    private lateinit var backButton: ImageView

    private lateinit var catatanPrefs: CatatanPreferences
    private lateinit var tugasPrefs: TugasPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistik)

        // Inisialisasi Preferences
        catatanPrefs = CatatanPreferences(this)
        tugasPrefs = TugasPreferences(this)

        // Inisialisasi Views
        initViews()

        // Setup Back Button
        backButton.setOnClickListener {
            finish()
        }

        // Load dan tampilkan semua statistik
        loadStatistik()
    }

    private fun initViews() {
        barChart = findViewById(R.id.barChart)
        tvJumlahCatatan = findViewById(R.id.tvJumlahCatatan)
        tvJumlahTugas = findViewById(R.id.tvJumlahTugas)
        tvJumlahSelesai = findViewById(R.id.tvJumlahSelesai)
        tvJumlahTertunda = findViewById(R.id.tvJumlahTertunda)
        progressCircle = findViewById(R.id.progressCircle)
        tvProgress = findViewById(R.id.tvProgress)
        tvProgressText = findViewById(R.id.tvProgressText)
        tvQuote1 = findViewById(R.id.tvQuote1)
        tvQuote2 = findViewById(R.id.tvQuote2)
        backButton = findViewById(R.id.backButton)
    }

    private fun loadStatistik() {
        // Ambil data dari preferences
        val allCatatan = catatanPrefs.getCatatanList()
        val allTugas = tugasPrefs.getAllTugas()

        // Hitung statistik
        val jumlahCatatan = allCatatan.size
        val jumlahTugas = allTugas.size
        val jumlahSelesai = allTugas.count { it.isSelesai }
        val jumlahTertunda = allTugas.count { !it.isSelesai }

        // Update UI Cards dengan animasi
        animateTextView(tvJumlahCatatan, jumlahCatatan)
        animateTextView(tvJumlahTugas, jumlahTugas)
        animateTextView(tvJumlahSelesai, jumlahSelesai)
        animateTextView(tvJumlahTertunda, jumlahTertunda)

        // Setup Bar Chart
        setupBarChart(allCatatan, allTugas)

        // Hitung dan tampilkan progress
        val progress = calculateProgress(jumlahSelesai, jumlahTugas)
        updateProgressCircle(progress)

        // Generate motivasi quotes
        generateMotivationQuotes(allCatatan, allTugas, progress)
    }

    private fun setupBarChart(catatanList: List<Catatan>, tugasList: List<Tugas>) {
        // Data untuk 7 hari terakhir (Senin - Minggu)
        val calendar = Calendar.getInstance()
        val dayLabels = mutableListOf<String>()
        val createdData = mutableListOf<BarEntry>()
        val completedData = mutableListOf<BarEntry>()

        // Set ke hari Senin minggu ini
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfWeek = calendar.timeInMillis

        // Hitung data untuk setiap hari
        for (i in 0..6) {
            val dayStart = startOfWeek + (i * 24 * 60 * 60 * 1000)
            val dayEnd = dayStart + (24 * 60 * 60 * 1000)

            // Hitung item yang dibuat pada hari ini
            val createdCount = (catatanList.count { it.timestamp in dayStart until dayEnd } +
                    tugasList.count { it.timestamp in dayStart until dayEnd }).toFloat()

            // Hitung tugas yang diselesaikan pada hari ini
            val completedCount = tugasList.count {
                it.isSelesai && it.timestamp in dayStart until dayEnd
            }.toFloat()

            createdData.add(BarEntry(i.toFloat(), createdCount))
            completedData.add(BarEntry(i.toFloat(), completedCount))

            // Label hari
            calendar.timeInMillis = dayStart
            val dayName = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "Sen"
                Calendar.TUESDAY -> "Sel"
                Calendar.WEDNESDAY -> "Rab"
                Calendar.THURSDAY -> "Kam"
                Calendar.FRIDAY -> "Jum"
                Calendar.SATURDAY -> "Sab"
                Calendar.SUNDAY -> "Min"
                else -> ""
            }
            dayLabels.add(dayName)
        }

        // Setup dataset untuk "Membuat"
        val dataSetCreated = BarDataSet(createdData, "Membuat").apply {
            color = Color.parseColor("#9C27B0") // Purple
            valueTextColor = Color.BLACK
            valueTextSize = 10f
        }

        // Setup dataset untuk "Selesai"
        val dataSetCompleted = BarDataSet(completedData, "Selesai").apply {
            color = Color.parseColor("#FF5252") // Red
            valueTextColor = Color.BLACK
            valueTextSize = 10f
        }

        // Combine datasets
        val barData = BarData(dataSetCreated, dataSetCompleted)
        barData.barWidth = 0.35f // Lebar bar

        // Setup chart
        barChart.apply {
            data = barData
            description.isEnabled = false
            setFitBars(true)

            // X Axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(dayLabels)
                granularity = 1f
                setDrawGridLines(false)
                textColor = Color.parseColor("#2C3E50")
                textSize = 11f
            }

            // Left Axis
            axisLeft.apply {
                axisMinimum = 0f
                granularity = 1f
                textColor = Color.parseColor("#2C3E50")
                setDrawGridLines(true)
                gridColor = Color.parseColor("#E0E0E0")
            }

            // Right Axis
            axisRight.isEnabled = false

            // Legend
            legend.isEnabled = false // Kita pakai custom legend di XML

            // Group bars
            val groupSpace = 0.1f
            val barSpace = 0.05f
            barData.barWidth = 0.35f
            groupBars(0f, groupSpace, barSpace)

            // Animation
            animateY(1000)

            invalidate()
        }
    }

    private fun calculateProgress(selesai: Int, total: Int): Int {
        return if (total > 0) {
            ((selesai.toFloat() / total.toFloat()) * 100).toInt()
        } else {
            0
        }
    }

    private fun updateProgressCircle(progress: Int) {
        progressCircle.progress = progress
        tvProgress.text = "$progress%"
        tvProgressText.text = "Anda Telah Menyelesaikan \"$progress%\"\nProgres Mingguan"

        // Animasi fade in
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        tvProgress.startAnimation(fadeIn)
    }

    private fun generateMotivationQuotes(
        catatanList: List<Catatan>,
        tugasList: List<Tugas>,
        progress: Int
    ) {
        // Quote 1: Analisis hari paling produktif
        val dayProductivity = analyzeProductiveDay(catatanList, tugasList)
        tvQuote1.text = "\"$dayProductivity\""

        // Quote 2: Analisis trend
        val trendMessage = analyzeTrend(tugasList, progress)
        tvQuote2.text = "\"$trendMessage\""
    }

    private fun analyzeProductiveDay(catatanList: List<Catatan>, tugasList: List<Tugas>): String {
        val calendar = Calendar.getInstance()
        val dayCount = mutableMapOf<String, Int>()

        // Hitung aktivitas per hari
        val allItems = catatanList.map { it.timestamp } + tugasList.map { it.timestamp }

        allItems.forEach { timestamp ->
            calendar.timeInMillis = timestamp
            val dayName = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "Senin"
                Calendar.TUESDAY -> "Selasa"
                Calendar.WEDNESDAY -> "Rabu"
                Calendar.THURSDAY -> "Kamis"
                Calendar.FRIDAY -> "Jumat"
                Calendar.SATURDAY -> "Sabtu"
                Calendar.SUNDAY -> "Minggu"
                else -> "Unknown"
            }
            dayCount[dayName] = (dayCount[dayName] ?: 0) + 1
        }

        val mostProductiveDay = dayCount.maxByOrNull { it.value }?.key ?: "Senin"
        return "Kamu paling produktif pada hari $mostProductiveDay"
    }

    private fun analyzeTrend(tugasList: List<Tugas>, progress: Int): String {
        val completedTasks = tugasList.count { it.isSelesai }

        return when {
            progress >= 80 -> "Luar biasa! Tingkat penyelesaian minggu ini sangat tinggi"
            progress >= 60 -> "Tingkat penyelesaian tugas minggu ini meningkat"
            progress >= 40 -> "Terus semangat! Kamu hampir mencapai target"
            completedTasks > 0 -> "Setiap tugas yang selesai adalah pencapaian"
            else -> "Ayo mulai menyelesaikan tugasmu hari ini"
        }
    }

    private fun animateTextView(textView: TextView, targetValue: Int) {
        // Simple counter animation
        val animator = android.animation.ValueAnimator.ofInt(0, targetValue)
        animator.duration = 1000
        animator.addUpdateListener { animation ->
            textView.text = animation.animatedValue.toString()
        }
        animator.start()
    }
}