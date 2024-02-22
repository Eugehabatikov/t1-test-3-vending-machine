import java.math.BigDecimal

fun main() {
    val products = mapOf(
        RealProduct(BigDecimal(15.0), "Барни") to 1
    )

    val vm = ConsoleVendingMachine(
        object : Console {
            override fun writeLine(line: String) = println(line)
            override fun readLine() = readln()
        },
        products
    )

    while (true) vm.step()
}