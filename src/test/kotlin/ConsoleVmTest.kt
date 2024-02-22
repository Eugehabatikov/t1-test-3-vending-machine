import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.io.BufferedReader
import java.math.BigDecimal

class ConsoleVmTest: FunSpec({
    class TestConsole(vararg input: String): Console {
        val out: MutableList<String> = mutableListOf()
        val inp: MutableList<String> = input.toMutableList()

        override fun writeLine(line: String) {
            out.add(line)
        }

        override fun readLine(): String = inp.removeFirst()

        fun output(): List<String> {
            val result = out.toList()
            out.clear()
            return result
        }
    }

    val products = mapOf(
        RealProduct(BigDecimal(15.0), "Барни") to 1,
        RealProduct(BigDecimal(20.0), "Марс") to 1
    )

    fun stepMatch(vararg input:String): List<String> {
        val console = TestConsole(*input)
        val vm = ConsoleVendingMachine(console, products)
        for ( i in 0..<input.size){
            vm.step()
        }
        return console.output()
    }

    test("return money") {
        stepMatch(
            "insert 5"
        ) shouldBe listOf("продукт не выбран", "монета возвращена: Five")

    }

    test("wrong request") {
        stepMatch(
            "Барни"
       ) shouldBe listOf("неверная команда: Барни")
    }

    test("Positive test") {
        stepMatch(
            "select Барни",
            "insert 10",
            "insert 5",
       ) shouldBe listOf("ваш продукт: Барни")
    }

    test("wrong coin") {
        stepMatch(
            "insert 11",
        ) shouldBe listOf("неправильный номинал монеты: 11")
    }

    test("empty store") {
        stepMatch(
            "select Барни",
            "insert 10",
            "insert 5",
            "select Барни",
        ) shouldBe listOf("ваш продукт: Барни", "продукт закончился: Барни")
    }

    test("wrong product") {
        stepMatch(
            "select Барнич"
        ) shouldBe listOf("продукт не найден: Барнич")
    }



    test("change product") {
       stepMatch("select Барни",
           "select Марс",
           "insert 10",
           "insert 10") shouldBe listOf("ваш продукт: Марс")
    }

    test("with DSL") {
        // TODO: vm(nSteps, consoleIn).shouldOut(consoleOut)
        //       vm(nSteps = 1, "insert 5", "insert 10")
    //               .shouldOut("продукт не выбран", "продукт не выбран")

    }


})
