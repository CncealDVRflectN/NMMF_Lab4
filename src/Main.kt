import java.util.*

fun printArr(arr: Array<Double>) {
    arr.forEach { value -> System.out.printf(Locale.ENGLISH, "%.5f ", value) }
    println()
}

fun printMtr(mtr: Array<Array<Double>>) {
    mtr.forEach { line ->
        run {
            line.forEach { value -> System.out.printf(Locale.ENGLISH, "%.5f ", value) }
            println()
        }
    }
}

fun calcNorm(prev: Array<Array<Double>>, next: Array<Array<Double>>): Double {
    var max = Double.NEGATIVE_INFINITY
    var abs: Double

    for (j in 0 until prev.size) {
        for (i in 0 until prev[j].size) {
            abs = Math.abs(next[j][i] - prev[j][i])
            max = if (abs > max) abs else max
        }
    }

    return max
}

fun calcF(x: Double, y: Double): Double {
    return Math.cosh(x - y)
}

fun calcMu1(y: Double): Double {
    return Math.pow(Math.sin(Math.PI * y), 2.0)
}

fun calcMu2(y: Double): Double {
    return 0.0
}

fun calcMu3(x: Double): Double {
    return Math.cosh(x * x - 3.0 * x) - 1.0
}

fun calcMu4(x: Double): Double {
    return 0.0
}

fun calcDirichletProblem(horizontalNodes: Array<Double>, verticalNodes: Array<Double>, accuracy: Double): Array<Array<Double>> {
    var prev = Array(verticalNodes.size, { Array(horizontalNodes.size, { 0.0 }) })
    var next = Array(verticalNodes.size, { Array(horizontalNodes.size, { 0.0 }) })
    var tmp: Array<Array<Double>>
    var horizontalStepRight: Double
    var horizontalStepLeft: Double
    var horizontalStepAvg: Double
    var verticalStepUp: Double
    var verticalStepDown: Double
    var verticalStepAvg: Double

    for (i in 0 until horizontalNodes.size) {
        next[0][i] = calcMu3(horizontalNodes[i])
        prev[0][i] = next[0][i]
        next[verticalNodes.size - 1][i] = calcMu4(horizontalNodes[i])
        prev[verticalNodes.size - 1][i] = next[verticalNodes.size - 1][i]
    }
    for (j in 0 until verticalNodes.size) {
        next[j][0] = calcMu1(verticalNodes[j])
        prev[j][0] = next[j][0]
        next[j][horizontalNodes.size - 1] = calcMu2(verticalNodes[j])
        prev[j][horizontalNodes.size - 1] = next[j][horizontalNodes.size - 1]
    }

    do {
        tmp = prev
        prev = next
        next = tmp

        for (j in 1..verticalNodes.size - 2) {
            verticalStepDown = verticalNodes[j] - verticalNodes[j - 1]
            verticalStepUp = verticalNodes[j + 1] - verticalNodes[j]
            verticalStepAvg = (verticalStepDown + verticalStepUp) / 2.0

            for (i in 1..horizontalNodes.size - 2) {
                horizontalStepLeft = horizontalNodes[i] - horizontalNodes[i - 1]
                horizontalStepRight = horizontalNodes[i + 1] - horizontalNodes[i]
                horizontalStepAvg = (horizontalStepLeft + horizontalStepRight) / 2.0

                next[j][i] = ((prev[j][i - 1] / horizontalStepLeft + prev[j][i + 1] / horizontalStepRight) / horizontalStepAvg +
                        (prev[j - 1][i] / verticalStepDown + prev[j + 1][i] / verticalStepUp) / verticalStepAvg +
                        calcF(horizontalNodes[i], verticalNodes[j])) /
                        ((1.0 / horizontalStepLeft + 1.0 / horizontalStepRight) / horizontalStepAvg +
                                (1.0 / verticalStepDown + 1.0 / verticalStepUp) / verticalStepAvg)
            }
        }
    } while (calcNorm(prev, next) > accuracy)

    return next
}

fun main(args: Array<String>) {
    val leftBorder = 0.0
    val rightBorder = 3.0
    val bottomBorder = 0.0
    val topBorder = 1.0
    val horizontalStep = 0.05
    val verticalStep = 0.15
    val horizontalSplitNum = Math.ceil((rightBorder - leftBorder) / horizontalStep).toInt()
    val verticalSplitNum = Math.ceil((topBorder - bottomBorder) / verticalStep).toInt()
    val horizontalNodes = Array(horizontalSplitNum + 1, { i: Int -> Math.min(leftBorder + i * horizontalStep, rightBorder) })
    val verticalNodes = Array(verticalSplitNum + 1, { j: Int -> Math.min(bottomBorder + j * verticalStep, topBorder) })
    val accuracy = 0.00001

    println("Узлы по X")
    printArr(horizontalNodes)
    println("Узлы по Y")
    printArr(verticalNodes)
    println("Результат")
    printMtr(calcDirichletProblem(horizontalNodes, verticalNodes, accuracy))
}