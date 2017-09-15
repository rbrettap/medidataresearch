package net.pot8os.kotlintestsample

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding2.view.RxView
import net.pot8os.kotlintestsample.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * @author So Nakamura, 2015/12/19
 */
class MainActivity : AppCompatActivity() {

    enum class Figure {
        ADD, SUB, MULTI, DIV, NONE;

        fun calc(arg1: BigDecimal, arg2: BigDecimal): BigDecimal {
            when (this) {
                Figure.ADD -> return arg1.plus(arg2)
                Figure.SUB -> return arg1.minus(arg2)
                Figure.MULTI -> return arg1.multiply(arg2)
                Figure.DIV -> return arg1.divide(arg2, 8, BigDecimal.ROUND_HALF_UP)
                Figure.NONE -> return arg2
            }
        }
    }

    private val formatter = DecimalFormat("#,###.#").apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 8
    }

    private var field = BigDecimal.ZERO
    private var stack = BigDecimal.ZERO
    private var currentFigure = Figure.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        arrayOf(binding.button0,
                binding.button1, binding.button2, binding.button3,
                binding.button4, binding.button5, binding.button6,
                binding.button7, binding.button8, binding.button9).forEach { button ->
            RxView.clicks(button)
                    .subscribe {
                        field = field.multiply(BigDecimal(10)).plus(BigDecimal(button.tag.toString().toInt()))
                        binding.field.setText(formatter.format(field))
                    }
        }

        arrayOf(binding.buttonAllClear,
                binding.buttonAdd, binding.buttonSub,
                binding.buttonMulti, binding.buttonDivide).forEach { button ->
            RxView.clicks(button)
                    .subscribe {
                        currentFigure = Figure.valueOf(button.tag.toString())
                        stack = if (currentFigure != Figure.NONE) field else BigDecimal.ZERO
                        field = BigDecimal.ZERO
                        if (stack == BigDecimal.ZERO) {
                            binding.field.setText(formatter.format(field))
                        }
                    }
        }

        RxView.clicks(binding.buttonCalc)
                .subscribe {
                    field = currentFigure.calc(stack, field)
                    binding.field.setText(formatter.format(field))
                }
    }
}