package net.osmand.gpx;

public abstract class GPXInterpolator {

	private final int pointsCount;
	private final double totalLength;
	private double step;

	private double[] calculatedX;
	private double[] calculatedY;
	private int calculatedPointsCount;

	public GPXInterpolator(int pointsCount, double totalLength, double step) {
		this.pointsCount = pointsCount;
		this.totalLength = totalLength;
		this.step = step;
	}

	public int getPointsCount() {
		return pointsCount;
	}

	public double getTotalLength() {
		return totalLength;
	}

	public double getStep() {
		return step;
	}

	public double[] getCalculatedX() {
		return calculatedX;
	}

	public double[] getCalculatedY() {
		return calculatedY;
	}

	public int getCalculatedPointsCount() {
		return calculatedPointsCount;
	}

	public abstract double getX(int index);

	public abstract double getY(int index);

	public void interpolate() {
		calculatedPointsCount = (int) (totalLength / step) + 1;
		calculatedX = new double[calculatedPointsCount];
		calculatedY = new double[calculatedPointsCount];
		int lastIndex = pointsCount - 1;
		int nextW = 0;
		for (int k = 0; k < calculatedX.length; k++) {
			if (k > 0) {
				calculatedX[k] = calculatedX[k - 1] + step;
			}
			while (nextW < lastIndex && calculatedX[k] > getX(nextW)) {
				nextW++;
			}
			double px = nextW == 0 ? 0 : getX(nextW - 1);
			double py = nextW == 0 ? getY(0) : getY(nextW - 1);
			calculatedY[k] = py + (getY(nextW) - py) / (getX(nextW) - px) * (calculatedX[k] - px);
		}
	}
}
