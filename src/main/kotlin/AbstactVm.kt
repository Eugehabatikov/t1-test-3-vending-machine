
interface Coin<M> {
    val value: M
}

interface Product<M> {
    val price: M
}

interface MoneyCalculator<M> {
    fun zero(): M
    fun add(m1: M, m2: M): M
    fun isLessOrEqual(m1: M, m2: M): Boolean
}

abstract class AbstractVendingMachine<M, P : Product<M>, C : Coin<M>>(
    private val calculator: MoneyCalculator<M>,
    available: Map<P, Int>,
) {
    private sealed interface State<M, P : Product<M>> {
        val available: Map<P, Int>
    }

    private class Idle<M, P : Product<M>>(
        override val available: Map<P, Int>
    ) : State<M, P>

    private class Paying<M, P : Product<M>>(
        override val available: Map<P, Int>,
        val product: P,
        val sum: M,
    ) : State<M, P>

    private var state: State<M, P> = Idle(available)

    fun insertCoin(coin: C) {
        when (val st = state) {
            is Idle -> {
                onDisplayProductNotSelected()
                onEjectCoin(coin)
            }

            is Paying -> {
                val sum = calculator.add(
                    st.sum, coin.value
                )

                state = if (
                    calculator.isLessOrEqual(st.product.price, sum)
                ) {
                    onEjectProduct(st.product)
                    Idle(
                        st.available.mapValues {
                            if (it.key == st.product) it.value - 1 else it.value
                        }
                    )
                } else
                    Paying(st.available, st.product, sum)
            }
        }
    }

    fun selectProduct(product: P) {
        when (val st = state) {
            is Idle -> {
                val isAvail = (st.available[product] ?: 0) != 0
                if (isAvail)
                    state = Paying(st.available, product, calculator.zero())
                else
                    onDisplayProductIsOut(product)
            }

            is Paying -> {
                if (calculator.isLessOrEqual(st.sum, calculator.zero()))
                    state = Paying(st.available, product, calculator.zero())
            }
        }
    }

    abstract fun onEjectCoin(coin: C)
    abstract fun onEjectProduct(product: P)
    abstract fun onDisplayProductIsOut(product: P)
    abstract fun onDisplayProductNotSelected()
}