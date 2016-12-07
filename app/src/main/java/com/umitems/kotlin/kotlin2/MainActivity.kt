package com.umitems.kotlin.kotlin2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.SeekBar
import java.lang.Thread.sleep
import java.util.*

class MainActivity : AppCompatActivity() {
    var random: Random = Random()
    val MAX_ITEMS = 29
    val SORT_TEXT = "Sort"
    val SORTED_TEXT = "Sorted"
    val SORTING_TEXT = "Sorting"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var data = initRandomArray(MAX_ITEMS, MAX_ITEMS)
        val shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake)

        var mRecyclerView = setupRecyclerView(data, R.id.recyclerView)
        setupBtns(data, mRecyclerView, shakeAnim)

    }

    //todo add functionality to adjust quantity of random data
    //todo may need to hide seekBarQuantity when sorting. Its gonna be hard/fun to sorting and adjusting quantity of data
    //todo dataBinding with txtCount access,swap etc...

    private val seekBar:SeekBar
    get(){
        var seekBar = findViewById(R.id.seekBar)as SeekBar
        return seekBar
    }

    private val btnSort: Button
        get() {
            var btnSort = findViewById(R.id.btnSort) as Button
            return btnSort
        }

    private fun setupBtns(array: ArrayList<Int>, mRecyclerView: RecyclerView, shakeAnim: Animation?) {
        var randomData = array
        var btnRandom = findViewById(R.id.btnRandom) as Button

        btnRandom.setOnClickListener {
            randomData = initRandomArray(MAX_ITEMS, MAX_ITEMS)
            mRecyclerView.adapter = SortAdapter(randomData, this)
            btnSort.text = SORT_TEXT
        }
        btnSort.text = SORT_TEXT
        btnSort.startAnimation(shakeAnim)
        btnSort.setOnClickListener {
            it as Button
            it.text = SORTED_TEXT
            System.out.println("Before: " + randomData)
            bubbleSort(randomData, mRecyclerView)
        }
    }

    private fun setupRecyclerView(array: ArrayList<Int>, recyclerViewId: Int): RecyclerView {
        var recyclerView = findViewById(recyclerViewId) as RecyclerView
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        var sortAdapter: SortAdapter
        val arrayList: ArrayList<Int> = array.let { intList ->
            ArrayList<Int>(intList.size).apply { intList.forEach { add(it) } }
        }
        sortAdapter = SortAdapter(arrayList, this)
        recyclerView.adapter = sortAdapter

        val callback = RecyclerItemTouchHelper(sortAdapter)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(recyclerView)
        return recyclerView
    }

    private fun initRandomArray(max: Int, range: Int): ArrayList<Int> {
        var i = 0
        var array: ArrayList<Int>
        array = ArrayList()

        while (i < max) {
            array.add(random.nextInt() % (range / 2) + (range / 2 + 1))
            i++
        }
        Log.d("New dataset:", array.toString())
        random.setSeed(Math.random().toLong())//change random seed?
        return array
    }

    fun getDelay(): Long {
        var delay = Math.abs(1000-(seekBar.progress*9.9))

        return delay.toLong()
    }

    fun bubbleSort(mItems: ArrayList<Int>, mRecyclerView: RecyclerView): ArrayList<Int> {
        mRecyclerView.adapter = SortAdapter(mItems, this)
        var i = 0
        var k = 0

        btnSort.text=SORTING_TEXT
        val thread = Thread {
            while (i < mItems.size) {
                k = 0
                while (k < mItems.size - 1) {
                    Log.d("chkDelay", "k=" + k)
                    if (mItems[k] < mItems[k + 1]) {
                        val tmp = mItems[k]
                        mItems[k] = mItems[k + 1]
                        mItems[k + 1] = tmp
                        runOnUiThread {
                            //todo measure and add more delay for ui to render the screen
                            mRecyclerView.adapter.notifyItemChanged(k)
                            mRecyclerView.adapter.notifyItemChanged(k + 1)
                        }
                        sleep(getDelay())
                    }
                    k++
                }
                i++
                Log.d("chkDelay", "i=" + i)
            }
            /**
             * on sorted
             * need to update the ui the the latest state since if the delay is too fast. The device cannot render ui in time.
             */
            runOnUiThread {
                mRecyclerView.adapter.notifyDataSetChanged()
           btnSort.text=SORTED_TEXT
            }
        }
        thread.start()

        return mItems.let { intList ->
            ArrayList<Int>(intList.size).apply { intList.forEach { add(it) } }
        }
    }
}
