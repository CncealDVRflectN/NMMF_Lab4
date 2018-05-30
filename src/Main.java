import java.util.Locale;

public class Main {
    private static void printVector(double[] vect) {
        for (double element : vect) {
            System.out.println(element);
        }
    }

    private static void printMtr(double[][] mtr) {
        for (double[] line : mtr) {
            for (double value : line) {
                System.out.printf(Locale.ENGLISH, "%.5f ", value);
            }
            System.out.println();
        }
    }

    private static double calcNorm(double[][] prev, double[][] next) {
        double max = Double.NEGATIVE_INFINITY;
        double abs;

        for (int j = 0; j < prev.length; j++) {
            for (int i = 0; i < prev[j].length; i++) {
                abs = Math.abs(next[j][i] - prev[j][i]);
                max = (abs > max) ? abs : max;
            }
        }

        return max;
    }

    private static double calcF(double x, double y) {
        return Math.cosh(x - y);
    }

    private static double calcMu1(double y) {
        return Math.pow(Math.sin(Math.PI * y), 2.0);
    }

    private static double calcMu2(double y) {
        return 0.0;
    }

    private static double calcMu3(double x) {
        return Math.cosh(x * x - 3.0 * x) - 1.0;
    }

    private static double calcMu4(double x) {
        return 0.0;
    }

    private static double[][] calcDirichletProblem(double[] horizontalNodes, double[] verticalNodes, double accuracy) {
        double[][] prev = new double[verticalNodes.length][horizontalNodes.length];
        double[][] next = new double[verticalNodes.length][horizontalNodes.length];
        double[][] swapBuf;
        double horizontalStepRight;
        double horizontalStepLeft;
        double horizontalStepAvg;
        double verticalStepUp;
        double verticalStepDown;
        double verticalStepAvg;

        for (int i = 0; i < horizontalNodes.length; i++) {
            next[0][i] = calcMu3(horizontalNodes[i]);
            prev[0][i] = next[0][i];
            next[verticalNodes.length - 1][i] = calcMu4(horizontalNodes[i]);
            prev[verticalNodes.length - 1][i] = next[verticalNodes.length - 1][i];
        }

        for (int j = 0; j < verticalNodes.length; j++) {
            next[j][0] = calcMu1(verticalNodes[j]);
            prev[j][0] = next[j][0];
            next[j][horizontalNodes.length - 1] = calcMu2(verticalNodes[j]);
            prev[j][horizontalNodes.length - 1] = next[j][horizontalNodes.length - 1];
        }

        do {
            swapBuf = prev;
            prev = next;
            next = swapBuf;

            for (int j = 1; j < verticalNodes.length - 1; j++) {
                verticalStepUp = verticalNodes[j + 1] - verticalNodes[j];
                verticalStepDown = verticalNodes[j] - verticalNodes[j - 1];
                verticalStepAvg = (verticalStepDown + verticalStepUp) / 2.0;

                for (int i = 1; i < horizontalNodes.length - 1; i++) {
                    horizontalStepRight = horizontalNodes[i + 1] - horizontalNodes[i];
                    horizontalStepLeft = horizontalNodes[i] - horizontalNodes[i - 1];
                    horizontalStepAvg = (horizontalStepLeft + horizontalStepRight) / 2.0;

                    next[j][i] = ((prev[j][i - 1] / horizontalStepLeft + prev[j][i + 1] / horizontalStepRight) / horizontalStepAvg +
                            (prev[j - 1][i] / verticalStepDown + prev[j + 1][i] / verticalStepUp) / verticalStepAvg +
                            calcF(horizontalNodes[i], verticalNodes[j])) /
                            ((1.0 / horizontalStepLeft + 1.0 / horizontalStepRight) / horizontalStepAvg +
                                    (1.0 / verticalStepDown + 1.0 / verticalStepUp) / verticalStepAvg);
                }
            }
        } while (calcNorm(prev, next) >= accuracy);

        return next;
    }

    public static void main(String... args) {
        double leftBorder = 0.0;
        double rightBorder = 3.0;
        double bottomBorder = 0.0;
        double topBorder = 1.0;
        double horizontalStep = 0.05;
        double verticalStep = 0.15;
        int horizontalSplitsNum = (int)Math.ceil((rightBorder - leftBorder) / horizontalStep);
        int verticalSplitsNum = (int)Math.ceil((topBorder - bottomBorder) / verticalStep);
        double[] horizontalNodes = new double[horizontalSplitsNum + 1];
        double[] verticalNodes = new double[verticalSplitsNum + 1];
        double accuracy = 0.00001;

        for (int i = 0; i < horizontalNodes.length; i++) {
            horizontalNodes[i] = Math.min(leftBorder + i * horizontalStep, rightBorder);
        }

        for (int j = 0; j < verticalNodes.length; j++) {
            verticalNodes[j] = Math.min(bottomBorder + j * verticalStep, topBorder);
        }

        System.out.println("Узлы по X:");
        printVector(horizontalNodes);
        System.out.println("Узлы по Y:");
        printVector(verticalNodes);
        System.out.println("Результат:");
        printMtr(calcDirichletProblem(horizontalNodes, verticalNodes, accuracy));
    }
}
